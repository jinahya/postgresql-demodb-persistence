# CLAUDE.md

Guidance for working in this module.

## The module, as it stands (absolute rules)

- **Hibernate ORM, deliberately.** The module targets Hibernate ORM only; EclipseLink proved too hard to make work.
  `persistence-unit.provider`, which `persistence.xml` reads, is `HibernatePersistenceProvider`, and there is no other
  provider's profile. Hibernate ORM's own features are welcome where Jakarta Persistence falls short, such as
  `Flight.route`'s `@JoinColumnsOrFormulas` and `MappedRoute`'s `@JdbcTypeCode`; prefer the standard where it does
  the job, and say in the javadoc where a mapping is Hibernate ORM's.
- **Two persistence units, `test` and `it`.**
  - `test` is H2, in memory, in PostgreSQL mode, with the schema generated from the mappings (`create-drop`); the
    `*_PersistenceTest` classes run on it, under surefire. What H2 cannot do is `@Disabled` there, with the reason:
    `Flight.route`'s join formula, whose `@>` H2 does not parse, and the views, into which nothing is inserted.
  - `it` is the demo database itself, on PostgreSQL; the `*_PersistenceIT` classes run on it, under failsafe
    (`-Pfailsafe`), and cover what `test` cannot.
  - The `*_Test` classes are plain unit tests that need no database.

