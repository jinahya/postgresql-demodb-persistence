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
-- Exercises at the intermediate level on timetable: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/timetable/02_intermediate.sql
--


-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_I_01
-- how many flights depart on another local day than the UTC one.
--  flights 
-- ---------
--     4597
-- (1 row)
SELECT COUNT(*) AS flights
FROM timetable
WHERE scheduled_departure_local::date <> (scheduled_departure AT TIME ZONE 'UTC')::date
;

-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_I_02
-- the five airport pairs with the most flights.
--  departure_airport | arrival_airport | flights 
-- -------------------+-----------------+---------
--  BGY               | FCO             |     121
--  CAN               | SZX             |     121
--  CDG               | NCE             |     121
--  FCO               | BGY             |     121
--  FCO               | MXP             |     121
-- (5 rows)
SELECT departure_airport, arrival_airport, COUNT(*) AS flights
FROM timetable
GROUP BY departure_airport, arrival_airport
ORDER BY flights DESC, departure_airport, arrival_airport
LIMIT 5
;

-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_I_03
-- the flights that arrived late, by month.
--   month  | arrived | late 
-- ---------+---------+------
--  2025-10 |    5526 |  261
--  2025-11 |    5440 |  292
--  2025-12 |       0 |    0
--  2026-01 |       0 |    0
-- (4 rows)
SELECT TO_CHAR(scheduled_arrival AT TIME ZONE 'UTC', 'YYYY-MM') AS month,
       COUNT(*) FILTER (WHERE status = 'Arrived') AS arrived,
       COUNT(*) FILTER (WHERE status = 'Arrived' AND actual_arrival > scheduled_arrival + INTERVAL '1 hour') AS late
FROM timetable
GROUP BY month
ORDER BY month
;

-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_I_04
-- the airport pairs whose flights land, by the local clocks, before they take off.
--  departure_airport | arrival_airport | flights | local_difference 
-- -------------------+-----------------+---------+------------------
--  AKL               | CLT             |       5 | -01:15:00
--  AKL               | SCL             |       9 | -03:50:00
--  CDG               | LGW             |      59 | -00:10:00
--  CDG               | LHR             |      31 | -00:05:00
--  HGH               | YYC             |       5 | -03:00:00
--  ORY               | LGW             |      25 | -00:05:00
--  ORY               | LHR             |      61 | -00:05:00
--  OVB               | DME             |       4 | -00:10:00
--  OVB               | KZN             |       9 | -00:45:00
--  OVB               | VKO             |       4 | -00:05:00
-- ... 16 rows in all.
SELECT departure_airport,
       arrival_airport,
       COUNT(*)                                                 AS flights,
       MIN(scheduled_arrival_local - scheduled_departure_local) AS local_difference
FROM timetable
WHERE scheduled_arrival_local < scheduled_departure_local
GROUP BY departure_airport, arrival_airport
ORDER BY departure_airport, arrival_airport
;

-- --------------------------------------------------------------------------- DEMODB_TIMETABLE_I_05
-- the ten airports with the longest average departure delay.
--  departure_airport | departed |  average_delay  
-- -------------------+----------+-----------------
--  AMD               |      121 | 00:26:30.318256
--  YUL               |      119 | 00:24:24.721629
--  OVB               |      106 | 00:23:45.132593
--  YEG               |      124 | 00:22:06.295919
--  DFW               |      138 | 00:21:33.284388
--  YVR               |      166 | 00:21:23.125409
--  PVG               |      242 | 00:21:14.01014
--  WUH               |      126 | 00:20:24.89244
--  HYD               |      139 | 00:19:40.605326
--  YYC               |      129 | 00:19:14.142478
-- (10 rows)
SELECT departure_airport,
       COUNT(*)                                    AS departed,
       AVG(actual_departure - scheduled_departure) AS average_delay
FROM timetable
WHERE actual_departure IS NOT NULL
GROUP BY departure_airport
HAVING COUNT(*) >= 100
ORDER BY average_delay DESC, departure_airport
LIMIT 10
;
