CLS
@ECHO OFF
SETLOCAL



@echo ------------------------------------------------------------------
@echo MAVEN 2 ECLIPSE:CLEAN ECLIPSE:ECLIPSE
@echo ------------------------------------------------------------------

call mvn  -DdownloadJavadocs=true -o eclipse:clean eclipse:eclipse

endlocal
