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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight.FlightStatus;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedFlight;

import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A randomizer which produces randomized {@link Flight} instances.
 * <p>
 * The {@code flightId}, which the database generates, is excluded from randomization. The route
 * number, the status and the four times are assigned after the fact, within what their columns and
 * the table's check constraints hold.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Flight_Randomizer
        extends PodamObjectRandomizer<Flight> {

    Flight_Randomizer() {
        super(Flight.class, List.of(
                MappedFlight.ATTRIBUTE_NAME_FLIGHT_ID
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
    public Flight get() {
        final var instance = super.get();
        instance.setRouteNo(__Random_TestUtils.routeNo());
        instance.setStatus(__Random_TestUtils.pick(FlightStatus.values()));
        final var departure = __Random_TestUtils.instant();
        final var arrival = departure.plus(ThreadLocalRandom.current().nextInt(30, 900),
                                           ChronoUnit.MINUTES);
        instance.setScheduledDeparture(departure);
        instance.setScheduledArrival(arrival);
        if (ThreadLocalRandom.current().nextBoolean()) {
            instance.setActualDeparture(
                    departure.plus(ThreadLocalRandom.current().nextInt(0, 60), ChronoUnit.MINUTES));
            instance.setActualArrival(
                    arrival.plus(ThreadLocalRandom.current().nextInt(0, 60), ChronoUnit.MINUTES));
        } else {
            instance.setActualDeparture(null);
            instance.setActualArrival(null);
        }
        return instance;
    }
}
