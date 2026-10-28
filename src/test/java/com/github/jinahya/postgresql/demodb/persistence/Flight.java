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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

/**
 * An entity which maps the {@value MappedFlight#TABLE_NAME} table, extending {@link MappedFlight}
 * with what is an application's to decide: its association to the route it flies.
 * <p>
 * No foreign key declares that association: a flight's route is the
 * {@value MappedRoute#TABLE_NAME} row of its {@value MappedFlight#COLUMN_NAME_ROUTE_NO} whose
 * {@value MappedRoute#COLUMN_NAME_VALIDITY} contains its
 * {@value MappedFlight#COLUMN_NAME_SCHEDULED_DEPARTURE}, and the table has no
 * {@value MappedRoute#COLUMN_NAME_VALIDITY} to join on. So the association joins on the route
 * number and on a Hibernate ORM join formula, a subquery which finds the validity; the table's
 * exclusion constraint keeps it to one row at most.
 * <p>
 * The table's unique constraint, one flight of a route per scheduled departure, is declared with
 * {@link UniqueConstraint @UniqueConstraint}, under the name the database gives it, so that a
 * schema generated from the mapping, the test unit's, has it too.
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

    /**
     * Returns the route this flight flies, the one valid at its scheduled departure; read-only.
     *
     * @return the route.
     */
    public Route getRoute() {
        return route;
    }

    // ---------------------------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(
                    name = MappedFlight.COLUMN_NAME_ROUTE_NO,
                    referencedColumnName = MappedRoute.COLUMN_NAME_ROUTE_NO,
                    insertable = false,
                    updatable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(
                    value = "(SELECT r.validity FROM routes r"
                            + " WHERE r.route_no = route_no"
                            + " AND r.validity @> scheduled_departure)",
                    referencedColumnName = MappedRoute.COLUMN_NAME_VALIDITY))
    })
    private Route route;
}
