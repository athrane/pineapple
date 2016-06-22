
Description
-------------
This example illustrates how to invoke a JMX model in WebLogic with an empty domain.

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.

Intended operations
-----------------------
* "deploy configuration"  - which will create the configuration defined in the model in the WebLogic MBean Model.
* "undeploy configuration"  - which will delete the configuration defined in the model in the WebLogic MBean Model.
* "test configuration"  - which will test the configuration defined in the model versus the state of the WebLogic MBean Model.

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "windows-default" - Environment to support execution of modules on a Windows based host with default values.
