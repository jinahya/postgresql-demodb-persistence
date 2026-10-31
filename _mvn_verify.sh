#!/bin/sh
# Runs `verify`: the whole lifecycle -- compile, package, javadoc, enforcer -- with both the unit
# tests and the integration tests executed.
#
# The ITs talk to a real PostgreSQL at localhost:5432/demo, so bring it up with
# ./compose.sh first; otherwise they fail on the connection rather than on a mapping.
#
# `failsafe` is the profile that binds maven-failsafe-plugin.
#
# As of 3.6.0 failsafe no longer binds its own skipTests parameter to the skipTests user property,
# so the two halves are selected separately -- and that is a property of ${version.maven-failsafe};
# under 3.5.x and earlier -DskipTests reached both:
#
#     ./_mvn_verify.sh -DskipTests   # the ITs alone
#     ./_mvn_verify.sh -DskipITs     # the unit tests alone
#     ./_mvn_verify_notests.sh       # neither
cd "$(dirname "$0")" || exit 1

exec ./mvnw -Pfailsafe clean verify "$@"
