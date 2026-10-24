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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute;
import lombok.extern.slf4j.Slf4j;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

/**
 * A class for testing the {@link Route} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Route_Test
        extends _Entity_Test<Route> {

    Route_Test() {
        super(Route.class);
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Route#equals(Object) equals} compares the
     *         {@value MappedRoute#COLUMN_NAME_ROUTE_NO} and
     *         {@value MappedRoute#COLUMN_NAME_VALIDITY} attributes, the pair the table's exclusion
     *         constraint keeps apart, not the {@code
     * @EmbeddedId} which reads the same two columns; every other attribute is mutable
     *         state.
     */
    @Override
    public SingleTypeEqualsVerifierApi<Route> equalsVerifier() {
        return super.equalsVerifier()
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
