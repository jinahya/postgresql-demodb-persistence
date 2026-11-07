package com.github.jinahya.postgresql.demodb.persistence.demodb.airports_data;

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

import com.github.jinahya.postgresql.demodb.persistence.AirportData;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirportData_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPORTS_DATA_I_02} of
 * {@code src/test/sql-exercises/airports_data/02_intermediate.sql} with JPQL and with the Criteria
 * API, or with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPORTS_DATA_I_02: the ten time zones with the most airports")
@Slf4j
class DEMODB_AIRPORTS_DATA_I_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a.timezone, COUNT(a)
                                        FROM AirportData a
                                        GROUP BY a.timezone
                                        ORDER BY COUNT(a) DESC, a.timezone""",
                                Object[].class
                        )
                        .setMaxResults(10)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(ZoneId.of("America/New_York"), 366L),
                        tuple(ZoneId.of("America/Chicago"), 296L),
                        tuple(ZoneId.of("Asia/Shanghai"), 188L),
                        tuple(ZoneId.of("America/Anchorage"), 168L),
                        tuple(ZoneId.of("America/Los_Angeles"), 143L),
                        tuple(ZoneId.of("Europe/Paris"), 123L),
                        tuple(ZoneId.of("America/Toronto"), 120L),
                        tuple(ZoneId.of("America/Denver"), 110L),
                        tuple(ZoneId.of("Asia/Kolkata"), 110L),
                        tuple(ZoneId.of("Europe/London"), 106L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(AirportData.class);
            final var timezone = a.get(MappedAirportData_.timezone);
            cq.select(cb.array(timezone, cb.count(a)))
                    .groupBy(timezone)
                    .orderBy(cb.desc(cb.count(a)), cb.asc(timezone));
            return em.createQuery(cq)
                    .setMaxResults(10)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple(ZoneId.of("America/New_York"), 366L),
                        tuple(ZoneId.of("America/Chicago"), 296L),
                        tuple(ZoneId.of("Asia/Shanghai"), 188L),
                        tuple(ZoneId.of("America/Anchorage"), 168L),
                        tuple(ZoneId.of("America/Los_Angeles"), 143L),
                        tuple(ZoneId.of("Europe/Paris"), 123L),
                        tuple(ZoneId.of("America/Toronto"), 120L),
                        tuple(ZoneId.of("America/Denver"), 110L),
                        tuple(ZoneId.of("Asia/Kolkata"), 110L),
                        tuple(ZoneId.of("Europe/London"), 106L));
    }

}
