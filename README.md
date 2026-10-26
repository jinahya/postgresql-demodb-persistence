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
back. The `*_exercises_*.sql` files beside them are the exercises of [`EXERCISES.asciidoc`](EXERCISES.asciidoc).

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

## Column types the providers differ on

The schema uses five PostgreSQL types Jakarta Persistence has no basic type for: `integer[]` (`routes.days_of_week`),
`interval` (`routes.duration`), `tstzrange` (`routes.validity`), `point` (`airports_data.coordinates`) and `jsonb`
(`airplanes_data.model`, `airports_data.airport_name`, `city`, `country`). The table below is what each provider does
with an attribute mapped by a plain `@Column` and no converter, measured against the database above with
Hibernate ORM 7.4.12.Final, EclipseLink 5.0.2 and PostgreSQL JDBC 42.7.14: a row is read, then the column is updated
and the transaction rolled back. "With cast" means the write works only with `stringtype=unspecified` on the JDBC
URL, which makes the driver send a string parameter untyped so that the server casts it; without it PostgreSQL
answers
`column "x" is of type integer[] but expression is of type character varying`.

| Column | Attribute type | Hibernate ORM | EclipseLink |
|---|---|---|---|
| `integer[]` | `Integer[]`, `int[]`, `List<Integer>` | reads as declared; writes natively, no cast needed | fails both ways: it serializes the value, reads `Integer[]` as `byte[]` and writes `bytea` |
| `integer[]` | `String` | reads `{"4","7"}`, the elements quoted, not the server's literal; writes with cast | reads `[Ljava.lang.Integer;@…`, the array's `toString()`; writes with cast |
| `interval` | `Duration` | fails both ways: it maps `Duration` as a number, `Bad value for type BigDecimal : 05:05:00` | fails both ways: the driver's `PGInterval` is not converted |
| `interval` | `String` | reads `05:05:00`, the server's text; writes with cast | reads `5 hours 5 mins`, the driver's `PGInterval.toString()`; writes with cast |
| `tstzrange` | `String` | reads the server's text, `["2025-10-01 09:00:00+09","2025-11-01 09:00:00+09")`; writes with cast | the same |
| `point` | `String` | reads `(-145.51,-17.3526)`; writes with cast | the same |
| `jsonb` | `String` | reads the JSON text; writes with cast | the same |
| `timestamptz` | `OffsetDateTime`, `Instant` | fine both ways, the offset normalized to UTC | fine both ways, the offset the JVM's default zone's |

Behind that table is what each provider hands over before any mapping is applied, read through a native query from
the same database. Neither adds anything of its own to the exotic types: both pass on what the PostgreSQL driver
returns. They part only on `jsonb`, and on which generation of date and time classes they prefer.

| Column | PostgreSQL type | Hibernate ORM | EclipseLink |
|---|---|---|---|
| `routes.days_of_week` | `integer[]` | `Integer[]` | `Integer[]` |
| `routes.duration` | `interval` | `org.postgresql.util.PGInterval` | `PGInterval` |
| `routes.validity` | `tstzrange` | `org.postgresql.util.PGobject` | `PGobject` |
| `airports_data.coordinates` | `point` | `org.postgresql.geometric.PGpoint` | `PGpoint` |
| `airports_data.airport_name` | `jsonb` | `String` | `PGobject` |
| `routes.scheduled_time` | `time` | `java.time.LocalTime` | `java.sql.Time` |
| `bookings.book_date` | `timestamptz` | `java.time.Instant` | `java.sql.Timestamp` |
| `airports_data.airport_code` | `character(3)` | `String` | `String` |
| `bookings.total_amount` | `numeric(10,2)` | `BigDecimal` | `BigDecimal` |
| `tickets.outbound` | `boolean` | `Boolean` | `Boolean` |

From there each provider converts to the attribute's type, which is where the differences above come from: Hibernate
has a conversion for arrays and reads a `String` attribute with `getString`; EclipseLink falls back to `toString()`
for a `String` attribute, which is `[Ljava.lang.Integer;@…` for the array and `5 hours 5 mins` for the interval, and
to Java serialization for any other type it does not know, which fails.

What follows from it, and what this module does:

- `integer[]` and `interval` go through an `AttributeConverter` each, `IntegerListArrayAttributeConverter.OfString` (`List<Integer>`) and
  `IntervalPostgresAttributeConverter.OfString` (`PostgresInterval`), whose database side is `String`, so that both providers bind the
  text on a write. The catch is the read: Hibernate hands a converter the column the way its database side says,
  EclipseLink hands it the driver's raw object whatever it says, and a method declared on `String` casts, and fails,
  before the converter runs. So each converter declares the database side as a type variable -- its method takes an
  `Object` once erased, and accepts text or object alike -- and its nested `OfString` binds `Y` to `String` without
  overriding the methods, which is what keeps the cast out. A database side declared as `Object`
  was tried first: reads work, but neither provider can write through it, Hibernate having no JDBC type to bind and
  EclipseLink guessing one from the attribute.
- `IntegerListArrayAttributeConverter` holds all of it in one file: the integer array literal -- braces, commas,
  `NULL`; an integer never needs quotes -- and the shapes an array arrives in, a `java.sql.Array` or a Java array
  included. It cannot be a plain class bound to `String`, for the cast above, so it declares the database side as a
  type variable itself, and its nested `OfString` binds it to `String`; an attribute names
  `IntegerListArrayAttributeConverter.OfString`.
- `flights.flight_id` is `GENERATED ALWAYS AS IDENTITY`, which `information_schema.columns` shows as `is_identity`,
  not as a default; it is `@GeneratedValue(IDENTITY)`, and the only generated identifier in the schema.
- A read-only foreign-key attribute, and the generated identifier, carry no `@NotNull`: the units validate on
  `prePersist`, when neither can have a value yet, so with it no row could ever be inserted. The column's
  `nullable = false` stays, and the database enforces it on what the association writes.
- The in-memory unit runs EclipseLink on its PostgreSQL platform, not its H2 one: on H2 2.x in PostgreSQL mode
  the H2 platform spells an identity column as the legacy `INTEGER IDENTITY` and reads a generated key with
  `CALL IDENTITY()`, neither of which the mode accepts, so the `flights` table was never created. The PostgreSQL
  platform's `SERIAL` and `select lastval()` are. A column definition cannot reach the read-back query, so the
  platform is the switch that has to flip.
- `point` goes through a converter of the same kind. `PointAttributeConverter.OfString` maps it as
  `PointAttributeConverter.Point`, written `(x,y)`; EclipseLink's `PGpoint` is read through its fields, by reflection.
- Each `jsonb` column, an object of one name per language, is mapped as a JSON-P `JsonObject` through
  `JsonpStringAttributeConverter.OfJsonObject`, whose database side is a plain `String`, not a type variable; the attributes
  have package-private accessors, and are read and written a name at a time, by the language of a `Locale`.
  `JsonpStringAttributeConverter`, from `jinahya-persistence-more`, has a converter for each JSON-P type, `OfJsonValue`, `OfJsonStructure`, `OfJsonObject`,
  `OfJsonArray`, `OfJsonString` and `OfJsonNumber`; each reads one JSON value, rejects anything after it, and rejects
  a value of another type. EclipseLink hands it the column as a `String` too, not the driver's `PGobject`.
- Three `text` columns go through converters too, for a type the text has no room for: `flights.status`, and the
  `timetable` view's, as a `MappedFlight.FlightStatus` (`On Time` is no Java identifier, which is why `@Enumerated`
  cannot map it), `seats.fare_conditions` and `segments.fare_conditions` as a `MappedSeat.FareConditions`, both enums
  of the values the check constraints allow, and `airports_data.timezone`, and the `airports` view's, as a `ZoneId`,
  through `MappedFlight.FlightStatusConverter`, `MappedSeat.FareConditionsConverter` and `ZoneIdStringAttributeConverter`, from `jinahya-persistence-more`. An enum
  and its converter, specific to a column, are nested in the mapped class of the column's table.
- `tstzrange` stays `String`, the text both providers read alike: the test entity over `routes` writes the column
  through its embedded identifier, and the specification has no conversion applied to an identifier's attributes,
  so a converted `validity` would leave the entity the same value twice, once converted and once as text.
- Writing any of the five needs `stringtype=unspecified` on the JDBC URL. Reading needs nothing.
- A persistence unit with `exclude-unlisted-classes` has to list the converters; EclipseLink does not find them
  otherwise.
- The in-memory unit generates its schema, and a converter with an `Object` database side gives Hibernate no column
  type to generate; the test entity over `routes` overrides the two columns with `columnDefinition = "varchar"`.

### What else was tried for `interval` and `integer[]`

Every attribute type the web suggests for the two, measured the same way, a row read and the column updated and
rolled back, with `stringtype=unspecified` on. `byte[]` is left out: it is the type of a `bytea` column, not of
either of these.

| Column | Attribute type | Hibernate ORM | EclipseLink |
|---|---|---|---|
| `interval` | `Duration`, as is | fails: `Duration` is a number to it, `Bad value for type BigDecimal` | fails: the driver's `PGInterval` is not converted |
| `interval` | `Duration` with `hibernate.type.preferred_duration_jdbc_type=INTERVAL_SECOND` | reads `PT5H5M`, writes `02:05:00` | no such setting |
| `interval` | `org.postgresql.util.PGInterval` | fails: serialized, `StreamCorruptedException` | fails: serialized to `bytea` |
| `interval` | `PostgresInterval` through `IntervalPostgresAttributeConverter.OfString` | works | works |
| `integer[]` | `Integer[]`, `int[]`, `long[]`, `List<Integer>`, `Set<Integer>` | reads and writes natively, no cast needed | fails: serialized to `bytea` |
| `integer[]` | `Number[]` | reads; the write sends `bytea[]` | fails |
| `integer[]` | `Object[]` | fails both ways, `UnsupportedOperationException` | fails |
| `integer[]` | `List<Integer>` through `IntegerListArrayAttributeConverter.OfString` | works | works |

So Hibernate maps `Duration` to a real `interval` behind one property, since 6.2, and any typed array or collection
of a basic type to a SQL array, since 6.1 ([migration guide](https://docs.hibernate.org/orm/6.1/migration-guide/));
the untyped shapes, `Object[]` and the driver's own classes, fail on both. EclipseLink maps none of it; its documented
route is `@Array` over `java.sql.Array` inside object-relational structures
([FAQ](https://wiki.eclipse.org/EclipseLink/FAQ/JPA/PostgreSQL),
[ELUG](https://wiki.eclipse.org/Configuring_an_Object-Relational_Data_Type_Array_Mapping_(ELUG))), not a column of an
entity. No portable converter exists off the shelf: [threeten-jpa](https://github.com/marschall/threeten-jpa) has none
for PostgreSQL intervals, the driver has no `Duration` support of its own, and the Hibernate-only
[Hypersistence type](https://vladmihalcea.com/map-postgresql-interval-java-duration-hibernate/) is a `@Type`, not a
converter.

### The shapes an `interval` arrives in

The text of an `interval` depends on the server's `IntervalStyle`, which a JDBC session runs with at the server
default, `postgres`; the driver does not change it. Hibernate hands a `String`-typed attribute or converter the text,
`getString`; EclipseLink hands it the driver's `PGInterval`, whose `toString()` is the driver's own form and does not
depend on the style:

| `IntervalStyle` | Hibernate (`getString`) | EclipseLink (`PGInterval.toString()`) |
|---|---|---|
| `postgres`, the default | `1 day 02:05:00`, `-1 days -02:05:00` | `1 days 2 hours 5 mins`, `-1 days -2 hours -5 mins` |
| `postgres_verbose` | `@ 1 day 2 hours 5 mins`, `@ 1 day 2 hours 5 mins ago` | as above |
| `sql_standard` | `1 2:05:00`, `-1 2:05:00`, `+1-2 +3 +4:05:06.5` | `0 secs`, the driver misreads the style |
| `iso_8601` | `P1DT2H5M`, `P-1DT-2H-5M` | as above |

`routes.duration` is mapped as `IntervalPostgresAttributeConverter.PostgresInterval`, a record of the months, the
days and the microseconds, as the column keeps them, through `IntervalPostgresAttributeConverter.OfString`, with no
class of the driver's in the code. It reads, and writes, the text of the `postgres` style only, which the JDBC URL
pins, `options=-c IntervalStyle=postgres`, whatever the server's, the database's or the role's default. EclipseLink
still hands the converter the driver's `PGInterval`, which it reads through the object's getters, by reflection, so
the driver is needed at run time only; the pinned `postgres` is what keeps the driver from misreading.

### `jsonb`, and why not `@Lob`

The four `jsonb` columns are `JsonObject` attributes over the text, `@Basic` and `@Column` with no `length` and no
`@Lob`. Both providers read them as the JSON text, which the converter parses, and write them back, the write
with `stringtype=unspecified` like every other exotic column.
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
`jsonb` or `text` column mismatches the type on both providers, whose default for a `String` is `CLOB`
([EclipseLink](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Mapping/Basic_Mappings/Lob)):
a read calls `getClob`, the driver parses the text as an `oid` and fails with `Bad value for type long`
([Red Hat](https://access.redhat.com/solutions/2945101), [pgsql-jdbc](https://www.postgresql.org/message-id/201102070001.22259.andreak%40officenet.no));
a write creates a large object and stores its `oid` in the column, where `vacuumlo` cannot see it and may delete the
data, which is why Hibernate asks for such columns to be `oid`
([Hibernate ORM 5.6.2](https://in.relation.to/2021/12/08/hibernate-orm-562/), [HHH-14211](https://redirect.github.com/creckord/psql-lob-test),
[JBPM-9939](https://issues.redhat.com/browse/JBPM-9939)). `@Lob` would not even buy streaming: PostgreSQL has none for
`jsonb`, the driver delivers the whole value either way.

The one provider-native improvement is Hibernate's `@JdbcTypeCode(SqlTypes.JSON)`, which it uses "only if explicitly
configured" ([Thorben Janssen](https://thorben-janssen.com/?p=3224),
[Hibernate javadoc](https://docs.hibernate.org/orm/6.6/javadocs/org/hibernate/dialect/PostgreSQLJsonPGObjectJsonbType.html)),
and which EclipseLink has no counterpart to; so it is an extending entity's choice, not the mapped superclass's.

## Building

Two persistence providers, one profile each; `jakarta-ee-11-hibernate-orm` is active by default.

```shell
$ ./_mvn_jakarta_ee_11_test_hibernate-orm.sh              # unit tests, H2 in PostgreSQL mode
$ ./_mvn_jakarta_ee_11_test_eclipselink.sh
$ ./_mvn_jakarta_ee_11_verify_hibernate-orm.sh            # + integration tests, against the database above
$ ./_mvn_jakarta_ee_11_verify_eclipselink.sh
$ ./_mvn_jakarta_ee_11_verify_notests_hibernate-orm.sh    # no database needed
$ ./_mvn_jakarta_ee_11_verify_notests_eclipselink.sh
```

## The tests

The entities live in `src/test`, one per table and view, extending the mapped superclasses with what an
application decides: the associations, lazy, with no cascade; `of(...)` factories where an association is part of
the identifier; the identifier `Route` gives a table which has none. The persistence units are there too,
`demonstration-test` on H2 in PostgreSQL mode, whose schema the provider generates, and `demonstration-it` on the
database above.

Per entity, after `mysql-sakila-persistence`: a `*_Randomizer`, podam with the constrained attributes assigned after
the fact; a `*_Persister`, which persists what the entity references first; a `*_Test`, without a persistence
context; a `*_Persistence_Test`, against the in-memory unit; and a `*_Persistence_IT`, against the database, which
`find`s a random installed row, asserts what the entity promises of it, and inserts a randomized row inside a
transaction it rolls back. The composite identifiers have a `*_Randomizer` and a `*_Test` of their own.

```shell
$ ./_mvn_jakarta_ee_11_test_hibernate-orm.sh        # 127 unit tests
$ ./_mvn_jakarta_ee_11_test_eclipselink.sh
$ ./_mvn_jakarta_ee_11_verify_hibernate-orm.sh      # + 36 integration tests, the database up
$ ./_mvn_jakarta_ee_11_verify_eclipselink.sh
```

All of them pass on both providers. Random codes are lower-case and random validity periods lie in the 2030s, so
that a randomized row never collides with an installed one.

## The exercises

The exercises on the installed database are listed, with their ids, in [EXERCISES.asciidoc](EXERCISES.asciidoc), and
registered in [EXERCISES.yaml](EXERCISES.yaml); the `src/test/sql/*_exercises_*.sql` files answer them in SQL, each with
what the installed data gives back. The `*_Exercises_*_PersistenceIT` classes, which answer them in JPQL and with the
Criteria API, follow once the entities exist.

```shell
$ docker exec -i postgresql-demodb psql -U postgres -d demo < src/test/sql/flights_exercises_01_basic.sql
```
