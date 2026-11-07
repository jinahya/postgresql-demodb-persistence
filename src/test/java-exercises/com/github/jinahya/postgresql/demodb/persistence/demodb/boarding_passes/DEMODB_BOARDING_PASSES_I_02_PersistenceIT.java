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
import com.github.jinahya.postgresql.demodb.persistence.Segment;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBoardingPass_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSegment_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code DEMODB_BOARDING_PASSES_I_02} of
 * {@code src/test/sql-exercises/boarding_passes/02_intermediate.sql} with JPQL and with the
 * Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_BOARDING_PASSES_I_02: how many segments have no boarding pass")
@Slf4j
class DEMODB_BOARDING_PASSES_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var count = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT COUNT(s)
                                        FROM Segment s
                                                 LEFT JOIN BoardingPass bp ON bp.id.ticketNo = s.id.ticketNo
                                            AND bp.id.flightId = s.id.flightId
                                        WHERE bp.id.ticketNo IS NULL""",
                                Long.class
                        )
                        .getSingleResult()
        );
        log.debug("count: {}", count);
        assertThat(count).isEqualTo(1477417L);
    }

    @Test
    void __CriteriaApi() {
        final var count = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Long.class);
            final var s = cq.from(Segment.class);
            final var bp = s.join(BoardingPass.class, JoinType.LEFT);
            final var sid = s.get(MappedSegment_.id);
            final var bpid = bp.get(MappedBoardingPass_.id);
            bp.on(cb.equal(bpid.get(SegmentId_.ticketNo), sid.get(SegmentId_.ticketNo)),
                  cb.equal(bpid.get(SegmentId_.flightId), sid.get(SegmentId_.flightId)));
            cq.select(cb.count(s))
                    .where(cb.isNull(bpid.get(SegmentId_.ticketNo)));
            return em.createQuery(cq)
                    .getSingleResult();
        });
        log.debug("count: {}", count);
        assertThat(count).isEqualTo(1477417L);
    }

}
