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
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A superclass for the identifier of the {@value MappedSegment#TABLE_NAME} table -- the pair of the
 * {@value MappedSegment#COLUMN_NAME_TICKET_NO} and {@value MappedSegment#COLUMN_NAME_FLIGHT_ID}
 * columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedSegment
 */
@MappedSuperclass
public abstract class MappedSegmentId {

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

    // ---------------------------------------------------------------------------------------------
    // STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of a subclass, with the specified values.
     *
     * @param <T>          the type of the identifier.
     * @param instantiator a supplier of a new, empty instance of {@code T}.
     * @param ticketNo     a value for the {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     * @param flightId     a value for the {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     * @return the instance supplied by {@code instantiator}, with its attributes set.
     * @throws NullPointerException if {@code instantiator} is {@code null}, or it supplies
     *                              {@code null}.
     */
    protected static <T extends MappedSegmentId> T of(final Supplier<? extends T> instantiator,
                                                      final String ticketNo,
                                                      final Integer flightId) {
        Objects.requireNonNull(instantiator, "instantiator is null");
        final var instance =
                Objects.requireNonNull(instantiator.get(), "instantiator.get() is null");
        instance.setTicketNo(ticketNo);
        instance.setFlightId(flightId);
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedSegmentId() {
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
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedSegmentId that)) {
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

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_TICKET_NO} attribute with the specified
     * value.
     *
     * @param ticketNo new value for {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     */
    protected void setTicketNo(final String ticketNo) {
        this.ticketNo = ticketNo;
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

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute with the specified
     * value.
     *
     * @param flightId new value for {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     */
    protected void setFlightId(final Integer flightId) {
        this.flightId = flightId;
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
