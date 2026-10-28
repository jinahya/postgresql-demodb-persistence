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
-- Exercises at the basic level on airplanes: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/airplanes_exercises_01_basic.sql
--


-- --------------------------------------------------------------------------- DEMODB_AIRPLANES_B_01
-- every airplane of the view, by code.
--  airplane_code |          model          | range | speed 
-- ---------------+-------------------------+-------+-------
--  32N           | Airbus A320neo          |  6500 |   830
--  339           | Airbus A330-900neo      | 13300 |   870
--  351           | Airbus A350-1000        | 16700 |   913
--  35X           | Airbus A350F            |  8700 |   903
--  76F           | Boeing 767-300F         |  6000 |   850
--  77W           | Boeing 777-300ER        | 14600 |   905
--  789           | Boeing 787-9 Dreamliner | 14000 |   913
--  7M7           | Boeing 737 MAX 7        |  7000 |   840
--  CR7           | Bombardier CRJ700       |  3100 |   829
--  E70           | Embraer E170            |  4000 |   800
-- (10 rows)
SELECT *
FROM airplanes
ORDER BY airplane_code
;

-- --------------------------------------------------------------------------- DEMODB_AIRPLANES_B_02
-- the Boeings.
--  airplane_code |          model          | range | speed 
-- ---------------+-------------------------+-------+-------
--  76F           | Boeing 767-300F         |  6000 |   850
--  77W           | Boeing 777-300ER        | 14600 |   905
--  789           | Boeing 787-9 Dreamliner | 14000 |   913
--  7M7           | Boeing 737 MAX 7        |  7000 |   840
-- (4 rows)
SELECT *
FROM airplanes
WHERE model LIKE 'Boeing%'
ORDER BY airplane_code
;
