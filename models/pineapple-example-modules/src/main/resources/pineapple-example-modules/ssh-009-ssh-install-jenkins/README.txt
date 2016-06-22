
Description
-------------
This example illustrates how the SSH plugin can be used to remote install a Jenkins server 
in a Vagrant box and configure a build job.

1) Installs a JDK for using YUM. 
- The JDK is used to run Jenkins and for the build job.
2) Install Jenkins packages using YUM.
3) Test that expected user and group to run Jenkins is created.
4) Install Jenkins job files:
- Jenkins main configuration file.
- Jenkins Maven configuration file. Defines Maven installations's used for CI jobs.
- Pineapple CI build job.
5) Starts the Jenkins server. 

Jenkins is available at: http://192.168.34.10:8080

The model is targeted to the resource named "ssh-node1" in the "linux-vagrant" environment.
The "ssh-node1" resource defines the Vagrant Box where the  CI server is  installed.
The "linux-vagrant" environment defines an environment with three VM's.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the module.

Intended operations
-----------------------
* "deploy-configuration"  - which will install the CI server.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 
* "vagrant/Vagrantfile" - Vagrant file for creation of the CI server VM.
* "bin/config.xml" - Jenkins main configuration file.
* "bin/hudson.tasks.Maven.xml" - Jenkins Maven configuration file.
* "bin/pineapple-build.config.xml" - Jenkins configuration file for the Pineapple build job.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
