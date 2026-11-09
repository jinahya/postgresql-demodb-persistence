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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

/**
 * A name in a language, one entry of the names kept as a JSON object keyed by language,
 * {@code {"en": "...", "ru": "..."}}; the language is the key and the name its value.
 * <p>
 * Two instances are equal when both their languages and their names are, so that a set of instances
 * with a name renamed is not equal to the set before, and the change is seen as a change of the
 * attribute. A set of instances may hold two names of a language; one who changes it keeps it to
 * one per language, as the JSON object is.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LocalizedNameUtils
 */
final class LocalizedName {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified language and name.
     *
     * @param language the language of the name, the key in the JSON object.
     * @param name     the name in {@code language}.
     * @throws NullPointerException     if {@code language} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code language} does not match
     *                                  {@link LocalizedNameUtils#REGEXP_LANGUAGE}, or {@code name}
     *                                  is blank.
     */
    LocalizedName(final String language, final String name) {
        super();
        this.language = LocalizedNameUtils.requireValidLanguage(language);
        this.name = LocalizedNameUtils.name(name);
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "language=" + language +
               ",name=" + name +
               '}';
    }

    /**
     * Indicates whether the specified object is equal to this one; equal when the object is a
     * {@code LocalizedName} of the same language and the same name.
     *
     * @param obj the object to compare.
     * @return {@code true} if {@code obj} is a {@code LocalizedName} of the same language and the
     *         same name; {@code false} otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalizedName that)) {
            return false;
        }
        return language.equals(that.language) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, name);
    }

    // ------------------------------------------------------------------------------------ language

    /**
     * Returns the language of this name.
     *
     * @return the language of this name.
     */
    String getLanguage() {
        return language;
    }

    // ---------------------------------------------------------------------------------------- name

    /**
     * Returns the name.
     *
     * @return the name.
     */
    String getName() {
        return name;
    }

    /**
     * Replaces the name with the specified value.
     *
     * @param name new value for the name.
     * @throws NullPointerException     if {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code name} is blank.
     * @implNote The name takes part in {@link #equals(Object)} and {@link #hashCode()}; an
     *         instance renamed while in a hash-based set or map is lost to it.
     */
    void setName(final String name) {
        this.name = LocalizedNameUtils.name(name);
    }

    // ---------------------------------------------------------------------------------------------
    @jakarta.validation.constraints.Pattern(regexp = LocalizedNameUtils.REGEXP_LANGUAGE)
    @NotNull
    private final String language;

    @NotBlank
    private String name;
}

