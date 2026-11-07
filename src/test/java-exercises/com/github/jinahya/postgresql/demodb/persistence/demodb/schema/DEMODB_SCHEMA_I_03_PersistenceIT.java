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

import com.github.jinahya.postgresql.demodb.persistence.AirplaneData;
import com.github.jinahya.postgresql.demodb.persistence.Flight_;
import com.github.jinahya.postgresql.demodb.persistence.Route_;
import com.github.jinahya.postgresql.demodb.persistence.Segment;
import com.github.jinahya.postgresql.demodb.persistence.Segment_;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplaneData_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSegment_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SCHEMA_I_03} of {@code src/test/sql-exercises/__/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SCHEMA_I_03: the money each airplane model took")
@Slf4j
class DEMODB_SCHEMA_I_03_PersistenceIT {

    // f.route is the row of routes valid at the flight's scheduled departure, which Flight maps
    // with Hibernate ORM's @JoinColumnsOrFormulas. The airplane is the route's AirplaneData, not
    // the airplanes view: a view has no primary key, so PostgreSQL would not take its model as
    // dependent on the code, by which Hibernate ORM groups an entity; the model's name, which the
    // view gives in the language of lang(), en, is read from the entity in English.
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT a, SUM(s.price)
                                        FROM Segment s
                                                 JOIN s.flight f
                                                 JOIN f.route r
                                                 JOIN r.airplaneData a
                                        GROUP BY a
                                        ORDER BY SUM(s.price) DESC, a.airplaneCode""",
                                Object[].class
                        )
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((AirplaneData) r[0]).getAirplaneCode(),
                            r -> ((AirplaneData) r[0]).getModelName(Locale.ENGLISH),
                            r -> r[1])
                .containsExactly(
                        tuple("77W", "Boeing 777-300ER", new BigDecimal("7669794850.00")),
                        tuple("789", "Boeing 787-9 Dreamliner", new BigDecimal("5208898700.00")),
                        tuple("7M7", "Boeing 737 MAX 7", new BigDecimal("3421118000.00")),
                        tuple("351", "Airbus A350-1000", new BigDecimal("3288182250.00")),
                        tuple("339", "Airbus A330-900neo", new BigDecimal("2231027150.00")),
                        tuple("E70", "Embraer E170", new BigDecimal("1303822500.00")),
                        tuple("CR7", "Bombardier CRJ700", new BigDecimal("814050375.00")),
                        tuple("32N", "Airbus A320neo", new BigDecimal("637037750.00")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var s = cq.from(Segment.class);
            final var f = s.join(Segment_.flight);
            final var r = f.join(Flight_.route);
            final var a = r.join(Route_.airplaneData);
            final var revenue = cb.sum(s.get(MappedSegment_.price));
            cq.select(cb.array(a, revenue))
                    .groupBy(a)
                    .orderBy(cb.desc(revenue), cb.asc(a.get(MappedAirplaneData_.airplaneCode)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((AirplaneData) r[0]).getAirplaneCode(),
                            r -> ((AirplaneData) r[0]).getModelName(Locale.ENGLISH),
                            r -> r[1])
                .containsExactly(
                        tuple("77W", "Boeing 777-300ER", new BigDecimal("7669794850.00")),
                        tuple("789", "Boeing 787-9 Dreamliner", new BigDecimal("5208898700.00")),
                        tuple("7M7", "Boeing 737 MAX 7", new BigDecimal("3421118000.00")),
                        tuple("351", "Airbus A350-1000", new BigDecimal("3288182250.00")),
                        tuple("339", "Airbus A330-900neo", new BigDecimal("2231027150.00")),
                        tuple("E70", "Embraer E170", new BigDecimal("1303822500.00")),
                        tuple("CR7", "Bombardier CRJ700", new BigDecimal("814050375.00")),
                        tuple("32N", "Airbus A320neo", new BigDecimal("637037750.00")));
    }

}
