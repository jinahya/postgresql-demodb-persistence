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
-- Profiling queries on timetable, a view, a section per column. Each says what it looks for and what the result
-- should be; 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/timetable.sql
--


-- ---------------------------------------------------------------------------------------------------------------------
-- the view joins flights with the route valid at the scheduled departure and with the two airports, and adds each
-- time in its airport's time zone.
-- what the view defines; a row per flight.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = 'bookings'
  AND table_name = 'timetable'
;


-- ----------------------------------------------------------------------------------------------------------- flight_id
-- how many rows; 21758, as many as flights has.
SELECT COUNT(*), (SELECT COUNT(*) FROM flights) AS flights
FROM timetable
;

-- a flight_id more than one row has; the view has no key, and the entity takes flight_id as its identifier.
-- no rows: flight_id is unique, every flight having one route valid at its departure.
SELECT flight_id, COUNT(*) AS rows_
FROM timetable
GROUP BY flight_id
HAVING COUNT(*) > 1
;

-- a flight with no row in the view.
-- no rows: every flight has a row.
SELECT f.flight_id
FROM flights f
WHERE NOT EXISTS (SELECT 1
                  FROM timetable t
                  WHERE t.flight_id = f.flight_id)
;


-- -------------------------------------------------- departure_airport, arrival_airport, airplane_code, the local times
-- a row whose airports, airplane or local times are not those of the flight's route and airports.
-- no rows: the view computes them as it says.
SELECT t.flight_id, t.scheduled_departure, t.scheduled_departure_local, dep.timezone
FROM timetable t
         JOIN flights f ON f.flight_id = t.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
         JOIN airports_data dep ON dep.airport_code = r.departure_airport
         JOIN airports_data arr ON arr.airport_code = r.arrival_airport
WHERE t.departure_airport <> r.departure_airport
   OR t.arrival_airport <> r.arrival_airport
   OR t.airplane_code <> r.airplane_code
   OR t.scheduled_departure_local <> f.scheduled_departure AT TIME ZONE dep.timezone
   OR t.scheduled_arrival_local <> f.scheduled_arrival AT TIME ZONE arr.timezone
;

-- the earliest and the latest local departure of the day; 00:00 to 23:55, the routes' scheduled times.
SELECT MIN(scheduled_departure_local::time), MAX(scheduled_departure_local::time)
FROM timetable
;
