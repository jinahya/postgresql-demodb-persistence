package com.github.jinahya.postgresql.demodb.persistence.demodb.airplanes;

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

import com.github.jinahya.postgresql.demodb.persistence.Airplane;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplane_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPLANES_B_01} of {@code src/test/sql-exercises/airplanes/01_basic.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPLANES_B_01: every airplane of the view, by code")
@Slf4j
class DEMODB_AIRPLANES_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a
                                        FROM Airplane a
                                        ORDER BY a.airplaneCode""",
                                Airplane.class
                        )
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Airplane::getAirplaneCode,
                            Airplane::getModel,
                            Airplane::getRange,
                            Airplane::getSpeed)
                .containsExactly(
                        tuple("32N", "Airbus A320neo", 6500, 830),
                        tuple("339", "Airbus A330-900neo", 13300, 870),
                        tuple("351", "Airbus A350-1000", 16700, 913),
                        tuple("35X", "Airbus A350F", 8700, 903),
                        tuple("76F", "Boeing 767-300F", 6000, 850),
                        tuple("77W", "Boeing 777-300ER", 14600, 905),
                        tuple("789", "Boeing 787-9 Dreamliner", 14000, 913),
                        tuple("7M7", "Boeing 737 MAX 7", 7000, 840),
                        tuple("CR7", "Bombardier CRJ700", 3100, 829),
                        tuple("E70", "Embraer E170", 4000, 800));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Airplane.class);
            final var a = cq.from(Airplane.class);
            cq.select(a)
                    .orderBy(cb.asc(a.get(MappedAirplane_.airplaneCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Airplane::getAirplaneCode,
                            Airplane::getModel,
                            Airplane::getRange,
                            Airplane::getSpeed)
                .containsExactly(
                        tuple("32N", "Airbus A320neo", 6500, 830),
                        tuple("339", "Airbus A330-900neo", 13300, 870),
                        tuple("351", "Airbus A350-1000", 16700, 913),
                        tuple("35X", "Airbus A350F", 8700, 903),
                        tuple("76F", "Boeing 767-300F", 6000, 850),
                        tuple("77W", "Boeing 777-300ER", 14600, 905),
                        tuple("789", "Boeing 787-9 Dreamliner", 14000, 913),
                        tuple("7M7", "Boeing 737 MAX 7", 7000, 840),
                        tuple("CR7", "Bombardier CRJ700", 3100, 829),
                        tuple("E70", "Embraer E170", 4000, 800));
    }

}
