package com.github.jinahya.postgresql.demodb.persistence.demodb.flights;

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

import com.github.jinahya.postgresql.demodb.persistence.Flight;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_FLIGHTS_B_02} of {@code src/test/sql-exercises/flights/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_FLIGHTS_B_02: how many flights of each status")
@Slf4j
class DEMODB_FLIGHTS_B_02_PersistenceIT {

    // the status is ordered by its column value, the text, not by the order of the constants
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT f.status, COUNT(f)
                                        FROM Flight f
                                        GROUP BY f.status
                                        ORDER BY f.status""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(FlightStatus.ARRIVED, 10966L),
                        tuple(FlightStatus.BOARDING, 4L),
                        tuple(FlightStatus.CANCELLED, 121L),
                        tuple(FlightStatus.DELAYED, 10L),
                        tuple(FlightStatus.DEPARTED, 20L),
                        tuple(FlightStatus.ON_TIME, 157L),
                        tuple(FlightStatus.SCHEDULED, 10480L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var f = cq.from(Flight.class);
            final var status = f.get(MappedFlight_.status);
            cq.select(cb.array(status, cb.count(f)))
                    .groupBy(status)
                    .orderBy(cb.asc(status));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(FlightStatus.ARRIVED, 10966L),
                        tuple(FlightStatus.BOARDING, 4L),
                        tuple(FlightStatus.CANCELLED, 121L),
                        tuple(FlightStatus.DELAYED, 10L),
                        tuple(FlightStatus.DEPARTED, 20L),
                        tuple(FlightStatus.ON_TIME, 157L),
                        tuple(FlightStatus.SCHEDULED, 10480L));
    }

}
