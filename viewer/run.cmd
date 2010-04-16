@echo off
 set JAVA_HOME=C:\PROGRA~2\Java\jdk1.6.0_10
 set HGDB_HOME=C:\temp\hg
 
set VIEWER_HOME=%CD%
set VIEWER_CLASSPATH=%VIEWER_HOME%/hgdbviewer.jar
set HGDB_NATIVE=%HGDB_HOME%\native\windows

set JAVA_EXEC="%JAVA_HOME%/bin/java"

set PATH=%HGDB_NATIVE%;%PATH%

cd %HGDB_HOME%
set HG_JARS=
echo set HG_JARS=%%~1;%%HG_JARS%%>append.bat
dir /s/b *.jar > tmpList.txt
FOR /F "usebackq tokens=1* delims=" %%i IN (tmpList.txt) do (call append.bat "%%i")
del append.bat
del tmpList.txt

cd %VIEWER_HOME%
set LIB_JARS=
echo set LIB_JARS=%%~1;%%LIB_JARS%%>append.bat
dir /s/b jars\*.jar > tmpList.txt
FOR /F "usebackq tokens=1* delims=" %%i IN (tmpList.txt) do (call append.bat "%%i")
del append.bat
del tmpList.txt
set VIEWER_CLASSPATH=%HG_JARS%%LIB_JARS%;%VIEWER_CLASSPATH%
echo  VIEWER_CLASSPATH: %VIEWER_CLASSPATH%

%JAVA_EXEC% -cp %VIEWER_CLASSPATH% -Djava.library.path=%HGDB_NATIVE%  org.hypergraphdb.viewer.HGVDesktop