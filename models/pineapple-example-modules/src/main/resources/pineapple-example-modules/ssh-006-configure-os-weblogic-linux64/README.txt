
Description
-------------
This example illustrates how the SSH plugin can be used to remote configure
the operating system to support the installation of WebLogic:

1) Create the group "oinstall".
2) Creates user "weblogic" and add it as member of the "oinstall" group.
3) Creates directories used by the middleware as described in the whitepaper 
"Oracle WebLogic on Shared Storage: Best Practices" found at:
http://www.oracle.com/technetwork/database/availability/maa-fmwsharedstoragebestpractices-402094.pdf

The model is targeted to any resource in the "linux-vagrant" environment which matches 
the match pattern "ssh-node*", e.g. ssh-node1, ssh-node2,...

Required modifications
-----------------------
Create some Vagrant boxes which are assigned to the IP addresses defined in the "linux-vagrant" example environment.

Intended operations
-----------------------
* "deploy configuration"  - which will configure the OS at the targeted SSH hosts.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
