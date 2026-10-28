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

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute;

import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

/**
 * A randomizer which produces randomized {@link Route} instances.
 * <p>
 * The identifier, the three read-only airport and airplane columns and the three associations are
 * excluded from randomization; {@link Route_Persister} supplies the associations. Every other
 * attribute is assigned after the fact, within what its column holds, and the identifier is derived
 * from two of them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Route_Randomizer
        extends PodamObjectRandomizer<Route> {

    Route_Randomizer() {
        super(Route.class, List.of(
                "id",
                MappedRoute.ATTRIBUTE_NAME_DEPARTURE_AIRPORT,
                MappedRoute.ATTRIBUTE_NAME_ARRIVAL_AIRPORT,
                MappedRoute.ATTRIBUTE_NAME_AIRPLANE_CODE,
                "departureAirportData",
                "arrivalAirportData",
                "airplaneData",
                MappedRoute.ATTRIBUTE_NAME_DURATION
        ));
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Override
    protected ClassInfoStrategy getClassInfoStrategy() {
        return super.getClassInfoStrategy();
    }

    @Override
    protected PodamFactory getPodamFactory() {
        return super.getPodamFactory();
    }

    @Override
    public Route get() {
        final var instance = super.get();
        instance.setRouteNo(__Random_TestUtils.routeNo());
        instance.setValidity(__Random_TestUtils.validity());
        instance.setDaysOfWeekSet(__Random_TestUtils.daysOfWeek());
        instance.setScheduledTime(__Random_TestUtils.time());
        instance.setDuration(__Random_TestUtils.duration());
        instance.setId(RouteId.of(instance.getRouteNo(), instance.getValidity()));
        return instance;
    }
}
