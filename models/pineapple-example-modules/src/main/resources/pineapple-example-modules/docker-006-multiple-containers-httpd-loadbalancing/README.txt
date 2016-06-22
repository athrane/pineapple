
Description
-------------
This example illustrates how the Docker plugin can be used to create multiple Docker containers
using HTTPD as a basic load balancer with 4 load balanced web servers.

The web servers are created from three images using several Dockerfiles.
The module contains the /dockersrc directory which is the source directory for the images.
Three images are defined:
a) httpd-loadbalancer
b) httpd-backend
c) httpd-backend2

The Dockerfile for the httpd-loadbalancer image installs HTTPD with a load balancer configuration and exposure of port 80 from the container.
The load balancer configuration is defined in the file mod_proxy.conf which forward requests to http://httpd1 - http://httpd4.
THe DNS httpd1 - httpd4 is defined in the load-balancer container using network links.

The Dockerfile for the httpd-backend image installs the installation of HTTPD, installation of a simple "Hello HTTPD" page 
and exposure of port 80 from the container.

The Dockerfile for the httpd-backend2 image installs the installation of HTTPD, installation of a simple "Hello HTTPD2" page 
and exposure of port 80 from the container.

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
* "deploy-configuration"  - which will create the tagged Docker image from the Dockerfile.
* "undeploy-configuration"  - which will delete the Docker image and the container.
* "create-report" - which will list the created Docker images and containers.

The model is targeted to the "deploy-configuration", "undeploy-configuration" and
"create-report" operations. All other operations are NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 
* "dockersrc/httpd-loadbalancer" - source directory which is compressed into a TAR archive and uploaded to the Docker daemon.
* "dockersrc/httpd-loadbalancer/Dockerfile" - Dockerfile from which the image is created.
* "dockersrc/httpd-loadbalancer/mod_proxy.conf" - Load balancer configuration file.
* "dockersrc/httpd-backend" - source directory which is compressed into a TAR archive and uploaded to the Docker daemon.
* "dockersrc/httpd-backend/Dockerfile" - Dockerfile from which the image is created.
* "dockersrc/httpd-backend2" - source directory which is compressed into a TAR archive and uploaded to the Docker daemon.
* "dockersrc/httpd-backend2/Dockerfile" - Dockerfile from which the image is created.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment which defines the Linux environment on the Vagrant box.
