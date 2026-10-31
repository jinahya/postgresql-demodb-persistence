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
-- Exercises at the advanced level on airplanes: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/airplanes_exercises_03_advanced.sql
--


-- --------------------------------------------------------------------------- DEMODB_AIRPLANES_A_02
-- the airplanes ranked by speed, ties sharing a rank.
--  airplane_code |          model          | speed | rank | dense_rank 
-- ---------------+-------------------------+-------+------+------------
--  351           | Airbus A350-1000        |   913 |    1 |          1
--  789           | Boeing 787-9 Dreamliner |   913 |    1 |          1
--  77W           | Boeing 777-300ER        |   905 |    3 |          2
--  35X           | Airbus A350F            |   903 |    4 |          3
--  339           | Airbus A330-900neo      |   870 |    5 |          4
--  76F           | Boeing 767-300F         |   850 |    6 |          5
--  7M7           | Boeing 737 MAX 7        |   840 |    7 |          6
--  32N           | Airbus A320neo          |   830 |    8 |          7
--  CR7           | Bombardier CRJ700       |   829 |    9 |          8
--  E70           | Embraer E170            |   800 |   10 |          9
-- (10 rows)
SELECT airplane_code,
       model,
       speed,
       RANK() OVER (ORDER BY speed DESC)       AS rank,
       DENSE_RANK() OVER (ORDER BY speed DESC) AS dense_rank
FROM airplanes
ORDER BY speed DESC, airplane_code
;
