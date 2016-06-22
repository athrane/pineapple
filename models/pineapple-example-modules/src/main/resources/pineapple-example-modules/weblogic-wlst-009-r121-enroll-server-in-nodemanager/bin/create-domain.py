
import os
import traceback
import java

#=======================================================================================
# print pineapple arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))    

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
# Set System User
#=======================================================================================
def setSystemUser():
    cd('/Security/%s/User/weblogic' % domainName)
    cmo.setName(systemUser)
    cmo.setPassword(systemPassword)

#=======================================================================================
# Configure adm server
#=======================================================================================
def configureAdmServer():
    # cd to the fixed server name 'AdminServer' from the template
    cd('/Servers/AdminServer') 
    cmo.setName(adminServerName)
    cmo.setListenAddress(adminListenAddress)
    cmo.setListenPort(int(adminListenPort))

#=======================================================================================
# Get node manager home
#=======================================================================================
def getNmHomeDirectory():
    return nmHome

#=======================================================================================
# create managed server
#=======================================================================================
def createManagedServer(serverName, listenAddress, listenPort):

    # navigate to domain root
    cd('/')

    # create server
    server = create(serverName, 'Server')

    # add properties
    server.setListenAddress(listenAddress)
    server.setListenPort(int(listenPort))
    print('Created managed server: ' + server.getName() + ' at ' + server.getListenAddress() + ':' + str(server.getListenPort()))

#=======================================================================================
# Create Machine
# - creates also node manager for machine
#=======================================================================================
def createMachine(machineName, machineListenAddress):

   # navigate to domain root
   cd('/')
   
   #create machine
   machine = create(machineName, 'Machine')
   print('Successfully created machine: ' + machineName)
   
   # navigate to machine 
   cd('/Machine/' + machineName)

   # create node manager
   nodeMgr = create(machineName, 'NodeManager')
   nodeMgr.setListenAddress(machineListenAddress)
   nodeMgr.setListenPort(int(nmListenPort))
   nodeMgr.setDebugEnabled(True)
   nodeMgr.setNodeManagerHome(getNmHomeDirectory())
   nodeMgr.setNMType(nmType)
   print('Successfully created NodeManager configuration for machine: ' + machineName)

   return machine
    
#=======================================================================================
# Map server to machine
#=======================================================================================
def setMachine(machineName, serverName):

   # navigate to domain root
   cd('/')

   # navigate to server
   cd('/Servers/')
   cd(serverName)
   currentServer = cmo;

   # navigate to machine   
   cd('/Machine/' + machineName)
   currentServer.setMachine(cmo)    
   print('Successfully mapped server [' + serverName + '] to machine [' + machineName + ']')


def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # system argument contains pineappleModulePath
        pineappleModulePath = java.lang.System.getProperty('pineapple.module.path')
        templateFile = os.path.join( str(pineappleModulePath), 'bin', templateName)
        print ("Template file: " + templateFile + "\n")

        readTemplate(templateFile)
        domain = create(domainName, 'Domain')
        setSystemUser()
        configureAdmServer()
        
        # create and configure 
        createManagedServer('server1',managedServer1_listenAddress,managedServer1_listenPort)
        createMachine('machine1',adminListenAddress)
        setMachine('machine1', adminServerName)
        setMachine('machine1', 'server1')
        
        # write domain
        domainDirectory = os.path.join( domainsDirectory, domainName)
        setOption('OverwriteDomain', 'true')                
        writeDomain(domainDirectory)
        closeTemplate()   

    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())
        raise

main()
