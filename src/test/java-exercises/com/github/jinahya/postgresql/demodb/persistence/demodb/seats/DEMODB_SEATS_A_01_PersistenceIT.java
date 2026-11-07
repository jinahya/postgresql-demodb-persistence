package com.github.jinahya.postgresql.demodb.persistence.demodb.seats;

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
import com.github.jinahya.postgresql.demodb.persistence.Seat;
import com.github.jinahya.postgresql.demodb.persistence.Timetable;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBoardingPass_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat.FareConditions;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTimetable_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SeatId_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SEATS_A_01} of {@code src/test/sql-exercises/seats/03_advanced.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SEATS_A_01: the empty seats of the flight 1")
@Slf4j
class DEMODB_SEATS_A_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT s
                                        FROM Timetable t
                                                 JOIN Seat s ON s.id.airplaneCode = t.airplaneCode
                                        WHERE t.flightId = :flightId
                                          AND NOT EXISTS (SELECT bp
                                                          FROM BoardingPass bp
                                                          WHERE bp.id.flightId = t.flightId
                                                            AND bp.seatNo = s.id.seatNo)
                                        ORDER BY s.id.seatNo""",
                                Seat.class
                        )
                        .setParameter("flightId", 1)
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 112 rows; the first ten
        assertThat(list)
                .hasSize(112)
                .extracting(Seat::getSeatNo, Seat::getFareConditions)
                .startsWith(
                        tuple("10A", FareConditions.ECONOMY),
                        tuple("10B", FareConditions.ECONOMY),
                        tuple("10C", FareConditions.ECONOMY),
                        tuple("10D", FareConditions.ECONOMY),
                        tuple("10E", FareConditions.ECONOMY),
                        tuple("10F", FareConditions.ECONOMY),
                        tuple("11A", FareConditions.ECONOMY),
                        tuple("11C", FareConditions.ECONOMY),
                        tuple("11D", FareConditions.ECONOMY),
                        tuple("11E", FareConditions.ECONOMY));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Seat.class);
            final var t = cq.from(Timetable.class);
            final var s = t.join(Seat.class);
            final var seatId = s.get(MappedSeat_.id);
            s.on(cb.equal(seatId.get(SeatId_.airplaneCode), t.get(MappedTimetable_.airplaneCode)));
            final var sq = cq.subquery(BoardingPass.class);
            final var bp = sq.from(BoardingPass.class);
            sq.select(bp)
                    .where(cb.equal(bp.get(MappedBoardingPass_.id).get(SegmentId_.flightId),
                                    t.get(MappedTimetable_.flightId)),
                           cb.equal(bp.get(MappedBoardingPass_.seatNo),
                                    seatId.get(SeatId_.seatNo)));
            cq.select(s)
                    .where(cb.equal(t.get(MappedTimetable_.flightId), 1),
                           cb.not(cb.exists(sq)))
                    .orderBy(cb.asc(seatId.get(SeatId_.seatNo)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 112 rows; the first ten
        assertThat(list)
                .hasSize(112)
                .extracting(Seat::getSeatNo, Seat::getFareConditions)
                .startsWith(
                        tuple("10A", FareConditions.ECONOMY),
                        tuple("10B", FareConditions.ECONOMY),
                        tuple("10C", FareConditions.ECONOMY),
                        tuple("10D", FareConditions.ECONOMY),
                        tuple("10E", FareConditions.ECONOMY),
                        tuple("10F", FareConditions.ECONOMY),
                        tuple("11A", FareConditions.ECONOMY),
                        tuple("11C", FareConditions.ECONOMY),
                        tuple("11D", FareConditions.ECONOMY),
                        tuple("11E", FareConditions.ECONOMY));
    }

}
