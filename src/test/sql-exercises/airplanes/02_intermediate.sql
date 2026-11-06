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
-- Exercises at the intermediate level on airplanes: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/airplanes/02_intermediate.sql
--


-- --------------------------------------------------------------------------- DEMODB_AIRPLANES_I_01
-- how many airplanes each maker has.
--    maker    | airplanes 
-- ------------+-----------
--  Airbus     |         4
--  Boeing     |         4
--  Bombardier |         1
--  Embraer    |         1
-- (4 rows)
SELECT SPLIT_PART(model, ' ', 1) AS maker, COUNT(*) AS airplanes
FROM airplanes
GROUP BY maker
ORDER BY airplanes DESC, maker
;
