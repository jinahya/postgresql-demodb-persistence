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
-- Exercises at the basic level on timetable: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/timetable_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------------- DEMODB-TIMETABLE-B-01
-- the flights from Sheremetyevo in the next 24 hours.
--  flight_id | route_no | arrival_airport | scheduled_departure_local | status  
-- -----------+----------+-----------------+---------------------------+---------
--      11120 | PG0012   | UFA             | 2025-12-01 13:55:00       | On Time
--      11121 | PG0034   | SVX             | 2025-12-01 14:05:00       | On Time
--      11132 | PG0177   | AER             | 2025-12-01 15:05:00       | On Time
--      11211 | PG0018   | KZN             | 2025-12-02 02:05:00       | On Time
--      11216 | PG0106   | LED             | 2025-12-02 02:40:00       | On Time
-- (5 rows)
SELECT flight_id, route_no, arrival_airport, scheduled_departure_local, status
FROM timetable
WHERE departure_airport = 'SVO'
  AND scheduled_departure >= bookings.now()
  AND scheduled_departure < bookings.now() + INTERVAL '24 hours'
ORDER BY scheduled_departure
;

-- ---------------------------------------------------------------------------------------------- DEMODB-TIMETABLE-B-02
-- the flights of the route PG0078 on the first day, in local time.
--  flight_id | departure_airport | arrival_airport |  scheduled_departure   | scheduled_departure_local | scheduled_arrival_local 
-- -----------+-------------------+-----------------+------------------------+---------------------------+-------------------------
--          1 | SVO               | HEL             | 2025-10-01 00:00:00+00 | 2025-10-01 03:00:00       | 2025-10-01 04:40:00
-- (1 row)
SELECT flight_id,
       departure_airport,
       arrival_airport,
       scheduled_departure,
       scheduled_departure_local,
       scheduled_arrival_local
FROM timetable
WHERE route_no = 'PG0078'
  AND scheduled_departure >= '2025-10-01'
  AND scheduled_departure < '2025-10-02'
ORDER BY scheduled_departure
;
