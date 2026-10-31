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

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Flight} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Flight_PersistenceIT
        extends _Entity_PersistenceIT<Flight> {

    Flight_PersistenceIT() {
        super(Flight.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityManager  {@inheritDoc}
     * @param entityInstance {@inheritDoc}
     * @implNote The flight, found again, flies the route its persister made for it: one
     *         valid at its scheduled departure, whose duration is its scheduled block time.
     */
    @Override
    void targetInstancePersisted(final EntityManager entityManager, final Flight entityInstance) {
        entityManager.clear();
        final var found = entityManager.find(Flight.class, entityInstance.getFlightId());
        assertThat(found.getRoute()).as("route of %s", found).isNotNull().satisfies(r -> {
            assertThat(r.getRouteNo()).isEqualTo(found.getRouteNo());
            final var validity = r.getValidityRange(List::of);
            assertThat(validity.get(0)).isBeforeOrEqualTo(found.getScheduledDeparture());
            assertThat(validity.get(1)).isAfter(found.getScheduledDeparture());
            assertThat(r.getDuration()).isEqualTo(found.getScheduledBlockTime());
        });
    }

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The scheduled arrival is after the scheduled departure, as the check
     *         constraint requires; and the route, joined by the route number and a formula, is the
     *         one of the route number valid at the scheduled departure, whose duration is the
     *         flight's scheduled block time.
     */
    @Override
    void randomTargetInstanceSelected(final Flight instance) {
        assertThat(instance.getScheduledArrival()).as("scheduled arrival of %s", instance)
                .isAfter(instance.getScheduledDeparture());
        assertThat(instance.getRoute()).as("route of %s", instance).isNotNull().satisfies(r -> {
            assertThat(r.getId().getRouteNo()).isEqualTo(instance.getRouteNo());
            final var validity = r.getId().getValidityRange(List::of);
            final var departure = instance.getScheduledDeparture();
            assertThat(validity.get(0)).isBeforeOrEqualTo(departure);
            assertThat(validity.get(1)).isAfter(departure);
            assertThat(r.getDuration()).isEqualTo(instance.getScheduledBlockTime());
        });
    }
}
