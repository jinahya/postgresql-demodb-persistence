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
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import static com.github.jinahya.postgresql.demodb.persistence.___PersistenceTest_Utils.applyEntityManagerFactory;
import static com.github.jinahya.postgresql.demodb.persistence.___PersistenceTest_Utils.applyEntityManager_;
import static com.github.jinahya.postgresql.demodb.persistence.___Persistence_TestUtils.assumeNotWoven;
import static com.github.jinahya.postgresql.demodb.persistence.___Persistence_TestUtils.selectRandomEntityInstance;
import static com.github.jinahya.persistence.test.util.EntityPersisterUtils.newPersistedInstanceOf;
import static com.github.jinahya.persistence.test.util.JinahyaEntityManagerTestUtils.selectRandomEntityId;
import static com.github.jinahya.persistence.test.util.JinahyaEntityManagerTestUtils.selectRandomEntityReference;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerFactoryUtils.applyEntityManager;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerUtils.applyInTransactionAndRollback;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * The unit tests of an entity which need a persistence context, against the
 * {@value ___PersistenceTest_Utils#PERSISTENCE_UNIT_NAME} unit: an in-memory H2 database whose
 * schema the provider generates from the mappings, empty but for what the tests of the run write.
 * <p>
 * The base holds no entity manager. Each test takes the factory from
 * {@link ___PersistenceTest_Utils} and goes through the jinahya library directly, one entity
 * manager per call. Nothing here reaches PostgreSQL; a test which needs its data, its constraints or its
 * triggers extends {@link _Entity_PersistenceIT} and is named {@code *_PersistenceIT}, so that
 * failsafe, not surefire, runs it.
 *
 * @param <T> the type of the entity.
 * @param <U> the type of the identifier of the entity: its id class, the type of its
 *            {@code @EmbeddedId}, or the type of its {@code @Id}.
 * @see ___PersistenceTest_Utils
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
@Slf4j
public abstract class _Entity_PersistenceTest<T, U>
        extends __Test<T> {

    /**
     * The prefix of the constants {@link #_SameValue_ATTRIBUTE_NAME_()} checks. The value is
     * {@value}.
     */
    private static final String ATTRIBUTE_NAME_CONSTANT_PREFIX = "ATTRIBUTE_NAME_";

    /**
     * The prefix of the constants {@link #_Resolvable_ATTRIBUTE_PATH_()} checks. The value is
     * {@value}.
     */
    private static final String ATTRIBUTE_PATH_CONSTANT_PREFIX = "ATTRIBUTE_PATH_";

    /**
     * Returns the {@code UPPER_SNAKE_CASE} form of the specified attribute name.
     *
     * @param attributeName the attribute name to convert; {@code airportCode}, say.
     * @return the converted name; {@code AIRPORT_CODE} for the example above.
     * @implNote An underscore goes wherever a lower-case letter or a digit is followed by
     *         an upper-case one, which is the boundary the constants in this project are spelled
     *         with.
     */
    private static String toUpperSnakeCase(final String attributeName) {
        return attributeName.replaceAll("(?<=[a-z0-9])(?=[A-Z])", "_").toUpperCase(Locale.ROOT);
    }

    /**
     * Creates a new instance for the specified entity class.
     *
     * @param targetClass the class of the entity under test.
     * @param idClass     the class of the identifier of the entity: its id class, the type of its
     *                    {@code @EmbeddedId}, or the type of its {@code @Id}.
     */
    protected _Entity_PersistenceTest(final Class<T> targetClass, final Class<U> idClass) {
        super(targetClass);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Selects a reference to a randomly chosen instance of {@link #targetClass}, with an entity
     * manager of its own of the {@value ___PersistenceTest_Utils#PERSISTENCE_UNIT_NAME} unit,
     * through {@code JinahyaEntityManagerTestUtils.selectRandomEntityReference}.
     *
     * @return an optional of the reference selected; {@code empty} when there is no instance.
     * @apiNote The entity manager is closed when this method returns, so the reference is
     *         detached; only its identifier is safe to read, for its state may never have been
     *         loaded.
     */
    Optional<T> selectRandomEntityReference_() {
        return applyEntityManager_(
                emf -> em -> selectRandomEntityReference(em, targetClass)
        );
    }

    /**
     * Selects the identifier of a randomly chosen instance of {@link #targetClass}, as an instance
     * of {@link #idClass}, with an entity manager of its own of the
     * {@value ___PersistenceTest_Utils#PERSISTENCE_UNIT_NAME} unit, through
     * {@code JinahyaEntityManagerTestUtils.selectRandomEntityId}.
     *
     * @return an optional of the identifier selected; {@code empty} when there is no instance.
     */
    Optional<U> selectRandomEntityId_() {
        return applyEntityManager_(
                emf -> em -> selectRandomEntityId(em, targetClass, idClass)
        );
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
     * Verifies the {@code ATTRIBUTE_NAME_*} constants of {@link #targetClass} both ways: every
     * managed attribute whose constant exists finds that constant holding the attribute's name, and
     * every such constant names a managed attribute of the type.
     *
     * @implNote The first direction runs from the attribute to the constant, so an
     *         attribute with no constant is skipped. The second runs from the constant to the
     *         attribute, so a constant holding a name the provider does not manage the type by --
     *         one left behind by a renamed field, or one naming an attribute of some other class --
     *         fails. A path into an embedded attribute is not an attribute name; it is declared as
     *         an {@code ATTRIBUTE_PATH_*} constant and checked by
     *         {@link #_Resolvable_ATTRIBUTE_PATH_()}.
     */
    @DisplayName("every ATTRIBUTE_NAME_* constant names a managed attribute, and vice versa")
    @Test
    protected void _SameValue_ATTRIBUTE_NAME_() {
        final var managedType =
                applyEntityManagerFactory(EntityManagerFactory::getMetamodel).managedType(
                        targetClass);
        final var constants = constantsPrefixed(ATTRIBUTE_NAME_CONSTANT_PREFIX);
        for (final var attribute : managedType.getAttributes()) {
            final var attributeName = attribute.getName();
            final var constantName =
                    ATTRIBUTE_NAME_CONSTANT_PREFIX + toUpperSnakeCase(attributeName);
            if (!constants.containsKey(constantName)) {
                continue; // existence is not this direction's concern
            }
            assertThat(constants.get(constantName))
                    .as("%s.%s, for the managed attribute %s", targetClass.getSimpleName(),
                        constantName, attributeName)
                    .isEqualTo(attributeName);
        }
        constants.forEach(
                (constantName, value) -> assertThatCode(() -> managedType.getAttribute(value))
                        .as("%s.%s = \"%s\" names a managed attribute of %s",
                            targetClass.getSimpleName(), constantName, value,
                            managedType.getJavaType().getSimpleName())
                        .doesNotThrowAnyException());
    }

    /**
     * Verifies that every {@code ATTRIBUTE_PATH_*} constant of {@link #targetClass} holds a dotted
     * path which resolves, segment by segment, from the entity, as a query navigates it.
     *
     * @implNote The path is navigated with a criteria {@link Path}, from a root of the
     *         entity, not with {@code JinayaManagedTypeUtils.resolveAttributePath}, which reads an
     *         attribute's type where it is declared: {@code MappedRoute}'s {@code id} is
     *         of its type variable, an {@link Object} there, while a root of the entity knows the
     *         id class.
     */
    @DisplayName("every ATTRIBUTE_PATH_* constant resolves through the metamodel")
    @Test
    protected void _Resolvable_ATTRIBUTE_PATH_() {
        final var paths = constantsPrefixed(ATTRIBUTE_PATH_CONSTANT_PREFIX);
        applyEntityManagerFactory(emf -> applyEntityManager(emf, em -> {
            paths.forEach((constantName, attributePath) -> {
                assertThat(attributePath)
                        .as("%s.%s is a dotted path", targetClass.getSimpleName(), constantName)
                        .contains(".");
                assertThatCode(() -> {
                    Path<?> path = em.getCriteriaBuilder().createQuery().from(targetClass);
                    for (final var name : attributePath.split("\\.")) {
                        path = path.get(name);
                    }
                }).as("%s.%s = \"%s\" resolves from %s", targetClass.getSimpleName(), constantName,
                      attributePath, targetClass.getSimpleName())
                        .doesNotThrowAnyException();
            });
            return null;
        }));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Persists a new, randomized instance of {@link #targetClass}, through its persister, flushes
     * it, and hands it to
     * {@link #randomizedEntityInstancePersisted__(EntityManagerFactory, EntityManager, Object)}.
     * <p>
     * An entity which is not persisted -- a view, or a table which only a trigger of the database
     * writes -- overrides this method with an empty one,
     * {@link org.junit.jupiter.api.Disabled @Disabled} with the reason, as its
     * {@code *_PersistenceIT} does.
     *
     * @implNote The flush is what makes the {@code INSERT} reach the database, against the
     *         schema generated from the mappings; the transaction is always rolled back, so the row
     *         never survives the test.
     */
    @DisplayName("a new randomized instance is persisted")
    @Test
    protected void persistRandomizedEntityInstance__() {
        applyEntityManagerFactory(emf -> applyEntityManager(
                emf,
                m -> applyInTransactionAndRollback(m, em -> {
                    final var persisted = newPersistedInstanceOf(em, targetClass);
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
     * @implSpec This class does nothing; a subclass overrides it to assert what its mapping
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
     * Selects a random instance of {@link #targetClass} and hands it to
     * {@link #randomEntityInstanceSelected__(EntityManagerFactory, EntityManager, Object)}.
     *
     * @implNote The selection and the callback both run inside a transaction which is
     *         rolled back, so the instance handed over is still managed, and nothing the callback
     *         does survives the test.
     * @implSpec Selecting from an empty table is not a failure: the schema is generated
     *         empty, and every test rolls back, so the query is what is tested, and
     *         {@link #randomEntityInstanceSelected__(EntityManagerFactory, EntityManager, Object)}
     *         is invoked only when a row is there.
     */
    @DisplayName("a random instance is selected")
    @Test
    protected void selectRandomEntityInstance__() {
        applyEntityManagerFactory(
                emf -> selectRandomEntityInstance(
                        emf,
                        targetClass,
                        (em, v) -> {
                            randomEntityInstanceSelected__(emf, em, v);
                            return null;
                        }
                )
        );
    }

    /**
     * Called with the instance {@link #selectRandomEntityInstance__()} selected, while it is still
     * managed.
     *
     * @param entityManagerFactory the factory of the {@code entityManager}.
     * @param entityManager        the entity manager which selected the instance.
     * @param entityInstance       the randomly selected instance.
     * @implSpec This class does nothing; a subclass overrides it to assert what its entity
     *         promises of a row.
     */
    protected void randomEntityInstanceSelected__(final EntityManagerFactory entityManagerFactory,
                                                  final EntityManager entityManager,
                                                  final T entityInstance) {
        // empty
    }

    // ------------------------------------------------------------ equality across the context

    /**
     * Skips the calling test when EclipseLink wove {@link #targetClass} and its identifier includes
     * an association: woven, EclipseLink's {@link EntityManager#getReference(Object) getReference}
     * builds such an association with no session, and fails with a {@link NullPointerException};
     * the unwoven runs verify the same methods.
     */
    void assumeReferenceable() {
        for (Class<?> c = targetClass; c != null; c = c.getSuperclass()) {
            for (final var field : c.getDeclaredFields()) {
                if ((field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(MapsId.class))
                    && (field.isAnnotationPresent(ManyToOne.class)
                        || field.isAnnotationPresent(OneToOne.class))) {
                    assumeNotWoven(targetClass);
                    return;
                }
            }
        }
    }

    /**
     * Applies the specified function to a new, randomized instance of {@link #targetClass},
     * persisted through its persister, inside a transaction which is rolled back.
     *
     * @param function the function to apply, to the entity manager and the persisted instance.
     */
    private void applyNewPersistedEntityInstanceAndRollback(
            final BiConsumer<? super EntityManager, ? super T> function) {
        applyEntityManagerFactory(emf -> applyEntityManager(
                emf,
                m -> applyInTransactionAndRollback(m, em -> {
                    function.accept(em, newPersistedInstanceOf(em, targetClass));
                    return null;
                })
        ));
    }

    /**
     * Verifies that a newly persisted instance of {@link #targetClass} equals, and hashes as, the
     * reference to its row taken once the instance is detached.
     *
     * @implNote This is the direction a {@link java.util.Set Set} of detached instances
     *         relies on: {@code equals} runs on the instance, with the reference, which may be a
     *         proxy, as its argument.
     */
    @DisplayName("persisted.equals(getReference(persisted))")
    @Test
    void _persist_EqualsReference() {
        assumeReferenceable();
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
        assumeReferenceable();
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            em.clear();
            final var reference = em.getReference(v);
            assertThat(reference.equals(v))
                    .as("reference.equals(persisted); reference: %s", reference.getClass())
                    .isTrue();
        });
    }

    /**
     * Verifies that a newly persisted instance of {@link #targetClass}, put in a {@link HashSet}
     * before it is flushed, is still found there once it is flushed, and that the instance merged
     * into a cleared context, and the reference to its row, are found there too.
     */
    @DisplayName(
            "a HashSet still contains the persisted instance across flush, merge, and getReference")
    @Test
    void _persist_HashSetMembership() {
        assumeReferenceable();
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
                        targetClass.getSimpleName(), reference.getClass())
                    .contains(reference);
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The class of the identifier of {@link #targetClass}: its id class, the type of its
     * {@code @EmbeddedId}, or the type of its {@code @Id}.
     */
    protected final Class<U> idClass;
}
