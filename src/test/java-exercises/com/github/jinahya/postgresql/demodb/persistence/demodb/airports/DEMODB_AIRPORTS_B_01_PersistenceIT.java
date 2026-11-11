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

import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPORTS_B_01} of {@code src/test/sql-exercises/airports/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPORTS_B_01: the airports of Norway")
@Slf4j
class DEMODB_AIRPORTS_B_01_PersistenceIT {

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
                                        WHERE a.country = :country
                                        ORDER BY a.airportCode""",
                                Airport.class
                        )
                        .setParameter("country", "Norway")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 55 rows; the first ten
        final var oslo = ZoneId.of("Europe/Oslo");
        assertThat(list)
                .hasSize(55)
                .extracting(Airport::getAirportCode,
                            Airport::getAirportName,
                            Airport::getCity,
                            Airport::getCountry,
                            Airport::getCoordinates,
                            Airport::getTimezone)
                .startsWith(
                        tuple("AES", "Vigra", "Ålesund", "Norway", List.of(6.1197, 62.5625), oslo),
                        tuple("ALF", "Alta", "Alta", "Norway", List.of(23.3717, 69.9761), oslo),
                        tuple("ANX", "Andøya", "Andenes", "Norway", List.of(16.1442, 69.2925),
                              oslo),
                        tuple("BDU", "Bardufoss", "Bardufoss", "Norway", List.of(18.5404, 69.0558),
                              oslo),
                        tuple("BGO", "Flesland", "Bergen", "Norway", List.of(5.2181, 60.2934),
                              oslo),
                        tuple("BJF", "Båtsfjord", "Båtsfjord", "Norway", List.of(29.6914, 70.6005),
                              oslo),
                        tuple("BNN", "Brønnøysund", "Brønnøysund", "Norway",
                              List.of(12.2175, 65.4611), oslo),
                        tuple("BOO", "Bodø", "Bodø", "Norway", List.of(14.3653, 67.2692), oslo),
                        tuple("BVG", "Berlevåg", "Berlevåg", "Norway", List.of(29.0342, 70.8714),
                              oslo),
                        tuple("DLD", "Dagali", "Geilo", "Norway", List.of(8.5183, 60.4173), oslo));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Airport.class);
            final var a = cq.from(Airport.class);
            cq.select(a)
                    .where(cb.equal(a.get(MappedAirport_.country), "Norway"))
                    .orderBy(cb.asc(a.get(MappedAirport_.airportCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 55 rows; the first ten
        final var oslo = ZoneId.of("Europe/Oslo");
        assertThat(list)
                .hasSize(55)
                .extracting(Airport::getAirportCode,
                            Airport::getAirportName,
                            Airport::getCity,
                            Airport::getCountry,
                            Airport::getCoordinates,
                            Airport::getTimezone)
                .startsWith(
                        tuple("AES", "Vigra", "Ålesund", "Norway", List.of(6.1197, 62.5625), oslo),
                        tuple("ALF", "Alta", "Alta", "Norway", List.of(23.3717, 69.9761), oslo),
                        tuple("ANX", "Andøya", "Andenes", "Norway", List.of(16.1442, 69.2925),
                              oslo),
                        tuple("BDU", "Bardufoss", "Bardufoss", "Norway", List.of(18.5404, 69.0558),
                              oslo),
                        tuple("BGO", "Flesland", "Bergen", "Norway", List.of(5.2181, 60.2934),
                              oslo),
                        tuple("BJF", "Båtsfjord", "Båtsfjord", "Norway", List.of(29.6914, 70.6005),
                              oslo),
                        tuple("BNN", "Brønnøysund", "Brønnøysund", "Norway",
                              List.of(12.2175, 65.4611), oslo),
                        tuple("BOO", "Bodø", "Bodø", "Norway", List.of(14.3653, 67.2692), oslo),
                        tuple("BVG", "Berlevåg", "Berlevåg", "Norway", List.of(29.0342, 70.8714),
                              oslo),
                        tuple("DLD", "Dagali", "Geilo", "Norway", List.of(8.5183, 60.4173), oslo));
    }

}
