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
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql-exercises/__/02_intermediate.sql
--


-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_01
-- the segments sold and the money taken, by month of flight.
-- NOT YET RUN
SELECT TO_CHAR(f.scheduled_departure AT TIME ZONE 'UTC', 'YYYY-MM') AS month,
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
-- NOT YET RUN
SELECT a.airport_code, a.airport_name, COUNT(*) AS boarded
FROM boarding_passes bp
         JOIN flights f ON f.flight_id = bp.flight_id
         JOIN routes r ON r.route_no = f.route_no AND r.validity @> f.scheduled_departure
         JOIN airports a ON a.airport_code = r.departure_airport
WHERE f.scheduled_departure >= TIMESTAMPTZ '2025-10-01 00:00:00+00'
  AND f.scheduled_departure < TIMESTAMPTZ '2025-10-08 00:00:00+00'
  AND bp.boarding_no IS NOT NULL
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

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_05
-- the passengers and the seats of the flights from Sheremetyevo in the next 24 hours.
-- NOT YET RUN
SELECT t.flight_id,
       t.arrival_airport,
       t.scheduled_departure_local,
       t.airplane_code,
       COUNT(s.ticket_no)                                                       AS passengers,
       (SELECT COUNT(*) FROM seats st WHERE st.airplane_code = t.airplane_code) AS seats
FROM timetable t
         LEFT JOIN segments s ON s.flight_id = t.flight_id
WHERE t.departure_airport = 'SVO'
  AND t.scheduled_departure >= bookings.now()
  AND t.scheduled_departure < bookings.now() + INTERVAL '24 hours'
GROUP BY t.flight_id, t.arrival_airport, t.scheduled_departure, t.scheduled_departure_local, t.airplane_code
ORDER BY t.scheduled_departure, t.flight_id
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_06
-- the flights with no route valid at their departure.
-- NOT YET RUN
SELECT f.flight_id, f.route_no, f.scheduled_departure
FROM flights f
WHERE NOT EXISTS (SELECT 1
                  FROM routes r
                  WHERE r.route_no = f.route_no
                    AND r.validity @> f.scheduled_departure)
ORDER BY f.scheduled_departure, f.flight_id
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_07
-- how far ahead the bookings were made.
-- NOT YET RUN
SELECT MIN(ahead)                     AS shortest,
       DATE_TRUNC('hour', AVG(ahead)) AS average,
       MAX(ahead)                     AS longest
FROM (SELECT b.book_ref, MIN(f.scheduled_departure) - b.book_date AS ahead
      FROM bookings b
               JOIN tickets t ON t.book_ref = b.book_ref
               JOIN segments s ON s.ticket_no = t.ticket_no
               JOIN flights f ON f.flight_id = s.flight_id
      GROUP BY b.book_ref, b.book_date) x
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_08
-- the seats of each airplane model, by fare conditions.
-- NOT YET RUN
SELECT a.airplane_code,
       a.model,
       a.range,
       a.speed,
       COUNT(s.seat_no)                                                 AS seats,
       COUNT(s.seat_no) FILTER (WHERE s.fare_conditions = 'Economy')  AS economy,
       COUNT(s.seat_no) FILTER (WHERE s.fare_conditions = 'Comfort')  AS comfort,
       COUNT(s.seat_no) FILTER (WHERE s.fare_conditions = 'Business') AS business
FROM airplanes a
         LEFT JOIN seats s ON s.airplane_code = a.airplane_code
GROUP BY a.airplane_code, a.model, a.range, a.speed
ORDER BY a.airplane_code
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_09
-- today's departures, in each airport's own day.
-- NOT YET RUN
SELECT a.airport_code,
       a.airport_name,
       a.timezone,
       t.flight_id,
       t.arrival_airport,
       t.scheduled_departure_local
FROM timetable t
         JOIN airports a ON a.airport_code = t.departure_airport
WHERE t.scheduled_departure_local::date = (bookings.now() AT TIME ZONE a.timezone)::date
ORDER BY a.airport_code, t.scheduled_departure_local, t.flight_id
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_I_10
-- the outbound and return tickets of one passenger in one booking.
-- NOT YET RUN
SELECT b.book_ref,
       b.book_date,
       o.passenger_id,
       o.ticket_no AS outbound_ticket,
       i.ticket_no AS return_ticket
FROM bookings b
         JOIN tickets o ON o.book_ref = b.book_ref AND o.outbound
         JOIN tickets i ON i.book_ref = o.book_ref AND i.passenger_id = o.passenger_id AND NOT i.outbound
ORDER BY b.book_ref, o.passenger_id
;
