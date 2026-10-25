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
import jakarta.json.JsonException;
import jakarta.json.JsonValue;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.StringReader;
import java.util.NoSuchElementException;

/**
 * Converts between a JSON-P {@link JsonValue} and a PostgreSQL {@code jsonb} (or {@code json})
 * column, through its text.
 * <p>
 * A {@code jsonb} column holds any one JSON value, an object, an array, a string, a number,
 * {@code true}, {@code false} or {@code null}, and {@link JsonValue} is the type of all of them;
 * the value is read with a {@link jakarta.json.stream.JsonParser}, which reads each of them and
 * tells whether anything follows, and written as its {@link JsonValue#toString() text}. A JSON
 * {@code null} is {@link JsonValue#NULL}, not an SQL {@code NULL}, which is {@code null} here as
 * for every converter.
 * <p>
 * The two providers hand the converter different things on a read: Hibernate the column's text,
 * EclipseLink the driver's {@link org.postgresql.util.PGobject}, whose {@code toString()} is that
 * text. So the database side is a type variable here, and {@link OfString}, the converter an
 * attribute names, binds it to {@link String} without overriding the methods: a method declared
 * on a type variable takes an {@link Object} once erased, and accepts either; one declared on
 * {@link String} would have the compiler cast whatever it is handed to a {@link String}, and fail
 * with EclipseLink's, before the method runs.
 * <p>
 * The converter needs an implementation of JSON-P at run time, such as Eclipse Parsson.
 *
 * @param <Y> the type of the database side; {@link String} in {@link OfString}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class JsonValueConverter<Y>
        implements AttributeConverter<JsonValue, Y> {

    /**
     * Converts between a {@link JsonValue} and the text of a {@code jsonb} column.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfString
            extends JsonValueConverter<String> {

        /**
         * Creates a new instance.
         */
        public OfString() {
            super();
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected JsonValueConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the JSON text of {@code attribute}; {@code null} if {@code attribute} is
     *         {@code null}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Y convertToDatabaseColumn(final JsonValue attribute) {
        if (attribute == null) {
            return null;
        }
        return (Y) attribute.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the column's text, or an object whose {@code toString()} is
     *               that text; one JSON value.
     * @return the value {@code dbData} denotes; {@code null} if {@code dbData} is {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is not one JSON value.
     */
    @Override
    public JsonValue convertToEntityAttribute(final Y dbData) {
        if (dbData == null) {
            return null;
        }
        final var text = dbData.toString();
        // a parser, not a JsonReader: JsonReader#readValue() reads the first value and ignores
        // whatever follows it, "1 2" or "{} []", which the server rejects
        try (var parser = Json.createParser(new StringReader(text))) {
            if (!parser.hasNext()) {
                throw new IllegalArgumentException("not a JSON value: " + text);
            }
            parser.next();
            final var value = parser.getValue();
            if (parser.hasNext()) {
                throw new IllegalArgumentException("more than one JSON value: " + text);
            }
            return value;
        } catch (final JsonException | IllegalStateException | NoSuchElementException e) {
            // JsonParsingException, a JsonException, for a text which is no JSON
            throw new IllegalArgumentException("not a JSON value: " + text, e);
        }
    }
}

