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

import lombok.extern.slf4j.Slf4j;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link Ticket} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Ticket_Test
        extends _Entity_Test<Ticket, String> {

    Ticket_Test() {
        super(Ticket.class, String.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link Ticket#of(Booking, String)}.
     */
    @DisplayName("of(booking, ticketNo)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link Ticket#of(Booking, String) of} returns an instance whose booking is
         * the very instance it was given, and whose read-only {@code bookRef} is the booking's.
         */
        @Test
        void __() {
            final var booking = mock(Booking.class);
            doReturn("00000F").when(booking).getBookRef();
            final var instance = Ticket.of(booking, "0005432000987");
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getBooking()).isSameAs(booking);
                assertThat(v.getBookRef()).isEqualTo("00000F");
                assertThat(v.getTicketNo()).isEqualTo("0005432000987");
            });
        }
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Ticket#equals(Object) equals} compares only the {@code @Id}, because
     *         every other attribute is mutable state.
     */
    @Override
    public SingleTypeEqualsVerifierApi<Ticket> equals_verifier_() {
        return super.equals_verifier_()
                .suppress(Warning.SURROGATE_KEY,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
