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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRouteId;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.jooq.postgres.extensions.converters.OffsetDateTimeRangeConverter;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Route} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Route_Persistence_IT
        extends _Entity_Persistence_IT<Route> {

    // the bounds of a validity, as a list to assert on
    private static final BiFunction<Instant, Instant, List<Instant>> BOUNDS = List::of;

    Route_Persistence_IT() {
        super(Route.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityManager  {@inheritDoc}
     * @param entityInstance {@inheritDoc}
     * @implNote The duration, written as an interval, is read back as it was, by the
     *         server, as its number of seconds, and by a new find; the validity, written as a
     *         range, is read back as it was, from the server's text, by the converter and by
     *         jooq-postgres-extensions alike, and by a new find, by its identifier.
     */
    @Override
    void targetInstancePersisted(final EntityManager entityManager, final Route entityInstance) {
        final var validity = new MappedRouteId.ValidityConverter()
                .convertToDatabaseColumn(entityInstance.getValidityRange(
                        (l, u) -> new Instant[]{l, u}));
        final var duration = entityInstance.getDuration();
        final var seconds = (Number) entityManager.createNamedQuery(
                        "selectDurationSecondsByRouteNoAndValidity")
                .setParameter(1, entityInstance.getRouteNo())
                .setParameter(2, validity)
                .getSingleResult();
        log.info("duration: {}, seconds: {}", duration, seconds);
        assertThat(seconds.longValue())
                .as("duration of %s, as the server reads it", entityInstance)
                .isEqualTo(duration.toSeconds());
        final var text = (String) entityManager.createNativeQuery(
                        "SELECT CAST(validity AS text) FROM routes"
                        + " WHERE route_no = ?1 AND validity = CAST(?2 AS tstzrange)")
                .setParameter(1, entityInstance.getRouteNo())
                .setParameter(2, validity)
                .getSingleResult();
        log.info("validity: {}, written: {}, the server's text: {}",
                 entityInstance.getValidityRange(BOUNDS), validity, text);
        assertThat(List.of(new MappedRouteId.ValidityConverter().convertToEntityAttribute(text)))
                .as("validity of %s, from the server's text, by the converter", entityInstance)
                .isEqualTo(entityInstance.getValidityRange(BOUNDS));
        final var range = new OffsetDateTimeRangeConverter().from(text);
        assertThat(range.lowerIncluding()).as("lower inclusive").isTrue();
        assertThat(range.upperIncluding()).as("upper inclusive").isFalse();
        assertThat(List.of(range.lower().toInstant(), range.upper().toInstant()))
                .as("validity of %s, from the server's text, by jooq-postgres-extensions",
                    entityInstance)
                .isEqualTo(entityInstance.getValidityRange(BOUNDS));
        entityManager.clear();
        final var found = entityManager.find(Route.class, entityInstance.getId());
        assertThat(found).as("%s, found by its identifier", entityInstance)
                .isNotNull()
                .isNotSameAs(entityInstance)
                .isEqualTo(entityInstance);
        assertThat(found.getValidityRange(BOUNDS))
                .as("validity of %s, found", entityInstance)
                .isEqualTo(entityInstance.getValidityRange(BOUNDS));
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
            assertThat(id.getValidityRange(BOUNDS)).isEqualTo(instance.getValidityRange(BOUNDS));
        });
        // DayOfWeek.of, in getDaysOfWeekSet(), throws for a number other than 1 to 7
        assertThat(instance.getDaysOfWeekSet()).as("days of week of %s", instance).isNotEmpty();
        assertThat(instance.getDuration()).as("duration of %s", instance).isNotNull()
                .isPositive()
                .isLessThan(Duration.ofDays(1));
        assertThat(instance.getDepartureAirportData()).as("departure of %s", instance).isNotNull();
        assertThat(instance.getArrivalAirportData()).as("arrival of %s", instance).isNotNull();
        assertThat(instance.getAirplaneData()).as("airplane of %s", instance).isNotNull();
        { // the validity, the bounds of [lower,upper)
            final var validity = instance.getValidityRange(BOUNDS);
            log.info("validity: {}", validity);
            assertThat(validity).as("validity of %s", instance).hasSize(2).doesNotContainNull();
            assertThat(validity.get(0)).as("lower").isBefore(validity.get(1));
        }
    }
}
