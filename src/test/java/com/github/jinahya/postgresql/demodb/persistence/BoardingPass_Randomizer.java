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

import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A randomizer which produces randomized {@link BoardingPass} instances.
 * <p>
 * The {@code segment} is excluded from randomization; {@link BoardingPass_Persister} supplies it,
 * and the identifier with it. The seat, the number and the time are assigned after the fact.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class BoardingPass_Randomizer
        extends PodamObjectRandomizer<BoardingPass> {

    BoardingPass_Randomizer() {
        super(BoardingPass.class, List.of(
                "segment"
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
    public BoardingPass get() {
        final var instance = super.get();
        instance.setSeatNo(__Random_TestUtils.seatNo());
        instance.setBoardingNo(ThreadLocalRandom.current()
                                       .nextBoolean() ? __Random_TestUtils.positiveInt() : null);
        instance.setBoardingTime(
                ThreadLocalRandom.current().nextBoolean() ? __Random_TestUtils.instant() : null);
        return instance;
    }
}
