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
 * A persister which persists {@link Seat} instances.
 * <p>
 * A newly persisted {@link AirplaneData} is flushed first, and what is persisted is
 * {@link Seat#of(AirplaneData, String) Seat.of} it and the instance's seat number, with the
 * instance's fare conditions; {@code of} is the only way to set the association, and the identifier
 * with it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Seat_Persister
        extends AbstractEntityPersister<Seat> {

    Seat_Persister() {
        super(Seat.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Seat apply(final EntityManager entityManager, final Seat entityInstance) {
        final var airplane =
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, AirplaneData.class);
        entityManager.flush();
        final var instance = Seat.of(airplane, entityInstance.getSeatNo());
        instance.setFareConditions(entityInstance.getFareConditions());
        return super.apply(entityManager, instance);
    }
}
