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
-- Exercises at the basic level on routes: a filter, a sort, a count or an aggregate. Each poses a question, answers
-- it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/routes_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------- DEMODB-ROUTES-B-01
-- the routes from Sheremetyevo.
--  route_no |                      validity                       | departure_airport | arrival_airport | airplane_code |  days_of_week   | scheduled_time | duration 
-- ----------+-----------------------------------------------------+-------------------+-----------------+---------------+-----------------+----------------+----------
--  PG0006   | ["2025-10-01 00:00:00+00","2025-11-01 00:00:00+00") | SVO               | KJA             | 789           | {6}             | 17:15:00       | 04:30:00
--  PG0006   | ["2025-11-01 00:00:00+00","2025-12-01 00:00:00+00") | SVO               | KJA             | E70           | {1,2,4,6}       | 17:15:00       | 05:05:00
--  PG0006   | ["2025-12-01 00:00:00+00","2026-01-01 00:00:00+00") | SVO               | KJA             | 339           | {3}             | 17:15:00       | 04:40:00
--  PG0006   | ["2026-01-01 00:00:00+00","2026-02-01 00:00:00+00") | SVO               | KJA             | 32N           | {4}             | 17:15:00       | 04:55:00
--  PG0012   | ["2025-10-01 00:00:00+00","2025-11-01 00:00:00+00") | SVO               | UFA             | 351           | {2}             | 13:55:00       | 01:55:00
--  PG0012   | ["2025-11-01 00:00:00+00","2025-12-01 00:00:00+00") | SVO               | UFA             | E70           | {2,3,5,6,7}     | 13:55:00       | 02:05:00
--  PG0012   | ["2025-12-01 00:00:00+00","2026-01-01 00:00:00+00") | SVO               | UFA             | E70           | {1,2,3,5,6,7}   | 13:55:00       | 02:05:00
--  PG0012   | ["2026-01-01 00:00:00+00","2026-02-01 00:00:00+00") | SVO               | UFA             | E70           | {1,2,3,4,5,6}   | 13:55:00       | 02:05:00
--  PG0018   | ["2025-10-01 00:00:00+00","2025-11-01 00:00:00+00") | SVO               | KZN             | 339           | {3,7}           | 02:05:00       | 01:25:00
--  PG0018   | ["2025-11-01 00:00:00+00","2025-12-01 00:00:00+00") | SVO               | KZN             | 7M7           | {2,4,6,7}       | 02:05:00       | 01:30:00
-- ... 34 rows in all.
SELECT *
FROM routes
WHERE departure_airport = 'SVO'
ORDER BY route_no, validity
;

-- ------------------------------------------------------------------------------------------------- DEMODB-ROUTES-B-02
-- how many rows, and how many route numbers.
--  rows | route_nos 
-- ------+-----------
--  1162 |       704
-- (1 row)
SELECT COUNT(*) AS rows, COUNT(DISTINCT route_no) AS route_nos
FROM routes
;

-- ------------------------------------------------------------------------------------------------- DEMODB-ROUTES-B-03
-- the routes flown every day of the week.
--  route_no | departure_airport | arrival_airport | scheduled_time |                      validity                       
-- ----------+-------------------+-----------------+----------------+-----------------------------------------------------
--  PG0017   | KZN               | SVO             | 06:30:00       | ["2025-12-01 00:00:00+00","2026-01-01 00:00:00+00")
--  PG0018   | SVO               | KZN             | 02:05:00       | ["2025-12-01 00:00:00+00","2026-01-01 00:00:00+00")
--  PG0019   | LKO               | DEL             | 13:20:00       | ["2025-10-01 00:00:00+00","2025-11-01 00:00:00+00")
--  PG0019   | LKO               | DEL             | 13:20:00       | ["2025-11-01 00:00:00+00","2025-12-01 00:00:00+00")
--  PG0019   | LKO               | DEL             | 13:20:00       | ["2026-01-01 00:00:00+00","2026-02-01 00:00:00+00")
--  PG0020   | DEL               | LKO             | 15:50:00       | ["2025-10-01 00:00:00+00","2025-11-01 00:00:00+00")
--  PG0020   | DEL               | LKO             | 15:50:00       | ["2025-11-01 00:00:00+00","2025-12-01 00:00:00+00")
--  PG0020   | DEL               | LKO             | 15:50:00       | ["2026-01-01 00:00:00+00","2026-02-01 00:00:00+00")
--  PG0035   | YEG               | YYC             | 00:15:00       | ["2026-01-01 00:00:00+00","2026-02-01 00:00:00+00")
--  PG0036   | YYC               | YEG             | 03:45:00       | ["2026-01-01 00:00:00+00","2026-02-01 00:00:00+00")
-- ... 288 rows in all.
SELECT route_no, departure_airport, arrival_airport, scheduled_time, validity
FROM routes
WHERE CARDINALITY(days_of_week) = 7
ORDER BY route_no, validity
;

-- ------------------------------------------------------------------------------------------------- DEMODB-ROUTES-B-04
-- the routes longer than ten hours.
--  route_no | departure_airport | arrival_airport | duration 
-- ----------+-------------------+-----------------+----------
--  PG0129   | SCL               | BOM             | 19:55:00
--  PG0130   | BOM               | SCL             | 19:55:00
--  PG0143   | MCO               | MAA             | 18:35:00
--  PG0144   | MAA               | MCO             | 18:35:00
--  PG0569   | SZX               | MCO             | 17:30:00
--  PG0570   | MCO               | SZX             | 17:30:00
--  PG0615   | AKL               | CLT             | 16:45:00
--  PG0616   | CLT               | AKL             | 16:45:00
--  PG0635   | MIA               | DEL             | 16:45:00
--  PG0636   | DEL               | MIA             | 16:45:00
-- ... 80 rows in all.
SELECT route_no, departure_airport, arrival_airport, duration
FROM routes
WHERE duration > INTERVAL '10 hours'
ORDER BY duration DESC, route_no, validity
;
