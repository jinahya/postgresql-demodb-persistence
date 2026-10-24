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
 * A persister which persists {@link Ticket} instances.
 * <p>
 * A newly persisted {@link Booking} is flushed first, and set as the instance's booking; the
 * read-only {@code bookRef} follows from it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Ticket_Persister
        extends AbstractEntityPersister<Ticket> {

    Ticket_Persister() {
        super(Ticket.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Ticket apply(final EntityManager entityManager, final Ticket entityInstance) {
        final var booking =
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, Booking.class);
        entityManager.flush();
        entityInstance.setBooking(booking);
        return super.apply(entityManager, entityInstance);
    }
}
