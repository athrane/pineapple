
Description
-------------
This example illustrates how to install JRockit R27.6.3 on 32-bit Linux
with default path values.

The default installation path for the JVM is /${user}/jrockit-jdk.....
where ${user} is resolved to the current user by the installer.

The module will assume ${user} to bs "weblogic", hence it will
install the JVM at /weblogic/jrockit-jdk.....

Required modifications
-----------------------
Download the installer named "jrmc-3.1.0-1.5.0-linux-ia32.bin" 
and place it in the "bin" directory.

Intended operations
-----------------------
* "deploy configuration"  - which will install JRockit.  
* "undeploy configuration"  - which will uninstall JRockit..  

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a local Linux based host with default values.

