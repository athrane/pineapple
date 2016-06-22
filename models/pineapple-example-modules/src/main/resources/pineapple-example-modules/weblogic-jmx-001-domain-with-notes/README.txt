
Description
-------------
This example illustrates how to invoke a JMX model in WebLogic with the Domain MBean
defined and its "Notes" attribute.

Required modifications
-----------------------
The models uses the target resource named "weblogic-edit-jmx" for the two environments "windows-default" and
"linux-default" which are both created as part of the default configuration. The "weblogic-edit-jmx" 
resource in defines communication with a local WebLogic server listening at 127.0.0.1:7001, 
with domain name "alphadomain" and credentials weblogic/WebLogic99. 

Such a domain can be created server can be create by execution of the module named 
"weblogic-wlst-900-r103-create-domain-for-jmx-modules" for WebLogic 10.3.x 
or "weblogic-wlst-900-r121-create-domain-for-jmx-modules" for WebLogic 12.1.x


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
