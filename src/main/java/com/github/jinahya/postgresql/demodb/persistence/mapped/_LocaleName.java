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

import java.util.Locale;
import java.util.Objects;

/**
 * A name in the language of a {@link Locale}, one entry of the names kept as a JSON object keyed
 * by language, {@code {"en": "...", "ru": "..."}}.
 *
 * @param locale the locale whose {@link Locale#getLanguage() language} names the name.
 * @param value  the name in the language of {@code locale}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _LocaleNames
 */
record _LocaleName(Locale locale, String value) {

    /**
     * Creates a new instance with the specified locale and value.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the name.
     * @param value  the name in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code value} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code value} is
     *                                  blank.
     */
    _LocaleName {
        _LocaleNames.language(locale);
        Objects.requireNonNull(value, "value is null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value is blank");
        }
    }
}

