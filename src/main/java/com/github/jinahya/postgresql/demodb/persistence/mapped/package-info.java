/**
 * Mapped superclasses for the {@code bookings} schema of the PostgreSQL demonstration database,
 * <q>Airlines</q>, one per table and one per view, plus one per composite identifier.
 * <p>
 * Each holds what the schema says about its table -- the names, the types, the lengths, the
 * nullability, the check constraints as validation constraints -- and nothing an application
 * decides: no association, no fetch type, no cascade. An entity which extends one adds those, and
 * names the table with {@link jakarta.persistence.Table @Table}. The persistence provider is the
 * entity's choice as well; the classes here are plain Jakarta Persistence.
 * <p>
 * The column types Jakarta Persistence has none of its own for, an {@code integer[]} and an
 * {@code interval}, are mapped through the converters of {@code ..persistence.converter}, which an
 * attribute names with {@link jakarta.persistence.Convert @Convert}; a persistence unit which
 * excludes unlisted classes has to list them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://postgrespro.com/community/demodb">The demonstration database</a>
 * @see <a href="https://postgrespro.com/docs/postgrespro/current/demodb-bookings">Schema
 *         description</a>
 */
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
