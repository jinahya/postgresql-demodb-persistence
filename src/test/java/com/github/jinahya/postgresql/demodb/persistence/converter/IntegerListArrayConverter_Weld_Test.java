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

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that CDI builds an {@link IntegerListArrayConverter} through its {@code @Inject}
 * constructor, with the element converter resolved as a bean.
 * <p>
 * The proof is an alternative: {@link CountingIntegerConverter}, enabled for the container, is what
 * CDI resolves for an {@code IntegerConverter}, so a converter CDI built holds one, and a converter
 * built with {@code new} holds a plain {@link IntegerConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@EnableWeld
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class IntegerListArrayConverter_Weld_Test {

    /**
     * An {@link IntegerConverter} which counts what it converts; the alternative the container
     * resolves.
     */
    @Alternative
    @Dependent
    public static class CountingIntegerConverter
            extends IntegerConverter {

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

    @DisplayName("the container injects the element converter it resolves")
    @Test
    void injected_ElementConverter_IsTheAlternative() {
        final var converter = weld.select(IntegerListArrayConverter.class).get();
        assertThat(converter.elementConverter())
                .as("element converter of %s", converter)
                .isInstanceOf(CountingIntegerConverter.class);
        final var before = CountingIntegerConverter.CONVERTED.get();
        assertThat(converter.convertToEntityAttribute("{1,2,3}")).isEqualTo(List.of(1, 2, 3));
        assertThat(CountingIntegerConverter.CONVERTED.get() - before)
                .as("elements the alternative converted")
                .isEqualTo(3);
    }

    @DisplayName("new, without the container, builds its own element converter")
    @Test
    void constructed_ElementConverter_IsAPlainOne() {
        final var converter = new IntegerListArrayConverter();
        assertThat(converter.elementConverter())
                .isExactlyInstanceOf(IntegerConverter.class);
    }
}
