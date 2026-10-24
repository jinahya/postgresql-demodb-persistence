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
 * A persister which persists {@link BoardingPass} instances.
 * <p>
 * A newly persisted {@link Segment} is flushed first, and what is persisted is
 * {@link BoardingPass#of(Segment) BoardingPass.of} it, with the instance's seat, number and time;
 * {@code of} is the only way to set the association, and the identifier with it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BoardingPass_Persister
        extends AbstractEntityPersister<BoardingPass> {

    BoardingPass_Persister() {
        super(BoardingPass.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public BoardingPass apply(final EntityManager entityManager,
                              final BoardingPass entityInstance) {
        final var segment =
                EntityPersisterUtils.newPersistedInstanceOf(entityManager, Segment.class);
        entityManager.flush();
        final var instance = BoardingPass.of(segment);
        instance.setSeatNo(entityInstance.getSeatNo());
        instance.setBoardingNo(entityInstance.getBoardingNo());
        instance.setBoardingTime(entityInstance.getBoardingTime());
        return super.apply(entityManager, instance);
    }
}
