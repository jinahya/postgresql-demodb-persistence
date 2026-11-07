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

/**
 * An abstract base class for testing a value class -- an id class, an embeddable -- which has no
 * identity of its own, and so no persistence test either.
 *
 * @param <T> the type of the value class.
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
@Slf4j
public abstract class _NonEntity_Test<T>
        extends __Test<T>
        implements __Object_Test<T> {

    /**
     * Creates a new instance for the specified target class.
     *
     * @param targetClass the value class to test.
     */
    protected _NonEntity_Test(final Class<T> targetClass) {
        super(targetClass);
    }
}

