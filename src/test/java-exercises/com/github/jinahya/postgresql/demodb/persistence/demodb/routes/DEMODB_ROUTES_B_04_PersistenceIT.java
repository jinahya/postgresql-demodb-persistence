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

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_ROUTES_B_04} of {@code src/test/sql-exercises/routes/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_ROUTES_B_04: the routes longer than ten hours")
@Slf4j
class DEMODB_ROUTES_B_04_PersistenceIT {

    // the duration, an interval, is mapped to a Duration, which binds as an interval of seconds
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT r
                                        FROM Route r
                                        WHERE r.duration > :duration
                                        ORDER BY r.duration DESC, r.id.routeNo, r.id.validity""",
                                Route.class
                        )
                        .setParameter("duration", Duration.ofHours(10L))
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 80 rows; the first ten
        assertThat(list)
                .hasSize(80)
                .extracting(Route::getRouteNo,
                            Route::getDepartureAirport,
                            Route::getArrivalAirport,
                            Route::getDuration)
                .startsWith(
                        tuple("PG0129", "SCL", "BOM", Duration.parse("PT19H55M")),
                        tuple("PG0130", "BOM", "SCL", Duration.parse("PT19H55M")),
                        tuple("PG0143", "MCO", "MAA", Duration.parse("PT18H35M")),
                        tuple("PG0144", "MAA", "MCO", Duration.parse("PT18H35M")),
                        tuple("PG0569", "SZX", "MCO", Duration.parse("PT17H30M")),
                        tuple("PG0570", "MCO", "SZX", Duration.parse("PT17H30M")),
                        tuple("PG0615", "AKL", "CLT", Duration.parse("PT16H45M")),
                        tuple("PG0616", "CLT", "AKL", Duration.parse("PT16H45M")),
                        tuple("PG0635", "MIA", "DEL", Duration.parse("PT16H45M")),
                        tuple("PG0636", "DEL", "MIA", Duration.parse("PT16H45M")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Route.class);
            final var r = cq.from(Route.class);
            final var id = r.get(MappedRoute_.id);
            cq.select(r)
                    .where(cb.greaterThan(r.get(MappedRoute_.duration), Duration.ofHours(10L)))
                    .orderBy(cb.desc(r.get(MappedRoute_.duration)),
                             cb.asc(id.get(RouteId_.routeNo)),
                             cb.asc(id.get(RouteId_.validity)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 80 rows; the first ten
        assertThat(list)
                .hasSize(80)
                .extracting(Route::getRouteNo,
                            Route::getDepartureAirport,
                            Route::getArrivalAirport,
                            Route::getDuration)
                .startsWith(
                        tuple("PG0129", "SCL", "BOM", Duration.parse("PT19H55M")),
                        tuple("PG0130", "BOM", "SCL", Duration.parse("PT19H55M")),
                        tuple("PG0143", "MCO", "MAA", Duration.parse("PT18H35M")),
                        tuple("PG0144", "MAA", "MCO", Duration.parse("PT18H35M")),
                        tuple("PG0569", "SZX", "MCO", Duration.parse("PT17H30M")),
                        tuple("PG0570", "MCO", "SZX", Duration.parse("PT17H30M")),
                        tuple("PG0615", "AKL", "CLT", Duration.parse("PT16H45M")),
                        tuple("PG0616", "CLT", "AKL", Duration.parse("PT16H45M")),
                        tuple("PG0635", "MIA", "DEL", Duration.parse("PT16H45M")),
                        tuple("PG0636", "DEL", "MIA", Duration.parse("PT16H45M")));
    }

}
