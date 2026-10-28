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

import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import com.github.jinahya.persistence.test.util.JinahyaPersistenceTestUtils;
import com.github.jinahya.persistence.util.JinahyaEntityManagerUtils;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.function.BiFunction;

import static com.github.jinahya.persistence.test.util.EntityPersisterUtils.newPersistedInstanceOf;
import static com.github.jinahya.persistence.test.util.JinahyaEntityManagerTestUtils.selectRandomEntity;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerUtils.applyInTransaction;
import static com.github.jinahya.persistence.util.JinahyaPersistenceUtils.applyEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * An abstract base class for integration tests which need the physical database, against a module's
 * integration-test persistence unit.
 * <p>
 * The unit is the one named {@value #PERSISTENCE_UNIT_NAME}, bootstrapped the Java SE way; each
 * {@code apply...} method opens a factory of it, on the PostgreSQL database the demonstration dump
 * was loaded into, connecting as {@code postgres}, and closes it once done.
 * <p>
 * These run under the {@code failsafe} profile only, and require the container from
 * {@code compose.yaml} to be up. Generating a schema is disabled for this unit: the tables are the
 * ones the dump itself created, and the mappings are verified against them rather than the other
 * way round.
 *
 * @param <T> the type of the entity under test.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
abstract class _Entity_Persistence_IT<T> {

    /**
     * The name of the persistence unit this class bootstraps. The value is {@value}.
     */
    static final String PERSISTENCE_UNIT_NAME = "it";

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified persistence class.
     *
     * @param targetClass the class of the entity under test.
     */
    _Entity_Persistence_IT(final Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code find} reaches a randomly selected row once the persistence context no
     * longer holds it.
     *
     * @implNote Compared by identifier, not with {@link Object#equals(Object)}: an entity
     *         here is free to base its equality on an association, and the reloaded instance holds
     *         proxies for those, which need not compare equal to the ones first selected. An empty
     *         table is not a failure; the test aborts, since the table's content is the
     *         installer's.
     *         <p>
     *         The identifier is read with
     *         {@link jakarta.persistence.PersistenceUnitUtil#getIdentifier(Object)}; see
     *         {@link JinahyaEntityManagerUtils#getIdentifier(EntityManager, Object)}.
     * @implSpec The identifier has to be a single attribute, basic or an
     *         {@link jakarta.persistence.EmbeddedId EmbeddedId}; a subclass whose entity is mapped
     *         with an {@link jakarta.persistence.IdClass IdClass} overrides this method, without
     *         {@link Test @Test}.
     */
    @DisplayName("find reaches a randomly selected row once the context is cleared")
    @Test
    void find_RandomlySelected_ContextCleared() {
        applyEntityManager(PERSISTENCE_UNIT_NAME, m -> applyInTransaction(m, em -> {
            final var selected = JinahyaPersistenceTestUtils.selectRandom(em, targetClass);
            assumeTrue(selected.isPresent(), () -> "no row of " + targetClass + " is installed");
            final var identifier = JinahyaEntityManagerUtils.getEntityIdentifier(em, selected.get());
            em.clear();
            final var found = em.find(targetClass, identifier);
            assertThat(found)
                    .as("the %s found by its identifier, from the database",
                        targetClass.getSimpleName())
                    .isNotNull()
                    .isNotSameAs(selected.get());
            assertThat(JinahyaEntityManagerUtils.<Object>getEntityIdentifier(em, found))
                    .as("the identifier of the %s found", targetClass.getSimpleName())
                    .isEqualTo(identifier);
            return null;
        }, true));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Persists a new, randomized instance of {@link #targetClass}, through its persister, and
     * flushes it.
     *
     * @implNote The flush is what makes the {@code INSERT} reach the database at all: a
     *         provider may defer it until the flush or the commit -- one whose identifier is
     *         assigned rather than generated, or generated by the {@code INSERT} itself -- and the
     *         transaction is rolled back, so without it nothing would be written, and nothing
     *         tested.
     */
    @Test
    void persistTargetInstance() {
        applyNewPersistedTargetInstanceAndRollback((em, v) -> {
            em.flush();
            targetInstancePersisted(em, v);
            return null;
        });
    }

    void targetInstancePersisted(final EntityManager entityManager, final T entityInstance) {
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Selects a random instance of {@link #targetClass}, from the installed schema, and hands it to
     * {@link #randomTargetInstanceSelected(Object)}.
     *
     * @implNote The selection and the callback both run inside a transaction which is
     *         rolled back, so the instance handed over is still managed -- a lazy association read
     *         in {@link #randomTargetInstanceSelected(Object)} resolves rather than throwing -- and
     *         nothing the callback does reaches the sample data.
     * @implSpec Selecting from an empty table is not a failure: the table's content is the
     *         installer's, not this test's, so {@link #randomTargetInstanceSelected(Object)} is
     *         simply not invoked.
     */
    @Test
    void selectRandomTargetInstance() {
        applyEntityManager(PERSISTENCE_UNIT_NAME, m -> applyInTransaction(m, em -> {
            selectRandomEntity(em, targetClass).ifPresent(this::randomTargetInstanceSelected);
            return null;
        }, true));
    }

    /**
     * Called with the instance {@link #selectRandomTargetInstance()} selected, while it is still
     * managed.
     *
     * @param instance the randomly selected instance.
     * @implSpec This class does nothing with it; a subclass overrides this method to assert
     *         whatever its entity promises of a row which is actually in the database.
     */
    void randomTargetInstanceSelected(final T instance) {
        // empty; overridden by a subclass which has something to assert
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Persists a new randomized instance of {@link #targetClass}, and applies it, along with the
     * entity manager, to the specified function, within a transaction which is always rolled back.
     *
     * @param function the function to apply the entity manager and the persisted instance to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when {@link #targetClass} has no usable randomizer or no
     *                                  usable persister; see
     *                                  {@link
     *                                  EntityPersisterUtils#newPersistedInstanceOf(EntityManager,
     *                                  Class)}.
     * @apiNote Unlike {@link #selectRandomTargetInstance()}, which reads a row the
     *         installer put there, this one writes a row of its own -- against the physical schema,
     *         with whatever constraints and triggers it carries, which is a good part of what an
     *         integration test is for. The rollback is what keeps it from being a change to the
     *         sample data.
     * @implNote The instance is persisted inside the same transaction the {@code function}
     *         runs in, so it is still managed when the function sees it, and it is gone when the
     *         transaction rolls back.
     */
    <R> R applyNewPersistedTargetInstanceAndRollback(
            final BiFunction<? super EntityManager, ? super T, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManager(PERSISTENCE_UNIT_NAME, m -> applyInTransaction(m, em -> {
            final var persisted = newPersistedInstanceOf(em, targetClass);
            return function.apply(em, persisted);
        }, true));
    }

    // --------------------------------------------------------------------------------- targetClass

    // ---------------------------------------------------------------------------------------------

    /**
     * The class which this test targets.
     */
    final Class<T> targetClass;
}
