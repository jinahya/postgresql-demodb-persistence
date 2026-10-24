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

import java.time.DateTimeException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link AirportData} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class AirportData_Persistence_IT
        extends _Entity_Persistence_IT<AirportData> {

    AirportData_Persistence_IT() {
        super(AirportData.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityInstance {@inheritDoc}
     * @implNote The names are JSON, the coordinates a point, and the time zone a zone the
     *         JDK knows.
     */
    @Override
    void randomTargetInstanceSelected(final AirportData entityInstance) {
        assertThat(entityInstance.getAirportName(Locale.ENGLISH)).as("name of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getAirportName(Locale.of("ru"))).as("name of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getCityName(Locale.ENGLISH)).as("city of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getCityName(Locale.of("ru"))).as("city of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getCountryName(Locale.ENGLISH))
                .as("country of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getCountryName(Locale.of("ru")))
                .as("country of %s", entityInstance)
                .isNotBlank();
        assertThat(entityInstance.getCoordinates()).as("coordinates of %s", entityInstance)
                .startsWith("(")
                .endsWith(")");
        {
            final var timeZone = entityInstance.getTimezoneAsTimeZone();
            log.info("entity.timezone: {}, timeZone: {}", entityInstance.getTimezone(), timeZone);
        }
        try {
            final var zoneId = entityInstance.getTimezoneAsZoneId();
            log.info("entity.timezone: {}, zoneId: {}", entityInstance.getTimezone(), zoneId);
        } catch (final DateTimeException dte) {
            log.error("failed to get ZoneId from " + entityInstance.getTimezone(), dte);
        }
    }
}
