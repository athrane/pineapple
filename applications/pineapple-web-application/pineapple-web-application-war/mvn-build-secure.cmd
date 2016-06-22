CLS
@ECHO OFF
SETLOCAL

set MAVEN_OPTS=-Djavax.net.ssl.trustStore=C:\tools\apache-maven-3.3.3\conf\zkee.jks -Djavax.net.ssl.trustStorePassword=secret -Djavax.net.debug=ssl

@echo ------------------------------------------------------------------
@echo MAVEN CLEAN
@echo ------------------------------------------------------------------

call mvn clean 

@echo ------------------------------------------------------------------
@echo MAVEN INSTALL
@echo ------------------------------------------------------------------

call mvn install -e 

endlocal
