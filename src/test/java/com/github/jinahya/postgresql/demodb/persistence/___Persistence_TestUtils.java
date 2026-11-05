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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Objects;
import java.util.function.BiFunction;

import static com.github.jinahya.persistence.test.util.JinahyaEntityManagerTestUtils.selectRandomEntity;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerFactoryUtils.applyEntityManagerInTransactionAndRollback;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Helpers, over the jinahya library, for what it has no single call for.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___Persistence_TestUtils {

    /**
     * Selects a random instance of the specified entity class, in a new entity manager, inside a
     * transaction which is rolled back afterwards, and applies the entity manager and the instance
     * to the specified function.
     *
     * @param entityManagerFactory the factory to create the entity manager with.
     * @param entityClass          the entity class.
     * @param function             the function to apply the entity manager and the instance to.
     * @param <T>                  the type of the entity.
     * @param <R>                  the type of the result.
     * @return the result of the {@code function}; {@code null} when no row is selected, the
     *         {@code function} not applied.
     */
    public static <T, R> R selectRandomEntityInstance(
            final EntityManagerFactory entityManagerFactory,
            final Class<T> entityClass,
            final BiFunction<? super EntityManager, ? super T, ? extends R> function) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransactionAndRollback(
                entityManagerFactory,
                em -> selectRandomEntity(em, entityClass)
                        .map(v -> function.apply(em, v))
                        .orElse(null)
        );
    }

    /**
     * Skips the calling test when EclipseLink wove the specified class.
     *
     * @param type the class to check.
     * @see __Object_Test#isWovenByEclipseLink(Class)
     */
    public static void assumeNotWoven(final Class<?> type) {
        Objects.requireNonNull(type, "type is null");
        assumeFalse(__Object_Test.isWovenByEclipseLink(type), "woven by EclipseLink");
    }

    // ---------------------------------------------------------------------------------------------

    private ___Persistence_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
