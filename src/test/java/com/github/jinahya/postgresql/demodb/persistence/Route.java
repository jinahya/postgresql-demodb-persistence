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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;

import java.util.Optional;

/**
 * An entity which maps the {@value MappedRoute#TABLE_NAME} table, extending {@link MappedRoute}
 * with what is an application's to decide: its associations, their fetch types and their cascades.
 * <p>
 * The table has no primary key, so the entity names the identifier {@link MappedRoute} takes:
 * {@link RouteId}, the {@value MappedRoute#COLUMN_NAME_ROUTE_NO} and
 * {@value MappedRoute#COLUMN_NAME_VALIDITY} columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NamedNativeQuery(
        name = "selectDurationSecondsByRouteNoAndValidity",
        query = """
                SELECT EXTRACT(EPOCH FROM duration) FROM routes
                WHERE route_no = ?1 AND validity = CAST(?2 AS tstzrange)"""
)
@Entity
@Table(name = MappedRoute.TABLE_NAME)
public class Route
        extends MappedRoute<RouteId> {

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance with the specified identifier.
     *
     * @param id a value for the identifier.
     * @return a new instance.
     */
    public static Route of(final RouteId id) {
        final var instance = new Route();
        instance.setId(id);
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected Route() {
        super();
    }

    // ------------------------------------------------------------------------ departureAirportData

    /**
     * Returns the airport of departure, over the {@value MappedRoute#COLUMN_NAME_DEPARTURE_AIRPORT}
     * column; the column's writable mapping.
     *
     * @return the departureAirportData.
     */
    public AirportData getDepartureAirportData() {
        return departureAirportData;
    }

    /**
     * Replaces the departureAirportData with the specified value, and the inherited, read-only
     * attribute over the {@value MappedRoute#COLUMN_NAME_DEPARTURE_AIRPORT} column with its airport
     * code, which is what this association writes.
     *
     * @param departureAirportData new value for the departureAirportData; {@code null} clears the
     *                             code too.
     */
    public void setDepartureAirportData(final AirportData departureAirportData) {
        this.departureAirportData = departureAirportData;
        setDepartureAirport(
                Optional.ofNullable(departureAirportData)
                        .map(AirportData::getAirportCode)
                        .orElse(null)
        );
    }

    // -------------------------------------------------------------------------- arrivalAirportData

    /**
     * Returns the airport of arrival, over the {@value MappedRoute#COLUMN_NAME_ARRIVAL_AIRPORT}
     * column; the column's writable mapping.
     *
     * @return the arrivalAirportData.
     */
    public AirportData getArrivalAirportData() {
        return arrivalAirportData;
    }

    /**
     * Replaces the arrivalAirportData with the specified value, and the inherited, read-only
     * attribute over the {@value MappedRoute#COLUMN_NAME_ARRIVAL_AIRPORT} column with its airport
     * code, which is what this association writes.
     *
     * @param arrivalAirportData new value for the arrivalAirportData; {@code null} clears the code
     *                           too.
     */
    public void setArrivalAirportData(final AirportData arrivalAirportData) {
        this.arrivalAirportData = arrivalAirportData;
        setArrivalAirport(
                Optional.ofNullable(arrivalAirportData)
                        .map(AirportData::getAirportCode)
                        .orElse(null)
        );
    }

    /**
     * Returns the airplane which flies the route, over the
     * {@value MappedRoute#COLUMN_NAME_AIRPLANE_CODE} column; the column's writable mapping.
     *
     * @return the airplaneData.
     */
    public AirplaneData getAirplaneData() {
        return airplaneData;
    }

    /**
     * Replaces the airplaneData with the specified value, and the inherited, read-only attribute
     * over the {@value MappedRoute#COLUMN_NAME_AIRPLANE_CODE} column with its airplane code, which
     * is what this association writes.
     *
     * @param airplaneData new value for the airplaneData; {@code null} clears the code too.
     */
    public void setAirplaneData(final AirplaneData airplaneData) {
        this.airplaneData = airplaneData;
        setAirplaneCode(
                Optional.ofNullable(airplaneData)
                        .map(AirplaneData::getAirplaneCode)
                        .orElse(null)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_DEPARTURE_AIRPORT, nullable = false, insertable = true,
                updatable = true)
    private AirportData departureAirportData;

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_ARRIVAL_AIRPORT, nullable = false, insertable = true,
                updatable = true)
    private AirportData arrivalAirportData;

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_AIRPLANE_CODE, nullable = false, insertable = true,
                updatable = true)
    private AirplaneData airplaneData;
}
