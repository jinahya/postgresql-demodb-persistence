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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

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
     * @implNote The name is the English name, no longer JSON.
     */
    @Override
    void randomTargetInstanceSelected(final Airport entityInstance) {
        {
            assertThat(entityInstance.getAirportName()).as("name of %s", entityInstance)
                    .isNotBlank()
                    .doesNotContain("\"en\"");
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class TimezoneTest {

        @Test
        void __TimeZone() {
            final var list = applyEntityManager(
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
