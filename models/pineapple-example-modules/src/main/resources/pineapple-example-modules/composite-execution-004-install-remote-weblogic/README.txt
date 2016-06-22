
Description
-------------
This example illustrates how to execute several composite modules in sequence:
1) ssh-002-install-yum-packages
2) ssh-005-install-jvm-rpm-linux64
3) ssh-006-configure-os-weblogic-linux64
4) ssh-007-install-pineapple-agent-linux64
5) weblogic-installation-009-r1212-linux64

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file the "linux-vagrant" environment. 

Intended operations
-----------------------
The intended operation is:
* "deploy-configuration" - which will install YUM packages, the JVM, the Pineapple agent 
and WebLogic 12.1.2

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
