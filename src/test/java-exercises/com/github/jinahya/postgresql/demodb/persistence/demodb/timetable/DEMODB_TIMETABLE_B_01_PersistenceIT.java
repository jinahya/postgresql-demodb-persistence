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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTimetable_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_TIMETABLE_B_01} of {@code src/test/sql-exercises/timetable/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_TIMETABLE_B_01: the flights from Sheremetyevo in the next 24 hours")
@Slf4j
class DEMODB_TIMETABLE_B_01_PersistenceIT {

    // The database's now is bookings.now(), and the 24 hours from it are bound as parameters. The
    // demo database fixes bookings.now(), so the rows do not change with the wall clock.
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var now = __Exercises_TestUtils.now(em);
            return em.createQuery(
                            """
                                    SELECT t
                                    FROM Timetable t
                                    WHERE t.departureAirport = :departureAirport
                                      AND t.scheduledDeparture >= :from
                                      AND t.scheduledDeparture < :to
                                    ORDER BY t.scheduledDeparture, t.flightId""",
                            Timetable.class
                    )
                    .setParameter("departureAirport", "SVO")
                    .setParameter("from", now)
                    .setParameter("to", now.plus(Duration.ofHours(24L)))
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Timetable::getFlightId,
                            Timetable::getRouteNo,
                            Timetable::getArrivalAirport,
                            Timetable::getScheduledDepartureLocal,
                            Timetable::getStatus)
                .containsExactly(
                        tuple(11120, "PG0012", "UFA", LocalDateTime.parse("2025-12-01T13:55:00"),
                              FlightStatus.ON_TIME),
                        tuple(11121, "PG0034", "SVX", LocalDateTime.parse("2025-12-01T14:05:00"),
                              FlightStatus.ON_TIME),
                        tuple(11132, "PG0177", "AER", LocalDateTime.parse("2025-12-01T15:05:00"),
                              FlightStatus.ON_TIME),
                        tuple(11211, "PG0018", "KZN", LocalDateTime.parse("2025-12-02T02:05:00"),
                              FlightStatus.ON_TIME),
                        tuple(11216, "PG0106", "LED", LocalDateTime.parse("2025-12-02T02:40:00"),
                              FlightStatus.ON_TIME));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var now = __Exercises_TestUtils.now(em);
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Timetable.class);
            final var t = cq.from(Timetable.class);
            final var scheduledDeparture = t.get(MappedTimetable_.scheduledDeparture);
            cq.select(t)
                    .where(cb.equal(t.get(MappedTimetable_.departureAirport), "SVO"),
                           cb.greaterThanOrEqualTo(scheduledDeparture, now),
                           cb.lessThan(scheduledDeparture, now.plus(Duration.ofHours(24L))))
                    .orderBy(cb.asc(scheduledDeparture),
                             cb.asc(t.get(MappedTimetable_.flightId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Timetable::getFlightId,
                            Timetable::getRouteNo,
                            Timetable::getArrivalAirport,
                            Timetable::getScheduledDepartureLocal,
                            Timetable::getStatus)
                .containsExactly(
                        tuple(11120, "PG0012", "UFA", LocalDateTime.parse("2025-12-01T13:55:00"),
                              FlightStatus.ON_TIME),
                        tuple(11121, "PG0034", "SVX", LocalDateTime.parse("2025-12-01T14:05:00"),
                              FlightStatus.ON_TIME),
                        tuple(11132, "PG0177", "AER", LocalDateTime.parse("2025-12-01T15:05:00"),
                              FlightStatus.ON_TIME),
                        tuple(11211, "PG0018", "KZN", LocalDateTime.parse("2025-12-02T02:05:00"),
                              FlightStatus.ON_TIME),
                        tuple(11216, "PG0106", "LED", LocalDateTime.parse("2025-12-02T02:40:00"),
                              FlightStatus.ON_TIME));
    }

}
