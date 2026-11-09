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

import java.util.List;

/**
 * The names kept as a JSON object keyed by language, {@code {"en": "...", "ru": "..."}}, as a list
 * of {@link LocaleName}s, one per language.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see LanguageNameUtils
 */
public class LocaleNameWrapper {

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
    private List<LocaleName> entries;
}

