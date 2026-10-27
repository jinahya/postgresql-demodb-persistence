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

import com.github.jinahya.object.randomizer.ObjectRandomizerUtils;
import jakarta.persistence.Transient;
import lombok.extern.slf4j.Slf4j;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.beans.IntrospectionException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * An abstract base class for testing, without a persistence context, a class which maps a table or
 * a view.
 * <p>
 * Its checks are, for now, the ones {@link _NonEntity_Test} runs too -- a usable
 * {@code toString()}, an {@code equals}/{@code hashCode} pair, and property accessors which
 * round-trip; checks only such a class has to pass go here.
 *
 * @param <T> the type of the class.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
abstract class _Entity_Test<T> {

    /**
     * Creates a new instance for the specified target class.
     *
     * @param targetClass the class to test.
     */
    _Entity_Test(final Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // ------------------------------------------------------------------------------------ toString

    /**
     * Verifies that {@code toString()} of a new instance of {@link #targetClass} is not blank.
     */
    @Test
    void toString_NotBlank_NewInstance() {
        final var instance = newTargetInstance();
        final var string = instance.toString();
        assertThat(string).isNotBlank();
    }

    /**
     * Verifies that {@code toString()} of a new randomized instance of {@link #targetClass} is not
     * blank.
     */
    @Test
    void toString_NotBlank_NewRandomizedInstance() {
        assertThat(newRandomizedTargetInstance().toString()).isNotBlank();
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * Returns an equals-verifier for {@link #targetClass}, which subclasses may further configure.
     *
     * @return an equals-verifier for {@link #targetClass}.
     */
    SingleTypeEqualsVerifierApi<T> equalsVerifier() {
        return EqualsVerifier.simple().forClass(targetClass);
    }

    /**
     * Verifies the {@code equals}/{@code hashCode} contract of {@link #targetClass}, using the
     * verifier which {@link #equalsVerifier()} returns.
     *
     * @implNote Skipped when {@link #targetClass} is woven by EclipseLink (the
     *         {@code jakarta-ee-11-eclipselink-weaving} profile): the weaver adds
     *         {@code _persistence_*} fields, which the verifier tries to fill, and fails on, though
     *         {@code equals} and {@code hashCode} do not read them. The unwoven runs verify the
     *         same methods.
     * @see __Object_TestUtils#isWovenByEclipseLink(Class)
     */
    @Test
    void equals_verify_() {
        assumeFalse(__Object_TestUtils.isWovenByEclipseLink(targetClass),
                    "woven by EclipseLink; the verifier fails on the _persistence_* fields");
        final var verifier = equalsVerifier();
        verifier.verify();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that every non-{@link Transient @Transient} read/write property of
     * {@link #targetClass} accepts what its own reader returns.
     *
     * @throws IntrospectionException when {@link #targetClass} cannot be introspected.
     * @see __Object_TestUtils#assertPropertyAccessorsDoNotThrow(Class, Object)
     */
    @Test
    void propertyAccessors_DoNotThrow() throws IntrospectionException {
        __Object_TestUtils.assertPropertyAccessorsDoNotThrow(targetClass, newTargetInstance());
    }

    // --------------------------------------------------------------------------------- targetClass

    /**
     * Returns a new, uninitialized instance of {@link #targetClass}.
     *
     * @return a new instance of {@link #targetClass}.
     */
    T newTargetInstance() {
        return ReflectionUtils.newInstance(targetClass);
    }

    /**
     * Returns a new instance of {@link #targetClass} with randomized property values.
     *
     * @return a new randomized instance of {@link #targetClass}.
     * @throws IllegalStateException if no randomizer is registered for {@link #targetClass}.
     */
    T newRandomizedTargetInstance() {
        return ObjectRandomizerUtils.newRandomizedInstanceOf(targetClass)
                .orElseThrow(() -> new IllegalStateException("no randomizer for " + targetClass));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The class which this test targets.
     */
    final Class<T> targetClass;
}
