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
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/timetable_exercises_02_intermediate.sql
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
SELECT TO_CHAR(scheduled_arrival, 'YYYY-MM')                                          AS month,
       COUNT(*) FILTER (WHERE status = 'Arrived')                                     AS arrived,
       COUNT(*) FILTER (WHERE actual_arrival > scheduled_arrival + INTERVAL '1 hour') AS late
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
