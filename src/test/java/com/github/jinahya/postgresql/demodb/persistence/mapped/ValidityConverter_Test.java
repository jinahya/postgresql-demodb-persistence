package com.github.jinahya.postgresql.demodb.persistence.mapped;

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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRouteId.ValidityConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing the {@link ValidityConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class ValidityConverter_Test {

    private static final Instant LOWER = Instant.parse("2025-10-01T00:00:00Z");

    private static final Instant UPPER = Instant.parse("2025-11-01T00:00:00Z");

    private final ValidityConverter converter = new ValidityConverter();

    @Test
    void null_null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    /**
     * Verifies that a range is written as the server prints it in UTC, and read back as it was --
     * which Hibernate ORM relies on, copying the attribute through both methods.
     */
    @Test
    void roundTrip() {
        final var text = converter.convertToDatabaseColumn(new Instant[] {LOWER, UPPER});
        assertThat(text).isEqualTo("[\"2025-10-01 00:00:00+00\",\"2025-11-01 00:00:00+00\")");
        assertThat(converter.convertToEntityAttribute(text)).containsExactly(LOWER, UPPER);
    }

    /**
     * Verifies that the bounds are read as instants, whatever offset the session prints them in.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "[\"2025-10-01 00:00:00+00\",\"2025-11-01 00:00:00+00\")",
            "[\"2025-10-01 09:00:00+09\",\"2025-11-01 09:00:00+09\")",
            "[\"2025-09-30 19:00:00-05\",\"2025-10-31 19:00:00-05\")",
            "[\"2025-10-01 05:30:00+05:30\",\"2025-11-01 05:30:00+05:30\")"
    })
    void convertToEntityAttribute_SameInstants_AnyOffset(final String dbData) {
        assertThat(converter.convertToEntityAttribute(dbData)).containsExactly(LOWER, UPPER);
    }

    @Test
    void roundTrip_Fraction() {
        final var lower = Instant.parse("2025-10-01T00:00:00.123456Z");
        final var text = converter.convertToDatabaseColumn(new Instant[] {lower, UPPER});
        assertThat(text).startsWith("[\"2025-10-01 00:00:00.123456+00\",");
        assertThat(converter.convertToEntityAttribute(text)).containsExactly(lower, UPPER);
    }

    /**
     * Verifies that a range of any other shape than {@code [lower,upper)} is rejected.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "empty",
            "(\"2025-10-01 00:00:00+00\",\"2025-11-01 00:00:00+00\")",
            "[\"2025-10-01 00:00:00+00\",\"2025-11-01 00:00:00+00\"]",
            "[\"2025-10-01 00:00:00+00\",)",
            "(,\"2025-11-01 00:00:00+00\")",
            "[-infinity,infinity)",
            "[\"2025-10-01 00:00:00+00\",infinity)",
            "[\"2025-10-01\",\"2025-11-01\")"
    })
    void convertToEntityAttribute_IllegalArgumentException_OtherShape(final String dbData) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convertToDatabaseColumn_IllegalArgumentException_NotTwoBounds() {
        assertThatThrownBy(() -> converter.convertToDatabaseColumn(new Instant[] {LOWER}))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> converter.convertToDatabaseColumn(new Instant[] {LOWER, null}))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
