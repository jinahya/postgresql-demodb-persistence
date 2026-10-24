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

/**
 * An abstract base class for testing a value class -- an id class, an embeddable, a mapped JSON
 * document -- which has no identity of its own, and so no persistence test either.
 * <p>
 * Its checks are the ones a class has to pass on its own: a usable {@code toString()}, an
 * {@code equals}/{@code hashCode} pair, and property accessors which round-trip.
 *
 * @param <T> the type of the value class.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S118"  // Abstract class names should comply with a naming convention
})
abstract class _NonEntity_Test<T> {

    /**
     * Creates a new instance for the specified target class.
     *
     * @param targetClass the value class to test.
     */
    _NonEntity_Test(final Class<T> targetClass) {
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
    SingleTypeEqualsVerifierApi<T> equals_verifier_() {
        return EqualsVerifier.simple().forClass(targetClass);
    }

    /**
     * Verifies the {@code equals}/{@code hashCode} contract of {@link #targetClass}, using the
     * verifier which {@link #equals_verifier_()} returns.
     */
    @Test
    void equals_verify_() {
        final var verifier = equals_verifier_();
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
