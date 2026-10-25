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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * A mapped superclass which holds the mappings of the {@value MappedAirplane#TABLE_NAME} view,
 * <q>Airplanes</q>.
 * <p>
 * Every column is mapped read-only; only the identifier stays insertable, which EclipseLink
 * requires of an {@link Id @Id}. It has no setters; nothing writes to a view.
 * <p>
 * The view reads {@code airplanes_data}, see {@link MappedAirplaneData}, with the
 * {@value #COLUMN_NAME_MODEL} taken out of its {@code jsonb} for the session's language, the
 * {@code bookings.lang} setting ({@code en} by default).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class MappedAirplane {

    /**
     * The name of the database view to which this class maps. The value is {@value}.
     */
    public static final String TABLE_NAME = "airplanes";

    // ------------------------------------------------------------------------------- airplane_code

    /**
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute
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
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_MODEL} attribute maps. The
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
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_RANGE} attribute maps. The
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
     * The name of the view column to which the {@value #ATTRIBUTE_NAME_SPEED} attribute maps. The
     * value is {@value}.
     */
    public static final String COLUMN_NAME_SPEED = "speed";

    /**
     * The name of the attribute which maps the {@value #COLUMN_NAME_SPEED} column. The value is
     * {@value}.
     */
    public static final String ATTRIBUTE_NAME_SPEED = "speed";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedAirplane() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

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
        if (!(obj instanceof MappedAirplane that)) {
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

    // ---------------------------------------------------------------------------------------------------- airplaneCode

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_AIRPLANE_CODE} attribute.
     */
    public String getAirplaneCode() {
        return airplaneCode;
    }

    // ----------------------------------------------------------------------------------------------------------- model

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_MODEL} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_MODEL} attribute.
     */
    public String getModel() {
        return model;
    }

    // ----------------------------------------------------------------------------------------------------------- range

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_RANGE} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_RANGE} attribute.
     */
    public Integer getRange() {
        return range;
    }

    // ----------------------------------------------------------------------------------------------------------- speed

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_SPEED} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_SPEED} attribute.
     */
    public Integer getSpeed() {
        return speed;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Size(min = SIZE_MIN_AIRPLANE_CODE, max = SIZE_MAX_AIRPLANE_CODE)
    @Id
    @Basic(optional = true)
    // insertable=false on an @Id -- "There should be one non-read-only mapping defined for the
    // primary key field", EclipseLink-46. Nothing writes to a view anyway.
    @Column(name = COLUMN_NAME_AIRPLANE_CODE,
            nullable = true,
            insertable = true,
            updatable = false,
            length = COLUMN_LENGTH_AIRPLANE_CODE
    )
    private String airplaneCode;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_MODEL, nullable = true, insertable = false, updatable = false)
    private String model;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_RANGE, nullable = true, insertable = false, updatable = false)
    private Integer range;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_SPEED, nullable = true, insertable = false, updatable = false)
    private Integer speed;
}
