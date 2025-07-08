#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# #############################################################################
#
#  Gradle startup script for UN*X
#
# #############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done

APP_HOME=`dirname "$PRG"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass any JVM options to Gradle respectively.
DEFAULT_JVM_OPTS=""

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

# For Darwin, add options to specify how the application appears in the dock
if [ `uname -s` = "Darwin" ]; then
    GRADLE_OPTS="-Xdock:name=$APP_NAME -Xdock:icon=\"$APP_HOME/media/gradle.icns\" $GRADLE_OPTS"
fi

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

# Attempt to find java
if [ -z "$JAVA_HOME" ] ; then
    JAVA_EXE=`which java 2>/dev/null`
    if [ -z "$JAVA_EXE" ] ; then
        echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
        echo ""
        echo "Please set the JAVA_HOME variable in your environment to match the"
        echo "location of your Java installation."
        exit 1
    fi
else
    JAVA_EXE="$JAVA_HOME/bin/java"
fi

if [ ! -x "$JAVA_EXE" ] ; then
  echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
  echo ""
  echo "Please set the JAVA_HOME variable in your environment to match the"
  echo "location of your Java installation."
  exit 1
fi


# #############################################################################
#
#  (c) 2012-2013, 2015 a pure-bash reimplementation of the gradlew script
#
#  This startup script is for UN*X and similar systems, and is not guaranteed
#  to work on Windows, though it may depending on the shell you use. It does
#  not use any external programs, and is licensed under the Apache License 2.0.
#
# #############################################################################
#
#  Created by:  Jochen Schalanda
#  Homepage:    http://jochen.schalanda.de
#  Created:     2012-07-26
#  Modified by: Benjamin Muschko
#  Modified:    2013-08-14
#  Modified by: Hans Dockter
#  Modified:    2015-08-11
#
# #############################################################################


# Set script variables
# --------------------
#
# GRADLE_VERSION: The version of Gradle to use.
# GRADLE_HOME: The home directory of the Gradle installation to use.
# GRADLE_OPTS: Extra options for the Gradle process.
# JAVA_HOME: The home directory of the Java installation to use.
# JAVA_OPTS: Extra options for the Java process.

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
fi

# Setup the command line arguments for the JVM.
#
# This is a bit of a hack, but it is the only way to get the Java command and
# the script options separated. If you know a better solution, please let me
# know.
for i in "$@"; do
    case "$i" in
        -D*) JAVAPROP="$JAVAPROP $i" ;;
        -*) ;;
        *) break ;;
    esac
    shift
done
JVM_OPTS="$JAVA_OPTS $JAVAPROP"

# Setup the classpath for the wrapper.
#
# We need to use the `path.separator` property of the JVM, as it is the only
# way to get the correct path separator for the current operating system.
CP_SEP=`"$JAVACMD" -XshowSettings:properties -version 2>&1 | grep 'path.separator' | cut -d'=' -f2 | tr -d '[:space:]'`
CLASSPATH="`dirname "$0"`/gradle/wrapper/gradle-wrapper.jar"

# Start the wrapper.
exec "$JAVACMD" $JVM_OPTS -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"