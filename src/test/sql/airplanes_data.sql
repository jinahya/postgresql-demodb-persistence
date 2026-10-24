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
-- Profiling queries on airplanes_data, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airplanes_data.sql
--


-- ------------------------------------------------------------------------------------------------------- airplane_code
-- how many rows, and the smallest and the largest airplane_code; 10 airplanes, 32N to E70.
SELECT COUNT(*), MIN(airplane_code), MAX(airplane_code)
FROM airplanes_data
;

-- an airplane_code which is not three upper-case letters or digits; the column is character(3).
-- no rows: every code is three of them.
SELECT airplane_code
FROM airplanes_data
WHERE airplane_code !~ '^[A-Z0-9]{3}$'
;

SELECT ad.airplane_code, a.airplane_code
FROM airplanes_data ad
         LEFT JOIN airplanes a ON ad.airplane_code = a.airplane_code
WHERE a.airplane_code IS NULL
;


-- --------------------------------------------------------------------------------------------------------------- model
-- model is jsonb; what it holds, and in which languages. Every row an object, with an en and a ru name.
SELECT k AS language, COUNT(*) AS airplanes
FROM airplanes_data,
     jsonb_object_keys(model) k
GROUP BY k
ORDER BY k
;

-- a model whose en or ru name is missing or blank.
-- no rows: every airplane has both names.
SELECT airplane_code, model
FROM airplanes_data
WHERE COALESCE(TRIM(model ->> 'en'), '') = ''
   OR COALESCE(TRIM(model ->> 'ru'), '') = ''
;

-- the shortest and the longest en name; 12 and 23 characters.
SELECT MIN(LENGTH(model ->> 'en')), MAX(LENGTH(model ->> 'en'))
FROM airplanes_data
;


-- --------------------------------------------------------------------------------------------------------------- range
-- the smallest and the largest range, in km; 3100 to 16700. A check constraint keeps it above zero.
SELECT MIN(range), MAX(range)
FROM airplanes_data
;


-- --------------------------------------------------------------------------------------------------------------- speed
-- the slowest and the fastest cruise speed, in km/h; 800 to 913. A check constraint keeps it above zero.
SELECT MIN(speed), MAX(speed)
FROM airplanes_data
;


-- ---------------------------------------------------------------------------------------------------------------------
-- an airplane with no seats; two, 35X and 76F.
SELECT a.airplane_code, a.model ->> 'en' AS model
FROM airplanes_data a
WHERE NOT EXISTS (SELECT 1
                  FROM seats s
                  WHERE s.airplane_code = a.airplane_code)
ORDER BY a.airplane_code
;

-- an airplane which flies no route; the same two, 35X and 76F.
SELECT a.airplane_code, a.model ->> 'en' AS model
FROM airplanes_data a
WHERE NOT EXISTS (SELECT 1
                  FROM routes r
                  WHERE r.airplane_code = a.airplane_code)
ORDER BY a.airplane_code
;
