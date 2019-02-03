
Description
-------------
This example illustrates how to  create a Pineapple Docker image 
from a empty Linux server using several composite modules in sequence:
1) ssh-011-install-docker-latest-version-centos7
2) docker-004-java8-openjdk-rpm-image-centos
3)docker-005-pineapple-image

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 

Intended operations
-----------------------
* "deploy-configuration"  - which will create the tagged Docker image from the Dockerfile.
* "undeploy-configuration"  - which will delete the Docker image.
* "create-report" - which will list the created Docker images and containers.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment which defines the Linux environment on the Vagrant box.
