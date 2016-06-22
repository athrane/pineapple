


set  BEA_HOME=C:\local-bea
set  WL_HOME=C:\local-bea\weblogic92

set JAVA_HOME=C:\jrockit\R27.6.3-1.5.0_17
set JAVA_VENDOR=BEA
set JAVA_VM=-jrockit

set  WL_USE_X86DLL=true

if "%PATCH_CLASSPATH%" == "" set PATCH_CLASSPATH=%BEA_HOME%\patch_weblogic923\profiles\default\sys_manifest_classpath\weblogic_patch.jar
set WEBLOGIC_CLASSPATH=%PATCH_CLASSPATH%;%JAVA_HOME%\lib\tools.jar;%WL_HOME%\server\lib\weblogic_sp.jar;%WL_HOME%\server\lib\weblogic.jar;%WL_HOME%\server\lib\webservices.jar

 if "%WL_USE_X86DLL%" == "true" set PATH=%PATCH_PATH%;%WL_HOME%\server\native\win\32;%WL_HOME%\server\bin;%JAVA_HOME%\jre\bin;%JAVA_HOME%\bin;%PATH%;%WL_HOME%\server\native\win\32\oci920_8

set MEM_ARGS=-Xms256m -Xmx512m -Xmanagement:ssl=false,authenticate=false,port=7091 -Dcom.sun.management.jmxremote
Set WLS_ARGS=-Dweblogic.RootDirectory=c:\domains\alphadomain 
Set WLS_SEC_ARGS=-Dweblogic.management.username=weblogic -Dweblogic.management.password=weblogic
Set WLS_DM_ARGS=-Dweblogic.Name=admserver -Dweblogic.Domain=alphadomain -Dweblogic.management.GenerateDefaultConfig=true
Set WLS_SRV_ARGS=-Dweblogic.ListenAddress=127.0.0.1 -Dweblogic.ListenPort=7001

set CLASSPATH=%WEBLOGIC_CLASSPATH%;%CLASSPATH%

@echo.
@echo CLASSPATH=%CLASSPATH%
@echo.
@echo PATH=%PATH%
@echo.
@echo Your environment has been set.

"%JAVA_HOME%\bin\java" %JAVA_VM% %MEM_ARGS% %WLS_ARGS% %WLS_SEC_ARGS% %WLS_DM_ARGS% %WLS_SRV_ARGS% weblogic.Server 

