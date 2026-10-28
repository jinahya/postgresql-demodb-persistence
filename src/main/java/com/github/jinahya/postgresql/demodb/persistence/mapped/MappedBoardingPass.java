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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * A mapped superclass which holds the mappings of the {@value MappedBoardingPass#TABLE_NAME}
 * table,
 * <q>Boarding passes</q>.
 * <p>
 * The composite primary key is the identifier, an {@link EmbeddedId @EmbeddedId} of a subclass of
 * {@link MappedBoardingPassId}, {@code T}, which an extending entity names;
 * {@link #equals(Object)} compares the identifiers, and the getters of the key's attributes read
 * them. An identifier is not modified once created; a boarding pass takes another with
 * {@link #setId(MappedBoardingPassId)}.
 * <p>
 * The two columns of the identifier together are also a foreign key, to the {@code segments}
 * table, see {@link MappedSegment}.
 * <p>
 * The table also keeps ({@value #COLUMN_NAME_FLIGHT_ID}, {@value #COLUMN_NAME_BOARDING_NO});
 * ({@value #COLUMN_NAME_FLIGHT_ID}, {@value #COLUMN_NAME_SEAT_NO}) unique, which an extending
 * entity may state in its {@link jakarta.persistence.Table @Table}.
 *
 * @param <T> the type of the identifier.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedBoardingPassId
 */
@MappedSuperclass
public abstract class MappedBoardingPass<T extends MappedBoardingPassId> {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "boarding_passes";

    // ------------------------------------------------------------------------------------------ id

    /**
     * The name of the attribute which holds the identifier. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // ----------------------------------------------------------------------------------- ticket_no

    /**
     * The name of the table column to which the
     * {@value MappedBoardingPassId#ATTRIBUTE_NAME_TICKET_NO} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_TICKET_NO = "ticket_no";

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_TICKET_NO} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_TICKET_NO =
            ATTRIBUTE_NAME_ID + '.' + MappedBoardingPassId.ATTRIBUTE_NAME_TICKET_NO;

    // ----------------------------------------------------------------------------------- flight_id

    /**
     * The name of the table column to which the
     * {@value MappedBoardingPassId#ATTRIBUTE_NAME_FLIGHT_ID} attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_FLIGHT_ID = "flight_id";

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_FLIGHT_ID} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_FLIGHT_ID =
            ATTRIBUTE_NAME_ID + '.' + MappedBoardingPassId.ATTRIBUTE_NAME_FLIGHT_ID;

    // ------------------------------------------------------------------------------------- seat_no

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

    // --------------------------------------------------------------------------------- boarding_no

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

    // ------------------------------------------------------------------------------- boarding_time

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

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedBoardingPass() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
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
     * @implSpec Equality is by the identifier alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedBoardingPass<?> that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is the identifier's, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    // ------------------------------------------------------------------------------------------ id

    /**
     * Returns current value of the identifier.
     *
     * @return current value of the identifier; {@code null} until set or loaded.
     */
    public T getId() {
        return id;
    }

    /**
     * Replaces current value of the identifier with the specified value.
     *
     * @param id new value for the identifier.
     */
    public void setId(final T id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------------------ ticketNo

    /**
     * Returns the {@value MappedBoardingPassId#ATTRIBUTE_NAME_TICKET_NO} of the identifier.
     *
     * @return the {@value MappedBoardingPassId#ATTRIBUTE_NAME_TICKET_NO} of the identifier;
     *         {@code null} when the identifier is {@code null}.
     */
    public String getTicketNo() {
        return Optional.ofNullable(getId())
                .map(MappedBoardingPassId::getTicketNo)
                .orElse(null);
    }

    // ------------------------------------------------------------------------------------ flightId

    /**
     * Returns the {@value MappedBoardingPassId#ATTRIBUTE_NAME_FLIGHT_ID} of the identifier.
     *
     * @return the {@value MappedBoardingPassId#ATTRIBUTE_NAME_FLIGHT_ID} of the identifier;
     *         {@code null} when the identifier is {@code null}.
     */
    public Integer getFlightId() {
        return Optional.ofNullable(getId())
                .map(MappedBoardingPassId::getFlightId)
                .orElse(null);
    }

    // -------------------------------------------------------------------------------------- seatNo

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

    // ---------------------------------------------------------------------------------- boardingNo

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

    // -------------------------------------------------------------------------------- boardingTime

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute.
     */
    public Instant getBoardingTime() {
        return boardingTime;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute with the specified
     * value.
     *
     * @param boardingTime new value for {@value #ATTRIBUTE_NAME_BOARDING_TIME} attribute.
     */
    public void setBoardingTime(final Instant boardingTime) {
        this.boardingTime = boardingTime;
    }

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @EmbeddedId
    private T id;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SEAT_NO, nullable = false, insertable = true, updatable = true)
    private String seatNo;

    // ---------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_BOARDING_NO, nullable = true, insertable = true, updatable = true)
    private Integer boardingNo;

    // ---------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_BOARDING_TIME, nullable = true, insertable = true, updatable = true)
    private Instant boardingTime;
}
