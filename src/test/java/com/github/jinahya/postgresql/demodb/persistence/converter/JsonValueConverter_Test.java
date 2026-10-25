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

import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.persistence.AttributeConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link JsonValueConverter.OfString}, with every kind of value a {@code jsonb} column
 * holds.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class JsonValueConverter_Test {

    static Stream<Arguments> values() {
        return Stream.of(
                Arguments.of("{\"en\": \"Anaa\", \"ru\": \"Анаа\"}",
                             JsonValue.ValueType.OBJECT),
                Arguments.of("[1, \"x\", [true]]", JsonValue.ValueType.ARRAY),
                Arguments.of("\"text\"", JsonValue.ValueType.STRING),
                Arguments.of("42", JsonValue.ValueType.NUMBER),
                Arguments.of("1.50", JsonValue.ValueType.NUMBER),
                Arguments.of("true", JsonValue.ValueType.TRUE),
                Arguments.of("false", JsonValue.ValueType.FALSE),
                Arguments.of("null", JsonValue.ValueType.NULL),
                Arguments.of(" {\"a\" : [ ] } ", JsonValue.ValueType.OBJECT)
        );
    }

    @MethodSource("values")
    @ParameterizedTest
    void convertToEntityAttribute_ValueOfItsType_Text(final String text,
                                                      final JsonValue.ValueType type) {
        final var value = new JsonValueConverter.OfString().convertToEntityAttribute(text);
        assertThat(value.getValueType()).as("type of %s", text).isEqualTo(type);
    }

    @MethodSource("values")
    @ParameterizedTest
    void convert_SameValue_RoundTrip(final String text, final JsonValue.ValueType type) {
        final var converter = new JsonValueConverter.OfString();
        final var value = converter.convertToEntityAttribute(text);
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(value)))
                .as("%s", text)
                .isEqualTo(value);
    }

    @Test
    void convertToEntityAttribute_Object_Fields() {
        final var value = new JsonValueConverter.OfString()
                .convertToEntityAttribute("{\"en\": \"Anaa\", \"ru\": \"Анаа\"}");
        assertThat(value.asJsonObject().getString("en")).isEqualTo("Anaa");
        assertThat(value.asJsonObject().getString("ru")).isEqualTo("Анаа");
    }

    @Test
    void convertToDatabaseColumn_Text_Value() {
        final var converter = new JsonValueConverter.OfString();
        assertThat(converter.convertToDatabaseColumn(
                Json.createObjectBuilder().add("en", "Anaa").build()))
                .isEqualTo("{\"en\":\"Anaa\"}");
        assertThat(converter.convertToDatabaseColumn(JsonValue.NULL)).isEqualTo("null");
        assertThat(converter.convertToDatabaseColumn(Json.createValue(42))).isEqualTo("42");
    }

    /**
     * An SQL {@code NULL} is {@code null}; a JSON {@code null} is {@link JsonValue#NULL}.
     */
    @Test
    void convert_Null_SqlNull() {
        final var converter = new JsonValueConverter.OfString();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
        assertThat(converter.convertToEntityAttribute("null")).isEqualTo(JsonValue.NULL);
    }

    /**
     * The driver's own object, as EclipseLink hands it over, through the erased signature.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void convertToEntityAttribute_Value_DriverObject() throws SQLException {
        final var object = new PGobject();
        object.setType("jsonb");
        object.setValue("{\"en\": \"Anaa\"}");
        final AttributeConverter converter = new JsonValueConverter.OfString();
        assertThat(((JsonValue) converter.convertToEntityAttribute(object)).asJsonObject()
                           .getString("en"))
                .isEqualTo("Anaa");
    }

    /**
     * What the server rejects for a {@code jsonb}, the converter rejects too.
     */
    @ValueSource(strings = {
            "",
            "{\"a\": 1",
            "{'a': 1}",
            "NaN",
            "undefined",
            "1 2",
            "{} []"
    })
    @ParameterizedTest
    void convertToEntityAttribute_IllegalArgumentException_NotJson(final String text) {
        assertThatThrownBy(() -> new JsonValueConverter.OfString().convertToEntityAttribute(text))
                .as("%s", text)
                .isInstanceOf(IllegalArgumentException.class);
    }
}

