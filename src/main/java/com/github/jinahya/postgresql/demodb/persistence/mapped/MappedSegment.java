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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat.FareConditions;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat.FareConditionsConverter;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

/**
 * A mapped superclass which holds the mappings of the {@value MappedSegment#TABLE_NAME} table,
 * <q>Flight segment (leg)</q>.
 * <p>
 * The composite primary key is the identifier, an {@link EmbeddedId @EmbeddedId} of a subclass of
 * {@link MappedSegmentId}, {@code T}, which an extending entity names; {@link #equals(Object)}
 * compares the identifiers, and the getters of the key's attributes read them. An identifier is not
 * modified once created; a segment takes another with {@link #setId(MappedSegmentId)}.
 * <p>
 * The {@value MappedSegment#COLUMN_NAME_TICKET_NO} and {@value MappedSegment#COLUMN_NAME_FLIGHT_ID}
 * columns are also foreign keys; the identifier writes them, and an association over them, if the
 * extending entity maps one, is read-only ({@code insertable = false, updatable = false}).
 *
 * @param <T> the type of the identifier.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedSegmentId
 */
@MappedSuperclass
public abstract class MappedSegment<T extends MappedSegmentId> {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "segments";

    // ------------------------------------------------------------------------------------------ id

    /**
     * The name of the attribute which holds the identifier. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // ----------------------------------------------------------------------------------- ticket_no

    /**
     * The name of the table column to which the {@value MappedSegmentId#ATTRIBUTE_NAME_TICKET_NO}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_TICKET_NO = "ticket_no";

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_TICKET_NO} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_TICKET_NO =
            ATTRIBUTE_NAME_ID + '.' + MappedSegmentId.ATTRIBUTE_NAME_TICKET_NO;

    // ----------------------------------------------------------------------------------- flight_id

    /**
     * The name of the table column to which the {@value MappedSegmentId#ATTRIBUTE_NAME_FLIGHT_ID}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_FLIGHT_ID = "flight_id";

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_FLIGHT_ID} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_FLIGHT_ID =
            ATTRIBUTE_NAME_ID + '.' + MappedSegmentId.ATTRIBUTE_NAME_FLIGHT_ID;

    // ----------------------------------------------------------------------------- fare_conditions

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_FARE_CONDITIONS = "fare_conditions";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FARE_CONDITIONS} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FARE_CONDITIONS = "fareConditions";

    /**
     * A value of the {@value #COLUMN_NAME_FARE_CONDITIONS} column, {@code Economy}. The value is
     * {@value}.
     */
    public static final String COLUMN_VALUE_FARE_CONDITIONS_ECONOMY =
            MappedSeat.COLUMN_VALUE_FARE_CONDITIONS_ECONOMY;

    /**
     * A value of the {@value #COLUMN_NAME_FARE_CONDITIONS} column, {@code Comfort}. The value is
     * {@value}.
     */
    public static final String COLUMN_VALUE_FARE_CONDITIONS_COMFORT =
            MappedSeat.COLUMN_VALUE_FARE_CONDITIONS_COMFORT;

    /**
     * A value of the {@value #COLUMN_NAME_FARE_CONDITIONS} column, {@code Business}. The value is
     * {@value}.
     */
    public static final String COLUMN_VALUE_FARE_CONDITIONS_BUSINESS =
            MappedSeat.COLUMN_VALUE_FARE_CONDITIONS_BUSINESS;

    /**
     * The regular expression which the {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute matches:
     * one of the values the {@code segments_fare_conditions_check} constraint allows. The value is
     * {@value}.
     */
    public static final String PATTERN_REGEXP_FARE_CONDITIONS =
            MappedSeat.PATTERN_REGEXP_FARE_CONDITIONS;

    // --------------------------------------------------------------------------------------- price

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_PRICE} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_PRICE = "price";

    /**
     * The precision of the {@value #COLUMN_NAME_PRICE} column. The value is {@value}.
     */
    public static final int COLUMN_PRECISION_PRICE = 10;

    /**
     * The scale of the {@value #COLUMN_NAME_PRICE} column. The value is {@value}.
     */
    public static final int COLUMN_SCALE_PRICE = 2;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_PRICE} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_PRICE = "price";

    /**
     * The maximum number of integral digits of the {@value #ATTRIBUTE_NAME_PRICE} attribute. The
     * value is {@value}.
     */
    public static final int ATTRIBUTE_DIGITS_INTEGER_PRICE =
            COLUMN_PRECISION_PRICE - COLUMN_SCALE_PRICE;

    /**
     * The maximum number of fractional digits of the {@value #ATTRIBUTE_NAME_PRICE} attribute. The
     * value is {@value}.
     */
    public static final int ATTRIBUTE_DIGITS_FRACTION_PRICE = COLUMN_SCALE_PRICE;

    /**
     * The minimum value of the {@value #ATTRIBUTE_NAME_PRICE} attribute, inclusive; the column's
     * check, {@code price >= 0}. The value is {@value}.
     */
    public static final String ATTRIBUTE_DECIMAL_MIN_PRICE = "0";

    /**
     * The maximum value of the {@value #ATTRIBUTE_NAME_PRICE} attribute, inclusive; the most a
     * {@code numeric(}{@value #COLUMN_PRECISION_PRICE}{@code ,}
     * {@value #COLUMN_SCALE_PRICE}{@code )} holds. The value is {@value}.
     */
    public static final String ATTRIBUTE_DECIMAL_MAX_PRICE = "99999999.99";

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedSegment() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
               ",fareConditions=" + fareConditions +
               ",price=" + price +
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
        if (!(obj instanceof MappedSegment<?> that)) {
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
     * Returns the {@value MappedSegmentId#ATTRIBUTE_NAME_TICKET_NO} of the identifier.
     *
     * @return the {@value MappedSegmentId#ATTRIBUTE_NAME_TICKET_NO} of the identifier; {@code null}
     *         when the identifier is {@code null}.
     */
    public String getTicketNo() {
        return Optional.ofNullable(getId())
                .map(MappedSegmentId::getTicketNo)
                .orElse(null);
    }

    // ------------------------------------------------------------------------------------ flightId

    /**
     * Returns the {@value MappedSegmentId#ATTRIBUTE_NAME_FLIGHT_ID} of the identifier.
     *
     * @return the {@value MappedSegmentId#ATTRIBUTE_NAME_FLIGHT_ID} of the identifier; {@code null}
     *         when the identifier is {@code null}.
     */
    public Integer getFlightId() {
        return Optional.ofNullable(getId())
                .map(MappedSegmentId::getFlightId)
                .orElse(null);
    }

    // ------------------------------------------------------------------------------ fareConditions

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute.
     */
    public FareConditions getFareConditions() {
        return fareConditions;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute with the
     * specified value.
     *
     * @param fareConditions new value for {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute.
     */
    public void setFareConditions(final FareConditions fareConditions) {
        this.fareConditions = fareConditions;
    }

    // --------------------------------------------------------------------------------------- price

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_PRICE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_PRICE} attribute.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_PRICE} attribute with the specified value,
     * rounded half up to the scale of the column, as the server rounds it.
     *
     * @param price new value for {@value #ATTRIBUTE_NAME_PRICE} attribute.
     */
    public void setPrice(final BigDecimal price) {
        this.price = Optional.ofNullable(price)
                .map(v -> v.setScale(COLUMN_SCALE_PRICE, RoundingMode.HALF_UP))
                .orElse(null);
    }

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @EmbeddedId
    private T id;

    // ---------------------------------------------------------------------------------------------
    @Convert(converter = FareConditionsConverter.class)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FARE_CONDITIONS, nullable = false, insertable = true,
            updatable = true)
    private FareConditions fareConditions;

    // ---------------------------------------------------------------------------------------------
    @DecimalMin(ATTRIBUTE_DECIMAL_MIN_PRICE)
    @DecimalMax(ATTRIBUTE_DECIMAL_MAX_PRICE)
    // @Digits counts digits differently by provider;
    // https://github.com/jakartaee/validation-spec/issues/346
    // @jakarta.validation.constraints.Digits(integer = ATTRIBUTE_DIGITS_INTEGER_PRICE,
    //                                        fraction = ATTRIBUTE_DIGITS_FRACTION_PRICE)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_PRICE,
            nullable = false,
            insertable = true,
            updatable = true,
            precision = COLUMN_PRECISION_PRICE,
            scale = COLUMN_SCALE_PRICE
    )
    private BigDecimal price;
}
