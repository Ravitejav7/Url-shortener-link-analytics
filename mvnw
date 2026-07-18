#!/usr/bin/env sh

PROJECT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
JAVA_HOME="/mnt/c/Program Files/Java/jdk-21.0.11"
MAVEN_HOME="$PROJECT_DIR/.tools/apache-maven-3.9.16"

if [ ! -x "$JAVA_HOME/bin/java.exe" ]; then
  echo "JAVA_HOME is not valid: $JAVA_HOME"
  exit 1
fi

if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
  echo "Maven was not found at: $MAVEN_HOME"
  exit 1
fi

export JAVA_HOME MAVEN_HOME
export PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH"
exec "$MAVEN_HOME/bin/mvn" "$@"
