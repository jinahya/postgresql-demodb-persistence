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
-- Exercises at the basic level on airports: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/airports_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------- DEMODB_AIRPORTS_B_01
-- the airports of Norway.
--  airport_code |  airport_name  |     city     | country |    coordinates    |      timezone       
-- --------------+----------------+--------------+---------+-------------------+---------------------
--  AES          | Vigra          | Ålesund      | Norway  | (6.1197,62.5625)  | Europe/Oslo
--  ALF          | Alta           | Alta         | Norway  | (23.3717,69.9761) | Europe/Oslo
--  ANX          | Andøya         | Andenes      | Norway  | (16.1442,69.2925) | Europe/Oslo
--  BDU          | Bardufoss      | Bardufoss    | Norway  | (18.5404,69.0558) | Europe/Oslo
--  BGO          | Flesland       | Bergen       | Norway  | (5.2181,60.2934)  | Europe/Oslo
--  BJF          | Båtsfjord      | Båtsfjord    | Norway  | (29.6914,70.6005) | Europe/Oslo
--  BNN          | Brønnøysund    | Brønnøysund  | Norway  | (12.2175,65.4611) | Europe/Oslo
--  BOO          | Bodø           | Bodø         | Norway  | (14.3653,67.2692) | Europe/Oslo
--  BVG          | Berlevåg       | Berlevåg     | Norway  | (29.0342,70.8714) | Europe/Oslo
--  DLD          | Dagali         | Geilo        | Norway  | (8.5183,60.4173)  | Europe/Oslo
-- ... 55 rows in all.
SELECT *
FROM airports
WHERE country = 'Norway'
ORDER BY airport_code
;

-- ---------------------------------------------------------------------------- DEMODB_AIRPORTS_B_02
-- the airports of Moscow.
--  airport_code | airport_name |  city  | country 
-- --------------+--------------+--------+---------
--  BKA          | Bykovo       | Moscow | Russia
--  DME          | Domodedovo   | Moscow | Russia
--  OSF          | Ostafyevo    | Moscow | Russia
--  SVO          | Sheremetyevo | Moscow | Russia
--  VKO          | Vnukovo      | Moscow | Russia
-- (5 rows)
SELECT airport_code, airport_name, city, country
FROM airports
WHERE city = 'Moscow'
ORDER BY airport_code
;

-- ---------------------------------------------------------------------------- DEMODB_AIRPORTS_B_03
-- how many airports are called International.
--  international 
-- ---------------
--              0
-- (1 row)
SELECT COUNT(*) AS international
FROM airports
WHERE airport_name LIKE '%International%'
;
