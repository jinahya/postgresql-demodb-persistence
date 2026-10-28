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

import com.github.jinahya.persistence.test.util.AbstractEntityPersister;
import com.github.jinahya.persistence.test.util.EntityPersisterUtils;

import jakarta.persistence.EntityManager;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A persister which persists {@link Flight} instances.
 * <p>
 * A flight's {@code route_no} is no foreign key, but every installed flight flies a route: one of
 * its route number whose validity holds the scheduled departure, which departs at the route's
 * scheduled time, local to its departure airport, on one of the route's days of the week, and
 * arrives the route's duration later. So a newly persisted {@link Route} is flushed first, and the
 * instance is given a departure of it; an actual departure, if the instance has one, is up to an
 * hour late, and the actual arrival the route's duration after it, and up to half an hour more.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Flight_Persister
        extends AbstractEntityPersister<Flight> {

    Flight_Persister() {
        super(Flight.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Flight apply(final EntityManager entityManager, final Flight entityInstance) {
        final var route = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Route.class);
        entityManager.flush();
        final var random = ThreadLocalRandom.current();
        final var zone = route.getDepartureAirportData().getTimezone();
        final var departures = route.getValidityRange(
                (lower, upper) -> lower.atZone(zone).toLocalDate()
                        .datesUntil(upper.atZone(zone).toLocalDate().plusDays(1L))
                        .filter(d -> route.getDaysOfWeekSet().contains(d.getDayOfWeek()))
                        .map(d -> ZonedDateTime.of(d, route.getScheduledTime(), zone).toInstant())
                        .filter(i -> !i.isBefore(lower) && i.isBefore(upper))
                        .toList());
        final var departure = departures.get(random.nextInt(departures.size()));
        entityInstance.setRouteNo(route.getRouteNo());
        entityInstance.setScheduledDeparture(departure);
        entityInstance.setScheduledArrival(departure.plus(route.getDuration()));
        if (entityInstance.getActualDeparture() != null) {
            final var actualDeparture = departure.plus(random.nextInt(0, 60), ChronoUnit.MINUTES);
            entityInstance.setActualDeparture(actualDeparture);
            entityInstance.setActualArrival(
                    actualDeparture.plus(route.getDuration())
                            .plus(random.nextInt(0, 30), ChronoUnit.MINUTES));
        }
        return super.apply(entityManager, entityInstance);
    }
}
