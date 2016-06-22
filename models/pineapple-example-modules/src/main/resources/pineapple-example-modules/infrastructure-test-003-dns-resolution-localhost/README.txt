
Description
-------------
This example illustrates how to invoke a Infrastructure Test model
containing a single test case which tests forward and reverse DNS resolution.    

Required modifications
-----------------------
None.

Module content
-----------------------
* "models/linux-default.xml" - model file the "linux-default" environment. 
* "models/windows-default.xml" - model file the "windows-default" environment.
* "models/local.xml" - model file the "local" environment.

Intended operations
-----------------------
* "test"  - which will execute the tests.  

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "windows-default" - Environment to support execution of modules on a Windows based host with default values.
* "local" - Environment to support execution of modules on a local host.
