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
-- Exercises at the intermediate level on airplanes_data: a join to a neighbour, a group, a jsonb field or a range.
-- Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airplanes_data_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------------------------- DEMODB-AIRPLANES_DATA-I-01
-- the English name of each airplane.
--  airplane_code |          model          | range 
-- ---------------+-------------------------+-------
--  32N           | Airbus A320neo          |  6500
--  339           | Airbus A330-900neo      | 13300
--  351           | Airbus A350-1000        | 16700
--  35X           | Airbus A350F            |  8700
--  76F           | Boeing 767-300F         |  6000
--  77W           | Boeing 777-300ER        | 14600
--  789           | Boeing 787-9 Dreamliner | 14000
--  7M7           | Boeing 737 MAX 7        |  7000
--  CR7           | Bombardier CRJ700       |  3100
--  E70           | Embraer E170            |  4000
-- (10 rows)
SELECT airplane_code, model ->> 'en' AS model, range
FROM airplanes_data
ORDER BY airplane_code
;

-- ----------------------------------------------------------------------------------------- DEMODB-AIRPLANES_DATA-I-02
-- the hours each airplane flies at its full range.
--  airplane_code |          model          | hours 
-- ---------------+-------------------------+-------
--  351           | Airbus A350-1000        | 18.29
--  77W           | Boeing 777-300ER        | 16.13
--  789           | Boeing 787-9 Dreamliner | 15.33
--  339           | Airbus A330-900neo      | 15.29
--  35X           | Airbus A350F            |  9.63
--  7M7           | Boeing 737 MAX 7        |  8.33
--  32N           | Airbus A320neo          |  7.83
--  76F           | Boeing 767-300F         |  7.06
--  E70           | Embraer E170            |  5.00
--  CR7           | Bombardier CRJ700       |  3.74
-- (10 rows)
SELECT airplane_code, model ->> 'en' AS model, ROUND(range :: numeric / speed, 2) AS hours
FROM airplanes_data
ORDER BY hours DESC, airplane_code
;

-- ----------------------------------------------------------------------------------------- DEMODB-AIRPLANES_DATA-I-03
-- the languages the models are named in.
--  lang 
-- ------
--  en
--  ru
-- (2 rows)
SELECT DISTINCT jsonb_object_keys(model) AS lang
FROM airplanes_data
ORDER BY lang
;
