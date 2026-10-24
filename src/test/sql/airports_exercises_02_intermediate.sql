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
-- Exercises at the intermediate level on airports: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airports_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------------------------------- DEMODB_AIRPORTS_I_01
-- the ten countries with the most airports.
--     country     | airports 
-- ----------------+----------
--  United States  |     1129
--  Canada         |      335
--  Australia      |      218
--  China          |      187
--  Brazil         |      180
--  Russia         |      158
--  France         |      122
--  India          |      110
--  Indonesia      |      105
--  United Kingdom |      105
-- (10 rows)
SELECT country, COUNT(*) AS airports
FROM airports
GROUP BY country
ORDER BY airports DESC, country
LIMIT 10
;

-- ----------------------------------------------------------------------------------------------- DEMODB_AIRPORTS_I_02
-- how many countries have exactly one airport.
--  countries 
-- -----------
--         38
-- (1 row)
SELECT COUNT(*) AS countries
FROM (SELECT country
      FROM airports
      GROUP BY country
      HAVING COUNT(*) = 1) t
;

-- ----------------------------------------------------------------------------------------------- DEMODB_AIRPORTS_I_03
-- the westernmost and the easternmost airport.
--  airport_code | airport_name | country | longitude 
-- --------------+--------------+---------+-----------
--  TVU          | Matei        | Fiji    |  -179.877
--  MFJ          | Moala        | Fiji    |   179.951
-- (2 rows)
SELECT airport_code, airport_name, country, coordinates[0] AS longitude
FROM airports
WHERE coordinates[0] = (SELECT MIN(coordinates[0]) FROM airports)
   OR coordinates[0] = (SELECT MAX(coordinates[0]) FROM airports)
ORDER BY longitude
;
