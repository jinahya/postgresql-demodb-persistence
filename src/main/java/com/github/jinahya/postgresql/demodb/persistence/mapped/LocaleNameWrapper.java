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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import org.hibernate.annotations.Immutable;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * The names kept as a JSON object keyed by language, {@code {"en": "...", "ru": "..."}}, one per
 * language, for an attribute which Hibernate ORM maps as JSON with Jackson.
 * <p>
 * An attribute of this type is mapped with
 * {@link org.hibernate.annotations.JdbcTypeCode @JdbcTypeCode}{@code (}{@link
 * org.hibernate.type.SqlTypes#JSON SqlTypes.JSON}{@code )}, which is Hibernate ORM's, not Jakarta
 * Persistence's, and needs no converter: Hibernate ORM's JSON format mapper, which is Jackson's
 * when Jackson is on the class path, writes an instance as its {@link #toJson() JSON object} and
 * reads one back through {@link #fromJson(Map)}.
 * <p>
 * The names are kept as a list of {@link LocaleName}s, one per language, in the order of their
 * languages. An instance cannot be changed; the methods which change names return a new instance,
 * which the attribute is replaced with, so that the change is seen as a change of the attribute.
 * Two instances are equal when they hold the same names in the same languages.
 * <p>
 * The class is {@link Immutable @Immutable}, which is Hibernate ORM's, so that Hibernate ORM keeps
 * no copy of a value for its dirty checking, a copy it would otherwise make by writing and reading
 * the JSON; and {@link Serializable}, so that a value can be cached, and an entity of it
 * serialized. A {@link LocaleName} is not serializable, so an instance is serialized as its JSON
 * object, through a proxy.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LanguageNameUtils
 */
@Immutable
public final class LocaleNameWrapper implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Comparator<LocaleName> ORDER =
            Comparator.comparing(n -> n.getLocale().getLanguage());

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Returns a new instance of the specified names.
     *
     * @param names the names, each keyed by the locale whose {@link Locale#getLanguage() language}
     *              names it.
     * @return a new instance of {@code names}.
     * @throws NullPointerException     if {@code names}, any of its locales, or any of its names is
     *                                  {@code null}.
     * @throws IllegalArgumentException if any of the locales has no language, if any of the names
     *                                  is blank, or if two locales are of the same language.
     */
    public static LocaleNameWrapper of(final Map<Locale, String> names) {
        Objects.requireNonNull(names, "names is null");
        final var entries = new ArrayList<LocaleName>(names.size());
        final var languages = new HashSet<Locale>();
        names.forEach((k, v) -> {
            final var entry = new LocaleName(k, v);
            if (!languages.add(entry.getLocale())) {
                throw new IllegalArgumentException("two names of " + entry.getLocale());
            }
            entries.add(entry);
        });
        return new LocaleNameWrapper(entries);
    }

    /**
     * Returns a new instance of the specified JSON object, a name per language; the method by
     * which Jackson reads an instance.
     *
     * @param json the JSON object, each name keyed by its language.
     * @return a new instance of {@code json}.
     * @throws NullPointerException     if {@code json}, any of its languages, or any of its names
     *                                  is {@code null}.
     * @throws IllegalArgumentException if any of the languages is empty, or any of the names is
     *                                  blank.
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    static LocaleNameWrapper fromJson(final Map<String, String> json) {
        Objects.requireNonNull(json, "json is null");
        final var names = new LinkedHashMap<Locale, String>(json.size());
        json.forEach((k, v) -> {
            names.put(new Locale(Objects.requireNonNull(k, "null language")), v);
        });
        return of(names);
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    private LocaleNameWrapper(final List<LocaleName> entries) {
        super();
        this.entries = entries.stream().sorted(ORDER).toList();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "entries=" + entries +
               '}';
    }

    /**
     * Indicates whether the specified object is equal to this one; equal when the object is a
     * {@code LocaleNameWrapper} of the same names in the same languages.
     *
     * @param obj the object to compare.
     * @return {@code true} if {@code obj} is a {@code LocaleNameWrapper} of the same names in the
     *         same languages; {@code false} otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocaleNameWrapper that)) {
            return false;
        }
        return entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        return entries.hashCode();
    }

    // ------------------------------------------------------------------------------- Serializable

    /**
     * Returns the proxy which is serialized in place of this instance.
     *
     * @return the proxy of this instance.
     */
    @Serial
    private Object writeReplace() {
        return new SerializationProxy(toJson());
    }

    @Serial
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("a proxy is required");
    }

    /**
     * The proxy of an instance, its JSON object, which is serialized in place of it.
     *
     * @param json the JSON object of the instance.
     */
    private record SerializationProxy(LinkedHashMap<String, String> json) implements Serializable {

        @Serial
        private Object readResolve() {
            return fromJson(json);
        }
    }

    // ---------------------------------------------------------------------------------------- JSON

    /**
     * Returns the JSON object of this instance, each name keyed by its language, in the order of
     * the languages; the method by which Jackson writes an instance.
     *
     * @return the JSON object of this instance.
     */
    @JsonValue
    LinkedHashMap<String, String> toJson() {
        final var json = new LinkedHashMap<String, String>(entries.size());
        entries.forEach(e -> json.put(e.getLocale().getLanguage(), e.getValue()));
        return json;
    }

    // ------------------------------------------------------------------------------------- entries

    /**
     * Returns the names of this instance, each keyed by the locale of its language alone, in the
     * order of the languages.
     *
     * @return an unmodifiable map of the names of this instance.
     */
    public Map<Locale, String> getEntries() {
        final var map = new LinkedHashMap<Locale, String>(entries.size());
        entries.forEach(e -> map.put(e.getLocale(), e.getValue()));
        return Collections.unmodifiableMap(map);
    }

    /**
     * Returns the name in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the name.
     * @return the name in the language of {@code locale}; {@code null} if there is none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    public String getName(final Locale locale) {
        final var language = new Locale(LanguageNameUtils.language(locale));
        return entries.stream()
                .filter(e -> e.getLocale().equals(language))
                .map(LocaleName::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a new instance of the names of this instance, with the name in the language of the
     * specified locale replaced with, or added as, the specified value.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the name.
     * @param name   the new name in the language of {@code locale}.
     * @return a new instance with {@code name} in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code name} is
     *                                  blank.
     */
    public LocaleNameWrapper withName(final Locale locale, final String name) {
        final var entry = new LocaleName(locale, name);
        final var entries = new ArrayList<>(this.entries);
        entries.removeIf(e -> e.getLocale().equals(entry.getLocale())); // one per language
        entries.add(entry);
        return new LocaleNameWrapper(entries);
    }

    /**
     * Returns a new instance of the names of this instance, without the name in the language of
     * the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the name.
     * @return a new instance without a name in the language of {@code locale}; this instance if
     *         there was none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    public LocaleNameWrapper withoutName(final Locale locale) {
        final var language = new Locale(LanguageNameUtils.language(locale));
        final var entries = new ArrayList<>(this.entries);
        if (!entries.removeIf(e -> e.getLocale().equals(language))) {
            return this;
        }
        return new LocaleNameWrapper(entries);
    }

    // ---------------------------------------------------------------------------------------------
    @JsonUnwrapped
    private final List<LocaleName> entries;
}

