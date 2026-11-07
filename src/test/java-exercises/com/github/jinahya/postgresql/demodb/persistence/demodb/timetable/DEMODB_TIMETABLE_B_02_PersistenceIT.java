package com.github.jinahya.postgresql.demodb.persistence.demodb.timetable;

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

import com.github.jinahya.postgresql.demodb.persistence.Timetable;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTimetable_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_TIMETABLE_B_02} of {@code src/test/sql-exercises/timetable/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName(
        "DEMODB_TIMETABLE_B_02: the flights of the route PG0078 on the first day, in local time")
@Slf4j
class DEMODB_TIMETABLE_B_02_PersistenceIT {

    // the scheduled departure, a timestamptz, is mapped to an Instant, which has no time zone; the
    // SQL's AT TIME ZONE 'UTC' only shows it in UTC
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT t
                                        FROM Timetable t
                                        WHERE t.routeNo = :routeNo
                                          AND t.scheduledDeparture >= :from
                                          AND t.scheduledDeparture < :to
                                        ORDER BY t.scheduledDeparture""",
                                Timetable.class
                        )
                        .setParameter("routeNo", "PG0078")
                        .setParameter("from", Instant.parse("2025-10-01T00:00:00Z"))
                        .setParameter("to", Instant.parse("2025-10-02T00:00:00Z"))
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Timetable::getFlightId,
                            Timetable::getDepartureAirport,
                            Timetable::getArrivalAirport,
                            Timetable::getScheduledDeparture,
                            Timetable::getScheduledDepartureLocal,
                            Timetable::getScheduledArrivalLocal)
                .containsExactly(
                        tuple(1, "SVO", "HEL", Instant.parse("2025-10-01T00:00:00Z"),
                              LocalDateTime.parse("2025-10-01T03:00:00"),
                              LocalDateTime.parse("2025-10-01T04:40:00")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Timetable.class);
            final var t = cq.from(Timetable.class);
            final var scheduledDeparture = t.get(MappedTimetable_.scheduledDeparture);
            cq.select(t)
                    .where(cb.equal(t.get(MappedTimetable_.routeNo), "PG0078"),
                           cb.greaterThanOrEqualTo(scheduledDeparture,
                                                   Instant.parse("2025-10-01T00:00:00Z")),
                           cb.lessThan(scheduledDeparture, Instant.parse("2025-10-02T00:00:00Z")))
                    .orderBy(cb.asc(scheduledDeparture));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Timetable::getFlightId,
                            Timetable::getDepartureAirport,
                            Timetable::getArrivalAirport,
                            Timetable::getScheduledDeparture,
                            Timetable::getScheduledDepartureLocal,
                            Timetable::getScheduledArrivalLocal)
                .containsExactly(
                        tuple(1, "SVO", "HEL", Instant.parse("2025-10-01T00:00:00Z"),
                              LocalDateTime.parse("2025-10-01T03:00:00"),
                              LocalDateTime.parse("2025-10-01T04:40:00")));
    }

}
