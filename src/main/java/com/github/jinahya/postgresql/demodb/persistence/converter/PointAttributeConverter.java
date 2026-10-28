package com.github.jinahya.postgresql.demodb.persistence.converter;

/*-
 * #%L
 * postgres-demonstration-persistence
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.regex.Pattern;

/**
 * Converts between a {@link Point} and a PostgreSQL {@code point} column, through its text,
 * {@code (x,y)}.
 * <p>
 * No provider maps a native {@code point}: Hibernate Spatial needs PostGIS {@code geometry}, which
 * the database has not, and Jakarta Persistence has no type of its own. Hibernate ORM reads the
 * column for this converter as its text, and binds the text this converter writes as a
 * {@link String}, which the server casts for the {@code stringtype=unspecified} on the JDBC URL.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class PointAttributeConverter
        implements AttributeConverter<PointAttributeConverter.Point, String> {

    /**
     * A geolocation, as a PostgreSQL {@code point} holds it: two {@code float8} coordinates, the
     * longitude as {@code x} and the latitude as {@code y}, with no SRID; the datum is a
     * convention, WGS 84 as GPS gives it.
     *
     * @param longitude the longitude, the point's {@code x}.
     * @param latitude  the latitude, the point's {@code y}.
     */
    public record Point(double longitude, double latitude) {
    }

    // (x,y), each a float8 as the server prints it, NaN and [-]Infinity included; the parentheses
    // and the spaces optional, as the server reads a point
    private static final Pattern PATTERN = Pattern.compile(
            "\\s*\\(?\\s*([^\\s,()]+)\\s*,\\s*([^\\s,()]+)\\s*\\)?\\s*");

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    public PointAttributeConverter() {
        super();
    }

    // -------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return {@code attribute} as {@code (longitude,latitude)}, or {@code null} if
     *         {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final Point attribute) {
        if (attribute == null) {
            return null;
        }
        return "(" + attribute.longitude() + "," + attribute.latitude() + ")";
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the column's text, {@code (x,y)}.
     * @return the point {@code dbData} denotes, or {@code null} if {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is not a point.
     * @implNote {@link Double#parseDouble(String)} reads each number as the server prints it,
     *         {@code 1e+16}, {@code 1e-06}, {@code NaN}, {@code Infinity}; the driver sets
     *         {@code extra_float_digits} to {@code 3}, so a value reads back exactly.
     */
    @Override
    public Point convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        final var matcher = PATTERN.matcher(dbData);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("not a point: " + dbData);
        }
        try {
            return new Point(Double.parseDouble(matcher.group(1)),
                             Double.parseDouble(matcher.group(2)));
        } catch (final NumberFormatException nfe) {
            throw new IllegalArgumentException("not a point: " + dbData, nfe);
        }
    }
}
