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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Answers {@code DEMODB_TICKETS_I_01} of {@code src/test/sql-exercises/tickets/02_intermediate.sql}
 * with JPQL and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_TICKETS_I_01: the five passengers with the most tickets")
@Slf4j
class DEMODB_TICKETS_I_01_PersistenceIT {

    // a passenger is not an entity, so the query groups by the two columns, as the SQL does
    @Test
    void __QueryLanguage() {
        final var list = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT t.passengerId, t.passengerName, COUNT(t)
                                        FROM Ticket t
                                        GROUP BY t.passengerId, t.passengerName
                                        ORDER BY COUNT(t) DESC, t.passengerId""",
                                Object[].class
                        )
                        .setMaxResults(5)
                        .getResultList()
        );
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple("CN 1303160118058", "Haiyun Yang", 5L),
                        tuple("CN 8458154433091", "Haiyun Zhao", 5L),
                        tuple("US 2995168528581", "William Thompson", 5L),
                        tuple("CA 0307652121299", "Pascal Lang", 4L),
                        tuple("CA 0314814108732", "Dawn Dupuis", 4L));
    }

    @Test
    void __CriteriaApi() {
        final var list = __Exercises_TestUtils.apply(em -> {
            final var cb = em.getCriteriaBuilder();
            final var cq = cb.createQuery(Object[].class);
            final var t = cq.from(Ticket.class);
            final var passengerId = t.get(MappedTicket_.passengerId);
            final var passengerName = t.get(MappedTicket_.passengerName);
            cq.select(cb.array(passengerId, passengerName, cb.count(t)))
                    .groupBy(passengerId, passengerName)
                    .orderBy(cb.desc(cb.count(t)), cb.asc(passengerId));
            return em.createQuery(cq)
                    .setMaxResults(5)
                    .getResultList();
        });
        list.forEach(r -> log.debug("{}", Arrays.toString(r)));
        assertThat(list)
                .extracting(r -> r[0], r -> r[1], r -> ((Number) r[2]).longValue())
                .containsExactly(
                        tuple("CN 1303160118058", "Haiyun Yang", 5L),
                        tuple("CN 8458154433091", "Haiyun Zhao", 5L),
                        tuple("US 2995168528581", "William Thompson", 5L),
                        tuple("CA 0307652121299", "Pascal Lang", 4L),
                        tuple("CA 0314814108732", "Dawn Dupuis", 4L));
    }

}
