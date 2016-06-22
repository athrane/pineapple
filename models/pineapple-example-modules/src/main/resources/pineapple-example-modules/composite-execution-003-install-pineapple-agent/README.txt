
Description
-------------
This example illustrates how to execute several composite modules in sequence:
1) ssh-002-install-yum-packages
2) ssh-005-install-jvm-rpm-linux64
4) ssh-007-install-pineapple-agent-linux64

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file the "linux-vagrant" environment. 

Intended operations
-----------------------
Since the two modules prepare and install agents their intended operation should be used:
* "deploy-configuration"  - which will install YUM packages, the JVM and and Pineapple as an agent (i.e. OS service).

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
