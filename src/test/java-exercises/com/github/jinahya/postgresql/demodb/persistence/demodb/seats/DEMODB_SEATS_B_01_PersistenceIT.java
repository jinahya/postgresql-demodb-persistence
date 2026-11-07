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

import com.github.jinahya.postgresql.demodb.persistence.Seat;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat.FareConditions;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SeatId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code DEMODB_SEATS_B_01} of {@code src/test/sql-exercises/seats/01_basic.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SEATS_B_01: the Business seats of the Airbus A320neo")
@Slf4j
class DEMODB_SEATS_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT s
                                        FROM Seat s
                                        WHERE s.id.airplaneCode = :airplaneCode
                                          AND s.fareConditions = :fareConditions
                                        ORDER BY s.id.seatNo""",
                                Seat.class
                        )
                        .setParameter("airplaneCode", "32N")
                        .setParameter("fareConditions", FareConditions.BUSINESS)
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 28 rows; the first ten
        assertThat(list)
                .hasSize(28)
                .allSatisfy(e -> {
                    assertThat(e.getAirplaneCode()).isEqualTo("32N");
                    assertThat(e.getFareConditions()).isEqualTo(FareConditions.BUSINESS);
                })
                .extracting(Seat::getSeatNo)
                .startsWith("1A", "1C", "1D", "1F", "2A", "2C", "2D", "2F", "3A", "3C");
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Seat.class);
            final var s = cq.from(Seat.class);
            final var id = s.get(MappedSeat_.id);
            cq.select(s)
                    .where(cb.equal(id.get(SeatId_.airplaneCode), "32N"),
                           cb.equal(s.get(MappedSeat_.fareConditions), FareConditions.BUSINESS))
                    .orderBy(cb.asc(id.get(SeatId_.seatNo)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 28 rows; the first ten
        assertThat(list)
                .hasSize(28)
                .allSatisfy(e -> {
                    assertThat(e.getAirplaneCode()).isEqualTo("32N");
                    assertThat(e.getFareConditions()).isEqualTo(FareConditions.BUSINESS);
                })
                .extracting(Seat::getSeatNo)
                .startsWith("1A", "1C", "1D", "1F", "2A", "2C", "2D", "2F", "3A", "3C");
    }

}
