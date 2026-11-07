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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SEGMENTS_B_02} of {@code src/test/sql-exercises/segments/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SEGMENTS_B_02: how many segments of each fare conditions")
@Slf4j
class DEMODB_SEGMENTS_B_02_PersistenceIT {

    // the fare conditions are ordered by their column value, the text
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT s.fareConditions, COUNT(s)
                                        FROM Segment s
                                        GROUP BY s.fareConditions
                                        ORDER BY s.fareConditions""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(FareConditions.BUSINESS, 417644L),
                        tuple(FareConditions.COMFORT, 301832L),
                        tuple(FareConditions.ECONOMY, 3221773L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var s = cq.from(Segment.class);
            final var fareConditions = s.get(MappedSegment_.fareConditions);
            cq.select(cb.array(fareConditions, cb.count(s)))
                    .groupBy(fareConditions)
                    .orderBy(cb.asc(fareConditions));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(FareConditions.BUSINESS, 417644L),
                        tuple(FareConditions.COMFORT, 301832L),
                        tuple(FareConditions.ECONOMY, 3221773L));
    }

}
