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
-- Exercises at the advanced level on airports_data: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/airports_data/03_advanced.sql
--


-- ----------------------------------------------------------------------- DEMODB_AIRPORTS_DATA_A_01
-- the cities with more than one airport.
-- NOT YET RUN
SELECT city ->> 'en'                                        AS city,
       country ->> 'en'                                     AS country,
       STRING_AGG(airport_code, ', ' ORDER BY airport_code) AS airports,
       COUNT(*)                                             AS count
FROM airports_data
GROUP BY city ->> 'en', country ->> 'en'
HAVING COUNT(*) > 1
ORDER BY count DESC, city, country
LIMIT 10
;

-- ----------------------------------------------------------------------- DEMODB_AIRPORTS_DATA_A_02
-- how many airports no route touches.
--  unused 
-- --------
--    5428
-- (1 row)
SELECT COUNT(*) AS unused
FROM airports_data a
WHERE NOT EXISTS (SELECT 1
                  FROM routes r
                  WHERE a.airport_code IN (r.departure_airport, r.arrival_airport))
;
