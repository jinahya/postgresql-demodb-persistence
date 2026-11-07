package com.github.jinahya.postgresql.demodb.persistence.demodb.tickets;

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

import com.github.jinahya.postgresql.demodb.persistence.Ticket;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTicket_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_TICKETS_B_01} of {@code src/test/sql-exercises/tickets/01_basic.sql} with
 * JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_TICKETS_B_01: the tickets of the booking 0000EG")
@Slf4j
class DEMODB_TICKETS_B_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT t
                                        FROM Ticket t
                                        WHERE t.bookRef = :bookRef
                                        ORDER BY t.ticketNo""",
                                Ticket.class
                        )
                        .setParameter("bookRef", "0000EG")
                        .getResultList()
        );
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Ticket::getTicketNo,
                            Ticket::getBookRef,
                            Ticket::getPassengerId,
                            Ticket::getPassengerName,
                            Ticket::getOutbound)
                .containsExactly(
                        tuple("0005434564147", "0000EG", "CA 1985281597520", "John Beland", true),
                        tuple("0005434564152", "0000EG", "CA 1985281597520", "John Beland", false));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Ticket.class);
            final var t = cq.from(Ticket.class);
            cq.select(t)
                    .where(cb.equal(t.get(MappedTicket_.bookRef), "0000EG"))
                    .orderBy(cb.asc(t.get(MappedTicket_.ticketNo)));
            return em.createQuery(cq)
                    .getResultList();
        });
        list.forEach(e -> log.debug("{}", e));
        assertThat(list)
                .extracting(Ticket::getTicketNo,
                            Ticket::getBookRef,
                            Ticket::getPassengerId,
                            Ticket::getPassengerName,
                            Ticket::getOutbound)
                .containsExactly(
                        tuple("0005434564147", "0000EG", "CA 1985281597520", "John Beland", true),
                        tuple("0005434564152", "0000EG", "CA 1985281597520", "John Beland", false));
    }

}
