package com.github.jinahya.postgresql.demodb.persistence.demodb.airplanes_data;

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

import com.github.jinahya.postgresql.demodb.persistence.AirplaneData;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplaneData_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPLANES_DATA_B_02} of
 * {@code src/test/sql-exercises/airplanes_data/01_basic.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 */
@DisplayName(
        "DEMODB_AIRPLANES_DATA_B_02: the airplanes with a range of 10000 km or more, the longest " +
        "first")
@Slf4j
class DEMODB_AIRPLANES_DATA_B_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a
                                        FROM AirplaneData a
                                        WHERE a.range >= :range
                                        ORDER BY a.range DESC, a.airplaneCode""",
                                AirplaneData.class
                        )
                        .setParameter("range", 10000)
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(AirplaneData::getAirplaneCode,
                            e -> e.getModelName(Locale.ENGLISH),
                            AirplaneData::getRange,
                            AirplaneData::getSpeed)
                .containsExactly(
                        tuple("351", "Airbus A350-1000", 16700, 913),
                        tuple("77W", "Boeing 777-300ER", 14600, 905),
                        tuple("789", "Boeing 787-9 Dreamliner", 14000, 913),
                        tuple("339", "Airbus A330-900neo", 13300, 870));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(AirplaneData.class);
            final var a = cq.from(AirplaneData.class);
            cq.select(a)
                    .where(cb.greaterThanOrEqualTo(a.get(MappedAirplaneData_.range), 10000))
                    .orderBy(cb.desc(a.get(MappedAirplaneData_.range)),
                             cb.asc(a.get(MappedAirplaneData_.airplaneCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(AirplaneData::getAirplaneCode,
                            e -> e.getModelName(Locale.ENGLISH),
                            AirplaneData::getRange,
                            AirplaneData::getSpeed)
                .containsExactly(
                        tuple("351", "Airbus A350-1000", 16700, 913),
                        tuple("77W", "Boeing 777-300ER", 14600, 905),
                        tuple("789", "Boeing 787-9 Dreamliner", 14000, 913),
                        tuple("339", "Airbus A330-900neo", 13300, 870));
    }

}
