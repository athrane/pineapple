
Description
-------------
This example illustrates how the SSH plugin can be used to get the log file entries
from the Docker host service from journald in the Pineapple CI setup 

The model is targeted to the resources named "ssh-ci-node1" and  "ssh-ci-node2" in the "linux-pineapple-ci" environment.
The "ssh-ci-node1" resource defines the Vagrant Box where the CI server is installed.
The "ssh-ci-node2" resource defines the Vagrant Box used to host Docker.

Required modifications
-----------------------
Assumes the Pinapple CI setup is created and running.

Intended operations
-----------------------
* "create-report"  - which will fetch the logs from the targeted resources.

The model is targeted to the "create-report" operation. All other operations is NOP.

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-ci" - Environment which defines the Pineapple CI environment.
