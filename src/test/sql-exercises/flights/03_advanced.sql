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
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/flights/03_advanced.sql
--


-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_A_01
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
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
WHERE f.status = 'Arrived'
ORDER BY flown DESC, f.flight_id
LIMIT 5
;

-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_A_02
-- the flights of each day of the first week, and their running total.
-- NOT YET RUN
SELECT (scheduled_departure AT TIME ZONE 'UTC')::date                         AS day,
       COUNT(*)                                                         AS flights,
       SUM(COUNT(*)) OVER (ORDER BY (scheduled_departure AT TIME ZONE 'UTC')::date) AS running_total
FROM flights
WHERE scheduled_departure >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND scheduled_departure < TIMESTAMPTZ '2025-10-08 00:00:00+00'
GROUP BY day
ORDER BY day
;

-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_A_03
-- the five fullest flights of the first day.
-- NOT YET RUN
SELECT f.flight_id,
       f.route_no,
       r.airplane_code,
       COUNT(bp.boarding_no)                            AS boarded,
       s.seats,
       ROUND(100.0 * COUNT(bp.boarding_no) / s.seats, 1) AS boarded_pct
FROM flights f
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
         JOIN (SELECT airplane_code, COUNT(*) AS seats FROM seats GROUP BY airplane_code) s ON s.airplane_code = r.airplane_code
         LEFT JOIN boarding_passes bp ON bp.flight_id = f.flight_id
WHERE f.status = 'Arrived'
  AND f.scheduled_departure >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND f.scheduled_departure < TIMESTAMPTZ '2025-10-02 00:00:00+00'
GROUP BY f.flight_id, f.route_no, r.airplane_code, s.seats
ORDER BY 100.0 * COUNT(bp.boarding_no) / s.seats DESC, f.flight_id
LIMIT 5
;

-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_A_04
-- the time since the previous flight of the route PG0001, in October 2025.
-- NOT YET RUN
SELECT flight_id,
       scheduled_departure,
       scheduled_departure - LAG(scheduled_departure) OVER (ORDER BY scheduled_departure) AS since_previous
FROM flights
WHERE route_no = 'PG0001'
  AND scheduled_departure >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND scheduled_departure < TIMESTAMPTZ '2025-11-01 00:00:00+00'
ORDER BY scheduled_departure
;
