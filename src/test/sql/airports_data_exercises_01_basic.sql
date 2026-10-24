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
-- Exercises at the basic level on airports_data: a filter, a sort, a count or an aggregate. Each poses a question,
-- answers it, and shows what the installed data gives back.
--
-- Run the file as a whole:
--   docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/airports_data_exercises_01_basic.sql
--


-- ------------------------------------------------------------------------------------------ DEMODB-AIRPORTS_DATA-B-01
-- the airports in the Europe/Moscow time zone.
--  airport_code |                    airport_name                     |                           city                           
-- --------------+-----------------------------------------------------+----------------------------------------------------------
--  AAQ          | {"en": "Vityazevo", "ru": "Витязево"}               | {"en": "Anapa", "ru": "Анапа"}
--  AER          | {"en": "Sochi", "ru": "Сочи"}                       | {"en": "Sochi", "ru": "Сочи"}
--  AMV          | {"en": "Amderma", "ru": "Амдерма"}                  | {"en": "Amderma", "ru": "Амдерма"}
--  ARH          | {"en": "Talagi", "ru": "Талаги"}                    | {"en": "Arkhangelsk", "ru": "Архангельск"}
--  BKA          | {"en": "Bykovo", "ru": "Быково"}                    | {"en": "Moscow", "ru": "Москва"}
--  BWO          | {"en": "Balakovo", "ru": "Балаково"}                | {"en": "Balakovo", "ru": "Балаково"}
--  BZK          | {"en": "Bryansk", "ru": "Брянск"}                   | {"en": "Bryansk", "ru": "Брянск"}
--  CEE          | {"en": "Cherepovets", "ru": "Череповец"}            | {"en": "Cherepovets", "ru": "Череповец"}
--  CKL          | {"en": "Chkalovskiy", "ru": "Чкаловский"}           | {"en": "Shchyolkovo", "ru": "Щёлково"}
--  CSH          | {"en": "Solovki", "ru": "Соловки"}                  | {"en": "Solovetsky Islands", "ru": "Соловецкие острова"}
-- ... 65 rows in all.
SELECT airport_code, airport_name, city
FROM airports_data
WHERE timezone = 'Europe/Moscow'
ORDER BY airport_code
;

-- ------------------------------------------------------------------------------------------ DEMODB-AIRPORTS_DATA-B-02
-- how many airports, and how many time zones.
--  airports | timezones 
-- ----------+-----------
--      5501 |       311
-- (1 row)
SELECT COUNT(*) AS airports, COUNT(DISTINCT timezone) AS timezones
FROM airports_data
;

-- ------------------------------------------------------------------------------------------ DEMODB-AIRPORTS_DATA-B-03
-- the airports whose code starts with SV.
--  airport_code |                         airport_name                         |      timezone       
-- --------------+--------------------------------------------------------------+---------------------
--  SVA          | {"en": "Savoonga", "ru": "Савоонга"}                         | America/Anchorage
--  SVB          | {"en": "Sambava", "ru": "Самбава"}                           | Indian/Antananarivo
--  SVD          | {"en": "Argyle", "ru": "Аргайл"}                             | America/St_Vincent
--  SVG          | {"en": "Sola", "ru": "Сола"}                                 | Europe/Oslo
--  SVH          | {"en": "Statesville", "ru": "Стейтсвилл"}                    | America/New_York
--  SVI          | {"en": "Eduardo Falla Solano", "ru": "Эдуардо Фалья Солано"} | America/Bogota
--  SVJ          | {"en": "Svolvær", "ru": "Свольвер"}                          | Europe/Oslo
--  SVL          | {"en": "Savonlinna", "ru": "Савонлинна"}                     | Europe/Helsinki
--  SVN          | {"en": "Hunter", "ru": "Хантер"}                             | America/New_York
--  SVO          | {"en": "Sheremetyevo", "ru": "Шереметьево"}                  | Europe/Moscow
-- ... 16 rows in all.
SELECT airport_code, airport_name, timezone
FROM airports_data
WHERE airport_code LIKE 'SV%'
ORDER BY airport_code
;
