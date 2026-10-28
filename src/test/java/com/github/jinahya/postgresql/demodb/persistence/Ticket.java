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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTicket;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * An entity which maps the {@value MappedTicket#TABLE_NAME} table, extending {@link MappedTicket}
 * with what is an application's to decide: its associations, their fetch types and their cascades.
 * <p>
 * The table's unique constraint, one ticket of a passenger per booking and direction, is declared
 * with {@link UniqueConstraint @UniqueConstraint}, under the name the database gives it, so that a
 * schema generated from the mapping, the test unit's, has it too.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = MappedTicket.TABLE_NAME,
       uniqueConstraints = {
               @UniqueConstraint(name = "tickets_book_ref_passenger_id_outbound_key",
                                 columnNames = {MappedTicket.COLUMN_NAME_BOOK_REF,
                                                MappedTicket.COLUMN_NAME_PASSENGER_ID,
                                                MappedTicket.COLUMN_NAME_OUTBOUND})
       }
)
public class Ticket
        extends MappedTicket {

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance with the specified ticket number.
     *
     * @param ticketNo a value for the {@value MappedTicket#ATTRIBUTE_NAME_TICKET_NO} attribute.
     * @return a new instance.
     */
    public static Ticket of(final String ticketNo) {
        return MappedTicket.of(Ticket::new, ticketNo);
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Ticket() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param ticketNo {@inheritDoc}
     * @implNote Widened to {@code public}: the identifier is assigned, not generated, so
     *         whoever creates an instance sets it.
     */
    @Override
    public void setTicketNo(final String ticketNo) {
        super.setTicketNo(ticketNo);
    }

    /**
     * Returns the booking this ticket belongs to, over the
     * {@value MappedTicket#COLUMN_NAME_BOOK_REF} column; this is the column's writable mapping, the
     * inherited {@code bookRef} being read-only.
     *
     * @return the booking.
     */
    public Booking getBooking() {
        return booking;
    }

    /**
     * Replaces the booking with the specified value, and the inherited, read-only attribute over
     * the {@value MappedTicket#COLUMN_NAME_BOOK_REF} column with its reference, which is what this
     * association writes.
     *
     * @param booking new value for the booking; {@code null} clears the reference too.
     */
    public void setBooking(final Booking booking) {
        this.booking = booking;
        setBookRef(booking == null ? null : booking.getBookRef());
    }

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_BOOK_REF, nullable = false, insertable = true,
                updatable = true)
    private Booking booking;
}
