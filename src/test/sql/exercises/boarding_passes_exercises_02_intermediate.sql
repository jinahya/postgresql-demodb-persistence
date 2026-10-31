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
-- Exercises at the intermediate level on boarding_passes: a join to a neighbour, a group, a jsonb field or a range.
-- Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/boarding_passes_exercises_02_intermediate.sql
--


-- --------------------------------------------------------------------- DEMODB_BOARDING_PASSES_I_01
-- the five seats boarded the most.
--  seat_no | boarded 
-- ---------+---------
--  20D     |    9773
--  22D     |    9752
--  20A     |    9742
--  20C     |    9732
--  22A     |    9730
-- (5 rows)
SELECT seat_no, COUNT(*) AS boarded
FROM boarding_passes
GROUP BY seat_no
ORDER BY boarded DESC, seat_no
LIMIT 5
;

-- --------------------------------------------------------------------- DEMODB_BOARDING_PASSES_I_02
-- how many segments have no boarding pass.
--  segments 
-- ----------
--   1477417
-- (1 row)
SELECT COUNT(*) AS segments
FROM segments s
         LEFT JOIN boarding_passes bp ON bp.ticket_no = s.ticket_no AND bp.flight_id = s.flight_id
WHERE bp.ticket_no IS NULL
;

-- --------------------------------------------------------------------- DEMODB_BOARDING_PASSES_I_03
-- how long before departure the passengers of the first day boarded.
--  minutes_before 
-- ----------------
--              11
-- (1 row)
SELECT ROUND(AVG(EXTRACT(EPOCH FROM f.scheduled_departure - bp.boarding_time)) /
             60) AS minutes_before
FROM boarding_passes bp
         JOIN flights f ON f.flight_id = bp.flight_id
WHERE f.scheduled_departure >= '2025-10-01'
  AND f.scheduled_departure < '2025-10-02'
  AND bp.boarding_time IS NOT NULL
;

-- --------------------------------------------------------------------- DEMODB_BOARDING_PASSES_I_04
-- the five arrived flights with the most passengers checked in but not boarded.
--  flight_id | checked_in | boarded | not_boarded 
-- -----------+------------+---------+-------------
--       8953 |        247 |      65 |         182
--       9761 |        404 |     248 |         156
--       1607 |        200 |      87 |         113
--      10471 |        257 |     183 |          74
--       8988 |        371 |     318 |          53
-- (5 rows)
SELECT bp.flight_id,
       COUNT(*)                         AS checked_in,
       COUNT(bp.boarding_no)            AS boarded,
       COUNT(*) - COUNT(bp.boarding_no) AS not_boarded
FROM boarding_passes bp
         JOIN flights f ON f.flight_id = bp.flight_id
WHERE f.status = 'Arrived'
GROUP BY bp.flight_id
HAVING COUNT(*) > COUNT(bp.boarding_no)
ORDER BY not_boarded DESC, bp.flight_id
LIMIT 5
;
