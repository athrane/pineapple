

Description
-------------
This example illustrates how to install WebLogic 10.3.6 on 64-bit Linux.
The example illustrates how to install the product in three ways:

1) Using default path values (e.g. in the model named "linux-default").
For a description of the used values:
http://docs.oracle.com/cd/E21764_01/install.1111/b32474/concepts.htm#CHDCIECE

2) Using path values are from the enterprise deployment documentation for
the Fusion Middleware (e.g. in the model named "linux-enterprise").
For a description of the used values:
http://docs.oracle.com/cd/E25054_01/fusionapps.1111/e21032/install.htm#CHDECGEF

3) Using path values described in the Exalogic Enterprise Deployment guide 
(E18479-08) (e.g. in the model named "linux-exalogic" ).  
For a description of the used values:
http://docs.oracle.com/cd/E18476_01/doc.220/e18479.pdf

Required modifications
-----------------------
Download the WebLogic installer named "wls1036_generic.jar" and place it in the "bin" directory.
Intended operations
-----------------------
* "deploy configuration"  - which will install JRockit.  
* "undeploy configuration"  - which will uninstall JRockit.  

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "linux-enterprise" - Environment to support execution of modules on a Linux based host in a enterprise setup.
* "linux-exalogic" - Environment to support execution of modules on a Linux based host as 
described in the Exalogic Enterprise Deployment guide (E18479-08).
