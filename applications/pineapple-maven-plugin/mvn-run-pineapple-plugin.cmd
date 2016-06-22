CLS
@ECHO OFF
SETLOCAL

@echo ------------------------------------------------------------------
@echo MAVEN 2 RUN maven-pineapple-plugin
@echo ------------------------------------------------------------------

set GROUP_ID=com.alpha.pineapple
set ARTIFACT_ID=pineapple-maven-plugin
set VERSION=1.0-SNAPSHOT
set GOAL=install-config-test

set TARGET_GROUP_ID=com.alpha.pineapple
set TARGET_ARTIFACT_ID=pineapple-maven-net-plugin

mvn -X %GROUP_ID%:%ARTIFACT_ID%:%VERSION%:%GOAL% -Dpineapple.environment=target-env

ENDLOCAL
