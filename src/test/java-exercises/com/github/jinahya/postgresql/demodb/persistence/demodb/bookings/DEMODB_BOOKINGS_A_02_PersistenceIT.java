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
import com.github.jinahya.postgresql.demodb.persistence.Ticket;
import com.github.jinahya.postgresql.demodb.persistence.Ticket_;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBooking_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_BOOKINGS_A_02} of {@code src/test/sql-exercises/bookings/03_advanced.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_BOOKINGS_A_02: the five bookings with the most tickets")
@Slf4j
class DEMODB_BOOKINGS_A_02_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT b, COUNT(t)
                                        FROM Ticket t
                                                 JOIN t.booking b
                                        GROUP BY b
                                        ORDER BY COUNT(t) DESC, b.bookRef""",
                                Object[].class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Booking) r[0]).getBookRef(),
                            r -> ((Booking) r[0]).getBookDate(),
                            r -> ((Booking) r[0]).getTotalAmount(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("033CTO", Instant.parse("2025-09-07T01:15:43.363826Z"),
                              new BigDecimal("87500.00"), 10L),
                        tuple("1OP3KI", Instant.parse("2025-10-14T22:12:29.76249Z"),
                              new BigDecimal("114000.00"), 10L),
                        tuple("25XXZL", Instant.parse("2025-09-08T06:49:23.980094Z"),
                              new BigDecimal("72500.00"), 10L),
                        tuple("3WXRTI", Instant.parse("2025-09-11T03:13:41.930626Z"),
                              new BigDecimal("36725.00"), 10L),
                        tuple("49Z3NQ", Instant.parse("2025-11-03T11:03:13.2549Z"),
                              new BigDecimal("86100.00"), 10L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var t = cq.from(Ticket.class);
            final var b = t.join(Ticket_.booking);
            cq.select(cb.array(b, cb.count(t)))
                    .groupBy(b)
                    .orderBy(cb.desc(cb.count(t)), cb.asc(b.get(MappedBooking_.bookRef)));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Booking) r[0]).getBookRef(),
                            r -> ((Booking) r[0]).getBookDate(),
                            r -> ((Booking) r[0]).getTotalAmount(),
                            r -> ((Number) r[1]).longValue())
                .containsExactly(
                        tuple("033CTO", Instant.parse("2025-09-07T01:15:43.363826Z"),
                              new BigDecimal("87500.00"), 10L),
                        tuple("1OP3KI", Instant.parse("2025-10-14T22:12:29.76249Z"),
                              new BigDecimal("114000.00"), 10L),
                        tuple("25XXZL", Instant.parse("2025-09-08T06:49:23.980094Z"),
                              new BigDecimal("72500.00"), 10L),
                        tuple("3WXRTI", Instant.parse("2025-09-11T03:13:41.930626Z"),
                              new BigDecimal("36725.00"), 10L),
                        tuple("49Z3NQ", Instant.parse("2025-11-03T11:03:13.2549Z"),
                              new BigDecimal("86100.00"), 10L));
    }

}
