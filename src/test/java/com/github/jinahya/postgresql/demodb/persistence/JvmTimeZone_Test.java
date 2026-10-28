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

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the tests run in UTC, as the mapping of a {@code tstzrange} requires; see
 * {@link com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class JvmTimeZone_Test {

    /**
     * Verifies that the JVM's default time zone is UTC, which the PostgreSQL driver gives each
     * session, and which a range reads back in.
     *
     * @implNote Surefire's and failsafe's {@code argLine} set it, {@code -Duser.timezone=UTC};
     *         without it, a range reads back in another zone than the one it was written in.
     */
    @Test
    void systemDefault_Utc() {
        assertThat(ZoneId.systemDefault().normalized())
                .as("the JVM's default time zone; the tests need -Duser.timezone=UTC")
                .isEqualTo(ZoneOffset.UTC);
    }
}
