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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_TIMETABLE_A_02} of {@code src/test/sql-exercises/timetable/03_advanced.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_TIMETABLE_A_02: the connections at Sheremetyevo on the first day")
@Slf4j
class DEMODB_TIMETABLE_A_02_PersistenceIT {

    // Jakarta Persistence has no arithmetic on a date or a time; the window and the layover use
    // Hibernate ORM's, a duration added to an instant (+ 1 hour) and the difference of two instants
    // (a Duration), in its query language and its HibernateCriteriaBuilder
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a, d, d.scheduledDeparture - a.scheduledArrival
                                        FROM Timetable a
                                                 JOIN Timetable d ON d.departureAirport = a.arrivalAirport
                                            AND d.scheduledDeparture BETWEEN a.scheduledArrival + 1 hour
                                                AND a.scheduledArrival + 4 hour
                                        WHERE a.arrivalAirport = :airport
                                          AND a.scheduledArrivalLocal >= :from
                                          AND a.scheduledArrivalLocal < :to
                                        ORDER BY a.flightId, d.flightId""",
                                Object[].class
                        )
                        .setParameter("airport", "SVO")
                        .setParameter("from", LocalDateTime.parse("2025-10-01T00:00:00"))
                        .setParameter("to", LocalDateTime.parse("2025-10-02T00:00:00"))
                        .setMaxResults(10)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Timetable) r[0]).getFlightId(),
                            r -> ((Timetable) r[1]).getFlightId(),
                            r -> ((Timetable) r[1]).getArrivalAirport(),
                            r -> r[2])
                .containsExactly(
                        tuple(54, 81, "AER", Duration.parse("PT2H30M")),
                        tuple(131, 168, "LED", Duration.parse("PT3H35M")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb =
                    (org.hibernate.query.criteria.HibernateCriteriaBuilder) em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(Timetable.class);
            final var d = a.join(Timetable.class);
            final var arrival = a.get(MappedTimetable_.scheduledArrival);
            final var departure = d.get(MappedTimetable_.scheduledDeparture);
            d.on(cb.equal(d.get(MappedTimetable_.departureAirport),
                          a.get(MappedTimetable_.arrivalAirport)),
                 cb.between(departure,
                            cb.addDuration(arrival, Duration.ofHours(1L)),
                            cb.addDuration(arrival, Duration.ofHours(4L))));
            final var arrivalLocal = a.get(MappedTimetable_.scheduledArrivalLocal);
            // durationBetween(x, y) is x - y
            cq.select(cb.array(a, d, cb.durationBetween(departure, arrival)))
                    .where(cb.equal(a.get(MappedTimetable_.arrivalAirport), "SVO"),
                           cb.greaterThanOrEqualTo(arrivalLocal,
                                                   LocalDateTime.parse("2025-10-01T00:00:00")),
                           cb.lessThan(arrivalLocal, LocalDateTime.parse("2025-10-02T00:00:00")))
                    .orderBy(cb.asc(a.get(MappedTimetable_.flightId)),
                             cb.asc(d.get(MappedTimetable_.flightId)));
            return em.createQuery(cq)
                    .setMaxResults(10)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Timetable) r[0]).getFlightId(),
                            r -> ((Timetable) r[1]).getFlightId(),
                            r -> ((Timetable) r[1]).getArrivalAirport(),
                            r -> r[2])
                .containsExactly(
                        tuple(54, 81, "AER", Duration.parse("PT2H30M")),
                        tuple(131, 168, "LED", Duration.parse("PT3H35M")));
    }

}
