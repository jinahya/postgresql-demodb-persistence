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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Optional;

import static com.github.jinahya.postgresql.demodb.persistence.___PersistenceIT_Utils.applyEntityManagerFactory;
import static com.github.jinahya.postgresql.demodb.persistence.___PersistenceIT_Utils.applyEntityManager_;
import static com.github.jinahya.postgresql.demodb.persistence.___Persistence_TestUtils.selectRandomEntityInstance;
import static com.github.jinahya.persistence.test.util.EntityPersisterUtils.newPersistedInstanceOf;
import static com.github.jinahya.persistence.test.util.JinahyaEntityManagerTestUtils.selectRandomEntityId;
import static com.github.jinahya.persistence.test.util.JinahyaEntityManagerTestUtils.selectRandomEntityReference;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerFactoryUtils.applyEntityManager;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerUtils.applyInTransactionAndRollback;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * The integration tests of an entity, against the
 * {@value ___PersistenceIT_Utils#PERSISTENCE_UNIT_NAME} unit on the database {@code compose.yaml}
 * brings up. Each reads installed rows, or persists a new randomized instance, and rolls back.
 * <p>
 * The base holds no entity manager. Each test takes the factory from {@link ___PersistenceIT_Utils}
 * and goes through the jinahya library directly, one entity manager per call.
 *
 * @param <T> the type of the entity.
 * @param <U> the type of the identifier of the entity: its id class, the type of its
 *            {@code @EmbeddedId}, or the type of its {@code @Id}.
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
public abstract class _Entity_PersistenceIT<T, U> {

    /**
     * Creates a new instance for the specified entity class.
     *
     * @param entityClass the class of the entity under test.
     * @param idClass     the class of the identifier of the entity: its id class, the type of its
     *                    {@code @EmbeddedId}, or the type of its {@code @Id}.
     */
    protected _Entity_PersistenceIT(final Class<T> entityClass, final Class<U> idClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Selects a reference to a randomly chosen instance of {@link #entityClass}, with an entity
     * manager of its own of the {@value ___PersistenceIT_Utils#PERSISTENCE_UNIT_NAME} unit, through
     * {@code JinahyaEntityManagerTestUtils.selectRandomEntityReference}.
     *
     * @return an optional of the reference selected; {@code empty} when there is no instance.
     * @apiNote The entity manager is closed when this method returns, so the reference is
     *         detached; only its identifier is safe to read, for its state may never have been
     *         loaded.
     */
    Optional<T> selectRandomEntityReference_() {
        return applyEntityManager_(
                emf -> em -> selectRandomEntityReference(em, entityClass)
        );
    }

    /**
     * Selects the identifier of a randomly chosen instance of {@link #entityClass}, as an instance
     * of {@link #idClass}, with an entity manager of its own of the
     * {@value ___PersistenceIT_Utils#PERSISTENCE_UNIT_NAME} unit, through
     * {@code JinahyaEntityManagerTestUtils.selectRandomEntityId}.
     *
     * @return an optional of the identifier selected; {@code empty} when there is no instance.
     */
    Optional<U> selectRandomEntityId_() {
        return applyEntityManager_(
                emf -> em -> selectRandomEntityId(em, entityClass, idClass)
        );
    }

    // ---------------------------------------------------------------------------------------------
//
//    /**
//     * Verifies that each {@code ATTRIBUTE_PATH_*} constant of the entity, a dotted path such as
//     * {@code id.productId}, resolves through the metamodel.
//     */
//    @DisplayName("each ATTRIBUTE_PATH_* constant resolves through the metamodel")
//    @Test
//    void attributePaths() throws IllegalAccessException {
//        final var paths = new ArrayList<String>();
//        for (final Field field : entityClass.getFields()) {
//            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class
//                && field.getName().startsWith("ATTRIBUTE_PATH_")) {
//                paths.add((String) field.get(null));
//            }
//        }
//        applyEntityManagerFactory(emf -> applyEntityManager(emf, em -> {
//            for (final var attributePath : paths) {
//                Path<?> path = em.getCriteriaBuilder().createQuery().from(entityClass);
//                for (final var name : attributePath.split("\\.")) {
//                    path = path.get(name);
//                }
//                assertThat(path.getJavaType()).as(attributePath).isNotNull();
//            }
//            return null;
//        }));
//    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Persists a new, randomized instance of the entity, through its persister, flushes it, and
     * hands it to
     * {@link #randomizedEntityInstancePersisted__(EntityManagerFactory, EntityManager, Object)}.
     * <p>
     * The randomizer, {@code <Entity>_Randomizer}, and the persister, {@code <Entity>_Persister},
     * are found by name, beside the entity. An entity which is not persisted -- a view, or a table
     * which only a trigger of the database writes -- overrides this method with an empty one,
     * {@link org.junit.jupiter.api.Disabled @Disabled} with the reason.
     *
     * @implNote The flush is what makes the {@code INSERT} reach the database, with its
     *         constraints and triggers; the transaction is rolled back, so without it nothing would
     *         be written, and nothing tested.
     */
    @DisplayName("a new randomized instance is persisted")
    @Test
    protected void persistRandomizedEntityInstance__() {
        applyEntityManagerFactory(emf -> applyEntityManager(
                emf,
                m -> applyInTransactionAndRollback(m, em -> {
                    final var persisted = newPersistedInstanceOf(em, entityClass);
                    em.flush();
                    assertThat(emf.getPersistenceUnitUtil().getIdentifier(persisted))
                            .as("the identifier of the persisted %s", persisted)
                            .isNotNull();
                    randomizedEntityInstancePersisted__(emf, em, persisted);
                    return null;
                })
        ));
    }

    /**
     * Called with the instance {@link #persistRandomizedEntityInstance__()} persisted and flushed,
     * while it is still managed.
     *
     * @param entityManagerFactory the factory of the {@code entityManager}.
     * @param entityManager        the entity manager which persisted the instance.
     * @param entityInstance       the persisted instance.
     * @implSpec This class does nothing; a subclass overrides it to assert what its table
     *         does with a new row.
     */
    protected void randomizedEntityInstancePersisted__(
            final EntityManagerFactory entityManagerFactory,
            final EntityManager entityManager,
            final T entityInstance) {
        // empty
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Selects a random instance of the entity, from the installed rows, validates it against the
     * constraints of the mapping, and hands it to
     * {@link #randomEntityInstanceSelected__(EntityManagerFactory, EntityManager, Object)}.
     * <p>
     * The validation is here, since Jakarta Persistence validates an entity only when it persists,
     * updates, or removes it, never when it loads it.
     *
     * @implNote The selection and the callback both run inside a transaction which is
     *         rolled back, so the instance handed over is still managed, and nothing the callback
     *         does reaches the sample data.
     * @implSpec An empty table is not a failure; the test aborts, since the table's content
     *         is the installer's.
     */
    @DisplayName(
            "a randomly selected installed row is valid, and is handed to " +
            "randomEntityInstanceSelected__")
    @Test
    protected void selectRandomEntityInstance__() {
        final var selected = Optional.ofNullable(applyEntityManagerFactory(
                emf -> selectRandomEntityInstance(
                        emf,
                        entityClass,
                        (em, v) -> {
                            ___Validation_TestUtils.assertValid(v);
                            randomEntityInstanceSelected__(emf, em, v);
                            return v;
                        }
                )
        ));
        assumeTrue(selected.isPresent(),
                   () -> "no row of " + entityClass.getSimpleName() + " is installed");
    }

    /**
     * Called with the instance {@link #selectRandomEntityInstance__()} selected, while it is still
     * managed.
     *
     * @param entityManagerFactory the factory of the {@code entityManager}.
     * @param entityManager        the entity manager which selected the instance.
     * @param entityInstance       the randomly selected instance.
     * @implSpec This class does nothing; a subclass overrides it to assert what its entity
     *         promises of an installed row.
     */
    protected void randomEntityInstanceSelected__(final EntityManagerFactory entityManagerFactory,
                                                  final EntityManager entityManager,
                                                  final T entityInstance) {
        // empty
    }

    // ---------------------------------------------------------------------------------------------

//    /**
//     * Verifies that {@code find} reaches a randomly selected installed row, once the context
//     the row
//     * was selected in is cleared, and that the instance found carries the same identifier.
//     *
//     * @implSpec An empty table is not a failure; the test aborts, since the table's content
//     *         is the installer's.
//     */
//    @DisplayName("find reaches a randomly selected row once the context is cleared")
//    @Test
//    void find_RandomlySelected_ContextCleared() {
//        applyEntityManagerFactory(emf -> applyEntityManager(
//                emf,
//                m -> applyInTransactionAndRollback(m, em -> {
//                    final var selected = selectRandomEntity(em, entityClass);
//                    assumeTrue(selected.isPresent(),
//                               () -> "no row of " + entityClass.getSimpleName() + " is
//                               installed");
//                    final var identifier =
//                            emf.getPersistenceUnitUtil().getIdentifier(selected.get());
//                    em.clear();
//                    final var found = em.find(entityClass, identifier);
//                    assertThat(found)
//                            .as("the %s found by its identifier, from the database",
//                                entityClass.getSimpleName())
//                            .isNotNull()
//                            .isNotSameAs(selected.get());
//                    assertThat(emf.getPersistenceUnitUtil().getIdentifier(found))
//                            .as("the identifier of the %s found", entityClass.getSimpleName())
//                            .isEqualTo(identifier);
//                    return null;
//                })
//        ));
//    }

    // ---------------------------------------------------------------------------------------------
    protected final Class<T> entityClass;

    /**
     * The class of the identifier of {@link #entityClass}: its id class, the type of its
     * {@code @EmbeddedId}, or the type of its {@code @Id}.
     */
    protected final Class<U> idClass;
}
