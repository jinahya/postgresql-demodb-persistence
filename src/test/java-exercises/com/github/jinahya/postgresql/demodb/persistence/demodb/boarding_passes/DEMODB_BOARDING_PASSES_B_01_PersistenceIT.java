package com.github.jinahya.postgresql.demodb.persistence.demodb.boarding_passes;

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

import com.github.jinahya.postgresql.demodb.persistence.BoardingPass;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBoardingPass_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import jakarta.persistence.criteria.Nulls;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_BOARDING_PASSES_B_01} of
 * {@code src/test/sql-exercises/boarding_passes/01_basic.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 */
@DisplayName("DEMODB_BOARDING_PASSES_B_01: the boarding passes of the flight 1, in boarding order")
@Slf4j
class DEMODB_BOARDING_PASSES_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT bp
                                        FROM BoardingPass bp
                                        WHERE bp.id.flightId = :flightId
                                        ORDER BY bp.boardingNo NULLS LAST, bp.id.ticketNo""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", 1)
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 48 rows; the first ten
        assertThat(list)
                .hasSize(48)
                .allSatisfy(e -> assertThat(e.getFlightId()).isEqualTo(1))
                .extracting(BoardingPass::getTicketNo,
                            BoardingPass::getSeatNo,
                            BoardingPass::getBoardingNo,
                            BoardingPass::getBoardingTime)
                .startsWith(
                        tuple("0005432486526", "24A", 1,
                              Instant.parse("2025-09-30T23:32:30.846267Z")),
                        tuple("0005432803911", "13C", 2,
                              Instant.parse("2025-09-30T23:33:02.37625Z")),
                        tuple("0005432498784", "14B", 3,
                              Instant.parse("2025-09-30T23:33:33.290931Z")),
                        tuple("0005432803924", "20A", 4,
                              Instant.parse("2025-09-30T23:34:21.750622Z")),
                        tuple("0005432533210", "29D", 5,
                              Instant.parse("2025-09-30T23:34:23.70731Z")),
                        tuple("0005432821450", "24D", 6,
                              Instant.parse("2025-09-30T23:34:34.584991Z")),
                        tuple("0005432099637", "28D", 7,
                              Instant.parse("2025-09-30T23:34:38.218385Z")),
                        tuple("0005432175472", "9B", 8,
                              Instant.parse("2025-09-30T23:34:48.710511Z")),
                        tuple("0005432928358", "20E", 9,
                              Instant.parse("2025-09-30T23:35:28.204127Z")),
                        tuple("0005432229264", "27B", 10,
                              Instant.parse("2025-09-30T23:35:53.553426Z")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(BoardingPass.class);
            final var bp = cq.from(BoardingPass.class);
            final var id = bp.get(MappedBoardingPass_.id);
            cq.select(bp)
                    .where(cb.equal(id.get(SegmentId_.flightId), 1))
                    .orderBy(cb.asc(bp.get(MappedBoardingPass_.boardingNo), Nulls.LAST),
                             cb.asc(id.get(SegmentId_.ticketNo)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 48 rows; the first ten
        assertThat(list)
                .hasSize(48)
                .allSatisfy(e -> assertThat(e.getFlightId()).isEqualTo(1))
                .extracting(BoardingPass::getTicketNo,
                            BoardingPass::getSeatNo,
                            BoardingPass::getBoardingNo,
                            BoardingPass::getBoardingTime)
                .startsWith(
                        tuple("0005432486526", "24A", 1,
                              Instant.parse("2025-09-30T23:32:30.846267Z")),
                        tuple("0005432803911", "13C", 2,
                              Instant.parse("2025-09-30T23:33:02.37625Z")),
                        tuple("0005432498784", "14B", 3,
                              Instant.parse("2025-09-30T23:33:33.290931Z")),
                        tuple("0005432803924", "20A", 4,
                              Instant.parse("2025-09-30T23:34:21.750622Z")),
                        tuple("0005432533210", "29D", 5,
                              Instant.parse("2025-09-30T23:34:23.70731Z")),
                        tuple("0005432821450", "24D", 6,
                              Instant.parse("2025-09-30T23:34:34.584991Z")),
                        tuple("0005432099637", "28D", 7,
                              Instant.parse("2025-09-30T23:34:38.218385Z")),
                        tuple("0005432175472", "9B", 8,
                              Instant.parse("2025-09-30T23:34:48.710511Z")),
                        tuple("0005432928358", "20E", 9,
                              Instant.parse("2025-09-30T23:35:28.204127Z")),
                        tuple("0005432229264", "27B", 10,
                              Instant.parse("2025-09-30T23:35:53.553426Z")));
    }

}
