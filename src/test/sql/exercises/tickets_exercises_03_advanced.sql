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
-- Exercises at the advanced level on tickets: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/tickets_exercises_03_advanced.sql
--


-- ----------------------------------------------------------------------------- DEMODB_TICKETS_A_01
-- how many passengers booked a return.
--  returns 
-- ---------
--  1168851
-- (1 row)
SELECT COUNT(*) AS returns
FROM tickets o
         JOIN tickets r
              ON r.book_ref = o.book_ref AND r.passenger_id = o.passenger_id AND NOT r.outbound
WHERE o.outbound
;

-- ----------------------------------------------------------------------------- DEMODB_TICKETS_A_02
-- the share of return tickets, by booking month.
--   month  | return_pct 
-- ---------+------------
--  2025-09 |       41.4
--  2025-10 |       39.2
--  2025-11 |       37.0
-- (3 rows)
SELECT TO_CHAR(b.book_date, 'YYYY-MM')                                     AS month,
       ROUND(100.0 * COUNT(*) FILTER (WHERE NOT t.outbound) / COUNT(*), 1) AS return_pct
FROM tickets t
         JOIN bookings b
              ON b.book_ref = t.book_ref
GROUP BY month
ORDER BY month
;
