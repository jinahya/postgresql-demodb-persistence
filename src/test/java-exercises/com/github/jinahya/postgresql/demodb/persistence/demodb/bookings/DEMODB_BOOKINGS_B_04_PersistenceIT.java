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
 * Answers {@code DEMODB_BOOKINGS_B_04} of {@code src/test/sql-exercises/bookings/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_BOOKINGS_B_04: the second page of the bookings of 2025-10-01")
@Slf4j
class DEMODB_BOOKINGS_B_04_PersistenceIT {

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
                        .setParameter("from", Instant.parse("2025-10-01T00:00:00Z"))
                        .setParameter("to", Instant.parse("2025-10-02T00:00:00Z"))
                        .setFirstResult(20)
                        .setMaxResults(20)
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        // 20 rows; the first ten
        assertThat(list)
                .hasSize(20)
                .extracting(Booking::getBookRef, Booking::getBookDate, Booking::getTotalAmount)
                .startsWith(
                        tuple("0GLNZI", Instant.parse("2025-10-01T00:01:30.918125Z"),
                              new BigDecimal("12075.00")),
                        tuple("M9ZJIV", Instant.parse("2025-10-01T00:01:34.386935Z"),
                              new BigDecimal("9750.00")),
                        tuple("WU45PM", Instant.parse("2025-10-01T00:01:35.027927Z"),
                              new BigDecimal("2500.00")),
                        tuple("2L26GJ", Instant.parse("2025-10-01T00:01:36.898209Z"),
                              new BigDecimal("12250.00")),
                        tuple("Z5FA7T", Instant.parse("2025-10-01T00:01:44.870713Z"),
                              new BigDecimal("33500.00")),
                        tuple("04UWDM", Instant.parse("2025-10-01T00:01:48.064918Z"),
                              new BigDecimal("47750.00")),
                        tuple("5SODYM", Instant.parse("2025-10-01T00:01:52.593366Z"),
                              new BigDecimal("44000.00")),
                        tuple("6C54DM", Instant.parse("2025-10-01T00:02:09.186487Z"),
                              new BigDecimal("46850.00")),
                        tuple("7VE52V", Instant.parse("2025-10-01T00:02:10.809042Z"),
                              new BigDecimal("1750.00")),
                        tuple("I32M88", Instant.parse("2025-10-01T00:02:17.050868Z"),
                              new BigDecimal("30500.00")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Booking.class);
            final var b = cq.from(Booking.class);
            final var bookDate = b.get(MappedBooking_.bookDate);
            cq.select(b)
                    .where(cb.greaterThanOrEqualTo(bookDate, Instant.parse("2025-10-01T00:00:00Z")),
                           cb.lessThan(bookDate, Instant.parse("2025-10-02T00:00:00Z")))
                    .orderBy(cb.asc(bookDate), cb.asc(b.get(MappedBooking_.bookRef)));
            return em.createQuery(cq)
                    .setFirstResult(20)
                    .setMaxResults(20)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        // 20 rows; the first ten
        assertThat(list)
                .hasSize(20)
                .extracting(Booking::getBookRef, Booking::getBookDate, Booking::getTotalAmount)
                .startsWith(
                        tuple("0GLNZI", Instant.parse("2025-10-01T00:01:30.918125Z"),
                              new BigDecimal("12075.00")),
                        tuple("M9ZJIV", Instant.parse("2025-10-01T00:01:34.386935Z"),
                              new BigDecimal("9750.00")),
                        tuple("WU45PM", Instant.parse("2025-10-01T00:01:35.027927Z"),
                              new BigDecimal("2500.00")),
                        tuple("2L26GJ", Instant.parse("2025-10-01T00:01:36.898209Z"),
                              new BigDecimal("12250.00")),
                        tuple("Z5FA7T", Instant.parse("2025-10-01T00:01:44.870713Z"),
                              new BigDecimal("33500.00")),
                        tuple("04UWDM", Instant.parse("2025-10-01T00:01:48.064918Z"),
                              new BigDecimal("47750.00")),
                        tuple("5SODYM", Instant.parse("2025-10-01T00:01:52.593366Z"),
                              new BigDecimal("44000.00")),
                        tuple("6C54DM", Instant.parse("2025-10-01T00:02:09.186487Z"),
                              new BigDecimal("46850.00")),
                        tuple("7VE52V", Instant.parse("2025-10-01T00:02:10.809042Z"),
                              new BigDecimal("1750.00")),
                        tuple("I32M88", Instant.parse("2025-10-01T00:02:17.050868Z"),
                              new BigDecimal("30500.00")));
    }

}
