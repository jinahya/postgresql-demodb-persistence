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
-- Exercises at the basic level on tickets: a filter, a sort, a count or an aggregate. Each poses a question, answers
-- it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/tickets_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------------ DEMODB-TICKETS-B-01
-- the tickets of the booking 0000EG.
--    ticket_no   | book_ref |   passenger_id   | passenger_name | outbound 
-- ---------------+----------+------------------+----------------+----------
--  0005434564147 | 0000EG   | CA 1985281597520 | John Beland    | t
--  0005434564152 | 0000EG   | CA 1985281597520 | John Beland    | f
-- (2 rows)
SELECT *
FROM tickets
WHERE book_ref = '0000EG'
ORDER BY ticket_no
;

-- ------------------------------------------------------------------------------------------------ DEMODB-TICKETS-B-02
-- how many tickets, outbound and return.
--  outbound | tickets 
-- ----------+---------
--  f        | 1168851
--  t        | 1805086
-- (2 rows)
SELECT outbound, COUNT(*) AS tickets
FROM tickets
GROUP BY outbound
ORDER BY outbound
;

-- ------------------------------------------------------------------------------------------------ DEMODB-TICKETS-B-03
-- the tickets of one passenger.
--    ticket_no   | book_ref |   passenger_id   |  passenger_name  | outbound 
-- ---------------+----------+------------------+------------------+----------
--  0005432000000 | RBUUIQ   | IT 2425980678984 | Ilaria Cazzaniga | t
--  0005432000003 | RBUUIQ   | IT 2425980678984 | Ilaria Cazzaniga | f
-- (2 rows)
SELECT *
FROM tickets
WHERE passenger_id = 'IT 2425980678984'
ORDER BY ticket_no
;
