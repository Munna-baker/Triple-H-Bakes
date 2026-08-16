#!/usr/bin/env sh
org_gradle_jvmargs="-Xmx64m"
app_name="Gradle"

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's Java on AIX uses separate locations for the jre
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n 27000`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "gt" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n "$MAX_FD" >/dev/null 2>&1
    fi
fi

# Escape spacing
save_args() {
    for i do printf '%s\n' "$i" | sed "s/'/'\\''/g;1s/^/'/;\$s/$/'/" ; done
    eval "set -- \
    `save_args "$@"`"
}

# Collect all arguments in shell variables, depending on, depending on the OS.
if [ -n "$CYGWIN" ] ; then
    cygwin=true
else
    cygwin=false
fi

# Determine the directory where this script is.
DIR=`which "$0" 2>/dev/null`
[ -z "$DIR" ] && DIR="$0"
DIRNAME=`dirname "$DIR"`
CDPATH=""
PRG="\"$DIRNAME/gradlew\""

# Find gradle wrapper jar
JAVAROOT="$DIRNAME/gradle/wrapper/gradle-wrapper.jar"

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "-jar" "$JAVAROOT" "$@"
