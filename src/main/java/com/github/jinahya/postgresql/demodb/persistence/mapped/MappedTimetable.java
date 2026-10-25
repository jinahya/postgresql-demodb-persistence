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

import com.github.jinahya.postgresql.demodb.persistence.converter.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.converter.FlightStatusConverter;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A mapped superclass which holds the mappings of the {@value MappedTimetable#TABLE_NAME} view,
 * <q>Detailed info about flights</q>.
 * <p>
 * Every column is mapped read-only; only the identifier stays insertable, which EclipseLink
 * requires of an {@link Id @Id}. It has no setters; nothing writes to a view.
 * <p>
 * The view joins {@code flights}, see {@link MappedFlight}, with the {@code routes} row valid at
 * the scheduled departure and with the two airports, and adds each time in its airport's time zone,
 * the {@code *_local} columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedTimetable {

    /**
     * The name of the database view to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "timetable";

    // ----------------------------------------------------------------------------------- flight_id

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_FLIGHT_ID = "flight_id";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_FLIGHT_ID} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_FLIGHT_ID = "flightId";

    // ------------------------------------------------------------------------------------ route_no

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute maps.
     * The value is {@value}.
     */
    public static final String COLUMN_NAME_ROUTE_NO = "route_no";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ROUTE_NO} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_ROUTE_NO = "routeNo";

    // --------------------------------------------------------------------------- departure_airport

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute
     * maps. The value is {@value}.
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
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute
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

    // -------------------------------------------------------------------------------------- status

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_STATUS} attribute maps. The
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
     * the values of the {@code flights} table's {@code flight_status_check} constraint. The value
     * is {@value}.
     */
    public static final String PATTERN_REGEXP_STATUS =
            "Scheduled|On Time|Delayed|Boarding|Departed|Arrived|Cancelled";

    // ------------------------------------------------------------------------------- airplane_code

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute
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

    // ------------------------------------------------------------------------- scheduled_departure

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SCHEDULED_DEPARTURE = "scheduled_departure";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SCHEDULED_DEPARTURE} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SCHEDULED_DEPARTURE = "scheduledDeparture";

    // ---------------------------------------------------------------------------------------
    // SCHEDULED_DEPARTURE_LOCAL

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE_LOCAL}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SCHEDULED_DEPARTURE_LOCAL = "scheduled_departure_local";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SCHEDULED_DEPARTURE_LOCAL}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SCHEDULED_DEPARTURE_LOCAL = "scheduledDepartureLocal";

    // ---------------------------------------------------------------------------- actual_departure

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTUAL_DEPARTURE = "actual_departure";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTUAL_DEPARTURE} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTUAL_DEPARTURE = "actualDeparture";

    // ------------------------------------------------------------------------------------------
    // ACTUAL_DEPARTURE_LOCAL

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE_LOCAL}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTUAL_DEPARTURE_LOCAL = "actual_departure_local";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTUAL_DEPARTURE_LOCAL} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTUAL_DEPARTURE_LOCAL = "actualDepartureLocal";

    // --------------------------------------------------------------------------- scheduled_arrival

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SCHEDULED_ARRIVAL = "scheduled_arrival";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SCHEDULED_ARRIVAL} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SCHEDULED_ARRIVAL = "scheduledArrival";

    // -----------------------------------------------------------------------------------------
    // SCHEDULED_ARRIVAL_LOCAL

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL_LOCAL}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_SCHEDULED_ARRIVAL_LOCAL = "scheduled_arrival_local";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SCHEDULED_ARRIVAL_LOCAL}
     * column. The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_SCHEDULED_ARRIVAL_LOCAL = "scheduledArrivalLocal";

    // ------------------------------------------------------------------------------ actual_arrival

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTUAL_ARRIVAL = "actual_arrival";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTUAL_ARRIVAL} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTUAL_ARRIVAL = "actualArrival";

    // ------------------------------------------------------------------------ actual_arrival_local

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL_LOCAL}
     * attribute maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_ACTUAL_ARRIVAL_LOCAL = "actual_arrival_local";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_ACTUAL_ARRIVAL_LOCAL} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ACTUAL_ARRIVAL_LOCAL = "actualArrivalLocal";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedTimetable() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "flightId=" + flightId +
               ",routeNo=" + routeNo +
               ",departureAirport=" + departureAirport +
               ",arrivalAirport=" + arrivalAirport +
               ",status=" + status +
               ",airplaneCode=" + airplaneCode +
               ",scheduledDeparture=" + scheduledDeparture +
               ",scheduledDepartureLocal=" + scheduledDepartureLocal +
               ",actualDeparture=" + actualDeparture +
               ",actualDepartureLocal=" + actualDepartureLocal +
               ",scheduledArrival=" + scheduledArrival +
               ",scheduledArrivalLocal=" + scheduledArrivalLocal +
               ",actualArrival=" + actualArrival +
               ",actualArrivalLocal=" + actualArrivalLocal +
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
        if (!(obj instanceof MappedTimetable that)) {
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

    // -------------------------------------------------------------------------------------------------------- flightId

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_FLIGHT_ID} attribute.
     */
    public Integer getFlightId() {
        return flightId;
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

    // ------------------------------------------------------------------------------------------------ departureAirport

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_DEPARTURE_AIRPORT} attribute.
     */
    public String getDepartureAirport() {
        return departureAirport;
    }

    // -------------------------------------------------------------------------------------------------- arrivalAirport

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ARRIVAL_AIRPORT} attribute.
     */
    public String getArrivalAirport() {
        return arrivalAirport;
    }

    // ---------------------------------------------------------------------------------------------------------- status

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_STATUS} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_STATUS} attribute.
     */
    public FlightStatus getStatus() {
        return status;
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

    // ---------------------------------------------------------------------------------------------- scheduledDeparture

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE} attribute.
     */
    public OffsetDateTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    // -----------------------------------------------------------------------------------------
    // scheduledDepartureLocal

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE_LOCAL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SCHEDULED_DEPARTURE_LOCAL} attribute.
     */
    public LocalDateTime getScheduledDepartureLocal() {
        return scheduledDepartureLocal;
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

    // -------------------------------------------------------------------------------------------- actualDepartureLocal

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE_LOCAL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTUAL_DEPARTURE_LOCAL} attribute.
     */
    public LocalDateTime getActualDepartureLocal() {
        return actualDepartureLocal;
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

    // ------------------------------------------------------------------------------------------- scheduledArrivalLocal

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL_LOCAL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SCHEDULED_ARRIVAL_LOCAL} attribute.
     */
    public LocalDateTime getScheduledArrivalLocal() {
        return scheduledArrivalLocal;
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

    // ---------------------------------------------------------------------------------------------- actualArrivalLocal

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL_LOCAL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ACTUAL_ARRIVAL_LOCAL} attribute.
     */
    public LocalDateTime getActualArrivalLocal() {
        return actualArrivalLocal;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @Basic(optional = true)
    // insertable=false on an @Id -- "There should be one non-read-only mapping defined for the
    // primary key field", EclipseLink-46. Nothing writes to a view anyway.
    @Column(name = COLUMN_NAME_FLIGHT_ID, nullable = true, insertable = true, updatable = false)
    private Integer flightId;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ROUTE_NO, nullable = true, insertable = false, updatable = false)
    private String routeNo;

    // -----------------------------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_DEPARTURE_AIRPORT, max = SIZE_MAX_DEPARTURE_AIRPORT)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_DEPARTURE_AIRPORT,
            nullable = true,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_DEPARTURE_AIRPORT
    )
    private String departureAirport;

    // -----------------------------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_ARRIVAL_AIRPORT, max = SIZE_MAX_ARRIVAL_AIRPORT)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ARRIVAL_AIRPORT,
            nullable = true,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_ARRIVAL_AIRPORT
    )
    private String arrivalAirport;

    // -----------------------------------------------------------------------------------------------------------------
    @Convert(converter = FlightStatusConverter.class)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_STATUS, nullable = true, insertable = false, updatable = false)
    private FlightStatus status;

    // -----------------------------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_AIRPLANE_CODE, max = SIZE_MAX_AIRPLANE_CODE)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_AIRPLANE_CODE,
            nullable = true,
            insertable = false,
            updatable = false,
            length = COLUMN_LENGTH_AIRPLANE_CODE
    )
    private String airplaneCode;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_SCHEDULED_DEPARTURE, nullable = true, insertable = false,
            updatable = false)
    private OffsetDateTime scheduledDeparture;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_SCHEDULED_DEPARTURE_LOCAL, nullable = true, insertable = false,
            updatable = false)
    private LocalDateTime scheduledDepartureLocal;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ACTUAL_DEPARTURE, nullable = true, insertable = false,
            updatable = false)
    private OffsetDateTime actualDeparture;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ACTUAL_DEPARTURE_LOCAL, nullable = true, insertable = false,
            updatable = false)
    private LocalDateTime actualDepartureLocal;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_SCHEDULED_ARRIVAL, nullable = true, insertable = false,
            updatable = false)
    private OffsetDateTime scheduledArrival;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_SCHEDULED_ARRIVAL_LOCAL, nullable = true, insertable = false,
            updatable = false)
    private LocalDateTime scheduledArrivalLocal;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ACTUAL_ARRIVAL, nullable = true, insertable = false,
            updatable = false)
    private OffsetDateTime actualArrival;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ACTUAL_ARRIVAL_LOCAL, nullable = true, insertable = false,
            updatable = false)
    private LocalDateTime actualArrivalLocal;
}
