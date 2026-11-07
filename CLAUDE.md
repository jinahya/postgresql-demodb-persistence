# CLAUDE.md

Guidance for working in this module.

## The module, as it stands (absolute rules)

- **Hibernate ORM, deliberately.** The module targets Hibernate ORM only; EclipseLink proved too hard to make work.
  `persistence-unit.provider`, which `persistence.xml` reads, is `HibernatePersistenceProvider`, and there is no other
  provider's profile. Hibernate ORM's own features are welcome where Jakarta Persistence falls short, such as
  `Flight.route`'s `@JoinColumnsOrFormulas` and `MappedRoute`'s `@JdbcTypeCode`; prefer the standard where it does
  the job, and say in the javadoc where a mapping is Hibernate ORM's.
- **No H2.** The tests that touch a database run on PostgreSQL, the demo database itself; no in-memory database.
- **ITs only.** There is one persistence unit, `it`. A test that needs the database is a `*_PersistenceIT` class, run by
  failsafe (`-Pfailsafe`); there is no `test` unit and no `*_PersistenceTest`. The `*_Test` classes are plain unit
  tests that need no database.

