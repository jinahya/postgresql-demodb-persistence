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

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplaneData;

import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.Locale;

/**
 * A randomizer which produces randomized {@link AirplaneData} instances.
 * <p>
 * The code, which the database does not generate, and the JSON model are assigned after the fact.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class AirplaneData_Randomizer
        extends PodamObjectRandomizer<AirplaneData> {

    AirplaneData_Randomizer() {
        super(AirplaneData.class, List.of(
                MappedAirplaneData.ATTRIBUTE_NAME_AIRPLANE_CODE,
                MappedAirplaneData.ATTRIBUTE_NAME_MODEL
        ));
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Override
    protected ClassInfoStrategy getClassInfoStrategy() {
        return super.getClassInfoStrategy();
    }

    @Override
    protected PodamFactory getPodamFactory() {
        return super.getPodamFactory();
    }

    @Override
    public AirplaneData get() {
        final var instance = super.get();
        instance.setModelName(Locale.ENGLISH, __Random_TestUtils.code(8));
        instance.setModelName(Locale.of("ru"), __Random_TestUtils.code(8));
        return instance;
    }
}
