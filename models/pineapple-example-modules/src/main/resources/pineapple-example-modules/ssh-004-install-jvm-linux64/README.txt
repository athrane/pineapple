
Description
-------------
This example illustrates how the SSH plugin can be used to remote install 
a 64 bit Java Virtual Machine(JVM) in Linux from a binary archive file (.GZ).  

The model is targeted to any resource in the "linux-vagrant" environment which matches 
the match pattern "ssh-node*", e.g. ssh-node1, ssh-node2,...

Required modifications
-----------------------
Create some Vagrant boxes which are assigned to the IP addresses defined in the "linux-vagrant" example environment.

Download the Java SE distribution jdk-7u40-linux-x64.tar.gz and place it in the /bin directory.

Intended operations
-----------------------
* "deploy configuration"  - which will install the JVM at the targeted SSH hosts.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.