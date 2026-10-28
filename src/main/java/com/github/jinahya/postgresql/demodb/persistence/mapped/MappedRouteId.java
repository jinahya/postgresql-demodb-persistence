package com.github.jinahya.postgresql.demodb.persistence.mapped;

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
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * A superclass for an identifier of the {@value MappedRoute#TABLE_NAME} table -- the pair of the
 * {@value MappedRoute#COLUMN_NAME_ROUTE_NO} and {@value MappedRoute#COLUMN_NAME_VALIDITY} columns.
 * <p>
 * The table has no primary key; the pair is what its {@code routes_route_no_validity_excl}
 * exclusion constraint keeps apart, so it is the identifier an extending entity can take. The
 * attributes are written ({@code insertable = true}) and never updated, since a provider writes an
 * identifier's columns whatever their {@code insertable} says.
 * <p>
 * The {@value MappedRoute#COLUMN_NAME_VALIDITY} attribute is converted by
 * {@link ValidityConverter}, the one mapping of the column. The specification does not have
 * {@link Convert @Convert} on an identifier's attribute, so an identifier of this class is not
 * portable; Hibernate ORM, the only provider, applies it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedRoute
 */
@MappedSuperclass
public abstract class MappedRouteId {

    /**
     * Converts between the {@value #ATTRIBUTE_NAME_VALIDITY} attribute, an array of two
     * {@link Instant}s, {@code [lower, upper]}, and the {@value MappedRoute#COLUMN_NAME_VALIDITY}
     * column, a {@code tstzrange} of the shape {@code [lower,upper)}, through its text.
     * <p>
     * Only that shape is converted: both bounds present, the lower inclusive and the upper
     * exclusive, the shape of every period the database holds. A range of any other shape --
     * {@code empty}, an unbounded or an {@code -infinity} or {@code infinity} bound, an exclusive
     * lower or an inclusive upper -- is rejected, not read as a different range.
     * <p>
     * A bound is read as an instant, whatever offset the server prints it with, which is the
     * session's time zone; so the attribute does not depend on the time zone the JVM, or the
     * session, runs in.
     * <p>
     * Hibernate ORM reads the column for this converter as its text, and binds the text this
     * converter writes as a {@link String}, which the server casts for the
     * {@code stringtype=unspecified} on the JDBC URL.
     * <p>
     * The converter is not {@link Converter#autoApply() auto-applied}; an attribute names it with
     * {@link jakarta.persistence.Convert @Convert}.
     */
    @Converter(autoApply = false)
    public static class ValidityConverter
            implements AttributeConverter<Instant[], String> {

        // ["lower","upper"), as the server prints a bounded range; the bounds quoted, for the space
        private static final Pattern PATTERN = Pattern.compile("\\[\"([^\"]+)\",\"([^\"]+)\"\\)");

        // 2025-10-01 00:00:00+00, as the server prints a timestamptz; the fraction and the offset's
        // minutes and seconds only when non-zero
        private static final DateTimeFormatter BOUND = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .appendOffset("+HH:mm:ss", "+00")
                .toFormatter();

        /**
         * Creates a new instance.
         */
        public ValidityConverter() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc} -- the lower bound and the upper bound.
         * @return {@code attribute} as {@code ["lower","upper")}, each bound in UTC, as the server
         *         prints it, {@code 2025-10-01 00:00:00+00}; or {@code null} if {@code attribute}
         *         is {@code null}.
         * @throws IllegalArgumentException if {@code attribute} is not two bounds.
         * @implNote The text is the one {@link #convertToEntityAttribute(String)} reads, as
         *         it has to be: Hibernate ORM copies an attribute it deems mutable, an array,
         *         through both methods.
         */
        @Override
        public String convertToDatabaseColumn(final Instant[] attribute) {
            if (attribute == null) {
                return null;
            }
            if (attribute.length != 2 || attribute[0] == null || attribute[1] == null) {
                throw new IllegalArgumentException("not two bounds: " + Arrays.toString(attribute));
            }
            return "[\""
                   + BOUND.format(attribute[0].atOffset(ZoneOffset.UTC))
                   + "\",\""
                   + BOUND.format(attribute[1].atOffset(ZoneOffset.UTC))
                   + "\")";
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc} -- the column's text, {@code ["lower","upper")}.
         * @return the lower bound and the upper bound {@code dbData} denotes, or {@code null} if
         *         {@code dbData} is {@code null}.
         * @throws IllegalArgumentException if {@code dbData} is not a range of the shape
         *                                  {@code [lower,upper)}.
         */
        @Override
        public Instant[] convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            final var matcher = PATTERN.matcher(dbData);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("not a range of [lower,upper): " + dbData);
            }
            try {
                return new Instant[]{
                        OffsetDateTime.parse(matcher.group(1), BOUND).toInstant(),
                        OffsetDateTime.parse(matcher.group(2), BOUND).toInstant()
                };
            } catch (final DateTimeParseException dtpe) {
                throw new IllegalArgumentException("not a range of [lower,upper): " + dbData, dtpe);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The name of the attribute which maps the {@value MappedRoute#COLUMN_NAME_ROUTE_NO} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_ROUTE_NO = "routeNo";

    // ---------------------------------------------------------------------------------------------

    /**
     * The name of the attribute which maps the {@value MappedRoute#COLUMN_NAME_VALIDITY} column.
     * The value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_VALIDITY = "validity";

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of a subclass, with the specified values. The instance is not modified
     * afterwards: an identifier has no setter.
     *
     * @param <T>           the type of the identifier.
     * @param instantiator  a supplier of a new, empty instance of {@code T}.
     * @param routeNo       a value for the {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute.
     * @param fromInclusive the lower bound of the {@value #ATTRIBUTE_NAME_VALIDITY} attribute,
     *                      inclusive.
     * @param toExclusive   the upper bound of the {@value #ATTRIBUTE_NAME_VALIDITY} attribute,
     *                      exclusive.
     * @return the instance supplied by {@code instantiator}, with its attributes set.
     * @throws NullPointerException     if {@code instantiator} is {@code null}, or it supplies
     *                                  {@code null}; or if either bound is {@code null}.
     * @throws IllegalArgumentException if {@code fromInclusive} is not before {@code toExclusive}.
     */
    protected static <T extends MappedRouteId> T of(final Supplier<? extends T> instantiator,
                                                    final String routeNo,
                                                    final Instant fromInclusive,
                                                    final Instant toExclusive) {
        Objects.requireNonNull(instantiator, "instantiator is null");
        final var validity = validity(fromInclusive, toExclusive);
        final T instance =
                Objects.requireNonNull(instantiator.get(), "instantiator.get() is null");
        // a private field is not accessed through a type variable
        final MappedRouteId id = instance;
        id.routeNo = routeNo;
        id.validity = validity;
        return instance;
    }

    /**
     * Returns a new value of the {@value #ATTRIBUTE_NAME_VALIDITY} attribute, of the specified
     * bounds.
     *
     * @param fromInclusive the lower bound, inclusive.
     * @param toExclusive   the upper bound, exclusive.
     * @return a new array of {@code fromInclusive} and {@code toExclusive}.
     * @throws NullPointerException     if either bound is {@code null}.
     * @throws IllegalArgumentException if {@code fromInclusive} is not before {@code toExclusive};
     *                                  the server would store the range as {@code empty}, which is
     *                                  not read back.
     */
    private static Instant[] validity(final Instant fromInclusive, final Instant toExclusive) {
        Objects.requireNonNull(fromInclusive, "fromInclusive is null");
        Objects.requireNonNull(toExclusive, "toExclusive is null");
        if (!fromInclusive.isBefore(toExclusive)) {
            throw new IllegalArgumentException(
                    "fromInclusive(" + fromInclusive + ") is not before toExclusive(" + toExclusive
                    + ")");
        }
        return new Instant[] {fromInclusive, toExclusive};
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedRouteId() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "routeNo=" + routeNo +
               ",validity=" + Arrays.toString(validity) +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by {@value #ATTRIBUTE_NAME_ROUTE_NO} and
     *         {@value #ATTRIBUTE_NAME_VALIDITY}, the latter's bounds compared as instants.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedRouteId that)) {
            return false;
        }
        return Objects.equals(getRouteNo(), that.getRouteNo())
               && Arrays.equals(validity, that.validity);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over {@value #ATTRIBUTE_NAME_ROUTE_NO} and
     *         {@value #ATTRIBUTE_NAME_VALIDITY}, consistent with {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getRouteNo(), Arrays.hashCode(validity));
    }

    // ------------------------------------------------------------------------------------- routeNo

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_ROUTE_NO} attribute.
     */
    public String getRouteNo() {
        return routeNo;
    }

    // ------------------------------------------------------------------------------------ validity

    /**
     * Applies the specified function to the bounds of the {@value #ATTRIBUTE_NAME_VALIDITY}
     * attribute, and returns the result.
     *
     * @param <R>      the type of the result.
     * @param function a function of the lower bound, inclusive, and the upper bound, exclusive.
     * @return the result of {@code function}; or {@code null} if the attribute is {@code null}.
     */
    public <R> R getValidityRange(
            final BiFunction<? super Instant, ? super Instant, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        if (validity == null) {
            return null;
        }
        return function.apply(validity[0], validity[1]);
    }

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Basic(optional = false)
    @Column(name = MappedRoute.COLUMN_NAME_ROUTE_NO, nullable = false, insertable = true,
            updatable = false)
    private String routeNo;

    // ---------------------------------------------------------------------------------------------
    @Size(min = 2, max = 2)
    @NotNull
    @Basic(optional = false)
    @Convert(converter = ValidityConverter.class)
    @Column(name = MappedRoute.COLUMN_NAME_VALIDITY, nullable = false, insertable = true,
            updatable = false, columnDefinition = "tstzrange")
    private Instant[] validity;
}
