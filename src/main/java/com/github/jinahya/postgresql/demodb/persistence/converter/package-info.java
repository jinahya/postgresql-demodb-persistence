/**
 * Attribute converters for the PostgreSQL column types Jakarta Persistence has none of its own for:
 * an {@code integer[]} as a {@link java.util.List} of {@link java.lang.Integer}s, an
 * {@code interval} as a {@link java.time.Duration}.
 * <p>
 * Each carries its column as text towards the database and accepts, from the database, whatever the
 * provider hands over -- the text, or the driver's own object, since Hibernate and EclipseLink
 * differ there -- so that no provider feature is relied on. The mapped superclasses of
 * {@code ..persistence.mapped} name them with {@link jakarta.persistence.Convert @Convert}; none is
 * auto-applied. A persistence unit which excludes unlisted classes has to list them, as EclipseLink
 * requires.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
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
