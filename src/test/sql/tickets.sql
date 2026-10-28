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
-- Profiling queries on tickets, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/tickets.sql
--


-- --------------------------------------------------------------------------------------- ticket_no
-- how many rows, and the smallest and the largest ticket_no; 2973937 tickets, 0005432000000 to 0005434990767.
SELECT COUNT(*), MIN(ticket_no), MAX(ticket_no)
FROM tickets
;

-- a ticket_no which is not thirteen digits; the column is text, and the numbers are the airline's.
-- no rows: every number is thirteen digits.
SELECT ticket_no
FROM tickets
WHERE ticket_no !~ '^[0-9]{13}$'
;


-- ---------------------------------------------------------------------------------------- book_ref
-- a book_ref which refers to no booking; the foreign key forbids it.
-- no rows: every book_ref refers to a booking.
SELECT t.ticket_no, t.book_ref
FROM tickets t
WHERE NOT EXISTS (SELECT 1
                  FROM bookings b
                  WHERE b.book_ref = t.book_ref)
;


-- ------------------------------------------------------------------------------------ passenger_id
-- a passenger_id which is not a two-letter country code, a space and digits; 16 characters, every one.
-- no rows: every id has that shape.
SELECT ticket_no, passenger_id
FROM tickets
WHERE passenger_id !~ '^[A-Z]{2} [0-9]+$'
;

-- the countries with the most passengers' tickets; CN has 772440.
SELECT SUBSTR(passenger_id, 1, 2) AS country, COUNT(*) AS tickets
FROM tickets
GROUP BY SUBSTR(passenger_id, 1, 2)
ORDER BY tickets DESC, country ASC
LIMIT 5
;

-- how many distinct passengers; 1797400, so many fly more than once.
SELECT COUNT(DISTINCT passenger_id)
FROM tickets
;


-- ---------------------------------------------------------------------------------- passenger_name
-- the shortest and the longest passenger_name; 5 and 27 characters.
SELECT MIN(LENGTH(passenger_name)), MAX(LENGTH(passenger_name))
FROM tickets
;

-- a passenger_name which is blank, or carries leading or trailing spaces.
-- no rows: every name is trimmed and not blank.
SELECT ticket_no, '[' || passenger_name || ']' AS passenger_name
FROM tickets
WHERE TRIM(passenger_name) = ''
   OR LENGTH(passenger_name) <> LENGTH(TRIM(passenger_name))
;

-- a passenger_id under more than one name.
-- no rows: a passenger has one name.
SELECT passenger_id, COUNT(DISTINCT passenger_name) AS names
FROM tickets
GROUP BY passenger_id
HAVING COUNT(DISTINCT passenger_name) > 1
;


-- ---------------------------------------------------------------------------------------- outbound
-- how many tickets each way; 1805086 outbound, 1168851 return.
SELECT outbound, COUNT(*) AS tickets
FROM tickets
GROUP BY outbound
ORDER BY outbound
;

-- a passenger with more than two tickets on a booking; the unique key on (book_ref, passenger_id, outbound) forbids it.
-- no rows: at most one each way.
SELECT book_ref, passenger_id, COUNT(*) AS tickets
FROM tickets
GROUP BY book_ref, passenger_id
HAVING COUNT(*) > 2
;


-- -------------------------------------------------------------------------------------------------
-- a ticket with no segment.
-- no rows: every ticket has at least one.
SELECT t.ticket_no
FROM tickets t
WHERE NOT EXISTS (SELECT 1
                  FROM segments s
                  WHERE s.ticket_no = t.ticket_no)
;

-- the fewest and the most segments on a ticket; 1 to 4.
SELECT MIN(segments), MAX(segments)
FROM (SELECT COUNT(*) AS segments
      FROM segments
      GROUP BY ticket_no) x
;
