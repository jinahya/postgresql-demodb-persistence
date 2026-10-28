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
-- Exercises at the intermediate level on seats: a join to a neighbour, a group, a jsonb field or a range. Each poses
-- a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/seats_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------- DEMODB_SEATS_I_01
-- the seats of each airplane, by fare conditions.
--  airplane_code |          model          | economy | comfort | business | seats 
-- ---------------+-------------------------+---------+---------+----------+-------
--  32N           | Airbus A320neo          |     138 |       0 |       28 |   166
--  339           | Airbus A330-900neo      |     224 |      28 |       29 |   281
--  351           | Airbus A350-1000        |     281 |       0 |       44 |   325
--  35X           | Airbus A350F            |       0 |       0 |        0 |     0
--  76F           | Boeing 767-300F         |       0 |       0 |        0 |     0
--  77W           | Boeing 777-300ER        |     326 |      48 |       30 |   404
--  789           | Boeing 787-9 Dreamliner |     188 |      21 |       48 |   257
--  7M7           | Boeing 737 MAX 7        |     144 |       0 |       16 |   160
--  CR7           | Bombardier CRJ700       |      52 |      12 |        6 |    70
--  E70           | Embraer E170            |      72 |       0 |        6 |    78
-- (10 rows)
SELECT a.airplane_code,
       a.model ->> 'en'                                       AS model,
       COUNT(*) FILTER (WHERE s.fare_conditions = 'Economy')  AS economy,
       COUNT(*) FILTER (WHERE s.fare_conditions = 'Comfort')  AS comfort,
       COUNT(*) FILTER (WHERE s.fare_conditions = 'Business') AS business,
       COUNT(s.seat_no)                                       AS seats
FROM airplanes_data a
         LEFT JOIN seats s
                   ON s.airplane_code = a.airplane_code
GROUP BY a.airplane_code, a.model
ORDER BY a.airplane_code
;

-- ------------------------------------------------------------------------------- DEMODB_SEATS_I_02
-- the rows and the letters of each airplane.
--  airplane_code | rows |  letters   
-- ---------------+------+------------
--  32N           |   30 | ABCDEF
--  339           |   58 | ABCDFGHJ
--  351           |   48 | ABCDEFGHJK
--  77W           |   51 | ABCDEFGHJK
--  789           |   57 | ABCDEFJKL
--  7M7           |   29 | ABCDEF
--  CR7           |   22 | ABCD
--  E70           |   24 | ABCD
-- (8 rows)
SELECT airplane_code,
       MAX(LEFT(seat_no, -1)::int)                                           AS rows,
       STRING_AGG(DISTINCT RIGHT(seat_no, 1), '' ORDER BY RIGHT(seat_no, 1)) AS letters
FROM seats
GROUP BY airplane_code
ORDER BY airplane_code
;

-- ------------------------------------------------------------------------------- DEMODB_SEATS_I_03
-- the seat numbers found in the most airplanes.
--  seat_no | airplanes 
-- ---------+-----------
--  1A      |         8
--  20A     |         8
--  20C     |         8
--  20D     |         8
--  21A     |         8
--  21C     |         8
--  21D     |         8
--  22A     |         8
--  22C     |         8
--  22D     |         8
-- (10 rows)
SELECT seat_no, COUNT(*) AS airplanes
FROM seats
GROUP BY seat_no
HAVING COUNT(*) =
       (SELECT MAX(airplanes) FROM (SELECT COUNT(*) AS airplanes FROM seats GROUP BY seat_no) c)
ORDER BY seat_no
;
