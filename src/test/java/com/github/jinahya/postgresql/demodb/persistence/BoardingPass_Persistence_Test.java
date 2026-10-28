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

import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link BoardingPass} against the schema generated into the test unit's
 * database, a PostgreSQL container.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class BoardingPass_Persistence_Test
        extends _Entity_Persistence_Test<BoardingPass> {

    /**
     * The number of boarding passes {@link #persistOnOneFlight(EntityManager)} persists.
     */
    private static final int COUNT_ON_ONE_FLIGHT = 5;

    BoardingPass_Persistence_Test() {
        super(BoardingPass.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Persists {@value #COUNT_ON_ONE_FLIGHT} boarding passes on one flight, and flushes them.
     *
     * @param em the entity manager to persist with.
     * @return the boarding passes persisted.
     * @implNote The first pass, with its segment, ticket and flight, comes from its
     *         persister; each other one is of a new ticket, on the first one's flight. Seat numbers
     *         are drawn until they are distinct, and a boarding number which is already taken is
     *         left {@code null}, so that neither unique constraint of the table is broken, and some
     *         passes, like some installed ones, have no boarding number.
     */
    private static List<BoardingPass> persistOnOneFlight(final EntityManager em) {
        final var first = EntityPersisterUtils.newPersistedInstanceOf(em, BoardingPass.class);
        em.flush();
        final var flight = first.getSegment().getFlight();
        final var seatNos = new HashSet<String>();
        seatNos.add(first.getSeatNo());
        final var boardingNos = new HashSet<Integer>();
        if (first.getBoardingNo() != null) {
            boardingNos.add(first.getBoardingNo());
        }
        final var passes = new ArrayList<BoardingPass>();
        passes.add(first);
        while (passes.size() < COUNT_ON_ONE_FLIGHT) {
            final var ticket = EntityPersisterUtils.newPersistedInstanceOf(em, Ticket.class);
            final var randomSegment = new Segment_Randomizer().get();
            final var segment = Segment.of(ticket, flight);
            segment.setFareConditions(randomSegment.getFareConditions());
            segment.setPrice(randomSegment.getPrice());
            em.persist(segment);
            final var random = new BoardingPass_Randomizer().get();
            final var pass = BoardingPass.of(segment);
            String seatNo;
            do {
                seatNo = __Random_TestUtils.seatNo();
            } while (!seatNos.add(seatNo));
            pass.setSeatNo(seatNo);
            final var boardingNo = random.getBoardingNo();
            pass.setBoardingNo(
                    boardingNo != null && boardingNos.add(boardingNo) ? boardingNo : null);
            pass.setBoardingTime(random.getBoardingTime());
            em.persist(pass);
            passes.add(pass);
        }
        em.flush();
        return passes;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests the {@code selectByFlightIdAndBoardingNo} named query.
     */
    @DisplayName("selectByFlightIdAndBoardingNo")
    @Nested
    class SelectByFlightIdAndBoardingNo_Test {

        /**
         * Verifies that the named query finds a newly persisted instance by its flight id and
         * boarding number, a unique pair.
         *
         * @implNote The context is cleared after the flush, so the instance found is read
         *         from the database, not answered from the context. The boarding number is
         *         nullable, and randomized as {@code null} half the time; a pass without one cannot
         *         be found by it, {@code = NULL} matching no row, so one is given before the flush
         *         when it is missing.
         */
        @DisplayName("finds the persisted instance, by the named query")
        @Test
        void __NamedQuery() {
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
         * Verifies that the same query, written in the Jakarta Persistence Query Language, finds a
         * newly persisted instance by its flight id and boarding number, a unique pair.
         *
         * @implNote The context is cleared after the flush, so the instance found is read
         *         from the database, not answered from the context. The boarding number is
         *         nullable, and randomized as {@code null} half the time; a pass without one cannot
         *         be found by it, {@code = NULL} matching no row, so one is given before the flush
         *         when it is missing.
         */
        @DisplayName("finds the persisted instance, by the query language")
        @Test
        void __QueryLanguage() {
            applyNewPersistedEntityInstanceAndRollback((em, v) -> {
                if (v.getBoardingNo() == null) {
                    v.setBoardingNo(__Random_TestUtils.positiveInt());
                }
                em.flush();
                em.clear();
                final var found = em.createQuery("""
                                                         SELECT e
                                                         FROM BoardingPass AS e
                                                         WHERE e.flightId = :flightId
                                                           AND e.boardingNo = :boardingNo""",
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
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * finds a newly persisted instance by its flight id and boarding number, a unique pair.
         *
         * @implNote The context is cleared after the flush, so the instance found is read
         *         from the database, not answered from the context. The boarding number is
         *         nullable, and randomized as {@code null} half the time; a pass without one cannot
         *         be found by it, {@code = NULL} matching no row, so one is given before the flush
         *         when it is missing.
         */
        @DisplayName("finds the persisted instance, by the Criteria API")
        @Test
        void __CriteriaApi() {
            applyNewPersistedEntityInstanceAndRollback((em, v) -> {
                if (v.getBoardingNo() == null) {
                    v.setBoardingNo(__Random_TestUtils.positiveInt());
                }
                em.flush();
                em.clear();
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root).where(
                        builder.equal(root.get(BoardingPass_.flightId), v.getFlightId()),
                        builder.equal(root.get(BoardingPass_.boardingNo), v.getBoardingNo()));
                final var found = em.createQuery(query).getSingleResult();
                assertThat(found).as("found by flight id and boarding number of %s", v)
                        .isNotSameAs(v)
                        .isEqualTo(v);
                return found;
            });
        }
    }

    /**
     * Tests the {@code selectByFlightIdAndSeatNo} named query.
     */
    @DisplayName("selectByFlightIdAndSeatNo")
    @Nested
    class SelectByFlightIdAndSeatNo_Test {

        /**
         * Verifies that the named query finds a newly persisted instance by its flight id and seat
         * number, a unique pair.
         *
         * @implNote The context is cleared after the flush, so the instance found is read
         *         from the database, not answered from the context.
         */
        @DisplayName("finds the persisted instance, by the named query")
        @Test
        void __NamedQuery() {
            applyNewPersistedEntityInstanceAndRollback((em, v) -> {
                em.flush();
                em.clear();
                final var found = em.createNamedQuery("selectByFlightIdAndSeatNo",
                                                      BoardingPass.class)
                        .setParameter("flightId", v.getFlightId())
                        .setParameter("seatNo", v.getSeatNo())
                        .getSingleResult();
                assertThat(found).as("found by flight id and seat number of %s", v)
                        .isNotSameAs(v)
                        .isEqualTo(v);
                return found;
            });
        }

        /**
         * Verifies that the same query, written in the Jakarta Persistence Query Language, finds a
         * newly persisted instance by its flight id and seat number, a unique pair.
         *
         * @implNote The context is cleared after the flush, so the instance found is read
         *         from the database, not answered from the context.
         */
        @DisplayName("finds the persisted instance, by the query language")
        @Test
        void __QueryLanguage() {
            applyNewPersistedEntityInstanceAndRollback((em, v) -> {
                em.flush();
                em.clear();
                final var found = em.createQuery(
                                """
                                        SELECT e
                                        FROM BoardingPass AS e
                                        WHERE e.flightId = :flightId
                                          AND e.seatNo = :seatNo""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", v.getFlightId())
                        .setParameter("seatNo", v.getSeatNo())
                        .getSingleResult();
                assertThat(found).as("found by flight id and seat number of %s", v)
                        .isNotSameAs(v)
                        .isEqualTo(v);
                return found;
            });
        }

        /**
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * finds a newly persisted instance by its flight id and seat number, a unique pair.
         *
         * @implNote The context is cleared after the flush, so the instance found is read
         *         from the database, not answered from the context.
         */
        @DisplayName("finds the persisted instance, by the Criteria API")
        @Test
        void __CriteriaApi() {
            applyNewPersistedEntityInstanceAndRollback((em, v) -> {
                em.flush();
                em.clear();
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root).where(
                        builder.equal(root.get(BoardingPass_.flightId), v.getFlightId()),
                        builder.equal(root.get(BoardingPass_.seatNo), v.getSeatNo())
                );
                final var found = em.createQuery(query).getSingleResult();
                assertThat(found).as("found by flight id and seat number of %s", v)
                        .isNotSameAs(v)
                        .isEqualTo(v);
                return found;
            });
        }
    }

    /**
     * Tests the {@code selectAllByFlightIdOrderByBoardingNoAac} named query.
     */
    @DisplayName("selectAllByFlightIdOrderByBoardingNoAac")
    @Nested
    class SelectAllByFlightIdOrderByBoardingNo_Test {

        /**
         * Verifies that the named query selects every pass of a flight, ordered by the boarding
         * number, ascending, with those without one last.
         *
         * @implNote The test unit's database is PostgreSQL, which orders a {@code NULL} last in
         *         an ascending order.
         */
        @DisplayName(
                "selects every pass of the flight, by boarding number, nulls last, by the named " +
                "query")
        @Test
        void __NamedQuery() {
            applyEntityManagerInTransactionAndRollback(em -> {
                final var passes = persistOnOneFlight(em);
                final var first = passes.getFirst();
                em.clear();
                final var all = em.createNamedQuery("selectAllByFlightIdOrderByBoardingNoAac",
                                                    BoardingPass.class)
                        .setParameter("flightId", first.getFlightId())
                        .getResultList();
                assertThat(all).as("passes of flight %s", first.getFlightId())
                        .containsExactlyInAnyOrderElementsOf(passes);
                assertThat(all).extracting(BoardingPass::getBoardingNo)
                        .as("boarding numbers of flight %s", first.getFlightId())
                        .isSortedAccordingTo(
                                Comparator.nullsLast(Comparator.<Integer>naturalOrder()));
                return null;
            });
        }

        /**
         * Verifies that the same query, written in the Jakarta Persistence Query Language, selects
         * every pass of a flight, ordered by the boarding number, ascending, with those without one
         * last.
         *
         * @implNote The test unit's database is PostgreSQL, which orders a {@code NULL} last in
         *         an ascending order.
         */
        @DisplayName(
                "selects every pass of the flight, by boarding number, nulls last, by the query " +
                "language")
        @Test
        void __QueryLanguage() {
            applyEntityManagerInTransactionAndRollback(em -> {
                final var passes = persistOnOneFlight(em);
                final var first = passes.getFirst();
                em.clear();
                final var all = em.createQuery(
                                """
                                        SELECT e
                                        FROM BoardingPass AS e
                                        WHERE e.flightId = :flightId
                                        ORDER BY e.boardingNo ASC""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", first.getFlightId())
                        .getResultList();
                assertThat(all).as("passes of flight %s", first.getFlightId())
                        .containsExactlyInAnyOrderElementsOf(passes);
                assertThat(all).extracting(BoardingPass::getBoardingNo)
                        .as("boarding numbers of flight %s", first.getFlightId())
                        .isSortedAccordingTo(
                                Comparator.nullsLast(Comparator.<Integer>naturalOrder()));
                return null;
            });
        }

        /**
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * selects every pass of a flight, ordered by the boarding number, ascending, with those
         * without one last.
         *
         * @implNote The test unit's database is PostgreSQL, which orders a {@code NULL} last in
         *         an ascending order.
         */
        @DisplayName(
                "selects every pass of the flight, by boarding number, nulls last, by the " +
                "Criteria API")
        @Test
        void __CriteriaApi() {
            applyEntityManagerInTransactionAndRollback(em -> {
                final var passes = persistOnOneFlight(em);
                final var first = passes.getFirst();
                em.clear();
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root)
                        .where(builder.equal(root.get(BoardingPass_.flightId), first.getFlightId()))
                        .orderBy(builder.asc(root.get(BoardingPass_.boardingNo)));
                final var all = em.createQuery(query).getResultList();
                assertThat(all).as("passes of flight %s", first.getFlightId())
                        .containsExactlyInAnyOrderElementsOf(passes);
                assertThat(all).extracting(BoardingPass::getBoardingNo)
                        .as("boarding numbers of flight %s", first.getFlightId())
                        .isSortedAccordingTo(Comparator.nullsLast(Comparator.naturalOrder()));
                return null;
            });
        }
    }

    /**
     * Tests the {@code selectAllByFlightIdOrderBySeatNoAsc} named query.
     */
    @DisplayName("selectAllByFlightIdOrderBySeatNoAsc")
    @Nested
    class SelectAllByFlightIdOrderBySeatNo_Test {

        /**
         * Verifies that the named query selects every pass of a flight, ordered by the seat number,
         * ascending.
         */
        @DisplayName("selects every pass of the flight, by seat number, by the named query")
        @Test
        void __NamedQuery() {
            applyEntityManagerInTransactionAndRollback(em -> {
                final var passes = persistOnOneFlight(em);
                final var first = passes.getFirst();
                em.clear();
                final var all = em.createNamedQuery("selectAllByFlightIdOrderBySeatNoAsc",
                                                    BoardingPass.class)
                        .setParameter("flightId", first.getFlightId())
                        .getResultList();
                assertThat(all).as("passes of flight %s", first.getFlightId())
                        .containsExactlyInAnyOrderElementsOf(passes);
                assertThat(all).extracting(BoardingPass::getSeatNo)
                        .as("seat numbers of flight %s", first.getFlightId())
                        .doesNotContainNull()
                        .isSortedAccordingTo(Comparator.naturalOrder());
                return null;
            });
        }

        /**
         * Verifies that the same query, written in the Jakarta Persistence Query Language, selects
         * every pass of a flight, ordered by the seat number, ascending.
         */
        @DisplayName("selects every pass of the flight, by seat number, by the query language")
        @Test
        void __QueryLanguage() {
            applyEntityManagerInTransactionAndRollback(em -> {
                final var passes = persistOnOneFlight(em);
                final var first = passes.getFirst();
                em.clear();
                final var all = em.createQuery(
                                """
                                        SELECT e
                                        FROM BoardingPass AS e
                                        WHERE e.flightId = :flightId
                                        ORDER BY e.seatNo ASC""",
                                BoardingPass.class
                        )
                        .setParameter("flightId", first.getFlightId())
                        .getResultList();
                assertThat(all).as("passes of flight %s", first.getFlightId())
                        .containsExactlyInAnyOrderElementsOf(passes);
                assertThat(all).extracting(BoardingPass::getSeatNo)
                        .as("seat numbers of flight %s", first.getFlightId())
                        .doesNotContainNull()
                        .isSortedAccordingTo(Comparator.naturalOrder());
                return null;
            });
        }

        /**
         * Verifies that the same query, built with the Criteria API over the static metamodel,
         * selects every pass of a flight, ordered by the seat number, ascending.
         */
        @DisplayName("selects every pass of the flight, by seat number, by the Criteria API")
        @Test
        void __CriteriaApi() {
            applyEntityManagerInTransactionAndRollback(em -> {
                final var passes = persistOnOneFlight(em);
                final var first = passes.getFirst();
                em.clear();
                final var builder = em.getCriteriaBuilder();
                final var query = builder.createQuery(BoardingPass.class);
                final var root = query.from(BoardingPass.class);
                query.select(root)
                        .where(builder.equal(root.get(BoardingPass_.flightId), first.getFlightId()))
                        .orderBy(builder.asc(root.get(BoardingPass_.seatNo)));
                final var all = em.createQuery(query).getResultList();
                assertThat(all).as("passes of flight %s", first.getFlightId())
                        .containsExactlyInAnyOrderElementsOf(passes);
                assertThat(all).extracting(BoardingPass::getSeatNo)
                        .as("seat numbers of flight %s", first.getFlightId())
                        .doesNotContainNull()
                        .isSortedAccordingTo(Comparator.naturalOrder());
                return null;
            });
        }
    }
}
