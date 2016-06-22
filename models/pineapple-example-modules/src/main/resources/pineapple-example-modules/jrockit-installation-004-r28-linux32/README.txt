
Description
-------------
This example illustrates how to install JRockit R28.2.3 on 32-bit Linux.
The example illustrates how to install the product in two ways:

1) Using default path values (e.g. in the model named "linux-default"). 
The default installation path for the JVM is /${user}/jrockit-jdk.....
where ${user} is resolved to the current user by the installer.
The module will assume ${user} to be "weblogic", hence it will
install the JVM at /weblogic/jrockit-jdk.....

2) Using path values are from the enterprise deployment documentation for
the Fusion Middleware (e.g. in the model named "linux-enterprise").

For a description of the used values:
http://docs.oracle.com/cd/E25054_01/fusionapps.1111/e21032/install.htm#CHDECGEF

Required modifications
-----------------------
Download the installer named "jrockit-jdk1.6.0_31-R28.2.3-4.1.0-linux-ia32.bin" 
and place it in the "bin" directory.

Intended operations
-----------------------
* "deploy configuration"  - which will install JRockit.  
* "undeploy configuration"  - which will uninstall JRockit. 

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "linux-enterprise" - Environment to support execution of modules on a Linux based host in a enterprise setup.
