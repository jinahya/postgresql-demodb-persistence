package com.github.jinahya.postgresql.demodb.persistence.demodb.segments;

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

import com.github.jinahya.postgresql.demodb.persistence.Segment;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSegment_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SEGMENTS_I_02} of
 * {@code src/test/sql-exercises/segments/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 */
@DisplayName("DEMODB_SEGMENTS_I_02: the five flights that earned the most")
@Slf4j
class DEMODB_SEGMENTS_I_02_PersistenceIT {

    // the SQL groups by the segments' flight_id, not joining flights; so does the query
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT s.id.flightId, SUM(s.price), COUNT(s)
                                        FROM Segment s
                                        GROUP BY s.id.flightId
                                        ORDER BY SUM(s.price) DESC, s.id.flightId""",
                                Object[].class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple(16679, new BigDecimal("22532100.00"), 404L),
                        tuple(17927, new BigDecimal("18808575.00"), 338L),
                        tuple(11358, new BigDecimal("15918200.00"), 404L),
                        tuple(11889, new BigDecimal("15918200.00"), 404L),
                        tuple(12608, new BigDecimal("15918200.00"), 404L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var s = cq.from(Segment.class);
            final var flightId = s.get(MappedSegment_.id).get(SegmentId_.flightId);
            final var revenue = cb.sum(s.get(MappedSegment_.price));
            cq.select(cb.array(flightId, revenue, cb.count(s)))
                    .groupBy(flightId)
                    .orderBy(cb.desc(revenue), cb.asc(flightId));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple(16679, new BigDecimal("22532100.00"), 404L),
                        tuple(17927, new BigDecimal("18808575.00"), 338L),
                        tuple(11358, new BigDecimal("15918200.00"), 404L),
                        tuple(11889, new BigDecimal("15918200.00"), 404L),
                        tuple(12608, new BigDecimal("15918200.00"), 404L));
    }

}
