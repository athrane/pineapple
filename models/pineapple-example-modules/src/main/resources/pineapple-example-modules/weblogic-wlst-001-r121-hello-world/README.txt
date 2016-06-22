
Description
-------------
This example illustrates how to invoke a hello world WLST script in WebLogic 12.1.  

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/script.py" - WLST script.

Intended operations
-----------------------
* "deploy configuration"  - which will invoke the script.  
* "undeploy configuration"  - which will invoke the script.  

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "windows-default" - Environment to support execution of modules on a Windows based host with default values.
