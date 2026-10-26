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

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * Converts between a {@link Point} and a PostgreSQL {@code point} column, through its text,
 * {@code (x,y)}, with no class of the driver's in sight.
 * <p>
 * From the database, a provider which hands the driver's own object over, as EclipseLink does, is
 * read through that object's public fields, {@code x} and {@code y}, looked up by reflection, so
 * that the driver is needed at run time only; its {@code toString()} is not read, for it writes a
 * coordinate as {@link Double#toString(double)} does, {@code 1.0E10}.
 * <p>
 * Why the database side is a type variable, and {@link OfString} the converter to name: the
 * providers need to see {@link String} there, and the methods need to take any object.
 * <ul>
 * <li>Declared as {@code String}, the methods take a {@code String}, and the bridge method the
 * compiler adds casts to it; EclipseLink, which hands over the driver's object whatever the
 * database side says, fails on that cast before the method has a say.</li>
 * <li>Declared as {@code Object}, reads work, but no provider can write: Hibernate has no JDBC
 * type to bind, and EclipseLink guesses one from the attribute.</li>
 * <li>Declared as {@code Y}, the methods take an {@code Object} once erased, so there is no cast;
 * and {@link OfString}, which binds {@code Y} to {@code String} without declaring a method, is
 * where the providers read {@code String} from, and bind the text as such.</li>
 * </ul>
 *
 * @param <Y> the type of the database side; {@link String} in {@link OfString}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class PointAttributeConverter<Y>
        implements AttributeConverter<PointAttributeConverter.Point, Y> {

    /**
     * A PostgreSQL {@code point}, as the server keeps it: two {@code float8} coordinates.
     *
     * @param x the {@code x} coordinate.
     * @param y the {@code y} coordinate.
     */
    public record Point(double x, double y) {
    }

    /**
     * Converts through the text, bound as a {@link String}; the converter an attribute names. It
     * only binds the database side to {@code String}, and declares no method, which would bring
     * the cast back; see the enclosing class.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfString
            extends PointAttributeConverter<String> {

        /**
         * Creates a new instance.
         */
        public OfString() {
            super();
        }
    }

    // (x,y), each a float8 as the server prints it, NaN and [-]Infinity included
    private static final Pattern PATTERN = Pattern.compile(
            "\\(\\s*(?<x>[^,()\\s]+)\\s*,\\s*(?<y>[^,()\\s]+)\\s*\\)");

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected PointAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return {@code attribute} as {@code (x,y)}, a {@link String}, or {@code null} if
     *         {@code attribute} is {@code null}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Y convertToDatabaseColumn(final Point attribute) {
        if (attribute == null) {
            return null;
        }
        return (Y) ("(" + attribute.x() + "," + attribute.y() + ")");
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the column's text, {@code (x,y)}, or the driver's own
     *               {@code org.postgresql.geometric.PGpoint}.
     * @return the point {@code dbData} denotes, or {@code null} if {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is text, and not a point.
     */
    @Override
    public Point convertToEntityAttribute(final Y dbData) {
        if (dbData == null) {
            return null;
        }
        if (DRIVER_POINT.equals(dbData.getClass().getName())) {
            final var fields = FIELDS.get(dbData.getClass());
            try {
                return new Point(fields[0].getDouble(dbData), fields[1].getDouble(dbData));
            } catch (final IllegalAccessException iae) {
                throw new IllegalArgumentException("failed to read " + dbData, iae);
            }
        }
        final var text = dbData.toString();
        final var matcher = PATTERN.matcher(text.strip());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("not a point: " + text);
        }
        try {
            return new Point(Double.parseDouble(matcher.group("x")),
                                     Double.parseDouble(matcher.group("y")));
        } catch (final NumberFormatException nfe) {
            throw new IllegalArgumentException("not a point: " + text, nfe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of the driver's own point class, which EclipseLink hands a converter.
     */
    private static final String DRIVER_POINT = "org.postgresql.geometric.PGpoint";

    /**
     * The public fields of the driver's point class, looked up once for each class: {@code x} and
     * {@code y}, as {@code double}s.
     */
    private static final ClassValue<Field[]> FIELDS = new ClassValue<>() {
        @Override
        protected Field[] computeValue(final Class<?> type) {
            try {
                return new Field[] {type.getField("x"), type.getField("y")};
            } catch (final NoSuchFieldException nsfe) {
                throw new IllegalStateException("no point fields on " + type, nsfe);
            }
        }
    };
}

