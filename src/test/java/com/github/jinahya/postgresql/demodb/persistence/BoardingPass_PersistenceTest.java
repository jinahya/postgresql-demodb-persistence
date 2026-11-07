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

import com.github.jinahya.postgresql.demodb.persistence.mapped.SegmentId;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Verifies the mappings of {@link BoardingPass} against the schema generated into the in-memory
 * database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class BoardingPass_PersistenceTest
        extends _Entity_PersistenceTest<BoardingPass, SegmentId> {

    BoardingPass_PersistenceTest() {
        super(BoardingPass.class, SegmentId.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; persisting a boarding pass loads its {@link Flight}, whose route is joined by a
     * formula with PostgreSQL's range operator, {@code @>}, which H2 does not parse.
     */
    @Disabled("Flight's route formula uses @>, which H2 does not parse")
    @Override
    @Test
    protected void persistRandomizedEntityInstance__() {
        // empty
    }

    /**
     * Does nothing; reading the flight back loads its route, which is joined by a formula
     * with PostgreSQL's range operator, {@code @>}, which H2 does not parse.
     */
    @Disabled("Flight's route formula uses @>, which H2 does not parse")
    @Override
    @Test
    void _persist_EqualsReference() {
        // empty
    }

    /**
     * Does nothing; reading the flight back loads its route, which is joined by a formula
     * with PostgreSQL's range operator, {@code @>}, which H2 does not parse.
     */
    @Disabled("Flight's route formula uses @>, which H2 does not parse")
    @Override
    @Test
    void _persist_ReferenceEquals() {
        // empty
    }

    /**
     * Does nothing; reading the flight back loads its route, which is joined by a formula
     * with PostgreSQL's range operator, {@code @>}, which H2 does not parse.
     */
    @Disabled("Flight's route formula uses @>, which H2 does not parse")
    @Override
    @Test
    void _persist_HashSetMembership() {
        // empty
    }
}
