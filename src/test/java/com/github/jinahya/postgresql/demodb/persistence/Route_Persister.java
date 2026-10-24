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

/**
 * A persister which persists {@link Route} instances.
 * <p>
 * Two newly persisted {@link AirportData} and a newly persisted {@link AirplaneData} are flushed
 * first, and set as the instance's associations; the three read-only columns follow from them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Route_Persister
        extends AbstractEntityPersister<Route> {

    Route_Persister() {
        super(Route.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Route apply(final EntityManager entityManager, final Route entityInstance) {
        final var departure =
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, AirportData.class);
        final var arrival =
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, AirportData.class);
        final var airplane =
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, AirplaneData.class);
        entityManager.flush();
        entityInstance.setDepartureAirportData(departure);
        entityInstance.setArrivalAirportData(arrival);
        entityInstance.setAirplaneData(airplane);
        return super.apply(entityManager, entityInstance);
    }
}
