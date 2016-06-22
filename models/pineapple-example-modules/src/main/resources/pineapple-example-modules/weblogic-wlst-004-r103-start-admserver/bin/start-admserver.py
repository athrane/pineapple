
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
    timeout = 90000
	
    startServer(serverName, domainName, url, systemUser, systemPassword, getDomainDirectory(), block, timeout)
    connect(systemUser, systemPassword, url)
    shutdown()

def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # start amdserver
        startAdmServer()

    except:
        dumpStack()

main()

