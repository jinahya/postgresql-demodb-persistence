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

import com.github.jinahya.postgresql.demodb.persistence.mapped.MappedRoute;
import com.github.jinahya.postgresql.demodb.persistence.mapped.RouteId;
import lombok.extern.slf4j.Slf4j;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * A class for testing the {@link Route} entity class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class Route_Test
        extends _Entity_Test<Route, RouteId> {

    Route_Test() {
        super(Route.class, RouteId.class);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Tests {@link Route#of(RouteId, AirportData, AirportData)}.
     */
    @DisplayName("of(id, departureAirportData, arrivalAirportData)")
    @Nested
    class OfTest {

        /**
         * Verifies that {@link Route#of(RouteId, AirportData, AirportData) of} returns an instance
         * whose airports are the very instances it was given, whose read-only codes are theirs, and
         * whose airplane is left {@code null}.
         */
        @Test
        void __() {
            final var id = mock(RouteId.class);
            final var departure = mock(AirportData.class);
            doReturn("DME").when(departure).getAirportCode();
            final var arrival = mock(AirportData.class);
            doReturn("LED").when(arrival).getAirportCode();
            final var instance = Route.of(id, departure, arrival);
            assertThat(instance).isNotNull().satisfies(v -> {
                assertThat(v.getId()).isSameAs(id);
                assertThat(v.getDepartureAirportData()).isSameAs(departure);
                assertThat(v.getDepartureAirport()).isEqualTo("DME");
                assertThat(v.getArrivalAirportData()).isSameAs(arrival);
                assertThat(v.getArrivalAirport()).isEqualTo("LED");
                assertThat(v.getAirplaneData()).isNull();
                assertThat(v.getAirplaneCode()).isNull();
            });
        }
    }

    // ----------------------------------------------------------------------------- equals/hashCode

    /**
     * {@inheritDoc}
     *
     * @implNote {@link Route#equals(Object) equals} compares the
     *         {@value MappedRoute#COLUMN_NAME_ROUTE_NO} and
     *         {@value MappedRoute#COLUMN_NAME_VALIDITY} attributes, the pair the table's exclusion
     *         constraint keeps apart, not the {@code
     * @EmbeddedId} which reads the same two columns; every other attribute is mutable
     *         state.
     */
    @Override
    public SingleTypeEqualsVerifierApi<Route> equals_verifier_() {
        return super.equals_verifier_()
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED,
                          Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY,
                          Warning.STRICT_HASHCODE);
    }
}
