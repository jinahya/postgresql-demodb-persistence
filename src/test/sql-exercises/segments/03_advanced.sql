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
-- Exercises at the advanced level on segments: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/segments/03_advanced.sql
--


-- ---------------------------------------------------------------------------- DEMODB_SEGMENTS_A_01
-- the five flights where Business costs the most over Economy.
-- NOT YET RUN
SELECT flight_id,
       MIN(price) FILTER (WHERE fare_conditions = 'Economy')  AS economy,
       MIN(price) FILTER (WHERE fare_conditions = 'Business') AS business,
       ROUND(MIN(price) FILTER (WHERE fare_conditions = 'Business') / MIN(price) FILTER (WHERE fare_conditions = 'Economy'), 2) AS ratio
FROM segments
GROUP BY flight_id
HAVING MIN(price) FILTER (WHERE fare_conditions = 'Economy') > 0
   AND COUNT(*) FILTER (WHERE fare_conditions = 'Business') > 0
ORDER BY ratio DESC, flight_id
LIMIT 5
;

-- ---------------------------------------------------------------------------- DEMODB_SEGMENTS_A_02
-- how many segments of the first day were sold in fare conditions the airplane has not.
--  segments 
-- ----------
--         0
-- NOT YET RUN
SELECT COUNT(*) AS segments
FROM segments s
         JOIN flights f ON f.flight_id = s.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
WHERE f.scheduled_departure >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND f.scheduled_departure < TIMESTAMPTZ '2025-10-02 00:00:00+00'
  AND NOT EXISTS (SELECT 1
                  FROM seats st
                  WHERE st.airplane_code = r.airplane_code
                    AND st.fare_conditions = s.fare_conditions)
;
