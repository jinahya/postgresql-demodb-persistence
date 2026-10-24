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
 * A persister which persists {@link Segment} instances.
 * <p>
 * A newly persisted {@link Ticket} and a newly persisted {@link Flight} are flushed first, and what
 * is persisted is {@link Segment#of(Ticket, Flight) Segment.of} the two, with the instance's fare
 * conditions and price; {@code of} is the only way to set either association, and the identifier
 * with them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Segment_Persister
        extends AbstractEntityPersister<Segment> {

    Segment_Persister() {
        super(Segment.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Segment apply(final EntityManager entityManager, final Segment entityInstance) {
        final var ticket = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Ticket.class);
        final var flight = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Flight.class);
        entityManager.flush();
        final var instance = Segment.of(ticket, flight);
        instance.setFareConditions(entityInstance.getFareConditions());
        instance.setPrice(entityInstance.getPrice());
        return super.apply(entityManager, instance);
    }
}
