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
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A mapped superclass which holds the mappings of the {@value MappedBoardingPass#TABLE_NAME} table,
 * <q>Boarding passes</q>, including the {@link Id @Id} attributes of its composite primary key.
 * <p>
 * The identifier is mapped with an {@link jakarta.persistence.IdClass @IdClass}: an extending
 * entity names its id class, a subclass of {@link MappedBoardingPassId}, with {@code @IdClass} and
 * inherits the {@code @Id} attributes from here.
 * <p>
 * The two {@code @Id} columns together are also a foreign key, to the {@code segments} table, see
 * {@link MappedSegment}.
 * <p>
 * The table also keeps ({@value #COLUMN_NAME_FLIGHT_ID}, {@value #COLUMN_NAME_BOARDING_NO});
 * ({@value #COLUMN_NAME_FLIGHT_ID}, {@value #COLUMN_NAME_SEAT_NO}) unique, which an extending
 * entity may state in its {@link jakarta.persistence.Table @Table}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedBoardingPassId
 */
@MappedSuperclass
public abstract class MappedBoardingPass {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "boarding_passes";

    // ------------------------------------------------------------------------------------------------------- TICKET_NO

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TICKET_NO} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_TICKET_NO = "ticket_no";

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_TICKET_NO}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_TICKET_NO =
            MappedBoardingPassId.ATTRIBUTE_NAME_TICKET_NO;

    // ------------------------------------------------------------------------------------------------------- FLIGHT_ID

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_FLIGHT_ID = "flight_id";

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_FLIGHT_ID}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FLIGHT_ID =
            MappedBoardingPassId.ATTRIBUTE_NAME_FLIGHT_ID;

    // --------------------------------------------------------------------------------------------------------- SEAT_NO

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_SEAT_NO} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_SEAT_NO = "seat_no";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SEAT_NO} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_SEAT_NO = "seatNo";

    // ----------------------------------------------------------------------------------------------------- BOARDING_NO

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_BOARDING_NO} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_BOARDING_NO = "boarding_no";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_BOARDING_NO} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_BOARDING_NO = "boardingNo";

    // --------------------------------------------------------------------------------------------------- BOARDING_TIME

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_BOARDING_TIME = "boarding_time";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_BOARDING_TIME} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_BOARDING_TIME = "boardingTime";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedBoardingPass() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "ticketNo=" + ticketNo +
               ",flightId=" + flightId +
               ",seatNo=" + seatNo +
               ",boardingNo=" + boardingNo +
               ",boardingTime=" + boardingTime +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the two {@link Id @Id} attributes,
     *         {@value #ATTRIBUTE_NAME_TICKET_NO} and {@value #ATTRIBUTE_NAME_FLIGHT_ID}, alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedBoardingPass that)) {
            return false;
        }
        return Objects.equals(getTicketNo(), that.getTicketNo())
               && Objects.equals(getFlightId(), that.getFlightId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the two {@link Id @Id} attributes, consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getTicketNo(), getFlightId());
    }

    // -------------------------------------------------------------------------------------------------------- ticketNo

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     */
    public String getTicketNo() {
        return ticketNo;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_TICKET_NO} attribute with the specified
     * value.
     *
     * @param ticketNo new value for {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     */
    protected void setTicketNo(final String ticketNo) {
        this.ticketNo = ticketNo;
    }

    // -------------------------------------------------------------------------------------------------------- flightId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     */
    public Integer getFlightId() {
        return flightId;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute with the specified
     * value.
     *
     * @param flightId new value for {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     */
    protected void setFlightId(final Integer flightId) {
        this.flightId = flightId;
    }

    // ---------------------------------------------------------------------------------------------------------- seatNo

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SEAT_NO} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SEAT_NO} attribute.
     */
    public String getSeatNo() {
        return seatNo;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_SEAT_NO} attribute with the specified
     * value.
     *
     * @param seatNo new value for {@value #ATTRIBUTE_NAME_SEAT_NO} attribute.
     */
    public void setSeatNo(final String seatNo) {
        this.seatNo = seatNo;
    }

    // ------------------------------------------------------------------------------------------------------ boardingNo

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_BOARDING_NO} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_BOARDING_NO} attribute.
     */
    public Integer getBoardingNo() {
        return boardingNo;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_BOARDING_NO} attribute with the specified
     * value.
     *
     * @param boardingNo new value for {@value #ATTRIBUTE_NAME_BOARDING_NO} attribute.
     */
    public void setBoardingNo(final Integer boardingNo) {
        this.boardingNo = boardingNo;
    }

    // ---------------------------------------------------------------------------------------------------- boardingTime

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute.
     */
    public OffsetDateTime getBoardingTime() {
        return boardingTime;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute with the specified
     * value.
     *
     * @param boardingTime new value for {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute.
     */
    public void setBoardingTime(final OffsetDateTime boardingTime) {
        this.boardingTime = boardingTime;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TICKET_NO, nullable = false, insertable = true, updatable = false)
    private String ticketNo;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FLIGHT_ID, nullable = false, insertable = true, updatable = false)
    private Integer flightId;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SEAT_NO, nullable = false, insertable = true, updatable = true)
    private String seatNo;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_BOARDING_NO, nullable = true, insertable = true, updatable = true)
    private Integer boardingNo;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_BOARDING_TIME, nullable = true, insertable = true, updatable = true)
    private OffsetDateTime boardingTime;
}
