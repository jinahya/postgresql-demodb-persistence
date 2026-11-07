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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat.FareConditions;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSegment_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SEGMENTS_I_01} of
 * {@code src/test/sql-exercises/segments/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 */
@DisplayName("DEMODB_SEGMENTS_I_01: the prices of each fare conditions")
@Slf4j
class DEMODB_SEGMENTS_I_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT s.fareConditions, MIN(s.price), ROUND(AVG(s.price), 2), MAX(s.price)
                                        FROM Segment s
                                        GROUP BY s.fareConditions
                                        ORDER BY s.fareConditions""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        // AVG gives a Double, whatever the type of its argument
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).doubleValue(), r -> r[3])
                .containsExactly(
                        tuple(FareConditions.BUSINESS, new BigDecimal("3500.00"), 11826.86,
                              new BigDecimal("119500.00")),
                        tuple(FareConditions.COMFORT, new BigDecimal("2275.00"), 6422.43,
                              new BigDecimal("65325.00")),
                        tuple(FareConditions.ECONOMY, new BigDecimal("1750.00"), 5492.63,
                              new BigDecimal("59750.00")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var s = cq.from(Segment.class);
            final var fareConditions = s.get(MappedSegment_.fareConditions);
            final var price = s.get(MappedSegment_.price);
            cq.select(cb.array(fareConditions, cb.min(price), cb.round(cb.avg(price), 2),
                               cb.max(price)))
                    .groupBy(fareConditions)
                    .orderBy(cb.asc(fareConditions));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        // AVG gives a Double, whatever the type of its argument
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).doubleValue(), r -> r[3])
                .containsExactly(
                        tuple(FareConditions.BUSINESS, new BigDecimal("3500.00"), 11826.86,
                              new BigDecimal("119500.00")),
                        tuple(FareConditions.COMFORT, new BigDecimal("2275.00"), 6422.43,
                              new BigDecimal("65325.00")),
                        tuple(FareConditions.ECONOMY, new BigDecimal("1750.00"), 5492.63,
                              new BigDecimal("59750.00")));
    }

}
