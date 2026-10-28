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
-- Exercises at the intermediate level on airports_data: a join to a neighbour, a group, a jsonb field or a range.
-- Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/airports_data_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------- DEMODB_AIRPORTS_DATA_I_01
-- the airports of Finland, in English.
--  airport_code |   airport_name   |     city     
-- --------------+------------------+--------------
--  ENF          | Enontekio        | Enontekio
--  HEL          | Vantaa           | Helsinki
--  HEM          | Malmi            | Helsinki
--  HYV          | Hyvinkää         | Hyvinkää
--  IVL          | Ivalo            | Ivalo
--  JOE          | Joensuu          | Joensuu
--  JYV          | Jyvaskyla        | Jyvaskyla
--  KAJ          | Kajaani          | Kajaani
--  KAO          | Kuusamo          | Kuusamo
--  KAU          | Kauhava          | Kauhava
-- ... 33 rows in all.
SELECT airport_code, airport_name ->> 'en' AS airport_name, city ->> 'en' AS city
FROM airports_data
WHERE country ->> 'en' = 'Finland'
ORDER BY airport_code
;

-- ----------------------------------------------------------------------- DEMODB_AIRPORTS_DATA_I_02
-- the ten time zones with the most airports.
--       timezone       | airports 
-- ---------------------+----------
--  America/New_York    |      366
--  America/Chicago     |      296
--  Asia/Shanghai       |      188
--  America/Anchorage   |      168
--  America/Los_Angeles |      143
--  Europe/Paris        |      123
--  America/Toronto     |      120
--  America/Denver      |      110
--  Asia/Kolkata        |      110
--  Europe/London       |      106
-- (10 rows)
SELECT timezone, COUNT(*) AS airports
FROM airports_data
GROUP BY timezone
ORDER BY airports DESC, timezone
LIMIT 10
;

-- ----------------------------------------------------------------------- DEMODB_AIRPORTS_DATA_I_03
-- the five countries with the most airports south of the equator.
--    country    | airports 
-- --------------+----------
--  Australia    |      217
--  Brazil       |      177
--  Argentina    |       83
--  Indonesia    |       75
--  South Africa |       56
-- (5 rows)
SELECT country ->> 'en' AS country, COUNT(*) AS airports
FROM airports_data
WHERE coordinates[1] < 0
GROUP BY country ->> 'en'
ORDER BY airports DESC, country
LIMIT 5
;
