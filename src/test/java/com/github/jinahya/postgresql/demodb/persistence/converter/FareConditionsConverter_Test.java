package com.github.jinahya.postgresql.demodb.persistence.converter;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link FareConditionsConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class FareConditionsConverter_Test {

    /**
     * Verifies that every value the check constraint allows has a constant.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Economy", "Comfort", "Business"})
    void convertToEntityAttribute_Constant_ConstraintValue(final String text) {
        assertThat(new FareConditionsConverter().convertToEntityAttribute(text).text()).isEqualTo(text);
    }

    @ParameterizedTest
    @EnumSource(FareConditions.class)
    void convertToEntityAttribute_SameConstant_RoundTrip(final FareConditions constant) {
        final var converter = new FareConditionsConverter();
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(constant)))
                .isSameAs(constant);
    }

    @ParameterizedTest
    @ValueSource(strings = {"economy", "ECONOMY", "First", ""})
    void convertToEntityAttribute_IllegalArgumentException_Unknown(final String text) {
        assertThatThrownBy(() -> new FareConditionsConverter().convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convertBoth_Null_Null() {
        final var converter = new FareConditionsConverter();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}

