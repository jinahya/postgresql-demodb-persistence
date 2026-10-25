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

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts between a {@link List} of {@link Integer}s and a PostgreSQL {@code integer[]} column,
 * through the array's literal, {@code {1,2,NULL}}, on its own; an attribute names
 * {@link OfString}.
 * <p>
 * An integer never needs the literal's quotes, so the literal is no more than the elements, or
 * {@code NULL}, between braces and separated by commas. Hibernate passes the literal as text;
 * EclipseLink passes the driver's {@link java.sql.Array}, or the same already unpacked into a Java
 * array, which goes through the elements' {@code toString()}.
 * <p>
 * The database side is a type variable which {@link OfString} binds to {@link String}, and
 * {@link #convertToEntityAttribute(Object)} is declared on it, here: a method declared on
 * {@code String} would cast what EclipseLink passes, and fail, before the method had a say.
 *
 * @param <Y> the type of the database side; {@link String} in {@link OfString}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute#ATTRIBUTE_NAME_DAYS_OF_WEEK
 */
public abstract class IntegerListArrayAttributeConverter<Y>
        implements AttributeConverter<List<Integer>, Y> {

    /**
     * Converts through the literal, bound as a {@link String}.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfString
            extends IntegerListArrayAttributeConverter<String> {

        /**
         * Creates a new instance.
         */
        public OfString() {
            super();
        }
    }

    private static final String NULL_ELEMENT = "NULL";

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected IntegerListArrayAttributeConverter() {
        super();
    }

    // -------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the literal of {@code attribute}, a {@link String}, or {@code null} if
     *         {@code attribute} is {@code null}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Y convertToDatabaseColumn(final List<Integer> attribute) {
        if (attribute == null) {
            return null;
        }
        final var builder = new StringBuilder("{");
        for (final var element : attribute) {
            if (builder.length() > 1) {
                builder.append(',');
            }
            builder.append(element == null ? NULL_ELEMENT : element.toString());
        }
        return (Y) builder.append('}').toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the literal as text, a {@link java.sql.Array}, or a Java
     *               array.
     * @return a list of the elements of {@code dbData}, or {@code null} if {@code dbData} is
     *         {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is none of the above, or the text is not a
     *                                  one-dimensional literal of integers.
     */
    @Override
    public List<Integer> convertToEntityAttribute(final Y dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof java.sql.Array array) {
            try {
                return fromArray(array.getArray());
            } catch (final SQLException sqle) {
                throw new IllegalArgumentException("failed to unpack " + dbData, sqle);
            }
        }
        if (dbData.getClass().isArray()) {
            return fromArray(dbData);
        }
        return fromText(dbData.toString());
    }

    // ---------------------------------------------------------------------------------------------
    private static List<Integer> fromArray(final Object array) {
        final var length = Array.getLength(array);
        final var list = new ArrayList<Integer>(length);
        for (var i = 0; i < length; i++) {
            final var element = Array.get(array, i);
            list.add(element == null ? null : Integer.valueOf(element.toString()));
        }
        return list;
    }

    private static List<Integer> fromText(final String text) {
        final var trimmed = text.strip();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            throw new IllegalArgumentException("not an array literal: " + text);
        }
        final var body = trimmed.substring(1, trimmed.length() - 1).strip();
        final var list = new ArrayList<Integer>();
        if (body.isEmpty()) {
            return list;
        }
        for (final var element : body.split(",")) {
            final var stripped = element.strip();
            list.add(stripped.equalsIgnoreCase(NULL_ELEMENT) ? null : Integer.valueOf(stripped));
        }
        return list;
    }
}

