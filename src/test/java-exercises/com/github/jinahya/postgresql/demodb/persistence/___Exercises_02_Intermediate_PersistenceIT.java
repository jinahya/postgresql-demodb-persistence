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
 * Answers the exercises of {@code src/test/sql-exercises/__/02_intermediate.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@Slf4j
class ___Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("DEMODB_SCHEMA_I_01: the segments sold and the money taken, by month of flight")
    @Nested
    class DEMODB_SCHEMA_I_01_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_02: the ten airports with the most passengers boarded in the first week")
    @Nested
    class DEMODB_SCHEMA_I_02_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_03: the money each airplane model took")
    @Nested
    class DEMODB_SCHEMA_I_03_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_04: the ten countries flown to the most")
    @Nested
    class DEMODB_SCHEMA_I_04_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_05: the passengers and the seats of the flights from Sheremetyevo in the next 24 hours")
    @Nested
    class DEMODB_SCHEMA_I_05_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_06: the flights with no route valid at their departure")
    @Nested
    class DEMODB_SCHEMA_I_06_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_07: how far ahead the bookings were made")
    @Nested
    class DEMODB_SCHEMA_I_07_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_08: the seats of each airplane model, by fare conditions")
    @Nested
    class DEMODB_SCHEMA_I_08_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_09: today's departures, in each airport's own day")
    @Nested
    class DEMODB_SCHEMA_I_09_Test {
    }

    @DisplayName("DEMODB_SCHEMA_I_10: the outbound and return tickets of one passenger in one booking")
    @Nested
    class DEMODB_SCHEMA_I_10_Test {
    }
}
