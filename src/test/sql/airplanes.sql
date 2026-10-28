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
-- Profiling queries on airplanes, a view, a section per column. Each says what it looks for and what the result
-- should be; 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airplanes.sql
--


-- -------------------------------------------------------------------------------------------------
-- the view reads airplanes_data, with the model in the session's language, bookings.lang(); en.
-- what the view defines; a row per airplane.
SELECT view_definition
FROM information_schema.views
WHERE table_schema = 'bookings'
  AND table_name = 'airplanes'
;

-- the language the view reads, and the database's today; en, 2025-12-01.
SELECT bookings.lang() AS lang, bookings.now() AS now
;


-- ----------------------------------------------------------------------------------- airplane_code
-- how many rows, and the smallest and the largest airplane_code; 10 rows, 32N to E70, as many as airplanes_data has.
SELECT COUNT(*),
       MIN(airplane_code),
       MAX(airplane_code),
       (SELECT COUNT(*) FROM airplanes_data) AS airplanes
FROM airplanes
;


-- ----------------------------------------------------------------------------- model, range, speed
-- a row whose model is not its airplane's en name, or whose range or speed differs.
-- no rows: the view copies the three.
SELECT v.airplane_code, v.model, d.model ->> 'en' AS en
FROM airplanes v
         JOIN airplanes_data d
              ON d.airplane_code = v.airplane_code
WHERE v.model IS DISTINCT FROM d.model ->> 'en'
   OR v.range <> d.range
   OR v.speed <> d.speed
;
