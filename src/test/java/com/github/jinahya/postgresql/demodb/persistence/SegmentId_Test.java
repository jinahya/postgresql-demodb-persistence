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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTicket;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link SegmentId} composite primary key class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class SegmentId_Test
        extends _NonEntity_Test<SegmentId> {

    SegmentId_Test() {
        super(SegmentId.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link SegmentId#from(MappedTicket, MappedFlight)}.
     */
    @DisplayName("from(ticket, flight)")
    @Nested
    class FromTicketFlightTest {

        /**
         * Verifies that {@link SegmentId#from(MappedTicket, MappedFlight) from} returns an instance of the
         * identifiers of the ticket and the flight.
         */
        @Test
        void __() {
            final var ticket = mock(Ticket.class);
            doReturn("0005432000987").when(ticket).getTicketNo();
            final var flight = mock(Flight.class);
            doReturn(1).when(flight).getFlightId();
            final var instance = SegmentId.from(ticket, flight);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getTicketNo()).isEqualTo("0005432000987");
                assertThat(v.getFlightId()).isEqualTo(1);
            });
        }

        /**
         * Verifies that {@link SegmentId#from(MappedTicket, MappedFlight) from} gives {@code null} values for a
         * {@code null} ticket and a {@code null} flight.
         */
        @Test
        void __null() {
            final var instance = SegmentId.from(null, null);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getTicketNo()).isNull();
                assertThat(v.getFlightId()).isNull();
            });
        }
    }
}
