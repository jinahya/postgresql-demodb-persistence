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
-- Exercises at the basic level on flights: a filter, a sort, a count or an aggregate. Each poses a question, answers
-- it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/flights_exercises_01_basic.sql
--


-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_B_01
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

-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_B_02
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

-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_B_03
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

-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_B_04
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

-- ----------------------------------------------------------------------------- DEMODB_FLIGHTS_B_05
-- the flights in the air.
--  flight_id | route_no |  status  |  scheduled_departure   |       actual_departure        |   scheduled_arrival    
-- -----------+----------+----------+------------------------+-------------------------------+------------------------
--      10976 | PG0328   | Departed | 2025-11-30 13:45:00+00 | 2025-11-30 13:47:41.516904+00 | 2025-12-01 01:35:00+00
--      10984 | PG0384   | Departed | 2025-11-30 14:10:00+00 | 2025-11-30 14:14:54.688712+00 | 2025-12-01 00:15:00+00
--      10966 | PG0292   | Departed | 2025-11-30 12:50:00+00 | 2025-11-30 15:47:11.461764+00 | 2025-12-01 03:25:00+00
--      11024 | PG0381   | Departed | 2025-11-30 20:25:00+00 | 2025-11-30 20:29:50.700992+00 | 2025-12-01 00:10:00+00
--      11025 | PG0211   | Departed | 2025-11-30 20:45:00+00 | 2025-11-30 20:49:46.926534+00 | 2025-12-01 01:40:00+00
--      11027 | PG0290   | Departed | 2025-11-30 21:35:00+00 | 2025-11-30 21:40:56.704528+00 | 2025-12-01 03:30:00+00
--      11031 | PG0301   | Departed | 2025-11-30 22:05:00+00 | 2025-11-30 22:11:44.385829+00 | 2025-12-01 01:20:00+00
--      11033 | PG0043   | Departed | 2025-11-30 22:35:00+00 | 2025-11-30 22:42:45.411075+00 | 2025-12-01 03:00:00+00
--      11037 | PG0406   | Departed | 2025-11-30 23:10:00+00 | 2025-11-30 23:16:07.884398+00 | 2025-12-01 00:25:00+00
--      11036 | PG0011   | Departed | 2025-11-30 23:10:00+00 | 2025-11-30 23:16:44.924251+00 | 2025-12-01 01:15:00+00
-- ... 20 rows in all.
SELECT flight_id, route_no, status, scheduled_departure, actual_departure, scheduled_arrival
FROM flights
WHERE actual_departure IS NOT NULL
  AND actual_arrival IS NULL
ORDER BY actual_departure, flight_id
;
