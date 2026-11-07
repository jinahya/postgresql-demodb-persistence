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
import com.github.jinahya.postgresql.demodb.persistence.Flight;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBoardingPass_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_BOARDING_PASSES_I_04} of
 * {@code src/test/sql-exercises/boarding_passes/02_intermediate.sql} with JPQL and with the
 * Criteria API, or with a native query where neither can.
 */
@DisplayName(
        "DEMODB_BOARDING_PASSES_I_04: the five arrived flights with the most passengers checked " +
        "in but not boarded")
@Slf4j
class DEMODB_BOARDING_PASSES_I_04_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT f,
                                               COUNT(bp),
                                               COUNT(bp.boardingNo),
                                               COUNT(bp) - COUNT(bp.boardingNo)
                                        FROM BoardingPass bp
                                                 JOIN Flight f ON f.flightId = bp.id.flightId
                                        WHERE f.status = :status
                                        GROUP BY f
                                        HAVING COUNT(bp) > COUNT(bp.boardingNo)
                                        ORDER BY COUNT(bp) - COUNT(bp.boardingNo) DESC, f.flightId""",
                                Object[].class
                        )
                        .setParameter("status", FlightStatus.ARRIVED)
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Flight) r[0]).getFlightId(),
                            r -> ((Number) r[1]).longValue(),
                            r -> ((Number) r[2]).longValue(),
                            r -> ((Number) r[3]).longValue())
                .containsExactly(
                        tuple(8953, 247L, 65L, 182L),
                        tuple(9761, 404L, 248L, 156L),
                        tuple(1607, 200L, 87L, 113L),
                        tuple(10471, 257L, 183L, 74L),
                        tuple(8988, 371L, 318L, 53L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var bp = cq.from(BoardingPass.class);
            final var f = bp.join(Flight.class);
            f.on(cb.equal(f.get(MappedFlight_.flightId),
                          bp.get(MappedBoardingPass_.id).get(SegmentId_.flightId)));
            final var checkedIn = cb.count(bp);
            final var boarded = cb.count(bp.get(MappedBoardingPass_.boardingNo));
            final var notBoarded = cb.diff(checkedIn, boarded);
            cq.select(cb.array(f, checkedIn, boarded, notBoarded))
                    .where(cb.equal(f.get(MappedFlight_.status), FlightStatus.ARRIVED))
                    // grouped by the flight's id, not by the flight: here Hibernate ORM would
                    // list all the columns of the flight, its route's subquery among them, which
                    // runs for every pass; PostgreSQL takes the flight's columns as functionally
                    // dependent on its primary key, as it does for GROUP BY f in the query
                    // language, which Hibernate ORM renders as the id alone
                    .groupBy(f.get(MappedFlight_.flightId))
                    .having(cb.gt(checkedIn, boarded))
                    .orderBy(cb.desc(notBoarded), cb.asc(f.get(MappedFlight_.flightId)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Flight) r[0]).getFlightId(),
                            r -> ((Number) r[1]).longValue(),
                            r -> ((Number) r[2]).longValue(),
                            r -> ((Number) r[3]).longValue())
                .containsExactly(
                        tuple(8953, 247L, 65L, 182L),
                        tuple(9761, 404L, 248L, 156L),
                        tuple(1607, 200L, 87L, 113L),
                        tuple(10471, 257L, 183L, 74L),
                        tuple(8988, 371L, 318L, 53L));
    }

}
