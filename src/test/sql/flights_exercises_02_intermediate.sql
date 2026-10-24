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
-- Exercises at the intermediate level on flights: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/flights_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------------------------ DEMODB_FLIGHTS_I_01
-- how many flights each month.
--   month  | flights 
-- ---------+---------
--  2025-10 |    5580
--  2025-11 |    5467
--  2025-12 |    5539
--  2026-01 |    5172
-- (4 rows)
SELECT TO_CHAR(scheduled_departure, 'YYYY-MM') AS month, COUNT(*) AS flights
FROM flights
GROUP BY month
ORDER BY month
;

-- ------------------------------------------------------------------------------------------------ DEMODB_FLIGHTS_I_02
-- the ten routes whose flights depart the latest.
--  route_no | delay_minutes | flights 
-- ----------+---------------+---------
--  PG0290   |         188.2 |       4
--  PG0377   |         108.5 |       4
--  PG0428   |         104.7 |       4
--  PG0447   |          98.7 |       4
--  PG0235   |          62.7 |       4
--  PG0287   |          62.0 |       4
--  PG0313   |          57.4 |       4
--  PG0360   |          55.9 |       4
--  PG0425   |          50.8 |       4
--  PG0434   |          49.8 |       4
-- (10 rows)
SELECT route_no,
       ROUND(AVG(EXTRACT(EPOCH FROM actual_departure - scheduled_departure)) / 60,
             1) AS delay_minutes,
       COUNT(*) AS flights
FROM flights
WHERE status = 'Arrived'
GROUP BY route_no
ORDER BY delay_minutes DESC, route_no
LIMIT 10
;

-- ------------------------------------------------------------------------------------------------ DEMODB_FLIGHTS_I_03
-- how many flights departed more than an hour late.
--  late 
-- ------
--   554
-- (1 row)
SELECT COUNT(*) AS late
FROM flights
WHERE actual_departure > scheduled_departure + INTERVAL '1 hour'
;

-- ------------------------------------------------------------------------------------------------ DEMODB_FLIGHTS_I_04
-- today's flights, with their airports.
--  flight_id | route_no | departure_airport | arrival_airport |  scheduled_departure   |  status  
-- -----------+----------+-------------------+-----------------+------------------------+----------
--      11048 | PG0098   | DEL               | CCU             | 2025-12-01 00:10:00+00 | Boarding
--      11049 | PG0456   | KZN               | UFA             | 2025-12-01 00:15:00+00 | Boarding
--      11050 | PG0215   | XIY               | SHA             | 2025-12-01 00:25:00+00 | Boarding
--      11051 | PG0356   | XIY               | WUH             | 2025-12-01 00:30:00+00 | Delayed
--      11052 | PG0191   | LAX               | LAS             | 2025-12-01 00:35:00+00 | On Time
--      11053 | PG0350   | ATL               | CLT             | 2025-12-01 00:35:00+00 | On Time
--      11054 | PG0059   | OKA               | CTS             | 2025-12-01 00:45:00+00 | On Time
--      11055 | PG0526   | ORY               | VIE             | 2025-12-01 00:50:00+00 | On Time
--      11056 | PG0364   | CKG               | CTU             | 2025-12-01 00:55:00+00 | On Time
--      11057 | PG0247   | ATL               | DEN             | 2025-12-01 01:20:00+00 | On Time
-- ... 170 rows in all.
SELECT f.flight_id,
       f.route_no,
       r.departure_airport,
       r.arrival_airport,
       f.scheduled_departure,
       f.status
FROM flights f
         JOIN routes r ON r.route_no = f.route_no AND r.validity @ > f.scheduled_departure
WHERE f.scheduled_departure::date = bookings.now()::date
ORDER BY f.scheduled_departure, f.flight_id
;
