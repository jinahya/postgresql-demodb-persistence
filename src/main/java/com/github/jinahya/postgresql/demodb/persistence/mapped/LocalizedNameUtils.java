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
import java.util.regex.Pattern;

/**
 * Utilities for localized names, a name per language, kept in a {@code jsonb} column as a JSON
 * object keyed by language, {@code {"en": "...", "ru": "..."}}.
 * <p>
 * A language is an ISO 639-1 code, {@link #REGEXP_LANGUAGE}, checked by
 * {@link #requireValidLanguage(String)}, and taken from a {@link Locale} by
 * {@link #language(Locale)}; a name is a text which is not blank, checked by
 * {@link #requireValidName(String)}.
 * <p>
 * For names mapped as a {@link JsonObject}, {@link #get(JsonObject, String)} reads a name, and
 * {@link #set(JsonObject, String, String)} and {@link #remove(JsonObject, String)} change one. A
 * {@link JsonObject} cannot be changed, so these return a new one, which the attribute is replaced
 * with, so that the change is seen as a change of the attribute.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class LocalizedNameUtils {

    /**
     * The regular expression which a language matches: an ISO 639-1 code, two letters in lower
     * case, as {@link Locale#getLanguage()} returns it for a language which has one. The value is
     * {@value}.
     */
    static final String REGEXP_LANGUAGE = "[a-z]{2}";

    /**
     * The compiled pattern of {@link #REGEXP_LANGUAGE}.
     */
    static final Pattern PATTERN_LANGUAGE = Pattern.compile(REGEXP_LANGUAGE);

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the specified language, if it matches {@link #REGEXP_LANGUAGE}.
     *
     * @param language the language.
     * @return {@code language}.
     * @throws NullPointerException     if {@code language} is {@code null}.
     * @throws IllegalArgumentException if {@code language} does not match
     *                                  {@link #REGEXP_LANGUAGE}.
     */
    static String requireValidLanguage(final String language) {
        Objects.requireNonNull(language, "language is null");
        if (!PATTERN_LANGUAGE.matcher(language).matches()) {
            throw new IllegalArgumentException("invalid language " + language);
        }
        return language;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the language of the specified locale, the key of a name, if it matches
     * {@link #REGEXP_LANGUAGE}.
     *
     * @param locale the locale.
     * @return the {@link Locale#getLanguage() language} of {@code locale}.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if the language of {@code locale} does not match
     *                                  {@link #REGEXP_LANGUAGE}.
     * @see #requireValidLanguage(String)
     */
    static String language(final Locale locale) {
        Objects.requireNonNull(locale, "locale is null");
        return requireValidLanguage(locale.getLanguage());
    }

    /**
     * Returns the specified name, if it is not blank.
     *
     * @param name the name.
     * @return {@code name}.
     * @throws NullPointerException     if {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code name} is blank.
     */
    static String requireValidName(final String name) {
        Objects.requireNonNull(name, "name is null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return name;
    }

    /**
     * Returns the name, in the specified names, in the specified language.
     *
     * @param names    the names; may be {@code null}, for none.
     * @param language the language of the name.
     * @return the name in {@code language}; {@code null} if there is none, or it is not a JSON
     *         string.
     * @throws NullPointerException     if {@code language} is {@code null}.
     * @throws IllegalArgumentException if {@code language} does not match
     *                                  {@link #REGEXP_LANGUAGE}.
     */
    static String get(final JsonObject names, final String language) {
        requireValidLanguage(language);
        return Optional.ofNullable(names).map(v -> v.getString(language, null)).orElse(null);
    }

    /**
     * Returns a new object of the specified names, with the name in the specified language
     * replaced with, or added as, the specified value; {@code names} itself is not changed.
     *
     * @param names    the names; may be {@code null}, for none.
     * @param language the language of the name.
     * @param name     the new name in {@code language}.
     * @return a new object of {@code names}, with {@code name} in {@code language}.
     * @throws NullPointerException     if {@code language} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code language} does not match
     *                                  {@link #REGEXP_LANGUAGE}, or {@code name} is blank.
     */
    static JsonObject set(final JsonObject names, final String language, final String name) {
        requireValidLanguage(language);
        return builder(names).add(language, requireValidName(name)).build();
    }

    /**
     * Returns a new object of the specified names, without the name in the specified language;
     * {@code names} itself is not changed.
     *
     * @param names    the names; may be {@code null}, for none.
     * @param language the language of the name.
     * @return a new object of {@code names}, without the name in {@code language}.
     * @throws NullPointerException     if {@code language} is {@code null}.
     * @throws IllegalArgumentException if {@code language} does not match
     *                                  {@link #REGEXP_LANGUAGE}.
     */
    static JsonObject remove(final JsonObject names, final String language) {
        requireValidLanguage(language);
        return builder(names).remove(language).build();
    }

    /**
     * Returns a new builder of the specified names, or of none.
     *
     * @param names the names; may be {@code null}, for none.
     * @return a new builder of {@code names}.
     */
    private static JsonObjectBuilder builder(final JsonObject names) {
        return names == null ? Json.createObjectBuilder() : Json.createObjectBuilder(names);
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    private LocalizedNameUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}

