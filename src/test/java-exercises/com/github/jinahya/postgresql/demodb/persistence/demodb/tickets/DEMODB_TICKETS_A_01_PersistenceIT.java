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
import com.github.jinahya.postgresql.demodb.persistence.Ticket_;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTicket_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code DEMODB_TICKETS_A_01} of {@code src/test/sql-exercises/tickets/03_advanced.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_TICKETS_A_01: how many passengers booked a return")
@Slf4j
class DEMODB_TICKETS_A_01_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var count = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT COUNT(o)
                                        FROM Ticket o
                                                 JOIN Ticket r ON r.booking = o.booking
                                            AND r.passengerId = o.passengerId
                                            AND r.outbound = FALSE
                                        WHERE o.outbound = TRUE""",
                                Long.class
                        )
                        .getSingleResult()
        );
        log.debug("count: {}", count);
        assertThat(count).isEqualTo(1168851L);
    }

    @Test
    void __CriteriaApi() {
        final var count = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Long.class);
            final var o = cq.from(Ticket.class);
            final var r = o.join(Ticket.class);
            r.on(cb.equal(r.get(Ticket_.booking), o.get(Ticket_.booking)),
                 cb.equal(r.get(MappedTicket_.passengerId), o.get(MappedTicket_.passengerId)),
                 cb.isFalse(r.get(MappedTicket_.outbound)));
            cq.select(cb.count(o))
                    .where(cb.isTrue(o.get(MappedTicket_.outbound)));
            return em.createQuery(cq)
                    .getSingleResult();
        });
        log.debug("count: {}", count);
        assertThat(count).isEqualTo(1168851L);
    }

}
