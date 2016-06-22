
Description
-------------
This example illustrates how to the Pineapple CI setup can be monitored
using Pineapple.

The model is targeted to the resources named "infrastructure-ci-node1" and  "infrastructure-ci-node2" in the "linux-pineapple-ci" environment.
The "infrastructure-ci-node1" resource defines the Vagrant Box where the CI server is installed.
The "infrastructure-ci-node2" resource defines the Vagrant Box used to host Docker.

Required modifications
-----------------------
Assumes the Pinapple CI setup is created and running.

Intended operations
-----------------------
* "test"  - which will run the tests at the targeted resources.

The model is targeted to the "test" operation. All other operations is NOP.

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-ci" - Environment which defines the Pineapple CI environment.
