package com.github.jinahya.postgresql.demodb.persistence.converter;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link ZoneIdConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ZoneIdConverter_Test {

    @ParameterizedTest
    @ValueSource(strings = {"Asia/Seoul", "Europe/Moscow", "America/New_York", "Asia/Saigon",
            "Pacific/Truk"})
    void convertToEntityAttribute_SameId_RoundTrip(final String text) {
        final var converter = new ZoneIdConverter();
        final var zone = converter.convertToEntityAttribute(text);
        assertThat(zone).isEqualTo(ZoneId.of(text));
        assertThat(converter.convertToDatabaseColumn(zone)).isEqualTo(text);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Korean Time", "Asia/Nowhere", ""})
    void convertToEntityAttribute_IllegalArgumentException_NotAZone(final String text) {
        assertThatThrownBy(() -> new ZoneIdConverter().convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convertBoth_Null_Null() {
        final var converter = new ZoneIdConverter();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}

