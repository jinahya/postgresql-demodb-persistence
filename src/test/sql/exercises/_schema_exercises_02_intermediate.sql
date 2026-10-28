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
-- Exercises at the intermediate level on the schema as a whole, not on a single table: a join to a neighbour, a
-- group, a jsonb field or a range. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/_schema_exercises_02_intermediate.sql
--


-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_01
-- the segments sold and the money taken, by month of flight.
--   month  | segments |    revenue    
-- ---------+----------+---------------
--  2025-10 |  1213221 | 7388546575.00
--  2025-11 |  1221803 | 7579759950.00
--  2025-12 |   996024 | 6209347975.00
--  2026-01 |   510201 | 3396277075.00
-- (4 rows)
SELECT TO_CHAR(f.scheduled_departure, 'YYYY-MM') AS month,
       COUNT(*)                                  AS segments,
       SUM(s.price)                              AS revenue
FROM segments s
         JOIN flights f
              ON f.flight_id = s.flight_id
GROUP BY month
ORDER BY month
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_02
-- the ten airports with the most passengers boarded in the first week.
--  airport_code |    airport_name     | boarded 
-- --------------+---------------------+---------
--  PVG          | Pudong              |   11468
--  ATL          | Hartsfield-Jackson  |   10666
--  DEL          | Indira Gandhi       |    9578
--  BOM          | Chhatrapati Shivaji |    8977
--  SHA          | Hongqiao            |    7982
--  SZX          | Bao'an              |    7743
--  TFU          | Tianfu              |    7653
--  FRA          | Frankfurt am Main   |    6932
--  HND          | Haneda              |    6743
--  CAN          | Baiyun              |    6668
-- (10 rows)
SELECT a.airport_code, a.airport_name, COUNT(*) AS boarded
FROM boarding_passes bp
         JOIN flights f ON f.flight_id = bp.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
         JOIN airports a ON a.airport_code = r.departure_airport
WHERE f.scheduled_departure >= '2025-10-01'
  AND f.scheduled_departure < '2025-10-08'
GROUP BY a.airport_code, a.airport_name
ORDER BY boarded DESC, a.airport_code
LIMIT 10
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_03
-- the money each airplane model took.
--  airplane_code |          model          |    revenue    
-- ---------------+-------------------------+---------------
--  77W           | Boeing 777-300ER        | 7669794850.00
--  789           | Boeing 787-9 Dreamliner | 5208898700.00
--  7M7           | Boeing 737 MAX 7        | 3421118000.00
--  351           | Airbus A350-1000        | 3288182250.00
--  339           | Airbus A330-900neo      | 2231027150.00
--  E70           | Embraer E170            | 1303822500.00
--  CR7           | Bombardier CRJ700       |  814050375.00
--  32N           | Airbus A320neo          |  637037750.00
-- (8 rows)
SELECT a.airplane_code, a.model, SUM(s.price) AS revenue
FROM segments s
         JOIN flights f ON f.flight_id = s.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
         JOIN airplanes a ON a.airplane_code = r.airplane_code
GROUP BY a.airplane_code, a.model
ORDER BY revenue DESC, a.airplane_code
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_04
-- the ten countries flown to the most.
--     country     | arrivals 
-- ----------------+----------
--  China          |     4844
--  United States  |     3500
--  India          |     3350
--  Russia         |     2311
--  Canada         |     1597
--  Germany        |     1256
--  Japan          |     1101
--  France         |     1001
--  Italy          |      890
--  United Kingdom |      760
-- (10 rows)
SELECT a.country, COUNT(*) AS arrivals
FROM timetable t
         JOIN airports a ON a.airport_code = t.arrival_airport
GROUP BY a.country
ORDER BY arrivals DESC, a.country
LIMIT 10
;
