@echo off
 set JAVA_HOME=C:\java6_32
 set HGDB_HOME=d:\classlib\hypergraphdb-1.0
 
set VIEWER_HOME=%CD%
set VIEWER_CLASSPATH=%VIEWER_HOME%/hgdbviewer.jar
set HGDB_NATIVE=%HGDB_HOME%\windows

set JAVA_EXEC="%JAVA_HOME%/bin/java"
%JAVA_EXEC% -version 2>&1 | find "64-Bit" >nul:

if errorlevel 1 (
   REM echo 32-Bit 
   set HGDB_NATIVE=%HGDB_HOME%\native
) else (
   REM  echo 64-Bit
   set HGDB_NATIVE=%HGDB_HOME%\native\amd64
)
REM echo HGDB_NATIVE:  %HGDB_NATIVE%
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