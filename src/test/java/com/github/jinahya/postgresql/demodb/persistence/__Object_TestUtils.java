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

import jakarta.persistence.Transient;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Helpers for the checks a class has to pass on its own, without a persistence context.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __Object_TestUtils {

    /**
     * Asserts that every non-{@link Transient @Transient} read/write property of the specified
     * class accepts, on the specified instance, what its own reader returns.
     *
     * @param targetClass the class whose properties are checked.
     * @param instance    the instance to read from and write to.
     * @param <T>         the type of the class.
     * @throws IntrospectionException when {@code targetClass} cannot be introspected.
     */
    static <T> void assertPropertyAccessorsDoNotThrow(final Class<T> targetClass, final T instance)
            throws IntrospectionException {
        Objects.requireNonNull(targetClass, "targetClass is null");
        Objects.requireNonNull(instance, "instance is null");
        final var info = Introspector.getBeanInfo(targetClass);
        for (final var descriptor : info.getPropertyDescriptors()) {
            final var reader = descriptor.getReadMethod();
            final var writer = descriptor.getWriteMethod();
            if (reader == null || writer == null
                || reader.isAnnotationPresent(Transient.class)
                || writer.isAnnotationPresent(Transient.class)) {
                continue;
            }
            reader.setAccessible(true);
            writer.setAccessible(true);
            assertThatCode(() -> writer.invoke(instance, reader.invoke(instance)))
                    .as("%s.%s(%s())", targetClass.getSimpleName(), writer.getName(),
                        reader.getName())
                    .doesNotThrowAnyException();
        }
    }

    /**
     * The name of the interface EclipseLink's weaver adds to every class it weaves.
     */
    private static final String ECLIPSELINK_PERSISTENCE_WEAVED =
            "org.eclipse.persistence.internal.weaving.PersistenceWeaved";

    /**
     * Checks whether the specified class, or one of its superclasses, has been woven by
     * EclipseLink, as the {@code jakarta-ee-11-eclipselink-weaving} profile does to the entities
     * and embeddables of the unit.
     *
     * @param targetClass the class to check.
     * @return {@code true} when {@code targetClass} is woven; {@code false} otherwise.
     * @implNote The interface is matched by name, so that this compiles, and answers
     *         {@code false}, without EclipseLink on the class path.
     */
    static boolean isWovenByEclipseLink(final Class<?> targetClass) {
        Objects.requireNonNull(targetClass, "targetClass is null");
        for (Class<?> c = targetClass; c != null; c = c.getSuperclass()) {
            for (final var i : c.getInterfaces()) {
                if (ECLIPSELINK_PERSISTENCE_WEAVED.equals(i.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    private __Object_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
