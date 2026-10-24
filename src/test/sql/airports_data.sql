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
-- Profiling queries on airports_data, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airports_data.sql
--


-- -------------------------------------------------------------------------------------------------------- airport_code
-- how many rows, and the smallest and the largest airport_code; 5501 airports, AAA to ZZU.
SELECT COUNT(*), MIN(airport_code), MAX(airport_code)
FROM airports_data
;

-- an airport_code which is not three upper-case letters; the column is character(3), and the codes are IATA's.
-- no rows: every code is three letters.
SELECT airport_code
FROM airports_data
WHERE airport_code !~ '^[A-Z]{3}$'
;


-- ----------------------------------------------------------------------------------------- airport_name, city, country
-- the three are jsonb; in which languages. Every row an object, with an en and a ru name.
SELECT k AS language, COUNT(*) AS airports
FROM airports_data,
     jsonb_object_keys(airport_name) k
GROUP BY k
ORDER BY k
;

-- a row whose en name, city or country is missing or blank.
-- no rows: every airport has all three in English.
SELECT airport_code, airport_name, city, country
FROM airports_data
WHERE COALESCE(TRIM(airport_name ->> 'en'), '') = ''
   OR COALESCE(TRIM(city ->> 'en'), '') = ''
   OR COALESCE(TRIM(country ->> 'en'), '') = ''
;

-- the shortest and the longest en name and city; names 2 to 35 characters, cities 2 to 26.
SELECT MIN(LENGTH(airport_name ->> 'en')),
       MAX(LENGTH(airport_name ->> 'en')),
       MIN(LENGTH(city ->> 'en')),
       MAX(LENGTH(city ->> 'en'))
FROM airports_data
;

-- how many countries and cities, in English; 230 countries, 5075 cities.
SELECT COUNT(DISTINCT country ->> 'en') AS countries, COUNT(DISTINCT city ->> 'en') AS cities
FROM airports_data
;

-- the countries with the most airports; the United States has 1129.
SELECT country ->> 'en' AS country, COUNT(*) AS airports
FROM airports_data
GROUP BY country ->> 'en'
ORDER BY airports DESC, country ASC
LIMIT 5
;

-- the cities with the most airports; London has 8.
SELECT city ->> 'en' AS city, COUNT(*) AS airports
FROM airports_data
GROUP BY city ->> 'en'
ORDER BY airports DESC, city ASC
LIMIT 5
;


-- --------------------------------------------------------------------------------------------------------- coordinates
-- coordinates is a point, (longitude,latitude); the extremes. -179.877 to 179.951 east, -62.1908 to 82.5178 north.
SELECT MIN(coordinates[0]),
       MAX(coordinates[0]),
       MIN(coordinates[1]),
       MAX(coordinates[1])
FROM airports_data
;

-- a point off the globe.
-- no rows: every longitude is within 180 and every latitude within 90.
SELECT airport_code, coordinates
FROM airports_data
WHERE coordinates[0] NOT BETWEEN -180 AND 180
   OR coordinates[1] NOT BETWEEN -90 AND 90
;


-- ------------------------------------------------------------------------------------------------------------ timezone
-- how many time zones; 311.
SELECT COUNT(DISTINCT timezone)
FROM airports_data
;

-- a timezone the server does not know; the timetable view would fail to convert on it.
-- no rows: every timezone is in pg_timezone_names.
SELECT DISTINCT timezone
FROM airports_data a
WHERE NOT EXISTS (SELECT 1
                  FROM pg_timezone_names z
                  WHERE z.name = a.timezone)
;

-- the time zones with the most airports; America/New_York has 366.
SELECT timezone, COUNT(*) AS airports
FROM airports_data
GROUP BY timezone
ORDER BY airports DESC, timezone ASC
LIMIT 5
;


-- ---------------------------------------------------------------------------------------------------------------------
-- how many airports no route departs from or arrives at; 5428 of the 5501, the routes touching 73.
SELECT COUNT(*)
FROM airports_data a
WHERE NOT EXISTS (SELECT 1
                  FROM routes r
                  WHERE r.departure_airport = a.airport_code
                     OR r.arrival_airport = a.airport_code)
;
