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
import org.postgresql.geometric.PGpoint;

import java.sql.SQLException;

/**
 * Converts between a {@link PGpoint} and a PostgreSQL {@code point} column, through its text,
 * {@code (x,y)}. The database side is bound to {@link String} here, and the methods are inherited
 * as they are; see {@link _TextConverter} for why that is what keeps it portable.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class PGpointConverter
        extends _TextConverter<PGpoint, String> {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    public PGpointConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------- text

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return {@code attribute} as {@code (x,y)}.
     */
    @Override
    protected String toText(final PGpoint attribute) {
        return attribute.getValue();
    }

    /**
     * {@inheritDoc}
     *
     * @param text {@inheritDoc} -- the point as {@code (x,y)}, which is what the server writes.
     * @return the point {@code text} denotes.
     * @throws IllegalArgumentException if {@link PGpoint} does not read {@code text} as a point.
     */
    @Override
    protected PGpoint fromText(final String text) {
        try {
            return new PGpoint(text);
        } catch (final SQLException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("not a point: " + text, e);
        }
    }
}

