
Description
-------------
This example illustrates how to invoke a WLST script which creates
a WebLogic 10.3 domain and configures the administration server to 
support HTTP tunneling which is a PREREQUISITE for accessing the
WebLogic MBean server using the HTTP protocol.

The administration server is accessed using the Oracle implementation of 
the HTTP protocol supplied with WebLogic.

The script sequence:

2) Script #1: create-domain.py
- creates a Weblogic 10.3 domain from a domain template file 
- configures the domain in WLST offline mode.

2) Script #2: configure-domain.py
- creates a Weblogic 10.3 domain from a domain template file 
- configures the domain in WLST offline mode.

3) Script #3: start-and-access-admserver.py
- starts the administration server for WLST online configuration:
- accesses the server using the HTTP protocol which results in the message:

Connecting to http://127.0.0.1:7001 with userid weblogic ...
Successfully connected to Admin Server 'admserver' that belongs to domain 'alphadomain-005-configure-domain-offline'.
   
- shuts down the administration server

Verification of example behavior
---------------------------------
To verify the example disable tunneling in configure-domain-py by modifying 
the script to:

def configurHttpTunneling():
    cd('/Servers/'+adminServerName)
    cmo.setTunnelingEnabled(false)

This will result in a connection error when the administration is accessed using HTTP: 

..Could not connect to the server to verify that it has started. The error returned is: 
javax.naming.CommunicationException [Root exception is java.net.ConnectException: 
http://127.0.0.1:7001: Destination unreachable; nested exception is: 
java.net.ProtocolException: 
Tunneling result unspecified - is the HTTP server at host: 'localhost' and port: '7001' a WebLogic Server?; 
No available router to destination]


Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/create-domain.py" - WLST script which creates domain from template.
* "bin/configure-domain.py" - WLST script which configures the domain in offline mode.
* "start-and-access-admserver-using-http.py" - WLST script which accesses the administration server
using the HTTP protocol.

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
