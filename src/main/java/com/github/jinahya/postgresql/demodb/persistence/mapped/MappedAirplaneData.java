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

import com.github.jinahya.persistence.more.converter.json.JsonpStringAttributeConverter;
import jakarta.json.JsonObject;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A mapped superclass which holds the mappings of the {@value MappedAirplaneData#TABLE_NAME}
 * table,
 * <q>Airplanes (internal multilingual data)</q>.
 * <p>
 * The {@value MappedAirplaneData#COLUMN_NAME_MODEL} column is {@code jsonb}, an object of one name
 * per language, keyed {@code en}, {@code ru}, ...; it is mapped as a {@link JsonObject}, through a
 * {@link JsonpStringAttributeConverter.OfJsonObject}, with package-private accessors, and is read
 * and written a name at a time, by the language of a {@link Locale}. Writing it back needs the
 * driver to leave its type to the server, {@code stringtype=unspecified} on the JDBC URL. The
 * {@code airplanes} view, see {@link MappedAirplane}, picks the session's language out of it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedAirplaneData {

    /**
     * The name of the database table to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "airplanes_data";

    // ------------------------------------------------------------------------------- airplane_code

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute
     * maps. The value is {@value}.
     */
    public static final String COLUMN_NAME_AIRPLANE_CODE = "airplane_code";

    /**
     * The length of the {@value #COLUMN_NAME_AIRPLANE_CODE} column. The value is {@value}.
     */
    public static final int COLUMN_LENGTH_AIRPLANE_CODE = 3;

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_AIRPLANE_CODE} column. The
     * value is {@value}.
     */
    public static final String ATTRIBUTE_NAME_AIRPLANE_CODE = "airplaneCode";

    /**
     * The minimum size of the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MIN_AIRPLANE_CODE = 0;

    /**
     * The maximum size of the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute. The value is
     * {@value}.
     */
    public static final int SIZE_MAX_AIRPLANE_CODE = COLUMN_LENGTH_AIRPLANE_CODE;

    // --------------------------------------------------------------------------------------- model

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_MODEL} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_MODEL = "model";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_MODEL} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_MODEL = "model";

    // --------------------------------------------------------------------------------------- range

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_RANGE} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_RANGE = "range";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_RANGE} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_RANGE = "range";

    // --------------------------------------------------------------------------------------- speed

    /**
     * The name of the table column to which the {@value #ATTRIBUTE_NAME_SPEED} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_SPEED = "speed";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SPEED} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_SPEED = "speed";

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance of a subclass, with the specified code.
     *
     * @param <T>          the type of the instance.
     * @param instantiator a supplier of a new, empty instance of {@code T}.
     * @param airplaneCode a value for the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     * @return the instance supplied by {@code instantiator}, with its code set.
     * @throws NullPointerException if {@code instantiator} is {@code null}, or it supplies
     *                              {@code null}.
     */
    protected static <T extends MappedAirplaneData> T of(final Supplier<? extends T> instantiator,
                                                         final String airplaneCode) {
        Objects.requireNonNull(instantiator, "instantiator is null");
        final var instance =
                Objects.requireNonNull(instantiator.get(), "instantiator.get() is null");
        instance.setAirplaneCode(airplaneCode);
        return instance;
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedAirplaneData() {
        super();
    }

    // ---------------------------------------------------------------------------- java.lang.Object

    @Override
    public final String toString() {
        return super.toString() + '{' +
               "airplaneCode=" + airplaneCode +
               ",model=" + model +
               ",range=" + range +
               ",speed=" + speed +
               '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param obj {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Equality is by the {@link Id @Id} attribute,
     *         {@value #ATTRIBUTE_NAME_AIRPLANE_CODE}, alone.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof MappedAirplaneData that)) {
            return false;
        }
        return Objects.equals(getAirplaneCode(), that.getAirplaneCode());
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec The hash is over the {@link Id @Id} attribute, consistent with
     *         {@link #equals(Object)}.
     */
    @Override
    public final int hashCode() {
        return Objects.hashCode(getAirplaneCode());
    }

    // -------------------------------------------------------------------------------- airplaneCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     */
    public String getAirplaneCode() {
        return airplaneCode;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute with the specified
     * value.
     *
     * @param airplaneCode new value for {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     */
    protected void setAirplaneCode(final String airplaneCode) {
        this.airplaneCode = airplaneCode;
    }

    // --------------------------------------------------------------------------------------- model

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_MODEL} attribute -- a {@code jsonb} column,
     * mapped as the names it holds, keyed by language.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_MODEL} attribute.
     * @see #getModelName(Locale)
     */
    JsonObject getModel() {
        return model;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_MODEL} attribute with the specified value.
     *
     * @param model new value for {@value #ATTRIBUTE_NAME_MODEL} attribute.
     * @see #setModelName(Locale, String)
     */
    void setModel(final JsonObject model) {
        this.model = model;
    }

    /**
     * Returns the name of the model in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the model.
     * @return the name of the model in the language of {@code locale}; {@code null} if there is
     *         none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     */
    public String getModelName(final Locale locale) {
        return _LocaleNames.get(getModel(), locale);
    }

    /**
     * Replaces the name of the model in the language of the specified locale with the specified
     * value.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the model.
     * @param name   the new name of the model in the language of {@code locale}.
     * @throws NullPointerException     if {@code locale} or {@code name} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language, or {@code name} is
     *                                  blank.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute.
     * @see #removeModelName(Locale)
     */
    public void setModelName(final Locale locale, final String name) {
        setModel(_LocaleNames.with(getModel(), locale, name));
    }

    /**
     * Removes the name of the model in the language of the specified locale.
     *
     * @param locale the locale whose {@link Locale#getLanguage() language} names the model.
     * @return the name removed; {@code null} if there was none.
     * @throws NullPointerException     if {@code locale} is {@code null}.
     * @throws IllegalArgumentException if {@code locale} has no language.
     * @implNote The names are replaced with a new object, not changed in place, so that the
     *         change is seen as a change of the attribute; nothing is replaced when there was no
     *         name.
     */
    public String removeModelName(final Locale locale) {
        final var removed = getModelName(locale);
        if (removed != null) {
            setModel(_LocaleNames.without(getModel(), locale));
        }
        return removed;
    }

    // --------------------------------------------------------------------------------------- range

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RANGE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RANGE} attribute.
     */
    public Integer getRange() {
        return range;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_RANGE} attribute with the specified value.
     *
     * @param range new value for {@value #ATTRIBUTE_NAME_RANGE} attribute.
     */
    public void setRange(final Integer range) {
        this.range = range;
    }

    // --------------------------------------------------------------------------------------- speed

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SPEED} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SPEED} attribute.
     */
    public Integer getSpeed() {
        return speed;
    }

    /**
     * Replaces current value of {@value #ATTRIBUTE_NAME_SPEED} attribute with the specified value.
     *
     * @param speed new value for {@value #ATTRIBUTE_NAME_SPEED} attribute.
     */
    public void setSpeed(final Integer speed) {
        this.speed = speed;
    }

    // ---------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_AIRPLANE_CODE, max = SIZE_MAX_AIRPLANE_CODE)
    @NotNull
    @Id
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_AIRPLANE_CODE,
            nullable = false,
            insertable = true,
            updatable = false,
            length = COLUMN_LENGTH_AIRPLANE_CODE
    )
    private String airplaneCode;

    // ---------------------------------------------------------------------------------------------
    @NotNull
    @Convert(converter = JsonpStringAttributeConverter.OfJsonObject.class)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_MODEL, nullable = false, insertable = true, updatable = true)
    private JsonObject model;

    // ---------------------------------------------------------------------------------------------
    @Positive
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_RANGE, nullable = false, insertable = true, updatable = true)
    private Integer range;

    // ---------------------------------------------------------------------------------------------
    @Positive
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SPEED, nullable = false, insertable = true, updatable = true)
    private Integer speed;

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns how long this airplane flies at its cruising speed until its range runs out; the
     * {@value #ATTRIBUTE_NAME_RANGE} divided by the {@value #ATTRIBUTE_NAME_SPEED}.
     *
     * @return the time, in whole seconds, flown at the cruising speed; {@code null} when either the
     *         {@value #ATTRIBUTE_NAME_RANGE} or the {@value #ATTRIBUTE_NAME_SPEED} is
     *         {@code null}.
     */
    @Transient
    public Duration getEndurance() {
        if (range == null || speed == null) {
            return null;
        }
        return Duration.ofSeconds(range * 3_600L / speed);
    }
}
