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
-- Profiling queries on airports, a view, a section per column. Each says what it looks for and what the result
-- should be; 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airports.sql
--


-- -------------------------------------------------------------------------------------------------
-- the view reads airports_data, with the name, the city and the country in the session's language, bookings.lang().
-- what the view defines; a row per airport.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = 'bookings'
  AND table_name = 'airports'
;


-- ------------------------------------------------------------------------------------ airport_code
-- how many rows, and the smallest and the largest airport_code; 5501 rows, AAA to ZZU, as many as airports_data has.
SELECT COUNT(*),
       MIN(airport_code),
       MAX(airport_code),
       (SELECT COUNT(*) FROM airports_data) AS airports
FROM airports
;


-- ---------------------------------------------- airport_name, city, country, coordinates, timezone
-- a row whose name, city or country is not its airport's en one, or whose coordinates or timezone differ.
-- no rows: the view copies the five.
SELECT v.airport_code, v.airport_name, d.airport_name ->> 'en' AS en
FROM airports v
         JOIN airports_data d
              ON d.airport_code = v.airport_code
WHERE v.airport_name IS DISTINCT FROM d.airport_name ->> 'en'
   OR v.city IS DISTINCT FROM d.city ->> 'en'
   OR v.country IS DISTINCT FROM d.country ->> 'en'
   OR v.coordinates::text <> d.coordinates::text
   OR v.timezone <> d.timezone
;
