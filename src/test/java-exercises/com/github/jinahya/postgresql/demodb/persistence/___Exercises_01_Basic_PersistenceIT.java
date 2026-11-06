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
 * Answers the exercises of {@code src/test/sql-exercises/__/01_basic.sql}; a nested class per exercise.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class ___Exercises_01_Basic_PersistenceIT {

    @DisplayName("DEMODB_SCHEMA_B_01: the tables and the views of the schema")
    @Nested
    class DEMODB_SCHEMA_B_01_Test {
    }

    @DisplayName("DEMODB_SCHEMA_B_02: the columns of tickets")
    @Nested
    class DEMODB_SCHEMA_B_02_Test {
    }

    @DisplayName("DEMODB_SCHEMA_B_03: the foreign keys, and what they reference")
    @Nested
    class DEMODB_SCHEMA_B_03_Test {
    }

    @DisplayName("DEMODB_SCHEMA_B_04: the functions of the schema")
    @Nested
    class DEMODB_SCHEMA_B_04_Test {
    }

    @DisplayName("DEMODB_SCHEMA_B_05: how many rows each table has")
    @Nested
    class DEMODB_SCHEMA_B_05_Test {
    }

    @DisplayName("DEMODB_SCHEMA_B_06: the sold segments of the cancelled flights")
    @Nested
    class DEMODB_SCHEMA_B_06_Test {
    }
}
