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
 * Answers the exercises of {@code src/test/sql-exercises/routes/01_basic.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class Routes_Exercises_01_Basic_PersistenceIT {

    @DisplayName("DEMODB_ROUTES_B_01: the routes from Sheremetyevo")
    @Nested
    class DEMODB_ROUTES_B_01_Test {
    }

    @DisplayName("DEMODB_ROUTES_B_02: how many rows, and how many route numbers")
    @Nested
    class DEMODB_ROUTES_B_02_Test {
    }

    @DisplayName("DEMODB_ROUTES_B_03: the routes flown every day of the week")
    @Nested
    class DEMODB_ROUTES_B_03_Test {
    }

    @DisplayName("DEMODB_ROUTES_B_04: the routes longer than ten hours")
    @Nested
    class DEMODB_ROUTES_B_04_Test {
    }
}
