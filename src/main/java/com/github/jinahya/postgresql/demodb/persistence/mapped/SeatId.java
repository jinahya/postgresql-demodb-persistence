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
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * The identifier of the {@value MappedSeat#TABLE_NAME} table -- the pair of the
 * {@value MappedSeat#COLUMN_NAME_AIRPLANE_CODE} and {@value MappedSeat#COLUMN_NAME_SEAT_NO}
 * columns; {@link Serializable} and {@link Embeddable}, as an
 * {@link jakarta.persistence.EmbeddedId @EmbeddedId} has to be.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedSeat
 */
@Embeddable
public class SeatId
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------------------------------------------

    /**
     * The minimum size of the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MIN_AIRPLANE_CODE = MappedSeat.SIZE_MIN_AIRPLANE_CODE;

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MAX_AIRPLANE_CODE = MappedSeat.SIZE_MAX_AIRPLANE_CODE;

    /**
     * The name of the attribute which maps the {@value MappedSeat#COLUMN_NAME_AIRPLANE_CODE}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_AIRPLANE_CODE = "airplaneCode";

    // ---------------------------------------------------------------------------------------------

    /**
     * The name of the attribute which maps the {@value MappedSeat#COLUMN_NAME_SEAT_NO} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SEAT_NO = "seatNo";

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of the specified values. The instance is not modified afterwards: an
     * identifier has no setter.
     *
     * @param airplaneCode a value for the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     * @param seatNo       a value for the {@value #ATTRIBUTE_NAME_SEAT_NO} attribute.
     * @return a new instance.
     */
    public static SeatId of(final String airplaneCode, final String seatNo) {
        final var instance = new SeatId();
        instance.airplaneCode = airplaneCode;
        instance.seatNo = seatNo;
        return instance;
    }

    /**
     * Creates a new instance of the identifier of the specified airplane and the specified seat
     * number.
     *
     * @param airplaneData the airplane whose
     *                     {@link MappedAirplaneData#getAirplaneCode() airplaneCode} is the
     *                     {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute; may be {@code null},
     *                     which gives a {@code null} value.
     * @param seatNo       a value for the {@value #ATTRIBUTE_NAME_SEAT_NO} attribute.
     * @return a new instance of {@code airplaneData}'s identifier and {@code seatNo}.
     * @see #of(String, String)
     */
    public static SeatId from(final MappedAirplaneData airplaneData, final String seatNo) {
        return of(
                Optional.ofNullable(airplaneData)
                        .map(MappedAirplaneData::getAirplaneCode)
                        .orElse(null),
                seatNo
        );
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance. The constructor is {@code public}, as the specification requires of a
     * primary key class.
     */
    public SeatId() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "airplaneCode=" + airplaneCode +
               ",seatNo=" + seatNo +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} and
     *         {@value #ATTRIBUTE_NAME_SEAT_NO}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof SeatId that)) {
            return false;
        }
        return Objects.equals(getAirplaneCode(), that.getAirplaneCode())
               && Objects.equals(getSeatNo(), that.getSeatNo());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} and
     *         {@value #ATTRIBUTE_NAME_SEAT_NO}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getAirplaneCode(), getSeatNo());
    }

    // -------------------------------------------------------------------------------- airplaneCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     */
    public String getAirplaneCode() {
        return airplaneCode;
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

    // ---------------------------------------------------------------------------------------------
    @Size(min = MappedSeat.SIZE_MIN_AIRPLANE_CODE, max = MappedSeat.SIZE_MAX_AIRPLANE_CODE)
    @NotNull
    @Basic(optional = false)
    @Column(name = MappedSeat.COLUMN_NAME_AIRPLANE_CODE,
            nullable = false,
            insertable = true,
            updatable = false,
            length = MappedSeat.COLUMN_LENGTH_AIRPLANE_CODE
    )
    private String airplaneCode;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = MappedSeat.COLUMN_NAME_SEAT_NO, nullable = false, insertable = true,
            updatable = false)
    private String seatNo;
}
