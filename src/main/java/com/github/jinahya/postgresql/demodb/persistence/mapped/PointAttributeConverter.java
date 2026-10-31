package com.github.jinahya.postgresql.demodb.persistence.mapped;

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

import java.util.List;
import java.util.regex.Pattern;

/**
 * Converts between an attribute of a list of two {@link Double}s, {@code [x, y]}, and a
 * PostgreSQL {@code point} column, through its text, {@code (x,y)}.
 * <p>
 * A {@code point} holds two {@code float8} coordinates with no SRID. The columns of the schema,
 * {@value MappedAirportData#COLUMN_NAME_COORDINATES} of {@value MappedAirportData#TABLE_NAME} and
 * of the {@value MappedAirport#TABLE_NAME} view, are geolocations, the longitude as {@code x} and
 * the latitude as {@code y}; the datum is a convention, WGS 84 as GPS gives it.
 * <p>
 * No provider maps a native {@code point}: Hibernate Spatial needs PostGIS {@code geometry},
 * which the database has not, and Jakarta Persistence has no type of its own. Hibernate ORM reads
 * the column for this converter as its text, and binds the text this converter writes as a
 * {@link String}, which the server casts for the {@code stringtype=unspecified} on the JDBC URL.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class PointAttributeConverter
        implements AttributeConverter<List<Double>, String> {

    // (x,y), each a float8 as the server prints it, NaN and [-]Infinity included; the parentheses
    // and the spaces optional, as the server reads a point
    private static final Pattern PATTERN = Pattern.compile(
            "\\s*\\(?\\s*([^\\s,()]+)\\s*,\\s*([^\\s,()]+)\\s*\\)?\\s*");

    /**
     * Creates a new instance.
     */
    public PointAttributeConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc} -- {@code x} and {@code y}.
     * @return {@code attribute} as {@code (x,y)}, or {@code null} if {@code attribute} is
     *         {@code null}.
     * @throws IllegalArgumentException if {@code attribute} is not two coordinates.
     */
    @Override
    public String convertToDatabaseColumn(final List<Double> attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.size() != 2 || attribute.get(0) == null || attribute.get(1) == null) {
            throw new IllegalArgumentException("not two coordinates: " + attribute);
        }
        return "(" + attribute.get(0) + "," + attribute.get(1) + ")";
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the column's text, {@code (x,y)}.
     * @return an unmodifiable list of {@code x} and {@code y} {@code dbData} denotes, or
     *         {@code null} if {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is not a point.
     * @implNote {@link Double#parseDouble(String)} reads each number as the server prints it,
     *         {@code 1e+16}, {@code 1e-06}, {@code NaN}, {@code Infinity}; the driver sets
     *         {@code extra_float_digits} to {@code 3}, so a value reads back exactly.
     */
    @Override
    public List<Double> convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        final var matcher = PATTERN.matcher(dbData);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("not a point: " + dbData);
        }
        try {
            return List.of(Double.parseDouble(matcher.group(1)),
                           Double.parseDouble(matcher.group(2)));
        } catch (final NumberFormatException nfe) {
            throw new IllegalArgumentException("not a point: " + dbData, nfe);
        }
    }
}

