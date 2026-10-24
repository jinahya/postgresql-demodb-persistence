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

/**
 * Converts between an {@link Integer} and its decimal text.
 * <p>
 * On its own it maps an integer kept in a text column; as the element converter of
 * {@link IntegerListArrayConverter} it maps each element of an {@code integer[]}.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class IntegerConverter
        implements AttributeConverter<Integer, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    public IntegerConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the decimal text of {@code attribute}, or {@code null} if {@code attribute} is
     *         {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final Integer attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the integer {@code dbData} denotes, or {@code null} if {@code dbData} is
     *         {@code null}.
     * @throws NumberFormatException if {@code dbData} is not a decimal integer.
     */
    @Override
    public Integer convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return Integer.valueOf(dbData.trim());
    }
}
