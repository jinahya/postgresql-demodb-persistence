package com.github.jinahya.postgresql.demodb.persistence.demodb.schema;

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
import com.github.jinahya.postgresql.demodb.persistence.BoardingPass;
import com.github.jinahya.postgresql.demodb.persistence.Flight;
import com.github.jinahya.postgresql.demodb.persistence.Flight_;
import com.github.jinahya.postgresql.demodb.persistence.Route_;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirportData_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBoardingPass_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SCHEMA_I_02} of {@code src/test/sql-exercises/__/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName(
        "DEMODB_SCHEMA_I_02: the ten airports with the most passengers boarded in the first week")
@Slf4j
class DEMODB_SCHEMA_I_02_PersistenceIT {

    // f.route is the row of routes valid at the flight's scheduled departure, which Flight maps
    // with Hibernate ORM's @JoinColumnsOrFormulas. The airport is the route's AirportData, not the
    // airports view: a view has no primary key, so PostgreSQL would not take its name as dependent
    // on the code, by which Hibernate ORM groups an entity; the name, which the view gives in the
    // language of lang(), en, is read from the entity in English.
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a, COUNT(bp)
                                        FROM BoardingPass bp
                                                 JOIN Flight f ON f.flightId = bp.id.flightId
                                                 JOIN f.route r
                                                 JOIN r.departureAirportData a
                                        WHERE f.scheduledDeparture >= :from
                                          AND f.scheduledDeparture < :to
                                          AND bp.boardingNo IS NOT NULL
                                        GROUP BY a
                                        ORDER BY COUNT(bp) DESC, a.airportCode""",
                                Object[].class
                        )
                        .setParameter("from", Instant.parse("2025-10-01T00:00:00Z"))
                        .setParameter("to", Instant.parse("2025-10-08T00:00:00Z"))
                        .setMaxResults(10)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((AirportData) r[0]).getAirportCode(),
                            r -> ((AirportData) r[0]).getAirportName(Locale.ENGLISH),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("PVG", "Pudong", 11468L),
                        tuple("ATL", "Hartsfield-Jackson", 10666L),
                        tuple("DEL", "Indira Gandhi", 9578L),
                        tuple("BOM", "Chhatrapati Shivaji", 8977L),
                        tuple("SHA", "Hongqiao", 7979L),
                        tuple("SZX", "Bao'an", 7743L),
                        tuple("TFU", "Tianfu", 7653L),
                        tuple("FRA", "Frankfurt am Main", 6932L),
                        tuple("HND", "Haneda", 6743L),
                        tuple("CAN", "Baiyun", 6668L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var bp = cq.from(BoardingPass.class);
            final var f = bp.join(Flight.class);
            f.on(cb.equal(f.get(MappedFlight_.flightId),
                          bp.get(MappedBoardingPass_.id).get(SegmentId_.flightId)));
            final var r = f.join(Flight_.route);
            final var a = r.join(Route_.departureAirportData);
            cq.select(cb.array(a, cb.count(bp)))
                    .where(cb.greaterThanOrEqualTo(f.get(MappedFlight_.scheduledDeparture),
                                                   Instant.parse("2025-10-01T00:00:00Z")),
                           cb.lessThan(f.get(MappedFlight_.scheduledDeparture),
                                       Instant.parse("2025-10-08T00:00:00Z")),
                           cb.isNotNull(bp.get(MappedBoardingPass_.boardingNo)))
                    // grouped by the airport's code, not by the airport: here Hibernate ORM would
                    // list all the columns of the airport, and its coordinates, a point, has no
                    // equality operator to group by
                    .groupBy(a.get(MappedAirportData_.airportCode))
                    .orderBy(cb.desc(cb.count(bp)),
                             cb.asc(a.get(MappedAirportData_.airportCode)));
            return em.createQuery(cq)
                    .setMaxResults(10)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((AirportData) r[0]).getAirportCode(),
                            r -> ((AirportData) r[0]).getAirportName(Locale.ENGLISH),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("PVG", "Pudong", 11468L),
                        tuple("ATL", "Hartsfield-Jackson", 10666L),
                        tuple("DEL", "Indira Gandhi", 9578L),
                        tuple("BOM", "Chhatrapati Shivaji", 8977L),
                        tuple("SHA", "Hongqiao", 7979L),
                        tuple("SZX", "Bao'an", 7743L),
                        tuple("TFU", "Tianfu", 7653L),
                        tuple("FRA", "Frankfurt am Main", 6932L),
                        tuple("HND", "Haneda", 6743L),
                        tuple("CAN", "Baiyun", 6668L));
    }

}
