
Description
-------------
This example illustrates how to invoke a WLST script which creates
a WebLogic 10.3 domain and configures the administration server to 
support IIOP communication which is a prerequisite for accessing the
WebLogic MBean server using the RMI protocol (using either the JDK or 
the Oracle implementation of IIOP protocol (wljmxclient.jar)).

The script sequence:

1) Script #1: create-domain.py
- creates a Weblogic 10.3 domain from a domain template file 
- configures the domain in WLST offline mode.

2) Script #3: start-and-configure-admserver.py
- starts the administration server for WLST online configuration:
- configures the domain with IIOP enabled, and configures the default IIOP user with 
the same credentials as the weblogic user.

3) Script #3: start-and-access-admserver.py
- starts the administration server for WLST online configuration:
- accesses the server using the IIOP protocol, 
through usage of the Oracle implementation of the HTTP protocol 
which is supplied with WebLogic (wljmxclient.jar). 
- shuts down the administration server

Links:
- wljmxclient.jar: http://docs.oracle.com/cd/E21764_01/web.1111/e13728/accesswls.htm

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/create-domain.py" - WLST script which creates domain from template.
* "start-and-configure-admserver.py" - WLST script which accesses the administration server
* "bin/start-and-access-admserver.py" - WLST script which connects to the administration server using the IIOP protocol.
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
