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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTimetable;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Timetable} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Timetable_PersistenceIT
        extends _Entity_PersistenceIT<Timetable> {

    Timetable_PersistenceIT() {
        super(Timetable.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; {@value MappedTimetable#TABLE_NAME} is a view, which PostgreSQL does not let a
     * row be inserted into.
     */
    @Disabled("a view: nothing is inserted into it")
    @Override
    @Test
    void persistTargetInstance() {
        // empty
    }

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The local departure is the departure in the airport's zone, which the view
     *         computes; and the four read-only associations resolve, each to the row of the code
     *         the view carries.
     */
    @Override
    void randomTargetInstanceSelected(final Timetable instance) {
        assertThat(instance.getScheduledDepartureLocal()).as("local departure of %s", instance)
                .isNotNull();
        assertThat(instance.getScheduledDeparture()).as("departure of %s", instance).isNotNull();
        assertThat(instance.getFlight()).as("flight of %s", instance).isNotNull()
                .extracting(Flight::getFlightId).isEqualTo(instance.getFlightId());
        assertThat(instance.getDepartureAirportData()).as("departure of %s", instance).isNotNull()
                .extracting(AirportData::getAirportCode).isEqualTo(instance.getDepartureAirport());
        assertThat(instance.getArrivalAirportData()).as("arrival of %s", instance).isNotNull()
                .extracting(AirportData::getAirportCode).isEqualTo(instance.getArrivalAirport());
        assertThat(instance.getAirplaneData()).as("airplane of %s", instance).isNotNull()
                .extracting(AirplaneData::getAirplaneCode).isEqualTo(instance.getAirplaneCode());
    }
}
