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
-- Exercises at the advanced level on airplanes_data: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airplanes_data_exercises_03_advanced.sql
--


-- ----------------------------------------------------------------------------------------- DEMODB_AIRPLANES_DATA_A_01
-- the airplanes that fly no route.
--  airplane_code |                       model                       | range | speed 
-- ---------------+---------------------------------------------------+-------+-------
--  35X           | {"en": "Airbus A350F", "ru": "Аэробус A350F"}     |  8700 |   903
--  76F           | {"en": "Boeing 767-300F", "ru": "Боинг 767-300F"} |  6000 |   850
-- (2 rows)
SELECT a.*
FROM airplanes_data a
WHERE NOT EXISTS (SELECT 1 FROM routes r WHERE r.airplane_code = a.airplane_code)
ORDER BY a.airplane_code
;

-- ----------------------------------------------------------------------------------------- DEMODB_AIRPLANES_DATA_A_02
-- the airplanes by the flights they fly.
--  airplane_code |          model          | flights 
-- ---------------+-------------------------+---------
--  77W           | Boeing 777-300ER        |    6331
--  7M7           | Boeing 737 MAX 7        |    3599
--  789           | Boeing 787-9 Dreamliner |    3030
--  351           | Airbus A350-1000        |    2548
--  E70           | Embraer E170            |    2413
--  CR7           | Bombardier CRJ700       |    1564
--  339           | Airbus A330-900neo      |    1428
--  32N           | Airbus A320neo          |     845
-- (8 rows)
SELECT a.airplane_code, a.model ->> 'en' AS model, COUNT(f.flight_id) AS flights
FROM airplanes_data a
         JOIN routes r
              ON r.airplane_code = a.airplane_code
         JOIN flights f ON f.route_no = r.route_no AND r.validity @> f.scheduled_departure
GROUP BY a.airplane_code, a.model
ORDER BY flights DESC, a.airplane_code
;
