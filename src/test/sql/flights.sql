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
-- Profiling queries on flights, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/flights.sql
--


-- ----------------------------------------------------------------------------------------------------------- flight_id
-- how many rows, and the smallest and the largest flight_id; 21758 flights, 1 to 21758.
SELECT COUNT(*), MIN(flight_id), MAX(flight_id)
FROM flights
;

-- a flight_id whose next value is missing, below the largest; the column is an identity column.
-- no rows: flight_id runs from 1 without a gap.
SELECT a.flight_id + 1 AS missing_from
FROM flights a
WHERE a.flight_id < (SELECT MAX(flight_id) FROM flights)
  AND NOT EXISTS (SELECT 1
                  FROM flights b
                  WHERE b.flight_id = a.flight_id + 1)
;


-- ------------------------------------------------------------------------------------------------------------ route_no
-- how many routes the flights fly; 704, every route_no routes has.
SELECT COUNT(DISTINCT route_no)
FROM flights
;

-- a route_no which is not PG and four digits.
-- no rows: every number has that shape.
SELECT flight_id, route_no
FROM flights
WHERE route_no !~ '^PG[0-9]{4}$'
;

-- a flight whose route_no has no route valid at its scheduled departure; no foreign key forbids it, routes having no
-- primary key, and the timetable view joins on it.
-- no rows: every flight has its route.
SELECT f.flight_id, f.route_no, f.scheduled_departure
FROM flights f
WHERE NOT EXISTS (SELECT 1
                  FROM routes r
                  WHERE r.route_no = f.route_no
                    AND r.validity @ > f.scheduled_departure)
;

-- the routes with the most flights; 121, a flight a day over the four months, on several.
SELECT route_no, COUNT(*) AS flights
FROM flights
GROUP BY route_no
ORDER BY flights DESC, route_no ASC LIMIT 5
;


-- -------------------------------------------------------------------------------------------------------------- status
-- the statuses and how many flights in each; a check constraint allows these seven. Arrived 10966, Scheduled 10480,
-- On Time 157, Cancelled 121, Departed 20, Delayed 10, Boarding 4.
SELECT status, COUNT(*) AS flights
FROM flights
GROUP BY status
ORDER BY flights DESC, status ASC
;


-- ------------------------------------------------------------------------------ scheduled_departure, scheduled_arrival
-- the earliest and the latest of each; departures from 2025-10-01 00:00 to 2026-01-29 23:55, arrivals from
-- 2025-10-01 01:40 to 2026-01-30 11:50, four months of flights.
SELECT MIN(scheduled_departure),
       MAX(scheduled_departure),
       MIN(scheduled_arrival),
       MAX(scheduled_arrival)
FROM flights
;

-- a flight scheduled to arrive before, or as, it departs; a check constraint forbids it.
-- no rows: every arrival is after its departure.
SELECT flight_id, scheduled_departure, scheduled_arrival
FROM flights
WHERE scheduled_arrival <= scheduled_departure
;

-- the shortest and the longest scheduled flight; 35 minutes and 19 hours 55 minutes, the routes' durations.
SELECT MIN(scheduled_arrival - scheduled_departure), MAX(scheduled_arrival - scheduled_departure)
FROM flights
;

-- a route scheduled twice at one moment; the unique key on (route_no, scheduled_departure) forbids it.
-- no rows: each departure of a route is one flight.
SELECT route_no, scheduled_departure, COUNT(*) AS flights
FROM flights
GROUP BY route_no, scheduled_departure
HAVING COUNT(*) > 1
;


-- ------------------------------------------------------------------------------------ actual_departure, actual_arrival
-- which statuses have the two; Arrived has both, Departed the departure only, every other status neither.
SELECT status,
       COUNT(*) FILTER (WHERE actual_departure IS NULL) AS no_departure, COUNT(*) FILTER (WHERE actual_arrival IS NULL)   AS no_arrival, COUNT(*) AS flights
FROM flights
GROUP BY status
ORDER BY status
;

-- an actual_arrival without an actual_departure, or before it; a check constraint forbids both.
-- no rows: an arrival follows a departure.
SELECT flight_id, actual_departure, actual_arrival
FROM flights
WHERE actual_arrival IS NOT NULL
  AND (actual_departure IS NULL OR actual_arrival <= actual_departure)
;

-- how late the flights left, the least and the most; 33 seconds to 4 hours 16 minutes, never early.
SELECT MIN(actual_departure - scheduled_departure), MAX(actual_departure - scheduled_departure)
FROM flights
;

-- the earliest and the latest actual departure; 2025-10-01 to 2025-11-30, up to the database's today, bookings.now().
SELECT MIN(actual_departure), MAX(actual_departure)
FROM flights
;


-- ---------------------------------------------------------------------------------------------------------------------
-- how many flights have no segment sold; 965.
SELECT COUNT(*)
FROM flights f
WHERE NOT EXISTS (SELECT 1
                  FROM segments s
                  WHERE s.flight_id = f.flight_id)
;
