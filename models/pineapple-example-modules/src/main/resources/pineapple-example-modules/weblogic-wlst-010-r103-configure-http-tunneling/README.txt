
Description
-------------
This example illustrates how to invoke a WLST script which creates
a WebLogic 10.3 domain and configures the administration server to 
support HTTP tunneling which is a prerequisite for accessing the
WebLogic MBean server using the HTTP protocol.

The script sequence:

1) Script #1: create-domain.py
- creates a Weblogic 10.3 domain from a domain template file 
- configures the domain in WLST offline mode.

2) Script #2: configure-domain.py
- creates a Weblogic 10.3 domain from a domain template file 
- configures the domain in WLST offline mode.

3) Script #3: start-and-access-admserver.py
- starts the administration server for WLST online configuration:
- accesses the server using the HTTP protcol, by usage of Oracle implementation
of the HTTP protocol supplied with WebLogic (wljmxclient.jar)).
 - shuts down the administration server

- accesses the server using the HTTP protocol, 
through usage of the Oracle implementation of the HTTP protocol which is 
supplied with WebLogic (wljmxclient.jar). 


Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/create-domain.py" - WLST script which creates domain from template.
* "bin/configure-domain.py" - WLST script which configures the domain in offline mode.
* "bin/start-and-access-admserver.py" - WLST script which connects to the administration server using the HTTP protocol.

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
