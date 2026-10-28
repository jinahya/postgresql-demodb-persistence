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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedBoardingPass;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSegment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * An entity which maps the {@value MappedBoardingPass#TABLE_NAME} table, extending
 * {@link MappedBoardingPass} with what is an application's to decide: its associations, their fetch
 * types and their cascades.
 * <p>
 * The identifier is {@link BoardingPassId}, {@link MappedBoardingPass}'s type argument.
 * <p>
 * The table's two unique constraints, a boarding number and a seat each once per flight, are
 * declared with {@link UniqueConstraint @UniqueConstraint}, under the names the database gives
 * them, so that a schema generated from the mapping, the test unit's, has them too.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedQuery(
        name = "selectAllByFlightIdOrderByBoardingNoAac",
        query = """
                SELECT e
                FROM BoardingPass AS e
                WHERE e.id.flightId = :flightId
                ORDER BY e.boardingNo ASC"""
)
@NamedQuery(
        name = "selectByFlightIdAndBoardingNo",
        query = """
                SELECT e
                FROM BoardingPass AS e
                WHERE e.id.flightId = :flightId AND e.boardingNo = :boardingNo"""
)
@NamedQuery(
        name = "selectAllByFlightIdOrderBySeatNoAsc",
        query = """
                SELECT e
                FROM BoardingPass AS e
                WHERE e.id.flightId = :flightId
                ORDER BY e.seatNo ASC"""
)
@NamedQuery(
        name = "selectByFlightIdAndSeatNo",
        query = """
                SELECT e
                FROM BoardingPass AS e
                WHERE e.id.flightId = :flightId AND e.seatNo = :seatNo"""
)
@Entity
@Table(name = MappedBoardingPass.TABLE_NAME,
       uniqueConstraints = {
               @UniqueConstraint(name = "boarding_passes_flight_id_boarding_no_key",
                                 columnNames = {MappedBoardingPass.COLUMN_NAME_FLIGHT_ID,
                                         MappedBoardingPass.COLUMN_NAME_BOARDING_NO}),
               @UniqueConstraint(name = "boarding_passes_flight_id_seat_no_key",
                                 columnNames = {MappedBoardingPass.COLUMN_NAME_FLIGHT_ID,
                                         MappedBoardingPass.COLUMN_NAME_SEAT_NO})
       }
)
public class BoardingPass
        extends MappedBoardingPass<BoardingPassId> {

    /**
     * Creates a new instance of the specified segment, whose identifier becomes the identifier.
     *
     * @param segment the segment the pass boards.
     * @return a new instance.
     */
    public static BoardingPass of(final Segment segment) {
        final var instance = new BoardingPass();
        instance.setId(BoardingPassId.of(segment.getTicketNo(), segment.getFlightId()));
        instance.segment = segment;
        return instance;
    }

    /**
     * Creates a new instance.
     */
    protected BoardingPass() {
        super();
    }

    /**
     * Returns the segment this pass boards, over the
     * {@value MappedBoardingPass#COLUMN_NAME_TICKET_NO} and
     * {@value MappedBoardingPass#COLUMN_NAME_FLIGHT_ID} columns, the identifier; the association is
     * read-only.
     *
     * @return the segment.
     */
    public Segment getSegment() {
        return segment;
    }

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = COLUMN_NAME_TICKET_NO,
                        referencedColumnName = MappedSegment.COLUMN_NAME_TICKET_NO,
                        nullable = false,
                        insertable = false,
                        updatable = false
            ),
            @JoinColumn(name = COLUMN_NAME_FLIGHT_ID,
                        referencedColumnName = MappedSegment.COLUMN_NAME_FLIGHT_ID,
                        nullable = false,
                        insertable = false,
                        updatable = false
            )
    })
    private Segment segment;
}
