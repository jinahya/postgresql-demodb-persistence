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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Segment} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Segment_Persistence_IT
        extends _Entity_Persistence_IT<Segment> {

    Segment_Persistence_IT() {
        super(Segment.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The ticket and the flight, lazy associations over the identifier, resolve
     *         to the ones of the identifier.
     */
    @Override
    void randomTargetInstanceSelected(final Segment instance) {
        assertThat(instance.getTicket()).as("ticket of %s", instance).isNotNull()
                .extracting(Ticket::getTicketNo).isEqualTo(instance.getTicketNo());
        assertThat(instance.getFlight()).as("flight of %s", instance).isNotNull()
                .extracting(Flight::getFlightId).isEqualTo(instance.getFlightId());
    }
}
