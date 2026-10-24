package com.github.jinahya.postgresql.demodb.persistence.converter;

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

import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import com.github.jinahya.postgresql.demodb.persistence.Route;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.persistence.Persistence;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that a persistence provider handed a {@link BeanManager}, through
 * {@code jakarta.persistence.bean.manager}, takes its {@link IntegerListArrayConverter} from CDI: a {@link Route}
 * persisted and read back through the in-memory unit has its {@code daysOfWeek} converted by the element converter
 * the container resolved, an alternative which counts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EnableWeld
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class IntegerListArrayConverter_Provider_Weld_Test {

    /**
     * The name of the persistence unit the test bootstraps. The value is {@value}.
     */
    static final String PERSISTENCE_UNIT_NAME = "demonstration-test";

    /**
     * An {@link IntegerConverter} which counts what it converts; the alternative the container resolves.
     */
    @Alternative
    @Dependent
    public static class CountingIntegerConverter extends IntegerConverter {

        static final AtomicInteger CONVERTED = new AtomicInteger();

        @Override
        public Integer convertToEntityAttribute(final String dbData) {
            CONVERTED.incrementAndGet();
            return super.convertToEntityAttribute(dbData);
        }
    }

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(
            WeldInitiator.createWeld()
                    .addBeanClasses(IntegerListArrayConverter.class, IntegerConverter.class,
                                    CountingIntegerConverter.class)
                    .alternatives(CountingIntegerConverter.class)
    ).build();

    @DisplayName("the provider takes the converter from the container it was handed")
    @Test
    void provider_ConverterFromContainer_BeanManagerHanded() {
        final var beanManager = weld.select(BeanManager.class).get();
        final var before = CountingIntegerConverter.CONVERTED.get();
        try (var emf = Persistence.createEntityManagerFactory(
                PERSISTENCE_UNIT_NAME, Map.of("jakarta.persistence.bean.manager", beanManager));
             var em = emf.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            try {
                final var persisted = EntityPersisterUtils.newPersistedInstanceOf(em, Route.class);
                em.flush();
                em.clear();
                final var loaded = em.find(Route.class, persisted.getId());
                assertThat(loaded.getDaysOfWeek())
                        .as("days of week of %s, read back", loaded)
                        .isEqualTo(persisted.getDaysOfWeek());
            } finally {
                tx.rollback();
            }
        }
        assertThat(CountingIntegerConverter.CONVERTED.get() - before)
                .as("elements the alternative converted while the provider read the row back")
                .isPositive();
    }
}
