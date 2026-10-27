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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirport;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.Optional;
import java.util.TimeZone;

/**
 * An entity which maps the {@value MappedAirport#TABLE_NAME} view, extending {@link MappedAirport}
 * with what is an application's to decide: a read-only association to the {@link AirportData} row
 * the view reads, whose names are there in every language.
 * <p>
 * A view has no foreign key; the association rests on the view's definition, which reads every
 * row of {@code airports_data} by the same {@value MappedAirport#COLUMN_NAME_AIRPORT_CODE}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = MappedAirport.TABLE_NAME)
public class Airport
        extends MappedAirport {

    /**
     * Creates a new instance.
     */
    protected Airport() {
        super();
    }

    // ---------------------------------------------------------------------------------------------
    @Transient
    @Override
    public TimeZone getTimezoneAsTimeZone() {
        return Optional.ofNullable(getTimezone())
                .map(TimeZone::getTimeZone)
                .orElse(null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the airport data this view row reads, over the
     * {@value MappedAirport#COLUMN_NAME_AIRPORT_CODE} column, which is the identifier; the
     * association is read-only.
     *
     * @return the airportData.
     */
    public AirportData getAirportData() {
        return airportData;
    }

    // ---------------------------------------------------------------------------------------------
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_AIRPORT_CODE, nullable = false, insertable = false,
                updatable = false)
    private AirportData airportData;
}
