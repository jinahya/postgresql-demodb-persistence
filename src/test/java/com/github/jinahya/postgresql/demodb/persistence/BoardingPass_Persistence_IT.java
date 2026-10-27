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

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Verifies the mappings of {@link BoardingPass} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class BoardingPass_Persistence_IT
        extends _Entity_Persistence_IT<BoardingPass> {

    BoardingPass_Persistence_IT() {
        super(BoardingPass.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @implNote {@link BoardingPass} is mapped with an
     *         {@link jakarta.persistence.IdClass IdClass}, which the generic test does not
     *         assemble; this one builds the {@link BoardingPassId} from the ticket number and the
     *         flight id of the selected row.
     */
    @DisplayName("find reaches a randomly selected row once the context is cleared")
    @Test
    @Override
    void find_RandomlySelected_ContextCleared() {
        applyEntityManagerInTransactionAndRollback(em -> {
            final var optional = __Persistence_TestUtils.selectRandomEntity(em, targetClass);
            assumeTrue(optional.isPresent(), () -> "no row of " + targetClass + " is installed");
            final var selected = optional.get();
            final var identifier =
                    BoardingPassId.of(selected.getTicketNo(), selected.getFlightId());
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
     * @implNote The segment, a lazy association over the identifier, resolves to the one of
     *         the identifier.
     */
    @Override
    void randomTargetInstanceSelected(final BoardingPass instance) {
        assertThat(instance.getSegment()).as("segment of %s", instance).isNotNull().satisfies(s -> {
            assertThat(s.getTicketNo()).isEqualTo(instance.getTicketNo());
            assertThat(s.getFlightId()).isEqualTo(instance.getFlightId());
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@code selectByFlightIdAndBoardingNo} named query finds a randomly
     * selected row by its flight id and boarding number, a unique pair.
     *
     * @implNote The context is cleared after the selection, so the instance found is read
     *         from the database, not answered from the context. The boarding number is nullable,
     *         and a row without one cannot be found by it, {@code = NULL} matching no row; rows are
     *         drawn until one has it, a few times at most, for nearly all of them do.
     */
    @DisplayName("selectByFlightIdAndBoardingNo finds a randomly selected row")
    @Test
    void selectByFlightIdAndBoardingNo__() {
        applyEntityManagerInTransactionAndRollback(em -> {
            final var optional = Stream.generate(
                            () -> __Persistence_TestUtils.selectRandomEntity(em, targetClass))
                    .limit(10)
                    .flatMap(Optional::stream)
                    .filter(e -> e.getBoardingNo() != null)
                    .findFirst();
            assumeTrue(optional.isPresent(),
                       () -> "no row of " + targetClass + " with a boarding number was drawn");
            final var selected = optional.get();
            em.clear();
            final var found = em.createNamedQuery("selectByFlightIdAndBoardingNo",
                                                  BoardingPass.class)
                    .setParameter("flightId", selected.getFlightId())
                    .setParameter("boardingNo", selected.getBoardingNo())
                    .getSingleResult();
            assertThat(found).as("found by flight id and boarding number of %s", selected)
                    .isNotSameAs(selected)
                    .isEqualTo(selected);
            return null;
        });
    }

    /**
     * Verifies that the {@code selectByFlightIdAndSeatNo} named query finds a randomly selected
     * row by its flight id and seat number, a unique pair.
     *
     * @implNote The context is cleared after the selection, so the instance found is read
     *         from the database, not answered from the context.
     */
    @DisplayName("selectByFlightIdAndSeatNo finds a randomly selected row")
    @Test
    void selectByFlightIdAndSeatNo__() {
        applyEntityManagerInTransactionAndRollback(em -> {
            final var optional = __Persistence_TestUtils.selectRandomEntity(em, targetClass);
            assumeTrue(optional.isPresent(), () -> "no row of " + targetClass + " is installed");
            final var selected = optional.get();
            em.clear();
            final var found = em.createNamedQuery("selectByFlightIdAndSeatNo", BoardingPass.class)
                    .setParameter("flightId", selected.getFlightId())
                    .setParameter("seatNo", selected.getSeatNo())
                    .getSingleResult();
            assertThat(found).as("found by flight id and seat number of %s", selected)
                    .isNotSameAs(selected)
                    .isEqualTo(selected);
            return null;
        });
    }
}
