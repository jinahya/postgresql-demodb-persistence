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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat.FareConditions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link FareConditions}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FareConditions_Test {

    /**
     * Verifies that every value the check constraint allows has a constant.
     */
    @ParameterizedTest
    @ValueSource(strings = {"Economy", "Comfort", "Business"})
    void valueOfColumnValue_Constant_ConstraintValue(final String columnValue) {
        assertThat(FareConditions.valueOfColumnValue(columnValue).columnValue())
                .isEqualTo(columnValue);
    }

    @ParameterizedTest
    @EnumSource(FareConditions.class)
    void valueOfColumnValue_Same_ColumnValue(final FareConditions constant) {
        assertThat(FareConditions.valueOfColumnValue(constant.columnValue())).isSameAs(constant);
    }

    @ParameterizedTest
    @ValueSource(strings = {"economy", "ECONOMY", "First", ""})
    void valueOfColumnValue_IllegalArgumentException_Unknown(final String columnValue) {
        assertThatThrownBy(() -> FareConditions.valueOfColumnValue(columnValue))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
