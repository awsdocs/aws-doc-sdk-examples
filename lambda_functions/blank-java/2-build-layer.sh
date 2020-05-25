#!/bin/bash
set -eo pipefail
gradle -q packageLibs
mv build/distributions/blank-java.zip build/blank-java-lib.zip