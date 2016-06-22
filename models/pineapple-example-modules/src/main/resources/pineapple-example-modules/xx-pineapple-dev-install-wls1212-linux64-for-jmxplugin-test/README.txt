
Description
-------------
This example is used to installed WebLogic 12.1.2 on server nodes in the Pineapple test infrastructure
to support development and test of Pineapple.

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-pineapple-test-infrastructure.xml" - model file the "linux-pineapple-test-infrastructure" environment. 

Intended operations
-----------------------
The intended operation is:
* "deploy-configuration" - which will install YUM packages, the JVM, the Pineapple agent and WebLogic 12.1.2

Environments
-----------------------
The module contains models for the environments:
* "linux-pineapple-test-infrastructure" - Environment to support development and test of Pineapple .
