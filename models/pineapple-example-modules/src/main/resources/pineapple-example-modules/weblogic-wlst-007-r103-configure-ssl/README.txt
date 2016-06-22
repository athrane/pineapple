
Description
-------------
This example illustrates how to invoke a sequence of WLST scripts which configures
the administration server to use the SSL listen port with generated self signed certificates.

Please notice that the administration will listen on BOTH the HTTP listen port and the
HTTPS listen port (whose configuration is focus of this example).  

The script sequence:

1) Script #1: create-keystores.py
- generates identity and trust key stores for the administration server in a domain
- copies the keystores into the domains directory structure 

2) Script #2: create-domain.py
- creates a Weblogic 10.3 domain from a domain template file 
- configures the domain in WLST offline mode.

3) Script #3: start-and-configure-admserver.py
- starts the administration server for WLST online configuration:
	* configures the domain to use the key stores
	* configures SSL for the admserver
- shuts down the administration server

Notes:
a) Some of configuration of key stores and SSL must be done in WLST online mode which concerns the setting of passwords.
 

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "bin/create-keystores.py" - WLST script which generates identity and trust key stores for the administration server in a domain.
* "bin/create-domain.py" - WLST script which creates domain from template and configures the domain in offline mode.
* "bin/start-and-configure-admserver.py" - WLST script which configures the domain in online mode.
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