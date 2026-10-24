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
-- Exercises at the advanced level on bookings: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/bookings_exercises_03_advanced.sql
--


-- ----------------------------------------------------------------------------------------------- DEMODB-BOOKINGS-A-01
-- how many bookings do not add up.
--  bookings 
-- ----------
--         0
-- (1 row)
SELECT COUNT(*) AS bookings
FROM (SELECT b.book_ref
      FROM bookings b
               JOIN tickets t ON t.book_ref = b.book_ref
               JOIN segments s ON s.ticket_no = t.ticket_no
      GROUP BY b.book_ref, b.total_amount
      HAVING b.total_amount <> SUM(s.price)) x
;

-- ----------------------------------------------------------------------------------------------- DEMODB-BOOKINGS-A-02
-- the five bookings with the most tickets.
--  book_ref |           book_date           | total_amount | tickets 
-- ----------+-------------------------------+--------------+---------
--  033CTO   | 2025-09-07 01:15:43.363826+00 |     87500.00 |      10
--  1OP3KI   | 2025-10-14 22:12:29.76249+00  |    114000.00 |      10
--  25XXZL   | 2025-09-08 06:49:23.980094+00 |     72500.00 |      10
--  3WXRTI   | 2025-09-11 03:13:41.930626+00 |     36725.00 |      10
--  49Z3NQ   | 2025-11-03 11:03:13.2549+00   |     86100.00 |      10
-- (5 rows)
SELECT b.book_ref, b.book_date, b.total_amount, COUNT(*) AS tickets
FROM bookings b
         JOIN tickets t ON t.book_ref = b.book_ref
GROUP BY b.book_ref, b.book_date, b.total_amount
ORDER BY tickets DESC, b.book_ref LIMIT 5
;
