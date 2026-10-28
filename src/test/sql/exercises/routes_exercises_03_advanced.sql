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
-- Exercises at the advanced level on routes: a subquery, a window, a self join or a recursion. Each poses a
-- question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/routes_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------ DEMODB_ROUTES_A_01
-- the longest route from each airport.
--  departure_airport | route_no | arrival_airport | duration 
-- -------------------+----------+-----------------+----------
--  AER               | PG0290   | KJA             | 05:55:00
--  AKL               | PG0615   | CLT             | 16:45:00
--  AMD               | PG0428   | SZX             | 06:00:00
--  ARN               | PG0499   | PKX             | 08:40:00
--  ATL               | PG0415   | MUC             | 09:45:00
--  BER               | PG0252   | BOM             | 08:45:00
--  BGY               | PG0087   | SZX             | 11:40:00
--  BLR               | PG0535   | PKX             | 06:45:00
--  BOM               | PG0130   | SCL             | 19:55:00
--  CAN               | PG0492   | AKL             | 11:50:00
-- (10 rows)
SELECT DISTINCT
    ON (departure_airport) departure_airport,
                           route_no,
                           arrival_airport,
                           duration
FROM routes
ORDER BY departure_airport, duration DESC, route_no, validity
LIMIT 10
;

-- ------------------------------------------------------------------------------ DEMODB_ROUTES_A_02
-- the routes with a return route in the same period.
--  route_no | return_route_no | departure_airport | arrival_airport 
-- ----------+-----------------+-------------------+-----------------
--  PG0001   | PG0002          | CJC               | SCL
--  PG0004   | PG0003          | DME               | KJA
--  PG0005   | PG0006          | KJA               | SVO
--  PG0007   | PG0008          | YOW               | YUL
--  PG0009   | PG0010          | YOW               | YYZ
--  PG0012   | PG0011          | SVO               | UFA
--  PG0014   | PG0013          | DME               | UFA
--  PG0016   | PG0015          | DME               | KZN
--  PG0017   | PG0018          | KZN               | SVO
--  PG0020   | PG0019          | DEL               | LKO
-- (10 rows)
SELECT DISTINCT r1.route_no,
                r2.route_no AS return_route_no,
                r1.departure_airport,
                r1.arrival_airport
FROM routes r1
         JOIN routes r2 ON r2.departure_airport = r1.arrival_airport
    AND r2.arrival_airport = r1.departure_airport
    AND r2.validity && r1.validity
WHERE r1.departure_airport < r1.arrival_airport
ORDER BY r1.route_no, r2.route_no
LIMIT 10
;
