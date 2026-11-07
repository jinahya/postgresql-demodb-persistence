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
 * Answers {@code DEMODB_BOOKINGS_B_03} of {@code src/test/sql-exercises/bookings/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_BOOKINGS_B_03: the bookings of 700000 or more")
@Slf4j
class DEMODB_BOOKINGS_B_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT b
                                        FROM Booking b
                                        WHERE b.totalAmount >= :totalAmount
                                        ORDER BY b.totalAmount DESC, b.bookRef""",
                                Booking.class
                        )
                        .setParameter("totalAmount", new BigDecimal("700000"))
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Booking::getBookRef, Booking::getBookDate, Booking::getTotalAmount)
                .containsExactly(
                        tuple("7UU4BE", Instant.parse("2025-09-20T04:54:14.10585Z"),
                              new BigDecimal("915250.00")),
                        tuple("3OSUL8", Instant.parse("2025-10-13T09:23:52.261287Z"),
                              new BigDecimal("741100.00")),
                        tuple("613WY4", Instant.parse("2025-10-14T03:44:02.154647Z"),
                              new BigDecimal("717450.00")));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Booking.class);
            final var b = cq.from(Booking.class);
            final var totalAmount = b.get(MappedBooking_.totalAmount);
            cq.select(b)
                    .where(cb.greaterThanOrEqualTo(totalAmount, new BigDecimal("700000")))
                    .orderBy(cb.desc(totalAmount), cb.asc(b.get(MappedBooking_.bookRef)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Booking::getBookRef, Booking::getBookDate, Booking::getTotalAmount)
                .containsExactly(
                        tuple("7UU4BE", Instant.parse("2025-09-20T04:54:14.10585Z"),
                              new BigDecimal("915250.00")),
                        tuple("3OSUL8", Instant.parse("2025-10-13T09:23:52.261287Z"),
                              new BigDecimal("741100.00")),
                        tuple("613WY4", Instant.parse("2025-10-14T03:44:02.154647Z"),
                              new BigDecimal("717450.00")));
    }

}
