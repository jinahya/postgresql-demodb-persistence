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
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A mapped superclass which holds the mappings of the {@value MappedSegment#TABLE_NAME} table,
 * <q>Flight segment (leg)</q>, including the {@link Id @Id} attributes of its composite primary
 * key.
 * <p>
 * The identifier is mapped with an {@link jakarta.persistence.IdClass @IdClass}: an extending
 * entity names its id class, a subclass of {@link MappedSegmentId}, with {@code @IdClass} and
 * inherits the {@code @Id} attributes from here.
 * <p>
 * The {@value MappedSegment#COLUMN_NAME_TICKET_NO} and {@value MappedSegment#COLUMN_NAME_FLIGHT_ID}
 * {@code @Id} columns are also foreign keys; being part of the identifier, they stay insertable,
 * and an association over them, if the extending entity maps one, is read-only
 * ({@link jakarta.persistence.MapsId @MapsId} or {@code insertable = false, updatable = false}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedSegmentId
 */
@MappedSuperclass
public abstract class MappedSegment {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "segments";

    // ----------------------------------------------------------------------------------- ticket_no

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TICKET_NO} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_TICKET_NO = "ticket_no";

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_TICKET_NO}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_TICKET_NO = MappedSegmentId.ATTRIBUTE_NAME_TICKET_NO;

    // ----------------------------------------------------------------------------------- flight_id

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_FLIGHT_ID = "flight_id";

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_FLIGHT_ID}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_FLIGHT_ID = MappedSegmentId.ATTRIBUTE_NAME_FLIGHT_ID;

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
               "ticketNo=" + ticketNo +
               ",flightId=" + flightId +
               ",fareConditions=" + fareConditions +
               ",price=" + price +
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
        if (!(obj instanceof MappedSegment that)) {
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
     * Replaces current value of {@value #ATTRIBUTE_NAME_PRICE} attribute with the specified value.
     *
     * @param price new value for {@value #ATTRIBUTE_NAME_PRICE} attribute.
     */
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TICKET_NO, nullable = false, insertable = true, updatable = false)
    private String ticketNo;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FLIGHT_ID, nullable = false, insertable = true, updatable = false)
    private Integer flightId;

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
