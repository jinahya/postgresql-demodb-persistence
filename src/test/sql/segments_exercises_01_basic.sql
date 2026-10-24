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
-- Exercises at the basic level on segments: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/segments_exercises_01_basic.sql
--


-- ----------------------------------------------------------------------------------------------- DEMODB_SEGMENTS_B_01
-- the segments of the ticket 0005432000000.
--    ticket_no   | flight_id | fare_conditions |  price  
-- ---------------+-----------+-----------------+---------
--  0005432000000 |       108 | Economy         | 3250.00
-- (1 row)
SELECT *
FROM segments
WHERE ticket_no = '0005432000000'
ORDER BY flight_id
;

-- ----------------------------------------------------------------------------------------------- DEMODB_SEGMENTS_B_02
-- how many segments of each fare conditions.
--  fare_conditions | segments 
-- -----------------+----------
--  Business        |   417644
--  Comfort         |   301832
--  Economy         |  3221773
-- (3 rows)
SELECT fare_conditions, COUNT(*) AS segments
FROM segments
GROUP BY fare_conditions
ORDER BY fare_conditions
;

-- ----------------------------------------------------------------------------------------------- DEMODB_SEGMENTS_B_03
-- the segments of the flight 1.
--    ticket_no   | flight_id | fare_conditions |  price   
-- ---------------+-----------+-----------------+----------
--  0005432037091 |         1 | Economy         |  5000.00
--  0005432099637 |         1 | Economy         |  5000.00
--  0005432115850 |         1 | Economy         |  5000.00
--  0005432175472 |         1 | Economy         |  5000.00
--  0005432206213 |         1 | Economy         |  5000.00
--  0005432206220 |         1 | Business        | 10000.00
--  0005432229264 |         1 | Economy         |  5000.00
--  0005432261838 |         1 | Economy         |  5000.00
--  0005432301833 |         1 | Economy         |  5000.00
--  0005432330445 |         1 | Economy         |  5000.00
-- ... 48 rows in all.
SELECT *
FROM segments
WHERE flight_id = 1
ORDER BY ticket_no
;
