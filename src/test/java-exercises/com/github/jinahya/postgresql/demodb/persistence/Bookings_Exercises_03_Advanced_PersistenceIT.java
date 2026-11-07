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
 * Answers the exercises of {@code src/test/sql-exercises/bookings/03_advanced.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class Bookings_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("DEMODB_BOOKINGS_A_01: how many bookings do not add up")
    @Nested
    class DEMODB_BOOKINGS_A_01_Test {
    }

    @DisplayName("DEMODB_BOOKINGS_A_02: the five bookings with the most tickets")
    @Nested
    class DEMODB_BOOKINGS_A_02_Test {
    }

    @DisplayName("DEMODB_BOOKINGS_A_03: the share of the money each tenth of the bookings brings")
    @Nested
    class DEMODB_BOOKINGS_A_03_Test {
    }
}
