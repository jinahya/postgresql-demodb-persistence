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
--   month  | segments |    revenue    
-- ---------+----------+---------------
--  2025-10 |  1213221 | 7388546575.00
--  2025-11 |  1221803 | 7579759950.00
--  2025-12 |   996024 | 6209347975.00
--  2026-01 |   510201 | 3396277075.00
-- (4 rows)
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
--  airport_code |    airport_name     | boarded 
-- --------------+---------------------+---------
--  PVG          | Pudong              |   11468
--  ATL          | Hartsfield-Jackson  |   10666
--  DEL          | Indira Gandhi       |    9578
--  BOM          | Chhatrapati Shivaji |    8977
--  SHA          | Hongqiao            |    7979
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
--  flight_id | arrival_airport | scheduled_departure_local | airplane_code | passengers | seats 
-- -----------+-----------------+---------------------------+---------------+------------+-------
--      11120 | UFA             | 2025-12-01 13:55:00       | E70           |         78 |    78
--      11121 | SVX             | 2025-12-01 14:05:00       | 339           |        281 |   281
--      11132 | AER             | 2025-12-01 15:05:00       | 7M7           |        160 |   160
--      11211 | KZN             | 2025-12-02 02:05:00       | CR7           |         70 |    70
--      11216 | LED             | 2025-12-02 02:40:00       | 77W           |        404 |   404
-- (5 rows)
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
--  flight_id | route_no | scheduled_departure 
-- -----------+----------+---------------------
-- (0 rows)
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
--        shortest        |     average      |         longest         
-- -----------------------+------------------+-------------------------
--  1 day 00:15:00.059124 | 32 days 29:00:00 | 60 days 00:04:37.102002
-- (1 row)
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
--  airplane_code |          model          | range | speed | seats | economy | comfort | business 
-- ---------------+-------------------------+-------+-------+-------+---------+---------+----------
--  32N           | Airbus A320neo          |  6500 |   830 |   166 |     138 |       0 |       28
--  339           | Airbus A330-900neo      | 13300 |   870 |   281 |     224 |      28 |       29
--  351           | Airbus A350-1000        | 16700 |   913 |   325 |     281 |       0 |       44
--  35X           | Airbus A350F            |  8700 |   903 |     0 |       0 |       0 |        0
--  76F           | Boeing 767-300F         |  6000 |   850 |     0 |       0 |       0 |        0
--  77W           | Boeing 777-300ER        | 14600 |   905 |   404 |     326 |      48 |       30
--  789           | Boeing 787-9 Dreamliner | 14000 |   913 |   257 |     188 |      21 |       48
--  7M7           | Boeing 737 MAX 7        |  7000 |   840 |   160 |     144 |       0 |       16
--  CR7           | Bombardier CRJ700       |  3100 |   829 |    70 |      52 |      12 |        6
--  E70           | Embraer E170            |  4000 |   800 |    78 |      72 |       0 |        6
-- (10 rows)
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
--  airport_code |        airport_name         |      timezone       | flight_id | arrival_airport | scheduled_departure_local 
-- --------------+-----------------------------+---------------------+-----------+-----------------+---------------------------
--  AER          | Sochi                       | Europe/Moscow       |     11027 | KJA             | 2025-12-01 00:35:00
--  AER          | Sochi                       | Europe/Moscow       |     11064 | SVO             | 2025-12-01 05:15:00
--  AMD          | Sardar Vallabhbhai Patel    | Asia/Kolkata        |     11066 | BOM             | 2025-12-01 07:45:00
--  AMD          | Sardar Vallabhbhai Patel    | Asia/Kolkata        |     11127 | DEL             | 2025-12-01 17:00:00
--  ARN          | Arlanda                     | Europe/Stockholm    |     11076 | LGW             | 2025-12-01 05:30:00
--  ATL          | Hartsfield-Jackson          | America/New_York    |     10941 | LAX             | 2025-11-30 05:00:00
--  ATL          | Hartsfield-Jackson          | America/New_York    |     10942 | MIA             | 2025-11-30 05:00:00
--  ATL          | Hartsfield-Jackson          | America/New_York    |     11053 | CLT             | 2025-11-30 19:35:00
--  ATL          | Hartsfield-Jackson          | America/New_York    |     11057 | DEN             | 2025-11-30 20:20:00
--  BER          | Brandenburg                 | Europe/Berlin       |     11129 | FRA             | 2025-12-01 12:45:00
-- ... 169 rows in all.
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
--  book_ref |           book_date           |   passenger_id   | outbound_ticket | return_ticket 
-- ----------+-------------------------------+------------------+-----------------+---------------
--  0000EG   | 2025-11-16 21:21:48.411495+00 | CA 1985281597520 | 0005434564147   | 0005434564152
--  00040W   | 2025-10-13 09:50:50.314587+00 | IN 2849974177127 | 0005433488169   | 0005433488174
--  000562   | 2025-09-11 10:21:54.084444+00 | CN 8124086130640 | 0005432379163   | 0005432379166
--  000562   | 2025-09-11 10:21:54.084444+00 | CN 8278780386895 | 0005432379152   | 0005432379162
--  000AB9   | 2025-09-14 09:38:24.862062+00 | FR 9136453193331 | 0005432484130   | 0005432484139
--  000CTL   | 2025-09-12 03:00:50.050639+00 | CA 1858375121919 | 0005432403569   | 0005432403579
--  000CTL   | 2025-09-12 03:00:50.050639+00 | CA 4214750109906 | 0005432403583   | 0005432403584
--  000FHR   | 2025-09-20 01:23:24.977241+00 | US 6852803198732 | 0005432684628   | 0005432684637
--  000J03   | 2025-09-21 10:36:16.247585+00 | DE 5115949846908 | 0005432733791   | 0005432733800
--  000J03   | 2025-09-21 10:36:16.247585+00 | DE 9841850206654 | 0005432733806   | 0005432733808
-- ... 1168851 rows in all.
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
