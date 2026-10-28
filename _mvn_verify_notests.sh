#!/bin/sh
# Runs `verify`: the whole lifecycle -- compile, package, javadoc, enforcer -- with no test
# executed. The tests are still COMPILED, which is half of what this is for; -Dmaven.test.skip
# would drop that too and is deliberately not used here.
#
# Needs no database: this is the variant to reach for when PostgreSQL is not up.
#
# `failsafe` is the profile that binds maven-failsafe-plugin.
#
# Both flags are needed. As of 3.6.0 failsafe no longer binds its own skipTests parameter to the
# skipTests user property, so -DskipTests reaches surefire alone and the ITs would still run;
# -DskipITs is what stops them. For either half on its own, see ./_mvn_verify.sh.
cd "$(dirname "$0")" || exit 1

exec ./mvnw -Pfailsafe clean verify -DskipTests -DskipITs "$@"
