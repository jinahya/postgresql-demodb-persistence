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

import com.github.jinahya.postgresql.demodb.persistence.converter.PGIntervalConverter;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Route} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Route_Persistence_IT
        extends _Entity_Persistence_IT<Route> {

    Route_Persistence_IT() {
        super(Route.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityManager  {@inheritDoc}
     * @param entityInstance {@inheritDoc}
     * @implNote The duration, written as an interval, is read back as it was, by the server
     *         and by a new find.
     */
    @Override
    void targetInstancePersisted(final EntityManager entityManager, final Route entityInstance) {
        final var duration = entityInstance.getDuration();
        final var text = (String) entityManager.createNativeQuery(
                        "SELECT duration::text FROM routes"
                        + " WHERE route_no = ?1 AND validity = CAST(?2 AS tstzrange)")
                .setParameter(1, entityInstance.getRouteNo())
                .setParameter(2, entityInstance.getValidity())
                .getSingleResult();
        log.info("duration: {}, text: {}", duration, text);
        assertThat(new PGIntervalConverter.OfPostgres().convertToEntityAttribute(text))
                .as("duration of %s, as the server reads it", entityInstance)
                .isEqualTo(duration);
        entityManager.clear();
        final var found = entityManager.find(Route.class, entityInstance.getId());
        assertThat(found.getDuration())
                .as("duration of %s, found", entityInstance)
                .isNotSameAs(duration)
                .isEqualTo(duration);
    }

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The identifier agrees with the two attributes it reads a second time, the
     *         days of the week are within the week, and the three lazy associations resolve.
     */
    @Override
    void randomTargetInstanceSelected(final Route instance) {
        assertThat(instance.getId()).as("id of %s", instance).isNotNull().satisfies(id -> {
            assertThat(id.getRouteNo()).isEqualTo(instance.getRouteNo());
            assertThat(id.getValidity()).isEqualTo(instance.getValidity());
        });
        assertThat(instance.getDaysOfWeek()).as("days of week of %s", instance).isNotEmpty()
                .allSatisfy(d -> assertThat(d).isBetween(1, 7));
        assertThat(instance.getDuration()).as("duration of %s", instance).isNotNull()
                .satisfies(d -> {
                    assertThat(d.getYears()).as("years").isZero();
                    assertThat(d.getMonths()).as("months").isZero();
                    assertThat(d.getHours() * 60 + d.getMinutes()).as("minutes").isPositive();
                });
        assertThat(instance.getDepartureAirportData()).as("departure of %s", instance).isNotNull();
        assertThat(instance.getArrivalAirportData()).as("arrival of %s", instance).isNotNull();
        assertThat(instance.getAirplaneData()).as("airplane of %s", instance).isNotNull();
    }
}
