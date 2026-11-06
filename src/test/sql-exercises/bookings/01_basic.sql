---
-- #%L
-- postgres-demonstration-persistence
-- %%
-- Copyright (C) 2026 Jinahya, Inc.
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--      http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
--
-- Exercises at the basic level on bookings: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/bookings/01_basic.sql
--


-- ---------------------------------------------------------------------------- DEMODB_BOOKINGS_B_01
-- the bookings of the first minute.
-- NOT YET RUN
SELECT *
FROM bookings
WHERE book_date >= TIMESTAMPTZ '2025-09-01 00:00:00+00'
  AND book_date < TIMESTAMPTZ '2025-09-01 00:01:00+00'
ORDER BY book_date, book_ref
;

-- ---------------------------------------------------------------------------- DEMODB_BOOKINGS_B_02
-- how many bookings, and how much in all.
--  bookings |     total      
-- ----------+----------------
--   1292893 | 24573931575.00
-- (1 row)
SELECT COUNT(*) AS bookings, SUM(total_amount) AS total
FROM bookings
;

-- ---------------------------------------------------------------------------- DEMODB_BOOKINGS_B_03
-- the bookings of 700000 or more.
--  book_ref |           book_date           | total_amount 
-- ----------+-------------------------------+--------------
--  7UU4BE   | 2025-09-20 04:54:14.10585+00  |    915250.00
--  3OSUL8   | 2025-10-13 09:23:52.261287+00 |    741100.00
--  613WY4   | 2025-10-14 03:44:02.154647+00 |    717450.00
-- (3 rows)
SELECT *
FROM bookings
WHERE total_amount >= 700000
ORDER BY total_amount DESC, book_ref
;

-- ---------------------------------------------------------------------------- DEMODB_BOOKINGS_B_04
-- the second page of the bookings of 2025-10-01.
-- NOT YET RUN
SELECT *
FROM bookings
WHERE book_date >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND book_date < TIMESTAMPTZ '2025-10-02 00:00:00+00'
ORDER BY book_date, book_ref
LIMIT 20 OFFSET 20
;
