-- -
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
-- -
--
-- Exercises at the intermediate level on bookings: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/bookings_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------------------------------- DEMODB-BOOKINGS-I-01
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

-- ----------------------------------------------------------------------------------------------- DEMODB-BOOKINGS-I-02
-- the ten most expensive bookings.
--  book_ref |           book_date           | total_amount 
-- ----------+-------------------------------+--------------
--  7UU4BE   | 2025-09-20 04:54:14.10585+00  |    915250.00
--  3OSUL8   | 2025-10-13 09:23:52.261287+00 |    741100.00
--  613WY4   | 2025-10-14 03:44:02.154647+00 |    717450.00
--  573JWM   | 2025-10-17 22:57:03.874836+00 |    635800.00
--  U44CN0   | 2025-10-02 11:31:37.375482+00 |    624150.00
--  2U34KR   | 2025-09-25 17:59:24.015294+00 |    602200.00
--  DZHSB2   | 2025-09-23 14:13:19.234068+00 |    580900.00
--  YIHL90   | 2025-09-13 21:30:36.249724+00 |    577900.00
--  8C20XV   | 2025-11-04 16:36:10.112231+00 |    564000.00
--  Q2ST5O   | 2025-11-09 23:43:51.389848+00 |    554750.00
-- (10 rows)
SELECT *
FROM bookings
ORDER BY total_amount DESC, book_ref LIMIT 10
;

-- ----------------------------------------------------------------------------------------------- DEMODB-BOOKINGS-I-03
-- the smallest, the average and the largest booking.
--  smallest | average  |  largest  
-- ----------+----------+-----------
--   1750.00 | 19006.93 | 915250.00
-- (1 row)
SELECT MIN(total_amount)           AS smallest,
       ROUND(AVG(total_amount), 2) AS average,
       MAX(total_amount)           AS largest
FROM bookings
;

-- ----------------------------------------------------------------------------------------------- DEMODB-BOOKINGS-I-04
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
