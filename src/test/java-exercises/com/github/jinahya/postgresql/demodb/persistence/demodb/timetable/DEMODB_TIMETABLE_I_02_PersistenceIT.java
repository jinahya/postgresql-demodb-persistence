package com.github.jinahya.postgresql.demodb.persistence.demodb.timetable;

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

import com.github.jinahya.postgresql.demodb.persistence.Timetable;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTimetable_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_TIMETABLE_I_02} of
 * {@code src/test/sql-exercises/timetable/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 */
@DisplayName("DEMODB_TIMETABLE_I_02: the five airport pairs with the most flights")
@Slf4j
class DEMODB_TIMETABLE_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT t.departureAirport, t.arrivalAirport, COUNT(t)
                                        FROM Timetable t
                                        GROUP BY t.departureAirport, t.arrivalAirport
                                        ORDER BY COUNT(t) DESC, t.departureAirport, t.arrivalAirport""",
                                Object[].class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple("BGY", "FCO", 121L),
                        tuple("CAN", "SZX", 121L),
                        tuple("CDG", "NCE", 121L),
                        tuple("FCO", "BGY", 121L),
                        tuple("FCO", "MXP", 121L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var t = cq.from(Timetable.class);
            final var departureAirport = t.get(MappedTimetable_.departureAirport);
            final var arrivalAirport = t.get(MappedTimetable_.arrivalAirport);
            cq.select(cb.array(departureAirport, arrivalAirport, cb.count(t)))
                    .groupBy(departureAirport, arrivalAirport)
                    .orderBy(cb.desc(cb.count(t)), cb.asc(departureAirport),
                             cb.asc(arrivalAirport));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple("BGY", "FCO", 121L),
                        tuple("CAN", "SZX", 121L),
                        tuple("CDG", "NCE", 121L),
                        tuple("FCO", "BGY", 121L),
                        tuple("FCO", "MXP", 121L));
    }

}
