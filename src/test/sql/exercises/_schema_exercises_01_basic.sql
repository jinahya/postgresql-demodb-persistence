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
-- Exercises at the basic level on the schema as a whole, not on a single table: a filter, a sort, a count or an
-- aggregate. Each poses a question, answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/_schema_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_B_01
-- the tables and the views of the schema.
--    table_name    | table_type 
-- -----------------+------------
--  airplanes_data  | BASE TABLE
--  airports_data   | BASE TABLE
--  boarding_passes | BASE TABLE
--  bookings        | BASE TABLE
--  flights         | BASE TABLE
--  routes          | BASE TABLE
--  seats           | BASE TABLE
--  segments        | BASE TABLE
--  tickets         | BASE TABLE
--  airplanes       | VIEW
-- ... 12 rows in all.
SELECT table_name, table_type
FROM information_schema.tables
WHERE table_schema = 'bookings'
ORDER BY table_type, table_name
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_B_02
-- the columns of tickets.
--   column_name   | data_type | is_nullable 
-- ----------------+-----------+-------------
--  ticket_no      | text      | NO
--  book_ref       | character | NO
--  passenger_id   | text      | NO
--  passenger_name | text      | NO
--  outbound       | boolean   | NO
-- (5 rows)
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'bookings'
  AND table_name = 'tickets'
ORDER BY ordinal_position
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_B_03
-- the foreign keys, and what they reference.
--    table_name    |                 conname                  |                                       definition                                       
-- -----------------+------------------------------------------+----------------------------------------------------------------------------------------
--  boarding_passes | boarding_passes_ticket_no_flight_id_fkey | FOREIGN KEY (ticket_no, flight_id) REFERENCES segments(ticket_no, flight_id)
--  routes          | routes_airplane_code_fkey                | FOREIGN KEY (airplane_code) REFERENCES airplanes_data(airplane_code)
--  routes          | routes_arrival_airport_fkey              | FOREIGN KEY (arrival_airport) REFERENCES airports_data(airport_code)
--  routes          | routes_departure_airport_fkey            | FOREIGN KEY (departure_airport) REFERENCES airports_data(airport_code)
--  seats           | seats_airplane_code_fkey                 | FOREIGN KEY (airplane_code) REFERENCES airplanes_data(airplane_code) ON DELETE CASCADE
--  segments        | segments_flight_id_fkey                  | FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
--  segments        | segments_ticket_no_fkey                  | FOREIGN KEY (ticket_no) REFERENCES tickets(ticket_no)
--  tickets         | tickets_book_ref_fkey                    | FOREIGN KEY (book_ref) REFERENCES bookings(book_ref)
-- (8 rows)
SELECT conrelid::regclass AS table_name, conname, PG_GET_CONSTRAINTDEF(oid) AS definition
FROM pg_constraint
WHERE connamespace = 'bookings'::regnamespace
  AND contype = 'f'
ORDER BY table_name, conname
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_B_04
-- the functions of the schema.
--  routine_name |        data_type         
-- --------------+--------------------------
--  lang         | text
--  now          | timestamp with time zone
--  version      | text
-- (3 rows)
SELECT routine_name, data_type
FROM information_schema.routines
WHERE routine_schema = 'bookings'
ORDER BY routine_name
;

-- ------------------------------------------------------------------------------ DEMODB_SCHEMA_B_05
-- how many rows each table has.
--    table_name    |  rows   
-- -----------------+---------
--  airplanes_data  |      10
--  airports_data   |    5501
--  boarding_passes | 2463832
--  bookings        | 1292893
--  flights         |   21758
--  routes          |    1162
--  seats           |    1741
--  segments        | 3941249
--  tickets         | 2973937
-- (9 rows)
SELECT 'airplanes_data' AS table_name, COUNT(*) AS rows
FROM airplanes_data
UNION ALL
SELECT 'airports_data', COUNT(*)
FROM airports_data
UNION ALL
SELECT 'boarding_passes', COUNT(*)
FROM boarding_passes
UNION ALL
SELECT 'bookings', COUNT(*)
FROM bookings
UNION ALL
SELECT 'flights', COUNT(*)
FROM flights
UNION ALL
SELECT 'routes', COUNT(*)
FROM routes
UNION ALL
SELECT 'seats', COUNT(*)
FROM seats
UNION ALL
SELECT 'segments', COUNT(*)
FROM segments
UNION ALL
SELECT 'tickets', COUNT(*)
FROM tickets
ORDER BY table_name
;
