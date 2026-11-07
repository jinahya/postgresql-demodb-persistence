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
 * Answers the exercises of {@code src/test/sql-exercises/__/03_advanced.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class ___Exercises_03_Advanced_PersistenceIT {

    @DisplayName("DEMODB_SCHEMA_A_01: the five tickets with the most segments, and their itineraries")
    @Nested
    class DEMODB_SCHEMA_A_01_Test {
    }

    @DisplayName("DEMODB_SCHEMA_A_02: the busiest airport of each country")
    @Nested
    class DEMODB_SCHEMA_A_02_Test {
    }

    @DisplayName("DEMODB_SCHEMA_A_03: how many airports are one and two routes away from Sheremetyevo")
    @Nested
    class DEMODB_SCHEMA_A_03_Test {
    }

    @DisplayName("DEMODB_SCHEMA_A_04: the five airports where the most passengers change flights")
    @Nested
    class DEMODB_SCHEMA_A_04_Test {
    }

    @DisplayName("DEMODB_SCHEMA_A_05: the segments which do not connect to the previous one of their ticket")
    @Nested
    class DEMODB_SCHEMA_A_05_Test {
    }

    @DisplayName("DEMODB_SCHEMA_A_06: the passengers still to board the flights now boarding")
    @Nested
    class DEMODB_SCHEMA_A_06_Test {
    }
}
