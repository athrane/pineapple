
Description
-------------
This example illustrates how to configure the proxy settings for YUM and 
install some YUM packages in Linux (specifically the Linux variants Centos and Red Hat).configure

The model is targeted to any resource in the "linux-vagrant" environment which matches 
the match pattern "ssh-node*", e.g. ssh-node1, ssh-node2,...


Required modifications
-----------------------
Create some Vagrant boxes which are assigned to the IP addresses defined in the "linux-vagrant" example environment.

Intended operations
-----------------------
* "deploy configuration"  - which will install the YUM packages at the targeted SSH hosts.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.