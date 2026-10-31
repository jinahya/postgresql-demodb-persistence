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

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A converter for an attribute of a set of {@link LocaleName}s, stored as a JSON object keyed by
 * language, {@code {"en": "...", "ru": "..."}}.
 * <p>
 * The column is read and written as a map of names keyed by locale with a
 * {@link LocaleStringMapAttributeConverter}, each entry of which is a {@link LocaleName}.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class LocaleNameSetAttributeConverter
        implements AttributeConverter<Set<LocaleName>, String> {

    private static final LocaleStringMapAttributeConverter LOCALE_STRING_MAP =
            new LocaleStringMapAttributeConverter();

    /**
     * Creates a new instance.
     */
    public LocaleNameSetAttributeConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc} -- names, one per language.
     * @return {@code attribute} as a JSON object keyed by language, or {@code null} if
     *         {@code attribute} is {@code null}.
     * @throws IllegalStateException if {@code attribute} has two names of the same language.
     */
    @Override
    public String convertToDatabaseColumn(final Set<LocaleName> attribute) {
        if (attribute == null) {
            return null;
        }
        return LOCALE_STRING_MAP.convertToDatabaseColumn(attribute.stream().collect(
                Collectors.toMap(LocaleName::getLocale, LocaleName::getValue)));
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- a JSON object keyed by language.
     * @return a set of the names {@code dbData} holds, or {@code null} if
     *         {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is not a JSON object, or a name of it is
     *                                  blank.
     * @throws ClassCastException       if a name of {@code dbData} is not a JSON string.
     */
    @Override
    public Set<LocaleName> convertToEntityAttribute(final String dbData) {
        final var map = LOCALE_STRING_MAP.convertToEntityAttribute(dbData);
        if (map == null) {
            return null;
        }
        return map.entrySet().stream()
                .map(e -> new LocaleName(e.getKey(), e.getValue()))
                .collect(Collectors.toSet());
    }
}

