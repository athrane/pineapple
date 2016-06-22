
Description
-------------
This example illustrates how to invoke a WLST script which creates
a WebLogic 10.3 domain from a domain template file and configure
it in WLST online mode.

The example sets the NodeManager credentials which can only be manipulated
in online mode.

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/create-domain.py" - WLST script which creates domain from template.
* "bin/configure-domain.py" - WLST script which configurs the domain in offline mode.
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
