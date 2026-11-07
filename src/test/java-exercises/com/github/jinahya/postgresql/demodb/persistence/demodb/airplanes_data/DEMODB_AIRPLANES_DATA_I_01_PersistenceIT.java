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

import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPLANES_DATA_I_01} of
 * {@code src/test/sql-exercises/airplanes_data/02_intermediate.sql} with JPQL and with the Criteria
 * API, or with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPLANES_DATA_I_01: the English name of each airplane")
@Slf4j
class DEMODB_AIRPLANES_DATA_I_01_PersistenceIT {

    // model ->> 'en' is PostgreSQL's; FUNCTION calls its jsonb_extract_path_text, which does the
    // same
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a, FUNCTION('jsonb_extract_path_text', a.model, 'en')
                                        FROM AirplaneData a
                                        ORDER BY a.airplaneCode""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((AirplaneData) r[0]).getAirplaneCode(),
                            r -> r[1],
                            r -> ((AirplaneData) r[0]).getRange())
                .containsExactly(
                        tuple("32N", "Airbus A320neo", 6500),
                        tuple("339", "Airbus A330-900neo", 13300),
                        tuple("351", "Airbus A350-1000", 16700),
                        tuple("35X", "Airbus A350F", 8700),
                        tuple("76F", "Boeing 767-300F", 6000),
                        tuple("77W", "Boeing 777-300ER", 14600),
                        tuple("789", "Boeing 787-9 Dreamliner", 14000),
                        tuple("7M7", "Boeing 737 MAX 7", 7000),
                        tuple("CR7", "Bombardier CRJ700", 3100),
                        tuple("E70", "Embraer E170", 4000));
        assertThat(list)
                .allSatisfy(r -> assertThat(((AirplaneData) r[0]).getModelName(Locale.ENGLISH))
                        .isEqualTo(r[1]));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(AirplaneData.class);
            cq.select(cb.array(a, cb.function("jsonb_extract_path_text", String.class,
                                              a.get(MappedAirplaneData_.model), cb.literal("en"))))
                    .orderBy(cb.asc(a.get(MappedAirplaneData_.airplaneCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((AirplaneData) r[0]).getAirplaneCode(),
                            r -> r[1],
                            r -> ((AirplaneData) r[0]).getRange())
                .containsExactly(
                        tuple("32N", "Airbus A320neo", 6500),
                        tuple("339", "Airbus A330-900neo", 13300),
                        tuple("351", "Airbus A350-1000", 16700),
                        tuple("35X", "Airbus A350F", 8700),
                        tuple("76F", "Boeing 767-300F", 6000),
                        tuple("77W", "Boeing 777-300ER", 14600),
                        tuple("789", "Boeing 787-9 Dreamliner", 14000),
                        tuple("7M7", "Boeing 737 MAX 7", 7000),
                        tuple("CR7", "Bombardier CRJ700", 3100),
                        tuple("E70", "Embraer E170", 4000));
        assertThat(list)
                .allSatisfy(r -> assertThat(((AirplaneData) r[0]).getModelName(Locale.ENGLISH))
                        .isEqualTo(r[1]));
    }

}
