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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPORTS_DATA_I_01} of
 * {@code src/test/sql-exercises/airports_data/02_intermediate.sql} with JPQL and with the Criteria
 * API, or with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPORTS_DATA_I_01: the airports of Finland, in English")
@Slf4j
class DEMODB_AIRPORTS_DATA_I_01_PersistenceIT {

    // ->> 'en' is PostgreSQL's; FUNCTION calls its jsonb_extract_path_text, which does the same
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a,
                                               FUNCTION('jsonb_extract_path_text', a.airportName, 'en'),
                                               FUNCTION('jsonb_extract_path_text', a.city, 'en')
                                        FROM AirportData a
                                        WHERE FUNCTION('jsonb_extract_path_text', a.country, 'en') = :country
                                        ORDER BY a.airportCode""",
                                Object[].class
                        )
                        .setParameter("country", "Finland")
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        // 33 rows; the first ten
        assertThat(list)
                .hasSize(33)
                .extracting(r -> ((AirportData) r[0]).getAirportCode(), r -> r[1], r -> r[2])
                .startsWith(
                        tuple("ENF", "Enontekio", "Enontekio"),
                        tuple("HEL", "Vantaa", "Helsinki"),
                        tuple("HEM", "Malmi", "Helsinki"),
                        tuple("HYV", "Hyvinkää", "Hyvinkää"),
                        tuple("IVL", "Ivalo", "Ivalo"),
                        tuple("JOE", "Joensuu", "Joensuu"),
                        tuple("JYV", "Jyvaskyla", "Jyvaskyla"),
                        tuple("KAJ", "Kajaani", "Kajaani"),
                        tuple("KAO", "Kuusamo", "Kuusamo"),
                        tuple("KAU", "Kauhava", "Kauhava"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(AirportData.class);
            final var en = cb.literal("en");
            cq.select(cb.array(
                            a,
                            cb.function("jsonb_extract_path_text", String.class,
                                        a.get(MappedAirportData_.airportName), en),
                            cb.function("jsonb_extract_path_text", String.class,
                                        a.get(MappedAirportData_.city), en)))
                    .where(cb.equal(cb.function("jsonb_extract_path_text", String.class,
                                                a.get(MappedAirportData_.country), en),
                                    "Finland"))
                    .orderBy(cb.asc(a.get(MappedAirportData_.airportCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        // 33 rows; the first ten
        assertThat(list)
                .hasSize(33)
                .extracting(r -> ((AirportData) r[0]).getAirportCode(), r -> r[1], r -> r[2])
                .startsWith(
                        tuple("ENF", "Enontekio", "Enontekio"),
                        tuple("HEL", "Vantaa", "Helsinki"),
                        tuple("HEM", "Malmi", "Helsinki"),
                        tuple("HYV", "Hyvinkää", "Hyvinkää"),
                        tuple("IVL", "Ivalo", "Ivalo"),
                        tuple("JOE", "Joensuu", "Joensuu"),
                        tuple("JYV", "Jyvaskyla", "Jyvaskyla"),
                        tuple("KAJ", "Kajaani", "Kajaani"),
                        tuple("KAO", "Kuusamo", "Kuusamo"),
                        tuple("KAU", "Kauhava", "Kauhava"));
    }

}
