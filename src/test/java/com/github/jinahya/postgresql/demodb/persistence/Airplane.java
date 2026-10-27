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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedAirplane;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * An entity which maps the {@value MappedAirplane#TABLE_NAME} view, extending
 * {@link MappedAirplane} with what is an application's to decide: a read-only association to the
 * {@link AirplaneData} row the view reads.
 * <p>
 * A view has no foreign key; the association rests on the view's definition, which reads every
 * row of {@code airplanes_data} by the same {@value MappedAirplane#COLUMN_NAME_AIRPLANE_CODE}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = MappedAirplane.TABLE_NAME)
public class Airplane
        extends MappedAirplane {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected Airplane() {
        super();
    }

    /**
     * Returns the airplane data this view row reads, over the
     * {@value MappedAirplane#COLUMN_NAME_AIRPLANE_CODE} column, which is the identifier; the
     * association is read-only.
     *
     * @return the airplaneData.
     */
    public AirplaneData getAirplaneData() {
        return airplaneData;
    }

    // ---------------------------------------------------------------------------------------------
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_NAME_AIRPLANE_CODE, nullable = false, insertable = false,
                updatable = false)
    private AirplaneData airplaneData;
}
