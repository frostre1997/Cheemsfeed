#!/usr/bin/env sh
# This is a simplified version of the Gradle startup script
APP_HOME=$(pwd)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
