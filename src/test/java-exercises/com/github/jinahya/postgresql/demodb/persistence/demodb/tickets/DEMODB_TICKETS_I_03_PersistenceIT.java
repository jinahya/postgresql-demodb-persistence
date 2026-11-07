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

import com.github.jinahya.postgresql.demodb.persistence.Segment;
import com.github.jinahya.postgresql.demodb.persistence.Segment_;
import com.github.jinahya.postgresql.demodb.persistence.Ticket;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code DEMODB_TICKETS_I_03} of {@code src/test/sql-exercises/tickets/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_TICKETS_I_03: how many tickets have no segment")
@Slf4j
class DEMODB_TICKETS_I_03_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var count = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT COUNT(t)
                                        FROM Ticket t
                                        WHERE NOT EXISTS (SELECT s FROM Segment s WHERE s.ticket = t)""",
                                Long.class
                        )
                        .getSingleResult()
        );
        log.debug("count: {}", count);
        assertThat(count).isZero();
    }

    @Test
    void __CriteriaApi() {
        final var count = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Long.class);
            final var t = cq.from(Ticket.class);
            final var sq = cq.subquery(Segment.class);
            final var s = sq.from(Segment.class);
            sq.select(s)
                    .where(cb.equal(s.get(Segment_.ticket), t));
            cq.select(cb.count(t))
                    .where(cb.not(cb.exists(sq)));
            return em.createQuery(cq)
                    .getSingleResult();
        });
        log.debug("count: {}", count);
        assertThat(count).isZero();
    }

}
