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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRouteId;

import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * The identifier {@link Route} gives the {@value MappedRoute#TABLE_NAME} table, which has none of
 * its own: {@link MappedRouteId} made concrete, {@link Serializable} and {@link Embeddable}, as an
 * {@link jakarta.persistence.EmbeddedId @EmbeddedId} has to be. This is the mapping which writes
 * the two columns, since a provider writes an identifier's columns whatever their
 * {@code insertable} says; {@link Route} overrides the inherited attributes over the same columns
 * to read-only.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class RouteId
        extends MappedRouteId
        implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ---------------------------------------------------------------------------------------------
    // STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of the specified values.
     *
     * @param routeNo  a value for the {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute.
     * @param validity a value for the {@value #ATTRIBUTE_NAME_VALIDITY} attribute.
     * @return a new instance.
     */
    public static RouteId of(final String routeNo, final Instant[] validity) {
        return of(RouteId::new, routeNo, validity);
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance. The constructor is {@code public}, as the specification requires of a
     * primary key class.
     */
    public RouteId() {
        super();
    }
}
