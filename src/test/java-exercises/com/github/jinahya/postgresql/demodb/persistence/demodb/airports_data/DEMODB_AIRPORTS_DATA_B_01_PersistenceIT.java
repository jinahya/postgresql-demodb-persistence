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
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_AIRPORTS_DATA_B_01} of
 * {@code src/test/sql-exercises/airports_data/01_basic.sql} with JPQL and with the Criteria API, or
 * with a native query where neither can.
 */
@DisplayName("DEMODB_AIRPORTS_DATA_B_01: the airports in the Europe/Moscow time zone")
@Slf4j
class DEMODB_AIRPORTS_DATA_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a
                                        FROM AirportData a
                                        WHERE a.timezone = :timezone
                                        ORDER BY a.airportCode""",
                                AirportData.class
                        )
                        .setParameter("timezone", ZoneId.of("Europe/Moscow"))
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 65 rows; the first ten
        assertThat(list)
                .hasSize(65)
                .extracting(AirportData::getAirportCode,
                            e -> e.getAirportName(Locale.ENGLISH),
                            e -> e.getCityName(Locale.ENGLISH))
                .startsWith(
                        tuple("AAQ", "Vityazevo", "Anapa"),
                        tuple("AER", "Sochi", "Sochi"),
                        tuple("AMV", "Amderma", "Amderma"),
                        tuple("ARH", "Talagi", "Arkhangelsk"),
                        tuple("BKA", "Bykovo", "Moscow"),
                        tuple("BWO", "Balakovo", "Balakovo"),
                        tuple("BZK", "Bryansk", "Bryansk"),
                        tuple("CEE", "Cherepovets", "Cherepovets"),
                        tuple("CKL", "Chkalovskiy", "Shchyolkovo"),
                        tuple("CSH", "Solovki", "Solovetsky Islands"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(AirportData.class);
            final var a = cq.from(AirportData.class);
            cq.select(a)
                    .where(cb.equal(a.get(MappedAirportData_.timezone),
                                    ZoneId.of("Europe/Moscow")))
                    .orderBy(cb.asc(a.get(MappedAirportData_.airportCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 65 rows; the first ten
        assertThat(list)
                .hasSize(65)
                .extracting(AirportData::getAirportCode,
                            e -> e.getAirportName(Locale.ENGLISH),
                            e -> e.getCityName(Locale.ENGLISH))
                .startsWith(
                        tuple("AAQ", "Vityazevo", "Anapa"),
                        tuple("AER", "Sochi", "Sochi"),
                        tuple("AMV", "Amderma", "Amderma"),
                        tuple("ARH", "Talagi", "Arkhangelsk"),
                        tuple("BKA", "Bykovo", "Moscow"),
                        tuple("BWO", "Balakovo", "Balakovo"),
                        tuple("BZK", "Bryansk", "Bryansk"),
                        tuple("CEE", "Cherepovets", "Cherepovets"),
                        tuple("CKL", "Chkalovskiy", "Shchyolkovo"),
                        tuple("CSH", "Solovki", "Solovetsky Islands"));
    }

}
