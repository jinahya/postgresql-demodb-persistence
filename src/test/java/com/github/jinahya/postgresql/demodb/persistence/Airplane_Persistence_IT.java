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

import com.github.jinahya.persistence.util.JinahyaEntityManagerUtils;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplane;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.persistence.util.JinahyaEntityManagerUtils.applyInTransaction;
import static com.github.jinahya.persistence.util.JinahyaPersistenceUtils.applyEntityManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Verifies the mappings of {@link Airplane} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Airplane_Persistence_IT
        extends _Entity_Persistence_IT<Airplane> {

    Airplane_Persistence_IT() {
        super(Airplane.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Does nothing; {@value MappedAirplane#TABLE_NAME} is a view, which PostgreSQL does not let a
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
     * @implNote The model is the name in the session's language, no longer JSON, and the
     *         association reaches the airplane data of the code.
     */
    @Override
    void randomTargetInstanceSelected(final Airplane instance) {
        assertThat(instance.getModel())
                .as("model of %s", instance).isNotBlank()
                .doesNotContain("\"en\"");
        assertThat(instance.getAirplaneData()).as("airplane data of %s", instance).isNotNull()
                .extracting(AirplaneData::getAirplaneCode).isEqualTo(instance.getAirplaneCode());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Selects every instance with its {@link Airplane#getAirplaneData() airplane data} fetched by
     * the same query, and checks that the fetch join, an inner join, loses no row of the view.
     *
     * @implNote The number of instances is counted with
     *         {@link JinahyaEntityManagerUtils#selectCount(jakarta.persistence.EntityManager,
     *         Class)}, of the view and of the table alike; the view reads every row of the table,
     *         so the three numbers are the same.
     */
    @DisplayName("a fetch join loads the airplane data with every airplane")
    @Test
    void selectTargetInstances_FetchAirplaneData() {
        applyEntityManager(PERSISTENCE_UNIT_NAME, m -> applyInTransaction(m, em -> {
            final var count = JinahyaEntityManagerUtils.selectEntityCount(em, Airplane.class);
            assumeTrue(count > 0L, () -> "no row of " + Airplane.class + " is installed");
            assertThat(JinahyaEntityManagerUtils.selectEntityCount(em, AirplaneData.class))
                    .as("number of airplane data")
                    .isEqualTo(count);
            final var builder = em.getCriteriaBuilder();
            final var query = builder.createQuery(Airplane.class);
            final var root = query.from(Airplane.class);
            root.fetch(Airplane_.airplaneData);
            query.select(root);
            final var airplanes = em.createQuery(query).getResultList();
            assertThat(airplanes).as("airplanes, with their airplane data fetched")
                    .hasSize(Math.toIntExact(count));
            final var util = em.getEntityManagerFactory().getPersistenceUnitUtil();
            airplanes.forEach(airplane -> {
                assertThat(util.isLoaded(airplane, Airplane_.AIRPLANE_DATA))
                        .as("airplane data of %s, loaded", airplane)
                        .isTrue();
                assertThat(airplane.getAirplaneData()).as("airplane data of %s", airplane)
                        .isNotNull()
                        .extracting(AirplaneData::getAirplaneCode)
                        .isEqualTo(airplane.getAirplaneCode());
            });
            return null;
        }, true));
    }
}
