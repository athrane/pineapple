
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
    url = 'http://' + adminListenAddress + ':' + adminListenPort    
    block = 'true'
    timeout = 60000
	
    startServer(serverName, domainName, url, systemUser, systemPassword, getDomainDirectory(), block, timeout)
    connect(systemUser, systemPassword, url)

#=======================================================================================
# shutdown adm server outside node manager process
#=======================================================================================
def shutdownAdmServer():
    shutdown()

#=======================================================================================
# Configure node manager security
#=======================================================================================
def configureNodeManagerSecurity():

    # set online edit mode
    edit()
    startEdit()

    # navigate to domain root
    cd('/')

    # create security configuration
    securityConfiguration = getMBean('/SecurityConfiguration/' + domainName)
    if securityConfiguration == None:
        securityConfiguration = create(domainName, 'SecurityConfiguration')

    # navigate to security configuration mbean
    cd('/SecurityConfiguration/' + domainName)

    # set nm credentials
    cmo.setNodeManagerUsername(nmUserName)
    cmo.setNodeManagerPassword(nmPassword)

    #store wlst online change
    showChanges()
    save()
    activate(block='true')	
	
def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # start amdserver
        startAdmServer()

        # configure node manager
        configureNodeManagerSecurity()

        # shutdown admserver
        shutdownAdmServer()

    except:
        dumpStack()

main()
