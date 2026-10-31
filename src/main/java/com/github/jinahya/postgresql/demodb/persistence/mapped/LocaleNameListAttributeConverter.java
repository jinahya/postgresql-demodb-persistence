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

import com.github.jinahya.persistence.more.converter.json.JsonpAttributeConverter;
import jakarta.json.Json;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * A converter for an attribute of a list of {@link LocaleName}s, one per language, stored as a
 * JSON object keyed by language, {@code {"en": "...", "ru": "..."}}.
 * <p>
 * The column is read and written as a {@link jakarta.json.JsonObject} with
 * {@link JsonpAttributeConverter.OfString.OfJsonObject}, which reads one JSON object and nothing
 * after it. A list read is in the order of the languages, {@link #ORDER}, so that the same names
 * read twice are equal lists.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class LocaleNameListAttributeConverter
        implements AttributeConverter<List<LocaleName>, String> {

    /**
     * The order of names, by their languages.
     */
    static final Comparator<LocaleName> ORDER =
            Comparator.comparing(n -> n.getLocale().getLanguage());

    private static final JsonpAttributeConverter.OfString.OfJsonObject JSON_OBJECT =
            new JsonpAttributeConverter.OfString.OfJsonObject();

    /**
     * Creates a new instance.
     */
    public LocaleNameListAttributeConverter() {
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
    public String convertToDatabaseColumn(final List<LocaleName> attribute) {
        if (attribute == null) {
            return null;
        }
        final var languages = new HashSet<String>();
        final var builder = Json.createObjectBuilder();
        attribute.forEach(n -> {
            final var language = n.getLocale().getLanguage();
            if (!languages.add(language)) {
                throw new IllegalStateException("two names of the language: " + language);
            }
            builder.add(language, n.getValue());
        });
        return JSON_OBJECT.convertToDatabaseColumn(builder.build());
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- a JSON object keyed by language.
     * @return a list of the names {@code dbData} holds, in the order of their languages, or
     *         {@code null} if {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is not a JSON object, a key of it is not
     *                                  a language, or a name of it is blank.
     * @throws ClassCastException       if a name of {@code dbData} is not a JSON string.
     */
    @Override
    public List<LocaleName> convertToEntityAttribute(final String dbData) {
        final var object = JSON_OBJECT.convertToEntityAttribute(dbData);
        if (object == null) {
            return null;
        }
        final var list = new ArrayList<LocaleName>(object.size());
        object.keySet().forEach(k -> list.add(new LocaleName(new Locale(k), object.getString(k))));
        list.sort(ORDER);
        return list;
    }
}

