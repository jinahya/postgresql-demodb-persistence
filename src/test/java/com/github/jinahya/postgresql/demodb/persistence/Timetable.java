package com.github.jinahya.postgresql.demodb.persistence;

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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedTimetable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * An entity which maps the {@value MappedTimetable#TABLE_NAME} view, extending
 * {@link MappedTimetable} with what is an application's to decide: read-only associations to the
 * {@link Flight} the row shows, its departure and arrival {@link AirportData}, and its
 * {@link AirplaneData}.
 * <p>
 * A view has no foreign key; the associations rest on the view's definition, which reads each
 * {@code flights} row with its route, by the same {@value MappedTimetable#COLUMN_NAME_FLIGHT_ID},
 * and carries the route's airport and airplane codes. Nothing is written to a view, so every
 * association is {@code insertable = false, updatable = false}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = MappedTimetable.TABLE_NAME)
public class Timetable
        extends MappedTimetable {

    /**
     * Creates a new instance.
     */
    protected Timetable() {
        super();
    }

    // -------------------------------------------------------------------------------------- flight

    /**
     * Returns the flight this view row shows, over the
     * {@value MappedTimetable#COLUMN_NAME_FLIGHT_ID} column, which is the identifier; the
     * association is read-only.
     *
     * @return the flight.
     */
    public Flight getFlight() {
        return flight;
    }

    // ------------------------------------------------------------------------ departureAirportData

    /**
     * Returns the airport of departure, over the
     * {@value MappedTimetable#COLUMN_NAME_DEPARTURE_AIRPORT} column; the association is read-only.
     *
     * @return the departureAirportData.
     */
    public AirportData getDepartureAirportData() {
        return departureAirportData;
    }

    // -------------------------------------------------------------------------- arrivalAirportData

    /**
     * Returns the airport of arrival, over the {@value MappedTimetable#COLUMN_NAME_ARRIVAL_AIRPORT}
     * column; the association is read-only.
     *
     * @return the arrivalAirportData.
     */
    public AirportData getArrivalAirportData() {
        return arrivalAirportData;
    }

    // -------------------------------------------------------------------------------- airplaneData

    /**
     * Returns the airplane which flies the flight, over the
     * {@value MappedTimetable#COLUMN_NAME_AIRPLANE_CODE} column; the association is read-only.
     *
     * @return the airplaneData.
     */
    public AirplaneData getAirplaneData() {
        return airplaneData;
    }

    // ---------------------------------------------------------------------------------------------
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_FLIGHT_ID, nullable = false, insertable = false,
                updatable = false)
    private Flight flight;

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_DEPARTURE_AIRPORT, nullable = false, insertable = false,
                updatable = false)
    private AirportData departureAirportData;

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_ARRIVAL_AIRPORT, nullable = false, insertable = false,
                updatable = false)
    private AirportData arrivalAirportData;

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_AIRPLANE_CODE, nullable = false, insertable = false,
                updatable = false)
    private AirplaneData airplaneData;
}
