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

import com.github.jinahya.postgresql.demodb.persistence.converter.IntegerListArrayAttributeConverter;
import com.github.jinahya.postgresql.demodb.persistence.converter.IntervalPostgresAttributeConverter;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * A mapped superclass which holds the mappings of the {@value MappedRoute#TABLE_NAME} table,
 * <q>Routes</q>.
 * <p>
 * The {@value MappedRoute#COLUMN_NAME_DEPARTURE_AIRPORT},
 * {@value MappedRoute#COLUMN_NAME_ARRIVAL_AIRPORT}, {@value MappedRoute#COLUMN_NAME_AIRPLANE_CODE}
 * columns are mapped read-only ({@code insertable = false, updatable = false}), with a
 * {@code protected} getter and no setter, for {@link #toString()} and for queries. How the
 * relationship behind each is mapped -- fetch type, cascade, whether there is an association at all
 * -- is the extending entity's decision, so the extending entity also owns their writable mapping,
 * by an association's {@link jakarta.persistence.JoinColumn @JoinColumn} or by an
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride}. <strong>An extending entity
 * which maps neither never writes them.</strong> Being read-only, they are populated by a load or a
 * refresh only, which is also why they carry no {@code @NotNull}: an instance about to be inserted
 * has none there, and the column's {@code NOT NULL} is the database's to enforce, on whatever the
 * association writes.
 * <p>
 * The {@value MappedRoute#COLUMN_NAME_VALIDITY} column has no type in Jakarta Persistence
 * ({@code tstzrange}), so it is mapped as {@link String}, the text form the driver reads it as.
 * Writing such a text back needs the driver to leave its type to the server,
 * {@code stringtype=unspecified} on the JDBC URL, or a provider-specific type the extending entity
 * may choose instead.
 * <p>
 * The table has no primary key. What keeps a row apart is the {@code routes_route_no_validity_excl}
 * exclusion constraint: no two rows share a {@value #COLUMN_NAME_ROUTE_NO} with overlapping
 * {@value #COLUMN_NAME_VALIDITY} periods. So {@link #equals(Object)} compares those two attributes,
 * and an extending entity has to choose its own identifier -- an
 * {@link jakarta.persistence.IdClass @IdClass} over the two, for instance -- or read the table by
 * native queries only.
 * <p>
 * The {@value #COLUMN_NAME_DAYS_OF_WEEK} column is an {@code integer[]} and the
 * {@value #COLUMN_NAME_DURATION} column an {@code interval}, neither a type of Jakarta Persistence.
 Each is mapped through a converter of this package,
 * {@link IntegerListArrayAttributeConverter.OfString} and
 * {@link IntervalPostgresAttributeConverter.OfString}, which carries it as the column's text form,
 * so that no provider feature is relied on; writing the text back needs the driver to leave its type
 * to the server, {@code stringtype=unspecified} on the JDBC URL. The interval is mapped as a
 * {@link IntervalPostgresAttributeConverter.PostgresInterval}, which holds the months, the days and the time apart, as the column does; its
 * text is read in the {@code postgres} {@code IntervalStyle}, which the JDBC URL pins,
 * {@code options=-c IntervalStyle=postgres}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedRoute {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "routes";

    // ------------------------------------------------------------------------------------ route_no

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_ROUTE_NO = "route_no";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ROUTE_NO} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_ROUTE_NO = "routeNo";

    // ------------------------------------------------------------------------------------ validity

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_VALIDITY} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_VALIDITY = "validity";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_VALIDITY} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_VALIDITY = "validity";

    // --------------------------------------------------------------------------- departure_airport

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_DEPARTURE_AIRPORT = "departure_airport";

    /**
     * The length of the {@value #COLUMN_NAME_DEPARTURE_AIRPORT} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_DEPARTURE_AIRPORT = 3;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_DEPARTURE_AIRPORT} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_DEPARTURE_AIRPORT = "departureAirport";

    /**
     * The minimum size of the {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MIN_DEPARTURE_AIRPORT = 0;

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MAX_DEPARTURE_AIRPORT = COLUMN_LENGTH_DEPARTURE_AIRPORT;

    // ----------------------------------------------------------------------------- arrival_airport

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ARRIVAL_AIRPORT = "arrival_airport";

    /**
     * The length of the {@value #COLUMN_NAME_ARRIVAL_AIRPORT} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_ARRIVAL_AIRPORT = 3;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ARRIVAL_AIRPORT} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ARRIVAL_AIRPORT = "arrivalAirport";

    /**
     * The minimum size of the {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MIN_ARRIVAL_AIRPORT = 0;

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MAX_ARRIVAL_AIRPORT = COLUMN_LENGTH_ARRIVAL_AIRPORT;

    // ------------------------------------------------------------------------------- airplane_code

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
     * The name of the attribute which maps the {@value #COLUMN_NAME_AIRPLANE_CODE} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_AIRPLANE_CODE = "airplaneCode";

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

    // -------------------------------------------------------------------------------- days_of_week

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_DAYS_OF_WEEK = "days_of_week";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_DAYS_OF_WEEK} column. The value
     * is {@value}.
     */
    public static final String ATTRIBUTE_NAME_DAYS_OF_WEEK = "daysOfWeek";

    // ------------------------------------------------------------------------------ scheduled_time

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_SCHEDULED_TIME} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SCHEDULED_TIME = "scheduled_time";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SCHEDULED_TIME} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SCHEDULED_TIME = "scheduledTime";

    // ------------------------------------------------------------------------------------ duration

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_DURATION} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_DURATION = "duration";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_DURATION} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_DURATION = "duration";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedRoute() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "routeNo=" + routeNo +
               ",validity=" + validity +
               ",departureAirport=" + departureAirport +
               ",arrivalAirport=" + arrivalAirport +
               ",airplaneCode=" + airplaneCode +
               ",daysOfWeek=" + daysOfWeek +
               ",scheduledTime=" + scheduledTime +
               ",duration=" + duration +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by {@value #ATTRIBUTE_NAME_ROUTE_NO} and
     *         {@value #ATTRIBUTE_NAME_VALIDITY}.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedRoute that)) {
            return false;
        }
        return Objects.equals(getRouteNo(), that.getRouteNo())
               && Objects.equals(getValidity(), that.getValidity());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over {@value #ATTRIBUTE_NAME_ROUTE_NO} and
     *         {@value #ATTRIBUTE_NAME_VALIDITY}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getRouteNo(), getValidity());
    }

    // --------------------------------------------------------------------------------------------------------- routeNo

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute.
     */
    public String getRouteNo() {
        return routeNo;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute with the specified
     * value.
     *
     * @param routeNo new value for {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute.
     */
    public void setRouteNo(final String routeNo) {
        this.routeNo = routeNo;
    }

    // -------------------------------------------------------------------------------------------------------- validity

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_VALIDITY} attribute -- a {@code tstzrange}
     * column, mapped as its text form, such as
     * {@code ["2025-10-01 00:00:00+00","2025-11-01 00:00:00+00")}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_VALIDITY} attribute.
     */
    public String getValidity() {
        return validity;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_VALIDITY} attribute with the specified
     * value.
     *
     * @param validity new value for {@value #ATTRIBUTE_NAME_VALIDITY} attribute.
     */
    public void setValidity(final String validity) {
        this.validity = validity;
    }

    // ------------------------------------------------------------------------------------------------ departureAirport

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute, which is
     * {@code null} until this instance is loaded.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute.
     * @apiNote This method is {@code protected}, not package-private, so that a lazy proxy,
     *         which is a subclass in another package, can override it.
     */
    protected String getDepartureAirport() {
        return departureAirport;
    }

    // -------------------------------------------------------------------------------------------------- arrivalAirport

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute, which is
     * {@code null} until this instance is loaded.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute.
     * @apiNote This method is {@code protected}, not package-private, so that a lazy proxy,
     *         which is a subclass in another package, can override it.
     */
    protected String getArrivalAirport() {
        return arrivalAirport;
    }

    // ---------------------------------------------------------------------------------------------------- airplaneCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute, which is
     * {@code null} until this instance is loaded.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     * @apiNote This method is {@code protected}, not package-private, so that a lazy proxy,
     *         which is a subclass in another package, can override it.
     */
    protected String getAirplaneCode() {
        return airplaneCode;
    }

    // ------------------------------------------------------------------------------------------------------ daysOfWeek

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute.
     */
    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute with the specified
     * value.
     *
     * @param daysOfWeek new value for {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute.
     */
    public void setDaysOfWeek(final List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    // --------------------------------------------------------------------------------------------------- scheduledTime

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SCHEDULED_TIME} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SCHEDULED_TIME} attribute.
     */
    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_SCHEDULED_TIME} attribute with the
     * specified value.
     *
     * @param scheduledTime new value for {@value #ATTRIBUTE_NAME_SCHEDULED_TIME} attribute.
     */
    public void setScheduledTime(final LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    // -------------------------------------------------------------------------------------------------------- duration

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DURATION} attribute -- an {@code interval}
     * column, mapped as a {@link IntervalPostgresAttributeConverter.PostgresInterval}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DURATION} attribute.
     */
    public IntervalPostgresAttributeConverter.PostgresInterval getDuration() {
        return duration;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DURATION} attribute with the specified
     * value.
     *
     * @param duration new value for {@value #ATTRIBUTE_NAME_DURATION} attribute.
     */
    public void setDuration(final IntervalPostgresAttributeConverter.PostgresInterval duration) {
        this.duration = duration;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ROUTE_NO, nullable = false, insertable = true, updatable = true)
    private String routeNo;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_VALIDITY, nullable = false, insertable = true, updatable = true)
    private String validity;

    // -----------------------------------------------------------------------------------------------------------------
    // read-only: the extending entity owns the writable mapping; see the class documentation
    @Size(min = SIZE_MIN_DEPARTURE_AIRPORT, max = SIZE_MAX_DEPARTURE_AIRPORT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_DEPARTURE_AIRPORT,
            nullable = false,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_DEPARTURE_AIRPORT
    )
    private String departureAirport;

    // -----------------------------------------------------------------------------------------------------------------
    // read-only: the extending entity owns the writable mapping; see the class documentation
    @Size(min = SIZE_MIN_ARRIVAL_AIRPORT, max = SIZE_MAX_ARRIVAL_AIRPORT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ARRIVAL_AIRPORT,
            nullable = false,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_ARRIVAL_AIRPORT
    )
    private String arrivalAirport;

    // -----------------------------------------------------------------------------------------------------------------
    // read-only: the extending entity owns the writable mapping; see the class documentation
    @Size(min = SIZE_MIN_AIRPLANE_CODE, max = SIZE_MAX_AIRPLANE_CODE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AIRPLANE_CODE,
            nullable = false,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_AIRPLANE_CODE
    )
    private String airplaneCode;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = IntegerListArrayAttributeConverter.OfString.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_DAYS_OF_WEEK, nullable = false, insertable = true, updatable = true)
    private List<Integer> daysOfWeek;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SCHEDULED_TIME, nullable = false, insertable = true,
            updatable = true)
    private LocalTime scheduledTime;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = IntervalPostgresAttributeConverter.OfString.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_DURATION, nullable = false, insertable = true, updatable = true)
    private IntervalPostgresAttributeConverter.PostgresInterval duration;
}
