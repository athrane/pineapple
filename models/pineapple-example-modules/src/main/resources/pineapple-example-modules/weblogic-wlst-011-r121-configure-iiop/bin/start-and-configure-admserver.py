
import os
import java

#=======================================================================================
# print pineappe arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))

#=======================================================================================
# Get domain directory
#=======================================================================================
def getDomainDirectory():
    return domainsDirectory + '/' + domainName	
	
#=======================================================================================
# start and connect to adm server outside node manager process
#=======================================================================================
def startAdmServer():
    url = 't3://' + adminListenAddress + ':' + adminListenPort
    block = 'true'
    timeout = 60000

    startServer(serverName, domainName, url, systemUser, systemPassword, getDomainDirectory(), block, timeout)
    connect(systemUser, systemPassword, url)

#=======================================================================================
# Configure IIOP
#=======================================================================================
def configureIiop():
    cd('/Servers/'+adminServerName)
    cmo.setIIOPEnabled(true)
    cmo.setDefaultIIOPUser(iiopUser)
    cmo.setDefaultIIOPPassword(iiopPassword)

#=======================================================================================
# shutdown adm server outside node manager process
#=======================================================================================
def shutdownAdmServer():
    shutdown()

def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # start amdserver
        startAdmServer()

        # set online edit mode
        edit()
        startEdit()

        # configure IIOP
        configureIiop()

        #store wlst online change
        showChanges()
        save()
        activate(block='true')

        # shutdown admserver
        shutdownAdmServer()

    except:
        dumpStack()

main()
