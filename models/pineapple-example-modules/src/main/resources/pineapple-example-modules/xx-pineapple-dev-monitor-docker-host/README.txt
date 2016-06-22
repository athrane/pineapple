

Description
-------------
This example illustrates how to the Pineapple CI setup can be monitored using Pineapple.

The model is targeted to the resources named "infrastructure-test" in the "*" environment.

If the test fails then a trigger is invoked to restart the Docker hosts using the module named xx-pineapple-dev-restart-docker-host
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

