package com.github.jinahya.postgresql.demodb.persistence;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link BoardingPass} against the schema generated into the in-memory
 * database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class BoardingPass_Persistence_Test
        extends _Entity_Persistence_Test<BoardingPass> {

    BoardingPass_Persistence_Test() {
        super(BoardingPass.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@code selectByFlightIdAndBoardingNo} named query finds a newly persisted
     * instance by its flight id and boarding number, a unique pair.
     *
     * @implNote The context is cleared after the flush, so the instance found is read from the
     *         database, not answered from the context. The boarding number is nullable, and
     *         randomized as {@code null} half the time; a pass without one cannot be found by it,
     *         {@code = NULL} matching no row, so one is given before the flush when it is missing.
     */
    @DisplayName("selectByFlightIdAndBoardingNo finds the persisted instance")
    @Test
    void selectByFlightIdAndBoardingNo__() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            if (v.getBoardingNo() == null) {
                v.setBoardingNo(__Random_TestUtils.positiveInt());
            }
            em.flush();
            em.clear();
            final var found = em.createNamedQuery("selectByFlightIdAndBoardingNo",
                                                  BoardingPass.class)
                    .setParameter("flightId", v.getFlightId())
                    .setParameter("boardingNo", v.getBoardingNo())
                    .getSingleResult();
            assertThat(found).as("found by flight id and boarding number of %s", v)
                    .isNotSameAs(v)
                    .isEqualTo(v);
            return found;
        });
    }

    /**
     * Verifies that the {@code selectByFlightIdAndSeatNo} named query finds a newly persisted
     * instance by its flight id and seat number, a unique pair.
     *
     * @implNote The context is cleared after the flush, so the instance found is read from the
     *         database, not answered from the context.
     */
    @DisplayName("selectByFlightIdAndSeatNo finds the persisted instance")
    @Test
    void selectByFlightIdAndSeatNo__() {
        applyNewPersistedEntityInstanceAndRollback((em, v) -> {
            em.flush();
            em.clear();
            final var found = em.createNamedQuery("selectByFlightIdAndSeatNo", BoardingPass.class)
                    .setParameter("flightId", v.getFlightId())
                    .setParameter("seatNo", v.getSeatNo())
                    .getSingleResult();
            assertThat(found).as("found by flight id and seat number of %s", v)
                    .isNotSameAs(v)
                    .isEqualTo(v);
            return found;
        });
    }
}
