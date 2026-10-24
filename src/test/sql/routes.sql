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
-- Profiling queries on routes, a section per column. Each says what it looks for and what the result should be;
-- 'no rows' marks a check that passes when nothing comes back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/routes.sql
--


-- -------------------------------------------------------------------------------------------------- route_no, validity
-- how many rows, how many route numbers, and the smallest and the largest; 1162 rows of 704 routes, PG0001 to PG0704.
-- The table has no primary key: a route is a route_no with a validity period, and an exclusion constraint keeps a
-- number's periods from overlapping.
SELECT COUNT(*), COUNT(DISTINCT route_no), MIN(route_no), MAX(route_no)
FROM routes
;

-- a route_no which is not PG and four digits.
-- no rows: every number has that shape.
SELECT route_no
FROM routes
WHERE route_no !~ '^PG[0-9]{4}$'
;

-- how many periods a route has; 430 routes have one, 144 two, 76 three, 54 all four.
SELECT periods, COUNT(*) AS routes
FROM (SELECT route_no, COUNT(*) AS periods
      FROM routes
      GROUP BY route_no) x
GROUP BY periods
ORDER BY periods
;

-- the periods, and how many routes in each; four calendar months, 2025-10 to 2026-01, 288 to 292 routes a month.
SELECT validity, COUNT(*) AS routes
FROM routes
GROUP BY validity
ORDER BY validity
;

-- two rows of a route whose periods overlap; the exclusion constraint forbids it.
-- no rows: a route's periods are apart.
SELECT a.route_no, a.validity, b.validity
FROM routes a
         JOIN routes b
              ON b.route_no = a.route_no AND b.validity <> a.validity AND b.validity && a.validity
;

-- a period which is not closed at its start and open at its end, or unbounded.
-- no rows: every period is [start,end).
SELECT route_no, validity
FROM routes
WHERE LOWER(validity) IS NULL
   OR UPPER(validity) IS NULL
   OR NOT LOWER_INC(validity)
   OR UPPER_INC(validity)
;


-- ---------------------------------------------------------------------------------- departure_airport, arrival_airport
-- an airport which refers to no airports_data row; the foreign keys forbid it.
-- no rows: every airport is there.
SELECT route_no, validity, departure_airport, arrival_airport
FROM routes r
WHERE NOT EXISTS (SELECT 1
                  FROM airports_data a
                  WHERE a.airport_code = r.departure_airport)
   OR NOT EXISTS (SELECT 1
                  FROM airports_data a
                  WHERE a.airport_code = r.arrival_airport)
;

-- a route which departs from the airport it arrives at.
-- no rows: every route goes somewhere.
SELECT route_no, validity, departure_airport
FROM routes
WHERE departure_airport = arrival_airport
;

-- how many airports the routes depart from and arrive at; 73 and 73, the same airports.
SELECT COUNT(DISTINCT departure_airport), COUNT(DISTINCT arrival_airport)
FROM routes
;

-- the airports with the most routes departing; LHR has 18.
SELECT departure_airport, COUNT(DISTINCT route_no) AS routes
FROM routes
GROUP BY departure_airport
ORDER BY routes DESC, departure_airport ASC
LIMIT 5
;

-- a pair of airports flown one way only.
-- no rows: every route has a route back.
SELECT p.departure_airport, p.arrival_airport
FROM (SELECT DISTINCT departure_airport, arrival_airport
      FROM routes) p
WHERE NOT EXISTS (SELECT 1
                  FROM routes r
                  WHERE r.departure_airport = p.arrival_airport
                    AND r.arrival_airport = p.departure_airport)
;


-- ------------------------------------------------------------------------------------------------------- airplane_code
-- an airplane_code which refers to no airplane; the foreign key forbids it.
-- no rows: every airplane is there.
SELECT route_no, validity, airplane_code
FROM routes r
WHERE NOT EXISTS (SELECT 1
                  FROM airplanes_data a
                  WHERE a.airplane_code = r.airplane_code)
;

-- the airplanes flying the most routes; 789 flies 208.
SELECT airplane_code, COUNT(DISTINCT route_no) AS routes
FROM routes
GROUP BY airplane_code
ORDER BY routes DESC, airplane_code ASC
LIMIT 5
;


-- -------------------------------------------------------------------------------------------------------- days_of_week
-- days_of_week is an integer[]; the fewest and the most days a route flies; 1 to 7.
SELECT MIN(CARDINALITY(days_of_week)), MAX(CARDINALITY(days_of_week))
FROM routes
;

-- a day outside 1 to 7, a day listed twice, or days out of order.
-- no rows: every array is ascending days of the week, each once.
SELECT route_no, validity, days_of_week
FROM routes r
WHERE EXISTS (SELECT 1
              FROM UNNEST(r.days_of_week) d
              WHERE d NOT BETWEEN 1 AND 7)
   OR (SELECT COUNT(DISTINCT d) FROM UNNEST(r.days_of_week) d) <> CARDINALITY(r.days_of_week)
   OR days_of_week <> (SELECT ARRAY_AGG(d ORDER BY d) FROM UNNEST(r.days_of_week) d)
;

-- how many days a week the routes fly; 288 rows fly daily, 216 one day a week.
SELECT CARDINALITY(days_of_week) AS days, COUNT(*) AS routes
FROM routes
GROUP BY CARDINALITY(days_of_week)
ORDER BY days
;


-- ------------------------------------------------------------------------------------------------------ scheduled_time
-- the earliest and the latest scheduled_time, the local time of departure; 00:00 to 23:55.
SELECT MIN(scheduled_time), MAX(scheduled_time)
FROM routes
;

-- a scheduled_time not on a five-minute mark.
-- no rows: every departure is on one.
SELECT route_no, validity, scheduled_time
FROM routes
WHERE EXTRACT(SECOND FROM scheduled_time) <> 0
   OR EXTRACT(MINUTE FROM scheduled_time)::int % 5 <> 0
;


-- ------------------------------------------------------------------------------------------------------------ duration
-- duration is an interval; the shortest and the longest; 35 minutes and 19 hours 55 minutes.
SELECT MIN(duration), MAX(duration)
FROM routes
;

-- a duration which is not a positive whole number of minutes.
-- no rows: every duration is.
SELECT route_no, validity, duration
FROM routes
WHERE duration <= INTERVAL '0'
   OR EXTRACT(SECOND FROM duration) <> 0
;
