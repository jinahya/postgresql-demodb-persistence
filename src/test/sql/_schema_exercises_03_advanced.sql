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
-- Exercises at the advanced level on the schema as a whole, not on a single table: a subquery, a window, a self join
-- or a recursion. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/_schema_exercises_03_advanced.sql
--


-- ------------------------------------------------------------------------------------------------- DEMODB_SCHEMA_A_01
-- the five tickets with the most segments, and their itineraries.
--    ticket_no   | segments |               itinerary                
-- ---------------+----------+----------------------------------------
--  0005432000048 |        4 | DEN->HEL, HEL->SVO, SVO->FRA, FRA->LHR
--  0005432000064 |        4 | LHR->YYZ, YYZ->ORD, ORD->ATL, ATL->DEN
--  0005432000070 |        4 | DEN->HEL, HEL->SVO, SVO->FRA, FRA->LHR
--  0005432000078 |        4 | LHR->YYZ, YYZ->ORD, ORD->ATL, ATL->DEN
--  0005432000085 |        4 | YYZ->YYC, YYC->YVR, YVR->PEK, PEK->SHA
-- (5 rows)
SELECT s.ticket_no,
       COUNT(*)                                        AS segments,
       STRING_AGG(r.departure_airport || '->' || r.arrival_airport,
                  ', ' ORDER BY f.scheduled_departure) AS itinerary
FROM segments s
         JOIN flights f ON f.flight_id = s.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @ > f.scheduled_departure
GROUP BY s.ticket_no
ORDER BY segments DESC, s.ticket_no LIMIT 5
;

-- ------------------------------------------------------------------------------------------------- DEMODB_SCHEMA_A_02
-- the busiest airport of each country.
--     country     | airport_code | departures 
-- ----------------+--------------+------------
--  Germany        | FRA          |        714
--  India          | BOM          |        600
--  United States  | ATL          |        576
--  Russia         | SVO          |        569
--  China          | TFU          |        533
--  Italy          | FCO          |        466
--  Japan          | HND          |        431
--  United Kingdom | LHR          |        427
--  France         | CDG          |        414
--  Canada         | YYZ          |        402
-- (10 rows)
SELECT country, airport_code, departures
FROM (SELECT a.country,
             a.airport_code,
             COUNT(*) AS  departures,
             ROW_NUMBER() OVER (PARTITION BY a.country ORDER BY COUNT(*) DESC, a.airport_code) AS rank
      FROM timetable t
               JOIN airports a ON a.airport_code = t.departure_airport
      GROUP BY a.country, a.airport_code) x
WHERE rank = 1
ORDER BY departures DESC, country LIMIT 10
;

-- ------------------------------------------------------------------------------------------------- DEMODB_SCHEMA_A_03
-- how many airports are one and two routes away from Sheremetyevo.
--  flights | airports 
-- ---------+----------
--        0 |        1
--        1 |       16
--        2 |       49
-- (3 rows)
WITH RECURSIVE hops AS (SELECT 'SVO'::bpchar AS airport, 0 AS flights
                        UNION
                        SELECT r.arrival_airport, h.flights + 1
                        FROM hops h
                                 JOIN routes r ON r.departure_airport = h.airport
                        WHERE h.flights < 2)
SELECT flights, COUNT(DISTINCT airport) AS airports
FROM hops
GROUP BY flights
ORDER BY flights
;
