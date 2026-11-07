package com.github.jinahya.postgresql.demodb.persistence.demodb.flights;

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

import com.github.jinahya.postgresql.demodb.persistence.Flight;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_FLIGHTS_B_05} of {@code src/test/sql-exercises/flights/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_FLIGHTS_B_05: the flights in the air")
@Slf4j
class DEMODB_FLIGHTS_B_05_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT f
                                        FROM Flight f
                                        WHERE f.actualDeparture IS NOT NULL
                                          AND f.actualArrival IS NULL
                                        ORDER BY f.actualDeparture, f.flightId""",
                                Flight.class
                        )
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 20 rows, every one Departed; the first ten
        assertThat(list)
                .hasSize(20)
                .allSatisfy(e -> assertThat(e.getStatus()).isEqualTo(FlightStatus.DEPARTED))
                .extracting(Flight::getFlightId,
                            Flight::getRouteNo,
                            Flight::getScheduledDeparture,
                            Flight::getActualDeparture,
                            Flight::getScheduledArrival)
                .startsWith(
                        tuple(10976, "PG0328", Instant.parse("2025-11-30T13:45:00Z"),
                              Instant.parse("2025-11-30T13:47:41.516904Z"),
                              Instant.parse("2025-12-01T01:35:00Z")),
                        tuple(10984, "PG0384", Instant.parse("2025-11-30T14:10:00Z"),
                              Instant.parse("2025-11-30T14:14:54.688712Z"),
                              Instant.parse("2025-12-01T00:15:00Z")),
                        tuple(10966, "PG0292", Instant.parse("2025-11-30T12:50:00Z"),
                              Instant.parse("2025-11-30T15:47:11.461764Z"),
                              Instant.parse("2025-12-01T03:25:00Z")),
                        tuple(11024, "PG0381", Instant.parse("2025-11-30T20:25:00Z"),
                              Instant.parse("2025-11-30T20:29:50.700992Z"),
                              Instant.parse("2025-12-01T00:10:00Z")),
                        tuple(11025, "PG0211", Instant.parse("2025-11-30T20:45:00Z"),
                              Instant.parse("2025-11-30T20:49:46.926534Z"),
                              Instant.parse("2025-12-01T01:40:00Z")),
                        tuple(11027, "PG0290", Instant.parse("2025-11-30T21:35:00Z"),
                              Instant.parse("2025-11-30T21:40:56.704528Z"),
                              Instant.parse("2025-12-01T03:30:00Z")),
                        tuple(11031, "PG0301", Instant.parse("2025-11-30T22:05:00Z"),
                              Instant.parse("2025-11-30T22:11:44.385829Z"),
                              Instant.parse("2025-12-01T01:20:00Z")),
                        tuple(11033, "PG0043", Instant.parse("2025-11-30T22:35:00Z"),
                              Instant.parse("2025-11-30T22:42:45.411075Z"),
                              Instant.parse("2025-12-01T03:00:00Z")),
                        tuple(11037, "PG0406", Instant.parse("2025-11-30T23:10:00Z"),
                              Instant.parse("2025-11-30T23:16:07.884398Z"),
                              Instant.parse("2025-12-01T00:25:00Z")),
                        tuple(11036, "PG0011", Instant.parse("2025-11-30T23:10:00Z"),
                              Instant.parse("2025-11-30T23:16:44.924251Z"),
                              Instant.parse("2025-12-01T01:15:00Z")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Flight.class);
            final var f = cq.from(Flight.class);
            cq.select(f)
                    .where(cb.isNotNull(f.get(MappedFlight_.actualDeparture)),
                           cb.isNull(f.get(MappedFlight_.actualArrival)))
                    .orderBy(cb.asc(f.get(MappedFlight_.actualDeparture)),
                             cb.asc(f.get(MappedFlight_.flightId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 20 rows, every one Departed; the first ten
        assertThat(list)
                .hasSize(20)
                .allSatisfy(e -> assertThat(e.getStatus()).isEqualTo(FlightStatus.DEPARTED))
                .extracting(Flight::getFlightId,
                            Flight::getRouteNo,
                            Flight::getScheduledDeparture,
                            Flight::getActualDeparture,
                            Flight::getScheduledArrival)
                .startsWith(
                        tuple(10976, "PG0328", Instant.parse("2025-11-30T13:45:00Z"),
                              Instant.parse("2025-11-30T13:47:41.516904Z"),
                              Instant.parse("2025-12-01T01:35:00Z")),
                        tuple(10984, "PG0384", Instant.parse("2025-11-30T14:10:00Z"),
                              Instant.parse("2025-11-30T14:14:54.688712Z"),
                              Instant.parse("2025-12-01T00:15:00Z")),
                        tuple(10966, "PG0292", Instant.parse("2025-11-30T12:50:00Z"),
                              Instant.parse("2025-11-30T15:47:11.461764Z"),
                              Instant.parse("2025-12-01T03:25:00Z")),
                        tuple(11024, "PG0381", Instant.parse("2025-11-30T20:25:00Z"),
                              Instant.parse("2025-11-30T20:29:50.700992Z"),
                              Instant.parse("2025-12-01T00:10:00Z")),
                        tuple(11025, "PG0211", Instant.parse("2025-11-30T20:45:00Z"),
                              Instant.parse("2025-11-30T20:49:46.926534Z"),
                              Instant.parse("2025-12-01T01:40:00Z")),
                        tuple(11027, "PG0290", Instant.parse("2025-11-30T21:35:00Z"),
                              Instant.parse("2025-11-30T21:40:56.704528Z"),
                              Instant.parse("2025-12-01T03:30:00Z")),
                        tuple(11031, "PG0301", Instant.parse("2025-11-30T22:05:00Z"),
                              Instant.parse("2025-11-30T22:11:44.385829Z"),
                              Instant.parse("2025-12-01T01:20:00Z")),
                        tuple(11033, "PG0043", Instant.parse("2025-11-30T22:35:00Z"),
                              Instant.parse("2025-11-30T22:42:45.411075Z"),
                              Instant.parse("2025-12-01T03:00:00Z")),
                        tuple(11037, "PG0406", Instant.parse("2025-11-30T23:10:00Z"),
                              Instant.parse("2025-11-30T23:16:07.884398Z"),
                              Instant.parse("2025-12-01T00:25:00Z")),
                        tuple(11036, "PG0011", Instant.parse("2025-11-30T23:10:00Z"),
                              Instant.parse("2025-11-30T23:16:44.924251Z"),
                              Instant.parse("2025-12-01T01:15:00Z")));
    }

}
