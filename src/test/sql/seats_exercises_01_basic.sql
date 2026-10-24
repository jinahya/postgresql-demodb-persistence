-- -
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
-- -
--
-- Exercises at the basic level on seats: a filter, a sort, a count or an aggregate. Each poses a question, answers
-- it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/seats_exercises_01_basic.sql
--


-- -------------------------------------------------------------------------------------------------- DEMODB-SEATS-B-01
-- the Business seats of the Airbus A320neo.
--  airplane_code | seat_no | fare_conditions 
-- ---------------+---------+-----------------
--  32N           | 1A      | Business
--  32N           | 1C      | Business
--  32N           | 1D      | Business
--  32N           | 1F      | Business
--  32N           | 2A      | Business
--  32N           | 2C      | Business
--  32N           | 2D      | Business
--  32N           | 2F      | Business
--  32N           | 3A      | Business
--  32N           | 3C      | Business
-- ... 28 rows in all.
SELECT *
FROM seats
WHERE airplane_code = '32N'
  AND fare_conditions = 'Business'
ORDER BY seat_no
;

-- -------------------------------------------------------------------------------------------------- DEMODB-SEATS-B-02
-- how many seats each airplane has.
--  airplane_code | seats 
-- ---------------+-------
--  32N           |   166
--  339           |   281
--  351           |   325
--  77W           |   404
--  789           |   257
--  7M7           |   160
--  CR7           |    70
--  E70           |    78
-- (8 rows)
SELECT airplane_code, COUNT(*) AS seats
FROM seats
GROUP BY airplane_code
ORDER BY airplane_code
;

-- -------------------------------------------------------------------------------------------------- DEMODB-SEATS-B-03
-- how many seats of each fare conditions.
--  fare_conditions | seats 
-- -----------------+-------
--  Business        |   207
--  Comfort         |   109
--  Economy         |  1425
-- (3 rows)
SELECT fare_conditions, COUNT(*) AS seats
FROM seats
GROUP BY fare_conditions
ORDER BY fare_conditions
;
