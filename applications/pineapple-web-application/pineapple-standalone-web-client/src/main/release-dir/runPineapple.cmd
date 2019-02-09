@echo off
setlocal enableextensions 

::
:: Define default values if not provided
::

if not defined PINEAPPLE_HOME      set PINEAPPLE_HOME=%USERPROFILE%\.pineapple
if not defined PINEAPPLE_HTTP_HOST set PINEAPPLE_HTTP_HOST=localhost
if not defined PINEAPPLE_HTTP_PORT set PINEAPPLE_HTTP_PORT=8080
if not defined LOG4J_PROPERTIES    set LOG4J_PROPERTIES=file:.\\conf\\log4j.properties

::
:: Check if JAVA_HOME is defined and fail if not
::

if not defined JAVA_HOME for %%I in (java.exe) do set JAVA_EXE_PATH=%%~dp$PATH:I
if defined JAVA_EXE_PATH set JAVA_HOME=%JAVA_EXE_PATH:~0,-5%
if not defined JAVA_HOME (
	echo ERROR: Can't find any Java installation in the path or via the JAVA_HOME environment variable!
	exit /b 1
)

set JAVA_EXE=%JAVA_HOME%\bin\java.exe

::
:: Check if JAVA executable exist and fail if not
::

if not exist "%JAVA_EXE%" (
	echo ERROR: Can't find any Java installation!
	exit /b 1
)

::
:: Get the current directory and remove the last trailing back slash characters
::

set CURRENT_DIRECTORY=%~dp0
set CURRENT_DIRECTORY=%CURRENT_DIRECTORY:~0,-1%

::
:: Configure JVM, Log4J and Pineapple options
::

set JVM_OPTS=
set PINEAPPLE_OPTIONS="-Dpineapple.jettystarter.home=%CURRENT_DIRECTORY%"
set PINEAPPLE_OPTIONS=%PINEAPPLE_OPTIONS% "-Dpineapple.home.dir=%PINEAPPLE_HOME%"
set PINEAPPLE_OPTIONS=%PINEAPPLE_OPTIONS% -Dpineapple.jettystarter.host=%PINEAPPLE_HTTP_HOST%
set PINEAPPLE_OPTIONS=%PINEAPPLE_OPTIONS% -Dpineapple.jettystarter.port=%PINEAPPLE_HTTP_PORT%
set PINEAPPLE_OPTIONS=%PINEAPPLE_OPTIONS% -Dpineapple.jettystarter.stdoutlogging=true
set LOG4J_OPTS=-Dslf4j=false -Dlog4j.configuration=%LOG4J_PROPERTIES%

set PINAPPLE_JAR=./lib/pineapple-jetty-starter-${pineapple.release.version}.jar

echo.
echo Pineapple configuration details:
echo.
echo - Current directory        : %CURRENT_DIRECTORY%
echo - Pineapple home directory : %PINEAPPLE_HOME%
echo - Pineapple host name      : %PINEAPPLE_HTTP_HOST%
echo - Pineapple port number    : %PINEAPPLE_HTTP_PORT%
echo - Pineapple endpoint       : http://%PINEAPPLE_HTTP_HOST%:%PINEAPPLE_HTTP_PORT%/
echo - Java home                : %JAVA_HOME%
echo.
echo Starting Pineapple - a browser window will be opened unless your computer forbids it.

set CMD_LINE="%JAVA_EXE%" %JVM_OPTS% %PINEAPPLE_OPTIONS% %LOG4J_OPTS% %JETTY_OPTS% -jar %PINAPPLE_JAR%

echo.
echo %CMD_LINE%
%CMD_LINE%
set EXITCODE=%errorlevel%
echo.

echo Pineapple terminated with exit code %EXITCODE%
exit /B %EXITCODE%

endlocal
