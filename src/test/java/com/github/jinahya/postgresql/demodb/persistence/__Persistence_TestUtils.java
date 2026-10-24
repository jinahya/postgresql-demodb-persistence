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
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Helpers for persistence tests.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Every method here takes the entity manager it works with, or the name of the
 *         persistence unit it opens one from, so the same method serves either persistence unit and
 *         nothing here has to know which one the caller is on.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __Persistence_TestUtils {

    /**
     * Applies a new factory of the persistence unit of the specified name to the specified
     * function, and closes the factory.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to apply the factory to.
     * @param <R>                 result type parameter.
     * @return the result of the {@code function}.
     * @implNote Opening a factory is what costs -- the provider reads the descriptor,
     *         builds the metamodel and connects to the database -- and each call opens a new one.
     * @see Persistence#createEntityManagerFactory(String)
     */
    static <R> R applyEntityManagerFactory(
            final String persistenceUnitName,
            final Function<? super EntityManagerFactory, ? extends R> function) {
        Objects.requireNonNull(persistenceUnitName, "persistenceUnitName is null");
        Objects.requireNonNull(function, "function is null");
        try (var entityManagerFactory = Persistence.createEntityManagerFactory(
                persistenceUnitName)) {
            return function.apply(entityManagerFactory);
        }
    }

    static <R> R applyEntityManager(
            final EntityManagerFactory entityManagerFactory,
            final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return function.apply(entityManager);
        }
    }

    /**
     * Applies a new entity manager, of a new factory of the persistence unit of the specified name,
     * to the specified function, and closes both.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to apply the entity manager to.
     * @param <R>                 result type parameter.
     * @return the result of the {@code function}.
     * @implNote The transaction is left to the {@code function}.
     * @see #applyEntityManagerFactory(String, Function)
     * @see EntityManagerFactory#createEntityManager()
     */
    static <R> R applyEntityManager(final String persistenceUnitName,
                                    final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManagerFactory(persistenceUnitName, emf -> {
            try (var entityManager = emf.createEntityManager()) {
                return function.apply(entityManager);
            }
        });
    }

    /**
     * Applies a new entity manager, of a new factory of the persistence unit of the specified name,
     * to the specified function, inside a transaction, and hands the transaction to the specified
     * consumer once the function is done with it.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to apply the entity manager to.
     * @param consumer            the consumer to accept the transaction; invoked whether the
     *                            {@code function} returned or threw.
     * @param <R>                 result type parameter.
     * @return the result of the {@code function}.
     * @apiNote The {@code consumer} decides what becomes of the transaction. It cannot tell
     *         a return from a throw, so committing through it would commit a transaction whose
     *         function failed.
     * @implNote The {@code consumer} should guard on {@link EntityTransaction#isActive()}:
     *         a {@code function} which ended the transaction itself leaves nothing to end, and
     *         ending an inactive transaction throws an {@link IllegalStateException} which would
     *         mask whatever the function did.
     * @see #applyEntityManager(String, Function)
     */
    static <R> R applyEntityManagerInTransaction(
            final String persistenceUnitName,
            final Function<? super EntityManager, ? extends R> function,
            final Consumer<? super EntityTransaction> consumer) {
        Objects.requireNonNull(function, "function is null");
        Objects.requireNonNull(consumer, "consumer is null");
        return applyEntityManager(persistenceUnitName, em -> {
            final var transaction = em.getTransaction();
            transaction.begin();
            try {
                return function.apply(em);
            } finally {
                consumer.accept(transaction);
            }
        });
    }

    /**
     * Applies a new entity manager, of a new factory of the persistence unit of the specified name,
     * to the specified function, inside a transaction which is always rolled back.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to apply the entity manager to.
     * @param <R>                 result type parameter.
     * @return the result of the {@code function}.
     * @implNote The rollback happens whether the {@code function} returned or threw, so a
     *         failing test leaves no more behind than a passing one. It is guarded by
     *         {@link EntityTransaction#isActive()}: a {@code function} which ended the transaction
     *         itself leaves nothing to roll back.
     * @see #applyEntityManagerInTransaction(String, Function, Consumer)
     */
    static <R> R applyEntityManagerInTransactionAndRollback(
            final String persistenceUnitName,
            final Function<? super EntityManager, ? extends R> function) {
        return applyEntityManagerInTransaction(persistenceUnitName, function, t -> {
            if (t.isActive()) {
                t.rollback();
            }
        });
    }

    /**
     * Applies a new entity manager, of a new factory of the persistence unit of the specified name,
     * to the specified function, inside a transaction which is committed when the function returns,
     * and rolled back when it throws.
     *
     * @param persistenceUnitName the name of the persistence unit.
     * @param function            the function to apply the entity manager to.
     * @param <R>                 result type parameter.
     * @return the result of the {@code function}.
     * @apiNote What this writes outlives the call; on the physical database that is the
     *         installed sample data being modified, so a test wants
     *         {@link #applyEntityManagerInTransactionAndRollback(String, Function)} unless it means
     *         to leave something behind.
     * @implNote Not built on
     *         {@link #applyEntityManagerInTransaction(String, Function, Consumer)}: its transaction
     *         consumer runs in a {@code finally}, which cannot tell a return from a throw, and
     *         committing a transaction whose function threw is the one thing this must not do.
     * @see #applyEntityManager(String, Function)
     */
    static <R> R applyEntityManagerInTransactionAndCommit(
            final String persistenceUnitName,
            final Function<? super EntityManager, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyEntityManager(persistenceUnitName, em -> {
            final var transaction = em.getTransaction();
            transaction.begin();
            final R result;
            try {
                result = function.apply(em);
            } catch (final Throwable t) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw t;
            }
            if (transaction.isActive()) {
                transaction.commit();
            }
            return result;
        });
    }

    // ---------------------------------------------------------------------------------------------
    static String getEntityName(final EntityManagerFactory entityManagerFactory,
                                final Class<?> entityClass) {
        Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        return entityManagerFactory.getMetamodel().entity(entityClass).getName();
    }

    static String getEntityName(final EntityManager entityManager, final Class<?> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        return getEntityName(entityManager.getEntityManagerFactory(), entityClass);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the identifier of the specified entity instance.
     *
     * @param emf            the entity manager factory whose persistence unit to ask.
     * @param entityInstance the entity instance whose identifier to read.
     * @return the identifier of the {@code entityInstance}.
     * @apiNote EclipseLink answers this by instantiating the identifier class -- an
     *         {@link jakarta.persistence.EmbeddedId EmbeddedId} or an
     *         {@link jakarta.persistence.IdClass IdClass} -- through
     *         {@link Class#getConstructor(Class[])}, which sees public constructors only; one with
     *         no {@code public} no-arg constructor fails with {@code EclipseLink-222}. The
     *         specification requires that constructor of a primary key class to be public (Jakarta
     *         Persistence 3.2, §2.4), and every identifier class here declares it so.
     * @see jakarta.persistence.PersistenceUnitUtil#getIdentifier(Object)
     */
    static Object getEntityIdentifier(final EntityManagerFactory emf, final Object entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        return emf.getPersistenceUnitUtil().getIdentifier(entityInstance);
    }

    /**
     * Returns the identifier of the specified entity instance.
     *
     * @param entityManager  the entity manager whose persistence unit to ask.
     * @param entityInstance the entity instance whose identifier to read.
     * @return the identifier of the {@code entityInstance}.
     * @apiNote See {@link #getEntityIdentifier(EntityManagerFactory, Object)} for where
     *         this fails under EclipseLink.
     * @see #getEntityIdentifier(EntityManagerFactory, Object)
     */
    static Object getEntityIdentifier(final EntityManager entityManager,
                                      final Object entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        return getEntityIdentifier(entityManager.getEntityManagerFactory(), entityInstance);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Counts the rows mapped by the specified entity class.
     *
     * @param entityManager the entity manager to count with.
     * @param entityClass   the entity class to count instances of.
     * @return the number of rows; {@code 0} when the table is empty.
     * @throws IllegalArgumentException if {@code entityClass} is not an entity of the persistence
     *                                  unit.
     * @implNote Built with the Criteria API, whose root is the class itself, so neither the
     *         JPQL entity name nor the class name goes into a query string.
     *         <p>
     *         {@code COUNT} with no {@code GROUP BY} is an aggregate over the whole table, so it
     *         always yields exactly one row holding a number. It neither returns {@code null} nor
     *         throws {@link jakarta.persistence.NoResultException}, hence no guard against either.
     */
    static long selectEntityCount(final EntityManager entityManager, final Class<?> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var builder = entityManager.getCriteriaBuilder();
        final var query = builder.createQuery(Long.class);
        final var root = query.from(entityClass);
        query.select(builder.count(root));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * Selects a random instance of the specified entity class using the specified entity manager.
     *
     * @param entityManager the entity manager to select with.
     * @param entityClass   the entity class to select an instance of.
     * @param <T>           entity type parameter.
     * @return a random instance of {@code entityClass}; {@link Optional#empty() empty} when its
     *         table is empty.
     * @throws IllegalArgumentException if {@code entityClass} is not an entity of the persistence
     *                                  unit.
     * @implNote Both queries are built with the Criteria API, rooted at the class itself.
     * @see #selectEntityCount(EntityManager, Class)
     */
    static <T> Optional<T> selectRandomEntity(final EntityManager entityManager,
                                              final Class<T> entityClass) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityClass, "entityClass is null");
        final var count = selectEntityCount(entityManager, entityClass);
        if (count == 0L) {
            return Optional.empty();
        }
        final var builder = entityManager.getCriteriaBuilder();
        final var query = builder.createQuery(entityClass);
        final var root = query.from(entityClass);
        query.select(root);
        return entityManager
                .createQuery(query)
                .setFirstResult(Math.toIntExact(ThreadLocalRandom.current().nextLong(count)))
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    // ----------------------------------------------------------------------------- attribute paths

    /**
     * Resolves the specified dotted attribute path, {@code id.promoId} say, from the specified
     * managed type, and returns the attribute the last segment names.
     * <p>
     * Each segment but the last has to name a singular attribute whose type is itself a managed
     * type -- an embeddable or an entity -- which the next segment is then resolved against. The
     * last segment may name any attribute.
     *
     * @param managedType   the managed type to start from.
     * @param attributePath the path to resolve; one or more attribute names joined by {@code .}.
     * @return the attribute the last segment of {@code attributePath} names.
     * @throws NullPointerException     if either argument is {@code null}.
     * @throws IllegalArgumentException if a segment names no attribute of the type it is resolved
     *                                  against, or if a segment but the last names an attribute
     *                                  whose type is not a managed type.
     */
    static Attribute<?, ?> resolveAttributePath(final ManagedType<?> managedType,
                                                final String attributePath) {
        Objects.requireNonNull(managedType, "managedType is null");
        Objects.requireNonNull(attributePath, "attributePath is null");
        final var segments = attributePath.split("\\.", -1);
        ManagedType<?> current = managedType;
        Attribute<?, ?> attribute = null;
        for (int i = 0; i < segments.length; i++) {
            if (current == null) {
                throw new IllegalArgumentException(
                        "'" + segments[i - 1] + "' of '" + attributePath +
                        "' is not of a managed type;"
                        + " cannot resolve '" + segments[i] + "' against it");
            }
            attribute = current.getAttribute(segments[i]); // IllegalArgumentException when absent
            current = attribute instanceof SingularAttribute<?, ?> singular
                      && singular.getType() instanceof ManagedType<?> next ? next : null;
        }
        return Objects.requireNonNull(attribute,
                                      "attribute is null"); // the split gives one segment at least
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    private __Persistence_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
