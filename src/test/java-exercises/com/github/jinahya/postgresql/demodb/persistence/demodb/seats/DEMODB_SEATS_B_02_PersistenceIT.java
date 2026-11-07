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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SeatId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SEATS_B_02} of {@code src/test/sql-exercises/seats/01_basic.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SEATS_B_02: how many seats each airplane has")
@Slf4j
class DEMODB_SEATS_B_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT s.id.airplaneCode, COUNT(s)
                                        FROM Seat s
                                        GROUP BY s.id.airplaneCode
                                        ORDER BY s.id.airplaneCode""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("32N", 166L),
                        tuple("339", 281L),
                        tuple("351", 325L),
                        tuple("77W", 404L),
                        tuple("789", 257L),
                        tuple("7M7", 160L),
                        tuple("CR7", 70L),
                        tuple("E70", 78L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var s = cq.from(Seat.class);
            final var airplaneCode = s.get(MappedSeat_.id).get(SeatId_.airplaneCode);
            cq.select(cb.array(airplaneCode, cb.count(s)))
                    .groupBy(airplaneCode)
                    .orderBy(cb.asc(airplaneCode));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("32N", 166L),
                        tuple("339", 281L),
                        tuple("351", 325L),
                        tuple("77W", 404L),
                        tuple("789", 257L),
                        tuple("7M7", 160L),
                        tuple("CR7", 70L),
                        tuple("E70", 78L));
    }

}
