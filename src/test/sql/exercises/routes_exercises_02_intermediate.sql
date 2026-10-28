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
-- Exercises at the intermediate level on routes: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/routes_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------ DEMODB_ROUTES_I_01
-- the periods of the route PG0001.
--  route_no |       valid_from       |        valid_to        | airplane_code | days_of_week  
-- ----------+------------------------+------------------------+---------------+---------------
--  PG0001   | 2025-10-01 00:00:00+00 | 2025-11-01 00:00:00+00 | 339           | {1,2,3,5,6,7}
--  PG0001   | 2025-11-01 00:00:00+00 | 2025-12-01 00:00:00+00 | 789           | {1,2,3,5,6,7}
--  PG0001   | 2025-12-01 00:00:00+00 | 2026-01-01 00:00:00+00 | 789           | {2,3,4,5,6,7}
--  PG0001   | 2026-01-01 00:00:00+00 | 2026-02-01 00:00:00+00 | 77W           | {2,4,6,7}
-- (4 rows)
SELECT route_no,
       LOWER(validity) AS valid_from,
       UPPER(validity) AS valid_to,
       airplane_code,
       days_of_week
FROM routes
WHERE route_no = 'PG0001'
ORDER BY validity
;

-- ------------------------------------------------------------------------------ DEMODB_ROUTES_I_02
-- the five route numbers with the most periods.
--  route_no | periods 
-- ----------+---------
--  PG0001   |       4
--  PG0002   |       4
--  PG0005   |       4
--  PG0006   |       4
--  PG0007   |       4
-- (5 rows)
SELECT route_no, COUNT(*) AS periods
FROM routes
GROUP BY route_no
ORDER BY periods DESC, route_no
LIMIT 5
;

-- ------------------------------------------------------------------------------ DEMODB_ROUTES_I_03
-- how many routes each airplane flies.
--  airplane_code |          model          | routes 
-- ---------------+-------------------------+--------
--  77W           | Boeing 777-300ER        |    250
--  789           | Boeing 787-9 Dreamliner |    230
--  7M7           | Boeing 737 MAX 7        |    192
--  E70           | Embraer E170            |    142
--  CR7           | Bombardier CRJ700       |    118
--  351           | Airbus A350-1000        |    110
--  339           | Airbus A330-900neo      |     80
--  32N           | Airbus A320neo          |     40
-- (8 rows)
SELECT r.airplane_code, a.model ->> 'en' AS model, COUNT(*) AS routes
FROM routes r
         JOIN airplanes_data a
              ON a.airplane_code = r.airplane_code
GROUP BY r.airplane_code, a.model
ORDER BY routes DESC, r.airplane_code
;

-- ------------------------------------------------------------------------------ DEMODB_ROUTES_I_04
-- how many routes are valid today.
--  valid_today 
-- -------------
--          292
-- (1 row)
SELECT COUNT(*) AS valid_today
FROM routes
WHERE validity @> bookings.now()
;
