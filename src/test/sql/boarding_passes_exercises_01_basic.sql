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
-- Exercises at the basic level on boarding_passes: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/boarding_passes_exercises_01_basic.sql
--


-- ---------------------------------------------------------------------------------------- DEMODB_BOARDING_PASSES_B_01
-- the boarding passes of the flight 1, in boarding order.
--    ticket_no   | flight_id | seat_no | boarding_no |         boarding_time         
-- ---------------+-----------+---------+-------------+-------------------------------
--  0005432486526 |         1 | 24A     |           1 | 2025-09-30 23:32:30.846267+00
--  0005432803911 |         1 | 13C     |           2 | 2025-09-30 23:33:02.37625+00
--  0005432498784 |         1 | 14B     |           3 | 2025-09-30 23:33:33.290931+00
--  0005432803924 |         1 | 20A     |           4 | 2025-09-30 23:34:21.750622+00
--  0005432533210 |         1 | 29D     |           5 | 2025-09-30 23:34:23.70731+00
--  0005432821450 |         1 | 24D     |           6 | 2025-09-30 23:34:34.584991+00
--  0005432099637 |         1 | 28D     |           7 | 2025-09-30 23:34:38.218385+00
--  0005432175472 |         1 | 9B      |           8 | 2025-09-30 23:34:48.710511+00
--  0005432928358 |         1 | 20E     |           9 | 2025-09-30 23:35:28.204127+00
--  0005432229264 |         1 | 27B     |          10 | 2025-09-30 23:35:53.553426+00
-- ... 48 rows in all.
SELECT *
FROM boarding_passes
WHERE flight_id = 1
ORDER BY boarding_no NULLS LAST, ticket_no
;

-- ---------------------------------------------------------------------------------------- DEMODB_BOARDING_PASSES_B_02
-- how many boarding passes, and how many not boarded.
--  passes  | not_boarded 
-- ---------+-------------
--  2463832 |       30360
-- (1 row)
SELECT COUNT(*) AS passes, COUNT(*) FILTER (WHERE boarding_no IS NULL) AS not_boarded
FROM boarding_passes
;

-- ---------------------------------------------------------------------------------------- DEMODB_BOARDING_PASSES_B_03
-- the boarding passes of the ticket 0005432000000.
--    ticket_no   | flight_id | seat_no | boarding_no |         boarding_time         
-- ---------------+-----------+---------+-------------+-------------------------------
--  0005432000000 |       108 | 39F     |         343 | 2025-10-01 14:35:05.617326+00
-- (1 row)
SELECT *
FROM boarding_passes
WHERE ticket_no = '0005432000000'
ORDER BY flight_id
;
