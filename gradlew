#!/usr/bin/env bash

################################################################################
##
##  Gradle start up script for UN*X
##
################################################################################

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
SAVED="`pwd`"
cd "`dirname "$PRG"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or at least 4G for the daemon
if [ -n "$JAVA_OPTS" ]; then
    JAVA_OPTS="$JAVA_OPTS -Xmx4096m"
else
    JAVA_OPTS="-Xmx4096m"
fi

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/bin/java" ] ; then
        #             JAVA_HOME is set and points to a valid execution file
        JAVACMD="$JAVA_HOME/bin/java"
    else
        echo
        echo "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
        echo "Please set the JAVA_HOME variable in your environment to match the"
        echo "location of your Java installation."
        echo
        exit 1
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || {
        echo
        echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
        echo
        echo "Please set the JAVA_HOME variable in your environment to match the"
        echo "location of your Java installation."
        echo
        exit 1
    }
fi

# Setup the classpath
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Execute Gradle
exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS 
        -classpath "$CLASSPATH" 
        org.gradle.wrapper.GradleWrapperMain 
        "$@"
