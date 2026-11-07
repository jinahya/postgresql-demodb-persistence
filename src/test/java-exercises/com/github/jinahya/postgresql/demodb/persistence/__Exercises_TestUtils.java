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

import java.time.Instant;
import java.util.function.Function;

/**
 * Helpers for the {@code *_PersistenceIT} classes of {@code src/test/java-exercises}, which answer
 * the exercises of {@code src/test/sql-exercises}.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __Exercises_TestUtils {

    /**
     * Applies the specified function to an entity manager of the installed {@code demo} database,
     * on the {@value ___PersistenceIT_Utils#PERSISTENCE_UNIT_NAME} unit, and returns the result.
     *
     * @param function the function to apply.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     */
    public static <R> R apply(final Function<? super EntityManager, ? extends R> function) {
        return ___PersistenceIT_Utils.applyEntityManager_(emf -> function);
    }

    /**
     * Returns the database's now, {@code bookings.now()}, through the specified entity manager.
     * Neither JPQL nor the Criteria API can call a function without a {@code FROM}, so it is read
     * with a native query, for the exercises that count from it to bind as a parameter. The demo
     * database fixes it, at {@code 2025-12-01 00:00:00+00} for the installed {@code 3m} edition,
     * so what is counted from it does not change with the wall clock.
     *
     * @param entityManager the entity manager.
     * @return the database's now.
     */
    public static Instant now(final EntityManager entityManager) {
        return (Instant) entityManager.createNativeQuery("SELECT bookings.now()", Instant.class)
                .getSingleResult();
    }

    // ---------------------------------------------------------------------------------------------
    private __Exercises_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
