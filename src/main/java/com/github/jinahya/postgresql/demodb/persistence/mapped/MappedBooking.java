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
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A mapped superclass which holds the mappings of the {@value MappedBooking#TABLE_NAME} table,
 * <q>Bookings</q>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedBooking {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "bookings";

    // -------------------------------------------------------------------------------------------------------- BOOK_REF

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

    // ------------------------------------------------------------------------------------------------------- BOOK_DATE

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_BOOK_DATE} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_BOOK_DATE = "book_date";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_BOOK_DATE} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_BOOK_DATE = "bookDate";

    // ---------------------------------------------------------------------------------------------------- TOTAL_AMOUNT

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TOTAL_AMOUNT} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_TOTAL_AMOUNT = "total_amount";

    /**
     * The precision of the {@value #COLUMN_NAME_TOTAL_AMOUNT} column. The value is {@value}.
     */
    public static final int COLUMN_PRECISION_TOTAL_AMOUNT = 10;

    /**
     * The scale of the {@value #COLUMN_NAME_TOTAL_AMOUNT} column. The value is {@value}.
     */
    public static final int COLUMN_SCALE_TOTAL_AMOUNT = 2;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_TOTAL_AMOUNT} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_TOTAL_AMOUNT = "totalAmount";

    /**
     * The maximum number of integral digits of the {@value #ATTRIBUTE_NAME_TOTAL_AMOUNT} attribute.
     * The value is {@value}.
     */
    public static final int ATTRIBUTE_DIGITS_INTEGER_TOTAL_AMOUNT =
            COLUMN_PRECISION_TOTAL_AMOUNT - COLUMN_SCALE_TOTAL_AMOUNT;

    /**
     * The maximum number of fractional digits of the {@value #ATTRIBUTE_NAME_TOTAL_AMOUNT}
     * attribute. The value is {@value}.
     */
    public static final int ATTRIBUTE_DIGITS_FRACTION_TOTAL_AMOUNT = COLUMN_SCALE_TOTAL_AMOUNT;

    // ------------------------------------------------------------------------------------------
    // STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of a subclass, with the specified booking reference.
     *
     * @param <T>          the type of the instance.
     * @param instantiator a supplier of a new, empty instance of {@code T}.
     * @param bookRef      a value for the {@value #ATTRIBUTE_NAME_BOOK_REF} attribute.
     * @return the instance supplied by {@code instantiator}, with its booking reference set.
     * @throws NullPointerException if {@code instantiator} is {@code null}, or it supplies
     *                              {@code null}.
     */
    protected static <T extends MappedBooking> T of(final Supplier<? extends T> instantiator,
                                                    final String bookRef) {
        Objects.requireNonNull(instantiator, "instantiator is null");
        final var instance =
                Objects.requireNonNull(instantiator.get(), "instantiator.get() is null");
        instance.setBookRef(bookRef);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedBooking() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "bookRef=" + bookRef +
               ",bookDate=" + bookDate +
               ",totalAmount=" + totalAmount +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@link Id @Id} attribute,
     *         {@value #ATTRIBUTE_NAME_BOOK_REF}, alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedBooking that)) {
            return false;
        }
        return Objects.equals(getBookRef(), that.getBookRef());
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
        return Objects.hashCode(getBookRef());
    }

    // --------------------------------------------------------------------------------------------------------- bookRef

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_BOOK_REF} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_BOOK_REF} attribute.
     */
    public String getBookRef() {
        return bookRef;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_BOOK_REF} attribute with the specified
     * value.
     *
     * @param bookRef new value for {@value #ATTRIBUTE_NAME_BOOK_REF} attribute.
     */
    protected void setBookRef(final String bookRef) {
        this.bookRef = bookRef;
    }

    // -------------------------------------------------------------------------------------------------------- bookDate

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_BOOK_DATE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_BOOK_DATE} attribute.
     */
    public OffsetDateTime getBookDate() {
        return bookDate;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_BOOK_DATE} attribute with the specified
     * value.
     *
     * @param bookDate new value for {@value #ATTRIBUTE_NAME_BOOK_DATE} attribute.
     */
    public void setBookDate(final OffsetDateTime bookDate) {
        this.bookDate = bookDate;
    }

    // ----------------------------------------------------------------------------------------------------- totalAmount

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TOTAL_AMOUNT} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TOTAL_AMOUNT} attribute.
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_TOTAL_AMOUNT} attribute with the specified
     * value.
     *
     * @param totalAmount new value for {@value #ATTRIBUTE_NAME_TOTAL_AMOUNT} attribute.
     */
    public void setTotalAmount(final BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_BOOK_REF, max = SIZE_MAX_BOOK_REF)
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BOOK_REF,
            nullable = false,
            insertable = true,
            updatable = false,
            length = COLUMN_LENGTH_BOOK_REF
    )
    private String bookRef;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BOOK_DATE, nullable = false, insertable = true, updatable = true)
    private OffsetDateTime bookDate;

    // -----------------------------------------------------------------------------------------------------------------
    @Digits(integer = ATTRIBUTE_DIGITS_INTEGER_TOTAL_AMOUNT,
            fraction = ATTRIBUTE_DIGITS_FRACTION_TOTAL_AMOUNT)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TOTAL_AMOUNT,
            nullable = false,
            insertable = true,
            updatable = true,
            precision = COLUMN_PRECISION_TOTAL_AMOUNT,
            scale = COLUMN_SCALE_TOTAL_AMOUNT
    )
    private BigDecimal totalAmount;
}
