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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * Answers the exercises of {@code src/test/sql-exercises/boarding_passes/02_intermediate.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class BoardingPasses_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("DEMODB_BOARDING_PASSES_I_01: the five seats boarded the most")
    @Nested
    class DEMODB_BOARDING_PASSES_I_01_Test {
    }

    @DisplayName("DEMODB_BOARDING_PASSES_I_02: how many segments have no boarding pass")
    @Nested
    class DEMODB_BOARDING_PASSES_I_02_Test {
    }

    @DisplayName("DEMODB_BOARDING_PASSES_I_03: how long before departure the passengers of the first day boarded")
    @Nested
    class DEMODB_BOARDING_PASSES_I_03_Test {
    }

    @DisplayName("DEMODB_BOARDING_PASSES_I_04: the five arrived flights with the most passengers checked in but not boarded")
    @Nested
    class DEMODB_BOARDING_PASSES_I_04_Test {
    }
}
