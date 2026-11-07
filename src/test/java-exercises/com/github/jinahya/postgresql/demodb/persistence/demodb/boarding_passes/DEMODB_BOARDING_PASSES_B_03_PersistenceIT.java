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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_BOARDING_PASSES_B_03} of
 * {@code src/test/sql-exercises/boarding_passes/01_basic.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 */
@DisplayName("DEMODB_BOARDING_PASSES_B_03: the boarding passes of the ticket 0005432000000")
@Slf4j
class DEMODB_BOARDING_PASSES_B_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT bp
                                        FROM BoardingPass bp
                                        WHERE bp.id.ticketNo = :ticketNo
                                        ORDER BY bp.id.flightId""",
                                BoardingPass.class
                        )
                        .setParameter("ticketNo", "0005432000000")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(BoardingPass::getTicketNo,
                            BoardingPass::getFlightId,
                            BoardingPass::getSeatNo,
                            BoardingPass::getBoardingNo,
                            BoardingPass::getBoardingTime)
                .containsExactly(
                        tuple("0005432000000", 108, "39F", 343,
                              Instant.parse("2025-10-01T14:35:05.617326Z")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(BoardingPass.class);
            final var bp = cq.from(BoardingPass.class);
            final var id = bp.get(MappedBoardingPass_.id);
            cq.select(bp)
                    .where(cb.equal(id.get(SegmentId_.ticketNo), "0005432000000"))
                    .orderBy(cb.asc(id.get(SegmentId_.flightId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(BoardingPass::getTicketNo,
                            BoardingPass::getFlightId,
                            BoardingPass::getSeatNo,
                            BoardingPass::getBoardingNo,
                            BoardingPass::getBoardingTime)
                .containsExactly(
                        tuple("0005432000000", 108, "39F", 343,
                              Instant.parse("2025-10-01T14:35:05.617326Z")));
    }

}
