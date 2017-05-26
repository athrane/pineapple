
Description
-------------
This example illustrates how the SSH plugin can be used to clean up a Docker host.

This examples requires the presence of a Vagrant box running Linux with Docker installed (follow the instructions below).
After execution of the "ssh-011-install-docker-latest-version" module, then Docker is available at: http://192.168.34.10:8082

The model is targeted to the resource named "ssh-node1" in the "linux-vagrant" environment.
The "ssh-node1" resource defines the Vagrant Box where Docker is  installed.
The "linux-vagrant" environment defines an environment with three VM's.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the module.
2) Create some containers.
Intended operations
-----------------------
* "deploy-configuration"  - will start the Docker host.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
