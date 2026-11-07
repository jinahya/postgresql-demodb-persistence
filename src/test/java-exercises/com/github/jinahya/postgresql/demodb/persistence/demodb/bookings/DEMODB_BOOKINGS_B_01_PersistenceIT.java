package com.github.jinahya.postgresql.demodb.persistence.demodb.bookings;

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

import com.github.jinahya.postgresql.demodb.persistence.Booking;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBooking_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_BOOKINGS_B_01} of {@code src/test/sql-exercises/bookings/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_BOOKINGS_B_01: the bookings of the first minute")
@Slf4j
class DEMODB_BOOKINGS_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT b
                                        FROM Booking b
                                        WHERE b.bookDate >= :from
                                          AND b.bookDate < :to
                                        ORDER BY b.bookDate, b.bookRef""",
                                Booking.class
                        )
                        .setParameter("from", Instant.parse("2025-09-01T00:00:00Z"))
                        .setParameter("to", Instant.parse("2025-09-01T00:01:00Z"))
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 17 rows; the first ten
        assertThat(list)
                .hasSize(17)
                .extracting(Booking::getBookRef, Booking::getBookDate, Booking::getTotalAmount)
                .startsWith(
                        tuple("6TAS39", Instant.parse("2025-09-01T00:00:06.265219Z"),
                              new BigDecimal("5000.00")),
                        tuple("GGY5EU", Instant.parse("2025-09-01T00:00:07.565193Z"),
                              new BigDecimal("5750.00")),
                        tuple("Y4KNLB", Instant.parse("2025-09-01T00:00:07.980879Z"),
                              new BigDecimal("18000.00")),
                        tuple("MJUZ2D", Instant.parse("2025-09-01T00:00:10.850077Z"),
                              new BigDecimal("5000.00")),
                        tuple("4O2NMP", Instant.parse("2025-09-01T00:00:12.040076Z"),
                              new BigDecimal("27500.00")),
                        tuple("2EW1SQ", Instant.parse("2025-09-01T00:00:12.557744Z"),
                              new BigDecimal("8125.00")),
                        tuple("KP3OIQ", Instant.parse("2025-09-01T00:00:15.012997Z"),
                              new BigDecimal("15000.00")),
                        tuple("H40481", Instant.parse("2025-09-01T00:00:17.28616Z"),
                              new BigDecimal("29000.00")),
                        tuple("KOS1KJ", Instant.parse("2025-09-01T00:00:18.970418Z"),
                              new BigDecimal("21500.00")),
                        tuple("NWE9DB", Instant.parse("2025-09-01T00:00:20.274286Z"),
                              new BigDecimal("41250.00")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Booking.class);
            final var b = cq.from(Booking.class);
            final var bookDate = b.get(MappedBooking_.bookDate);
            cq.select(b)
                    .where(cb.greaterThanOrEqualTo(bookDate, Instant.parse("2025-09-01T00:00:00Z")),
                           cb.lessThan(bookDate, Instant.parse("2025-09-01T00:01:00Z")))
                    .orderBy(cb.asc(bookDate), cb.asc(b.get(MappedBooking_.bookRef)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 17 rows; the first ten
        assertThat(list)
                .hasSize(17)
                .extracting(Booking::getBookRef, Booking::getBookDate, Booking::getTotalAmount)
                .startsWith(
                        tuple("6TAS39", Instant.parse("2025-09-01T00:00:06.265219Z"),
                              new BigDecimal("5000.00")),
                        tuple("GGY5EU", Instant.parse("2025-09-01T00:00:07.565193Z"),
                              new BigDecimal("5750.00")),
                        tuple("Y4KNLB", Instant.parse("2025-09-01T00:00:07.980879Z"),
                              new BigDecimal("18000.00")),
                        tuple("MJUZ2D", Instant.parse("2025-09-01T00:00:10.850077Z"),
                              new BigDecimal("5000.00")),
                        tuple("4O2NMP", Instant.parse("2025-09-01T00:00:12.040076Z"),
                              new BigDecimal("27500.00")),
                        tuple("2EW1SQ", Instant.parse("2025-09-01T00:00:12.557744Z"),
                              new BigDecimal("8125.00")),
                        tuple("KP3OIQ", Instant.parse("2025-09-01T00:00:15.012997Z"),
                              new BigDecimal("15000.00")),
                        tuple("H40481", Instant.parse("2025-09-01T00:00:17.28616Z"),
                              new BigDecimal("29000.00")),
                        tuple("KOS1KJ", Instant.parse("2025-09-01T00:00:18.970418Z"),
                              new BigDecimal("21500.00")),
                        tuple("NWE9DB", Instant.parse("2025-09-01T00:00:20.274286Z"),
                              new BigDecimal("41250.00")));
    }

}
