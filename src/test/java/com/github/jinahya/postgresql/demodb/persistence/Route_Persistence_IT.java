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

import com.github.jinahya.postgresql.demodb.persistence.converter.IntervalPostgresAttributeConverter;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.jooq.postgres.extensions.converters.OffsetDateTimeRangeConverter;
import org.jooq.postgres.extensions.types.OffsetDateTimeRange;
import org.postgresql.util.PGInterval;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies the mappings of {@link Route} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Route_Persistence_IT
        extends _Entity_Persistence_IT<Route> {

    private static final boolean HIBERNATE = isPresent("org.hibernate.usertype.UserType");

    private static boolean isPresent(final String className) {
        try {
            Class.forName(className, false, Route_Persistence_IT.class.getClassLoader());
            return true;
        } catch (final ClassNotFoundException cnfe) {
            return false;
        }
    }

    private static PGInterval interval(final String text) {
        try {
            return new PGInterval(text);
        } catch (final SQLException sqle) {
            throw new IllegalArgumentException("not an interval: " + text, sqle);
        }
    }

    // reflectively, for the EclipseLink run compiles this class without Hibernate ORM, which
    // PostgreSQLIntervalType's supertypes are of
    private static Duration hypersistenceDuration(final String text) {
        try {
            final var rs = mock(ResultSet.class);
            when(rs.getObject(1)).thenReturn(interval(text));
            final var type = Class.forName(
                    "io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType");
            final var nullSafeGet = Arrays.stream(type.getMethods())
                    .filter(m -> m.getName().equals("nullSafeGet") && m.getParameterCount() == 4)
                    .findFirst().orElseThrow();
            return (Duration) nullSafeGet.invoke(type.getField("INSTANCE").get(null), rs, 1, null,
                                                 null);
        } catch (final ReflectiveOperationException | SQLException e) {
            throw new IllegalStateException("failed to read " + text + " by hypersistence-utils", e);
        }
    }

    private static final DateTimeFormatter HYPERSISTENCE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssx");

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
        assertThat(new IntervalPostgresAttributeConverter.OfString().convertToEntityAttribute(text))
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
                    assertThat(d.months()).as("months").isZero();
                    assertThat(d.days()).as("days").isZero();
                    assertThat(d.microseconds()).as("microseconds").isPositive();
                });
        assertThat(instance.getDepartureAirportData()).as("departure of %s", instance).isNotNull();
        assertThat(instance.getArrivalAirportData()).as("arrival of %s", instance).isNotNull();
        assertThat(instance.getAirplaneData()).as("airplane of %s", instance).isNotNull();
        { // the duration's text, read by hypersistence-utils
            if (HIBERNATE) {
                // PostgreSQLIntervalType reads the driver's PGInterval from a result set, never text, so
                // the text is handed to it as the object the driver would have returned
                final var text = new IntervalPostgresAttributeConverter.OfString()
                        .convertToDatabaseColumn(instance.getDuration());
                final var duration = hypersistenceDuration(text);
                log.info("hypersistence-utils: {} -> {}", text, duration);
                assertThat(duration).as("duration").isPositive();
            } else {
                // PostgreSQLIntervalType is a Hibernate UserType, which the EclipseLink run cannot load
                log.info("hypersistence-utils: skipped, no Hibernate ORM on the class path");
            }
        }
        { // the validity's text, read by hypersistence-utils
            final var validity = Range.zonedDateTimeRange(instance.getValidity());
            log.info("hypersistence-utils: {} -> {} ({})", instance.getValidity(), validity,
                     validity.getClass().getName());
            log.info("  lower: {} ({}, zone {}, instant {}), closed: {}, has: {}",
                     validity.lower(), validity.lower().getClass().getSimpleName(),
                     validity.lower().getZone(), validity.lower().toInstant(),
                     validity.isLowerBoundClosed(), validity.hasLowerBound());
            log.info("  upper: {} ({}, zone {}, instant {}), closed: {}, has: {}",
                     validity.upper(), validity.upper().getClass().getSimpleName(),
                     validity.upper().getZone(), validity.upper().toInstant(),
                     validity.isUpperBoundClosed(), validity.hasUpperBound());
            log.info("  empty: {}, bounded: {}, equal bounds: {}, contains lower: {}, upper: {}",
                     validity.isEmpty(), validity.isBounded(), validity.hasEqualBounds(),
                     validity.contains(validity.lower()), validity.contains(validity.upper()));
            final var utc = Range.zonedDateTimeRange(
                    "[\"" + validity.lower().withZoneSameInstant(ZoneOffset.UTC)
                            .format(HYPERSISTENCE_TIMESTAMP) + "\",\""
                    + validity.upper().withZoneSameInstant(ZoneOffset.UTC)
                            .format(HYPERSISTENCE_TIMESTAMP) + "\")");
            log.info("  the same range in UTC: {}, equal: {}, its text: {}", utc,
                     validity.equals(utc), validity.asString());
            assertThat(validity.isEmpty()).as("empty").isFalse();
            assertThat(validity.hasLowerBound()).as("lower bound").isTrue();
            assertThat(validity.hasUpperBound()).as("upper bound").isTrue();
            assertThat(validity.isLowerBoundClosed()).as("lower inclusive").isTrue();
            assertThat(validity.isUpperBoundClosed()).as("upper inclusive").isFalse();
            assertThat(validity.lower()).as("lower").isBefore(validity.upper());
        }
        { // the validity's text, read by jooq-postgres-extensions
            final var converter = new OffsetDateTimeRangeConverter();
            final var validity = converter.from(instance.getValidity());
            log.info("jooq-postgres-extensions: {} -> {} ({})", instance.getValidity(), validity,
                     validity.getClass().getName());
            log.info("  lower: {} ({}, offset {}, instant {}), including: {}", validity.lower(),
                     validity.lower().getClass().getSimpleName(), validity.lower().getOffset(),
                     validity.lower().toInstant(), validity.lowerIncluding());
            log.info("  upper: {} ({}, offset {}, instant {}), including: {}", validity.upper(),
                     validity.upper().getClass().getSimpleName(), validity.upper().getOffset(),
                     validity.upper().toInstant(), validity.upperIncluding());
            log.info("  empty: {}", validity.isEmpty());
            final var utc = OffsetDateTimeRange.offsetDateTimeRange(
                    validity.lower().withOffsetSameInstant(ZoneOffset.UTC),
                    validity.upper().withOffsetSameInstant(ZoneOffset.UTC));
            log.info("  the same range in UTC: {}, equal: {}, written back: {}", utc,
                     validity.equals(utc), converter.to(validity));
            assertThat(validity.isEmpty()).as("empty").isFalse();
            assertThat(validity.lower()).as("lower").isNotNull();
            assertThat(validity.upper()).as("upper").isNotNull();
            assertThat(validity.lowerIncluding()).as("lower inclusive").isTrue();
            assertThat(validity.upperIncluding()).as("upper inclusive").isFalse();
            assertThat(validity.lower()).as("lower").isBefore(validity.upper());
        }
    }
}
