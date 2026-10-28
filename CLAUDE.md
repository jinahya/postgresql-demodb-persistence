# CLAUDE.md

Guidance for working in this module.

## The module, as it stands (absolute rules)

- **Not Hibernate ORM only.** The provider is a property, `persistence-unit.provider`, which `persistence.xml` reads;
  the module is not written for one provider. Prefer portable Jakarta Persistence (`@Convert` /
  `AttributeConverter`, the standard annotations) over a provider's own features, never describe the module as
  Hibernate-only, and never drop a provider or make a mapping work on one provider only. A change that seems to call
  for that is the author's decision: ask first.
- **No H2.** The tests that touch a database run on PostgreSQL, the demo database itself; no in-memory database.
- **ITs only.** There is one persistence unit, `it`. A test that needs the database is a `*_IT` class, run by
  failsafe (`-Pfailsafe`); there is no `test` unit and no `*_Persistence_Test`. The `*_Test` classes are plain unit
  tests that need no database.

