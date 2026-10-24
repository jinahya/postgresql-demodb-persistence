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
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * A mapped superclass which holds the mappings of the {@value MappedSeat#TABLE_NAME} table,
 * <q>Seats</q>, including the {@link Id @Id} attributes of its composite primary key.
 * <p>
 * The identifier is mapped with an {@link jakarta.persistence.IdClass @IdClass}: an extending
 * entity names its id class, a subclass of {@link MappedSeatId}, with {@code @IdClass} and inherits
 * the {@code @Id} attributes from here.
 * <p>
 * The {@value MappedSeat#COLUMN_NAME_AIRPLANE_CODE} {@code @Id} column is also a foreign key; being
 * part of the identifier, it stays insertable, and an association over it, if the extending entity
 * maps one, is read-only ({@link jakarta.persistence.MapsId @MapsId} or
 * {@code insertable = false, updatable = false}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedSeatId
 */
@MappedSuperclass
public abstract class MappedSeat {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "seats";

    // --------------------------------------------------------------------------------------------------- AIRPLANE_CODE

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_AIRPLANE_CODE = "airplane_code";

    /**
     * The length of the {@value #COLUMN_NAME_AIRPLANE_CODE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_AIRPLANE_CODE = 3;

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_AIRPLANE_CODE}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_AIRPLANE_CODE =
            MappedSeatId.ATTRIBUTE_NAME_AIRPLANE_CODE;

    /**
     * The minimum size of the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MIN_AIRPLANE_CODE = 0;

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MAX_AIRPLANE_CODE = COLUMN_LENGTH_AIRPLANE_CODE;

    // --------------------------------------------------------------------------------------------------------- SEAT_NO

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_SEAT_NO} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_SEAT_NO = "seat_no";

    /**
     * The name of the {@link Id @Id} attribute which maps the {@value #COLUMN_NAME_SEAT_NO} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SEAT_NO = MappedSeatId.ATTRIBUTE_NAME_SEAT_NO;

    // ------------------------------------------------------------------------------------------------- FARE_CONDITIONS

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
    public static final String COLUMN_VALUE_FARE_CONDITIONS_ECONOMY = "Economy";

    /**
     * A value of the {@value #COLUMN_NAME_FARE_CONDITIONS} column, {@code Comfort}. The value is
     * {@value}.
     */
    public static final String COLUMN_VALUE_FARE_CONDITIONS_COMFORT = "Comfort";

    /**
     * A value of the {@value #COLUMN_NAME_FARE_CONDITIONS} column, {@code Business}. The value is
     * {@value}.
     */
    public static final String COLUMN_VALUE_FARE_CONDITIONS_BUSINESS = "Business";

    /**
     * The regular expression which the {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute matches:
     * one of the values the {@code seat_fare_conditions_check} constraint allows. The value is
     * {@value}.
     */
    public static final String PATTERN_REGEXP_FARE_CONDITIONS = "Economy|Comfort|Business";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedSeat() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "airplaneCode=" + airplaneCode +
               ",seatNo=" + seatNo +
               ",fareConditions=" + fareConditions +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the two {@link Id @Id} attributes,
     *         {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} and {@value #ATTRIBUTE_NAME_SEAT_NO}, alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedSeat that)) {
            return false;
        }
        return Objects.equals(getAirplaneCode(), that.getAirplaneCode())
               && Objects.equals(getSeatNo(), that.getSeatNo());
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
        return Objects.hash(getAirplaneCode(), getSeatNo());
    }

    // ---------------------------------------------------------------------------------------------------- airplaneCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     */
    public String getAirplaneCode() {
        return airplaneCode;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute with the specified
     * value.
     *
     * @param airplaneCode new value for {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     */
    protected void setAirplaneCode(final String airplaneCode) {
        this.airplaneCode = airplaneCode;
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
    protected void setSeatNo(final String seatNo) {
        this.seatNo = seatNo;
    }

    // -------------------------------------------------------------------------------------------------- fareConditions

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute.
     */
    public String getFareConditions() {
        return fareConditions;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute with the
     * specified value.
     *
     * @param fareConditions new value for {@value #ATTRIBUTE_NAME_FARE_CONDITIONS} attribute.
     */
    public void setFareConditions(final String fareConditions) {
        this.fareConditions = fareConditions;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_AIRPLANE_CODE, max = SIZE_MAX_AIRPLANE_CODE)
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AIRPLANE_CODE,
            nullable = false,
            insertable = true,
            updatable = false,
            length = COLUMN_LENGTH_AIRPLANE_CODE
    )
    private String airplaneCode;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SEAT_NO, nullable = false, insertable = true, updatable = false)
    private String seatNo;

    // -----------------------------------------------------------------------------------------------------------------
    @Pattern(regexp = PATTERN_REGEXP_FARE_CONDITIONS)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FARE_CONDITIONS, nullable = false, insertable = true,
            updatable = true)
    private String fareConditions;
}
