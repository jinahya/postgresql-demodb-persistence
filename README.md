# postgres-demonstration-persistence

Jakarta Persistence mappings for the [PostgreSQL Demonstration Database](https://postgrespro.com/community/demodb)
(Airlines; database `demo`, schema `bookings`).

Sibling of [mysql-sakila-persistence](https://github.com/jinahya/mysql-sakila-persistence).

## The database

```shell
$ ./_docker-compose-up.sh
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
