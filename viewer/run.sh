#!/bin/sh

#Uncomment and set appropriately
#HGDB_HOME="/usr/local/hypergraphdb"

# OS specific support.  $var _must_ be set to either true or false.
pathsep=':'
systemname='windows'
cygwin=false
os400=false
case "`uname`" in
CYGWIN*) 
  cygwin=true
  systemname='windows'
  ;;
*) 
  systemname='linux'
  ;;
esac

if [ `uname -m` = 'x86_64' ]; then
  systemarch='/x86_64'
fi

VIEWER_BIN_DIR=`dirname $0`
cd $VIEWER_BIN_DIR
export VIEWER_HOME="`pwd`"
echo "Using VIEWER  home directory '$VIEWER_HOME'"

JAVA_EXEC=java

VIEWER_CLASSPATH="$VIEWER_HOME/hgdbviewer.jar"
VIEWER_CLASSPATH="$VIEWER_CLASSPATH$pathsep$HGDB_HOME/hgdbfull.jar"

for f in $HGDB_HOME/jars/*.jar; do
  VIEWER_CLASSPATH="$VIEWER_CLASSPATH$pathsep$f"
done;

cd $VIEWER_HOME

for f in jars/*.jar; do
  echo "F is $f"
  VIEWER_CLASSPATH="$VIEWER_CLASSPATH$pathsep$f"
done;


HGDB_NATIVE=$HGDB_HOME/native/$systemarch

echo "Directory containing native libs: $HGDB_NATIVE"

if $cygwin; then
  [ -n "$VIEWER_HOME" ] && VIEWER_CLASSPATH=`cygpath --absolute --path --windows "$VIEWER_CLASSPATH"`
  [ -n "$HGDB_NATIVE" ] && HGDB_NATIVE=`cygpath --path --windows "$HGDB_NATIVE"`
fi

echo "Using classpath: $VIEWER_CLASSPATH"
exec $JAVA_EXEC -cp $VIEWER_CLASSPATH  -Djava.library.path=$HGDB_NATIVE org.hypergraphdb.viewer.HGVDesktop
