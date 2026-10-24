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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Random values within what the columns of the {@code bookings} schema hold, for the randomizers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class __Random_TestUtils {

    /**
     * Lower case, where the installed codes -- IATA codes, airplane codes, booking references --
     * are upper case, so that a random code never collides with an installed row.
     */
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

    private static final String DIGITS = "0123456789";

    private static final List<String> TIMEZONES =
            List.of("Asia/Seoul", "Europe/Moscow", "Asia/Yekaterinburg",
                    "Asia/Vladivostok", "America/Santiago", "Pacific/Tahiti");

    private static String of(final String alphabet, final int length) {
        final var random = ThreadLocalRandom.current();
        final var builder = new StringBuilder(length);
        for (var i = 0; i < length; i++) {
            builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    /**
     * Returns a code of the specified length, of lower-case letters and digits, such as an IATA
     * code or a booking reference; lower-case so that it never collides with an installed one,
     * which are upper-case.
     */
    static String code(final int length) {
        return of(LOWER + DIGITS, length);
    }

    /**
     * Returns a string of the specified number of digits, such as a ticket number.
     */
    static String digits(final int length) {
        return of(DIGITS, length);
    }

    @SafeVarargs
    static <T> T pick(final T... values) {
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }

    static int positiveInt() {
        return ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
    }

    /**
     * Returns a non-negative amount of the specified precision and scale.
     */
    static BigDecimal amount(final int precision, final int scale) {
        final var unscaled =
                ThreadLocalRandom.current().nextLong(0L, (long) Math.pow(10, precision));
        return BigDecimal.valueOf(unscaled, scale).setScale(scale, RoundingMode.UNNECESSARY);
    }

    static OffsetDateTime instant() {
        return OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS)
                .plusDays(ThreadLocalRandom.current().nextInt(-365, 365));
    }

    static LocalTime time() {
        return LocalTime.of(ThreadLocalRandom.current().nextInt(24),
                            ThreadLocalRandom.current().nextInt(0, 12) * 5);
    }

    static Duration duration() {
        return Duration.ofMinutes(ThreadLocalRandom.current().nextInt(30, 900));
    }

    /**
     * Returns a route number of the schema's shape, {@code PG} and four digits.
     */
    static String routeNo() {
        return "PG" + digits(4);
    }

    static String seatNo() {
        return ThreadLocalRandom.current().nextInt(1, 60) + of("ABCDEFHJK", 1);
    }

    /**
     * Returns a {@code tstzrange} literal of one calendar month, {@code [start,end)}, in UTC, in a
     * year after every installed route's validity, so that no random route overlaps an installed
     * one with the same number.
     */
    static String validity() {
        final var month = YearMonth.of(ThreadLocalRandom.current().nextInt(2030, 2040),
                                       ThreadLocalRandom.current().nextInt(1, 13));
        return "[" + month.atDay(1) + " 00:00:00+00," + month.plusMonths(1).atDay(1) +
               " 00:00:00+00)";
    }

    /**
     * Returns a non-empty, ascending subset of the days of the week, {@code 1} to {@code 7}.
     */
    static List<Integer> daysOfWeek() {
        final var random = ThreadLocalRandom.current();
        final var days = new ArrayList<Integer>();
        for (var day = 1; day <= 7; day++) {
            if (random.nextBoolean()) {
                days.add(day);
            }
        }
        if (days.isEmpty()) {
            days.add(random.nextInt(1, 8));
        }
        return days;
    }

    /**
     * Returns a {@code point} literal, {@code (longitude,latitude)}.
     */
    static String point() {
        final var random = ThreadLocalRandom.current();
        return "(" +
               BigDecimal.valueOf(random.nextDouble(-180, 180)).setScale(4, RoundingMode.HALF_UP)
               + "," +
               BigDecimal.valueOf(random.nextDouble(-90, 90)).setScale(4, RoundingMode.HALF_UP) +
               ")";
    }

    /**
     * Returns a {@code jsonb} literal of the schema's multilingual shape, an English and a Russian
     * name.
     */
    static String jsonNames() {
        return "{\"en\": \"" + code(8) + "\", \"ru\": \"" + code(8) + "\"}";
    }

    static Map<String, String> names() {
        return Map.of("en", code(8), "ru", code(8));
    }

    static String timezone() {
        return pick(TIMEZONES.toArray(String[]::new));
    }

    private __Random_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
