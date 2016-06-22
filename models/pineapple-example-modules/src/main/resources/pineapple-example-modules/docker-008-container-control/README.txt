
Description
-------------
This example illustrates how the Docker plugin supports creation/destruction and control of Docker containers.

When the module is invoked with the deploy-configuration operation the busybox:lasted image is pulled
and a container named "alpha01" is created from the busybox base image. The container is then started.

When the module is invoked with the undeploy-configuration operation the busybox:lasted image is left alone 
i.e. not deleted) and the "alpha01" container is stopped and deleted.

This example requires the presence of a Vagrant box running Linux with Docker installed (follow the instructions below).
After execution of the "ssh-011-install-docker-latest-version-centos7" module, then Docker is available at: http://192.168.34.10:8082

The model is targeted to the resource named "docker-node" in the "linux-vagrant" environment.
The "docker-node" resource defines the Docker daemon on Vagrant Box.
The "linux-vagrant" environment defines the Linux environment on the Vagrant box.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the example module named "ssh-011-install-docker-latest-version-centos7".
2) Execute the example module named "ssh-011-install-docker-latest-version-centos7" to install Docker on the Vagrant box.

Intended operations
-----------------------
* "deploy-configuration"  - which will create the Docker container and start it.
* "undeploy-configuration"  - which will stop and delete the Docker container.
* "create-report" - which will list the created Docker images and containers.

The model is targeted to the "deploy-configuration", "undeploy-configuration" and
"create-report" operations. All other operations are NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment which defines the Linux environment on the Vagrant box.
