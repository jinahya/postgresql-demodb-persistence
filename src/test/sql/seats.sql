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
-- Profiling queries on seats, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/seats.sql
--


-- ------------------------------------------------------------------------------------------------------- airplane_code
-- how many rows, and of how many airplanes; 1741 seats on 8 of the 10 airplanes.
SELECT COUNT(*), COUNT(DISTINCT airplane_code)
FROM seats
;

-- an airplane_code which refers to no airplane; the foreign key forbids it.
-- no rows: every airplane is there.
SELECT s.airplane_code, s.seat_no
FROM seats s
WHERE NOT EXISTS (SELECT 1
                  FROM airplanes_data a
                  WHERE a.airplane_code = s.airplane_code)
;

-- the seats on each airplane; 77W has 404, CR7 70.
SELECT airplane_code, COUNT(*) AS seats
FROM seats
GROUP BY airplane_code
ORDER BY seats DESC, airplane_code ASC
;


-- ------------------------------------------------------------------------------------------------------------- seat_no
-- a seat_no which is not a row number and a letter, A to L without I.
-- no rows: every seat is one.
SELECT airplane_code, seat_no
FROM seats
WHERE seat_no !~ '^[0-9]{1,2}[A-HJ-L]$'
;

-- the letters in use; A to L without I, eleven.
SELECT DISTINCT RIGHT(seat_no, 1) AS letter
FROM seats
ORDER BY letter
;


-- ----------------------------------------------------------------------------------------------------- fare_conditions
-- the classes and how many seats in each; a check constraint allows these three.
-- Economy 1425, Business 207, Comfort 109.
SELECT fare_conditions, COUNT(*) AS seats
FROM seats
GROUP BY fare_conditions
ORDER BY seats DESC
;
