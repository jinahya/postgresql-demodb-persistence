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
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_ROUTES_I_01} of {@code src/test/sql-exercises/routes/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_ROUTES_I_01: the periods of the route PG0001")
@Slf4j
class DEMODB_ROUTES_I_01_PersistenceIT {

    // LOWER(validity) and UPPER(validity) are the two ends of the range, which the id maps to a
    // pair of instants; the query selects the route, and the test reads the two ends from it
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT r
                                        FROM Route r
                                        WHERE r.id.routeNo = :routeNo
                                        ORDER BY r.id.validity""",
                                Route.class
                        )
                        .setParameter("routeNo", "PG0001")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Route::getRouteNo,
                            e -> e.getValidityRange((from, to) -> from),
                            e -> e.getValidityRange((from, to) -> to),
                            Route::getAirplaneCode,
                            e -> e.getDaysOfWeekSet().stream().map(DayOfWeek::getValue).toList())
                .containsExactly(
                        tuple("PG0001", Instant.parse("2025-10-01T00:00:00Z"),
                              Instant.parse("2025-11-01T00:00:00Z"), "339",
                              List.of(1, 2, 3, 5, 6, 7)),
                        tuple("PG0001", Instant.parse("2025-11-01T00:00:00Z"),
                              Instant.parse("2025-12-01T00:00:00Z"), "789",
                              List.of(1, 2, 3, 5, 6, 7)),
                        tuple("PG0001", Instant.parse("2025-12-01T00:00:00Z"),
                              Instant.parse("2026-01-01T00:00:00Z"), "789",
                              List.of(2, 3, 4, 5, 6, 7)),
                        tuple("PG0001", Instant.parse("2026-01-01T00:00:00Z"),
                              Instant.parse("2026-02-01T00:00:00Z"), "77W",
                              List.of(2, 4, 6, 7)));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Route.class);
            final var r = cq.from(Route.class);
            final var id = r.get(MappedRoute_.id);
            cq.select(r)
                    .where(cb.equal(id.get(RouteId_.routeNo), "PG0001"))
                    .orderBy(cb.asc(id.get(RouteId_.validity)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Route::getRouteNo,
                            e -> e.getValidityRange((from, to) -> from),
                            e -> e.getValidityRange((from, to) -> to),
                            Route::getAirplaneCode,
                            e -> e.getDaysOfWeekSet().stream().map(DayOfWeek::getValue).toList())
                .containsExactly(
                        tuple("PG0001", Instant.parse("2025-10-01T00:00:00Z"),
                              Instant.parse("2025-11-01T00:00:00Z"), "339",
                              List.of(1, 2, 3, 5, 6, 7)),
                        tuple("PG0001", Instant.parse("2025-11-01T00:00:00Z"),
                              Instant.parse("2025-12-01T00:00:00Z"), "789",
                              List.of(1, 2, 3, 5, 6, 7)),
                        tuple("PG0001", Instant.parse("2025-12-01T00:00:00Z"),
                              Instant.parse("2026-01-01T00:00:00Z"), "789",
                              List.of(2, 3, 4, 5, 6, 7)),
                        tuple("PG0001", Instant.parse("2026-01-01T00:00:00Z"),
                              Instant.parse("2026-02-01T00:00:00Z"), "77W",
                              List.of(2, 4, 6, 7)));
    }

}
