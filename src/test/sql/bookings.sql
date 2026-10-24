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
-- Profiling queries on bookings, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/bookings.sql
--


-- ------------------------------------------------------------------------------------------------------------ book_ref
-- how many rows, and the smallest and the largest book_ref; 1292893 bookings, 0000EG to ZZZZP8.
SELECT COUNT(*), MIN(book_ref), MAX(book_ref)
FROM bookings
;

-- a book_ref which is not six upper-case letters or digits; the column is character(6).
-- no rows: every reference is six of them.
SELECT book_ref
FROM bookings
WHERE book_ref !~ '^[A-Z0-9]{6}$'
;


-- ----------------------------------------------------------------------------------------------------------- book_date
-- the earliest and the latest book_date; 2025-09-01 to 2025-11-30, the three months before the database's today.
SELECT MIN(book_date), MAX(book_date)
FROM bookings
;

-- a booking made after the database's today, bookings.now().
-- no rows: every booking is in the past.
SELECT book_ref, book_date
FROM bookings
WHERE book_date > bookings.now()
;


-- -------------------------------------------------------------------------------------------------------- total_amount
-- the smallest, the largest and the average total_amount; 1750.00 to 915250.00, 19006.93 on average.
SELECT MIN(total_amount), MAX(total_amount), ROUND(AVG(total_amount), 2)
FROM bookings
;

-- a total_amount which is not a positive whole number of currency units.
-- no rows: every amount is positive, with a zero fraction.
SELECT book_ref, total_amount
FROM bookings
WHERE total_amount <= 0
   OR total_amount <> ROUND(total_amount, 0)
;

-- a booking whose total_amount is not the sum of the prices of its tickets' segments.
-- no rows: every total is its segments' prices.
SELECT b.book_ref, b.total_amount, SUM(s.price) AS prices
FROM bookings b
         JOIN tickets t ON t.book_ref = b.book_ref
         JOIN segments s ON s.ticket_no = t.ticket_no
GROUP BY b.book_ref, b.total_amount
HAVING b.total_amount <> SUM(s.price)
;


-- ---------------------------------------------------------------------------------------------------------------------
-- a booking with no ticket.
-- no rows: every booking has at least one ticket.
SELECT b.book_ref
FROM bookings b
WHERE NOT EXISTS (SELECT 1
                  FROM tickets t
                  WHERE t.book_ref = b.book_ref)
;

-- the fewest and the most tickets on a booking; 1 to 10.
SELECT MIN(tickets), MAX(tickets)
FROM (SELECT COUNT(*) AS tickets
      FROM tickets
      GROUP BY book_ref) x
;
