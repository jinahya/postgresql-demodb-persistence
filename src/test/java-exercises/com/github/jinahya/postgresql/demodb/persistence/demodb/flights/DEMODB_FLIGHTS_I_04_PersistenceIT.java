package com.github.jinahya.postgresql.demodb.persistence.demodb.flights;

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

import com.github.jinahya.postgresql.demodb.persistence.Flight;
import com.github.jinahya.postgresql.demodb.persistence.Flight_;
import com.github.jinahya.postgresql.demodb.persistence.Route;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight_;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_FLIGHTS_I_04} of {@code src/test/sql-exercises/flights/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_FLIGHTS_I_04: today's flights, with their airports")
@Slf4j
class DEMODB_FLIGHTS_I_04_PersistenceIT {

    // The database's today is the UTC day of bookings.now(), whose bounds are computed here and
    // bound as parameters. The demo database fixes bookings.now(), so the rows do not change with
    // the wall clock.
    private static Instant[] today(final EntityManager em) {
        final var from = __Exercises_TestUtils.now(em).truncatedTo(ChronoUnit.DAYS);
        return new Instant[] {from, from.plus(1L, ChronoUnit.DAYS)};
    }

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var today = today(em);
            return em.createQuery(
                            """
                                    SELECT f, r
                                    FROM Flight f
                                             JOIN f.route r
                                    WHERE f.scheduledDeparture >= :from
                                      AND f.scheduledDeparture < :to
                                    ORDER BY f.scheduledDeparture, f.flightId""",
                            Object[].class
                    )
                    .setParameter("from", today[0])
                    .setParameter("to", today[1])
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .hasSize(170)
                .extracting(r -> ((Flight) r[0]).getFlightId(),
                            r -> ((Flight) r[0]).getRouteNo(),
                            r -> ((Route) r[1]).getDepartureAirport(),
                            r -> ((Route) r[1]).getArrivalAirport(),
                            r -> ((Flight) r[0]).getScheduledDeparture(),
                            r -> ((Flight) r[0]).getStatus())
                .startsWith(
                        tuple(11048, "PG0098", "DEL", "CCU",
                              Instant.parse("2025-12-01T00:10:00Z"), FlightStatus.BOARDING),
                        tuple(11049, "PG0456", "KZN", "UFA",
                              Instant.parse("2025-12-01T00:15:00Z"), FlightStatus.BOARDING),
                        tuple(11050, "PG0215", "XIY", "SHA",
                              Instant.parse("2025-12-01T00:25:00Z"), FlightStatus.BOARDING),
                        tuple(11051, "PG0356", "XIY", "WUH",
                              Instant.parse("2025-12-01T00:30:00Z"), FlightStatus.DELAYED),
                        tuple(11052, "PG0191", "LAX", "LAS",
                              Instant.parse("2025-12-01T00:35:00Z"), FlightStatus.ON_TIME));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var today = today(em);
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var f = cq.from(Flight.class);
            final var r = f.join(Flight_.route);
            cq.select(cb.array(f, r))
                    .where(cb.greaterThanOrEqualTo(f.get(MappedFlight_.scheduledDeparture),
                                                   today[0]),
                           cb.lessThan(f.get(MappedFlight_.scheduledDeparture), today[1]))
                    .orderBy(cb.asc(f.get(MappedFlight_.scheduledDeparture)),
                             cb.asc(f.get(MappedFlight_.flightId)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .hasSize(170)
                .extracting(r -> ((Flight) r[0]).getFlightId(),
                            r -> ((Flight) r[0]).getRouteNo(),
                            r -> ((Route) r[1]).getDepartureAirport(),
                            r -> ((Route) r[1]).getArrivalAirport(),
                            r -> ((Flight) r[0]).getScheduledDeparture(),
                            r -> ((Flight) r[0]).getStatus())
                .startsWith(
                        tuple(11048, "PG0098", "DEL", "CCU",
                              Instant.parse("2025-12-01T00:10:00Z"), FlightStatus.BOARDING),
                        tuple(11049, "PG0456", "KZN", "UFA",
                              Instant.parse("2025-12-01T00:15:00Z"), FlightStatus.BOARDING),
                        tuple(11050, "PG0215", "XIY", "SHA",
                              Instant.parse("2025-12-01T00:25:00Z"), FlightStatus.BOARDING),
                        tuple(11051, "PG0356", "XIY", "WUH",
                              Instant.parse("2025-12-01T00:30:00Z"), FlightStatus.DELAYED),
                        tuple(11052, "PG0191", "LAX", "LAS",
                              Instant.parse("2025-12-01T00:35:00Z"), FlightStatus.ON_TIME));
    }

}
