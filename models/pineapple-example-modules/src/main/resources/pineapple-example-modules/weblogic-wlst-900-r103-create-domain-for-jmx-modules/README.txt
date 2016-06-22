
Description
-------------
This example illustrates how to invoke a WLST script which creates
a WebLogic 10.3 domain from a domain template file.  

This module is basically a copy of the module "weblogic-wlst-003-r103-create-domain-from-template"
with the only different that the name of the result domain is "alphadomain". 

The purpose of this module is to provide a way to create a domain for the WebLogic JMX Plugin
example modules.  

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/script.py" - WLST script.
* "bin/windows.properties" - WLST property file for Windows.
* "bin/linux.properties" - WLST property file for Linux.
* "bin/domain-template.jar" - Domain template file.

Intended operations
-----------------------
* "deploy configuration"  - which will invoke the script.  
* "undeploy configuration"  - which will invoke the script.  

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "windows-default" - Environment to support execution of modules on a Windows based host with default values.
