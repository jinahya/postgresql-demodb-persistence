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

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the mappings of {@link AirplaneData} against the installed {@code demo} database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class AirplaneData_PersistenceIT
        extends _Entity_PersistenceIT<AirplaneData> {

    AirplaneData_PersistenceIT() {
        super(AirplaneData.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param instance {@inheritDoc}
     * @implNote The model is JSON with an English name, and the range and the speed are
     *         positive.
     */
    @Override
    void randomTargetInstanceSelected(final AirplaneData instance) {
        assertThat(instance.getModelName(Locale.ENGLISH)).as("model of %s", instance).isNotBlank();
        assertThat(instance.getModelName(Locale.of("ru"))).as("model of %s", instance).isNotBlank();
        assertThat(instance.getRange()).as("range of %s", instance).isPositive();
        assertThat(instance.getSpeed()).as("speed of %s", instance).isPositive();
    }
}
