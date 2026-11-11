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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPORTS_B_02} of {@code src/test/sql-exercises/airports/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPORTS_B_02: the airports of Moscow")
@Slf4j
class DEMODB_AIRPORTS_B_02_PersistenceIT {

    @BeforeAll
    static void assumeLang() {
        __Exercises_TestUtils.assumeLang();
    }

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a
                                        FROM Airport a
                                        WHERE a.city = :city
                                        ORDER BY a.airportCode""",
                                Airport.class
                        )
                        .setParameter("city", "Moscow")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Airport::getAirportCode,
                            Airport::getAirportName,
                            Airport::getCity,
                            Airport::getCountry)
                .containsExactly(
                        tuple("BKA", "Bykovo", "Moscow", "Russia"),
                        tuple("DME", "Domodedovo", "Moscow", "Russia"),
                        tuple("OSF", "Ostafyevo", "Moscow", "Russia"),
                        tuple("SVO", "Sheremetyevo", "Moscow", "Russia"),
                        tuple("VKO", "Vnukovo", "Moscow", "Russia"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Airport.class);
            final var a = cq.from(Airport.class);
            cq.select(a)
                    .where(cb.equal(a.get(MappedAirport_.city), "Moscow"))
                    .orderBy(cb.asc(a.get(MappedAirport_.airportCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Airport::getAirportCode,
                            Airport::getAirportName,
                            Airport::getCity,
                            Airport::getCountry)
                .containsExactly(
                        tuple("BKA", "Bykovo", "Moscow", "Russia"),
                        tuple("DME", "Domodedovo", "Moscow", "Russia"),
                        tuple("OSF", "Ostafyevo", "Moscow", "Russia"),
                        tuple("SVO", "Sheremetyevo", "Moscow", "Russia"),
                        tuple("VKO", "Vnukovo", "Moscow", "Russia"));
    }

}
