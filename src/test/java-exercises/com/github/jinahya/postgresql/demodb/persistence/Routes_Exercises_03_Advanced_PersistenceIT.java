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
 * Answers the exercises of {@code src/test/sql-exercises/routes/03_advanced.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class Routes_Exercises_03_Advanced_PersistenceIT {

    @DisplayName("DEMODB_ROUTES_A_01: the longest route from each airport")
    @Nested
    class DEMODB_ROUTES_A_01_Test {
    }

    @DisplayName("DEMODB_ROUTES_A_02: the routes with a return route in the same period")
    @Nested
    class DEMODB_ROUTES_A_02_Test {
    }

    @DisplayName("DEMODB_ROUTES_A_03: the airplane changes of each route from one period to the next")
    @Nested
    class DEMODB_ROUTES_A_03_Test {
    }

    @DisplayName("DEMODB_ROUTES_A_04: the ten longest routes valid now, by great-circle distance")
    @Nested
    class DEMODB_ROUTES_A_04_Test {
    }
}
