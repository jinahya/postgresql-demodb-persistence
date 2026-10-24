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
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.ManagedType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * An abstract base class for tests which need a persistence context, against a module's unit-test
 * persistence unit.
 * <p>
 * The unit is the one named {@value #PERSISTENCE_UNIT_NAME}, bootstrapped the Java SE way; each
 * {@code apply...} method opens a factory of it, on an in-memory database whose schema the provider
 * generates, and closes it once done.
 * <p>
 * Nothing here reaches the PostgreSQL database; a test which needs the real data extends
 * {@link _Entity_Persistence_IT} and is named {@code *_IT} so that failsafe, not surefire, runs
 * it.
 *
 * @param <T> the type of the entity under test.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
abstract class _Entity_Persistence_Test<T> {

    /**
     * The name of the persistence unit this class bootstraps. The value is {@value}.
     */
    static final String PERSISTENCE_UNIT_NAME = "demonstration-test";

    /**
     * The prefix of the constants {@link #_Resolvable_ATTRIBUTE_NAME_()} checks. The value is
     * {@value}.
     */
    private static final String ATTRIBUTE_NAME_CONSTANT_PREFIX = "ATTRIBUTE_NAME_";

    /**
     * The prefix of the constants {@link #_Resolvable_ATTRIBUTE_PATH_()} checks. The value is
     * {@value}.
     */
    private static final String ATTRIBUTE_PATH_CONSTANT_PREFIX = "ATTRIBUTE_PATH_";

    /**
     * Creates a new instance for the specified persistence class.
     *
     * @param targetClass the class of the entity under test.
     */
    _Entity_Persistence_Test(final Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns every {@code static final String} field, declared by {@link #targetClass} or
     * inherited by it, whose name starts with the specified prefix, with its value.
     */
    private Map<String, String> constantsPrefixed(final String prefix) {
        final var constants = new TreeMap<String, String>();
        for (Class<?> c = targetClass; c != null; c = c.getSuperclass()) {
            for (final var field : c.getDeclaredFields()) {
                if (!field.getName().startsWith(prefix) || constants.containsKey(field.getName())) {
                    continue;
                }
                final var modifiers = field.getModifiers();
                assertThat(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)
                           && field.getType() == String.class)
                        .as("%s.%s is a static final String", c.getSimpleName(), field.getName())
                        .isTrue();
                field.setAccessible(true);
                try {
                    constants.put(field.getName(), (String) field.get(null));
                } catch (final IllegalAccessException iae) {
                    throw new AssertionError(
                            "failed to read " + c.getSimpleName() + '.' + field.getName(), iae);
                }
            }
        }
        return constants;
    }

    /**
     * Verifies that every {@code ATTRIBUTE_NAME_*} constant of {@link #targetClass} holds the name
     * of a managed attribute of the type.
     *
     * @implNote A constant holding a name the provider does not manage the type by -- one
     *         left behind by a renamed field, or one naming an attribute of some other class --
     *         fails. A name is resolved as a path of one segment; a path into an embedded attribute
     *         is declared as an {@code ATTRIBUTE_PATH_*} constant and checked by
     *         {@link #_Resolvable_ATTRIBUTE_PATH_()}.
     * @see __Persistence_TestUtils#resolveAttributePath(ManagedType, String)
     */
    @DisplayName("every ATTRIBUTE_NAME_* constant names a managed attribute")
    @Test
    void _Resolvable_ATTRIBUTE_NAME_() {
        __Persistence_TestUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, emf -> {
            final var managedType = emf.getMetamodel().managedType(targetClass);
            constantsPrefixed(ATTRIBUTE_NAME_CONSTANT_PREFIX).forEach(
                    (constantName, name) -> assertThatCode(
                            () -> __Persistence_TestUtils.resolveAttributePath(managedType, name))
                            .as("%s.%s = \"%s\" names a managed attribute of %s",
                                targetClass.getSimpleName(), constantName,
                                name, managedType.getJavaType().getSimpleName())
                            .doesNotThrowAnyException());
            return null;
        });
    }

    /**
     * Verifies that every {@code ATTRIBUTE_PATH_*} constant of {@link #targetClass} holds a dotted
     * path which resolves through the metamodel, segment by segment, to an attribute whose name is
     * the path's last segment.
     *
     * @see __Persistence_TestUtils#resolveAttributePath(ManagedType, String)
     */
    @DisplayName("every ATTRIBUTE_PATH_* constant resolves through the metamodel")
    @Test
    void _Resolvable_ATTRIBUTE_PATH_() {
        __Persistence_TestUtils.applyEntityManagerFactory(PERSISTENCE_UNIT_NAME, emf -> {
            final var managedType = emf.getMetamodel().managedType(targetClass);
            constantsPrefixed(ATTRIBUTE_PATH_CONSTANT_PREFIX).forEach((constantName, path) -> {
                assertThat(path)
                        .as("%s.%s is a dotted path", targetClass.getSimpleName(), constantName)
                        .contains(".");
                final var resolved =
                        __Persistence_TestUtils.resolveAttributePath(managedType, path);
                assertThat(resolved.getName())
                        .as("%s.%s = \"%s\" resolves to its last segment",
                            targetClass.getSimpleName(), constantName,
                            path)
                        .isEqualTo(path.substring(path.lastIndexOf('.') + 1));
            });
            return null;
        });
    }

    /**
     * Verifies that a randomized instance of {@link #targetClass} can be persisted and flushed.
     *
     * @implNote The work happens in a transaction which is always rolled back, so the row
     *         never survives the test.
     * @implSpec A subclass whose entity is not to be persisted overrides this method,
     *         without {@link Test @Test}.
     */
    @Test
    void persistRandomizedInstance() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            log.debug("persisted: {}", v);
            randomizedInstancePersisted(v);
            return v;
        });
    }

    void randomizedInstancePersisted(final T entityInstance) {
    }

    /**
     * Verifies that a newly persisted instance of {@link #targetClass} equals, and hashes the same
     * as, the reference to its row, taken once the instance is detached.
     *
     * @implNote The context is cleared so that
     *         {@link EntityManager#getReference(Object) getReference} does not answer the managed
     *         instance itself; what comes back is a lazy proxy, under a provider which makes one.
     *         The reference is taken from the detached instance, so no identifier has to be
     *         assembled, which keeps an {@link jakarta.persistence.IdClass IdClass} in reach.
     */
    @DisplayName("persisted.equals(getReference(persisted))")
    @Test
    void _persist_EqualsReference() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            em.clear();
            final var reference = em.getReference(v);
            assertThat(v.equals(reference))
                    .as("persisted.equals(reference); reference: %s", reference.getClass())
                    .isTrue();
            assertThat(v.hashCode())
                    .as("persisted.hashCode() == reference.hashCode(); reference: %s",
                        reference.getClass())
                    .isEqualTo(reference.hashCode());
            return reference;
        });
    }

    /**
     * Verifies that the reference to the row of a newly persisted instance of {@link #targetClass},
     * taken once the instance is detached, equals the instance.
     *
     * @implNote This is the other direction of {@link #_persist_EqualsReference()}; here
     *         {@code equals} runs on the reference itself.
     */
    @DisplayName("getReference(persisted).equals(persisted)")
    @Test
    void _persist_ReferenceEquals() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            em.clear();
            final var reference = em.getReference(v);
            assertThat(reference.equals(v))
                    .as("reference.equals(persisted); reference: %s", reference.getClass())
                    .isTrue();
            return reference;
        });
    }

    /**
     * Verifies that a newly persisted instance of {@link #targetClass}, put in a {@link HashSet}
     * before it is flushed, is still found there once it is flushed, and that the instance
     * {@code merge} answers for it, detached, and the reference to its row, are found there too.
     *
     * @implNote The flush is where an identifier from an {@code IDENTITY} column is
     *         assigned under EclipseLink, so an {@code equals}/{@code hashCode} which followed the
     *         identifier would lose the instance there; Hibernate ORM assigns it as {@code persist}
     *         is called, earlier than the instance reaches this test. {@code merge} stands in for
     *         {@code find}, which would need the identifier, and
     *         {@link jakarta.persistence.PersistenceUnitUtil#getIdentifier} does not assemble an id
     *         class under EclipseLink.
     */
    @DisplayName(
            "a HashSet still contains the persisted instance across flush, merge, and getReference")
    @Test
    void _persist_HashSetMembership() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            final var set = new HashSet<T>();
            set.add(v);
            em.flush();
            assertThat(set)
                    .as("the set, after the flush, of the persisted %s",
                        targetClass.getSimpleName())
                    .contains(v);
            em.clear();
            final var merged = em.merge(v);
            assertThat(set)
                    .as("the set, of the %s merged into a cleared context",
                        targetClass.getSimpleName())
                    .contains(merged);
            em.clear();
            final var reference = em.getReference(v);
            assertThat(set)
                    .as("the set, of the reference to the %s; reference: %s",
                        targetClass.getSimpleName(),
                        reference.getClass())
                    .contains(reference);
            return set;
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies a new entity manager, of a new factory of the {@value #PERSISTENCE_UNIT_NAME} unit,
     * to the specified function, and closes both.
     *
     * @param function the function to apply the entity manager to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @implNote The transaction is left to the {@code function}: the unit is
     *         {@code RESOURCE_LOCAL}, so there is no container transaction to join.
     * @see __Persistence_TestUtils#applyEntityManager(String, Function)
     */
    <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        return __Persistence_TestUtils.applyEntityManager(PERSISTENCE_UNIT_NAME, function);
    }

    /**
     * Applies a new entity manager to the specified function, within a transaction which is always
     * rolled back.
     *
     * @param function the function to apply the entity manager to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @implNote The rollback is what keeps one test from being visible to the next, on a
     *         schema which is recreated per factory anyway.
     * @see __Persistence_TestUtils#applyEntityManagerInTransactionAndRollback(String, Function)
     */
    <R> R applyEntityManagerInTransactionAndRollback(
            final Function<? super EntityManager, ? extends R> function) {
        return __Persistence_TestUtils.applyEntityManagerInTransactionAndRollback(
                PERSISTENCE_UNIT_NAME, function);
    }

    /**
     * Applies a new entity manager to the specified function, within a transaction which is
     * committed when the function returns, and rolled back when it throws.
     *
     * @param function the function to apply the entity manager to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @implNote What this writes survives the transaction, but not the factory: the schema
     *         is generated per factory and the in-memory database goes with it.
     * @see __Persistence_TestUtils#applyEntityManagerInTransactionAndCommit(String, Function)
     */
    <R> R applyEntityManagerInTransactionAndCommit(
            final Function<? super EntityManager, ? extends R> function) {
        return __Persistence_TestUtils.applyEntityManagerInTransactionAndCommit(
                PERSISTENCE_UNIT_NAME, function);
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
     * @implNote The instance is persisted inside the same transaction the {@code function}
     *         runs in, so it is still managed when the function sees it, and it is gone when the
     *         transaction rolls back.
     */
    <R> R applyNewPersistedEntityInstanceAndRollback(
            final BiFunction<? super EntityManager, ? super T, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransactionAndRollback(em -> {
            final var persisted = EntityPersisterUtils.newPersistedInstanceOf(em, targetClass);
            return function.apply(em, persisted);
        });
    }

    /**
     * Persists a new randomized instance of {@link #targetClass}, and applies it, along with the
     * entity manager, to the specified function, within a transaction which is committed when the
     * function returns, and rolled back when it throws.
     *
     * @param function the function to apply the entity manager and the persisted instance to.
     * @param <R>      the type of the result.
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when {@link #targetClass} has no usable randomizer or no
     *                                  usable persister; see
     *                                  {@link
     *                                  EntityPersisterUtils#newPersistedInstanceOf(EntityManager,
     *                                  Class)}.
     * @implNote The persisted row outlives the call, on a database which is thrown away
     *         with the factory; a test which only needs the instance for the length of the
     *         {@code function} wants the rolled-back one.
     */
    <R> R applyNewPersistedEntityInstanceAndCommit(
            final BiFunction<? super EntityManager, ? super T, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerInTransactionAndCommit(em -> {
            final var persisted = EntityPersisterUtils.newPersistedInstanceOf(em, targetClass);
            return function.apply(em, persisted);
        });
    }

    // --------------------------------------------------------------------------------- targetClass

    // ---------------------------------------------------------------------------------------------

    /**
     * The class which this test targets.
     */
    final Class<T> targetClass;
}
