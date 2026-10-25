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
import org.postgresql.util.PGInterval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Converts between a {@link PGInterval} and a PostgreSQL {@code interval} column, through the
 * text the server prints in one {@code IntervalStyle}.
 * <p>
 * The server prints an interval in the session's {@code IntervalStyle}, and the driver's own
 * {@link PGInterval#PGInterval(String)} does not read every one of them: it reads
 * {@code sql_standard}'s {@code 1 2:05:00}, a day and a clock, as {@code 0 secs}. So there is a
 * converter for each style, nested here: the JDBC URL picks the style,
 * {@code options=-c IntervalStyle=...}, and an attribute names the converter of the same style.
 * {@snippet :
 * @Convert(converter = PGIntervalConverter.OfSqlStandard.class) // jdbc:...?options=-c%20IntervalStyle=sql_standard
 * private PGInterval duration;
 *}
 * <p>
 * Each converter writes the interval in its own style too, so that what it writes, it reads back,
 * whether the server prints the column or another database keeps the text as it is. From the
 * database, a provider which hands the driver's own object over, as EclipseLink does, is given a
 * copy of it; the text was read by the driver then, not by the converter.
 *
 * @param <Y> the type of the database side; {@link String} in a nested converter.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class PGIntervalConverter<Y>
        extends _TextConverter<PGInterval, Y> {

    /**
     * Converts through the text of the {@code postgres} {@code IntervalStyle}, the server's
     * default: {@code 1 year 2 mons 3 days 04:05:06.5}, {@code -1 days -02:05:00}.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfPostgres
            extends PGIntervalConverter<String> {

        private static final String STYLE = "postgres";

        private static final Pattern PATTERN = Pattern.compile(
                "(?:(?<years>[+-]?\\d+) years?)?\\s*"
                + "(?:(?<months>[+-]?\\d+) mons?)?\\s*"
                + "(?:(?<days>[+-]?\\d+) days?)?\\s*"
                + "(?:(?<sign>[+-]?)(?<hours>\\d+):(?<minutes>\\d{2}):"
                + "(?<seconds>\\d{2}(?:\\.\\d+)?))?");

        /**
         * Creates a new instance.
         */
        public OfPostgres() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return {@code attribute} in the {@code postgres} style.
         */
        @Override
        protected String toText(final PGInterval attribute) {
            final var parts = new ArrayList<String>();
            unit(parts, attribute.getYears(), "year", "years");
            unit(parts, attribute.getMonths(), "mon", "mons");
            unit(parts, attribute.getDays(), "day", "days");
            final var micros = micros(attribute);
            if (micros != 0L || parts.isEmpty()) {
                parts.add(clock(micros, "%02d"));
            }
            return String.join(" ", parts);
        }

        /**
         * {@inheritDoc}
         *
         * @param text {@inheritDoc} -- the interval in the {@code postgres} style.
         * @return the interval {@code text} denotes.
         * @throws IllegalArgumentException if {@code text} is not an interval in the
         *                                  {@code postgres} style.
         */
        @Override
        protected PGInterval fromText(final String text) {
            final var matcher = PATTERN.matcher(text.strip());
            if (text.isBlank() || !matcher.matches()) {
                throw notAnInterval(STYLE, text, null);
            }
            final var negative = "-".equals(matcher.group("sign"));
            final var hours = integer(matcher.group("hours"));
            final var minutes = integer(matcher.group("minutes"));
            final var seconds = decimal(matcher.group("seconds"));
            return interval(integer(matcher.group("years")), integer(matcher.group("months")),
                            integer(matcher.group("days")),
                            negative ? -hours : hours, negative ? -minutes : minutes,
                            negative ? -seconds : seconds);
        }
    }

    /**
     * Converts through the text of the {@code postgres_verbose} {@code IntervalStyle}:
     * {@code @ 1 year 2 mons 3 days 4 hours 5 mins 6.5 secs}, {@code @ 1 day -2 hours -5 mins},
     * and {@code @ 1 day 2 hours 5 mins ago}, the {@code ago} negating the whole.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfPostgresVerbose
            extends PGIntervalConverter<String> {

        private static final String STYLE = "postgres_verbose";

        private static final Pattern WHOLE = Pattern.compile("@\\s*(?<body>.+?)(?<ago>\\s+ago)?");

        private static final Pattern PART = Pattern.compile(
                "\\G\\s*(?<value>[+-]?\\d+(?:\\.\\d+)?)\\s+"
                + "(?<unit>years?|mons?|days?|hours?|mins?|secs?)");

        /**
         * Creates a new instance.
         */
        public OfPostgresVerbose() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return {@code attribute} in the {@code postgres_verbose} style, each field with its own
         *         sign, never {@code ago}.
         */
        @Override
        protected String toText(final PGInterval attribute) {
            final var parts = new ArrayList<String>();
            unit(parts, attribute.getYears(), "year", "years");
            unit(parts, attribute.getMonths(), "mon", "mons");
            unit(parts, attribute.getDays(), "day", "days");
            final var micros = micros(attribute);
            final var sign = micros < 0L ? -1 : 1;
            final var abs = Math.abs(micros);
            unit(parts, sign * (int) (abs / MICROS_PER_HOUR), "hour", "hours");
            unit(parts, sign * (int) (abs / MICROS_PER_MINUTE % 60L), "min", "mins");
            final var seconds = abs % MICROS_PER_MINUTE;
            if (seconds != 0L) {
                parts.add((sign < 0 ? "-" : "") + seconds(seconds) + (seconds == MICROS_PER_SECOND
                                                                      ? " sec" : " secs"));
            }
            return parts.isEmpty() ? "@ 0" : "@ " + String.join(" ", parts);
        }

        /**
         * {@inheritDoc}
         *
         * @param text {@inheritDoc} -- the interval in the {@code postgres_verbose} style.
         * @return the interval {@code text} denotes.
         * @throws IllegalArgumentException if {@code text} is not an interval in the
         *                                  {@code postgres_verbose} style.
         */
        @Override
        protected PGInterval fromText(final String text) {
            final var whole = WHOLE.matcher(text.strip());
            if (!whole.matches()) {
                throw notAnInterval(STYLE, text, null);
            }
            final var body = whole.group("body");
            final var negate = whole.group("ago") != null;
            if ("0".equals(body)) {
                return interval(0, 0, 0, 0, 0, 0.0d);
            }
            int years = 0, months = 0, days = 0, hours = 0, minutes = 0;
            double seconds = 0.0d;
            final var part = PART.matcher(body);
            var end = 0;
            try {
                while (part.find()) {
                    final var value = part.group("value");
                    final var unit = part.group("unit");
                    if (unit.startsWith("year")) {
                        years = Integer.parseInt(value);
                    } else if (unit.startsWith("mon")) {
                        months = Integer.parseInt(value);
                    } else if (unit.startsWith("day")) {
                        days = Integer.parseInt(value);
                    } else if (unit.startsWith("hour")) {
                        hours = Integer.parseInt(value);
                    } else if (unit.startsWith("min")) {
                        minutes = Integer.parseInt(value);
                    } else {
                        seconds = Double.parseDouble(value);
                    }
                    end = part.end();
                }
            } catch (final NumberFormatException nfe) {
                // a fraction on a unit other than the seconds
                throw notAnInterval(STYLE, text, nfe);
            }
            if (end == 0 || end != body.length()) {
                throw notAnInterval(STYLE, text, null);
            }
            if (negate) {
                return interval(-years, -months, -days, -hours, -minutes, -seconds);
            }
            return interval(years, months, days, hours, minutes, seconds);
        }
    }

    /**
     * Converts through the text of the {@code sql_standard} {@code IntervalStyle}, which has no
     * unit names, only positions: {@code Y-M D H:MM:SS}, each part left out when the server has no
     * need for it.
     * <p>
     * When every field has the same sign, one sign in front stands for the whole:
     * {@code 1 2:05:00}, {@code -1 2:05:00}, {@code -1-2}. When the signs are mixed, the server
     * writes every field, each with its own sign: {@code +0-0 +1 -2:05:00}.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfSqlStandard
            extends PGIntervalConverter<String> {

        private static final String STYLE = "sql_standard";

        private static final Pattern YEAR_MONTH = Pattern.compile(
                "(?<sign>[+-]?)(?<years>\\d+)-(?<months>\\d+)");

        private static final Pattern DAY = Pattern.compile("(?<sign>[+-]?)(?<days>\\d+)");

        private static final Pattern CLOCK = Pattern.compile(
                "(?<sign>[+-]?)(?<hours>\\d+):(?<minutes>\\d{2}):(?<seconds>\\d{2}(?:\\.\\d+)?)");

        /**
         * Creates a new instance.
         */
        public OfSqlStandard() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return {@code attribute} in the {@code sql_standard} style, every part written, each
         *         with its own sign, {@code +Y-M +D +H:MM:SS}, which no sign in front can misread.
         */
        @Override
        protected String toText(final PGInterval attribute) {
            final var months = attribute.getYears() * 12L + attribute.getMonths();
            final var absMonths = Math.abs(months);
            final var days = attribute.getDays();
            final var micros = micros(attribute);
            return (months < 0L ? "-" : "+") + absMonths / 12L + "-" + absMonths % 12L
                   + " " + (days < 0 ? "-" : "+") + Math.abs((long) days)
                   + " " + (micros < 0L ? "" : "+") + clock(micros, "%d");
        }

        /**
         * {@inheritDoc}
         *
         * @param text {@inheritDoc} -- the interval in the {@code sql_standard} style.
         * @return the interval {@code text} denotes.
         * @throws IllegalArgumentException if {@code text} is not an interval in the
         *                                  {@code sql_standard} style.
         */
        @Override
        protected PGInterval fromText(final String text) {
            final var parts = text.strip().split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty() || parts.length > 3) {
                throw notAnInterval(STYLE, text, null);
            }
            // signs on each part, or one in front for the whole
            var signed = parts[0].startsWith("+");
            for (var i = 1; i < parts.length; i++) {
                signed |= parts[i].startsWith("+") || parts[i].startsWith("-");
            }
            final var whole = !signed && parts[0].startsWith("-") ? -1 : 1;
            int years = 0, months = 0, days = 0, hours = 0, minutes = 0;
            double seconds = 0.0d;
            var position = 0; // 1: year-month, 2: day, 3: clock; each once, in that order
            for (final var part : parts) {
                final var yearMonth = YEAR_MONTH.matcher(part);
                final var day = DAY.matcher(part);
                final var clock = CLOCK.matcher(part);
                if (yearMonth.matches() && position < 1) {
                    final var sign = sign(yearMonth.group("sign"), whole);
                    years = sign * Integer.parseInt(yearMonth.group("years"));
                    months = sign * Integer.parseInt(yearMonth.group("months"));
                    position = 1;
                } else if (day.matches() && position < 2) {
                    days = sign(day.group("sign"), whole) * Integer.parseInt(day.group("days"));
                    position = 2;
                } else if (clock.matches() && position < 3) {
                    final var sign = sign(clock.group("sign"), whole);
                    hours = sign * Integer.parseInt(clock.group("hours"));
                    minutes = sign * Integer.parseInt(clock.group("minutes"));
                    seconds = sign * Double.parseDouble(clock.group("seconds"));
                    position = 3;
                } else {
                    throw notAnInterval(STYLE, text, null);
                }
            }
            return interval(years, months, days, hours, minutes, seconds);
        }

        /**
         * Returns the sign of a part: the whole's, if one sign in front stands for the whole, or
         * its own.
         */
        private static int sign(final String own, final int whole) {
            if (whole == -1) {
                return -1;
            }
            return "-".equals(own) ? -1 : 1;
        }
    }

    /**
     * Converts through the text of the {@code iso_8601} {@code IntervalStyle}:
     * {@code P1Y2M3DT4H5M6.5S}, {@code P1DT-2H-5M}, each field with its own sign.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfIso8601
            extends PGIntervalConverter<String> {

        private static final String STYLE = "iso_8601";

        private static final Pattern PATTERN = Pattern.compile(
                "P(?:(?<years>[+-]?\\d+)Y)?(?:(?<months>[+-]?\\d+)M)?(?:(?<days>[+-]?\\d+)D)?"
                + "(?:T(?:(?<hours>[+-]?\\d+)H)?(?:(?<minutes>[+-]?\\d+)M)?"
                + "(?:(?<seconds>[+-]?\\d+(?:\\.\\d+)?)S)?)?");

        /**
         * Creates a new instance.
         */
        public OfIso8601() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return {@code attribute} in the {@code iso_8601} style, each field with its own sign.
         */
        @Override
        protected String toText(final PGInterval attribute) {
            final var builder = new StringBuilder("P");
            designator(builder, attribute.getYears(), 'Y');
            designator(builder, attribute.getMonths(), 'M');
            designator(builder, attribute.getDays(), 'D');
            final var seconds = attribute.getWholeSeconds() * MICROS_PER_SECOND
                                + attribute.getMicroSeconds();
            if (attribute.getHours() != 0 || attribute.getMinutes() != 0 || seconds != 0L) {
                builder.append('T');
                designator(builder, attribute.getHours(), 'H');
                designator(builder, attribute.getMinutes(), 'M');
                if (seconds != 0L) {
                    builder.append(seconds < 0L ? "-" : "").append(seconds(Math.abs(seconds)))
                            .append('S');
                }
            }
            return builder.length() == 1 ? "PT0S" : builder.toString();
        }

        private static void designator(final StringBuilder builder, final int value,
                                       final char designator) {
            if (value != 0) {
                builder.append(value).append(designator);
            }
        }

        /**
         * {@inheritDoc}
         *
         * @param text {@inheritDoc} -- the interval in the {@code iso_8601} style.
         * @return the interval {@code text} denotes.
         * @throws IllegalArgumentException if {@code text} is not an interval in the
         *                                  {@code iso_8601} style.
         */
        @Override
        protected PGInterval fromText(final String text) {
            final var stripped = text.strip();
            final var matcher = PATTERN.matcher(stripped);
            // "P" and "PT" alone match the pattern, and are no interval
            if (!matcher.matches() || stripped.equals("P") || stripped.endsWith("T")) {
                throw notAnInterval(STYLE, text, null);
            }
            return interval(integer(matcher.group("years")), integer(matcher.group("months")),
                            integer(matcher.group("days")), integer(matcher.group("hours")),
                            integer(matcher.group("minutes")), decimal(matcher.group("seconds")));
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected PGIntervalConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the column's text, or the driver's own {@link PGInterval}.
     * @return {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    @Override
    public PGInterval convertToEntityAttribute(final Y dbData) {
        if (dbData instanceof PGInterval interval) {
            return interval(interval.getYears(), interval.getMonths(), interval.getDays(),
                            interval.getHours(), interval.getMinutes(), interval.getSeconds());
        }
        return super.convertToEntityAttribute(dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static final long MICROS_PER_SECOND = 1_000_000L;

    private static final long MICROS_PER_MINUTE = 60L * MICROS_PER_SECOND;

    private static final long MICROS_PER_HOUR = 60L * MICROS_PER_MINUTE;

    /**
     * Returns the hours, the minutes and the seconds of the specified interval, as microseconds.
     */
    private static long micros(final PGInterval interval) {
        return interval.getHours() * MICROS_PER_HOUR + interval.getMinutes() * MICROS_PER_MINUTE
               + interval.getWholeSeconds() * MICROS_PER_SECOND + interval.getMicroSeconds();
    }

    /**
     * Returns the specified microseconds as a clock, {@code [-]H:MM:SS[.ffffff]}, the hours in the
     * specified format.
     */
    private static String clock(final long micros, final String hoursFormat) {
        final var abs = Math.abs(micros);
        return (micros < 0L ? "-" : "")
               + String.format(hoursFormat + ":%02d:", abs / MICROS_PER_HOUR,
                               abs / MICROS_PER_MINUTE % 60L)
               + (abs % MICROS_PER_MINUTE < 10L * MICROS_PER_SECOND ? "0" : "")
               + seconds(abs % MICROS_PER_MINUTE);
    }

    /**
     * Returns the specified, non-negative, microseconds as seconds, with no trailing zero in the
     * fraction, and no fraction at all for a whole second.
     */
    private static String seconds(final long micros) {
        return BigDecimal.valueOf(micros, 6).stripTrailingZeros().toPlainString();
    }

    /**
     * Adds the specified value, with its unit, to the specified parts, unless it is zero.
     */
    private static void unit(final List<String> parts, final int value, final String one,
                             final String many) {
        if (value != 0) {
            parts.add(value + " " + (Math.abs(value) == 1 ? one : many));
        }
    }

    private static PGInterval interval(final int years, final int months, final int days,
                                       final int hours, final int minutes, final double seconds) {
        return new PGInterval(years, months, days, hours, minutes, seconds);
    }

    private static int integer(final String group) {
        return group == null ? 0 : Integer.parseInt(group);
    }

    private static double decimal(final String group) {
        return group == null ? 0.0d : Double.parseDouble(group);
    }

    private static IllegalArgumentException notAnInterval(final String style, final String text,
                                                          final Throwable cause) {
        return new IllegalArgumentException(
                "not an interval in the " + style + " style: " + text, cause);
    }
}

