
Description
-------------
This example illustrates how the Git plugin can be used to clone a public repository from GitHub.
The cloned repository is an empty Pineapple module.

The model is targeted to the resource named "docker-node" in the "linux-vagrant" environment.
The "git-pineapple-example-repo" resource defines the Git repository where the repositoyr is cloned from.
The "local" environment defines the local environment in Pineapple.

Intended operations
-----------------------
* "deploy-configuration"  - which will clone the repository into the Pineapple modules directory.
The model is targeted to the "deploy-configuration". All other operations are NOP.

Module content
-----------------------
* "models/local.xml" - model file for the "local" environment. 

Environments
-----------------------
The module contains models for the environments:
* "local" - Environment which defines the local Pineapple environment.
