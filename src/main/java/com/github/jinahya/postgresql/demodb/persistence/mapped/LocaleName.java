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
 * <p>
 * The locale is kept as its {@link Locale#getLanguage() language} alone, and two instances are
 * equal when both their languages and their values are, so that a set of instances with a name
 * renamed is not equal to the set before, and the change is seen as a change of the attribute. A
 * set of instances may hold two names of a language; one who changes it keeps it to one per
 * language, as the JSON object is.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LanguageNameUtils
 */
final class LocaleName {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified locale and value.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the name.
     * @param value  the name in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code value} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code value} is
     *                                  blank.
     */
    LocaleName(final Locale locale, final String value) {
        super();
        this.locale = new Locale(LanguageNameUtils.language(locale));
        this.value = LanguageNameUtils.name(value);
    }

    // ------------------------------------------------------------------------------------ Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "locale=" + locale +
               ",value=" + value +
               '}';
    }

    /**
     * Indicates whether the specified object is equal to this one; equal when the object is a
     * {@code LocaleName} of the same language and the same value.
     *
     * @param obj the object to compare.
     * @return {@code true} if {@code obj} is a {@code LocaleName} of the same language and the
     *         same value; {@code false} otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocaleName that)) {
            return false;
        }
        return locale.equals(that.locale) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locale, value);
    }

    // -------------------------------------------------------------------------------------- locale

    /**
     * Returns the locale of this name, its language alone.
     *
     * @return the locale of this name.
     */
    Locale getLocale() {
        return locale;
    }

    // --------------------------------------------------------------------------------------- value

    /**
     * Returns the value of this name.
     *
     * @return the value of this name.
     */
    String getValue() {
        return value;
    }

    // ---------------------------------------------------------------------------------------------
    private final Locale locale;

    private final String value;
}

