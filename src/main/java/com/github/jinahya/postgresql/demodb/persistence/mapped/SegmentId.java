package com.github.jinahya.postgresql.demodb.persistence.mapped;

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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * The identifier of the {@value MappedSegment#TABLE_NAME} table -- the pair of the
 * {@value MappedSegment#COLUMN_NAME_TICKET_NO} and {@value MappedSegment#COLUMN_NAME_FLIGHT_ID}
 * columns; {@link Embeddable}, as the type of an
 * {@link jakarta.persistence.EmbeddedId @EmbeddedId} has to be.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedSegment
 * @see MappedBoardingPass
 */
@Embeddable
public class SegmentId {

    // ---------------------------------------------------------------------------------------------

    /**
     * The name of the attribute which maps the {@value MappedSegment#COLUMN_NAME_TICKET_NO} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_TICKET_NO = "ticketNo";

    // ---------------------------------------------------------------------------------------------

    /**
     * The name of the attribute which maps the {@value MappedSegment#COLUMN_NAME_FLIGHT_ID} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FLIGHT_ID = "flightId";

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of the specified values. The instance is not modified afterwards: an
     * identifier has no setter.
     *
     * @param ticketNo a value for the {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     * @param flightId a value for the {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     * @return a new instance.
     */
    public static SegmentId of(final String ticketNo, final Integer flightId) {
        final var instance = new SegmentId();
        instance.ticketNo = ticketNo;
        instance.flightId = flightId;
        return instance;
    }

    /**
     * Creates a new instance of the identifiers of the specified entities.
     *
     * @param ticket the ticket whose {@link MappedTicket#getTicketNo() ticketNo} is the
     *               {@value #ATTRIBUTE_NAME_TICKET_NO} attribute; may be {@code null}.
     * @param flight the flight whose {@link MappedFlight#getFlightId() flightId} is the
     *               {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute; may be {@code null}.
     * @return a new instance of {@code ticket}'s and {@code flight}'s identifiers.
     * @see #of(String, Integer)
     */
    public static SegmentId from(final MappedTicket ticket, final MappedFlight flight) {
        return of(
                Optional.ofNullable(ticket).map(MappedTicket::getTicketNo).orElse(null),
                Optional.ofNullable(flight).map(MappedFlight::getFlightId).orElse(null)
        );
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance. The constructor is {@code public}, as the specification requires of a
     * primary key class.
     */
    public SegmentId() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "ticketNo=" + ticketNo +
               ",flightId=" + flightId +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by {@value #ATTRIBUTE_NAME_TICKET_NO} and
     *         {@value #ATTRIBUTE_NAME_FLIGHT_ID}.
     * @implNote The class is the identifier of both {@link MappedSegment} and
     *         {@link MappedBoardingPass}, for the {@code boarding_passes} key is the
     *         {@code segments} key it references.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof SegmentId that)) {
            return false;
        }
        return Objects.equals(getTicketNo(), that.getTicketNo())
               && Objects.equals(getFlightId(), that.getFlightId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over {@value #ATTRIBUTE_NAME_TICKET_NO} and
     *         {@value #ATTRIBUTE_NAME_FLIGHT_ID}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getTicketNo(), getFlightId());
    }

    // ------------------------------------------------------------------------------------ ticketNo

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     */
    public String getTicketNo() {
        return ticketNo;
    }

    // ------------------------------------------------------------------------------------ flightId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     */
    public Integer getFlightId() {
        return flightId;
    }

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = MappedSegment.COLUMN_NAME_TICKET_NO, nullable = false, insertable = true,
            updatable = false)
    private String ticketNo;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = MappedSegment.COLUMN_NAME_FLIGHT_ID, nullable = false, insertable = true,
            updatable = false)
    private Integer flightId;
}
