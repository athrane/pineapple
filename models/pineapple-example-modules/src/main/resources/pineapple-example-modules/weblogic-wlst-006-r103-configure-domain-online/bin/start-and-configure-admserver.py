
import os
import java

#=======================================================================================
# print pineapple arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))

#=======================================================================================
# Get domain directory
#=======================================================================================
def getDomainDirectory():
    return os.path.join( domainsDirectory, domainName)	
	
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
# shutdown adm server outside node manager process
#=======================================================================================
def shutdownAdmServer():
    shutdown()

#=======================================================================================
# Configure node manager security
#=======================================================================================
def configureNodeManagerSecurity():

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
	
def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # start amdserver
        startAdmServer()
        
        # set online edit mode
        edit()
        startEdit()

        # configure node manager
        configureNodeManagerSecurity()
        
        #store WLST online change
        showChanges()
        save()
        activate(block='true')    
        
        # shutdown admserver
        shutdownAdmServer()

    except:
        dumpStack()

main()
