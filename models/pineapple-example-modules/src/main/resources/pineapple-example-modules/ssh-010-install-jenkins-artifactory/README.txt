
Description
-------------

This example illustrates how the SSH plugin can be used to remote install a 
continuous integration server in a Vagrant box. The CI server consists of 
Jenkins and Artifactory which are integrated. A Jenkins build is also 
installed which uses the integration:

1) Installs a JDK for using YUM. The JDK is used to run Jenkins and Artifactory.
2) Configures YUM with Artifactory package repository. 
3) Installs Artifactory packages using YUM. 
4) Starts the Artifactory server (as a OS service). 
5) Initialize Artifactory.
6) Installs Jenkins packages using YUM.
7) Installs Jenkins job files:
- Jenkins main configuration file.
- Jenkins Maven configuration file. Defines Maven installations's used for CI jobs.
- Pineapple CI build job.
8) Starts the Jenkins server (as a OS service). 

The model is targeted to the resource named "ssh-node1" in the "linux-vagrant" environment.
The "ssh-node1" resource defines the Vagrant Box where the  CI server is  installed.
The "linux-vagrant" environment defines an environment with three VM's.

Jenkins is available at: http://192.168.34.10:8080
Artifactory is available at: http://192.168.34.10:8081

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the module.

Intended operations
-----------------------
* "deploy-configuration"  - which will install the CI server.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Module content
-----------------------
* "models/linux-pineapple-ci.xml" - model file for the "linux-vagrant" environment. 
* "vagrant/Vagrantfile" - Vagrant file for creation of the CI server VM.
* "bin/config.xml" - Jenkins main configuration file.
* "bin/hudson.tasks.Maven.xml" - Jenkins Maven configuration file.
* "bin/pineapple-build.config.xml" - Jenkins configuration file for the Pineapple build job.
* "bin/artifactory.repo" - YUM artifactory repository configuration file.
* "bin/artifactory.config.xml" - Artifactory main configuration file.
* "bin/settings.xml" - Main configuration file used to configured Maven settings in build jobs

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-ci" - Environment which defines the Pineapple CI environment.
