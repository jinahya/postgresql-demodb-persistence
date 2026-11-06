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
 * Answers the exercises of {@code src/test/sql-exercises/airports/02_intermediate.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Airports_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("DEMODB_AIRPORTS_I_01: the ten countries with the most airports")
    @Nested
    class DEMODB_AIRPORTS_I_01_Test {
    }

    @DisplayName("DEMODB_AIRPORTS_I_02: how many countries have exactly one airport")
    @Nested
    class DEMODB_AIRPORTS_I_02_Test {
    }

    @DisplayName("DEMODB_AIRPORTS_I_03: the westernmost and the easternmost airport")
    @Nested
    class DEMODB_AIRPORTS_I_03_Test {
    }
}
