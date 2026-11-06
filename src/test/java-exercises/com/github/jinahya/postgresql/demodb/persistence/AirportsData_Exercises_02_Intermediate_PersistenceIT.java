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
 * Answers the exercises of {@code src/test/sql-exercises/airports_data/02_intermediate.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class AirportsData_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("DEMODB_AIRPORTS_DATA_I_01: the airports of Finland, in English")
    @Nested
    class DEMODB_AIRPORTS_DATA_I_01_Test {
    }

    @DisplayName("DEMODB_AIRPORTS_DATA_I_02: the ten time zones with the most airports")
    @Nested
    class DEMODB_AIRPORTS_DATA_I_02_Test {
    }

    @DisplayName("DEMODB_AIRPORTS_DATA_I_03: the five countries with the most airports south of the equator")
    @Nested
    class DEMODB_AIRPORTS_DATA_I_03_Test {
    }

    @DisplayName("DEMODB_AIRPORTS_DATA_I_04: the airports missing an English name, city or country")
    @Nested
    class DEMODB_AIRPORTS_DATA_I_04_Test {
    }
}
