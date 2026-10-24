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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Converts between a {@link Duration} and a PostgreSQL {@code interval} column.
 * <p>
 * Towards the database the duration becomes {@code [-]HH:MM:SS[.fraction]}, hours running past 24
 * as PostgreSQL allows, bound as text; the server casts it to {@code interval} when the driver
 * leaves the type to it ({@code stringtype=unspecified} on the JDBC URL). From the database the
 * converter takes whatever the provider hands it, since the two differ: Hibernate passes the
 * interval as text, EclipseLink the driver's own interval object, whose {@code toString()} is
 * parsed. Every text the server prints, under each of its {@code IntervalStyle}s, is accepted, and
 * so is the driver's own form:
 * <ul>
 * <li>{@code postgres}: {@code 1 day 02:05:00}, {@code -1 days -02:05:00};</li>
 * <li>{@code postgres_verbose}: {@code @ 1 day 2 hours 5 mins}, {@code @ 1 day 2 hours 5 mins ago},
 * the {@code ago} negating the whole;</li>
 * <li>{@code sql_standard}: {@code 1 2:05:00}, {@code -1 2:05:00}, the sign applying to the
 * whole;</li>
 * <li>{@code iso_8601}: {@code P1DT2H5M}, {@code P-1DT-2H-5M};</li>
 * <li>the driver's: {@code 1 days 2 hours 5 mins}, {@code 0 years 0 mons 1 days 2 hours 5 mins 0.00
 * secs}.</li>
 * </ul>
 * Years and months have no fixed length, so an interval holding either, in any style, is rejected
 * rather than guessed at. The database side is bound to {@link String} here, the methods inherited
 * as they are; see {@link _TextConverter} for why that is what keeps it portable.
 * <p>
 * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
 * {@link jakarta.persistence.Convert @Convert}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute#ATTRIBUTE_NAME_DURATION
 */
@Converter(autoApply = false)
public class IntervalConverter
        extends _TextConverter<Duration, String> {

    private static final Pattern UNIT_PART = Pattern.compile(
            "(?<sign>[-+]?)(?<value>\\d+(?:\\.\\d+)?)\\s*"
            + "(?<unit>years?|mons?|months?|days?|hours?|mins?|minutes?|secs?|seconds?)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern CLOCK_PART = Pattern.compile(
            "(?<sign>[-+]?)(?<hours>\\d+):(?<minutes>\\d{1,2})(?::(?<seconds>\\d{1,2}(?:\\.\\d+)" +
            "?))?");

    /**
     * The {@code sql_standard} style: an optional sign for the whole, an optional year-month part,
     * an optional day count, an optional clock.
     */
    private static final Pattern SQL_STANDARD = Pattern.compile(
            "(?<sign>[-+]?)(?:(?<years>\\d+)-(?<months>\\d+))?\\s*(?:(?<days>[-+]?\\d+)" +
            "(?=\\s+[-+]?\\d+:))?"
            + "\\s*(?<clock>[-+]?\\d+:\\d{1,2}(?::\\d{1,2}(?:\\.\\d+)?)?)?");

    private static final Pattern ISO_8601 = Pattern.compile("[-+]?P.*", Pattern.CASE_INSENSITIVE);

    private static final Pattern ISO_8601_YEAR_OR_MONTH = Pattern.compile(
            "[-+]?P[^T]*\\d+[YM]", Pattern.CASE_INSENSITIVE);

    private static final long SECONDS_PER_DAY = 86_400L;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    public IntervalConverter() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------------ text

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return {@code attribute} as {@code [-]HH:MM:SS[.fraction]}.
     */
    @Override
    protected String toText(final Duration attribute) {
        final var negative = attribute.isNegative();
        final var abs = attribute.abs();
        final var seconds = abs.getSeconds();
        final var nanos = abs.getNano();
        final var builder = new StringBuilder();
        if (negative) {
            builder.append('-');
        }
        builder.append(String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60,
                                     seconds % 60));
        if (nanos > 0) {
            final var fraction = String.format("%09d", nanos).replaceFirst("0+$", "");
            builder.append('.').append(fraction);
        }
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param text {@inheritDoc} -- the interval in any of the styles above.
     * @return the duration {@code text} denotes.
     * @throws IllegalArgumentException if {@code text} is not an interval, or holds years or
     *                                  months.
     */
    @Override
    protected Duration fromText(final String text) {
        return parse(text);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static Duration parse(final String dbData) {
        var text = dbData.trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException("not an interval: " + dbData);
        }
        // postgres_verbose: "@ " in front, " ago" behind for a negative whole
        var negate = false;
        if (text.startsWith("@")) {
            text = text.substring(1).trim();
        }
        if (text.toLowerCase().endsWith(" ago")) {
            negate = true;
            text = text.substring(0, text.length() - 4).trim();
        }
        final Duration parsed;
        if (ISO_8601.matcher(text).matches()) {
            parsed = parseIso8601(text, dbData);
        } else if (UNIT_PART.matcher(text).find() || CLOCK_PART.matcher(text).matches()) {
            parsed = parsePostgres(text, dbData);
        } else {
            parsed = parseSqlStandard(text, dbData);
        }
        return negate ? parsed.negated() : parsed;
    }

    /**
     * The {@code postgres} style, the verbose words, and the driver's own form: signed units, then
     * an optional signed clock.
     */
    private static Duration parsePostgres(final String text, final String dbData) {
        var total = BigDecimal.ZERO;
        final var units = UNIT_PART.matcher(text);
        while (units.find()) {
            final var value = new BigDecimal(units.group("value"));
            final var unit = units.group("unit").toLowerCase();
            final BigDecimal seconds;
            if (unit.startsWith("year") || unit.startsWith("mon")) {
                // the verbose output spells every unit out, "0 years 0 mons ..."; only a
                // non-zero one is a problem
                if (value.signum() != 0) {
                    throw new IllegalArgumentException(
                            "years and months have no fixed length: " + dbData);
                }
                continue;
            } else if (unit.startsWith("day")) {
                seconds = value.multiply(BigDecimal.valueOf(SECONDS_PER_DAY));
            } else if (unit.startsWith("hour")) {
                seconds = value.multiply(BigDecimal.valueOf(3600L));
            } else if (unit.startsWith("min")) {
                seconds = value.multiply(BigDecimal.valueOf(60L));
            } else {
                seconds = value;
            }
            total = total.add("-".equals(units.group("sign")) ? seconds.negate() : seconds);
        }
        var rest = units.replaceAll("").trim();
        final var clock = CLOCK_PART.matcher(rest);
        if (clock.find()) {
            total = total.add(clockSeconds(clock.group("hours"), clock.group("minutes"),
                                           clock.group("seconds"),
                                           "-".equals(clock.group("sign"))));
            rest = clock.replaceAll("").trim();
        }
        if (!rest.isEmpty()) {
            throw new IllegalArgumentException("not an interval: " + dbData);
        }
        return toDuration(total);
    }

    /**
     * The {@code sql_standard} style: {@code [-]Y-M D HH:MM:SS}, each part optional, the leading
     * sign for the whole.
     */
    private static Duration parseSqlStandard(final String text, final String dbData) {
        final var matcher = SQL_STANDARD.matcher(text);
        if (!matcher.matches() || (matcher.group("days") == null && matcher.group("clock") == null
                                   && matcher.group("years") == null)) {
            throw new IllegalArgumentException("not an interval: " + dbData);
        }
        if (matcher.group("years") != null
            && (Integer.parseInt(matcher.group("years")) != 0 ||
                Integer.parseInt(matcher.group("months")) != 0)) {
            throw new IllegalArgumentException("years and months have no fixed length: " + dbData);
        }
        var total = BigDecimal.ZERO;
        if (matcher.group("days") != null) {
            total = total.add(new BigDecimal(matcher.group("days")).multiply(
                    BigDecimal.valueOf(SECONDS_PER_DAY)));
        }
        if (matcher.group("clock") != null) {
            final var clock = CLOCK_PART.matcher(matcher.group("clock"));
            if (!clock.matches()) {
                throw new IllegalArgumentException("not an interval: " + dbData);
            }
            total = total.add(clockSeconds(clock.group("hours"), clock.group("minutes"),
                                           clock.group("seconds"),
                                           "-".equals(clock.group("sign"))));
        }
        return "-".equals(matcher.group("sign")) ? toDuration(total).negated() : toDuration(total);
    }

    /**
     * The {@code iso_8601} style, {@code P1DT2H5M}, which {@link Duration#parse(CharSequence)}
     * reads as long as there is no year or month designator.
     */
    private static Duration parseIso8601(final String text, final String dbData) {
        try {
            return Duration.parse(text);
        } catch (final DateTimeParseException dtpe) {
            if (ISO_8601_YEAR_OR_MONTH.matcher(text).lookingAt()) {
                throw new IllegalArgumentException(
                        "years and months have no fixed length: " + dbData, dtpe);
            }
            throw new IllegalArgumentException("not an interval: " + dbData, dtpe);
        }
    }

    private static BigDecimal clockSeconds(final String hours, final String minutes,
                                           final String seconds,
                                           final boolean negative) {
        var total = new BigDecimal(hours).multiply(BigDecimal.valueOf(3600L))
                .add(new BigDecimal(minutes).multiply(BigDecimal.valueOf(60L)));
        if (seconds != null) {
            total = total.add(new BigDecimal(seconds));
        }
        return negative ? total.negate() : total;
    }

    private static Duration toDuration(final BigDecimal seconds) {
        final var whole = seconds.setScale(0, RoundingMode.DOWN);
        final var nanos =
                seconds.subtract(whole).movePointRight(9).setScale(0, RoundingMode.HALF_UP);
        return Duration.ofSeconds(whole.longValueExact(), nanos.longValueExact());
    }
}
