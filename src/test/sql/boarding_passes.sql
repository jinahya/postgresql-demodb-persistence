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
-- Profiling queries on boarding_passes, a section per column. Each says what it looks for and what the result
-- should be; 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/boarding_passes.sql
--


-- ------------------------------------------------------------------------------------------------ ticket_no, flight_id
-- how many rows, and of how many flights; 2463832 passes on 11445 flights.
SELECT COUNT(*), COUNT(DISTINCT flight_id)
FROM boarding_passes
;

-- a pass whose (ticket_no, flight_id) is no segment; the foreign key forbids it.
-- no rows: every pass boards a segment.
SELECT b.ticket_no, b.flight_id
FROM boarding_passes b
WHERE NOT EXISTS (SELECT 1
                  FROM segments s
                  WHERE s.ticket_no = b.ticket_no
                    AND s.flight_id = b.flight_id)
;

-- how many segments of each flight status have a pass; every Arrived, Departed and Boarding segment, some of the
-- Delayed, On Time and Scheduled ones, 5962 of the 1465784 Scheduled.
SELECT f.status, COUNT(*) AS segments, COUNT(b.ticket_no) AS passes
FROM segments s
         JOIN flights f ON f.flight_id = s.flight_id
         LEFT JOIN boarding_passes b ON b.ticket_no = s.ticket_no AND b.flight_id = s.flight_id
GROUP BY f.status
ORDER BY f.status
;


-- ------------------------------------------------------------------------------------------------------------- seat_no
-- a seat_no which is not a row number and a letter, A to L without I.
-- no rows: every seat is one.
SELECT ticket_no, flight_id, seat_no
FROM boarding_passes
WHERE seat_no !~ '^[0-9]{1,2}[A-HJ-L]$'
;

-- a pass for a seat the flight's airplane does not have; the airplane is the route's at the scheduled departure.
-- no rows: every seat is on board.
SELECT b.ticket_no, b.flight_id, b.seat_no, r.airplane_code
FROM boarding_passes b
         JOIN flights f ON f.flight_id = b.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @ > f.scheduled_departure
WHERE NOT EXISTS (SELECT 1
                  FROM seats s
                  WHERE s.airplane_code = r.airplane_code
                    AND s.seat_no = b.seat_no)
;


-- ------------------------------------------------------------------------------------------ boarding_no, boarding_time
-- the two are nullable, and go together; how many passes have neither, and how many one without the other.
-- 30360 have neither, none has one of the two.
SELECT COUNT(*) FILTER (WHERE boarding_no IS NULL)                              AS no_number,
       COUNT(*) FILTER (WHERE boarding_time IS NULL)                            AS no_time,
       COUNT(*) FILTER (WHERE (boarding_no IS NULL) <> (boarding_time IS NULL)) AS one_of_two
FROM boarding_passes
;

-- the smallest and the largest boarding_no; 1 to 404, the largest airplane's seats.
SELECT MIN(boarding_no), MAX(boarding_no)
FROM boarding_passes
;

-- a flight whose numbers do not run from 1 to the number of passes; the unique key on (flight_id, boarding_no) keeps
-- them apart, and the data numbers them without a gap.
-- no rows: every flight's numbers are 1 to n.
SELECT flight_id, COUNT(boarding_no) AS passes, MAX(boarding_no) AS largest
FROM boarding_passes
GROUP BY flight_id
HAVING MAX(boarding_no) <> COUNT(boarding_no)
;

-- the earliest and the latest boarding_time; 2025-09-30 23:32 to 2025-11-30 23:59.
SELECT MIN(boarding_time), MAX(boarding_time)
FROM boarding_passes
;

-- a boarding after the flight left, or more than a day before it was scheduled to.
-- no rows: every boarding is in the day before the departure.
SELECT b.ticket_no, b.flight_id, b.boarding_time, f.scheduled_departure, f.actual_departure
FROM boarding_passes b
         JOIN flights f ON f.flight_id = b.flight_id
WHERE b.boarding_time >= COALESCE(f.actual_departure, f.scheduled_departure)
   OR b.boarding_time < f.scheduled_departure - INTERVAL '1 day'
;
