package com.github.jinahya.postgresql.demodb.persistence.demodb.routes;

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

import com.github.jinahya.postgresql.demodb.persistence.Route;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.RouteId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_ROUTES_B_01} of {@code src/test/sql-exercises/routes/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_ROUTES_B_01: the routes from Sheremetyevo")
@Slf4j
class DEMODB_ROUTES_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT r
                                        FROM Route r
                                        WHERE r.departureAirport = :departureAirport
                                        ORDER BY r.id.routeNo, r.id.validity""",
                                Route.class
                        )
                        .setParameter("departureAirport", "SVO")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 34 rows; the first ten
        assertThat(list)
                .hasSize(34)
                .extracting(Route::getRouteNo,
                            e -> e.getValidityRange((from, to) -> from + "/" + to),
                            Route::getDepartureAirport,
                            Route::getArrivalAirport,
                            Route::getAirplaneCode,
                            e -> e.getDaysOfWeekSet().stream().map(DayOfWeek::getValue).toList(),
                            Route::getScheduledTime,
                            Route::getDuration)
                .startsWith(
                        tuple("PG0006", "2025-10-01T00:00:00Z/2025-11-01T00:00:00Z", "SVO", "KJA",
                              "789", List.of(6), LocalTime.of(17, 15), Duration.parse("PT4H30M")),
                        tuple("PG0006", "2025-11-01T00:00:00Z/2025-12-01T00:00:00Z", "SVO", "KJA",
                              "E70", List.of(1, 2, 4, 6), LocalTime.of(17, 15),
                              Duration.parse("PT5H5M")),
                        tuple("PG0006", "2025-12-01T00:00:00Z/2026-01-01T00:00:00Z", "SVO", "KJA",
                              "339", List.of(3), LocalTime.of(17, 15), Duration.parse("PT4H40M")),
                        tuple("PG0006", "2026-01-01T00:00:00Z/2026-02-01T00:00:00Z", "SVO", "KJA",
                              "32N", List.of(4), LocalTime.of(17, 15), Duration.parse("PT4H55M")),
                        tuple("PG0012", "2025-10-01T00:00:00Z/2025-11-01T00:00:00Z", "SVO", "UFA",
                              "351", List.of(2), LocalTime.of(13, 55), Duration.parse("PT1H55M")),
                        tuple("PG0012", "2025-11-01T00:00:00Z/2025-12-01T00:00:00Z", "SVO", "UFA",
                              "E70", List.of(2, 3, 5, 6, 7), LocalTime.of(13, 55),
                              Duration.parse("PT2H5M")),
                        tuple("PG0012", "2025-12-01T00:00:00Z/2026-01-01T00:00:00Z", "SVO", "UFA",
                              "E70", List.of(1, 2, 3, 5, 6, 7), LocalTime.of(13, 55),
                              Duration.parse("PT2H5M")),
                        tuple("PG0012", "2026-01-01T00:00:00Z/2026-02-01T00:00:00Z", "SVO", "UFA",
                              "E70", List.of(1, 2, 3, 4, 5, 6), LocalTime.of(13, 55),
                              Duration.parse("PT2H5M")),
                        tuple("PG0018", "2025-10-01T00:00:00Z/2025-11-01T00:00:00Z", "SVO", "KZN",
                              "339", List.of(3, 7), LocalTime.of(2, 5), Duration.parse("PT1H25M")),
                        tuple("PG0018", "2025-11-01T00:00:00Z/2025-12-01T00:00:00Z", "SVO", "KZN",
                              "7M7", List.of(2, 4, 6, 7), LocalTime.of(2, 5),
                              Duration.parse("PT1H30M")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Route.class);
            final var r = cq.from(Route.class);
            final var id = r.get(MappedRoute_.id);
            cq.select(r)
                    .where(cb.equal(r.get(MappedRoute_.departureAirport), "SVO"))
                    .orderBy(cb.asc(id.get(RouteId_.routeNo)),
                             cb.asc(id.get(RouteId_.validity)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 34 rows; the first ten
        assertThat(list)
                .hasSize(34)
                .extracting(Route::getRouteNo,
                            e -> e.getValidityRange((from, to) -> from + "/" + to),
                            Route::getDepartureAirport,
                            Route::getArrivalAirport,
                            Route::getAirplaneCode,
                            e -> e.getDaysOfWeekSet().stream().map(DayOfWeek::getValue).toList(),
                            Route::getScheduledTime,
                            Route::getDuration)
                .startsWith(
                        tuple("PG0006", "2025-10-01T00:00:00Z/2025-11-01T00:00:00Z", "SVO", "KJA",
                              "789", List.of(6), LocalTime.of(17, 15), Duration.parse("PT4H30M")),
                        tuple("PG0006", "2025-11-01T00:00:00Z/2025-12-01T00:00:00Z", "SVO", "KJA",
                              "E70", List.of(1, 2, 4, 6), LocalTime.of(17, 15),
                              Duration.parse("PT5H5M")),
                        tuple("PG0006", "2025-12-01T00:00:00Z/2026-01-01T00:00:00Z", "SVO", "KJA",
                              "339", List.of(3), LocalTime.of(17, 15), Duration.parse("PT4H40M")),
                        tuple("PG0006", "2026-01-01T00:00:00Z/2026-02-01T00:00:00Z", "SVO", "KJA",
                              "32N", List.of(4), LocalTime.of(17, 15), Duration.parse("PT4H55M")),
                        tuple("PG0012", "2025-10-01T00:00:00Z/2025-11-01T00:00:00Z", "SVO", "UFA",
                              "351", List.of(2), LocalTime.of(13, 55), Duration.parse("PT1H55M")),
                        tuple("PG0012", "2025-11-01T00:00:00Z/2025-12-01T00:00:00Z", "SVO", "UFA",
                              "E70", List.of(2, 3, 5, 6, 7), LocalTime.of(13, 55),
                              Duration.parse("PT2H5M")),
                        tuple("PG0012", "2025-12-01T00:00:00Z/2026-01-01T00:00:00Z", "SVO", "UFA",
                              "E70", List.of(1, 2, 3, 5, 6, 7), LocalTime.of(13, 55),
                              Duration.parse("PT2H5M")),
                        tuple("PG0012", "2026-01-01T00:00:00Z/2026-02-01T00:00:00Z", "SVO", "UFA",
                              "E70", List.of(1, 2, 3, 4, 5, 6), LocalTime.of(13, 55),
                              Duration.parse("PT2H5M")),
                        tuple("PG0018", "2025-10-01T00:00:00Z/2025-11-01T00:00:00Z", "SVO", "KZN",
                              "339", List.of(3, 7), LocalTime.of(2, 5), Duration.parse("PT1H25M")),
                        tuple("PG0018", "2025-11-01T00:00:00Z/2025-12-01T00:00:00Z", "SVO", "KZN",
                              "7M7", List.of(2, 4, 6, 7), LocalTime.of(2, 5),
                              Duration.parse("PT1H30M")));
    }

}
