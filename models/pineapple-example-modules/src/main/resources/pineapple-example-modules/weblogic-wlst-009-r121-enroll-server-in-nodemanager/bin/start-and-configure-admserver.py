
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
        shutdown()
        
    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())
        raise

main()
