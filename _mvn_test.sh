#!/bin/sh
# Runs the unit tests.
#
# `clean` keeps the run from compiling against a metamodel, or a schema, a previous build left.
cd "$(dirname "$0")" || exit 1

exec ./mvnw clean test "$@"
