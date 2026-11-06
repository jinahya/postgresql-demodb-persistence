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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

/**
 * Answers the exercises of {@code src/test/sql-exercises/flights/03_advanced.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Flights_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("DEMODB_FLIGHTS_A_01: the five longest flights flown")
    @Nested
    class DEMODB_FLIGHTS_A_01_Test {
    }

    @DisplayName("DEMODB_FLIGHTS_A_02: the flights of each day of the first week, and their running total")
    @Nested
    class DEMODB_FLIGHTS_A_02_Test {
    }

    @DisplayName("DEMODB_FLIGHTS_A_03: the five fullest flights of the first day")
    @Nested
    class DEMODB_FLIGHTS_A_03_Test {
    }

    @DisplayName("DEMODB_FLIGHTS_A_04: the time since the previous flight of the route PG0001, in October 2025")
    @Nested
    class DEMODB_FLIGHTS_A_04_Test {
    }
}
