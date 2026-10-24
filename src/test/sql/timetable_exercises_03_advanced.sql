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
-- Exercises at the advanced level on timetable: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/timetable_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------------- DEMODB_TIMETABLE_A_01
-- whether the view has a row for every flight.
--  missing 
-- ---------
--        0
-- (1 row)
SELECT COUNT(*) AS missing
FROM flights f
         LEFT JOIN timetable t ON t.flight_id = f.flight_id
WHERE t.flight_id IS NULL
;

-- ---------------------------------------------------------------------------------------------- DEMODB_TIMETABLE_A_02
-- the connections at Sheremetyevo on the first day.
--  arriving | departing | destination | layover  
-- ----------+-----------+-------------+----------
--        54 |        81 | AER         | 02:30:00
--       131 |       168 | LED         | 03:35:00
-- (2 rows)
SELECT a.flight_id                                 AS arriving,
       d.flight_id                                 AS departing,
       d.arrival_airport                           AS destination,
       d.scheduled_departure - a.scheduled_arrival AS layover
FROM timetable a
         JOIN timetable d ON d.departure_airport = a.arrival_airport
    AND
                             d.scheduled_departure BETWEEN a.scheduled_arrival + INTERVAL '1 hour' AND a.scheduled_arrival + INTERVAL '4 hours'
WHERE a.arrival_airport = 'SVO'
  AND a.scheduled_arrival >= '2025-10-01'
  AND a.scheduled_arrival
    < '2025-10-02'
ORDER BY a.flight_id, d.flight_id
    LIMIT 10
;
