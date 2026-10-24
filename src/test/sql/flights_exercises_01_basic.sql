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
-- Exercises at the basic level on flights: a filter, a sort, a count or an aggregate. Each poses a question, answers
-- it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/flights_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------ DEMODB-FLIGHTS-B-01
-- the flights of the route PG0001 in October 2025.
--  flight_id | route_no | status  |  scheduled_departure   |   scheduled_arrival    |       actual_departure        |        actual_arrival         
-- -----------+----------+---------+------------------------+------------------------+-------------------------------+-------------------------------
--         56 | PG0001   | Arrived | 2025-10-01 08:55:00+00 | 2025-10-01 11:00:00+00 | 2025-10-01 09:04:22.242543+00 | 2025-10-01 11:10:08.207796+00
--        420 | PG0001   | Arrived | 2025-10-03 08:55:00+00 | 2025-10-03 11:00:00+00 | 2025-10-03 09:01:01.503634+00 | 2025-10-03 11:06:26.993085+00
--        595 | PG0001   | Arrived | 2025-10-04 08:55:00+00 | 2025-10-04 11:00:00+00 | 2025-10-04 09:01:14.82653+00  | 2025-10-04 11:06:58.248499+00
--        786 | PG0001   | Arrived | 2025-10-05 08:55:00+00 | 2025-10-05 11:00:00+00 | 2025-10-05 09:00:29.243524+00 | 2025-10-05 11:03:09.767793+00
--        962 | PG0001   | Arrived | 2025-10-06 08:55:00+00 | 2025-10-06 11:00:00+00 | 2025-10-06 09:00:22.207094+00 | 2025-10-06 11:05:05.472695+00
--       1145 | PG0001   | Arrived | 2025-10-07 08:55:00+00 | 2025-10-07 11:00:00+00 | 2025-10-07 09:00:29.021862+00 | 2025-10-07 11:05:45.299731+00
--       1319 | PG0001   | Arrived | 2025-10-08 08:55:00+00 | 2025-10-08 11:00:00+00 | 2025-10-08 09:01:37.086295+00 | 2025-10-08 11:07:39.885001+00
--       1682 | PG0001   | Arrived | 2025-10-10 08:55:00+00 | 2025-10-10 11:00:00+00 | 2025-10-10 09:02:00.561019+00 | 2025-10-10 11:06:54.008201+00
--       1858 | PG0001   | Arrived | 2025-10-11 08:55:00+00 | 2025-10-11 11:00:00+00 | 2025-10-11 08:59:42.455704+00 | 2025-10-11 11:04:41.659585+00
--       2049 | PG0001   | Arrived | 2025-10-12 08:55:00+00 | 2025-10-12 11:00:00+00 | 2025-10-12 08:58:44.753122+00 | 2025-10-12 11:04:51.136787+00
-- ... 26 rows in all.
SELECT *
FROM flights
WHERE route_no = 'PG0001'
  AND scheduled_departure >= '2025-10-01'
  AND scheduled_departure < '2025-11-01'
ORDER BY scheduled_departure
;

-- ------------------------------------------------------------------------------------------------ DEMODB-FLIGHTS-B-02
-- how many flights of each status.
--   status   | flights 
-- -----------+---------
--  Arrived   |   10966
--  Boarding  |       4
--  Cancelled |     121
--  Delayed   |      10
--  Departed  |      20
--  On Time   |     157
--  Scheduled |   10480
-- (7 rows)
SELECT status, COUNT(*) AS flights
FROM flights
GROUP BY status
ORDER BY status
;

-- ------------------------------------------------------------------------------------------------ DEMODB-FLIGHTS-B-03
-- the cancelled flights.
--  flight_id | route_no |  scheduled_departure   
-- -----------+----------+------------------------
--         84 | PG0029   | 2025-10-01 12:25:00+00
--        168 | PG0106   | 2025-10-01 23:40:00+00
--        846 | PG0231   | 2025-10-05 15:35:00+00
--       1143 | PG0284   | 2025-10-07 08:30:00+00
--       1209 | PG0204   | 2025-10-07 15:40:00+00
--       1425 | PG0037   | 2025-10-08 23:20:00+00
--       1492 | PG0113   | 2025-10-09 08:20:00+00
--       1563 | PG0204   | 2025-10-09 15:40:00+00
--       1592 | PG0200   | 2025-10-09 19:45:00+00
--       1596 | PG0202   | 2025-10-09 20:30:00+00
-- ... 121 rows in all.
SELECT flight_id, route_no, scheduled_departure
FROM flights
WHERE status = 'Cancelled'
ORDER BY scheduled_departure, flight_id
;

-- ------------------------------------------------------------------------------------------------ DEMODB-FLIGHTS-B-04
-- the flights scheduled to take more than twelve hours.
--  flight_id | route_no | scheduled 
-- -----------+----------+-----------
--        196 | PG0130   | 19:55:00
--       1122 | PG0129   | 19:55:00
--       1459 | PG0130   | 19:55:00
--       2384 | PG0129   | 19:55:00
--       2721 | PG0130   | 19:55:00
--       3646 | PG0129   | 19:55:00
--       3983 | PG0130   | 19:55:00
--       4907 | PG0129   | 19:55:00
--       5244 | PG0130   | 19:55:00
--        342 | PG0144   | 18:35:00
-- ... 225 rows in all.
SELECT flight_id, route_no, scheduled_arrival - scheduled_departure AS scheduled
FROM flights
WHERE scheduled_arrival - scheduled_departure > INTERVAL '12 hours'
ORDER BY scheduled DESC, flight_id
;
