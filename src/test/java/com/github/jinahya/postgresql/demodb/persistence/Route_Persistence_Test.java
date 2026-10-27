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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Verifies the mappings of {@link Route} against the schema generated into the in-memory database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Route_Persistence_Test
        extends _Entity_Persistence_Test<Route> {

    Route_Persistence_Test() {
        super(Route.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @implNote Skipped when woven for EclipseLink; see {@link #assumeNotWoven()}.
     */
    @DisplayName("persisted.equals(getReference(persisted))")
    @Override
    @Test
    void _persist_EqualsReference() {
        assumeNotWoven();
        super._persist_EqualsReference();
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Skipped when woven for EclipseLink; see {@link #assumeNotWoven()}.
     */
    @DisplayName("getReference(persisted).equals(persisted)")
    @Override
    @Test
    void _persist_ReferenceEquals() {
        assumeNotWoven();
        super._persist_ReferenceEquals();
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Skipped when woven for EclipseLink; see {@link #assumeNotWoven()}.
     */
    @DisplayName(
            "a HashSet still contains the persisted instance across flush, merge, and getReference")
    @Override
    @Test
    void _persist_HashSetMembership() {
        assumeNotWoven();
        super._persist_HashSetMembership();
    }

    /**
     * Skips the calling test when {@link Route} is woven by EclipseLink (the
     * {@code jakarta-ee-11-eclipselink-weaving} profile).
     *
     * @implNote Woven, EclipseLink 5.0.2 fails inside
     *         {@link jakarta.persistence.EntityManager#getReference(Object) getReference} for an
     *         entity whose identifier is an {@link jakarta.persistence.EmbeddedId EmbeddedId},
     *         {@link RouteId}: building the identifier's aggregate throws a
     *         {@link NullPointerException} on a query with no session, at
     *         {@code ObjectBuilder.buildAttributesIntoShallowObject}. Nothing of this entity is on
     *         that path; the unwoven runs, and both providers, pass the same tests.
     */
    private static void assumeNotWoven() {
        assumeFalse(__Object_TestUtils.isWovenByEclipseLink(Route.class),
                    "woven by EclipseLink; getReference fails with an EmbeddedId");
    }
}
