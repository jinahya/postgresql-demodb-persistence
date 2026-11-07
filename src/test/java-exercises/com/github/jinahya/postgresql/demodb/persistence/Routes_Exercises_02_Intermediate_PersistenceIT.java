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
 * Answers the exercises of {@code src/test/sql-exercises/routes/02_intermediate.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class Routes_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("DEMODB_ROUTES_I_01: the periods of the route PG0001")
    @Nested
    class DEMODB_ROUTES_I_01_Test {
    }

    @DisplayName("DEMODB_ROUTES_I_02: the five route numbers with the most periods")
    @Nested
    class DEMODB_ROUTES_I_02_Test {
    }

    @DisplayName("DEMODB_ROUTES_I_03: how many routes each airplane flies")
    @Nested
    class DEMODB_ROUTES_I_03_Test {
    }

    @DisplayName("DEMODB_ROUTES_I_04: how many routes are valid today")
    @Nested
    class DEMODB_ROUTES_I_04_Test {
    }

    @DisplayName("DEMODB_ROUTES_I_05: the routes from Sheremetyevo valid today and flown on Mondays")
    @Nested
    class DEMODB_ROUTES_I_05_Test {
    }

    @DisplayName("DEMODB_ROUTES_I_06: how many routes fly on each day of the week, now")
    @Nested
    class DEMODB_ROUTES_I_06_Test {
    }

    @DisplayName("DEMODB_ROUTES_I_07: the rows whose days of the week are not a set of days")
    @Nested
    class DEMODB_ROUTES_I_07_Test {
    }
}
