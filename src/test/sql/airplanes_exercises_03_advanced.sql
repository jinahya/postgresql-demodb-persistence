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
-- Exercises at the advanced level on airplanes: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airplanes_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------- DEMODB_AIRPLANES_A_01
-- the three airplanes with the most routes.
--  airplane_code |          model          | routes 
-- ---------------+-------------------------+--------
--  77W           | Boeing 777-300ER        |    250
--  789           | Boeing 787-9 Dreamliner |    230
--  7M7           | Boeing 737 MAX 7        |    192
-- (3 rows)
SELECT a.airplane_code, a.model, COUNT(*) AS routes
FROM airplanes a
         JOIN routes r ON r.airplane_code = a.airplane_code
GROUP BY a.airplane_code, a.model
ORDER BY routes DESC, a.airplane_code LIMIT 3
;
