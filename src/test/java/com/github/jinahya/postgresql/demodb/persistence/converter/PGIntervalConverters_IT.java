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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies each {@code interval} converter against the text the installed database prints, the
 * style picked by the JDBC URL as an application would pick it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class PGIntervalConverters_IT {

    private static final String URL = "jdbc:postgresql://localhost:5432/demo";

    private static final String USER = "postgres";

    private static final String PASSWORD = "password";

    private static final List<String> INTERVALS = List.of(
            "0", "02:05:00", "26:05:00", "1 day 02:05:00", "-02:05:00", "-1 day -02:05:00",
            "1 day -02:05:00", "00:00:01.5", "-00:00:01.5", "1 mon 2 days", "1 year 2 mons",
            "-1 year -2 mons", "1 year 2 mons 3 days 04:05:06.5",
            "1 year -2 mons 3 days -04:05:06", "3 days", "-3 days", "1 year", "00:00:00.000001"
    );

    static Stream<Arguments> styles() {
        return Stream.of(
                Arguments.of("postgres", new PGIntervalConverter.OfPostgres()),
                Arguments.of("postgres_verbose", new PGIntervalConverter.OfPostgresVerbose()),
                Arguments.of("sql_standard", new PGIntervalConverter.OfSqlStandard()),
                Arguments.of("iso_8601", new PGIntervalConverter.OfIso8601())
        );
    }

    private static Connection connect(final String style) throws SQLException {
        return DriverManager.getConnection(
                URL + "?options=-c%20IntervalStyle=" + style, USER, PASSWORD);
    }

    /**
     * Reads each interval as the server prints it in {@code style}, converts it, and has the server
     * read the converted interval's value back; the two must print the same, field by field, under
     * the {@code postgres} style.
     */
    @MethodSource("styles")
    @ParameterizedTest
    void convertToEntityAttribute_SameInterval_ServerText(
            final String style, final PGIntervalConverter<String> converter) throws SQLException {
        try (var printed = connect(style);
             var compared = connect("postgres")) {
            for (final var interval : INTERVALS) {
                final String text;
                try (var statement = printed.prepareStatement("SELECT CAST(? AS interval)::text")) {
                    statement.setString(1, interval);
                    try (var result = statement.executeQuery()) {
                        result.next();
                        text = result.getString(1);
                    }
                }
                final var converted = converter.convertToEntityAttribute(text);
                try (var statement = compared.prepareStatement(
                        "SELECT CAST(? AS interval)::text, CAST(? AS interval)::text")) {
                    statement.setString(1, interval);
                    statement.setString(2, converted.getValue());
                    try (var result = statement.executeQuery()) {
                        result.next();
                        log.debug("{}: {} -> {} -> {}", style, interval, text,
                                  converted.getValue());
                        assertThat(result.getString(2))
                                .as("%s, printed as %s in %s, converted to %s", interval, text,
                                    style, converted.getValue())
                                .isEqualTo(result.getString(1));
                    }
                }
                // and what the converter writes, the server reads, in the same style, as it was
                final var written = converter.convertToDatabaseColumn(converted);
                final String read;
                try (var statement = printed.prepareStatement(
                        "SELECT CAST(CAST(? AS interval) AS text)")) {
                    statement.setString(1, written);
                    try (var result = statement.executeQuery()) {
                        result.next();
                        read = result.getString(1);
                    }
                }
                assertThat(read)
                        .as("%s, written as %s in %s", interval, written, style)
                        .isEqualTo(text);
            }
        }
    }
}

