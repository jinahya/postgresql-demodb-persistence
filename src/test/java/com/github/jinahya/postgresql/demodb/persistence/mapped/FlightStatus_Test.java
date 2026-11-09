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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link FlightStatus}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class FlightStatus_Test {

    /**
     * Verifies that every value the check constraint allows has a constant.
     */
    @ParameterizedTest
    @ValueSource(
            strings = {"Scheduled", "On Time", "Delayed", "Boarding", "Departed", "Arrived",
                    "Cancelled"})
    void valueOfColumnValue_Constant_ConstraintValue(final String columnValue) {
        assertThat(FlightStatus.valueOfColumnValue(columnValue).columnValue())
                .isEqualTo(columnValue);
    }

    @ParameterizedTest
    @EnumSource(FlightStatus.class)
    void valueOfColumnValue_Same_ColumnValue(final FlightStatus constant) {
        assertThat(FlightStatus.valueOfColumnValue(constant.columnValue())).isSameAs(constant);
    }

    @ParameterizedTest
    @ValueSource(strings = {"on time", "OnTime", "ON_TIME", ""})
    void valueOfColumnValue_IllegalArgumentException_Unknown(final String columnValue) {
        assertThatThrownBy(() -> FlightStatus.valueOfColumnValue(columnValue))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
