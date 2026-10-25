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
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * A mapped superclass which holds the mappings of the {@value MappedTicket#TABLE_NAME} table,
 * <q>Tickets</q>.
 * <p>
 * The {@value MappedTicket#COLUMN_NAME_BOOK_REF} column is mapped read-only
 * ({@code insertable = false, updatable = false}), with a {@code protected} getter and no setter,
 * for {@link #toString()} and for queries. How the relationship behind it is mapped -- fetch type,
 * cascade, whether there is an association at all -- is the extending entity's decision, so the
 * extending entity also owns its writable mapping, by an association's
 * {@link jakarta.persistence.JoinColumn @JoinColumn} or by an
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride}. <strong>An extending entity
 * which maps neither never writes it.</strong> Being read-only, it is populated by a load or a
 * refresh only, which is also why it carries no {@code @NotNull}: an instance about to be inserted
 * has nothing there, and the column's {@code NOT NULL} is the database's to enforce, on whatever
 * the association writes.
 * <p>
 * The table also keeps ({@value #COLUMN_NAME_BOOK_REF}, {@value #COLUMN_NAME_PASSENGER_ID},
 * {@value #COLUMN_NAME_OUTBOUND}) unique, which an extending entity may state in its
 * {@link jakarta.persistence.Table @Table}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedTicket {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "tickets";

    // ----------------------------------------------------------------------------------- ticket_no

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TICKET_NO} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_TICKET_NO = "ticket_no";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_TICKET_NO} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_TICKET_NO = "ticketNo";

    // ------------------------------------------------------------------------------------ book_ref

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_BOOK_REF} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_BOOK_REF = "book_ref";

    /**
     * The length of the {@value #COLUMN_NAME_BOOK_REF} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_BOOK_REF = 6;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_BOOK_REF} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_BOOK_REF = "bookRef";

    /**
     * The minimum size of the {@value #ATTRIBUTE_NAME_BOOK_REF} attribute. The value is {@value}.
     */
    public static final int SIZE_MIN_BOOK_REF = 0;

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_BOOK_REF} attribute. The value is {@value}.
     */
    public static final int SIZE_MAX_BOOK_REF = COLUMN_LENGTH_BOOK_REF;

    // -------------------------------------------------------------------------------- passenger_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PASSENGER_ID} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_PASSENGER_ID = "passenger_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PASSENGER_ID} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PASSENGER_ID = "passengerId";

    // ------------------------------------------------------------------------------ passenger_name

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PASSENGER_NAME} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_PASSENGER_NAME = "passenger_name";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PASSENGER_NAME} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_PASSENGER_NAME = "passengerName";

    // ------------------------------------------------------------------------------------ outbound

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_OUTBOUND} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_OUTBOUND = "outbound";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_OUTBOUND} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_OUTBOUND = "outbound";

    // ---------------------------------------------------------------------------------------------
    // STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of a subclass, with the specified ticket number.
     *
     * @param <T>          the type of the instance.
     * @param instantiator a supplier of a new, empty instance of {@code T}.
     * @param ticketNo     a value for the {@value #ATTRIBUTE_NAME_TICKET_NO} attribute.
     * @return the instance supplied by {@code instantiator}, with its ticket number set.
     * @throws NullPointerException if {@code instantiator} is {@code null}, or it supplies
     *                              {@code null}.
     */
    protected static <T extends MappedTicket> T of(final Supplier<? extends T> instantiator,
                                                   final String ticketNo) {
        Objects.requireNonNull(instantiator, "instantiator is null");
        final var instance =
                Objects.requireNonNull(instantiator.get(), "instantiator.get() is null");
        instance.setTicketNo(ticketNo);
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedTicket() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "ticketNo=" + ticketNo +
               ",bookRef=" + bookRef +
               ",passengerId=" + passengerId +
               ",passengerName=" + passengerName +
               ",outbound=" + outbound +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@link Id @Id} attribute,
     *         {@value #ATTRIBUTE_NAME_TICKET_NO}, alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedTicket that)) {
            return false;
        }
        return Objects.equals(getTicketNo(), that.getTicketNo());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the {@link Id @Id} attribute, consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getTicketNo());
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

    // ------------------------------------------------------------------------------------- bookRef

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_BOOK_REF} attribute, which is {@code null}
     * until this instance is loaded.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_BOOK_REF} attribute.
     * @apiNote This method is {@code protected}, not package-private, so that a lazy proxy,
     *         which is a subclass in another package, can override it.
     */
    protected String getBookRef() {
        return bookRef;
    }

    // --------------------------------------------------------------------------------- passengerId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PASSENGER_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PASSENGER_ID} attribute.
     */
    public String getPassengerId() {
        return passengerId;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_PASSENGER_ID} attribute with the specified
     * value.
     *
     * @param passengerId new value for {@value #ATTRIBUTE_NAME_PASSENGER_ID} attribute.
     */
    public void setPassengerId(final String passengerId) {
        this.passengerId = passengerId;
    }

    // ------------------------------------------------------------------------------- passengerName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PASSENGER_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PASSENGER_NAME} attribute.
     */
    public String getPassengerName() {
        return passengerName;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_PASSENGER_NAME} attribute with the
     * specified value.
     *
     * @param passengerName new value for {@value #ATTRIBUTE_NAME_PASSENGER_NAME} attribute.
     */
    public void setPassengerName(final String passengerName) {
        this.passengerName = passengerName;
    }

    // ------------------------------------------------------------------------------------ outbound

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_OUTBOUND} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_OUTBOUND} attribute.
     */
    public Boolean getOutbound() {
        return outbound;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_OUTBOUND} attribute with the specified
     * value.
     *
     * @param outbound new value for {@value #ATTRIBUTE_NAME_OUTBOUND} attribute.
     */
    public void setOutbound(final Boolean outbound) {
        this.outbound = outbound;
    }

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TICKET_NO, nullable = false, insertable = true, updatable = false)
    private String ticketNo;

    // ---------------------------------------------------------------------------------------------
    // read-only: the extending entity owns the writable mapping; see the class documentation
    @Size(min = SIZE_MIN_BOOK_REF, max = SIZE_MAX_BOOK_REF)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BOOK_REF,
            nullable = false,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_BOOK_REF
    )
    private String bookRef;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_PASSENGER_ID, nullable = false, insertable = true, updatable = true)
    private String passengerId;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_PASSENGER_NAME, nullable = false, insertable = true,
            updatable = true)
    private String passengerName;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_OUTBOUND, nullable = false, insertable = true, updatable = true)
    private Boolean outbound;
}
