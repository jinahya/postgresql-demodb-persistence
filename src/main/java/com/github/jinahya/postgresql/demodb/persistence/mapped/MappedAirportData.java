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

import com.github.jinahya.persistence.more.converter.json.JsonpStringAttributeConverter;
import com.github.jinahya.postgresql.demodb.persistence.converter.PointAttributeConverter;
import com.github.jinahya.persistence.more.converter.time.ZoneIdStringAttributeConverter;
import jakarta.json.JsonObject;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Supplier;

/**
 * A mapped superclass which holds the mappings of the {@value MappedAirportData#TABLE_NAME} table,
 * <q>Airports (internal multilingual data)</q>.
 * <p>
 * The {@value MappedAirportData#COLUMN_NAME_AIRPORT_NAME},
 * {@value MappedAirportData#COLUMN_NAME_CITY} and {@value MappedAirportData#COLUMN_NAME_COUNTRY}
 * columns are {@code jsonb}, an object of one name per language, keyed {@code en}, {@code ru}, ...;
 * each is mapped as a {@link JsonObject}, through a {@link JsonpStringAttributeConverter.OfJsonObject},
 * with package-private accessors, and is read and written a name at a time, by the language of a
 * {@link Locale}. The {@code airports} view, see {@link MappedAirport}, picks the session's
 * language out of them.
 * <p>
 * The {@value MappedAirportData#COLUMN_NAME_COORDINATES} column is a {@code point}, which has no type
 * in Jakarta Persistence; it is mapped as a {@link PointAttributeConverter.Point}, the longitude as
 * {@code x} and the latitude as {@code y}, through a {@link PointAttributeConverter}.
 * <p>
 * Writing a text back, the names' or the point's, needs the driver to leave its type to the server,
 * {@code stringtype=unspecified} on the JDBC URL.
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

    // -------------------------------------------------------------------------------- airport_code

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

    // -------------------------------------------------------------------------------- airport_name

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

    // ---------------------------------------------------------------------------------------- city

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

    // ------------------------------------------------------------------------------------- country

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

    // --------------------------------------------------------------------------------- coordinates

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

    // ------------------------------------------------------------------------------------ timezone

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

    // ---------------------------------------------------------------------------------------------
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

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedAirportData() {
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

    // --------------------------------------------------------------------------------- airportCode

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

    // --------------------------------------------------------------------------------- airportName

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute -- a {@code jsonb}
     * column, mapped as the names it holds, keyed by language.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute.
     * @see #getAirportName(Locale)
     */
    JsonObject getAirportName() {
        return airportName;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute with the specified
     * value.
     *
     * @param airportName new value for {@value #ATTRIBUTE_NAME_AIRPORT_NAME} attribute.
     * @see #setAirportName(Locale, String)
     */
    void setAirportName(final JsonObject airportName) {
        this.airportName = airportName;
    }

    /**
     * Returns the name of the airport in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the airport.
     * @return the name of the airport in the language of {@code locale}; {@code null} if there is
     *         none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    public String getAirportName(final Locale locale) {
        return _LocaleNames.get(getAirportName(), locale);
    }

    /**
     * Replaces the name of the airport in the language of the specified locale with the specified
     * value.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the airport.
     * @param name   the new name of the airport in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code name} is
     *                                  blank.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute.
     * @see #removeAirportName(Locale)
     */
    public void setAirportName(final Locale locale, final String name) {
        setAirportName(_LocaleNames.with(getAirportName(), locale, name));
    }

    /**
     * Removes the name of the airport in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the airport.
     * @return the name removed; {@code null} if there was none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute; nothing is replaced when there was no
     *         name.
     */
    public String removeAirportName(final Locale locale) {
        final var removed = getAirportName(locale);
        if (removed != null) {
            setAirportName(_LocaleNames.without(getAirportName(), locale));
        }
        return removed;
    }

    // ---------------------------------------------------------------------------------------- city

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_CITY} attribute -- a {@code jsonb} column,
     * mapped as the names it holds, keyed by language.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_CITY} attribute.
     * @see #getCityName(Locale)
     */
    JsonObject getCity() {
        return city;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_CITY} attribute with the specified value.
     *
     * @param city new value for {@value #ATTRIBUTE_NAME_CITY} attribute.
     * @see #setCityName(Locale, String)
     */
    void setCity(final JsonObject city) {
        this.city = city;
    }

    /**
     * Returns the name of the city in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the city.
     * @return the name of the city in the language of {@code locale}; {@code null} if there is
     *         none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    public String getCityName(final Locale locale) {
        return _LocaleNames.get(getCity(), locale);
    }

    /**
     * Replaces the name of the city in the language of the specified locale with the specified
     * value.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the city.
     * @param name   the new name of the city in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code name} is
     *                                  blank.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute.
     * @see #removeCityName(Locale)
     */
    public void setCityName(final Locale locale, final String name) {
        setCity(_LocaleNames.with(getCity(), locale, name));
    }

    /**
     * Removes the name of the city in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the city.
     * @return the name removed; {@code null} if there was none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute; nothing is replaced when there was no
     *         name.
     */
    public String removeCityName(final Locale locale) {
        final var removed = getCityName(locale);
        if (removed != null) {
            setCity(_LocaleNames.without(getCity(), locale));
        }
        return removed;
    }

    // ------------------------------------------------------------------------------------- country

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute -- a {@code jsonb}
     * column, mapped as the names it holds, keyed by language.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     * @see #getCountryName(Locale)
     */
    JsonObject getCountry() {
        return country;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_COUNTRY} attribute with the specified
     * value.
     *
     * @param country new value for {@value #ATTRIBUTE_NAME_COUNTRY} attribute.
     * @see #setCountryName(Locale, String)
     */
    void setCountry(final JsonObject country) {
        this.country = country;
    }

    /**
     * Returns the name of the country in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the country.
     * @return the name of the country in the language of {@code locale}; {@code null} if there is
     *         none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    public String getCountryName(final Locale locale) {
        return _LocaleNames.get(getCountry(), locale);
    }

    /**
     * Replaces the name of the country in the language of the specified locale with the specified
     * value.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the country.
     * @param name   the new name of the country in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code name} is
     *                                  blank.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute.
     * @see #removeCountryName(Locale)
     */
    public void setCountryName(final Locale locale, final String name) {
        setCountry(_LocaleNames.with(getCountry(), locale, name));
    }

    /**
     * Removes the name of the country in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the country.
     * @return the name removed; {@code null} if there was none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute; nothing is replaced when there was no
     *         name.
     */
    public String removeCountryName(final Locale locale) {
        final var removed = getCountryName(locale);
        if (removed != null) {
            setCountry(_LocaleNames.without(getCountry(), locale));
        }
        return removed;
    }

    // --------------------------------------------------------------------------------- coordinates

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute -- a {@code point}
     * column, mapped as a {@link PointAttributeConverter.Point}, the longitude as {@code x} and the
     * latitude as {@code y}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute.
     */
    public PointAttributeConverter.Point getCoordinates() {
        return coordinates;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_COORDINATES} attribute with the specified
     * value.
     *
     * @param coordinates new value for {@value #ATTRIBUTE_NAME_COORDINATES} attribute.
     */
    public void setCoordinates(final PointAttributeConverter.Point coordinates) {
        this.coordinates = coordinates;
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
     * Replaces current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute with the specified
     * value.
     *
     * @param timezone new value for {@value #ATTRIBUTE_NAME_TIMEZONE} attribute.
     */
    public void setTimezone(final ZoneId timezone) {
        this.timezone = timezone;
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

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_TIMEZONE} attribute with the specified
     * {@link TimeZone}.
     *
     * @param timezone new value for {@value #ATTRIBUTE_NAME_TIMEZONE} attribute, as a
     *                 {@link TimeZone}; may be {@code null}.
     */
    @Transient
    public void setTimezoneFromTimeZone(final TimeZone timezone) {
        setTimezone(Optional.ofNullable(timezone).map(TimeZone::toZoneId).orElse(null));
    }

    // ---------------------------------------------------------------------------------------------
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

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = JsonpStringAttributeConverter.OfJsonObject.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AIRPORT_NAME, nullable = false, insertable = true, updatable = true)
    private JsonObject airportName;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = JsonpStringAttributeConverter.OfJsonObject.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_CITY, nullable = false, insertable = true, updatable = true)
    private JsonObject city;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = JsonpStringAttributeConverter.OfJsonObject.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_COUNTRY, nullable = false, insertable = true, updatable = true)
    private JsonObject country;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = PointAttributeConverter.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_COORDINATES, nullable = false, insertable = true, updatable = true)
    private PointAttributeConverter.Point coordinates;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = ZoneIdStringAttributeConverter.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_TIMEZONE, nullable = false, insertable = true, updatable = true)
    private ZoneId timezone;
}
