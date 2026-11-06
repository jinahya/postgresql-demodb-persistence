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
--  book_ref |           book_date           | total_amount 
-- ----------+-------------------------------+--------------
--  6TAS39   | 2025-09-01 00:00:06.265219+00 |      5000.00
--  GGY5EU   | 2025-09-01 00:00:07.565193+00 |      5750.00
--  Y4KNLB   | 2025-09-01 00:00:07.980879+00 |     18000.00
--  MJUZ2D   | 2025-09-01 00:00:10.850077+00 |      5000.00
--  4O2NMP   | 2025-09-01 00:00:12.040076+00 |     27500.00
--  2EW1SQ   | 2025-09-01 00:00:12.557744+00 |      8125.00
--  KP3OIQ   | 2025-09-01 00:00:15.012997+00 |     15000.00
--  H40481   | 2025-09-01 00:00:17.28616+00  |     29000.00
--  KOS1KJ   | 2025-09-01 00:00:18.970418+00 |     21500.00
--  NWE9DB   | 2025-09-01 00:00:20.274286+00 |     41250.00
-- ... 17 rows in all.
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
--  book_ref |           book_date           | total_amount 
-- ----------+-------------------------------+--------------
--  0GLNZI   | 2025-10-01 00:01:30.918125+00 |     12075.00
--  M9ZJIV   | 2025-10-01 00:01:34.386935+00 |      9750.00
--  WU45PM   | 2025-10-01 00:01:35.027927+00 |      2500.00
--  2L26GJ   | 2025-10-01 00:01:36.898209+00 |     12250.00
--  Z5FA7T   | 2025-10-01 00:01:44.870713+00 |     33500.00
--  04UWDM   | 2025-10-01 00:01:48.064918+00 |     47750.00
--  5SODYM   | 2025-10-01 00:01:52.593366+00 |     44000.00
--  6C54DM   | 2025-10-01 00:02:09.186487+00 |     46850.00
--  7VE52V   | 2025-10-01 00:02:10.809042+00 |      1750.00
--  I32M88   | 2025-10-01 00:02:17.050868+00 |     30500.00
-- ... 20 rows in all.
SELECT *
FROM bookings
WHERE book_date >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND book_date < TIMESTAMPTZ '2025-10-02 00:00:00+00'
ORDER BY book_date, book_ref
LIMIT 20 OFFSET 20
;
