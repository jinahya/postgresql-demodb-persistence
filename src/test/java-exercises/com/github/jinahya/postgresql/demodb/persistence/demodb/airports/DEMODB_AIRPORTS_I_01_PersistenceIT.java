package com.github.jinahya.postgresql.demodb.persistence.demodb.airports;

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

import com.github.jinahya.postgresql.demodb.persistence.Airport;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirport_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPORTS_I_01} of
 * {@code src/test/sql-exercises/airports/02_intermediate.sql} with JPQL and with the Criteria API,
 * or with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPORTS_I_01: the ten countries with the most airports")
@Slf4j
class DEMODB_AIRPORTS_I_01_PersistenceIT {

    @BeforeAll
    static void assumeLang() {
        __Exercises_TestUtils.assumeLang();
    }

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a.country, COUNT(a)
                                        FROM Airport a
                                        GROUP BY a.country
                                        ORDER BY COUNT(a) DESC, a.country""",
                                Object[].class
                        )
                        .setMaxResults(10)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("United States", 1129L),
                        tuple("Canada", 335L),
                        tuple("Australia", 218L),
                        tuple("China", 187L),
                        tuple("Brazil", 180L),
                        tuple("Russia", 158L),
                        tuple("France", 122L),
                        tuple("India", 110L),
                        tuple("Indonesia", 105L),
                        tuple("United Kingdom", 105L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var a = cq.from(Airport.class);
            final var country = a.get(MappedAirport_.country);
            cq.select(cb.array(country, cb.count(a)))
                    .groupBy(country)
                    .orderBy(cb.desc(cb.count(a)), cb.asc(country));
            return em.createQuery(cq)
                    .setMaxResults(10)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("United States", 1129L),
                        tuple("Canada", 335L),
                        tuple("Australia", 218L),
                        tuple("China", 187L),
                        tuple("Brazil", 180L),
                        tuple("Russia", 158L),
                        tuple("France", 122L),
                        tuple("India", 110L),
                        tuple("Indonesia", 105L),
                        tuple("United Kingdom", 105L));
    }

}
