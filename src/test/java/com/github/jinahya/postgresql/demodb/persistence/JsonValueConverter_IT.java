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

import com.github.jinahya.postgresql.demodb.persistence.converter.JsonValueConverter;
import jakarta.json.JsonValue;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link JsonValueConverter.OfString} with what the provider in use hands over for a {@code jsonb}
 * column of the installed {@code demo} database, and with what the server reads of what the
 * converter writes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class JsonValueConverter_IT {

    /**
     * Reads a value of the specified {@code jsonb} column, as the provider hands it over, the
     * driver's text or object, and converts it.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @CsvSource({
            "airplanes_data, model",
            "airports_data,  airport_name",
            "airports_data,  city",
            "airports_data,  country"
    })
    @ParameterizedTest
    void convertToEntityAttribute_Object_ProviderValue(final String table, final String column) {
        __Persistence_TestUtils.applyEntityManager(
                _Entity_Persistence_IT.PERSISTENCE_UNIT_NAME, em -> {
            final var raw = em.createNativeQuery(
                    "SELECT " + column + " FROM " + table + " LIMIT 1").getSingleResult();
            final AttributeConverter converter = new JsonValueConverter.OfString();
            final var value = (JsonValue) converter.convertToEntityAttribute(raw);
            log.info("{}.{}: {} ({}) -> {} ({})", table, column, raw, raw.getClass().getName(),
                     value, value.getValueType());
            assertThat(value.getValueType()).isEqualTo(JsonValue.ValueType.OBJECT);
            assertThat(value.asJsonObject().getString("en")).isNotBlank();
            assertThat(value.asJsonObject().getString("ru")).isNotBlank();
            return null;
        });
    }

    /**
     * Has the server read what the converter writes of each kind of value, through the provider's
     * parameter binding, as the same {@code jsonb}.
     */
    @ValueSource(strings = {
            "{\"en\": \"Anaa\", \"ru\": \"Анаа\"}",
            "[1, \"x\", [true]]",
            "\"text\"",
            "42",
            "1.50",
            "true",
            "false",
            "null"
    })
    @ParameterizedTest
    void convertToDatabaseColumn_SameJsonb_Value(final String text) {
        final var converter = new JsonValueConverter.OfString();
        final var written = converter.convertToDatabaseColumn(
                converter.convertToEntityAttribute(text));
        __Persistence_TestUtils.applyEntityManager(
                _Entity_Persistence_IT.PERSISTENCE_UNIT_NAME, em -> {
            final var same = em.createNativeQuery(
                            "SELECT CAST(CAST(?1 AS text) AS jsonb)"
                            + " = CAST(CAST(?2 AS text) AS jsonb)")
                    .setParameter(1, text)
                    .setParameter(2, written)
                    .getSingleResult();
            log.info("{} -> {}: {}", text, written, same);
            assertThat(same).as("%s, written as %s", text, written).isEqualTo(Boolean.TRUE);
            return null;
        });
    }
}

