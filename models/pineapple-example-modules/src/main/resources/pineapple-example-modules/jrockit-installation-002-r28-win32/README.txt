
Description
-------------
This example illustrates how to install JRockit R28.2.3 on 32-bit Windows.
1) Using default path values (e.g. in the model named "windows-default"). 

2) Using path values are adapted from the enterprise deployment documentation for
the Fusion Middleware to Windows (e.g. in the model named "windows-enterprise").
Strangely enough the enterprise deployment documentation concentrates on Linux 
and lacks examples for Windows. The values are adapted from Linux where the suggested path 
"/u01/app/oracle/product/fmw/wlserver_10.3" is modified by mapping "/u01" to "c:\".

For a description of the used values:
http://docs.oracle.com/cd/E21764_01/install.1111/b32474/concepts.htm#CHDCIECE
(SOA Suite) http://docs.oracle.com/cd/E23943_01/core.1111/e12037/file_sys.htm
(WebCenter) http://docs.oracle.com/cd/E21043_01/core.1111/e12037/net.htm#CIHB

Required modifications
-----------------------
Download the installer named "jrockit-jdk1.6.0_31-R28.2.3-4.1.0-windows-ia32.exe" 
and place it in the "bin" directory.

Intended operations
-----------------------
* "deploy configuration"  - which will install JRockit.  
* "undeploy configuration"  - which will uninstall JRockit.  

Environments
-----------------------
The module contains models for the environments:
* "windows-default" - Environment to support execution of modules on a Windows based host with default values.
* "windows-enterprise" - Environment to support execution of modules on a Windows based host in a enterprise setup.
