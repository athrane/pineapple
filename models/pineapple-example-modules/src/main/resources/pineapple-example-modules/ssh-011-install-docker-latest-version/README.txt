
Description
-------------
This example illustrates how the SSH plugin can be used to remote install Docker 
in a Vagrant box running CentOS 6.5.

1) Create and add Docker user to sudoers.
2) Installs the EPEL repository which contains the Docker packages.
3) Installs the Docker packages as a OS service.
4) Starts Docker as a OS service. 

Docker is available at: http://192.168.34.10:8082

If SSH is used to access the Vagrant box and control Docker the the created Docker user "docker" can
be used with the Docker daemon without sudo. Change to the the docker user with: sudo su docker.

The model is targeted to the resource named "ssh-node1" in the "linux-vagrant" environment.
The "ssh-node1" resource defines the Vagrant Box where Docker is  installed.
The "linux-vagrant" environment defines an environment with three VM's.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the module.

Intended operations
-----------------------
* "deploy-configuration"  - which will install Docker.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 
* "vagrant/Vagrantfile" - Vagrant file for creation of CentOS 6.5 VM.
* "bin/docker.config" - Docker configuration file.
* "bin/create-docker-user.sh" - shell script to create docker user and add it to sudoers.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
