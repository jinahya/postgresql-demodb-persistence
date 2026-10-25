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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.postgresql.util.PGInterval;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link PGIntervalConverter.OfPostgres},
 * {@link PGIntervalConverter.OfPostgresVerbose}, {@link PGIntervalConverter.OfSqlStandard} and
 * {@link PGIntervalConverter.OfIso8601}, with the texts the server prints in each style.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class PGIntervalConverters_Test {

    private static PGInterval i(final int years, final int months, final int days,
                                final int hours, final int minutes, final double seconds) {
        return new PGInterval(years, months, days, hours, minutes, seconds);
    }

    /**
     * The same intervals, as the server prints each of them in each style.
     */
    static Stream<Arguments> texts() {
        final var postgres = new PGIntervalConverter.OfPostgres();
        final var verbose = new PGIntervalConverter.OfPostgresVerbose();
        final var standard = new PGIntervalConverter.OfSqlStandard();
        final var iso = new PGIntervalConverter.OfIso8601();
        return Stream.of(
                Arguments.of(postgres, "00:00:00", i(0, 0, 0, 0, 0, 0)),
                Arguments.of(postgres, "02:05:00", i(0, 0, 0, 2, 5, 0)),
                Arguments.of(postgres, "26:05:00", i(0, 0, 0, 26, 5, 0)),
                Arguments.of(postgres, "1 day 02:05:00", i(0, 0, 1, 2, 5, 0)),
                Arguments.of(postgres, "-02:05:00", i(0, 0, 0, -2, -5, 0)),
                Arguments.of(postgres, "-1 days -02:05:00", i(0, 0, -1, -2, -5, 0)),
                Arguments.of(postgres, "1 day -02:05:00", i(0, 0, 1, -2, -5, 0)),
                Arguments.of(postgres, "-00:00:01.5", i(0, 0, 0, 0, 0, -1.5)),
                Arguments.of(postgres, "1 mon 2 days", i(0, 1, 2, 0, 0, 0)),
                Arguments.of(postgres, "-1 years -2 mons", i(-1, -2, 0, 0, 0, 0)),
                Arguments.of(postgres, "1 year 2 mons 3 days 04:05:06.5", i(1, 2, 3, 4, 5, 6.5)),
                Arguments.of(postgres, "10 mons 3 days -04:05:06", i(0, 10, 3, -4, -5, -6)),
                Arguments.of(postgres, "-3 days", i(0, 0, -3, 0, 0, 0)),
                Arguments.of(postgres, "00:00:00.000001", i(0, 0, 0, 0, 0, 0.000001)),

                Arguments.of(verbose, "@ 0", i(0, 0, 0, 0, 0, 0)),
                Arguments.of(verbose, "@ 2 hours 5 mins", i(0, 0, 0, 2, 5, 0)),
                Arguments.of(verbose, "@ 26 hours 5 mins", i(0, 0, 0, 26, 5, 0)),
                Arguments.of(verbose, "@ 1 day 2 hours 5 mins", i(0, 0, 1, 2, 5, 0)),
                Arguments.of(verbose, "@ 2 hours 5 mins ago", i(0, 0, 0, -2, -5, 0)),
                Arguments.of(verbose, "@ 1 day 2 hours 5 mins ago", i(0, 0, -1, -2, -5, 0)),
                Arguments.of(verbose, "@ 1 day -2 hours -5 mins", i(0, 0, 1, -2, -5, 0)),
                Arguments.of(verbose, "@ 1.5 secs ago", i(0, 0, 0, 0, 0, -1.5)),
                Arguments.of(verbose, "@ 1 mon 2 days", i(0, 1, 2, 0, 0, 0)),
                Arguments.of(verbose, "@ 1 year 2 mons ago", i(-1, -2, 0, 0, 0, 0)),
                Arguments.of(verbose, "@ 1 year 2 mons 3 days 4 hours 5 mins 6.5 secs",
                             i(1, 2, 3, 4, 5, 6.5)),
                Arguments.of(verbose, "@ 10 mons 3 days -4 hours -5 mins -6 secs",
                             i(0, 10, 3, -4, -5, -6)),
                Arguments.of(verbose, "@ 3 days ago", i(0, 0, -3, 0, 0, 0)),
                Arguments.of(verbose, "@ 0.000001 secs", i(0, 0, 0, 0, 0, 0.000001)),

                Arguments.of(standard, "0", i(0, 0, 0, 0, 0, 0)),
                Arguments.of(standard, "2:05:00", i(0, 0, 0, 2, 5, 0)),
                Arguments.of(standard, "26:05:00", i(0, 0, 0, 26, 5, 0)),
                Arguments.of(standard, "1 2:05:00", i(0, 0, 1, 2, 5, 0)),
                Arguments.of(standard, "-2:05:00", i(0, 0, 0, -2, -5, 0)),
                Arguments.of(standard, "-1 2:05:00", i(0, 0, -1, -2, -5, 0)),
                Arguments.of(standard, "+0-0 +1 -2:05:00", i(0, 0, 1, -2, -5, 0)),
                Arguments.of(standard, "-0:00:01.5", i(0, 0, 0, 0, 0, -1.5)),
                Arguments.of(standard, "+0-1 +2 +0:00:00", i(0, 1, 2, 0, 0, 0)),
                Arguments.of(standard, "-1-2", i(-1, -2, 0, 0, 0, 0)),
                Arguments.of(standard, "+1-2 +3 +4:05:06.5", i(1, 2, 3, 4, 5, 6.5)),
                Arguments.of(standard, "+0-10 +3 -4:05:06", i(0, 10, 3, -4, -5, -6)),
                Arguments.of(standard, "-3 0:00:00", i(0, 0, -3, 0, 0, 0)),
                Arguments.of(standard, "1-0", i(1, 0, 0, 0, 0, 0)),
                Arguments.of(standard, "0:00:00.000001", i(0, 0, 0, 0, 0, 0.000001)),

                Arguments.of(iso, "PT0S", i(0, 0, 0, 0, 0, 0)),
                Arguments.of(iso, "PT2H5M", i(0, 0, 0, 2, 5, 0)),
                Arguments.of(iso, "PT26H5M", i(0, 0, 0, 26, 5, 0)),
                Arguments.of(iso, "P1DT2H5M", i(0, 0, 1, 2, 5, 0)),
                Arguments.of(iso, "PT-2H-5M", i(0, 0, 0, -2, -5, 0)),
                Arguments.of(iso, "P-1DT-2H-5M", i(0, 0, -1, -2, -5, 0)),
                Arguments.of(iso, "P1DT-2H-5M", i(0, 0, 1, -2, -5, 0)),
                Arguments.of(iso, "PT-1.5S", i(0, 0, 0, 0, 0, -1.5)),
                Arguments.of(iso, "P1M2D", i(0, 1, 2, 0, 0, 0)),
                Arguments.of(iso, "P-1Y-2M", i(-1, -2, 0, 0, 0, 0)),
                Arguments.of(iso, "P1Y2M3DT4H5M6.5S", i(1, 2, 3, 4, 5, 6.5)),
                Arguments.of(iso, "P10M3DT-4H-5M-6S", i(0, 10, 3, -4, -5, -6)),
                Arguments.of(iso, "P-3D", i(0, 0, -3, 0, 0, 0)),
                Arguments.of(iso, "PT0.000001S", i(0, 0, 0, 0, 0, 0.000001))
        );
    }

    @MethodSource("texts")
    @ParameterizedTest
    void convertToEntityAttribute_Interval_Text(final PGIntervalConverter<String> converter,
                                                final String text, final PGInterval expected) {
        assertThat(converter.convertToEntityAttribute(text))
                .as("%s from %s", converter.getClass().getSimpleName(), text)
                .isEqualTo(expected);
    }

    /**
     * Each converter rejects the other styles' texts of {@code 1 day 02:05:00}, rather than
     * misreading them.
     */
    static Stream<Arguments> otherStyles() {
        return Stream.of(
                Arguments.of(new PGIntervalConverter.OfPostgres(), "@ 1 day 2 hours 5 mins"),
                Arguments.of(new PGIntervalConverter.OfPostgres(), "1 2:05:00"),
                Arguments.of(new PGIntervalConverter.OfPostgres(), "P1DT2H5M"),
                Arguments.of(new PGIntervalConverter.OfPostgresVerbose(), "1 day 02:05:00"),
                Arguments.of(new PGIntervalConverter.OfPostgresVerbose(), "1 2:05:00"),
                Arguments.of(new PGIntervalConverter.OfPostgresVerbose(), "P1DT2H5M"),
                Arguments.of(new PGIntervalConverter.OfSqlStandard(), "1 day 02:05:00"),
                Arguments.of(new PGIntervalConverter.OfSqlStandard(), "@ 1 day 2 hours 5 mins"),
                Arguments.of(new PGIntervalConverter.OfSqlStandard(), "P1DT2H5M"),
                Arguments.of(new PGIntervalConverter.OfIso8601(), "1 day 02:05:00"),
                Arguments.of(new PGIntervalConverter.OfIso8601(), "@ 1 day 2 hours 5 mins"),
                Arguments.of(new PGIntervalConverter.OfIso8601(), "1 2:05:00"),
                Arguments.of(new PGIntervalConverter.OfPostgres(), ""),
                Arguments.of(new PGIntervalConverter.OfPostgresVerbose(), "@"),
                Arguments.of(new PGIntervalConverter.OfSqlStandard(), ""),
                Arguments.of(new PGIntervalConverter.OfIso8601(), "P"),
                Arguments.of(new PGIntervalConverter.OfIso8601(), "P1DT")
        );
    }

    @MethodSource("otherStyles")
    @ParameterizedTest
    void convertToEntityAttribute_IllegalArgumentException_OtherStyle(
            final PGIntervalConverter<String> converter, final String text) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                .as("%s from %s", converter.getClass().getSimpleName(), text)
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * The driver's own object, as EclipseLink hands it over, through the erased signature.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void convertToEntityAttribute_Copy_DriverObject() {
        final var interval = i(0, 0, 1, 2, 5, 0);
        final PGIntervalConverter converter = new PGIntervalConverter.OfSqlStandard();
        final var converted = converter.convertToEntityAttribute(interval);
        assertThat(converted).isNotSameAs(interval).isEqualTo(interval);
    }

    /**
     * Each converter writes {@code 1 day -02:05:00} as the server prints it in its style.
     */
    static Stream<Arguments> written() {
        return Stream.of(
                Arguments.of(new PGIntervalConverter.OfPostgres(), "1 day -02:05:00"),
                Arguments.of(new PGIntervalConverter.OfPostgresVerbose(),
                             "@ 1 day -2 hours -5 mins"),
                Arguments.of(new PGIntervalConverter.OfSqlStandard(), "+0-0 +1 -2:05:00"),
                Arguments.of(new PGIntervalConverter.OfIso8601(), "P1DT-2H-5M")
        );
    }

    @MethodSource("written")
    @ParameterizedTest
    void convertToDatabaseColumn_ServerText_Interval(final PGIntervalConverter<String> converter,
                                                     final String expected) {
        assertThat(converter.convertToDatabaseColumn(i(0, 0, 1, -2, -5, 0)))
                .as("%s", converter.getClass().getSimpleName())
                .isEqualTo(expected);
    }

    /**
     * What each converter writes, it reads back.
     */
    @MethodSource("texts")
    @ParameterizedTest
    void convert_SameInterval_RoundTrip(final PGIntervalConverter<String> converter,
                                        final String text, final PGInterval interval) {
        final var written = converter.convertToDatabaseColumn(interval);
        assertThat(converter.convertToEntityAttribute(written))
                .as("%s, written as %s", interval.getValue(), written)
                .isEqualTo(interval);
    }

    @Test
    void convert_Null_Null() {
        final var converter = new PGIntervalConverter.OfPostgres();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}

