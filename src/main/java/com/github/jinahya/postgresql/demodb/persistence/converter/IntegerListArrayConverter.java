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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.Converter;

/**
 * Converts between a {@link java.util.List} of {@link Integer}s and a PostgreSQL {@code integer[]}
 * column, each element through an {@link IntegerConverter}. The database side is bound to
 * {@link String} here, and the methods are inherited as they are; see {@link _TextConverter} for
 * why that is what keeps it portable.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 * <p>
 * It is also a CDI bean, {@link Dependent @Dependent}, with an {@link Inject @Inject} constructor
 * which takes the element converter: a provider handed a {@code BeanManager}
 * ({@code jakarta.persistence.bean.manager}) obtains its instances from CDI, which resolves the
 * {@link IntegerConverter} bean for it; one without uses the no-arg constructor, which builds the
 * element converter itself. Both end in the same place.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _ListArrayConverter
 * @see com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute#ATTRIBUTE_NAME_DAYS_OF_WEEK
 */
@Dependent
@Converter(autoApply = false)
public class IntegerListArrayConverter
        extends _ListArrayConverter<Integer, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, with an element converter of its own.
     */
    public IntegerListArrayConverter() {
        this(new IntegerConverter());
    }

    /**
     * Creates a new instance with the specified element converter; the constructor CDI uses.
     *
     * @param elementConverter the converter between one element and its text.
     */
    @Inject
    public IntegerListArrayConverter(final IntegerConverter elementConverter) {
        super(elementConverter);
    }
}
