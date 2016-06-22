
Description
-------------
This example illustrates how the SSH plugin can be used to remote install Pineapple agents.

The model is targeted to any resource in the "linux-vagrant" environment which matches 
the match pattern "ssh-node*", e.g. ssh-node1, ssh-node2,...

The agent is configured to listen on http://0.0.0.0:7099. 

Required modifications
-----------------------
Create some Vagrant boxes which are assigned to the IP addresses defined in the "linux-vagrant" example environment.
Add the Pineapple standalone client pineapple-standalone-web-client-VERSION.zip into the /bin directory

Intended operations
-----------------------
* "deploy configuration"  - which will install the agents at the targeted SSH hosts.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 
* "models/linux-pineapple-test-infrastructure.xml" - model file for the "linux-pineapple-test-infrastructure" environment. 
* "bin/resources.xml" - Pineapple environment configuration file used to configure resources.
* "bin/credentials.xml" - Pineapple environment configuration file used to configure credentials.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
* "linux-pineapple-test-infrastructure" - Environment to support execution of modules in a environment for testing Pineapple.
