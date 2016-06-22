
Description
-------------
This example illustrates how the composite execution plugin can be used to install a 
local WebLogic environment for development. JRockit 4.1 will be used as JVM along 
with WebLogic 12.1.1 on a 64-bit Windows. The software will be installed with 
default path values.

The used composite modules:

1) jrockit-installation-003-r28-win64
- Installs JRockit with default Windows path values
2) weblogic-installation-005-r1211-win64
- Installs WebLogic with default Windows path values
3) weblogic-wlst-003-r121-create-domain-from-template
- Creates a domain in c:/app/oracle/admin/alphadomain-003-create-domain-from-template 
where you can execute startWebLogic.cmd to start the administration server. 
The server can be accessed at http:127.0.0.1:7001/console with the weblogic/Weblogic99

Required modifications
-----------------------
Download the installer named "jrockit-jdk1.6.0_31-R28.2.3-4.1.0-windows-x64.exe" 
and place it in the "bin" directory in the "jrockit-installation-003-r28-win64" module

Download the WebLogic installer named "wls1211_generic.jar" and place it in the "bin" directory
of the "weblogic-installation-005-r1211-win64" module.

Intended operations
-----------------------
* "deploy configuration"  - which will install the software and create the domain.
* "undeploy configuration"  - which will uninstall the software.  

Module content
-----------------------
* "models/windows-default.xml" - model file the "windows-default" environment.

Environments
-----------------------
The module contains models for the environments:
* "windows-default" - Environment to support execution of modules on a Windows based host with default values.
