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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirport;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link Airport} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Airport_Persistence_IT
        extends _Entity_Persistence_IT<Airport> {

    Airport_Persistence_IT() {
        super(Airport.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; {@value MappedAirport#TABLE_NAME} is a view, which PostgreSQL does not let a
     * row be inserted into.
     */
    @Disabled("a view: nothing is inserted into it")
    @Override
    @Test
    void persistTargetInstance() {
        // empty
    }

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The name is the English name, no longer JSON.
     */
    @Override
    void randomTargetInstanceSelected(final Airport instance) {
        assertThat(instance.getAirportName()).as("name of %s", instance).isNotBlank()
                .doesNotContain("\"en\"");
    }
}
