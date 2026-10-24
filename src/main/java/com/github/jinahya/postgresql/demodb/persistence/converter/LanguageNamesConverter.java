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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.persistence.Converter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts between a {@link Map} of names, keyed by language, and a PostgreSQL {@code jsonb}
 * column holding them as a JSON object, {@code {"en": "...", "ru": "..."}}, through Jakarta JSON
 * Binding. The database side is bound to {@link String} here, and the methods are inherited as
 * they are; see {@link _TextConverter} for why that is what keeps it portable.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class LanguageNamesConverter
        extends _TextConverter<Map<String, String>, String> {

    /**
     * The type JSON-B reads the column's text as; a {@code Map.class} alone would leave the value
     * type to the implementation.
     */
    private static final Type TYPE =
            new HashMap<String, String>() {
            }.getClass().getGenericSuperclass();

    /**
     * The JSON-B instance; it is thread-safe, and shared by every instance of this class.
     */
    private static final Jsonb JSONB = JsonbBuilder.create();

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    public LanguageNamesConverter() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------------ text

    @Override
    protected String toText(final Map<String, String> attribute) {
        try {
            return JSONB.toJson(attribute);
        } catch (final JsonbException jsonbe) {
            throw new IllegalArgumentException("failed to write " + attribute, jsonbe);
        }
    }

    @Override
    protected Map<String, String> fromText(final String text) {
        try {
            return JSONB.fromJson(text, TYPE);
        } catch (final JsonbException jsonbe) {
            throw new IllegalArgumentException("not a JSON object of names: " + text, jsonbe);
        }
    }
}

