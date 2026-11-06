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
 * Answers the exercises of {@code src/test/sql-exercises/tickets/02_intermediate.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class Tickets_Exercises_02_Intermediate_PersistenceIT {

    @DisplayName("DEMODB_TICKETS_I_01: the five passengers with the most tickets")
    @Nested
    class DEMODB_TICKETS_I_01_Test {
    }

    @DisplayName("DEMODB_TICKETS_I_02: the ten countries whose passengers hold the most tickets")
    @Nested
    class DEMODB_TICKETS_I_02_Test {
    }

    @DisplayName("DEMODB_TICKETS_I_03: how many tickets have no segment")
    @Nested
    class DEMODB_TICKETS_I_03_Test {
    }

    @DisplayName("DEMODB_TICKETS_I_04: how many bookings carry one, two or more passengers")
    @Nested
    class DEMODB_TICKETS_I_04_Test {
    }

    @DisplayName("DEMODB_TICKETS_I_05: the passenger ids recorded under more than one name")
    @Nested
    class DEMODB_TICKETS_I_05_Test {
    }
}
