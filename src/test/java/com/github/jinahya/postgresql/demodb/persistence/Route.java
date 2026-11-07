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

import com.github.jinahya.postgresql.demodb.persistence.mapped.RouteId;
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
 * The table has no primary key, so {@link MappedRoute} takes an identifier of its own:
 * {@link RouteId}, the {@value MappedRoute#COLUMN_NAME_ROUTE_NO} and
 * {@value MappedRoute#COLUMN_NAME_VALIDITY} columns.
 * <p>
 * A row is what was valid for its period: its airports and its airplane are set by
 * {@link #of(RouteId, AirportData, AirportData, AirplaneData)}, whose setters are package-private,
 * and written once, their join columns {@code updatable = false}. Another airplane is another row,
 * of another period.
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
        extends MappedRoute {

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance with the specified identifier, from the specified airport of departure
     * to the specified airport of arrival, flown by the specified airplane.
     * <p>
     * This is the only way to set the two airports and the airplane from outside the package. A
     * route number names the way between two airports, and neither changes; another way takes
     * another route. The airplane of a route number does change, from one period of validity to
     * the next, but as another row, of another period: a row is what was valid for its period, and
     * the flights of the period, and their seats, are the airplane's.
     *
     * @param id                   a value for the identifier.
     * @param departureAirportData a value for the departureAirportData.
     * @param arrivalAirportData   a value for the arrivalAirportData.
     * @param airplaneData         a value for the airplaneData.
     * @return a new instance of {@code id}, {@code departureAirportData},
     *         {@code arrivalAirportData} and {@code airplaneData}.
     */
    public static Route of(final RouteId id, final AirportData departureAirportData,
                           final AirportData arrivalAirportData, final AirplaneData airplaneData) {
        final var instance = new Route();
        instance.setId(id);
        instance.setDepartureAirportData(departureAirportData);
        instance.setArrivalAirportData(arrivalAirportData);
        instance.setAirplaneData(airplaneData);
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
     * column; the column's writable mapping, which inserts it and never updates it.
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
     * @apiNote This method is package-private;
     *         {@link #of(RouteId, AirportData, AirportData, AirplaneData)} sets the
     *         departureAirportData from outside the package.
     */
    void setDepartureAirportData(final AirportData departureAirportData) {
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
     * column; the column's writable mapping, which inserts it and never updates it.
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
     * @apiNote This method is package-private;
     *         {@link #of(RouteId, AirportData, AirportData, AirplaneData)} sets the
     *         arrivalAirportData from outside the package.
     */
    void setArrivalAirportData(final AirportData arrivalAirportData) {
        this.arrivalAirportData = arrivalAirportData;
        setArrivalAirport(
                Optional.ofNullable(arrivalAirportData)
                        .map(AirportData::getAirportCode)
                        .orElse(null)
        );
    }

    /**
     * Returns the airplane which flies the route, over the
     * {@value MappedRoute#COLUMN_NAME_AIRPLANE_CODE} column; the column's writable mapping, which
     * inserts it and never updates it.
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
     * @apiNote This method is package-private;
     *         {@link #of(RouteId, AirportData, AirportData, AirplaneData)} sets the airplaneData
     *         from outside the package.
     */
    void setAirplaneData(final AirplaneData airplaneData) {
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
                updatable = false)
    private AirportData departureAirportData;

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_ARRIVAL_AIRPORT, nullable = false, insertable = true,
                updatable = false)
    private AirportData arrivalAirportData;

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_AIRPLANE_CODE, nullable = false, insertable = true,
                updatable = false)
    private AirplaneData airplaneData;
}
