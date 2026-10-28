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
-- Exercises at the intermediate level on segments: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/segments_exercises_02_intermediate.sql
--


-- ---------------------------------------------------------------------------- DEMODB_SEGMENTS_I_01
-- the prices of each fare conditions.
--  fare_conditions | cheapest | average  |  dearest  
-- -----------------+----------+----------+-----------
--  Business        |  3500.00 | 11826.86 | 119500.00
--  Comfort         |  2275.00 |  6422.43 |  65325.00
--  Economy         |  1750.00 |  5492.63 |  59750.00
-- (3 rows)
SELECT fare_conditions,
       MIN(price)           AS cheapest,
       ROUND(AVG(price), 2) AS average,
       MAX(price)           AS dearest
FROM segments
GROUP BY fare_conditions
ORDER BY fare_conditions
;

-- ---------------------------------------------------------------------------- DEMODB_SEGMENTS_I_02
-- the five flights that earned the most.
--  flight_id |   revenue   | passengers 
-- -----------+-------------+------------
--      16679 | 22532100.00 |        404
--      17927 | 18808575.00 |        338
--      11358 | 15918200.00 |        404
--      11889 | 15918200.00 |        404
--      12608 | 15918200.00 |        404
-- (5 rows)
SELECT flight_id, SUM(price) AS revenue, COUNT(*) AS passengers
FROM segments
GROUP BY flight_id
ORDER BY revenue DESC, flight_id
LIMIT 5
;

-- ---------------------------------------------------------------------------- DEMODB_SEGMENTS_I_03
-- how many tickets have more than two segments.
--  tickets 
-- ---------
--   249113
-- (1 row)
SELECT COUNT(*) AS tickets
FROM (SELECT ticket_no
      FROM segments
      GROUP BY ticket_no
      HAVING COUNT(*) > 2) t
;
