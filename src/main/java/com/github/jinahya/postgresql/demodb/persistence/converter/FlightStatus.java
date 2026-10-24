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

import java.util.Arrays;
import java.util.Objects;

/**
 * The status of a flight, the value of a {@code flights.status} column, and of the
 * {@code timetable} view's; the values the {@code flight_status_check} constraint allows.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see FlightStatusConverter
 */
public enum FlightStatus {

    /**
     * {@code Scheduled}.
     */
    SCHEDULED("Scheduled"),

    /**
     * {@code On Time}.
     */
    ON_TIME("On Time"),

    /**
     * {@code Delayed}.
     */
    DELAYED("Delayed"),

    /**
     * {@code Boarding}.
     */
    BOARDING("Boarding"),

    /**
     * {@code Departed}.
     */
    DEPARTED("Departed"),

    /**
     * {@code Arrived}.
     */
    ARRIVED("Arrived"),

    /**
     * {@code Cancelled}.
     */
    CANCELLED("Cancelled");

    /**
     * Returns the constant of the specified column value.
     *
     * @param text the column value.
     * @return the constant whose {@link #text() text} is {@code text}.
     * @throws NullPointerException     if {@code text} is {@code null}.
     * @throws IllegalArgumentException if no constant's text is {@code text}.
     */
    public static FlightStatus valueOfText(final String text) {
        Objects.requireNonNull(text, "text is null");
        return Arrays.stream(values())
                .filter(v -> v.text.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no constant for '" + text + "'"));
    }

    FlightStatus(final String text) {
        this.text = text;
    }

    /**
     * Returns the column value of this constant.
     *
     * @return the column value of this constant.
     */
    public String text() {
        return text;
    }

    private final String text;
}

