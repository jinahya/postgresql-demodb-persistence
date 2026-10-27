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

import jakarta.persistence.MappedSuperclass;

/**
 * A superclass for the identifier of the {@value MappedBoardingPass#TABLE_NAME} table -- the pair
 * of the {@value MappedBoardingPass#COLUMN_NAME_TICKET_NO} and
 * {@value MappedBoardingPass#COLUMN_NAME_FLIGHT_ID} columns.
 * <p>
 * The pair is the table's primary key and, as a whole, a foreign key to the
 * {@value MappedSegment#TABLE_NAME} table's, so this class adds nothing to
 * {@link MappedSegmentId}: the attributes, their names, the factory method and the equality are
 * all its.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedBoardingPass
 * @see MappedSegmentId
 */
@MappedSuperclass
public abstract class MappedBoardingPassId
        extends MappedSegmentId {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedBoardingPassId() {
        super();
    }
}
