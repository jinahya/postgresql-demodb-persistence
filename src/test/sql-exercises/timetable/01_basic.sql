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
-- Exercises at the basic level on timetable: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/timetable/01_basic.sql
--


-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_B_01
-- the flights from Sheremetyevo in the next 24 hours.
-- NOT YET RUN
SELECT flight_id, route_no, arrival_airport, scheduled_departure_local, status
FROM timetable
WHERE departure_airport = 'SVO'
  AND scheduled_departure >= bookings.now()
  AND scheduled_departure < bookings.now() + INTERVAL '24 hours'
ORDER BY scheduled_departure, flight_id
;

-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_B_02
-- the flights of the route PG0078 on the first day, in local time.
-- NOT YET RUN
SELECT flight_id,
       departure_airport,
       arrival_airport,
       scheduled_departure AT TIME ZONE 'UTC' AS scheduled_departure_utc,
       scheduled_departure_local,
       scheduled_arrival_local
FROM timetable
WHERE route_no = 'PG0078'
  AND scheduled_departure >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND scheduled_departure < TIMESTAMPTZ '2025-10-02 00:00:00+00'
ORDER BY scheduled_departure
;

-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_B_03
-- how many flights of each status.
-- NOT YET RUN
SELECT status, COUNT(*) AS flights
FROM timetable
GROUP BY status
ORDER BY status
;
