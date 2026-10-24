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

/**
 * An abstract converter between an attribute and a column PostgreSQL has a text form for, through
 * that text.
 * <p>
 * A subclass writes the attribute as text, {@link #toText}, and reads it back from text,
 * {@link #fromText}; this class does the rest. The database side, {@code Y}, is a type variable
 * here, and a concrete subclass binds it to {@link String}, so that a provider binds the text as a
 * string parameter (which the server casts to the column's type when the driver leaves the type to
 * it, {@code stringtype=unspecified} on the JDBC URL). The variable is what keeps the converter
 * portable: the two providers hand a converter different things on a read -- Hibernate the text,
 * EclipseLink whatever the driver returned, an object of its own for an {@code interval}, a Java
 * array for an array column -- and a method declared on a type variable takes an {@link Object}
 * once erased, where one declared on {@code String} would cast, and fail, before this class had a
 * say. The binding has to be left to the subclass, which does not override the methods, for the
 * same reason.
 *
 * @param <X> the type of the attribute.
 * @param <Y> the type of the database side; {@link String} in a concrete subclass.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
abstract class _TextConverter<X, Y>
        implements AttributeConverter<X, Y> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected _TextConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the text of {@code attribute}, a {@link String}, or {@code null} if {@code attribute}
     *         is {@code null}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Y convertToDatabaseColumn(final X attribute) {
        if (attribute == null) {
            return null;
        }
        return (Y) toText(attribute);
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the column's text, or an object whose {@code toString()} is
     *               that text.
     * @return the attribute {@code dbData} denotes, or {@code null} if {@code dbData} is
     *         {@code null}.
     * @throws IllegalArgumentException if {@code dbData} does not denote an attribute.
     */
    @Override
    public X convertToEntityAttribute(final Y dbData) {
        if (dbData == null) {
            return null;
        }
        return fromText(dbData.toString());
    }

    // ------------------------------------------------------------------------------------------------------------ text

    /**
     * Writes the specified attribute as the column's text.
     *
     * @param attribute the attribute; never {@code null}.
     * @return the text of {@code attribute}.
     */
    protected abstract String toText(X attribute);

    /**
     * Reads an attribute from the specified text of the column.
     *
     * @param text the text; never {@code null}.
     * @return the attribute {@code text} denotes.
     * @throws IllegalArgumentException if {@code text} does not denote an attribute.
     */
    protected abstract X fromText(String text);
}
