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

import com.github.jinahya.persistence.more.AttributeEnum;
import com.github.jinahya.persistence.more.converter.AttributeEnumConverter;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * A mapped superclass which holds the mappings of the {@value MappedSeat#TABLE_NAME} table,
 * <q>Seats</q>.
 * <p>
 * The composite primary key is the identifier, an {@link EmbeddedId @EmbeddedId} of a subclass of
 * {@link MappedSeatId}, {@code T}, which an extending entity names; {@link #equals(Object)}
 * compares the identifiers, and the getters of the key's attributes read them. An identifier is not
 * modified once created; a seat takes another with {@link #setId(MappedSeatId)}.
 * <p>
 * The {@value MappedSeat#COLUMN_NAME_AIRPLANE_CODE} column is also a foreign key; the identifier
 * writes it, and an association over it, if the extending entity maps one, is read-only
 * ({@code insertable = false, updatable = false}).
 *
 * @param <T> the type of the identifier.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedSeatId
 */
@MappedSuperclass
public abstract class MappedSeat<T extends MappedSeatId> {

    /**
     * The fare conditions, the travel class, the value of a {@code seats.fare_conditions} column
     * and of a {@code segments.fare_conditions} column; the values the
     * {@code seat_fare_conditions_check} and {@code segments_fare_conditions_check} constraints
     * allow.
     *
     * @see FareConditionsConverter
     */
    public enum FareConditions
            implements AttributeEnum.OfString<FareConditions> {

        /**
         * {@code Economy}.
         */
        ECONOMY("Economy"),

        /**
         * {@code Comfort}.
         */
        COMFORT("Comfort"),

        /**
         * {@code Business}.
         */
        BUSINESS("Business");

        FareConditions(final String text) {
            this.text = text;
        }

        /**
         * Returns the column value of this constant, the text the column holds; not its
         * {@link Enum#name() name()}, which differs for every constant.
         *
         * @return the column value of this constant.
         */
        @Override
        public String attributeValue() {
            return text;
        }

        private final String text;
    }

    /**
     * Converts between a {@link FareConditions} and its column value, its
     * {@link FareConditions#attributeValue() attributeValue()}, through
     * {@link AttributeEnumConverter.OfString}, which looks a constant up by its value.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class FareConditionsConverter
            extends AttributeEnumConverter.OfString<FareConditions> {

        /**
         * Creates a new instance.
         */
        public FareConditionsConverter() {
            super(FareConditions.class);
        }
    }

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "seats";

    // ------------------------------------------------------------------------------------------ id

    /**
     * The name of the attribute which holds the identifier. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // ------------------------------------------------------------------------------- airplane_code

    /**
     * The name of the table column to which the {@value MappedSeatId#ATTRIBUTE_NAME_AIRPLANE_CODE}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_AIRPLANE_CODE = "airplane_code";

    /**
     * The length of the {@value #COLUMN_NAME_AIRPLANE_CODE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_AIRPLANE_CODE = 3;

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_AIRPLANE_CODE}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_AIRPLANE_CODE =
            ATTRIBUTE_NAME_ID + '.' + MappedSeatId.ATTRIBUTE_NAME_AIRPLANE_CODE;

    /**
     * The minimum size of the {@value MappedSeatId#ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The
     * value is {@value}.
     */
    public static final int SIZE_MIN_AIRPLANE_CODE = 0;

    /**
     * The maximum size of the {@value MappedSeatId#ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The
     * value is {@value}.
     */
    public static final int SIZE_MAX_AIRPLANE_CODE = COLUMN_LENGTH_AIRPLANE_CODE;

    // ------------------------------------------------------------------------------------- seat_no

    /**
     * The name of the table column to which the {@value MappedSeatId#ATTRIBUTE_NAME_SEAT_NO}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SEAT_NO = "seat_no";

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_SEAT_NO} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_SEAT_NO =
            ATTRIBUTE_NAME_ID + '.' + MappedSeatId.ATTRIBUTE_NAME_SEAT_NO;

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

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedSeat() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
               ",fareConditions=" + fareConditions +
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
        if (!(obj instanceof MappedSeat<?> that)) {
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

    // -------------------------------------------------------------------------------- airplaneCode

    /**
     * Returns the {@value MappedSeatId#ATTRIBUTE_NAME_AIRPLANE_CODE} of the identifier.
     *
     * @return the {@value MappedSeatId#ATTRIBUTE_NAME_AIRPLANE_CODE} of the identifier;
     *         {@code null} when the identifier is {@code null}.
     */
    public String getAirplaneCode() {
        return Optional.ofNullable(getId())
                .map(MappedSeatId::getAirplaneCode)
                .orElse(null);
    }

    // -------------------------------------------------------------------------------------- seatNo

    /**
     * Returns the {@value MappedSeatId#ATTRIBUTE_NAME_SEAT_NO} of the identifier.
     *
     * @return the {@value MappedSeatId#ATTRIBUTE_NAME_SEAT_NO} of the identifier; {@code null} when
     *         the identifier is {@code null}.
     */
    public String getSeatNo() {
        return Optional.ofNullable(getId())
                .map(MappedSeatId::getSeatNo)
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
}
