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

import com.github.jinahya.postgresql.demodb.persistence.mapped.PointAttributeConverter;
import jakarta.persistence.EntityManager;
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
class AirportData_PersistenceIT
        extends _Entity_PersistenceIT<AirportData> {

    AirportData_PersistenceIT() {
        super(AirportData.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityManager  {@inheritDoc}
     * @param entityInstance {@inheritDoc}
     * @implNote The city and the country, each renamed in a language they have, are written
     *         with the rename, and read back with it by the server and by a new find; the
     *         coordinates, written as a point, are read back as they were, by the server and by a
     *         new find.
     */
    @Override
    void targetInstancePersisted(final EntityManager entityManager,
                                 final AirportData entityInstance) {
        final var ru = Locale.of("ru");
        entityInstance.setCityName(Locale.ENGLISH, "city renamed");
        entityInstance.setCountryName(Locale.ENGLISH, "country renamed");
        entityManager.flush();
        final var names = (Object[]) entityManager.createNativeQuery(
                        "SELECT city->>'en', city->>'ru', country->>'en', country->>'ru'"
                        + " FROM airports_data WHERE airport_code = ?1")
                .setParameter(1, entityInstance.getAirportCode())
                .getSingleResult();
        assertThat(names).as("names of %s, as the server reads them", entityInstance)
                .containsExactly("city renamed", entityInstance.getCityName(ru),
                                 "country renamed", entityInstance.getCountryName(ru));
        final var cityRu = entityInstance.getCityName(ru);
        final var countryRu = entityInstance.getCountryName(ru);
        final var coordinates = entityInstance.getCoordinates();
        final var text = (String) entityManager.createNativeQuery(
                        "SELECT coordinates::text FROM airports_data WHERE airport_code = ?1")
                .setParameter(1, entityInstance.getAirportCode())
                .getSingleResult();
        log.info("coordinates: {}, text: {}", coordinates, text);
        assertThat(new PointAttributeConverter()
                           .convertToEntityAttribute(text))
                .as("coordinates of %s, as the server reads them", entityInstance)
                .isEqualTo(coordinates);
        entityManager.clear();
        final var found = entityManager.find(AirportData.class, entityInstance.getAirportCode());
        assertThat(found.getCoordinates())
                .as("coordinates of %s, found", entityInstance)
                .isNotSameAs(coordinates)
                .isEqualTo(coordinates);
        assertThat(found.getCityName(Locale.ENGLISH)).as("city of %s, found", entityInstance)
                .isEqualTo("city renamed");
        assertThat(found.getCityName(ru)).as("city of %s, found", entityInstance)
                .isEqualTo(cityRu);
        assertThat(found.getCountryName(Locale.ENGLISH)).as("country of %s, found", entityInstance)
                .isEqualTo("country renamed");
        assertThat(found.getCountryName(ru)).as("country of %s, found", entityInstance)
                .isEqualTo(countryRu);
    }

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
                .isNotNull()
                .satisfies(p -> {
                    assertThat(p).as("longitude and latitude").hasSize(2);
                    assertThat(p.get(0)).as("longitude").isBetween(-180.0d, 180.0d);
                    assertThat(p.get(1)).as("latitude").isBetween(-90.0d, 90.0d);
                });
        {
            final var timeZone = entityInstance.getTimezoneAsTimeZone();
            log.info("entity.timezone: {}, timeZone: {}", entityInstance.getTimezone(), timeZone);
        }
        try {
            final var zoneId = entityInstance.getTimezone();
            log.info("entity.timezone: {}, zoneId: {}", entityInstance.getTimezone(), zoneId);
        } catch (final DateTimeException dte) {
            log.error("failed to get ZoneId from " + entityInstance.getTimezone(), dte);
        }
    }
}
