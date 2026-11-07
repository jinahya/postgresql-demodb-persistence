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

import com.github.jinahya.postgresql.demodb.persistence.Segment;
import com.github.jinahya.postgresql.demodb.persistence.Segment_;
import com.github.jinahya.postgresql.demodb.persistence.__Exercises_TestUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight_;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Answers {@code DEMODB_SCHEMA_B_06} of {@code src/test/sql-exercises/__/01_basic.sql} with JPQL
 * and with the Criteria API, or with a native query where neither can.
 */
@DisplayName("DEMODB_SCHEMA_B_06: the sold segments of the cancelled flights")
@Slf4j
class DEMODB_SCHEMA_B_06_PersistenceIT {

    @Test
    void __QueryLanguage() {
        final var count = __Exercises_TestUtils.apply(
                em -> em.createQuery(
                                """
                                        SELECT COUNT(s)
                                        FROM Segment s
                                                 JOIN s.flight f
                                        WHERE f.status = :status""",
                                Long.class
                        )
                        .setParameter("status", FlightStatus.CANCELLED)
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
            final var s = cq.from(Segment.class);
            final var f = s.join(Segment_.flight);
            cq.select(cb.count(s))
                    .where(cb.equal(f.get(MappedFlight_.status), FlightStatus.CANCELLED));
            return em.createQuery(cq)
                    .getSingleResult();
        });
        log.debug("count: {}", count);
        assertThat(count).isZero();
    }

}
