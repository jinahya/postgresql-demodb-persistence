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
-- Exercises at the advanced level on flights: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/flights_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------ DEMODB_FLIGHTS_A_01
-- the five longest flights flown.
--  flight_id | route_no | departure_airport | arrival_airport |      flown      
-- -----------+----------+-------------------+-----------------+-----------------
--       2721 | PG0130   | BOM               | SCL             | 20:24:04.083773
--       3646 | PG0129   | SCL               | BOM             | 20:04:01.490994
--       5244 | PG0130   | BOM               | SCL             | 19:59:43.766426
--       1122 | PG0129   | SCL               | BOM             | 19:59:40.677147
--        196 | PG0130   | BOM               | SCL             | 19:59:14.949474
-- (5 rows)
SELECT f.flight_id,
       f.route_no,
       r.departure_airport,
       r.arrival_airport,
       f.actual_arrival - f.actual_departure AS flown
FROM flights f
         JOIN routes r ON r.route_no = f.route_no AND r.validity @ > f.scheduled_departure
WHERE f.status = 'Arrived'
ORDER BY flown DESC, f.flight_id
LIMIT 5
;

-- ------------------------------------------------------------------------------------------------ DEMODB_FLIGHTS_A_02
-- the flights of each day of the first week, and their running total.
--     day     | flights | running_total 
-- ------------+---------+---------------
--  2025-10-01 |     169 |           169
--  2025-10-02 |     189 |           358
--  2025-10-03 |     174 |           532
--  2025-10-04 |     185 |           717
--  2025-10-05 |     183 |           900
--  2025-10-06 |     175 |          1075
--  2025-10-07 |     187 |          1262
-- (7 rows)
SELECT scheduled_departure::date                               AS day,
       COUNT(*)                                                AS flights,
       SUM(COUNT(*)) OVER (ORDER BY scheduled_departure::date) AS running_total
FROM flights
WHERE scheduled_departure < '2025-10-08'
GROUP BY scheduled_departure:: date
ORDER BY day
;

-- ------------------------------------------------------------------------------------------------ DEMODB_FLIGHTS_A_03
-- the five fullest flights of the first day.
--  flight_id | route_no | airplane_code | boarded | seats | boarded_pct 
-- -----------+----------+---------------+---------+-------+-------------
--          2 | PG0247   | 32N           |     166 |   166 |       100.0
--          3 | PG0215   | 789           |     257 |   257 |       100.0
--          4 | PG0059   | CR7           |      70 |    70 |       100.0
--          5 | PG0220   | 351           |     325 |   325 |       100.0
--          6 | PG0009   | 789           |     257 |   257 |       100.0
-- (5 rows)
SELECT f.flight_id,
       f.route_no,
       r.airplane_code,
       COUNT(bp.ticket_no)                             AS boarded,
       s.seats,
       ROUND(100.0 * COUNT(bp.ticket_no) / s.seats, 1) AS boarded_pct
FROM flights f
         JOIN routes r ON r.route_no = f.route_no AND r.validity @ > f.scheduled_departure
         JOIN (SELECT airplane_code, COUNT(*) AS seats FROM seats GROUP BY airplane_code) s
              ON s.airplane_code = r.airplane_code
         JOIN boarding_passes bp ON bp.flight_id = f.flight_id
WHERE f.status = 'Arrived'
  AND f.scheduled_departure >= '2025-10-01'
  AND f.scheduled_departure < '2025-10-02'
GROUP BY f.flight_id, f.route_no, r.airplane_code, s.seats
ORDER BY boarded_pct DESC, f.flight_id
LIMIT 5
;
