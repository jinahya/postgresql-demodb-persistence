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
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat;
import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedSeat.FareConditions;

import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

/**
 * A randomizer which produces randomized {@link Seat} instances.
 * <p>
 * The {@code airplaneData} is excluded from randomization; {@link Seat_Persister} supplies it, and
 * the identifier with it. The fare conditions are assigned after the fact.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class Seat_Randomizer
        extends PodamObjectRandomizer<Seat> {

    Seat_Randomizer() {
        super(Seat.class, List.of(
                MappedSeat.ATTRIBUTE_NAME_ID,
                "airplaneData"
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
    public Seat get() {
        final var instance = super.get();
        instance.setId(SeatId.of(__Random_TestUtils.code(3), __Random_TestUtils.seatNo()));
        instance.setFareConditions(__Random_TestUtils.pick(FareConditions.values()));
        return instance;
    }
}
