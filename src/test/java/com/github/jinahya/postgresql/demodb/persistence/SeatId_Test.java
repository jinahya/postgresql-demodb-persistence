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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplaneData;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SeatId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link SeatId} composite primary key class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SeatId_Test
        extends _NonEntity_Test<SeatId> {

    SeatId_Test() {
        super(SeatId.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link SeatId#from(MappedAirplaneData, String)}.
     */
    @DisplayName("from(airplaneData, seatNo)")
    @Nested
    class FromAirplaneDataSeatNoTest {

        /**
         * Verifies that {@link SeatId#from(MappedAirplaneData, String) from} returns an instance of the
         * airplane's code and the seat number.
         */
        @Test
        void __() {
            final var airplaneData = mock(AirplaneData.class);
            doReturn("773").when(airplaneData).getAirplaneCode();
            final var instance = SeatId.from(airplaneData, "1A");
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getAirplaneCode()).isEqualTo("773");
                assertThat(v.getSeatNo()).isEqualTo("1A");
            });
        }

        /**
         * Verifies that {@link SeatId#from(MappedAirplaneData, String) from} gives a {@code null} code
         * for a {@code null} airplane.
         */
        @Test
        void __null() {
            final var instance = SeatId.from(null, null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getAirplaneCode()).isNull();
                assertThat(v.getSeatNo()).isNull();
            });
        }
    }
}
