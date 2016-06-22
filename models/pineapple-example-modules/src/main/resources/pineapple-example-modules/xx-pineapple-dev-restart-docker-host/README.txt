
Description
-------------
This example illustrates how the SSH plugin can be restart Docker hosts in the Pineapple CI setup.

The model is targeted to the resources named "ci-ssh-node1" and "ci-ssh-node2" in the
"linux-pineapple-ci" environment.

-----------------------
Assumes the Pinapple CI setup is created and running.

Intended operations
-----------------------
* "deploy-configuration"  - which will restart the Docker hosts.

The model is targeted to the "deploy-configuration" operation. All other operations is NOP.

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-ci" - Environment which defines the Pineapple CI environment.

