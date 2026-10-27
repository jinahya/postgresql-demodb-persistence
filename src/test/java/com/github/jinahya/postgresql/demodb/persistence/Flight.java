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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * An entity which maps the {@value MappedFlight#TABLE_NAME} table, extending {@link MappedFlight}
 * with what is an application's to decide; it adds nothing, there being no foreign key to map an
 * association over.
 * <p>
 * The table's unique constraint, one flight of a route per scheduled departure, is declared with
 * {@link UniqueConstraint @UniqueConstraint}, under the name the database gives it, so that a
 * schema generated from the mapping, the in-memory one, has it too.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = MappedFlight.TABLE_NAME,
       uniqueConstraints = {
               @UniqueConstraint(name = "flights_route_no_scheduled_departure_key",
                                 columnNames = {MappedFlight.COLUMN_NAME_ROUTE_NO,
                                                MappedFlight.COLUMN_NAME_SCHEDULED_DEPARTURE})
       }
)
public class Flight
        extends MappedFlight {

    /**
     * Creates a new instance.
     */
    protected Flight() {
        super();
    }

    // ---------------------------------------------------------------------------------------------
}
