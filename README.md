# postgres-demonstration-persistence

Jakarta Persistence mappings for the [PostgreSQL Demonstration Database](https://postgrespro.com/community/demodb)
(Airlines; database `demo`, schema `bookings`).

Sibling of [mysql-sakila-persistence](https://github.com/jinahya/mysql-sakila-persistence).

## The database

```shell
$ ./_docker_compose_up.sh
```

Brings up [`onacit/postgresql-demodb:18-alpine-3m`](https://hub.docker.com/r/onacit/postgresql-demodb) on
`localhost:5432`, as `postgres` / `password`, and follows its log; Ctrl-C takes it down again. That is the official
`postgres:18-alpine` image with the demonstration database dump baked in, loaded on first start; the smallest tag,
about 260 MB to pull. Every run starts from an empty cluster, so loading happens every time, about a minute; the
container is `healthy` once `demo` is there.

The tag is `<postgres>-<base>-<edition>`: `3m` is three months of flights, the smallest edition; `6m`, `1y` and `2y`
are the bigger ones, and leaving out `alpine` gives the Debian-based image. The dump itself is
https://edu.postgrespro.ru/demo-20250901-3m.sql.gz, unchanged (see
[postgresql-demodb-docker](https://github.com/jinahya/postgresql-demodb-docker)).

```shell
$ docker exec -it postgres-demonstration psql -U postgres -d demo
```

[`src/test/sql`](src/test/sql) holds a profiling script per table and view, a section per column; each query says
what it looks for and what the installed data gives back, and `no rows` marks a check which passes when nothing comes
back. The `*_exercises_*.sql` files under [`src/test/sql/exercises`](src/test/sql/exercises) are the exercises of
[`EXERCISES.asciidoc`](EXERCISES.asciidoc).

```shell
$ docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/routes.sql
```

The same stack brings up [pgAdmin](https://hub.docker.com/r/dpage/pgadmin4) on http://localhost:5050, login
`postgres@example.com` / `password` (pgAdmin wants an e-mail address). The demo server is already registered
there and connects on its own; see the `configs` at the bottom of `compose.yaml`.

## The diagrams

`docs/erd/` holds the entity-relationship diagrams, SVGs from Graphviz in the style of `mysql-sakila-persistence`,
one per table and view, three groups, and the whole schema; a table shows its key columns, `PK` and `FK`, and
`routes`, which has no primary key, the pair its exclusion constraint keeps apart.

| File | What it shows |
|---|---|
| `<table>.svg` | the table, the tables it references and the tables referencing it, and the foreign keys among them |
| `<view>.svg` | the view, each column with the table it reads, and the tables it reads |
| `_airplanes_and_airports.svg`, `_routes_and_flights.svg`, `_bookings.svg` | a group of tables and the foreign keys between them only; the header comment names the group |
| `__demo.svg` | the whole schema as the [official diagram](https://postgrespro.com/community/demodb) draws it: every table, each view as an enclosure around the tables it reads, the foreign keys, and, dashed, the join of `flights` to `routes` on `route_no` and the validity period, which no foreign key declares |

## The column types

The schema uses five PostgreSQL types Jakarta Persistence has no basic type for: `integer[]` (`routes.days_of_week`),
`interval` (`routes.duration`), `tstzrange` (`routes.validity`), `point` (`airports_data.coordinates`) and `jsonb`
(`airplanes_data.model`, `airports_data.airport_name`, `city`, `country`). Hibernate ORM is the only provider, so
each is mapped with what maps it best: Hibernate's own types, or a converter.

| PostgreSQL | Columns | Attribute | How |
|---|---|---|---|
| `integer[]` | `routes.days_of_week` | `List<Integer>` | Hibernate's, `@JdbcTypeCode(SqlTypes.ARRAY)` |
| `interval` | `routes.duration` | `Duration` | Hibernate's, `@JdbcTypeCode(SqlTypes.INTERVAL_SECOND)` |
| `tstzrange` | `routes.validity` | `Instant[]`, the lower and the upper bound | `MappedRouteId.ValidityConverter`, through the text `["lower","upper")` |
| `point` | `airports_data.coordinates`, the `airports` view's | `List<Double>`, the longitude and the latitude | `MappedAirportData.CoordinatesAttributeConverter`, through the text `(x,y)` |
| `jsonb` | `airplanes_data.model`, `airports_data.airport_name`, `city`, `country` | `JsonObject` | `JsonpStringAttributeConverter.OfJsonObject`, through the JSON text |

What Hibernate does with each of the five, mapped by a plain `@Column` and no converter, measured against the database
above with Hibernate ORM 7.4.12.Final and PostgreSQL JDBC 42.7.14, a row read and the column updated and rolled back;
"with cast" means the write works only with `stringtype=unspecified` on the JDBC URL, which makes the driver send a
string untyped so that the server casts it:

| Column | Attribute type | Hibernate ORM |
|---|---|---|
| `integer[]` | `Integer[]`, `int[]`, `List<Integer>` | reads as declared; writes natively, no cast needed |
| `integer[]` | `String` | reads `{"4","7"}`, the elements quoted, not the server's literal; writes with cast |
| `interval` | `Duration` | fails both ways without `INTERVAL_SECOND`: a `Duration` is a number to it, `Bad value for type BigDecimal : 05:05:00` |
| `interval` | `String` | reads `05:05:00`, the server's text; writes with cast |
| `tstzrange` | `String` | reads the server's text, `["2025-10-01 09:00:00+09","2025-11-01 09:00:00+09")`; writes with cast |
| `point` | `String` | reads `(-145.51,-17.3526)`; writes with cast |
| `jsonb` | `String` | reads the JSON text; writes with cast |

And what it hands over before any mapping is applied, read through a native query: what the driver returns, but for
`jsonb`, which it reads as text, and the date and time types, which it reads as `java.time`.

| Column | PostgreSQL type | Hibernate ORM |
|---|---|---|
| `routes.days_of_week` | `integer[]` | `Integer[]` |
| `routes.duration` | `interval` | `org.postgresql.util.PGInterval` |
| `routes.validity` | `tstzrange` | `org.postgresql.util.PGobject` |
| `airports_data.coordinates` | `point` | `org.postgresql.geometric.PGpoint` |
| `airports_data.airport_name` | `jsonb` | `String` |
| `routes.scheduled_time` | `time` | `java.time.LocalTime` |
| `bookings.book_date` | `timestamptz` | `java.time.Instant` |
| `airports_data.airport_code` | `character(3)` | `String` |
| `bookings.total_amount` | `numeric(10,2)` | `BigDecimal` |
| `tickets.outbound` | `boolean` | `Boolean` |

Until this module dropped EclipseLink, a second provider shaped these mappings: it maps no array, and hands a converter
the driver's own object whatever the converter declares. Its measurements, and the converters it called for, are in
this file as it was at `9ef856e`, before the provider went.

What else this module does:

- `integer[]` is a `List<Integer>` and `interval` a `Duration`, each with `@JdbcTypeCode`: Hibernate maps a typed
  array or collection of a basic type to a SQL array since 6.1, and a `Duration` to an `interval` since 6.2, the
  latter for `SqlTypes.INTERVAL_SECOND` only ([migration guide](https://docs.hibernate.org/orm/6.1/migration-guide/)).
  A `Duration` has no months; every duration of the schema is hours and minutes, 35 minutes to 19 hours 55 minutes.
- `tstzrange` is an `Instant[]` of two, the lower bound, inclusive, and the upper bound, exclusive; neither Jakarta
  Persistence nor Hibernate gives the type a Java type. Every period the database holds is of that shape,
  `[lower,upper)`, so `MappedRouteId.ValidityConverter`, a plain `AttributeConverter<Instant[], String>`, converts that
  shape only, and rejects any other -- `empty`, an unbounded or an infinite bound -- rather than read it as a
  different range. A bound reads back in the session's time zone, `+09` in Seoul, and is read as an instant, so
  neither the attribute nor the identifier, of which it is part, depends on the zone the JVM runs in; the
  converter writes UTC, in the server's own text, which it has to read back too: Hibernate copies an array, a mutable
  value to it, through the converter both ways. The write needs `stringtype=unspecified` on the JDBC URL.
- `point` holds an airport's geolocation, the longitude as `x` and the latitude as `y`, as the columns' comments say;
  `MappedAirportData.CoordinatesAttributeConverter`, a plain `AttributeConverter<List<Double>, String>`, prints `(longitude,latitude)` and reads the
  text as the server does. No provider maps a native `point`: Hibernate Spatial and geolatte need PostGIS `geometry`,
  which the database does not have.
- Each `jsonb` column, an object of one name per language, is mapped as a JSON-P `JsonObject` through
  `JsonpStringAttributeConverter.OfJsonObject`; the attributes have package-private accessors, and are read and
  written a name at a time, by the language of a `Locale`. `JsonpStringAttributeConverter`, from
  `jinahya-persistence-more`, has a converter for each JSON-P type, `OfJsonValue`, `OfJsonStructure`, `OfJsonObject`,
  `OfJsonArray`, `OfJsonString` and `OfJsonNumber`; each reads one JSON value, rejects anything after it, and rejects
  a value of another type.
- Writing a `point` or a `jsonb`, as text through its converter, needs `stringtype=unspecified` on the JDBC URL.
  Reading needs nothing.
- Three `text` columns go through converters too, for a type the text has no room for: `flights.status`, and the
  `timetable` view's, as a `MappedFlight.FlightStatus` (`On Time` is no Java identifier, which is why `@Enumerated`
  cannot map it), `seats.fare_conditions` and `segments.fare_conditions` as a `MappedSeat.FareConditions`, both enums
  of the values the check constraints allow, and `airports_data.timezone`, and the `airports` view's, as a `ZoneId`,
  through `MappedFlight.FlightStatusConverter`, `MappedSeat.FareConditionsConverter` and
  `ZoneIdStringAttributeConverter`, from `jinahya-persistence-more`. An enum and its converter, specific to a column,
  are nested in the mapped class of the column's table.
- Each `timestamptz` column -- the four of `flights`, `bookings.book_date`, `boarding_passes.boarding_time` and the
  `timetable` view's four -- is mapped as an `Instant`, a basic type since Jakarta Persistence 3.2. The column holds a
  moment and no offset; an `OffsetDateTime` would carry an offset the database never stored. A local time is the
  airport's: `airports_data.timezone`, a `ZoneId`, or the `timetable` view's `*_local` columns,
  `timestamp without time zone`, mapped as `LocalDateTime`.
- `flights.flight_id` is `GENERATED ALWAYS AS IDENTITY`, which `information_schema.columns` shows as `is_identity`,
  not as a default; it is `@GeneratedValue(IDENTITY)`, and the only generated identifier in the schema. A flight's
  equality is by its natural key, the route number and the scheduled departure, which the table holds unique.
- The units validate on `prePersist`. The generated identifier carries no `@NotNull`, for it has no value yet then.
  `MappedRoute`'s three read-only foreign-key codes do carry one: `Route`'s association setters set each code with
  the association which writes its column, so a route about to be inserted has them. `MappedTicket`'s `bookRef`,
  which `Ticket.setBooking` keeps in step the same way, carries none yet.

### The text of an `interval`

The text of an `interval` depends on the server's `IntervalStyle`, which a JDBC session runs with at the server
default, `postgres`; the driver does not change it, and its own `PGInterval` misreads one of the four styles:

| `IntervalStyle` | the server's text | `PGInterval` |
|---|---|---|
| `postgres`, the default | `1 day 02:05:00`, `-1 days -02:05:00` | `1 days 2 hours 5 mins`, `-1 days -2 hours -5 mins` |
| `postgres_verbose` | `@ 1 day 2 hours 5 mins`, `@ 1 day 2 hours 5 mins ago` | as above |
| `sql_standard` | `1 2:05:00`, `-1 2:05:00`, `+1-2 +3 +4:05:06.5` | `0 secs`, the driver misreads the style |
| `iso_8601` | `P1DT2H5M`, `P-1DT-2H-5M` | as above |

The demo database runs with the default; a database set to `sql_standard` would need the session pinned back,
`options=-c IntervalStyle=postgres` on the JDBC URL.

### `jsonb`, and why not `@Lob`

The four `jsonb` columns are `JsonObject` attributes over the text, `@Basic` and `@Column` with no `length` and no
`@Lob`. Hibernate reads them as the JSON text, which the converter parses, and writes them back, the write with
`stringtype=unspecified`.
A `jsonb` value is at most 268,435,455 bytes, one short of 256 MB -- the 28-bit offsets of its binary
form, not a setting, see [BUG #15079](https://www.postgresql.org/message-id/151933308143.1365.3106046996770708952%40wrigleys.postgresql.org)
and its [reply](https://www.postgresql.org/message-id/CAKFQuwZZ95W5JxQ_fgzdPONeCenUJ59-RB_zQtSQ8puKUJEJiQ%40mail.gmail.com)
-- which a `String` holds with room to spare.

`@Lob` is not a size hint. The specification has it "persisted as a large object to a database-native large object
(LOB) type", `CLOB` for a `String`, and says "portable applications should use the Lob annotation when mapping to a
database Lob type" ([Jakarta Persistence 3.2, `Lob`](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/Lob.html)).
PostgreSQL's LOB type is the large object, a value kept outside the table and referred to by an `oid` column, with
its own API and its own garbage collection ([pgjdbc, Storing Binary Data](https://jdbc.postgresql.org/documentation/binary-data/)).
`jsonb`, like `text`, is an ordinary column, read with `getString`; `getClob` is for large objects only
([pgjdbc](https://lightrun.com/answers/pgjdbc-pgjdbc-error-in-pgresultsetgetblobint)). So `@Lob` on a `String` over a
`jsonb` or `text` column mismatches the type, the default for a `String` being `CLOB`: a read calls `getClob`, the
driver parses the text as an `oid` and fails with `Bad value for type long`
([Red Hat](https://access.redhat.com/solutions/2945101), [pgsql-jdbc](https://www.postgresql.org/message-id/201102070001.22259.andreak%40officenet.no));
a write creates a large object and stores its `oid` in the column, where `vacuumlo` cannot see it and may delete the
data, which is why Hibernate asks for such columns to be `oid`
([Hibernate ORM 5.6.2](https://in.relation.to/2021/12/08/hibernate-orm-562/), [HHH-14211](https://redirect.github.com/creckord/psql-lob-test),
[JBPM-9939](https://issues.redhat.com/browse/JBPM-9939)). `@Lob` would not even buy streaming: PostgreSQL has none for
`jsonb`, the driver delivers the whole value either way.

Hibernate's `@JdbcTypeCode(SqlTypes.JSON)` would bind `jsonb` as itself, with no `stringtype=unspecified`
([Thorben Janssen](https://thorben-janssen.com/?p=3224),
[Hibernate javadoc](https://docs.hibernate.org/orm/6.6/javadocs/org/hibernate/dialect/PostgreSQLJsonPGObjectJsonbType.html)),
but it serializes a value through Jackson or JSON-B into a type whose shape it fixes, a record or a `Map`; the
columns' objects have no fixed keys and values, so they stay JSON-P `JsonObject`s.

## Building

Hibernate ORM is the persistence provider.

```shell
$ ./_mvn_test.sh              # unit tests, no database needed
$ ./_mvn_verify.sh            # + integration tests, against the database above
$ ./_mvn_verify_notests.sh    # no database needed
```

## The tests

The entities live in `src/test`, one per table and view, extending the mapped superclasses with what an
application decides: the associations, lazy, with no cascade; `of(...)` factories where an association is part of
the identifier; `RouteId`, the `MappedRouteId` `Route` gives `MappedRoute<T>` for a table which has none. The
persistence unit is there too, `it`, on the database above.

Per entity, after `mysql-sakila-persistence`: a `*_Randomizer`, podam with the constrained attributes assigned after
the fact; a `*_Persister`, which persists what the entity references first; a `*_Test`, without a persistence
context; and a `*_Persistence_IT`, against the database, which
`find`s a random installed row, asserts what the entity promises of it, and inserts a randomized row inside a
transaction it rolls back. The composite identifiers have a `*_Randomizer` and a `*_Test` of their own.

```shell
$ ./_mvn_test.sh      # 111 unit tests
$ ./_mvn_verify.sh    # + 52 integration tests, the database up
```

All of them pass. Random codes are lower-case and random validity periods lie in the 2030s, so
that a randomized row never collides with an installed one.

## The exercises

The exercises on the installed database are listed, with their ids, in [EXERCISES.asciidoc](EXERCISES.asciidoc), and
registered in [EXERCISES.yaml](EXERCISES.yaml); the `src/test/sql/exercises/*_exercises_*.sql` files answer them in
SQL, each with what the installed data gives back. The `*_Exercises_*_PersistenceIT` classes, which answer them in JPQL
and with the Criteria API, follow once the entities exist.

```shell
$ docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/exercises/flights_exercises_01_basic.sql
```
