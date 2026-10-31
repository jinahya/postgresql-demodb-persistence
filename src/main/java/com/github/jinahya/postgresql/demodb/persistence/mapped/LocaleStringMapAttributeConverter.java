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

import com.github.jinahya.persistence.more.converter.json.JsonpStringAttributeConverter;
import com.github.jinahya.persistence.more.converter.util.LocaleStringAttributeConverter;
import jakarta.json.Json;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A converter for an attribute of names keyed by {@link Locale}, stored as a JSON object keyed by
 * language, {@code {"en": "...", "ru": "..."}}.
 * <p>
 * The column is read and written as a {@link jakarta.json.JsonObject} with
 * {@link JsonpStringAttributeConverter.OfJsonObject}, and each key is a {@link Locale} stored as
 * its {@link Locale#getLanguage() language}, with
 * {@link LocaleStringAttributeConverter.OfLanguage}.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class LocaleStringMapAttributeConverter
        implements AttributeConverter<Map<Locale, String>, String> {

    private static final JsonpStringAttributeConverter.OfJsonObject JSON_OBJECT =
            new JsonpStringAttributeConverter.OfJsonObject();

    private static final LocaleStringAttributeConverter.OfLanguage LANGUAGE =
            new LocaleStringAttributeConverter.OfLanguage();

    /**
     * Creates a new instance.
     */
    public LocaleStringMapAttributeConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc} -- names keyed by locale.
     * @return {@code attribute} as a JSON object keyed by language, or {@code null} if
     *         {@code attribute} is {@code null}.
     * @throws NullPointerException  if a locale of {@code attribute} has no language.
     * @throws IllegalStateException if two locales of {@code attribute} have the same language.
     */
    @Override
    public String convertToDatabaseColumn(final Map<Locale, String> attribute) {
        if (attribute == null) {
            return null;
        }
        final var names = attribute.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Objects.requireNonNull(LANGUAGE.convertToDatabaseColumn(e.getKey()),
                                                    () -> "no language in " + e.getKey()),
                        Map.Entry::getValue
                ));
        return JSON_OBJECT.convertToDatabaseColumn(Json.createObjectBuilder(names).build());
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- a JSON object keyed by language.
     * @return a map of the names {@code dbData} holds, keyed by the locale of each
     *         language, or {@code null} if {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is not a JSON object.
     * @throws ClassCastException       if a name of {@code dbData} is not a JSON string.
     */
    @Override
    public Map<Locale, String> convertToEntityAttribute(final String dbData) {
        final var object = JSON_OBJECT.convertToEntityAttribute(dbData);
        if (object == null) {
            return null;
        }
        return object.keySet().stream()
                .collect(Collectors.toMap(
                        LANGUAGE::convertToEntityAttribute,
                        object::getString
                ));
    }
}

