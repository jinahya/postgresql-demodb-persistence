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

/**
 * A point on a plane, the value of a PostgreSQL {@code point} column, written {@code (x,y)}.
 * <p>
 * A {@code point} holds two double precision numbers and nothing else; what they mean is the
 * column's to say. The {@code airports_data.coordinates} column, for one, keeps the longitude as
 * {@code x} and the latitude as {@code y}.
 *
 * @param x the first coordinate.
 * @param y the second coordinate.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PointConverter
 */
public record Point(double x, double y) {

}

