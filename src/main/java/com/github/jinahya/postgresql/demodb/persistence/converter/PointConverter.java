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

import jakarta.persistence.Converter;

/**
 * Converts between a {@link Point} and a PostgreSQL {@code point} column, through its text,
 * {@code (x,y)}. The database side is bound to {@link String} here, and the methods are inherited
 * as they are; see {@link _TextConverter} for why that is what keeps it portable.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class PointConverter
        extends _TextConverter<Point, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    public PointConverter() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------------ text

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return {@code attribute} as {@code (x,y)}.
     */
    @Override
    protected String toText(final Point attribute) {
        return "(" + attribute.x() + "," + attribute.y() + ")";
    }

    /**
     * {@inheritDoc}
     *
     * @param text {@inheritDoc} -- the point as {@code (x,y)}, which is what the server writes;
     *             the parentheses may be left out, and spaces put around the numbers, as the server
     *             accepts it.
     * @return the point {@code text} denotes.
     * @throws IllegalArgumentException if {@code text} is not a point.
     */
    @Override
    protected Point fromText(final String text) {
        var body = text.strip();
        if (body.startsWith("(") != body.endsWith(")")) {
            throw new IllegalArgumentException("not a point: " + text);
        }
        if (body.startsWith("(")) {
            body = body.substring(1, body.length() - 1);
        }
        final var parts = body.split(",", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("not a point: " + text);
        }
        try {
            return new Point(Double.parseDouble(parts[0].strip()),
                             Double.parseDouble(parts[1].strip()));
        } catch (final NumberFormatException nfe) {
            throw new IllegalArgumentException("not a point: " + text, nfe);
        }
    }
}

