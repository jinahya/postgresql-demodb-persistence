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
 * Converts between a {@link FlightStatus} and its column value, {@link FlightStatus#text()}. The database side is
 * bound to {@link String} here, and the methods are inherited as they are; see
 * {@link _TextConverter} for why that is what keeps it portable.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class FlightStatusConverter
        extends _TextConverter<FlightStatus, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    public FlightStatusConverter() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------------ text

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return {@code attribute}'s {@link FlightStatus#text() text}.
     */
    @Override
    protected String toText(final FlightStatus attribute) {
        return attribute.text();
    }

    /**
     * {@inheritDoc}
     *
     * @param text {@inheritDoc}
     * @return the constant of {@code text}.
     * @throws IllegalArgumentException if no constant's text is {@code text}.
     */
    @Override
    protected FlightStatus fromText(final String text) {
        return FlightStatus.valueOfText(text);
    }
}

