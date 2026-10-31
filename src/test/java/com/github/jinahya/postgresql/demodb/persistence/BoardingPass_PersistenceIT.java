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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSegmentId_;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.github.jinahya.persistence.util.JinahyaPersistenceUtils.applyEntityManager;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link BoardingPass} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class BoardingPass_PersistenceIT
        extends _Entity_PersistenceIT<BoardingPass> {

    BoardingPass_PersistenceIT() {
        super(BoardingPass.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param entityManager  {@inheritDoc}
     * @param entityInstance {@inheritDoc}
     * @implNote The pass, found again, is of a seat of the airplane of its flight's route,
     *         which its persister made, of the segment's fare conditions.
     */
    @Override
    void targetInstancePersisted(final EntityManager entityManager,
                                 final BoardingPass entityInstance) {
        entityManager.clear();
        final var found = entityManager.find(BoardingPass.class, entityInstance.getId());
        final var segment = found.getSegment();
        final var route = segment.getFlight().getRoute();
        assertThat(route).as("route of the flight of %s", found).isNotNull();
        final var seat = entityManager.find(
                Seat.class, SeatId.of(route.getAirplaneCode(), found.getSeatNo()));
        assertThat(seat).as("seat of %s, on the airplane of its flight's route", found)
                .isNotNull()
                .extracting(Seat::getFareConditions)
                .isEqualTo(segment.getFareConditions());
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
     * The flight the named queries are tested with, one of the installed {@code 3m} edition with
     * {@value #COUNT_OF_FLIGHT} boarding passes, three of them without a boarding number.
     */
    private static final int FLIGHT_ID = 8767;

    /**
     * The number of the boarding passes of {@value #FLIGHT_ID}.
     */
    private static final int COUNT_OF_FLIGHT = 15;

    /**
     * The ticket number of the pass of boarding number {@code 1}, seat {@code 9C}, of
     * {@value #FLIGHT_ID}.
     */
    private static final String TICKET_NO_OF_BOARDING_NO_1 = "0005434546074";

    /**
     * The boarding numbers of {@value #FLIGHT_ID}, in an ascending order, the three {@code NULL}s
     * last.
     */
    private static final List<Integer> BOARDING_NOS_ASC =
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, null, null, null);

    /**
     * The seat numbers of {@value #FLIGHT_ID}, in an ascending order of the text.
     */
    private static final List<String> SEAT_NOS_ASC = List.of(
            "13C", "13D", "14A", "14B", "15A", "15C", "16A", "17A", "19B", "21A", "21C", "7C", "7D",
            "8B", "9C");

    /**
     * Tests the {@code selectByFlightIdAndBoardingNo} named query.
     */
    @DisplayName("selectByFlightIdAndBoardingNo")
    @Nested
    class SelectByFlightIdAndBoardingNo_IT {

        /**
         * Verifies that the named query finds the pass of boarding number {@code 1} of the flight,
         * the one of ticket {@code 0005434546074}, seat {@code 9C}.
         */
        @DisplayName("finds the pass of boarding number 1, by the named query")
        @Test
        void __NamedQuery() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var found = em.createNamedQuery("selectByFlightIdAndBoardingNo",
                                                      BoardingPass.class)
                        .setParameter("flightId", FLIGHT_ID)
                        .setParameter("boardingNo", 1)
                        .getSingleResult();
                assertThat(found).as("pass of boarding number 1 of flight %s", FLIGHT_ID)
                        .satisfies(p -> {
                            assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID);
                            assertThat(p.getTicketNo()).isEqualTo(TICKET_NO_OF_BOARDING_NO_1);
                            assertThat(p.getSeatNo()).isEqualTo("9C");
                        });
                return null;
            });
        }

        /**
         * Verifies that the same query, written in the Jakarta Persistence Query Language, finds
         * the pass of boarding number {@code 1} of the flight, the one of ticket
         * {@code 0005434546074}, seat {@code 9C}.
         */
        @DisplayName("finds the pass of boarding number 1, by the query language")
        @Test
        void __QueryLanguage() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var found = em.createQuery(
                                """
                                        SELECT e
                                        FROM BoardingPass AS e
                                        WHERE e.id.flightId = :flightId
                                          AND e.boardingNo = :boardingNo""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", FLIGHT_ID)
                        .setParameter("boardingNo", 1)
                        .getSingleResult();
                assertThat(found).as("pass of boarding number 1 of flight %s", FLIGHT_ID)
                        .satisfies(p -> {
                            assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID);
                            assertThat(p.getTicketNo()).isEqualTo(TICKET_NO_OF_BOARDING_NO_1);
                            assertThat(p.getSeatNo()).isEqualTo("9C");
                        });
                return null;
            });
        }

        /**
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * finds the pass of boarding number {@code 1} of the flight, the one of ticket
         * {@code 0005434546074}, seat {@code 9C}.
         */
        @DisplayName("finds the pass of boarding number 1, by the Criteria API")
        @Test
        void __CriteriaApi() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root).where(
                        builder.equal(
                                root.get(BoardingPass_.id).get(MappedSegmentId_.flightId),
                                FLIGHT_ID),
                        builder.equal(root.get(BoardingPass_.boardingNo), 1));
                final var found = em.createQuery(query).getSingleResult();
                assertThat(found).as("pass of boarding number 1 of flight %s", FLIGHT_ID)
                        .satisfies(p -> {
                            assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID);
                            assertThat(p.getTicketNo()).isEqualTo(TICKET_NO_OF_BOARDING_NO_1);
                            assertThat(p.getSeatNo()).isEqualTo("9C");
                        });
                return null;
            });
        }
    }

    /**
     * Tests the {@code selectByFlightIdAndSeatNo} named query.
     */
    @DisplayName("selectByFlightIdAndSeatNo")
    @Nested
    class SelectByFlightIdAndSeatNo_IT {

        /**
         * Verifies that the named query finds the pass of seat {@code 9C} of the flight, the one of
         * ticket {@code 0005434546074}, boarding number {@code 1}.
         */
        @DisplayName("finds the pass of seat 9C, by the named query")
        @Test
        void __NamedQuery() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var found = em.createNamedQuery("selectByFlightIdAndSeatNo",
                                                      BoardingPass.class)
                        .setParameter("flightId", FLIGHT_ID)
                        .setParameter("seatNo", "9C")
                        .getSingleResult();
                assertThat(found).as("pass of seat 9C of flight %s", FLIGHT_ID)
                        .satisfies(p -> {
                            assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID);
                            assertThat(p.getTicketNo()).isEqualTo(TICKET_NO_OF_BOARDING_NO_1);
                            assertThat(p.getBoardingNo()).isEqualTo(1);
                        });
                return null;
            });
        }

        /**
         * Verifies that the same query, written in the Jakarta Persistence Query Language, finds
         * the pass of seat {@code 9C} of the flight, the one of ticket {@code 0005434546074},
         * boarding number {@code 1}.
         */
        @DisplayName("finds the pass of seat 9C, by the query language")
        @Test
        void __QueryLanguage() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var found = em.createQuery(
                                """
                                        SELECT e
                                        FROM BoardingPass AS e
                                        WHERE e.id.flightId = :flightId
                                          AND e.seatNo = :seatNo""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", FLIGHT_ID)
                        .setParameter("seatNo", "9C")
                        .getSingleResult();
                assertThat(found).as("pass of seat 9C of flight %s", FLIGHT_ID)
                        .satisfies(p -> {
                            assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID);
                            assertThat(p.getTicketNo()).isEqualTo(TICKET_NO_OF_BOARDING_NO_1);
                            assertThat(p.getBoardingNo()).isEqualTo(1);
                        });
                return null;
            });
        }

        /**
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * finds the pass of seat {@code 9C} of the flight, the one of ticket {@code 0005434546074},
         * boarding number {@code 1}.
         */
        @DisplayName("finds the pass of seat 9C, by the Criteria API")
        @Test
        void __CriteriaApi() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root).where(
                        builder.equal(
                                root.get(BoardingPass_.id).get(MappedSegmentId_.flightId),
                                FLIGHT_ID),
                        builder.equal(root.get(BoardingPass_.seatNo), "9C"));
                final var found = em.createQuery(query).getSingleResult();
                assertThat(found).as("pass of seat 9C of flight %s", FLIGHT_ID)
                        .satisfies(p -> {
                            assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID);
                            assertThat(p.getTicketNo()).isEqualTo(TICKET_NO_OF_BOARDING_NO_1);
                            assertThat(p.getBoardingNo()).isEqualTo(1);
                        });
                return null;
            });
        }
    }

    /**
     * Tests the {@code selectAllByFlightIdOrderByBoardingNoAac} named query.
     */
    @DisplayName("selectAllByFlightIdOrderByBoardingNoAac")
    @Nested
    class SelectAllByFlightIdOrderByBoardingNo_IT {

        /**
         * Verifies that the named query selects the 15 passes of the flight, by boarding number,
         * {@code 1} to {@code 12} and then the three without one, as PostgreSQL orders a
         * {@code NULL} last in an ascending order.
         */
        @DisplayName("selects all, by boarding number, nulls last, by the named query")
        @Test
        void __NamedQuery() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var all = em.createNamedQuery("selectAllByFlightIdOrderByBoardingNoAac",
                                                    BoardingPass.class)
                        .setParameter("flightId", FLIGHT_ID)
                        .getResultList();
                assertThat(all).as("passes of flight %s", FLIGHT_ID)
                        .hasSize(COUNT_OF_FLIGHT)
                        .allSatisfy(p -> assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID));
                assertThat(all).extracting(BoardingPass::getBoardingNo)
                        .as("boarding numbers of flight %s", FLIGHT_ID)
                        .containsExactlyElementsOf(BOARDING_NOS_ASC);
                return null;
            });
        }

        /**
         * Verifies that the same query, written in the Jakarta Persistence Query Language, selects
         * the 15 passes of the flight, by boarding number, {@code 1} to {@code 12} and then the
         * three without one, as PostgreSQL orders a {@code NULL} last in an ascending order.
         */
        @DisplayName("selects all, by boarding number, nulls last, by the query language")
        @Test
        void __QueryLanguage() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var all = em.createQuery(
                                """
                                        SELECT e
                                        FROM BoardingPass AS e
                                        WHERE e.id.flightId = :flightId
                                        ORDER BY e.boardingNo ASC""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", FLIGHT_ID)
                        .getResultList();
                assertThat(all).as("passes of flight %s", FLIGHT_ID)
                        .hasSize(COUNT_OF_FLIGHT)
                        .allSatisfy(p -> assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID));
                assertThat(all).extracting(BoardingPass::getBoardingNo)
                        .as("boarding numbers of flight %s", FLIGHT_ID)
                        .containsExactlyElementsOf(BOARDING_NOS_ASC);
                return null;
            });
        }

        /**
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * selects the 15 passes of the flight, by boarding number, {@code 1} to {@code 12} and then
         * the three without one, as PostgreSQL orders a {@code NULL} last in an ascending order.
         */
        @DisplayName("selects all, by boarding number, nulls last, by the Criteria API")
        @Test
        void __CriteriaApi() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root)
                        .where(builder.equal(
                                root.get(BoardingPass_.id).get(MappedSegmentId_.flightId),
                                FLIGHT_ID))
                        .orderBy(builder.asc(root.get(BoardingPass_.boardingNo)));
                final var all = em.createQuery(query).getResultList();
                assertThat(all).as("passes of flight %s", FLIGHT_ID)
                        .hasSize(COUNT_OF_FLIGHT)
                        .allSatisfy(p -> assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID));
                assertThat(all).extracting(BoardingPass::getBoardingNo)
                        .as("boarding numbers of flight %s", FLIGHT_ID)
                        .containsExactlyElementsOf(BOARDING_NOS_ASC);
                return null;
            });
        }
    }

    /**
     * Tests the {@code selectAllByFlightIdOrderBySeatNoAsc} named query.
     */
    @DisplayName("selectAllByFlightIdOrderBySeatNoAsc")
    @Nested
    class SelectAllByFlightIdOrderBySeatNo_IT {

        /**
         * Verifies that the named query selects the 15 passes of the flight, by seat number, in the
         * order of the text, not of the row: {@code 21C} before {@code 7C}.
         */
        @DisplayName("selects all, by seat number, by the named query")
        @Test
        void __NamedQuery() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var all = em.createNamedQuery("selectAllByFlightIdOrderBySeatNoAsc",
                                                    BoardingPass.class)
                        .setParameter("flightId", FLIGHT_ID)
                        .getResultList();
                assertThat(all).as("passes of flight %s", FLIGHT_ID)
                        .hasSize(COUNT_OF_FLIGHT)
                        .allSatisfy(p -> assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID));
                assertThat(all).extracting(BoardingPass::getSeatNo)
                        .as("seat numbers of flight %s", FLIGHT_ID)
                        .containsExactlyElementsOf(SEAT_NOS_ASC);
                return null;
            });
        }

        /**
         * Verifies that the same query, written in the Jakarta Persistence Query Language, selects
         * the 15 passes of the flight, by seat number, in the order of the text, not of the row:
         * {@code 21C} before {@code 7C}.
         */
        @DisplayName("selects all, by seat number, by the query language")
        @Test
        void __QueryLanguage() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var all = em.createQuery(
                                """
                                        SELECT e
                                        FROM BoardingPass AS e
                                        WHERE e.id.flightId = :flightId
                                        ORDER BY e.seatNo ASC""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", FLIGHT_ID)
                        .getResultList();
                assertThat(all).as("passes of flight %s", FLIGHT_ID)
                        .hasSize(COUNT_OF_FLIGHT)
                        .allSatisfy(p -> assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID));
                assertThat(all).extracting(BoardingPass::getSeatNo)
                        .as("seat numbers of flight %s", FLIGHT_ID)
                        .containsExactlyElementsOf(SEAT_NOS_ASC);
                return null;
            });
        }

        /**
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * selects the 15 passes of the flight, by seat number, in the order of the text, not of the
         * row: {@code 21C} before {@code 7C}.
         */
        @DisplayName("selects all, by seat number, by the Criteria API")
        @Test
        void __CriteriaApi() {
            applyEntityManager(PERSISTENCE_UNIT_NAME, em -> {
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root)
                        .where(builder.equal(
                                root.get(BoardingPass_.id).get(MappedSegmentId_.flightId),
                                FLIGHT_ID))
                        .orderBy(builder.asc(root.get(BoardingPass_.seatNo)));
                final var all = em.createQuery(query).getResultList();
                assertThat(all).as("passes of flight %s", FLIGHT_ID)
                        .hasSize(COUNT_OF_FLIGHT)
                        .allSatisfy(p -> assertThat(p.getFlightId()).isEqualTo(FLIGHT_ID));
                assertThat(all).extracting(BoardingPass::getSeatNo)
                        .as("seat numbers of flight %s", FLIGHT_ID)
                        .containsExactlyElementsOf(SEAT_NOS_ASC);
                return null;
            });
        }
    }
}
