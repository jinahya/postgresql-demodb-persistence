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

import com.github.jinahya.postgresql.demodb.persistence.Booking;
import com.github.jinahya.postgresql.demodb.persistence.Ticket;
import com.github.jinahya.postgresql.demodb.persistence.Ticket_;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBooking_;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTicket_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_SCHEMA_I_10} of {@code src/test/sql-exercises/__/02_intermediate.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SCHEMA_I_10: the outbound and return tickets of one passenger in one booking")
@Slf4j
class DEMODB_SCHEMA_I_10_PersistenceIT {

    // The answer is 1168851 rows, as many as DEMODB_TICKETS_A_01 counts; the test reads the first
    // ten of them, with setMaxResults, rather than load them all.
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT b, o, i
                                        FROM Booking b
                                                 JOIN Ticket o ON o.booking = b AND o.outbound = TRUE
                                                 JOIN Ticket i ON i.booking = o.booking
                                            AND i.passengerId = o.passengerId
                                            AND i.outbound = FALSE
                                        ORDER BY b.bookRef, o.passengerId""",
                                Object[].class
                        )
                        .setMaxResults(10)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Booking) r[0]).getBookRef(),
                            r -> ((Booking) r[0]).getBookDate(),
                            r -> ((Ticket) r[1]).getPassengerId(),
                            r -> ((Ticket) r[1]).getTicketNo(),
                            r -> ((Ticket) r[2]).getTicketNo())
                .containsExactly(
                        tuple("0000EG", Instant.parse("2025-11-16T21:21:48.411495Z"),
                              "CA 1985281597520", "0005434564147", "0005434564152"),
                        tuple("00040W", Instant.parse("2025-10-13T09:50:50.314587Z"),
                              "IN 2849974177127", "0005433488169", "0005433488174"),
                        tuple("000562", Instant.parse("2025-09-11T10:21:54.084444Z"),
                              "CN 8124086130640", "0005432379163", "0005432379166"),
                        tuple("000562", Instant.parse("2025-09-11T10:21:54.084444Z"),
                              "CN 8278780386895", "0005432379152", "0005432379162"),
                        tuple("000AB9", Instant.parse("2025-09-14T09:38:24.862062Z"),
                              "FR 9136453193331", "0005432484130", "0005432484139"),
                        tuple("000CTL", Instant.parse("2025-09-12T03:00:50.050639Z"),
                              "CA 1858375121919", "0005432403569", "0005432403579"),
                        tuple("000CTL", Instant.parse("2025-09-12T03:00:50.050639Z"),
                              "CA 4214750109906", "0005432403583", "0005432403584"),
                        tuple("000FHR", Instant.parse("2025-09-20T01:23:24.977241Z"),
                              "US 6852803198732", "0005432684628", "0005432684637"),
                        tuple("000J03", Instant.parse("2025-09-21T10:36:16.247585Z"),
                              "DE 5115949846908", "0005432733791", "0005432733800"),
                        tuple("000J03", Instant.parse("2025-09-21T10:36:16.247585Z"),
                              "DE 9841850206654", "0005432733806", "0005432733808"));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var b = cq.from(Booking.class);
            final var o = b.join(Ticket.class);
            o.on(cb.equal(o.get(Ticket_.booking), b),
                 cb.isTrue(o.get(MappedTicket_.outbound)));
            final var i = b.join(Ticket.class);
            i.on(cb.equal(i.get(Ticket_.booking), o.get(Ticket_.booking)),
                 cb.equal(i.get(MappedTicket_.passengerId), o.get(MappedTicket_.passengerId)),
                 cb.isFalse(i.get(MappedTicket_.outbound)));
            cq.select(cb.array(b, o, i))
                    .orderBy(cb.asc(b.get(MappedBooking_.bookRef)),
                             cb.asc(o.get(MappedTicket_.passengerId)));
            return em.createQuery(cq)
                    .setMaxResults(10)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> ((Booking) r[0]).getBookRef(),
                            r -> ((Booking) r[0]).getBookDate(),
                            r -> ((Ticket) r[1]).getPassengerId(),
                            r -> ((Ticket) r[1]).getTicketNo(),
                            r -> ((Ticket) r[2]).getTicketNo())
                .containsExactly(
                        tuple("0000EG", Instant.parse("2025-11-16T21:21:48.411495Z"),
                              "CA 1985281597520", "0005434564147", "0005434564152"),
                        tuple("00040W", Instant.parse("2025-10-13T09:50:50.314587Z"),
                              "IN 2849974177127", "0005433488169", "0005433488174"),
                        tuple("000562", Instant.parse("2025-09-11T10:21:54.084444Z"),
                              "CN 8124086130640", "0005432379163", "0005432379166"),
                        tuple("000562", Instant.parse("2025-09-11T10:21:54.084444Z"),
                              "CN 8278780386895", "0005432379152", "0005432379162"),
                        tuple("000AB9", Instant.parse("2025-09-14T09:38:24.862062Z"),
                              "FR 9136453193331", "0005432484130", "0005432484139"),
                        tuple("000CTL", Instant.parse("2025-09-12T03:00:50.050639Z"),
                              "CA 1858375121919", "0005432403569", "0005432403579"),
                        tuple("000CTL", Instant.parse("2025-09-12T03:00:50.050639Z"),
                              "CA 4214750109906", "0005432403583", "0005432403584"),
                        tuple("000FHR", Instant.parse("2025-09-20T01:23:24.977241Z"),
                              "US 6852803198732", "0005432684628", "0005432684637"),
                        tuple("000J03", Instant.parse("2025-09-21T10:36:16.247585Z"),
                              "DE 5115949846908", "0005432733791", "0005432733800"),
                        tuple("000J03", Instant.parse("2025-09-21T10:36:16.247585Z"),
                              "DE 9841850206654", "0005432733806", "0005432733808"));
    }

}
