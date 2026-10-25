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
import jakarta.persistence.Converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Converts between a {@link PostgresInterval} and a PostgreSQL {@code interval} column, through the
 * text of the {@code postgres} {@code IntervalStyle}, with no class of the driver's in sight:
 * {@code 1 year 2 mons 3 days 04:05:06.5}, {@code -1 days -02:05:00}.
 * <p>
 * The server prints an interval in the session's {@code IntervalStyle}, so the JDBC URL pins this
 * one, {@code options=-c IntervalStyle=postgres}. The converter writes the interval in the same
 * style, so that what it writes, it reads back, whether the server prints the column or another
 * database keeps the text as it is. From the database, a provider which hands the driver's own
 * object over, as EclipseLink does, is read through that object's getters, {@code getYears()} to
 * {@code getSeconds()}, looked up by reflection, so that the driver is needed at run time only; the
 * text was read by the driver then, not by the converter, and the driver reads {@code infinity} and
 * {@code -infinity} as no interval at all, {@code 0 secs}.
 * <p>
 * Why the database side is a type variable, and {@link OfString} the converter to name: the
 * providers need to see {@link String} there, and the methods need to take any object.
 * <ul>
 * <li>Declared as {@code String}, the methods take a {@code String}, and the bridge method the
 * compiler adds casts to it; EclipseLink, which hands over the driver's object whatever the
 * database side says, fails on that cast before the method has a say.</li>
 * <li>Declared as {@code Object}, reads work, but no provider can write: Hibernate has no JDBC
 * type to bind, and EclipseLink guesses one from the attribute.</li>
 * <li>Declared as {@code Y}, the methods take an {@code Object} once erased, so there is no cast;
 * and {@link OfString}, which binds {@code Y} to {@code String} without declaring a method, is
 * where the providers read {@code String} from, and bind the text as such.</li>
 * </ul>
 *
 * @param <Y> the type of the database side; {@link String} in {@link OfString}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class IntervalPostgresAttributeConverter<Y>
        implements AttributeConverter<IntervalPostgresAttributeConverter.PostgresInterval, Y> {

    /**
     * A PostgreSQL {@code interval}, as the server keeps it: months, days and microseconds, three
     * fields none of which converts exactly into another -- a month has no fixed number of days,
     * nor a day, across a daylight saving change, a fixed number of hours.
     * <p>
     * The fields are not normalized into one another, so {@code 1 mon}, {@code 30 days} and
     * {@code 720:00:00} are three different values here, as they are three different values in
     * the column.
     *
     * @param months       the months, years included, twelve to a year.
     * @param days         the days.
     * @param microseconds the time, hours, minutes and seconds, in microseconds.
     */
    public record PostgresInterval(int months, int days, long microseconds) {

        /**
         * The interval of no length, {@code 00:00:00}.
         */
        public static final PostgresInterval ZERO = new PostgresInterval(0, 0, 0L);
    }

    /**
     * Converts through the text, bound as a {@link String}; the converter an attribute names. It
     * only binds the database side to {@code String}, and declares no method, which would bring
     * the cast back; see the enclosing class.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class OfString
            extends IntervalPostgresAttributeConverter<String> {

        /**
         * Creates a new instance.
         */
        public OfString() {
            super();
        }
    }

    // [N year[s]] [N mon[s]] [N day[s]] [-]HH:MM:SS[.ffffff], each part left out when the server has
    // no need for it
    private static final Pattern PATTERN = Pattern.compile(
            "(?:(?<years>[+-]?\\d+) years?)?\\s*"
            + "(?:(?<months>[+-]?\\d+) mons?)?\\s*"
            + "(?:(?<days>[+-]?\\d+) days?)?\\s*"
            + "(?:(?<sign>[+-]?)(?<hours>\\d+):(?<minutes>\\d{2}):"
            + "(?<seconds>\\d{2}(?:\\.\\d+)?))?");

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected IntervalPostgresAttributeConverter() {
        super();
    }

    // -------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return {@code attribute} in the {@code postgres} style, a {@link String}, or {@code null} if
     *         {@code attribute} is {@code null}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Y convertToDatabaseColumn(final PostgresInterval attribute) {
        if (attribute == null) {
            return null;
        }
        final var parts = new ArrayList<String>();
        unit(parts, attribute.months() / 12, "year", "years");
        unit(parts, attribute.months() % 12, "mon", "mons");
        unit(parts, attribute.days(), "day", "days");
        final var micros = attribute.microseconds();
        if (micros != 0L || parts.isEmpty()) {
            parts.add(clock(micros));
        }
        return (Y) String.join(" ", parts);
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc} -- the column's text in the {@code postgres} style, or the
     *               driver's own {@code org.postgresql.util.PGInterval}.
     * @return the interval {@code dbData} denotes, or {@code null} if {@code dbData} is
     *         {@code null}.
     * @throws IllegalArgumentException if {@code dbData} is text, and not an interval in the
     *                                  {@code postgres} style.
     */
    @Override
    public PostgresInterval convertToEntityAttribute(final Y dbData) {
        if (dbData == null) {
            return null;
        }
        if (DRIVER_INTERVAL.equals(dbData.getClass().getName())) {
            final var getters = GETTERS.get(dbData.getClass());
            try {
                return interval((int) getters[0].invoke(dbData), (int) getters[1].invoke(dbData),
                                (int) getters[2].invoke(dbData), (int) getters[3].invoke(dbData),
                                (int) getters[4].invoke(dbData), (double) getters[5].invoke(dbData));
            } catch (final IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("failed to read " + dbData, e);
            }
        }
        final var text = dbData.toString();
        final var matcher = PATTERN.matcher(text.strip());
        if (text.isBlank() || !matcher.matches()) {
            throw new IllegalArgumentException("not an interval in the postgres style: " + text);
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

    // ---------------------------------------------------------------------------------------------

    /**
     * The name of the driver's own interval class, which EclipseLink hands a converter.
     */
    private static final String DRIVER_INTERVAL = "org.postgresql.util.PGInterval";

    /**
     * The getters of the driver's interval class, looked up once for each class: the years, the
     * months, the days, the hours and the minutes, as {@code int}s, and the seconds, as a
     * {@code double}.
     */
    private static final ClassValue<Method[]> GETTERS = new ClassValue<>() {
        @Override
        protected Method[] computeValue(final Class<?> type) {
            try {
                return new Method[] {
                        type.getMethod("getYears"),
                        type.getMethod("getMonths"),
                        type.getMethod("getDays"),
                        type.getMethod("getHours"),
                        type.getMethod("getMinutes"),
                        type.getMethod("getSeconds")
                };
            } catch (final NoSuchMethodException nsme) {
                throw new IllegalStateException("no interval getters on " + type, nsme);
            }
        }
    };

    private static final long MICROS_PER_SECOND = 1_000_000L;

    private static final long MICROS_PER_MINUTE = 60L * MICROS_PER_SECOND;

    private static final long MICROS_PER_HOUR = 60L * MICROS_PER_MINUTE;

    /**
     * Returns the specified microseconds as a clock, {@code [-]HH:MM:SS[.ffffff]}.
     */
    private static String clock(final long micros) {
        final var abs = Math.abs(micros);
        return (micros < 0L ? "-" : "")
               + String.format("%02d:%02d:", abs / MICROS_PER_HOUR, abs / MICROS_PER_MINUTE % 60L)
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

    /**
     * Returns an interval of the specified fields, the years folded into the months and the hours,
     * the minutes and the seconds into the microseconds, as the server keeps them.
     */
    private static PostgresInterval interval(final int years, final int months, final int days,
                                     final int hours, final int minutes, final double seconds) {
        return new PostgresInterval(Math.toIntExact(years * 12L + months), days,
                            hours * MICROS_PER_HOUR + minutes * MICROS_PER_MINUTE
                            + Math.round(seconds * MICROS_PER_SECOND));
    }

    private static int integer(final String group) {
        return group == null ? 0 : Integer.parseInt(group);
    }

    private static double decimal(final String group) {
        return group == null ? 0.0d : Double.parseDouble(group);
    }
}

