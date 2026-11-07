package com.github.jinahya.postgresql.demodb.persistence.demodb.schema;

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
import com.github.jinahya.postgresql.demodb.persistence.BoardingPass_;
import com.github.jinahya.postgresql.demodb.persistence.Flight;
import com.github.jinahya.postgresql.demodb.persistence.Segment;
import com.github.jinahya.postgresql.demodb.persistence.Segment_;
import com.github.jinahya.postgresql.demodb.persistence.Ticket;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBoardingPass_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSegment_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SCHEMA_A_06} of {@code src/test/sql-exercises/__/03_advanced.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SCHEMA_A_06: the passengers still to board the flights now boarding")
@Slf4j
class DEMODB_SCHEMA_A_06_PersistenceIT {

    // bp is null for a segment with no boarding pass
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT f, s, t, bp
                                        FROM Segment s
                                                 JOIN s.flight f
                                                 JOIN s.ticket t
                                                 LEFT JOIN BoardingPass bp ON bp.segment = s
                                        WHERE f.status = :status
                                          AND bp.boardingTime IS NULL
                                        ORDER BY f.flightId, s.id.ticketNo""",
                                Object[].class
                        )
                        .setParameter("status", FlightStatus.BOARDING)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        // 240 rows; the first ten, each with a boarding pass but no boarding number yet
        assertThat(list)
                .hasSize(240)
                .extracting(r -> ((Flight) r[0]).getFlightId(),
                            r -> ((Segment) r[1]).getTicketNo(),
                            r -> ((Ticket) r[2]).getPassengerName(),
                            r -> Optional.ofNullable((BoardingPass) r[3])
                                    .map(BoardingPass::getSeatNo)
                                    .orElse(null),
                            r -> Optional.ofNullable((BoardingPass) r[3])
                                    .map(BoardingPass::getBoardingNo)
                                    .orElse(null))
                .startsWith(
                        tuple(11048, "0005433234672", "Kamla Reddy", "15F", null),
                        tuple(11048, "0005433296114", "Amrita Verma", "50D", null),
                        tuple(11048, "0005433317257", "Laxmi Gupta", "19K", null),
                        tuple(11048, "0005433325245", "Sheetal Kumar", "15D", null),
                        tuple(11048, "0005433337233", "Rajakumari Saha", "34K", null),
                        tuple(11048, "0005433337242", "Ramesh Padhi", "46J", null),
                        tuple(11048, "0005433337249", "Chhotelal Bahadur", "12G", null),
                        tuple(11048, "0005433339085", "Reshma Basumatary", "46C", null),
                        tuple(11048, "0005433354525", "Manisha Mondal", "30D", null),
                        tuple(11048, "0005433354539", "Ramdas Bairagi", "46K", null));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var s = cq.from(Segment.class);
            final var f = s.join(Segment_.flight);
            final var t = s.join(Segment_.ticket);
            final var bp = s.join(BoardingPass.class, JoinType.LEFT);
            bp.on(cb.equal(bp.get(BoardingPass_.segment), s));
            cq.select(cb.array(f, s, t, bp))
                    .where(cb.equal(f.get(MappedFlight_.status), FlightStatus.BOARDING),
                           cb.isNull(bp.get(MappedBoardingPass_.boardingTime)))
                    .orderBy(cb.asc(f.get(MappedFlight_.flightId)),
                             cb.asc(s.get(MappedSegment_.id).get(SegmentId_.ticketNo)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        // 240 rows; the first ten, each with a boarding pass but no boarding number yet
        assertThat(list)
                .hasSize(240)
                .extracting(r -> ((Flight) r[0]).getFlightId(),
                            r -> ((Segment) r[1]).getTicketNo(),
                            r -> ((Ticket) r[2]).getPassengerName(),
                            r -> Optional.ofNullable((BoardingPass) r[3])
                                    .map(BoardingPass::getSeatNo)
                                    .orElse(null),
                            r -> Optional.ofNullable((BoardingPass) r[3])
                                    .map(BoardingPass::getBoardingNo)
                                    .orElse(null))
                .startsWith(
                        tuple(11048, "0005433234672", "Kamla Reddy", "15F", null),
                        tuple(11048, "0005433296114", "Amrita Verma", "50D", null),
                        tuple(11048, "0005433317257", "Laxmi Gupta", "19K", null),
                        tuple(11048, "0005433325245", "Sheetal Kumar", "15D", null),
                        tuple(11048, "0005433337233", "Rajakumari Saha", "34K", null),
                        tuple(11048, "0005433337242", "Ramesh Padhi", "46J", null),
                        tuple(11048, "0005433337249", "Chhotelal Bahadur", "12G", null),
                        tuple(11048, "0005433339085", "Reshma Basumatary", "46C", null),
                        tuple(11048, "0005433354525", "Manisha Mondal", "30D", null),
                        tuple(11048, "0005433354539", "Ramdas Bairagi", "46K", null));
    }

}
