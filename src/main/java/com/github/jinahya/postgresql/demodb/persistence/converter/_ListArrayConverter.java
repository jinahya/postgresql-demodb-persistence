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

import jakarta.persistence.AttributeConverter;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * An abstract converter between a {@link List} of {@code E} and a one-dimensional PostgreSQL array
 * column, each element through a converter of its own.
 * <p>
 * This class owns what every array has in common: the literal syntax, {@code {1,2,3}},
 * {@code {"a b",NULL}}, with its quoting and escaping, and the shapes the value arrives in from the
 * database, which differ by provider -- Hibernate passes the literal as text, EclipseLink the
 * driver's {@link java.sql.Array} or the same already unpacked into a Java array. What is specific
 * to the element type is the element converter a subclass hands the constructor, an
 * {@link AttributeConverter} between one element and its text, which this class applies to each
 * element in turn; an element the driver already unpacked goes through its {@code toString()}. A
 * {@code NULL} element becomes a {@code null} in the list, and back.
 * <p>
 * Towards the database the list becomes the array literal, bound as text; the server casts it to
 * the column's type when the driver leaves the type to it ({@code stringtype=unspecified} on the
 * JDBC URL). The database side is a type variable a concrete subclass binds to {@link String}; see
 * {@link _TextConverter} for why.
 *
 * @param <E> the type of the elements.
 * @param <Y> the type of the database side; {@link String} in a concrete subclass.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
abstract class _ListArrayConverter<E, Y>
        extends _TextConverter<List<E>, Y> {

    /**
     * The text of a {@code NULL} element. The value is {@value}.
     */
    protected static final String NULL_ELEMENT = "NULL";

    /**
     * One element of a literal: quoted, with backslash escapes, or bare, with none of the
     * characters that would need the quotes.
     */
    private static final String ELEMENT = "\"(?:[^\"\\\\]|\\\\.)*\"|[^\\s\",{}\\\\]+";

    private static final Pattern ELEMENT_PATTERN = Pattern.compile(ELEMENT);

    /**
     * A one-dimensional literal: braces around zero or more elements separated by commas.
     */
    private static final Pattern LITERAL_PATTERN = Pattern.compile(
            "\\s*\\{\\s*(?:(?:" + ELEMENT + ")(?:\\s*,\\s*(?:" + ELEMENT + "))*\\s*)?\\}\\s*");

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\\\(.)");

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for the specified element converter.
     *
     * @param elementConverter the converter between one element and its text.
     */
    protected _ListArrayConverter(final AttributeConverter<E, String> elementConverter) {
        super();
        this.elementConverter =
                Objects.requireNonNull(elementConverter, "elementConverter is null");
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the array literal as text, a {@link java.sql.Array}, or a Java
     *               array.
     * @return a list of the elements of {@code dbData}, or {@code null} if {@code dbData} is
     *         {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is none of the above, or the text is not a
     *                                  one-dimensional array literal of the element type.
     */
    @Override
    public List<E> convertToEntityAttribute(final Y dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof java.sql.Array array) {
            try {
                return fromArray(array.getArray());
            } catch (final SQLException sqle) {
                throw new IllegalArgumentException("failed to unpack " + dbData, sqle);
            }
        }
        if (dbData.getClass().isArray()) {
            return fromArray(dbData);
        }
        return super.convertToEntityAttribute(dbData);
    }

    // ------------------------------------------------------------------------------------------------------------ text

    @Override
    protected String toText(final List<E> attribute) {
        final var builder = new StringBuilder("{");
        var first = true;
        for (final var element : attribute) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            final var text =
                    element == null ? null : elementConverter.convertToDatabaseColumn(element);
            builder.append(text == null ? NULL_ELEMENT : quote(text));
        }
        return builder.append('}').toString();
    }

    @Override
    protected List<E> fromText(final String text) {
        if (!LITERAL_PATTERN.matcher(text).matches()) {
            throw new IllegalArgumentException("not a one-dimensional array literal: " + text);
        }
        final var list = new ArrayList<E>();
        final var elements = ELEMENT_PATTERN.matcher(text);
        while (elements.find()) {
            final var element = elements.group();
            if (element.charAt(0) == '"') {
                final var unquoted =
                        ESCAPE_PATTERN.matcher(element.substring(1, element.length() - 1))
                                .replaceAll("$1");
                list.add(elementConverter.convertToEntityAttribute(unquoted));
            } else {
                list.add(element.equalsIgnoreCase(NULL_ELEMENT)
                                 ? null : elementConverter.convertToEntityAttribute(element));
            }
        }
        return list;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private List<E> fromArray(final Object array) {
        final var length = Array.getLength(array);
        final var list = new ArrayList<E>(length);
        for (var i = 0; i < length; i++) {
            final var element = Array.get(array, i);
            list.add(element == null ? null : elementConverter.convertToEntityAttribute(
                    element.toString()));
        }
        return list;
    }

    private static String quote(final String element) {
        var needed = element.isEmpty() || element.equalsIgnoreCase(NULL_ELEMENT);
        for (var i = 0; !needed && i < element.length(); i++) {
            final var c = element.charAt(i);
            needed = c == '"' || c == '\\' || c == ',' || c == '{' || c == '}'
                     || Character.isWhitespace(c);
        }
        if (!needed) {
            return element;
        }
        final var builder = new StringBuilder("\"");
        for (var i = 0; i < element.length(); i++) {
            final var c = element.charAt(i);
            if (c == '"' || c == '\\') {
                builder.append('\\');
            }
            builder.append(c);
        }
        return builder.append('"').toString();
    }

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Returns the element converter this converter applies to each element.
     *
     * @return the element converter.
     */
    AttributeConverter<E, String> elementConverter() {
        return elementConverter;
    }

    private final AttributeConverter<E, String> elementConverter;
}
