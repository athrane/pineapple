
import sys
import traceback

import os
import java
import java.lang.String as String

#=======================================================================================
# print pineappe arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))

#=======================================================================================
# Is script verbose
#=======================================================================================
def isVerbose():
    if isScriptVerbose == '':
        return false
    return (createLowerCaseName(isVerbose) == 'true')
	
#=======================================================================================
# Print exception info
#=======================================================================================
def formatExceptionInfo(maxTBlevel=5):
         cla, exc, trbk = sys.exc_info()
         excName = cla.__name__
         try:
             excArgs = exc.__dict__["args"]
         except KeyError:
             excArgs = "<no args>"
         excTb = traceback.format_tb(trbk, maxTBlevel)
         return (excName, excArgs, excTb)
	
#=======================================================================================
# Print depending on verbose settings
#=======================================================================================
def printVerbose(str):
    if isVerbose:
        print(str)

#=======================================================================================
# Get domain directory
#=======================================================================================
def getDomainDirectory():
    return domainsDirectory + '/' + domainName	

#=======================================================================================
# Get node manager home
#=======================================================================================
def getNmHomeDirectory():
    return wlsHome + '/common/nodemanager'
	
#=======================================================================================
# Return which type of machine should be created
#=======================================================================================	
def getMachineCreateType():
    # On Unix, machine type is 'UnixMachine' on Windows it is 'Machine'
    if os.pathsep == ':':
        return 'UnixMachine'
    else:
        return 'Machine'
		
#=======================================================================================
# create managed server
#=======================================================================================
def createManagedServer(serverName):

    # navigate to domain root
    cd('/')

    # create server
    server = create(serverName, 'Server')
    print('Created managed server: ' + server.getName())

#=======================================================================================
# Create Machine
# - creates also node manager for machine
#=======================================================================================
def createMachine(machineName, machineListenAddress):

   # print message
   printVerbose('Starting to create machine: ' + machineName)

   # navigate to domain root
   cd('/')
   
   #create machine
   machine = create(machineName, getMachineCreateType())
   
   # print message   
   printVerbose('Successfully created machine: ' + machineName)
   printVerbose('Starting to create NodeManager configuration for machine: ' + machineName)
   
   # navigate to machine 
   cd('/Machine/' + machineName)

   # create node manager
   nodeMgr = create(machineName, 'NodeManager')
   nodeMgr.setListenAddress(machineListenAddress)
   nodeMgr.setListenPort(int(nmPort))
   nodeMgr.setDebugEnabled(True)
   nodeMgr.setNodeManagerHome(getNmHomeDirectory())

   # print message      
   printVerbose('Successfully created NodeManager configuration for machine: ' + machineName)

   return machine
	
#=======================================================================================
# Map server to machine
#=======================================================================================
def setMachine(machineName, serverName):

   # print message      
   printVerbose('Starting to map server [' + serverName + '] to machine [' + machineName + ']')
   
   # navigate to domain root
   cd('/')

   # navigate to server
   cd('/Servers/')
   cd(serverName)
   currentServer = cmo;

   # navigate to machine   
   cd('/Machine/' + machineName)
   currentServer.setMachine(cmo)	

   # print message      
   printVerbose('Successfully mapped server [' + serverName + '] to machine [' + machineName + ']')

   
def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # configure domain
        domainDirectory = domainsDirectory + '/' + domainName
        readDomain(domainDirectory)
        createManagedServer('server1')
        createManagedServer('server2')
		
        # create machine
        createMachine('machine1',adminListenAddress)

        # bind to machine
        setMachine('machine1', adminServerName)
        setMachine('machine1', 'server1')
        setMachine('machine1', 'server2')

        updateDomain()

    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())

main()
