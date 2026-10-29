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

import com.github.jinahya.persistence.util.JinahyaEntityManagerUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.util.TimeZone;

import static com.github.jinahya.persistence.util.JinahyaPersistenceUtils.applyEntityManager;
import static com.github.jinahya.persistence.util.JinahyaEntityManagerUtils.applyInTransaction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Verifies the mappings of {@link Airport} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Airport_Persistence_IT
        extends _Entity_Persistence_IT<Airport> {

    Airport_Persistence_IT() {
        super(Airport.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; {@value MappedAirport#TABLE_NAME} is a view, which PostgreSQL does not let a
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
     * @param entityInstance {@inheritDoc}
     * @implNote The name is the name in the session's language, no longer JSON, and the
     *         association reaches the airport data of the code.
     */
    @Override
    void randomTargetInstanceSelected(final Airport entityInstance) {
        {
            assertThat(entityInstance.getAirportName()).as("name of %s", entityInstance)
                    .isNotBlank()
                    .doesNotContain("\"en\"");
        }
        assertThat(entityInstance.getAirportData()).as("airport data of %s", entityInstance)
                .isNotNull()
                .extracting(AirportData::getAirportCode)
                .isEqualTo(entityInstance.getAirportCode());
    }

    /**
     * Selects every instance with its {@link Airport#getAirportData() airport data} fetched by the
     * same query, and checks that the fetch join, an inner join, loses no row of the view.
     *
     * @implNote The number of instances is counted with
     *         {@link JinahyaEntityManagerUtils#selectEntityCount(jakarta.persistence.EntityManager,
     *         Class)}, of the view and of the table alike; the view reads every row of the table,
     *         so the three numbers are the same.
     */
    @DisplayName("a fetch join loads the airport data with every airport")
    @Test
    void selectTargetInstances_FetchAirportData() {
        applyEntityManager(PERSISTENCE_UNIT_NAME, m -> applyInTransaction(m, em -> {
            final var count = JinahyaEntityManagerUtils.selectEntityCount(em, Airport.class);
            assumeTrue(count > 0L, () -> "no row of " + Airport.class + " is installed");
            assertThat(JinahyaEntityManagerUtils.selectEntityCount(em, AirportData.class))
                    .as("number of airport data")
                    .isEqualTo(count);
            final var builder = em.getCriteriaBuilder();
            final var query = builder.createQuery(Airport.class);
            final var root = query.from(Airport.class);
            root.fetch(Airport_.airportData);
            query.select(root);
            final var airports = em.createQuery(query).getResultList();
            assertThat(airports).as("airports, with their airport data fetched")
                    .hasSize(Math.toIntExact(count));
            final var util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            airports.forEach(airport -> {
                assertThat(util.isLoaded(airport, Airport_.AIRPORT_DATA))
                        .as("airport data of %s, loaded", airport)
                        .isTrue();
                assertThat(airport.getAirportData()).as("airport data of %s", airport)
                        .isNotNull()
                        .extracting(AirportData::getAirportCode)
                        .isEqualTo(airport.getAirportCode());
            });
            return null;
        }, true));
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class TimezoneTest {

        @Test
        void __TimeZone() {
            final var list = applyEntityManager(
                    PERSISTENCE_UNIT_NAME,
                    em -> em.createQuery("SELECT e FROM AirportData AS e", AirportData.class)
                            .getResultList()
            );
            for (final var entityInstance : list) {
                final var timezone = TimeZone.getTimeZone(entityInstance.getTimezone());
                log.info("entity.timezone: {}, timeZone: {}", entityInstance.getTimezone(),
                         timezone);
            }
        }

        @Test
        void __ZoneId() {
            final var list = applyEntityManager(
                    PERSISTENCE_UNIT_NAME,
                    em -> em.createQuery("SELECT e FROM AirportData AS e", AirportData.class)
                            .getResultList()
            );
            for (final var entityInstance : list) {
                try {
                    final var zoneId = entityInstance.getTimezone();
                    log.info("entity.timezone: {}, zoneId: {}", entityInstance.getTimezone(),
                             zoneId);
                } catch (final DateTimeException dte) {
                    log.error("failed to get ZoneId from " + entityInstance.getTimezone(), dte);
                }
            }
        }
    }
}
