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

import com.github.jinahya.persistence.more.converter.jakartajson.JsonpAttributeConverter;
import jakarta.json.Json;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A converter for an attribute of localized names, a name per language, keyed by language, stored
 * as a JSON object keyed by language, {@code {"en": "...", "ru": "..."}}; for the multilingual
 * name columns of the schema, such as {@code airports_data.city} and {@code country}, not for maps
 * in general.
 * <p>
 * The column is read and written as a {@link jakarta.json.JsonObject} with
 * {@link JsonpAttributeConverter.OfString.OfJsonObject}; each key is the language as it is.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class LocalizedNameMapAttributeConverter
        implements AttributeConverter<Map<String, String>, String> {

    private static final JsonpAttributeConverter.OfString.OfJsonObject JSON_OBJECT =
            new JsonpAttributeConverter.OfString.OfJsonObject();

    /**
     * Creates a new instance.
     */
    public LocalizedNameMapAttributeConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc} -- names keyed by language.
     * @return {@code attribute} as a JSON object keyed by language, or {@code null} if
     *         {@code attribute} is {@code null}.
     * @throws NullPointerException if a language or a name of {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final Map<String, String> attribute) {
        if (attribute == null) {
            return null;
        }
        final var builder = Json.createObjectBuilder();
        attribute.forEach(builder::add);
        return JSON_OBJECT.convertToDatabaseColumn(builder.build());
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- a JSON object keyed by language.
     * @return a map of the names {@code dbData} holds, keyed by language, or {@code null} if
     *         {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is not a JSON object.
     * @throws ClassCastException       if a name of {@code dbData} is not a JSON string.
     */
    @Override
    public Map<String, String> convertToEntityAttribute(final String dbData) {
        final var object = JSON_OBJECT.convertToEntityAttribute(dbData);
        if (object == null) {
            return null;
        }
        return object.keySet().stream()
                .collect(Collectors.toMap(k -> k, object::getString));
    }
}

