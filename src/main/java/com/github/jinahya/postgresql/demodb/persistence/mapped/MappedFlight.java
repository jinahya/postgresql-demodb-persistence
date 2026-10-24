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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A mapped superclass which holds the mappings of the {@value MappedFlight#TABLE_NAME} table,
 * <q>Flights</q>.
 * <p>
 * The {@value MappedFlight#COLUMN_NAME_FLIGHT_ID} column is an identity column,
 * {@code GENERATED ALWAYS AS IDENTITY}: the database assigns it on insert, and rejects a value
 * supplied for it, so the {@code @Id} is {@link GeneratedValue @GeneratedValue} with
 * {@link GenerationType#IDENTITY} and is read back once the row is inserted; it carries no
 * {@code @NotNull}, an instance about to be inserted having none yet.
 * <p>
 * The {@value #COLUMN_NAME_ROUTE_NO} column names a row of the {@code routes} table, see
 * {@link MappedRoute}, which has no primary key for a foreign key to point at; the column is
 * therefore mapped as a plain, writable value.
 * <p>
 * The table also keeps ({@value #COLUMN_NAME_ROUTE_NO}, {@value #COLUMN_NAME_SCHEDULED_DEPARTURE})
 * unique, which an extending entity may state in its {@link jakarta.persistence.Table @Table}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedFlight {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "flights";

    // ------------------------------------------------------------------------------------------------------- FLIGHT_ID

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_FLIGHT_ID = "flight_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FLIGHT_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_FLIGHT_ID = "flightId";

    // -------------------------------------------------------------------------------------------------------- ROUTE_NO

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

    // ---------------------------------------------------------------------------------------------------------- STATUS

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_STATUS} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_STATUS = "status";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_STATUS} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_STATUS = "status";

    /**
     * A value of the {@value #COLUMN_NAME_STATUS} column, {@code Scheduled}. The value is
     * {@value}.
     */
    public static final String COLUMN_VALUE_STATUS_SCHEDULED = "Scheduled";

    /**
     * A value of the {@value #COLUMN_NAME_STATUS} column, {@code On Time}. The value is {@value}.
     */
    public static final String COLUMN_VALUE_STATUS_ON_TIME = "On Time";

    /**
     * A value of the {@value #COLUMN_NAME_STATUS} column, {@code Delayed}. The value is {@value}.
     */
    public static final String COLUMN_VALUE_STATUS_DELAYED = "Delayed";

    /**
     * A value of the {@value #COLUMN_NAME_STATUS} column, {@code Boarding}. The value is {@value}.
     */
    public static final String COLUMN_VALUE_STATUS_BOARDING = "Boarding";

    /**
     * A value of the {@value #COLUMN_NAME_STATUS} column, {@code Departed}. The value is {@value}.
     */
    public static final String COLUMN_VALUE_STATUS_DEPARTED = "Departed";

    /**
     * A value of the {@value #COLUMN_NAME_STATUS} column, {@code Arrived}. The value is {@value}.
     */
    public static final String COLUMN_VALUE_STATUS_ARRIVED = "Arrived";

    /**
     * A value of the {@value #COLUMN_NAME_STATUS} column, {@code Cancelled}. The value is
     * {@value}.
     */
    public static final String COLUMN_VALUE_STATUS_CANCELLED = "Cancelled";

    /**
     * The regular expression which the {@value #ATTRIBUTE_NAME_STATUS} attribute matches: one of
     * the values the {@code flight_status_check} constraint allows. The value is {@value}.
     */
    public static final String PATTERN_REGEXP_STATUS =
            "Scheduled|On Time|Delayed|Boarding|Departed|Arrived|Cancelled";

    // --------------------------------------------------------------------------------------------- SCHEDULED_DEPARTURE

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SCHEDULED_DEPARTURE = "scheduled_departure";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SCHEDULED_DEPARTURE} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SCHEDULED_DEPARTURE = "scheduledDeparture";

    // ----------------------------------------------------------------------------------------------- SCHEDULED_ARRIVAL

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SCHEDULED_ARRIVAL = "scheduled_arrival";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SCHEDULED_ARRIVAL} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SCHEDULED_ARRIVAL = "scheduledArrival";

    // ------------------------------------------------------------------------------------------------ ACTUAL_DEPARTURE

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTUAL_DEPARTURE = "actual_departure";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTUAL_DEPARTURE} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTUAL_DEPARTURE = "actualDeparture";

    // -------------------------------------------------------------------------------------------------- ACTUAL_ARRIVAL

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTUAL_ARRIVAL = "actual_arrival";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTUAL_ARRIVAL} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTUAL_ARRIVAL = "actualArrival";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedFlight() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "flightId=" + flightId +
               ",routeNo=" + routeNo +
               ",status=" + status +
               ",scheduledDeparture=" + scheduledDeparture +
               ",scheduledArrival=" + scheduledArrival +
               ",actualDeparture=" + actualDeparture +
               ",actualArrival=" + actualArrival +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@link Id @Id} attribute,
     *         {@value #ATTRIBUTE_NAME_FLIGHT_ID}, alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedFlight that)) {
            return false;
        }
        return Objects.equals(getFlightId(), that.getFlightId());
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
        return Objects.hashCode(getFlightId());
    }

    // ---------------------------------------------------------------------------------------------- Jakarta-Validation

    /**
     * Tests whether current value of {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL} attribute is after
     * current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE} attribute, as the
     * {@code flight_scheduled_check} constraint, {@code scheduled_arrival > scheduled_departure}
     * requires.
     *
     * @return {@code true} if the current value of the {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL}
     *         attribute is after the current value of the
     *         {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE} attribute, or either of them is
     *         {@code null}; {@code false} otherwise.
     */
    @AssertTrue
    protected boolean isScheduledArrivalAfterScheduledDeparture() {
        final var scheduledArrival = getScheduledArrival();
        if (scheduledArrival == null) {
            return true;
        }
        final var scheduledDeparture = getScheduledDeparture();
        if (scheduledDeparture == null) {
            return true;
        }
        return scheduledArrival.isAfter(scheduledDeparture);
    }

    /**
     * Tests whether current value of {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL} attribute is after
     * current value of {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE} attribute, as the
     * {@code flight_actual_check} constraint: {@code actual_arrival} is {@code null}, or both are
     * set and {@code actual_arrival > actual_departure} requires.
     *
     * @return {@code true} if the current value of the {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL}
     *         attribute is after the current value of the {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE}
     *         attribute, or either of them is {@code null}; {@code false} otherwise.
     */
    @AssertTrue
    protected boolean isActualArrivalAfterActualDeparture() {
        final var actualArrival = getActualArrival();
        if (actualArrival == null) {
            return true;
        }
        final var actualDeparture = getActualDeparture();
        if (actualDeparture == null) {
            return true;
        }
        return actualArrival.isAfter(actualDeparture);
    }

    // -------------------------------------------------------------------------------------------------------- flightId

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

    // ---------------------------------------------------------------------------------------------------------- status

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STATUS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STATUS} attribute.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_STATUS} attribute with the specified
     * value.
     *
     * @param status new value for {@value #ATTRIBUTE_NAME_STATUS} attribute.
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    // ---------------------------------------------------------------------------------------------- scheduledDeparture

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE} attribute.
     */
    public OffsetDateTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE} attribute with the
     * specified value.
     *
     * @param scheduledDeparture new value for {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE}
     *                           attribute.
     */
    public void setScheduledDeparture(final OffsetDateTime scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    // ------------------------------------------------------------------------------------------------ scheduledArrival

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL} attribute.
     */
    public OffsetDateTime getScheduledArrival() {
        return scheduledArrival;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL} attribute with the
     * specified value.
     *
     * @param scheduledArrival new value for {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL} attribute.
     */
    public void setScheduledArrival(final OffsetDateTime scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    // ------------------------------------------------------------------------------------------------- actualDeparture

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE} attribute.
     */
    public OffsetDateTime getActualDeparture() {
        return actualDeparture;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE} attribute with the
     * specified value.
     *
     * @param actualDeparture new value for {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE} attribute.
     */
    public void setActualDeparture(final OffsetDateTime actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    // --------------------------------------------------------------------------------------------------- actualArrival

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL} attribute.
     */
    public OffsetDateTime getActualArrival() {
        return actualArrival;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL} attribute with the
     * specified value.
     *
     * @param actualArrival new value for {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL} attribute.
     */
    public void setActualArrival(final OffsetDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_FLIGHT_ID, nullable = false, insertable = true, updatable = false)
    private Integer flightId;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ROUTE_NO, nullable = false, insertable = true, updatable = true)
    private String routeNo;

    // -----------------------------------------------------------------------------------------------------------------
    @Pattern(regexp = PATTERN_REGEXP_STATUS)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_STATUS, nullable = false, insertable = true, updatable = true)
    private String status;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SCHEDULED_DEPARTURE, nullable = false, insertable = true,
            updatable = true)
    private OffsetDateTime scheduledDeparture;

    // -----------------------------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SCHEDULED_ARRIVAL, nullable = false, insertable = true,
            updatable = true)
    private OffsetDateTime scheduledArrival;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ACTUAL_DEPARTURE, nullable = true, insertable = true,
            updatable = true)
    private OffsetDateTime actualDeparture;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ACTUAL_ARRIVAL, nullable = true, insertable = true, updatable = true)
    private OffsetDateTime actualArrival;
}
