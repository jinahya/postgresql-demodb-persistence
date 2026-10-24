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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplaneData;

import jakarta.persistence.EntityManager;

import java.util.Locale;

/**
 * A persister which persists {@link AirplaneData} instances.
 * <p>
 * An airplane references nothing, so an instance is persisted as it arrives.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class AirplaneData_Persister
        extends AbstractEntityPersister<AirplaneData> {

    AirplaneData_Persister() {
        super(AirplaneData.class);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public AirplaneData apply(final EntityManager entityManager,
                              final AirplaneData entityInstance) {
        final var instance = AirplaneData.of(
                __Random_TestUtils.code(MappedAirplaneData.COLUMN_LENGTH_AIRPLANE_CODE));
        for (final var locale : new Locale[] {Locale.ENGLISH, Locale.of("ru")}) {
            instance.setModelName(locale, entityInstance.getModelName(locale));
        }
        instance.setRange(entityInstance.getRange());
        instance.setSpeed(entityInstance.getSpeed());
        return super.apply(entityManager, instance);
    }
}
