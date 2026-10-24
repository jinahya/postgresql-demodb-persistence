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
-- Exercises at the advanced level on seats: a subquery, a window, a self join or a recursion. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/seats_exercises_03_advanced.sql
--


-- -------------------------------------------------------------------------------------------------- DEMODB_SEATS_A_01
-- the empty seats of the flight 1.
--  seat_no | fare_conditions 
-- ---------+-----------------
--  10A     | Economy
--  10B     | Economy
--  10C     | Economy
--  10D     | Economy
--  10E     | Economy
--  10F     | Economy
--  11A     | Economy
--  11C     | Economy
--  11D     | Economy
--  11E     | Economy
-- ... 112 rows in all.
SELECT s.seat_no, s.fare_conditions
FROM seats s
         LEFT JOIN boarding_passes bp ON bp.flight_id = 1 AND bp.seat_no = s.seat_no
WHERE s.airplane_code = '7M7'
  AND bp.ticket_no IS NULL
ORDER BY s.seat_no
;

-- -------------------------------------------------------------------------------------------------- DEMODB_SEATS_A_02
-- the share of Business seats of each airplane.
--  airplane_code | business_pct 
-- ---------------+--------------
--  789           |         18.7
--  32N           |         16.9
--  351           |         13.5
--  339           |         10.3
--  7M7           |         10.0
--  CR7           |          8.6
--  E70           |          7.7
--  77W           |          7.4
-- (8 rows)
SELECT airplane_code,
       ROUND(100.0 * COUNT(*) FILTER (WHERE fare_conditions = 'Business') / COUNT(*),
             1) AS business_pct
FROM seats
GROUP BY airplane_code
ORDER BY business_pct DESC, airplane_code
;
