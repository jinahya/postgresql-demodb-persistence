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
-- Exercises at the intermediate level on tickets: a join to a neighbour, a group, a jsonb field or a range. Each
-- poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/tickets_exercises_02_intermediate.sql
--


-- ----------------------------------------------------------------------------- DEMODB_TICKETS_I_01
-- the five passengers with the most tickets.
--    passenger_id   |  passenger_name  | tickets 
-- ------------------+------------------+---------
--  CN 1303160118058 | Haiyun Yang      |       5
--  CN 8458154433091 | Haiyun Zhao      |       5
--  US 2995168528581 | William Thompson |       5
--  CA 0307652121299 | Pascal Lang      |       4
--  CA 0314814108732 | Dawn Dupuis      |       4
-- (5 rows)
SELECT passenger_id, passenger_name, COUNT(*) AS tickets
FROM tickets
GROUP BY passenger_id, passenger_name
ORDER BY tickets DESC, passenger_id
LIMIT 5
;

-- ----------------------------------------------------------------------------- DEMODB_TICKETS_I_02
-- the ten countries whose passengers hold the most tickets.
--  country | tickets 
-- ---------+---------
--  CN      |  772440
--  US      |  520042
--  IN      |  401834
--  CA      |  198167
--  RU      |  191727
--  DE      |  171900
--  IT      |  151811
--  JP      |  149313
--  GB      |  144743
--  FR      |  125518
-- (10 rows)
SELECT LEFT(passenger_id, 2) AS country, COUNT(*) AS tickets
FROM tickets
GROUP BY country
ORDER BY tickets DESC, country
LIMIT 10
;

-- ----------------------------------------------------------------------------- DEMODB_TICKETS_I_03
-- how many tickets have no segment.
--  tickets 
-- ---------
--        0
-- (1 row)
SELECT COUNT(*) AS tickets
FROM tickets t
WHERE NOT EXISTS (SELECT 1 FROM segments s WHERE s.ticket_no = t.ticket_no)
;

-- ----------------------------------------------------------------------------- DEMODB_TICKETS_I_04
-- how many bookings carry one, two or more passengers.
--  passengers | bookings 
-- ------------+----------
--           1 |   851889
--           2 |   374707
--           3 |    61520
--           4 |     4662
--           5 |      115
-- (5 rows)
SELECT passengers, COUNT(*) AS bookings
FROM (SELECT book_ref, COUNT(DISTINCT passenger_id) AS passengers
      FROM tickets
      GROUP BY book_ref) b
GROUP BY passengers
ORDER BY passengers
;
