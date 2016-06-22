
Description
-------------
This example illustrates how to execute two composite modules in sequence:
1) infrastructure-test-001-forward-dns-resolution-localhost
2) infrastructure-test-004-host-listens-on-ports

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
Since the two modules are test modules their intended operation should be used:
* "test"  - which will execute the test defined in the two modules.

Environments
-----------------------
The module contains models for the environments:
* "linux-default" - Environment to support execution of modules on a Linux based host with default values.
* "windows-default" - Environment to support execution of modules on a Windows based host with default values.
* "local" - Environment to support execution of modules on a local host.
