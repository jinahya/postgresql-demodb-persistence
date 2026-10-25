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
import org.postgresql.geometric.PGpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link PGpointConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class PGpointConverter_Test {

    @Test
    void convertToDatabaseColumn_Text_Point() {
        final var converter = new PGpointConverter();
        assertThat(converter.convertToDatabaseColumn(new PGpoint(126.4407d, 37.4602d)))
                .isEqualTo("(126.4407,37.4602)");
    }

    @Test
    void convertToDatabaseColumn_Null_Null() {
        assertThat(new PGpointConverter().convertToDatabaseColumn(null)).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "(126.4407,37.4602)",
            "( 126.4407 , 37.4602 )",
            "126.4407,37.4602"
    })
    void convertToEntityAttribute_Point_Text(final String text) {
        assertThat(new PGpointConverter().convertToEntityAttribute(text))
                .isEqualTo(new PGpoint(126.4407d, 37.4602d));
    }

    @Test
    void convertToEntityAttribute_Null_Null() {
        assertThat(new PGpointConverter().convertToEntityAttribute(null)).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "()",
            "(1)",
            "(a,b)"
    })
    void convertToEntityAttribute_IllegalArgumentException_NotAPoint(final String text) {
        assertThatThrownBy(() -> new PGpointConverter().convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convertToEntityAttribute_SamePoint_RoundTrip() {
        final var converter = new PGpointConverter();
        final var point = new PGpoint(-145.51d, -17.3526d);
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(point)))
                .isEqualTo(point);
    }
}

