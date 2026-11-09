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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The names kept as a JSON object keyed by language, {@code {"en": "...", "ru": "..."}}, as a list
 * of {@link LocaleName}s, one per language.
 * <p>
 * An attribute of this type is mapped with
 * {@link org.hibernate.annotations.JdbcTypeCode @JdbcTypeCode}{@code (}{@link
 * org.hibernate.type.SqlTypes#JSON SqlTypes.JSON}{@code )}, which is Hibernate ORM's, and no
 * converter; Hibernate ORM's JSON format mapper, Jackson's, writes the names through
 * {@link #getNames()} and reads them back, a name at a time, through
 * {@link #setName(String, String)}.
 * <p>
 * Hibernate ORM keeps a copy of a value, which it makes by writing and reading the JSON, and
 * compares the value to it by {@link #equals(Object)}; two instances are equal when they hold the
 * same names, in whatever order.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LanguageNameUtils
 */
public class LocaleNameWrapper {

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "entries=" + entries +
               '}';
    }

    /**
     * Indicates whether the specified object is equal to this one; equal when the object is a
     * {@code LocaleNameWrapper} of the same names, in whatever order.
     *
     * @param obj the object to compare.
     * @return {@code true} if {@code obj} is a {@code LocaleNameWrapper} of the same names;
     *         {@code false} otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocaleNameWrapper that)) {
            return false;
        }
        return new HashSet<>(entries).equals(new HashSet<>(that.entries));
    }

    @Override
    public int hashCode() {
        return new HashSet<>(entries).hashCode();
    }

    // ---------------------------------------------------------------------------------------- JSON

    /**
     * Returns the names of this instance, each keyed by its language; the method by which Jackson
     * writes the JSON object.
     *
     * @return the names of this instance, each keyed by its language.
     */
    @JsonAnyGetter
    Map<String, String> getNames() {
        final var names = new LinkedHashMap<String, String>(entries.size());
        entries.forEach(e -> names.put(e.getLocale().getLanguage(), e.getValue()));
        return names;
    }

    /**
     * Replaces, or adds, the name in the specified language; the method by which Jackson reads the
     * JSON object, a name at a time.
     *
     * @param language the language of the name.
     * @param name     the name in {@code language}.
     * @throws NullPointerException     if {@code language} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code language} is empty, or {@code name} is blank.
     */
    @JsonAnySetter
    void setName(final String language, final String name) {
        final var entry = new LocaleName(new Locale(language), name);
        entries.removeIf(e -> e.getLocale().equals(entry.getLocale())); // one per language
        entries.add(entry);
    }

    // ------------------------------------------------------------------------------------- entries

    /**
     * Returns current value of {@code entries} property.
     *
     * @return current value of {@code entries} property.
     */
    List<LocaleName> getEntries() {
        return entries;
    }

    /**
     * Replaces current value of {@code entries} property with the specified value.
     *
     * @param entries new value for {@code entries} property.
     */
    void setEntries(final List<LocaleName> entries) {
        this.entries = entries;
    }

    // ---------------------------------------------------------------------------------------------
    private List<LocaleName> entries = new ArrayList<>();
}

