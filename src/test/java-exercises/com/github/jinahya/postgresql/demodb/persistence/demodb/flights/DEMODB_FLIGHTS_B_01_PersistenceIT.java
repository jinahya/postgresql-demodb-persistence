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
 * Answers {@code DEMODB_FLIGHTS_B_01} of {@code src/test/sql-exercises/flights/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_FLIGHTS_B_01: the flights of the route PG0001 in October 2025")
@Slf4j
class DEMODB_FLIGHTS_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT f
                                        FROM Flight f
                                        WHERE f.routeNo = :routeNo
                                          AND f.scheduledDeparture >= :from
                                          AND f.scheduledDeparture < :to
                                        ORDER BY f.scheduledDeparture""",
                                Flight.class
                        )
                        .setParameter("routeNo", "PG0001")
                        .setParameter("from", Instant.parse("2025-10-01T00:00:00Z"))
                        .setParameter("to", Instant.parse("2025-11-01T00:00:00Z"))
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 26 rows; the first ten
        assertThat(list)
                .hasSize(26)
                .allSatisfy(e -> {
                    assertThat(e.getRouteNo()).isEqualTo("PG0001");
                    assertThat(e.getStatus()).isEqualTo(FlightStatus.ARRIVED);
                })
                .extracting(Flight::getFlightId,
                            Flight::getScheduledDeparture,
                            Flight::getScheduledArrival,
                            Flight::getActualDeparture,
                            Flight::getActualArrival)
                .startsWith(
                        tuple(56, Instant.parse("2025-10-01T08:55:00Z"),
                              Instant.parse("2025-10-01T11:00:00Z"),
                              Instant.parse("2025-10-01T09:04:22.242543Z"),
                              Instant.parse("2025-10-01T11:10:08.207796Z")),
                        tuple(420, Instant.parse("2025-10-03T08:55:00Z"),
                              Instant.parse("2025-10-03T11:00:00Z"),
                              Instant.parse("2025-10-03T09:01:01.503634Z"),
                              Instant.parse("2025-10-03T11:06:26.993085Z")),
                        tuple(595, Instant.parse("2025-10-04T08:55:00Z"),
                              Instant.parse("2025-10-04T11:00:00Z"),
                              Instant.parse("2025-10-04T09:01:14.82653Z"),
                              Instant.parse("2025-10-04T11:06:58.248499Z")),
                        tuple(786, Instant.parse("2025-10-05T08:55:00Z"),
                              Instant.parse("2025-10-05T11:00:00Z"),
                              Instant.parse("2025-10-05T09:00:29.243524Z"),
                              Instant.parse("2025-10-05T11:03:09.767793Z")),
                        tuple(962, Instant.parse("2025-10-06T08:55:00Z"),
                              Instant.parse("2025-10-06T11:00:00Z"),
                              Instant.parse("2025-10-06T09:00:22.207094Z"),
                              Instant.parse("2025-10-06T11:05:05.472695Z")),
                        tuple(1145, Instant.parse("2025-10-07T08:55:00Z"),
                              Instant.parse("2025-10-07T11:00:00Z"),
                              Instant.parse("2025-10-07T09:00:29.021862Z"),
                              Instant.parse("2025-10-07T11:05:45.299731Z")),
                        tuple(1319, Instant.parse("2025-10-08T08:55:00Z"),
                              Instant.parse("2025-10-08T11:00:00Z"),
                              Instant.parse("2025-10-08T09:01:37.086295Z"),
                              Instant.parse("2025-10-08T11:07:39.885001Z")),
                        tuple(1682, Instant.parse("2025-10-10T08:55:00Z"),
                              Instant.parse("2025-10-10T11:00:00Z"),
                              Instant.parse("2025-10-10T09:02:00.561019Z"),
                              Instant.parse("2025-10-10T11:06:54.008201Z")),
                        tuple(1858, Instant.parse("2025-10-11T08:55:00Z"),
                              Instant.parse("2025-10-11T11:00:00Z"),
                              Instant.parse("2025-10-11T08:59:42.455704Z"),
                              Instant.parse("2025-10-11T11:04:41.659585Z")),
                        tuple(2049, Instant.parse("2025-10-12T08:55:00Z"),
                              Instant.parse("2025-10-12T11:00:00Z"),
                              Instant.parse("2025-10-12T08:58:44.753122Z"),
                              Instant.parse("2025-10-12T11:04:51.136787Z")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Flight.class);
            final var f = cq.from(Flight.class);
            final var scheduledDeparture = f.get(MappedFlight_.scheduledDeparture);
            cq.select(f)
                    .where(cb.equal(f.get(MappedFlight_.routeNo), "PG0001"),
                           cb.greaterThanOrEqualTo(scheduledDeparture,
                                                   Instant.parse("2025-10-01T00:00:00Z")),
                           cb.lessThan(scheduledDeparture, Instant.parse("2025-11-01T00:00:00Z")))
                    .orderBy(cb.asc(scheduledDeparture));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 26 rows; the first ten
        assertThat(list)
                .hasSize(26)
                .allSatisfy(e -> {
                    assertThat(e.getRouteNo()).isEqualTo("PG0001");
                    assertThat(e.getStatus()).isEqualTo(FlightStatus.ARRIVED);
                })
                .extracting(Flight::getFlightId,
                            Flight::getScheduledDeparture,
                            Flight::getScheduledArrival,
                            Flight::getActualDeparture,
                            Flight::getActualArrival)
                .startsWith(
                        tuple(56, Instant.parse("2025-10-01T08:55:00Z"),
                              Instant.parse("2025-10-01T11:00:00Z"),
                              Instant.parse("2025-10-01T09:04:22.242543Z"),
                              Instant.parse("2025-10-01T11:10:08.207796Z")),
                        tuple(420, Instant.parse("2025-10-03T08:55:00Z"),
                              Instant.parse("2025-10-03T11:00:00Z"),
                              Instant.parse("2025-10-03T09:01:01.503634Z"),
                              Instant.parse("2025-10-03T11:06:26.993085Z")),
                        tuple(595, Instant.parse("2025-10-04T08:55:00Z"),
                              Instant.parse("2025-10-04T11:00:00Z"),
                              Instant.parse("2025-10-04T09:01:14.82653Z"),
                              Instant.parse("2025-10-04T11:06:58.248499Z")),
                        tuple(786, Instant.parse("2025-10-05T08:55:00Z"),
                              Instant.parse("2025-10-05T11:00:00Z"),
                              Instant.parse("2025-10-05T09:00:29.243524Z"),
                              Instant.parse("2025-10-05T11:03:09.767793Z")),
                        tuple(962, Instant.parse("2025-10-06T08:55:00Z"),
                              Instant.parse("2025-10-06T11:00:00Z"),
                              Instant.parse("2025-10-06T09:00:22.207094Z"),
                              Instant.parse("2025-10-06T11:05:05.472695Z")),
                        tuple(1145, Instant.parse("2025-10-07T08:55:00Z"),
                              Instant.parse("2025-10-07T11:00:00Z"),
                              Instant.parse("2025-10-07T09:00:29.021862Z"),
                              Instant.parse("2025-10-07T11:05:45.299731Z")),
                        tuple(1319, Instant.parse("2025-10-08T08:55:00Z"),
                              Instant.parse("2025-10-08T11:00:00Z"),
                              Instant.parse("2025-10-08T09:01:37.086295Z"),
                              Instant.parse("2025-10-08T11:07:39.885001Z")),
                        tuple(1682, Instant.parse("2025-10-10T08:55:00Z"),
                              Instant.parse("2025-10-10T11:00:00Z"),
                              Instant.parse("2025-10-10T09:02:00.561019Z"),
                              Instant.parse("2025-10-10T11:06:54.008201Z")),
                        tuple(1858, Instant.parse("2025-10-11T08:55:00Z"),
                              Instant.parse("2025-10-11T11:00:00Z"),
                              Instant.parse("2025-10-11T08:59:42.455704Z"),
                              Instant.parse("2025-10-11T11:04:41.659585Z")),
                        tuple(2049, Instant.parse("2025-10-12T08:55:00Z"),
                              Instant.parse("2025-10-12T11:00:00Z"),
                              Instant.parse("2025-10-12T08:58:44.753122Z"),
                              Instant.parse("2025-10-12T11:04:51.136787Z")));
    }

}
