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

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for names kept in a {@code jsonb} column as a JSON object keyed by language,
 * {@code {"en": "...", "ru": "..."}}, and mapped as a {@link JsonObject}.
 * <p>
 * A name is looked up by the {@link Locale#getLanguage() language} of a {@link Locale}. A
 * {@link JsonObject} cannot be changed, so the methods which change names return a new one, which
 * the attribute is replaced with, so that the change is seen as a change of the attribute.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class _LocaleNames {

    /**
     * Returns the language of the specified locale, the key of a name.
     *
     * @param locale the locale.
     * @return the {@link Locale#getLanguage() language} of {@code locale}.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    static String language(final Locale locale) {
        Objects.requireNonNull(locale, "locale is null");
        final var language = locale.getLanguage();
        if (language.isEmpty()) {
            throw new IllegalArgumentException("no language in " + locale);
        }
        return language;
    }

    /**
     * Returns the name, in the specified names, in the language of the specified locale.
     *
     * @param names  the names; may be {@code null}.
     * @param locale the locale whose language names the name.
     * @return the name in the language of {@code locale}; {@code null} if there is none, or it is
     *         not a JSON string.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    static String get(final JsonObject names, final Locale locale) {
        final var language = language(locale);
        return Optional.ofNullable(names).map(v -> v.getString(language, null)).orElse(null);
    }

    /**
     * Returns a new object of the specified names, with the name in the language of the specified
     * locale replaced with the specified value.
     *
     * @param names  the names; may be {@code null}.
     * @param locale the locale whose language names the name.
     * @param name   the new name in the language of {@code locale}.
     * @return a new object of {@code names} with {@code name} in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code name} is
     *                                  blank.
     */
    static JsonObject with(final JsonObject names, final Locale locale, final String name) {
        final var language = language(locale);
        Objects.requireNonNull(name, "name is null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return builder(names).add(language, name).build();
    }

    /**
     * Returns a new object of the specified names, without the name in the language of the
     * specified locale.
     *
     * @param names  the names; may be {@code null}.
     * @param locale the locale whose language names the name.
     * @return a new object of {@code names} without the name in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    static JsonObject without(final JsonObject names, final Locale locale) {
        final var language = language(locale);
        return builder(names).remove(language).build();
    }

    /**
     * Returns a new builder of the specified names, or of none.
     */
    private static JsonObjectBuilder builder(final JsonObject names) {
        return names == null ? Json.createObjectBuilder() : Json.createObjectBuilder(names);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private _LocaleNames() {
        throw new AssertionError("instantiation is not allowed");
    }
}

