@echo off
set JAVA_HOME = C:\Program Files (x86)\Java\jdk1.6.0_10
set VIEWER_HOME=%CD%

set VIEWER_CLASSPATH="%VIEWER_HOME%/hgdbviewer.jar"
set JAVA_EXEC="%JAVA_HOME%/bin/java"

echo %PATH%

set LIB_JARS=
echo set LIB_JARS=%%~1;%%LIB_JARS%%>append.bat
dir /s/b jars\*.jar > tmpList.txt
FOR /F "usebackq tokens=1* delims=" %%i IN (tmpList.txt) do (call append.bat "%%i")
del append.bat
del tmpList.txt
set VIEWER_CLASSPATH=%LIB_JARS%;%VIEWER_CLASSPATH%
echo %VIEWER_CLASSPATH%

%JAVA_EXEC% -cp %VIEWER_CLASSPATH% org.hypergraphdb.viewer.HGVDesktop