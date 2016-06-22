
Description
-------------
This example illustrates how the Docker plugin supports creation of report describing the images and containers.

The model is targeted to the all resources named "docker-ci-node*" in the "linux-pineapple-ci" environment.

Required modifications
-----------------------
Assumes the Pinapple CI setup is created and running.

Intended operations
-----------------------

* "create-report" - which will list the created Docker images and containers.

The model is targeted to the "create-report" operations. All other operations are NOP.

Module content
-----------------------
* "models/linux-pineapple-ci.xml" - model file for the "linux-pineapple-ci" environment. 

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-ci" - Environment which defines the Pineapple CI environment.