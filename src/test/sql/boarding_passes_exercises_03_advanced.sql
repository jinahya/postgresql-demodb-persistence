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
-- Exercises at the advanced level on boarding_passes: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/boarding_passes_exercises_03_advanced.sql
--


-- ---------------------------------------------------------------------------------------- DEMODB-BOARDING_PASSES-A-01
-- the passengers of the first day seated in other fare conditions than they paid.
--  passes | upgraded | downgraded 
-- --------+----------+------------
--       0 |        0 |          0
-- (1 row)
SELECT COUNT(*) AS passes,
       COUNT(*)    FILTER (WHERE rank.seat > rank.paid)                                            AS upgraded, COUNT(*) FILTER (WHERE rank.seat < rank.paid)                                            AS downgraded
FROM boarding_passes bp
         JOIN segments s ON s.ticket_no = bp.ticket_no AND s.flight_id = bp.flight_id
         JOIN flights f ON f.flight_id = bp.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @ > f.scheduled_departure
         JOIN seats st ON st.airplane_code = r.airplane_code AND st.seat_no = bp.seat_no
         CROSS JOIN LATERAL (SELECT ARRAY_POSITION(ARRAY['Economy', 'Comfort', 'Business'],
                                                   st.fare_conditions) AS seat,
                                    ARRAY_POSITION(ARRAY['Economy', 'Comfort', 'Business'],
                                                   s.fare_conditions)  AS paid) rank
WHERE f.scheduled_departure >= '2025-10-01'
  AND f.scheduled_departure < '2025-10-02'
  AND st.fare_conditions <> s.fare_conditions
;

-- ---------------------------------------------------------------------------------------- DEMODB-BOARDING_PASSES-A-02
-- the boarding numbers the flight 1 skipped.
--  missing 
-- ---------
-- (0 rows)
SELECT n AS missing
FROM GENERATE_SERIES(1, (SELECT MAX(boarding_no) FROM boarding_passes WHERE flight_id = 1)) n
WHERE NOT EXISTS (SELECT 1 FROM boarding_passes WHERE flight_id = 1 AND boarding_no = n)
ORDER BY n
;
