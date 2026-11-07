package com.github.jinahya.postgresql.demodb.persistence;

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

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * An abstract base class for testing, without a persistence context, an entity class.
 * <p>
 * Its checks are, for now, the ones of {@link __Object_Test}, which {@link _NonEntity_Test} runs
 * too; checks only an entity has to pass go here.
 *
 * @param <T> the type of the entity.
 * @param <U> the type of the identifier of the entity: its id class, the type of its
 *            {@code @EmbeddedId}, or the type of its {@code @Id}.
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
@Slf4j
public abstract class _Entity_Test<T, U>
        extends __Test<T>
        implements __Object_Test<T> {

    /**
     * Creates a new instance for the specified entity class.
     *
     * @param targetClass the entity class to test.
     * @param idClass     the class of the identifier of the entity: its id class, the type of its
     *                    {@code @EmbeddedId}, or the type of its {@code @Id}.
     */
    protected _Entity_Test(final Class<T> targetClass, final Class<U> idClass) {
        super(targetClass);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The class of the identifier of {@link #targetClass}: its id class, the type of its
     * {@code @EmbeddedId}, or the type of its {@code @Id}.
     */
    protected final Class<U> idClass;
}
