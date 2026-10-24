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
-- Exercises at the basic level on airplanes_data: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airplanes_data_exercises_01_basic.sql
--


-- ----------------------------------------------------------------------------------------- DEMODB_AIRPLANES_DATA_B_01
-- every airplane, by code.
--  airplane_code |                               model                               | range | speed 
-- ---------------+-------------------------------------------------------------------+-------+-------
--  32N           | {"en": "Airbus A320neo", "ru": "Аэробус A320neo"}                 |  6500 |   830
--  339           | {"en": "Airbus A330-900neo", "ru": "Аэробус A330-900neo"}         | 13300 |   870
--  351           | {"en": "Airbus A350-1000", "ru": "Аэробус A350-1000"}             | 16700 |   913
--  35X           | {"en": "Airbus A350F", "ru": "Аэробус A350F"}                     |  8700 |   903
--  76F           | {"en": "Boeing 767-300F", "ru": "Боинг 767-300F"}                 |  6000 |   850
--  77W           | {"en": "Boeing 777-300ER", "ru": "Боинг 777-300ER"}               | 14600 |   905
--  789           | {"en": "Boeing 787-9 Dreamliner", "ru": "Боинг 787-9 Dreamliner"} | 14000 |   913
--  7M7           | {"en": "Boeing 737 MAX 7", "ru": "Боинг 737 MAX 7"}               |  7000 |   840
--  CR7           | {"en": "Bombardier CRJ700", "ru": "Бомбардье CRJ700"}             |  3100 |   829
--  E70           | {"en": "Embraer E170", "ru": "Эмбраэр E170"}                      |  4000 |   800
-- (10 rows)
SELECT *
FROM airplanes_data
ORDER BY airplane_code
;

-- ----------------------------------------------------------------------------------------- DEMODB_AIRPLANES_DATA_B_02
-- the airplanes with a range of 10000 km or more, the longest first.
--  airplane_code |                               model                               | range | speed 
-- ---------------+-------------------------------------------------------------------+-------+-------
--  351           | {"en": "Airbus A350-1000", "ru": "Аэробус A350-1000"}             | 16700 |   913
--  77W           | {"en": "Boeing 777-300ER", "ru": "Боинг 777-300ER"}               | 14600 |   905
--  789           | {"en": "Boeing 787-9 Dreamliner", "ru": "Боинг 787-9 Dreamliner"} | 14000 |   913
--  339           | {"en": "Airbus A330-900neo", "ru": "Аэробус A330-900neo"}         | 13300 |   870
-- (4 rows)
SELECT *
FROM airplanes_data
WHERE range >= 10000
ORDER BY range DESC, airplane_code
;

-- ----------------------------------------------------------------------------------------- DEMODB_AIRPLANES_DATA_B_03
-- how many airplanes, and the shortest and the longest range.
--  airplanes | shortest | longest 
-- -----------+----------+---------
--         10 |     3100 |   16700
-- (1 row)
SELECT COUNT(*) AS airplanes, MIN(range) AS shortest, MAX(range) AS longest
FROM airplanes_data
;
