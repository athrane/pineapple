
Description
-------------
This example illustrates how the SSH plugin can be used to remote install Docker 
wit a fxied version in a Vagrant box running  CentOS 7.0 based on the instructions can found here: 
https://docs.docker.com/engine/installation/centos/

The Docker is defined bu the variable ${docker-verion}.
For a list of alternate docker versions for CentOS 7, please visit https://yum.dockerproject.org/repo/main/centos/7/Packages/

1) Create and add Docker user to sudoers.
1) Update YUM (can take some time).
2) Add Docker repository to YUM configuration.
3) Installs the Docker as a OS service using systemd (https://docs.docker.com/engine/articles/systemd/).
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
* "deploy-configuration"  - will install Docker.
* "undeploy-configuration"  - will uninstall Docker.

The model is targeted to the "deploy-configuration" and the "undeploy-configuration" operations. 
All other operations is NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 
* "vagrant/Vagrantfile" - Vagrant file for creation of CentOS 7.0 VM.
* "bin/docker.repo" - Docker YUM repository.
* "bin/docker.service" - Docker systemd configuration file.
* "bin/docker.conf" - Docker systemd drop-in configuration file.
* "bin/create-docker-user.sh" - shell script to create docker user and add it to sudoers.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
