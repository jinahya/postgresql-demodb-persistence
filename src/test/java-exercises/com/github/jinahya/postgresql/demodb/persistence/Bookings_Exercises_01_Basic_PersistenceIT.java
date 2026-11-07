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
 * Answers the exercises of {@code src/test/sql-exercises/bookings/01_basic.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class Bookings_Exercises_01_Basic_PersistenceIT {

    @DisplayName("DEMODB_BOOKINGS_B_01: the bookings of the first minute")
    @Nested
    class DEMODB_BOOKINGS_B_01_Test {
    }

    @DisplayName("DEMODB_BOOKINGS_B_02: how many bookings, and how much in all")
    @Nested
    class DEMODB_BOOKINGS_B_02_Test {
    }

    @DisplayName("DEMODB_BOOKINGS_B_03: the bookings of 700000 or more")
    @Nested
    class DEMODB_BOOKINGS_B_03_Test {
    }

    @DisplayName("DEMODB_BOOKINGS_B_04: the second page of the bookings of 2025-10-01")
    @Nested
    class DEMODB_BOOKINGS_B_04_Test {
    }
}
