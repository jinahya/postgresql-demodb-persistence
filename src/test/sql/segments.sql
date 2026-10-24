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
-- Profiling queries on segments, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/segments.sql
--


-- ------------------------------------------------------------------------------------------------ ticket_no, flight_id
-- how many rows, of how many tickets and flights; 3941249 segments, every one of the 2973937 tickets, 20793 flights.
SELECT COUNT(*), COUNT(DISTINCT ticket_no), COUNT(DISTINCT flight_id)
FROM segments
;

-- a ticket_no or a flight_id which refers to nothing; the foreign keys forbid it.
-- no rows: every segment has its ticket and its flight.
SELECT s.ticket_no, s.flight_id
FROM segments s
WHERE NOT EXISTS (SELECT 1
                  FROM tickets t
                  WHERE t.ticket_no = s.ticket_no)
   OR NOT EXISTS (SELECT 1
                  FROM flights f
                  WHERE f.flight_id = s.flight_id)
;

-- the fewest and the most segments on a flight; 1 to 404, the largest airplane's seats.
SELECT MIN(segments), MAX(segments)
FROM (SELECT COUNT(*) AS segments
      FROM segments
      GROUP BY flight_id) x
;


-- ----------------------------------------------------------------------------------------------------- fare_conditions
-- the classes, how many segments in each, and the prices; a check constraint allows these three.
-- Economy 3221773, 1750.00 to 59750.00; Business 417644, 3500.00 to 119500.00; Comfort 301832, 2275.00 to 65325.00.
SELECT fare_conditions, COUNT(*) AS segments, MIN(price), MAX(price)
FROM segments
GROUP BY fare_conditions
ORDER BY segments DESC
;

-- a segment in a class its flight's airplane has no seat of; the airplane is the route's at the scheduled departure.
-- no rows: every class sold is on board.
SELECT s.ticket_no, s.flight_id, s.fare_conditions, r.airplane_code
FROM segments s
         JOIN flights f ON f.flight_id = s.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
WHERE NOT EXISTS (SELECT 1
                  FROM seats st
                  WHERE st.airplane_code = r.airplane_code
                    AND st.fare_conditions = s.fare_conditions)
;


-- --------------------------------------------------------------------------------------------------------------- price
-- a price which is not a positive whole number of currency units; a check constraint keeps it from being negative.
-- no rows: every price is positive, with a zero fraction.
SELECT ticket_no, flight_id, price
FROM segments
WHERE price <= 0
   OR price <> ROUND(price, 0)
;
