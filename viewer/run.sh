#!/bin/sh

#Uncomment and set appropriately those 2 vars
#JAVA_HOME=
#HGDB_HOME=
 
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


VIEWER_BIN_DIR=`dirname $0`
cd $VIEWER_BIN_DIR
export VIEWER_HOME="`pwd`"
echo "Using VIEWER  home directory '$VIEWER_HOME'"

JAVA_EXEC=java

VIEWER_CLASSPATH="$VIEWER_HOME/hgdbviewer.jar"

cd $HGDB_HOME
for f in *.jar; do
  VIEWER_CLASSPATH="$VIEWER_CLASSPATH$pathsep$f"
done;
cd $VIEWER_BIN_DIR

for f in jars/*.jar; do
  VIEWER_CLASSPATH="$VIEWER_CLASSPATH$pathsep$f"
done;


 HGDB_NATIVE=$HGDB_HOME$\native\windows

if $cygwin; then
  [ -n "$VIEWER_HOME" ] && VIEWER_CLASSPATH=`cygpath --absolute --path --windows "$VIEWER_CLASSPATH"`
  [ -n "$HGDB_NATIVE" ] && HGDB_NATIVE=`cygpath --path --windows "$HGDB_NATIVE"`
fi

exec $JAVA_EXEC -cp $VIEWER_CLASSPATH  -Djava.library.path=$HGDB_NATIVE org.hypergraphdb.viewer.HGVDesktop
