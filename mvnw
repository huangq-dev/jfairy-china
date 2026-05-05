#!/bin/sh

# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup script, version 3.2.0
#
# Required ENV vars:
# JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
# export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"
# MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

set -e

if [ -z "$JAVA_HOME" ]; then
  echo "Error: JAVA_HOME is not set." >&2
  exit 1
fi

JAVA_HOME=$(echo "$JAVA_HOME" | sed 's/\/$//')
if [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "Error: JAVA_HOME is set to an invalid directory: $JAVA_HOME" >&2
  echo "Please set the JAVA_HOME variable in your environment to match the" >&2
  echo "location of your Java installation." >&2
  exit 1
fi

MVNW_DIR=$(dirname "$0")/.mvn/wrapper
MVNW_JAR="$MVNW_DIR/maven-wrapper.jar"
MVNW_PROPERTIES="$MVNW_DIR/maven-wrapper.properties"

MVNW_URL=""
if [ -f "$MVNW_PROPERTIES" ]; then
  while IFS='=' read -r key value; do
    if [ "$key" = "wrapperUrl" ]; then
      MVNW_URL="$value"
    fi
  done < "$MVNW_PROPERTIES"
fi

if [ ! -f "$MVNW_JAR" ]; then
  if [ -z "$MVNW_URL" ]; then
    echo "Error: wrapperUrl not set in $MVNW_PROPERTIES" >&2
    exit 1
  fi
  echo "Downloading Maven wrapper..."
  curl -o "$MVNW_JAR" "$MVNW_URL" || {
    echo "Error downloading Maven wrapper" >&2
    exit 1
  }
fi

MVNW_ARGS=""
if [ -f "$MVNW_PROPERTIES" ]; then
  while IFS='=' read -r key value; do
    if [ "$key" = "wrapperArgs" ]; then
      MVNW_ARGS="$value"
    fi
  done < "$MVNW_PROPERTIES"
fi

exec "$JAVA_HOME/bin/java" $MVNW_ARGS -jar "$MVNW_JAR" "$@"