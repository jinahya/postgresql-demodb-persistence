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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_ROUTES_I_02} of {@code src/test/sql-exercises/routes/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_ROUTES_I_02: the five route numbers with the most periods")
@Slf4j
class DEMODB_ROUTES_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT r.id.routeNo, COUNT(r)
                                        FROM Route r
                                        GROUP BY r.id.routeNo
                                        ORDER BY COUNT(r) DESC, r.id.routeNo""",
                                Object[].class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("PG0001", 4L),
                        tuple("PG0002", 4L),
                        tuple("PG0005", 4L),
                        tuple("PG0006", 4L),
                        tuple("PG0007", 4L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var r = cq.from(Route.class);
            final var routeNo = r.get(MappedRoute_.id).get(RouteId_.routeNo);
            cq.select(cb.array(routeNo, cb.count(r)))
                    .groupBy(routeNo)
                    .orderBy(cb.desc(cb.count(r)), cb.asc(routeNo));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("PG0001", 4L),
                        tuple("PG0002", 4L),
                        tuple("PG0005", 4L),
                        tuple("PG0006", 4L),
                        tuple("PG0007", 4L));
    }

}
