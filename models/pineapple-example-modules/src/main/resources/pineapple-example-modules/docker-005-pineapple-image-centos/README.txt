
Description
-------------
This example illustrates how the Docker plugin can be used to create a image from a Dockerfile
with Pineapple installed.  

The module contains the /bin directory which is the source directory for the image.
The source directory contain a Dockerfile used to direct the image creation.
Upon creation the source directory is compressed into a TAR archive and uploaded to the Docker daemon
which uses the archive for creation of the image.

This example requires the presence of a Vagrant box running Linux with Docker installed (follow the instructions below).
After execution of the "ssh-011-install-docker-latest-version-centos7" module, then Docker is available at: http://192.168.34.10:8082

Pineapple is installed in the most simple fashion, basically an unzip with no consideration for the used user.
Pineapple will run at http:/0.0.0.0:8080 within the Docker image under the pineapple:pineapple user.
The image defines exposure of port 8080 to the host with no port mapping.
The image is configured to use /var/pineapple as the Pineapple home directory.
The home directory is mapped to a volume.

The model is targeted to the resource named "docker-node" in the "linux-vagrant" environment.
The "docker-node" resource defines the Docker daemon on Vagrant Box.
The "linux-vagrant" environment defines the Linux environment on the Vagrant box.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the example module named "ssh-011-install-docker-latest-version".
2) Download Pineapple pineapple-standalone-web-client-1.8.0.zip and place it in the /dockersrc directory.
2) Execute the example module named "ssh-011-install-docker-latest-version-centos7" to install Docker on the Vagrant box.
3) Execute the example module named "docker-004-java8-rpm-image-from-dockerfile-centos" to create a Docker image with Java.

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
* "bin" - source directory which is compressed into a TAR archive and uploaded to the Docker daemon.
* "bin/Dockerfile" - Dockerfile from which the image is created.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment which defines the Linux environment on the Vagrant box.
