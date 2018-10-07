
Description
-------------

This example illustrates how the Docker plugin can be used to restart the Jenkins and Artifactory Docker containers 
in the continuous integration server in a Vagrant box. 

1) Restart the Docker container 

The model is targeted to the resource named "docker-ci-node1" in the "linux-pineapple-ci" environment.
The "docker-ci-node1" resource defines the Docker host where the CI server is installed.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the module "xx-pineapple-dev-docker-install-ci-linux64".

Intended operations
-----------------------
* "deploy-configuration"  - which will start the containers.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Module content
-----------------------
* "models/linux-pineapple-ci.xml" - model file for the "linux-vagrant" environment. 

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-ci" - Environment which defines the Pineapple CI environment.
