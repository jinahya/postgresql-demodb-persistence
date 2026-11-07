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
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SEGMENTS_B_01} of {@code src/test/sql-exercises/segments/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SEGMENTS_B_01: the segments of the ticket 0005432000000")
@Slf4j
class DEMODB_SEGMENTS_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT s
                                        FROM Segment s
                                        WHERE s.id.ticketNo = :ticketNo
                                        ORDER BY s.id.flightId""",
                                Segment.class
                        )
                        .setParameter("ticketNo", "0005432000000")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Segment::getTicketNo,
                            Segment::getFlightId,
                            Segment::getFareConditions,
                            Segment::getPrice)
                .containsExactly(
                        tuple("0005432000000", 108, FareConditions.ECONOMY,
                              new BigDecimal("3250.00")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Segment.class);
            final var s = cq.from(Segment.class);
            final var id = s.get(MappedSegment_.id);
            cq.select(s)
                    .where(cb.equal(id.get(SegmentId_.ticketNo), "0005432000000"))
                    .orderBy(cb.asc(id.get(SegmentId_.flightId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Segment::getTicketNo,
                            Segment::getFlightId,
                            Segment::getFareConditions,
                            Segment::getPrice)
                .containsExactly(
                        tuple("0005432000000", 108, FareConditions.ECONOMY,
                              new BigDecimal("3250.00")));
    }

}
