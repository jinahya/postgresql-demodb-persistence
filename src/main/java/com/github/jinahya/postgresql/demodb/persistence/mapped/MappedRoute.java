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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
 * refresh, or by the extending entity, which sets them through their {@code protected} setters in
 * step with the associations which write the columns. They carry {@code @NotNull}, which holds at
 * {@code prePersist} only so: an extending entity which writes a column without setting its code
 * has an instance about to be inserted rejected, though the association would write the column.
 * <p>
 * The table has no primary key. What keeps a row apart is the {@code routes_route_no_validity_excl}
 * exclusion constraint: no two rows share a {@value #COLUMN_NAME_ROUTE_NO} with overlapping
 * {@value #COLUMN_NAME_VALIDITY} periods. So the two are the identifier, an
 * {@link EmbeddedId @EmbeddedId} of a subclass of {@link MappedRouteId}, {@code T}, which an
 * extending entity names; {@link #equals(Object)} compares the identifiers, and
 * {@link #getRouteNo()} and {@link #getValidityRange(BiFunction)} read them. An identifier is not
 * modified once created; a route takes another with {@link #setId(MappedRouteId)}.
 * <p>
 * The {@value MappedRoute#COLUMN_NAME_VALIDITY} column is a {@code tstzrange}, which neither
 * Jakarta Persistence nor Hibernate ORM maps; the identifier maps it as its two bounds, the lower,
 * inclusive, and the upper, exclusive, through {@link MappedRouteId.ValidityConverter}. A bound is
 * an instant, whatever time zone the session prints it in, so neither the identifier nor
 * {@link #equals(Object)} depends on the time zone the JVM runs in.
 * <p>
 * The specification has neither an attribute of a type variable nor
 * {@link jakarta.persistence.Convert @Convert} on an identifier's attribute; Hibernate ORM, the
 * only provider, maps both.
 * <p>
 * The {@value #COLUMN_NAME_DAYS_OF_WEEK} column is an {@code integer[]}, mapped as a {@link List}
 * of {@link Integer}, which Hibernate ORM binds and reads as an array; the
 * {@value #COLUMN_NAME_DURATION} column is an {@code interval}, mapped as a {@link Duration}, which
 * Hibernate ORM binds and reads as an interval for
 * {@link JdbcTypeCode @JdbcTypeCode}{@code (}{@link SqlTypes#INTERVAL_SECOND}{@code )}, and as a
 * number without it. A {@link Duration} has no months; the column's values are hours and minutes.
 *
 * @param <T> the type of the identifier.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedRoute<T extends MappedRouteId> {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "routes";

    // ------------------------------------------------------------------------------------------ id

    /**
     * The name of the attribute which holds the identifier, the
     * {@value #COLUMN_NAME_ROUTE_NO} and {@value #COLUMN_NAME_VALIDITY} columns. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    // ------------------------------------------------------------------------------------ route_no

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_PATH_ID_ROUTE_NO} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ROUTE_NO = "route_no";

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_ROUTE_NO} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_ROUTE_NO =
            ATTRIBUTE_NAME_ID + '.' + MappedRouteId.ATTRIBUTE_NAME_ROUTE_NO;

    // ------------------------------------------------------------------------------------ validity

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_PATH_ID_VALIDITY} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_VALIDITY = "validity";

    /**
     * The path of the identifier's attribute which maps the {@value #COLUMN_NAME_VALIDITY} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_PATH_ID_VALIDITY =
            ATTRIBUTE_NAME_ID + '.' + MappedRouteId.ATTRIBUTE_NAME_VALIDITY;

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

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedRoute() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "id=" + id +
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
     * @implSpec Equality is by the identifier, {@value MappedRouteId#ATTRIBUTE_NAME_ROUTE_NO}
     *         and {@value MappedRouteId#ATTRIBUTE_NAME_VALIDITY}, the latter's bounds compared as
     *         instants.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedRoute<?> that)) {
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

    // ------------------------------------------------------------------------------------- routeNo

    /**
     * Returns the {@value MappedRouteId#ATTRIBUTE_NAME_ROUTE_NO} of the identifier.
     *
     * @return the {@value MappedRouteId#ATTRIBUTE_NAME_ROUTE_NO} of the identifier; {@code null}
     *         when the identifier is {@code null}.
     */
    public String getRouteNo() {
        return Optional.ofNullable(getId())
                .map(MappedRouteId::getRouteNo)
                .orElse(null);
    }

    // ------------------------------------------------------------------------------------ validity

    /**
     * Applies the specified function to the bounds of the
     * {@value MappedRouteId#ATTRIBUTE_NAME_VALIDITY} of the identifier, and returns the result.
     *
     * @param <R>      the type of the result.
     * @param function a function of the lower bound, inclusive, and the upper bound, exclusive.
     * @return the result of {@code function}; or {@code null} when the identifier, or its
     *         {@value MappedRouteId#ATTRIBUTE_NAME_VALIDITY}, is {@code null}.
     */
    public <R> R getValidityRange(
            final BiFunction<? super Instant, ? super Instant, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        final var id = getId();
        if (id == null) {
            return null;
        }
        return id.getValidityRange(function);
    }

    // ---------------------------------------------------------------------------- departureAirport

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute, which is
     * {@code null} until this instance is loaded, or until an extending entity sets it with the
     * association which writes the column.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute.
     */
    public String getDepartureAirport() {
        return departureAirport;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute with the
     * specified value.
     *
     * @param departureAirport new value for {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute.
     * @apiNote The attribute is read-only, so the value is never written: an extending
     *         entity which maps the column's writable side, an association, calls this method to
     *         keep the attribute in step with what the association writes.
     */
    protected void setDepartureAirport(final String departureAirport) {
        this.departureAirport = departureAirport;
    }

    // ------------------------------------------------------------------------------ arrivalAirport

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute, which is
     * {@code null} until this instance is loaded, or until an extending entity sets it with the
     * association which writes the column.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute.
     */
    public String getArrivalAirport() {
        return arrivalAirport;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute with the
     * specified value.
     *
     * @param arrivalAirport new value for {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute.
     * @apiNote The attribute is read-only, so the value is never written: an extending
     *         entity which maps the column's writable side, an association, calls this method to
     *         keep the attribute in step with what the association writes.
     */
    protected void setArrivalAirport(final String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    // -------------------------------------------------------------------------------- airplaneCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute, which is
     * {@code null} until this instance is loaded, or until an extending entity sets it with the
     * association which writes the column.
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
     * @apiNote The attribute is read-only, so the value is never written: an extending
     *         entity which maps the column's writable side, an association, calls this method to
     *         keep the attribute in step with what the association writes.
     */
    protected void setAirplaneCode(final String airplaneCode) {
        this.airplaneCode = airplaneCode;
    }

    // ---------------------------------------------------------------------------------- daysOfWeek

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute.
     * @apiNote This method is {@code protected}: the attribute is the column's numbers, and the
     *         days are read as {@link DayOfWeek}s with {@link #getDaysOfWeekSet()}.
     */
    protected List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute with the specified
     * value.
     *
     * @param daysOfWeek new value for {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute.
     * @apiNote This method is {@code protected}: the attribute is the column's numbers, and the
     *         days are written as {@link DayOfWeek}s with {@link #setDaysOfWeekSet(Set)}.
     */
    protected void setDaysOfWeek(final List<Integer> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    /**
     * Returns the days of the week of the {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute, as the
     * column numbers them: {@code 1} for {@link DayOfWeek#MONDAY} to {@code 7} for
     * {@link DayOfWeek#SUNDAY}, as {@link DayOfWeek#of(int)} reads them.
     *
     * @return a new set of the days, from Monday to Sunday; {@code null} when the attribute is
     *         {@code null}. Changing the set does not change the attribute.
     * @throws java.time.DateTimeException when the attribute holds a number other than {@code 1}
     *                                     to {@code 7}, which no check constraint of the column
     *                                     forbids.
     */
    @Transient
    public Set<DayOfWeek> getDaysOfWeekSet() {
        return Optional.ofNullable(getDaysOfWeek())
                .map(l -> l.stream()
                        .map(DayOfWeek::of)
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(DayOfWeek.class))))
                .orElse(null);
    }

    /**
     * Replaces the {@value #ATTRIBUTE_NAME_DAYS_OF_WEEK} attribute with the numbers of the
     * specified days, {@link DayOfWeek#getValue()}, in ascending order, as the column keeps them.
     *
     * @param daysOfWeekSet the days of the week; {@code null} for a {@code null} attribute.
     */
    public void setDaysOfWeekSet(final Set<DayOfWeek> daysOfWeekSet) {
        setDaysOfWeek(
                Optional.ofNullable(daysOfWeekSet)
                        .map(v -> v.stream()
                                .map(DayOfWeek::getValue)
                                .sorted()
                                .collect(Collectors.toCollection(ArrayList::new)))
                        .orElse(null)
        );
    }

    // ------------------------------------------------------------------------------- scheduledTime

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

    // ------------------------------------------------------------------------------------ duration

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DURATION} attribute -- an {@code interval}
     * column, mapped as a {@link Duration}.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DURATION} attribute.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_DURATION} attribute with the specified
     * value.
     *
     * @param duration new value for {@value #ATTRIBUTE_NAME_DURATION} attribute.
     */
    public void setDuration(final Duration duration) {
        this.duration = duration;
    }

    // ---------------------------------------------------------------------------------------------
    @Valid
    @NotNull
    @EmbeddedId
    private T id;

    // ---------------------------------------------------------------------------------------------
    // read-only: the extending entity owns the writable mapping; see the class documentation
    @Size(min = SIZE_MIN_DEPARTURE_AIRPORT, max = SIZE_MAX_DEPARTURE_AIRPORT)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_DEPARTURE_AIRPORT,
            nullable = false,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_DEPARTURE_AIRPORT
    )
    private String departureAirport;

    // ---------------------------------------------------------------------------------------------
    // read-only: the extending entity owns the writable mapping; see the class documentation
    @Size(min = SIZE_MIN_ARRIVAL_AIRPORT, max = SIZE_MAX_ARRIVAL_AIRPORT)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ARRIVAL_AIRPORT, nullable = false, insertable = false,
            updatable = false, length = COLUMN_LENGTH_ARRIVAL_AIRPORT
    )
    private String arrivalAirport;

    // ---------------------------------------------------------------------------------------------
    // read-only: the extending entity owns the writable mapping; see the class documentation
    @Size(min = SIZE_MIN_AIRPLANE_CODE, max = SIZE_MAX_AIRPLANE_CODE)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AIRPLANE_CODE, nullable = false, insertable = false,
            updatable = false, length = COLUMN_LENGTH_AIRPLANE_CODE
    )
    private String airplaneCode;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_DAYS_OF_WEEK, nullable = false, insertable = true, updatable = true)
    private List<@Positive @NotNull Integer> daysOfWeek;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SCHEDULED_TIME, nullable = false, insertable = true,
            updatable = true)
    private LocalTime scheduledTime;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_DURATION, nullable = false, insertable = true, updatable = true)
    private Duration duration;
}
