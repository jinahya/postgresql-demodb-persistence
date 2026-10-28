/**
 * Attribute converters for the PostgreSQL column types neither Jakarta Persistence nor Hibernate ORM
 * maps: a {@code point} as a {@link PointAttributeConverter.Point}. A {@code jsonb}, as a JSON-P
 * value, and a time zone's text, as a {@link java.time.ZoneId}, go through converters of
 * {@code jinahya-persistence-more}; an {@code integer[]} and an {@code interval} need none, Hibernate
 * ORM mapping them natively.
 * <p>
 * The converter carries its column as text, both ways, so that it relies on no class of the
 * driver's. The mapped superclasses of {@code ..persistence.mapped} name it with
 * {@link jakarta.persistence.Convert @Convert}; it is not auto-applied.
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
