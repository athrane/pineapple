
Description
-------------
This example illustrates how to install WebLogic 12.1.1 on 32-bit Linux.
The example illustrates how to install the product in two ways:

1) Using default path values (e.g. in the model named "linux-default").
For a description of the used values:
http://docs.oracle.com/cd/E21764_01/install.1111/b32474/concepts.htm#CHDCIECE

2) Using path values are from the enterprise deployment documentation for
the Fusion Middleware (e.g. in the model named "linux-enterprise").
For a description of the used values:
http://docs.oracle.com/cd/E21764_01/install.1111/b32474/concepts.htm#CHDCIECE
(SOA Suite) http://docs.oracle.com/cd/E23943_01/core.1111/e12037/file_sys.htm
(WebCenter) http://docs.oracle.com/cd/E21043_01/core.1111/e12037/net.htm#CIHB


Required modifications
-----------------------
Download the WebLogic installer named "wls1211_linux32.bin" and place it in the "bin" directory.

Intended operations
-----------------------
* "deploy configuration"  - which will install WebLogic.  
* "undeploy configuration"  - which will uninstall WebLogic.  

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "linux-enterprise" - Environment to support execution of modules on a Linux based host in a enterprise setup.