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
-- Exercises at the intermediate level on bookings: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/bookings_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------- DEMODB_BOOKINGS_I_01
-- the bookings and the money of each month.
--   month  | bookings |     total     
-- ---------+----------+---------------
--  2025-09 |   448064 | 8749891200.00
--  2025-10 |   434159 | 8223964900.00
--  2025-11 |   410670 | 7600075475.00
-- (3 rows)
SELECT TO_CHAR(book_date, 'YYYY-MM') AS month, COUNT(*) AS bookings, SUM(total_amount) AS total
FROM bookings
GROUP BY month
ORDER BY month
;

-- ---------------------------------------------------------------------------- DEMODB_BOOKINGS_I_04
-- how many bookings on each day of the week.
--  weekday | bookings 
-- ---------+----------
--  Sun     |   183235
--  Mon     |   185709
--  Tue     |   185390
--  Wed     |   185600
--  Thu     |   184292
--  Fri     |   184620
--  Sat     |   184047
-- (7 rows)
SELECT TO_CHAR(book_date, 'Dy') AS weekday, COUNT(*) AS bookings
FROM bookings
GROUP BY weekday, EXTRACT(DOW FROM book_date)
ORDER BY EXTRACT(DOW FROM book_date)
;

-- ---------------------------------------------------------------------------- DEMODB_BOOKINGS_I_05
-- the median and the 90th percentile of the booking amounts.
--  median |  p90  
-- --------+-------
--   12000 | 38625
-- (1 row)
SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY total_amount) AS median,
       PERCENTILE_CONT(0.9) WITHIN GROUP (ORDER BY total_amount) AS p90
FROM bookings
;
