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
-- Exercises at the advanced level on airports: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/airports_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------- DEMODB_AIRPORTS_A_01
-- the airports flown from but never to.
--  airport_code | airport_name | country 
-- --------------+--------------+---------
-- (0 rows)
SELECT a.airport_code, a.airport_name, a.country
FROM airports a
WHERE EXISTS (SELECT 1 FROM routes r WHERE r.departure_airport = a.airport_code)
  AND NOT EXISTS (SELECT 1 FROM routes r WHERE r.arrival_airport = a.airport_code)
ORDER BY a.airport_code
;

-- ---------------------------------------------------------------------------- DEMODB_AIRPORTS_A_02
-- the ten airports with the most departures.
--  airport_code |    airport_name     |       city        | departures 
-- --------------+---------------------+-------------------+------------
--  FRA          | Frankfurt am Main   | Frankfurt am main |        714
--  BOM          | Chhatrapati Shivaji | Mumbai            |        600
--  DEL          | Indira Gandhi       | Delhi             |        592
--  ATL          | Hartsfield-Jackson  | Atlanta           |        576
--  SVO          | Sheremetyevo        | Moscow            |        569
--  TFU          | Tianfu              | Chengdu           |        533
--  PVG          | Pudong              | Shanghai          |        514
--  BLR          | Kempegowda          | Bangalore         |        497
--  DEN          | Denver              | Denver            |        475
--  FCO          | Fiumicino           | Rome              |        466
-- (10 rows)
SELECT a.airport_code, a.airport_name, a.city, COUNT(*) AS departures
FROM airports a
         JOIN routes r ON r.departure_airport = a.airport_code
         JOIN flights f ON f.route_no = r.route_no AND r.validity @> f.scheduled_departure
GROUP BY a.airport_code, a.airport_name, a.city
ORDER BY departures DESC, a.airport_code
LIMIT 10
;
