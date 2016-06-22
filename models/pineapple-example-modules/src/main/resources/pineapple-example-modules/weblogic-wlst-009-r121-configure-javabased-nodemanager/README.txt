
Description
-------------
This example illustrates how to invoke a sequence of WLST scripts which configures
the administration server and the Java-based NodeManager under Linux.

The script sequence:

1) Script #1: configure-nodemanager.py
- ...configure the node manager...

2) Script #2: create-keystores.py
- generates identity and trust key stores for the administration server in a domain
- copies the keystores into the domains directory structure 

3) Script #3: create-domain.py
- creates a Weblogic 10.3 domain from a domain template file 
- configures the domain in WLST offline mode:
	* configures SSL for the admserver, using the DEMO trust.

4) Script #4: start-and-configure-admserver.py
- starts the administration server for WLST online configuration:
	* configures the domain to use the key stores
	* configures SSL for the admserver
	* disables the HTTP listen port.
- shuts down the administration server

5) Script #5: enroll-admserver.py
- enrolls the administration server under node manager control:
	* starts the admserver
	* enrolls the server
	* shuts down the server	
- starts the server under node manager control:	
	* connects to the node manager
	* starts the server under node manager control
	* disconnects fromthe node manager

Notes:
a) Some of configuration of key stores and SSL must be done in WLST online mode which 
concerns the setting of passwords.

b) Since the server is configured to use DEMO trust in step 3) the WLST must
connect to the running server in step 4) using DEMO trust. This is done by 
setting the attribute demo-trust-enabled="true" when WLSt is invoked in step 4.

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/create-keystores.py" - WLST script which generates identity and trust key stores for the administration server in a domain.
* "bin/create-domain.py" - WLST script which creates domain from template and configures the domain in offline mode.
* "bin/start-and-configure-admserver.py" - WLST script which configurs the domain in online mode.
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
