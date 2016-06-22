
Description
-------------
This example illustrates how the Docker plugin can be used to create a image from a Dockerfile
with Oracle Java 7 installed. 

The module contains the /dockersrc directory which is the source directory for the image.
The source directory contain a Dockerfile used to direct the image creation.
Upon creation the source directory is compressed into a TAR archive and uploaded to the Docker daemon
which uses the archive for creation of the image.

This example requires the presence of a Vagrant box running Linux with Docker installed (follow the instructions below).
After execution of the "ssh-011-install-docker-latest-version-centos7" module, then Docker is available at: http://192.168.34.10:8082

The model is targeted to the resource named "docker-node" in the "linux-vagrant" environment.
The "docker-node" resource defines the Docker daemon on Vagrant Box.
The "linux-vagrant" environment defines the Linux environment on the Vagrant box.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the example module named "ssh-011-install-docker-latest-version".
2) Download the Java SE distribution jdk-7u71-linux-x64.rpm and place it in the /dockersrc directory.
3) Execute the example module named "ssh-011-install-docker-latest-version-centos7" to install Docker on the Vagrant box.

Intended operations
-----------------------
* "deploy-configuration"  - which will create the tagged Docker image from the Dockerfile.
* "undeploy-configuration"  - which will delete the Docker image.
* "create-report" - which will list the created Docker images and containers.

The model is targeted to the "deploy-configuration", "undeploy-configuration" and
"create-report" operations. All other operations are NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 
* "dockersrc" - source directory which is compressed into a TAR archive and uploaded to the Docker daemon.
* "dockersrc/Dockerfile" - Dockerfile from which the image is created.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment which defines the Linux environment on the Vagrant box.
