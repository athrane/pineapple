
Description
-------------
This example illustrates how the SSH plugin can be used to remote install a Minikube in a Vagrant box running CentOS.

1) Installs latest version of Docker CE. 
2) Prepare the OS for Kubernetes by disabling swapping and installing socat.
3) Install kubectl.
4) Install Minikube (using the instructions from https://github.com/kubernetes/minikube/blob/master/docs/vmdriver-none.md)
5) Start Minikube (with --vm-driver=none).
6) Validate Minikube and kubectl installation.
7) Expose the dashboard via ingress (using the Minikube addons: dashboard and ingress).
8) Validate Minikube dashboard ingress (can takes some time until the addons have been enabled).

Usage of kubectl:
Either use as root or as user vagrant with sudo, i.e. "sudo kubectl".

Usage of minikube:
Use as root: /usr/local/bin/minikube 

Dashboard is available (via ingress) at: 
- http://127.0.0.1:80
- http://0.0.0.0:80
- http://192.168.34.10:80

The model is targeted to the resource named "ssh-node1" in the "linux-vagrant" environment.
The "ssh-node1" resource defines the Vagrant Box where Minikube is  installed.
The "linux-vagrant" environment defines an environment with three VM's.

Required modifications
-----------------------
1) Create a Vagrant Box using the Vagrantfile included in the module.

Intended operations
-----------------------
* "deploy-configuration"  - which will install minikube.

The model is targeted to the "deploy-configuration" operation. 
Validation steps supports the "deploy-configuration" and the "test" operation. 
All other operations is NOP.

Module content
-----------------------
* "models/linux-vagrant.xml" - model file for the "linux-vagrant" environment. 
* "vagrant/Vagrantfile" - Vagrant file for creation of the server VM.
* "bin/kubernetes.repo" - YUM repository definition for kubectl.
* "bin/dashboard-ingress.yaml" - Ingress deifintion for exposure of the dashboard at http://<hosts>:80.

Environments
-----------------------
The module contains models for the environments:
* "linux-vagrant" - Environment to support execution of modules in a Vagrant multi-machine Linux environment.
