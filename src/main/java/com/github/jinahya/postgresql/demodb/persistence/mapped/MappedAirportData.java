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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A mapped superclass which holds the mappings of the {@value MappedAirportData#TABLE_NAME} table,
 * <q>Airports (internal multilingual data)</q>.
 * <p>
 * The {@value MappedAirportData#COLUMN_NAME_AIRPORT_NAME},
 * {@value MappedAirportData#COLUMN_NAME_CITY}, {@value MappedAirportData#COLUMN_NAME_COUNTRY},
 * {@value MappedAirportData#COLUMN_NAME_COORDINATES} columns have no type in Jakarta Persistence
 * ({@code jsonb}, {@code point}), so they are mapped as {@link String}, the text form the driver
 * reads them as. Writing such a text back needs the driver to leave its type to the server,
 * {@code stringtype=unspecified} on the JDBC URL, or a provider-specific type the extending entity
 * may choose instead.
 * <p>
 * The {@value #COLUMN_NAME_AIRPORT_NAME}, {@value #COLUMN_NAME_CITY} and
 * {@value #COLUMN_NAME_COUNTRY} columns are {@code jsonb}, an object of one value per language; the
 * {@code airports} view, see {@link MappedAirport}, picks the session's language out of them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedAirportData {

    /**
     * The name of the database schema which holds the {@value #TABLE_NAME} table. The value is
     * {@value}.
     */
    public static final String TABLE_SCHEMA = "bookings";

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "airports_data";

    // ---------------------------------------------------------------------------------------------------- AIRPORT_CODE

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute
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

    // ---------------------------------------------------------------------------------------------------- AIRPORT_NAME

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_AIRPORT_NAME = "airport_name";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_AIRPORT_NAME} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_AIRPORT_NAME = "airportName";

    // ------------------------------------------------------------------------------------------------------------ CITY

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_CITY} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_CITY = "city";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_CITY} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_CITY = "city";

    // --------------------------------------------------------------------------------------------------------- COUNTRY

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_COUNTRY} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_COUNTRY = "country";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_COUNTRY} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_COUNTRY = "country";

    // ----------------------------------------------------------------------------------------------------- COORDINATES

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_COORDINATES} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_COORDINATES = "coordinates";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_COORDINATES} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_COORDINATES = "coordinates";

    // -------------------------------------------------------------------------------------------------------- TIMEZONE

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_TIMEZONE} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_TIMEZONE = "timezone";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_TIMEZONE} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_TIMEZONE = "timezone";

    // -----------------------------------------------------------------------
    // STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of a subclass, with the specified code.
     *
     * @param <T>          the type of the instance.
     * @param instantiator a supplier of a new, empty instance of {@code T}.
     * @param airportCode  a value for the {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute.
     * @return the instance supplied by {@code instantiator}, with its code set.
     * @throws NullPointerException if {@code instantiator} is {@code null}, or it supplies
     *                              {@code null}.
     */
    protected static <T extends MappedAirportData> T of(final Supplier<? extends T> instantiator,
                                                        final String airportCode) {
        Objects.requireNonNull(instantiator, "instantiator is null");
        final var instance =
                Objects.requireNonNull(instantiator.get(), "instantiator.get() is null");
        instance.setAirportCode(airportCode);
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedAirportData() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

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
        if (!(obj instanceof MappedAirportData that)) {
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

    // ----------------------------------------------------------------------------------------------------- airportCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute.
     */
    public String getAirportCode() {
        return airportCode;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute with the specified
     * value.
     *
     * @param airportCode new value for {@value #ATTRIBUTE_NAME_AIRPORT_CODE} attribute.
     */
    protected void setAirportCode(final String airportCode) {
        this.airportCode = airportCode;
    }

    // ----------------------------------------------------------------------------------------------------- airportName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute -- a {@code jsonb}
     * column, mapped as the JSON text.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute.
     */
    public String getAirportName() {
        return airportName;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute with the specified
     * value.
     *
     * @param airportName new value for {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute.
     */
    public void setAirportName(final String airportName) {
        this.airportName = airportName;
    }

    // ------------------------------------------------------------------------------------------------------------ city

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CITY} attribute -- a {@code jsonb} column,
     * mapped as the JSON text.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    public String getCity() {
        return city;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_CITY} attribute with the specified value.
     *
     * @param city new value for {@value #ATTRIBUTE_NAME_CITY} attribute.
     */
    public void setCity(final String city) {
        this.city = city;
    }

    // --------------------------------------------------------------------------------------------------------- country

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute -- a {@code jsonb}
     * column, mapped as the JSON text.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute with the specified
     * value.
     *
     * @param country new value for {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     */
    public void setCountry(final String country) {
        this.country = country;
    }

    // ----------------------------------------------------------------------------------------------------- coordinates

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute -- a {@code point}
     * column, mapped as its text form, {@code (x,y)}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute.
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute with the specified
     * value.
     *
     * @param coordinates new value for {@value #ATTRIBUTE_NAME_COORDINATES} attribute.
     */
    public void setCoordinates(final String coordinates) {
        this.coordinates = coordinates;
    }

    // -------------------------------------------------------------------------------------------------------- timezone

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute.
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute with the specified
     * value.
     *
     * @param timezone new value for {@value #ATTRIBUTE_NAME_TIMEZONE} attribute.
     */
    public void setTimezone(final String timezone) {
        this.timezone = timezone;
    }

    public <R> R getTimezoneAs(final Function<? super String, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return Optional.ofNullable(getTimezone()).map(mapper).orElse(null);
    }

    public <T> void setTimezoneFrom(final T timezone,
                                    final Function<? super T, ? extends String> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        setTimezone(Optional.ofNullable(timezone).map(mapper).orElse(null));
    }

    @Transient
    public TimeZone getTimezoneAsTimeZone() {
        return getTimezoneAs(TimeZone::getTimeZone);
    }

    @Transient
    public void setTimezoneFromTimeZone(final TimeZone timezone) {
        setTimezoneFrom(timezone, TimeZone::getID);
    }

    @Transient
    public ZoneId getTimezoneAsZoneId() {
        return getTimezoneAs(ZoneId::of);
    }

    @Transient
    public void setTimezoneFromZoneId(final ZoneId zoneId) {
        setTimezoneFrom(zoneId, ZoneId::getId);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_AIRPORT_CODE, max = SIZE_MAX_AIRPORT_CODE)
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AIRPORT_CODE,
            nullable = false,
            insertable = true,
            updatable = false,
            length = COLUMN_LENGTH_AIRPORT_CODE
    )
    private String airportCode;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AIRPORT_NAME, nullable = false, insertable = true, updatable = true)
    private String airportName;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_CITY, nullable = false, insertable = true, updatable = true)
    private String city;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_COUNTRY, nullable = false, insertable = true, updatable = true)
    private String country;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_COORDINATES, nullable = false, insertable = true, updatable = true)
    private String coordinates;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TIMEZONE, nullable = false, insertable = true, updatable = true)
    private String timezone;
}
