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

import com.github.jinahya.persistence.more.converter.time.ZoneIdStringAttributeConverter;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

/**
 * A mapped superclass which holds the mappings of the {@value MappedAirport#TABLE_NAME} view,
 * <q>Airports</q>.
 * <p>
 * Every column is mapped read-only, the identifier too. It has no setters; nothing writes to a
 * view.
 * <p>
 * The {@value MappedAirport#COLUMN_NAME_COORDINATES} column is a {@code point}, which has no type
 * in Jakarta Persistence; it is mapped as a list of two {@link Double}s, the longitude as {@code x}
 * and the latitude as {@code y}, through a
 * {@link MappedAirportData.CoordinatesAttributeConverter}.
 * <p>
 * The view reads {@code airports_data}, see {@link MappedAirportData}, with the
 * {@value #COLUMN_NAME_AIRPORT_NAME}, {@value #COLUMN_NAME_CITY} and {@value #COLUMN_NAME_COUNTRY}
 * taken out of their {@code jsonb} for the session's language, the {@code bookings.lang} setting
 * ({@code en} by default).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedAirport {

    /**
     * The name of the database view to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "airports";

    // -------------------------------------------------------------------------------- airport_code

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_AIRPORT_CODE = "airport_code";

    /**
     * The length of the {@value #COLUMN_NAME_AIRPORT_CODE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_AIRPORT_CODE = 3;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_AIRPORT_CODE} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_AIRPORT_CODE = "airportCode";

    /**
     * The minimum size of the {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MIN_AIRPORT_CODE = 0;

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MAX_AIRPORT_CODE = COLUMN_LENGTH_AIRPORT_CODE;

    // -------------------------------------------------------------------------------- airport_name

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_AIRPORT_NAME = "airport_name";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_AIRPORT_NAME} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_AIRPORT_NAME = "airportName";

    // ---------------------------------------------------------------------------------------- city

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_CITY} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_CITY = "city";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CITY} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_CITY = "city";

    // ------------------------------------------------------------------------------------- country

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_COUNTRY} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_COUNTRY = "country";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_COUNTRY} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_COUNTRY = "country";

    // --------------------------------------------------------------------------------- coordinates

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_COORDINATES} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_COORDINATES = "coordinates";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_COORDINATES} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_COORDINATES = "coordinates";

    // ------------------------------------------------------------------------------------ timezone

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_TIMEZONE} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_TIMEZONE = "timezone";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_TIMEZONE} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_TIMEZONE = "timezone";

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedAirport() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "airportCode=" + airportCode +
               ",airportName=" + airportName +
               ",city=" + city +
               ",country=" + country +
               ",coordinates=" + coordinates +
               ",timezone=" + timezone +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@link Id @Id} attribute,
     *         {@value #ATTRIBUTE_NAME_AIRPORT_CODE}, alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedAirport that)) {
            return false;
        }
        return Objects.equals(getAirportCode(), that.getAirportCode());
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
        return Objects.hashCode(getAirportCode());
    }

    // ---------------------------------------------------------------------------------- VALIDATION

    /**
     * Tests whether current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute is a
     * geolocation, as the column's comment says it holds: a longitude from {@code -180} to
     * {@code 180} and a latitude from {@code -90} to {@code 90}, inclusive.
     * <p>
     * No constraint of the schema requires it, so this method carries no constraint annotation; an
     * extending entity which wants it checked overrides it with
     * {@link jakarta.validation.constraints.AssertTrue @AssertTrue}.
     *
     * @return {@code true} if the longitude and the latitude are within their ranges, or the
     *         attribute, or either coordinate, is {@code null}, or the attribute has fewer than two
     *         coordinates; {@code false} otherwise.
     */
    protected boolean isCoordinatesInRange() {
        final var coordinates = getCoordinates();
        if (coordinates == null || coordinates.size() < 2 || coordinates.get(0) == null
            || coordinates.get(1) == null) {
            return true;
        }
        final double longitude = coordinates.get(0);
        final double latitude = coordinates.get(1);
        return longitude >= -180.0d && longitude <= 180.0d
               && latitude >= -90.0d && latitude <= 90.0d;
    }

    // --------------------------------------------------------------------------------- airportCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute.
     */
    public String getAirportCode() {
        return airportCode;
    }

    // --------------------------------------------------------------------------------- airportName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute.
     */
    public String getAirportName() {
        return airportName;
    }

    // ---------------------------------------------------------------------------------------- city

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    public String getCity() {
        return city;
    }

    // ------------------------------------------------------------------------------------- country

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    public String getCountry() {
        return country;
    }

    // --------------------------------------------------------------------------------- coordinates

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute -- a {@code point}
     * column, mapped as a list of two {@link Double}s, the longitude as {@code x} and the latitude
     * as {@code y}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute.
     */
    public List<Double> getCoordinates() {
        return coordinates;
    }

    // ------------------------------------------------------------------------------------ timezone

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute -- a {@code text} column
     * of a zone identifier, such as {@code Asia/Seoul}, mapped as a {@link ZoneId}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute.
     */
    public ZoneId getTimezone() {
        return timezone;
    }

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute as a {@link TimeZone}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute as a {@link TimeZone};
     *         {@code null} if it is {@code null}.
     */
    @Transient
    public TimeZone getTimezoneAsTimeZone() {
        return Optional.ofNullable(getTimezone()).map(TimeZone::getTimeZone).orElse(null);
    }

    // ---------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_AIRPORT_CODE, max = SIZE_MAX_AIRPORT_CODE)
    @Id
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_AIRPORT_CODE,
            nullable = true,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_AIRPORT_CODE
    )
    private String airportCode;

    // ---------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_AIRPORT_NAME, nullable = true, insertable = false, updatable = false)
    private String airportName;

    // ---------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_CITY, nullable = true, insertable = false, updatable = false)
    private String city;

    // ---------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_COUNTRY, nullable = true, insertable = false, updatable = false)
    private String country;

    // ---------------------------------------------------------------------------------------------
    @Size(min = 2, max = 2)
    @Convert(converter = MappedAirportData.CoordinatesAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_COORDINATES, nullable = true, insertable = false, updatable = false)
    private List<@NotNull Double> coordinates;

    // ---------------------------------------------------------------------------------------------
    @Convert(converter = ZoneIdStringAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_TIMEZONE, nullable = true, insertable = false, updatable = false)
    private ZoneId timezone;
}
