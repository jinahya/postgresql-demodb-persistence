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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Verifies the mappings of {@link Seat} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Seat_Persistence_IT
        extends _Entity_Persistence_IT<Seat> {

    Seat_Persistence_IT() {
        super(Seat.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Seat} is mapped with an {@link jakarta.persistence.IdClass IdClass},
     *         which the generic test does not assemble; this one builds the {@link SeatId} from the
     *         airplane code and the seat number of the selected row.
     */
    @DisplayName("find reaches a randomly selected row once the context is cleared")
    @Test
    @Override
    void find_RandomlySelected_ContextCleared() {
        applyEntityManagerInTransactionAndRollback(em -> {
            final var optional = __Persistence_TestUtils.selectRandomEntity(em, targetClass);
            assumeTrue(optional.isPresent(), () -> "no row of " + targetClass + " is installed");
            final var selected = optional.get();
            final var identifier = SeatId.of(selected.getAirplaneCode(), selected.getSeatNo());
            em.clear();
            final var found = em.find(targetClass, identifier);
            assertThat(found)
                    .as("the %s found by %s, from the database", targetClass.getSimpleName(),
                        identifier)
                    .isNotNull()
                    .isNotSameAs(selected)
                    .isEqualTo(selected);
            return null;
        });
    }

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The airplane, a lazy association over the identifier, resolves to the
     *         airplane of the code.
     */
    @Override
    void randomTargetInstanceSelected(final Seat instance) {
        assertThat(instance.getAirplaneData()).as("airplane of %s", instance).isNotNull()
                .extracting(AirplaneData::getAirplaneCode).isEqualTo(instance.getAirplaneCode());
    }
}
