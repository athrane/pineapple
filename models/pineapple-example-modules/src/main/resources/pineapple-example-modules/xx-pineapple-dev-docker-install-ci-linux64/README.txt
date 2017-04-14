
Description
-------------

This example illustrates how the SSH plugin and Docker plugin can be used to remote install a 
continuous integration server in a Vagrant box. 
The CI server consists of a Docker host with Jenkins and Artifactory Docker containers which are integrated. 
A Jenkins build is also installed which uses the integration:

1) Installs Docker 
2) Creates a Jenkins image (named pineapple/ci:1.0) based on the official Jenkins Docker image.
This includes installation of Jenkins job files:
- Jenkins main configuration file.
- Jenkins Maven configuration file. Defines Maven installations's used for CI jobs.
- Pineapple CI build job.
3) Creates a Artifactory image (named pineapple/repo:1.0) based on the official Artifactory Docker image.
4) Creates and starts a Artifactory Docker container.
5) Creates and starts a Jenkins Docker container and links it with the Artifactory container.
6) Initialize Artifactory.
7) Installs 3rd party artifacts in Artifactory.

The model is targeted to the resources named "ssh-ci-node1" and  "ssh-ci-node2" in the "linux-pineapple-ci" environment.
The "ssh-ci-node1" resource defines the Vagrant Box where the CI server is installed.
The "ssh-ci-node2" resource defines the Vagrant Box used to host Docker.

Jenkins is available at: http://192.168.99.10:8080
Artifactory is available at: http://192.168.99.10:8081
Docker is available at: http://192.168.99.10:8082
Docker is available at: http://192.168.99.11:8082

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the module.
2) Add the weblogic-full-client-VERSION.jar and other 3rd party artifacts to the bin directory.

Intended operations
-----------------------
* "deploy-configuration"  - which will install the CI server.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Module content
-----------------------
* "models/linux-pineapple-ci.xml" - model file for the "linux-vagrant" environment. 
* "vagrant/Vagrantfile" - Vagrant file for creation of the CI server VM.
* "bin/artifactory.config.xml" - Artifactory main configuration file.
* "bin/create-docker-user.sh" - shell script for setting the Docker user to run the Docker host under.
* "bin/daemon.json" - Docker daemon configuration file.
* "weblogic-full-client-12.1.2.pom" - Maven POM for the WebLogic client JAR.
* "dockersrc/ci/config.xml" - Jenkins main configuration file.
* "dockersrc/ci/Dockerfile" - Dockerfile for building the pineapple/ci image.
* "dockersrc/ci/hudson.tasks.Maven.xml" - Jenkins Maven configuration file.
* "dockersrc/ci/pineapple-build-test.config.xml" - Jenkins configuration file for the Pineapple build job.
* "dockersrc/ci/pineapple-build.config.xml" - Jenkins configuration file for the Pineapple build job.
* "dockersrc/ci/plugin.xml" - List of Jenkins plugins to install (used by the official Jenkins image).
* "dockersrc/ci/settings.xml" - Main configuration file used to configured Maven settings in build jobs
* "dockersrc/ci/Dockerfile" - Dockerfile for building the pineapple/repo image.

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-ci" - Environment which defines the Pineapple CI environment.
