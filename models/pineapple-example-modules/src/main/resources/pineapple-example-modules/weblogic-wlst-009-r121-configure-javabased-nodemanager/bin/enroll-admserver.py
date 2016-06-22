
import os
import traceback
import java

#=======================================================================================
# print pineappe arguments 
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
         try:
             excTb = traceback.format_tb(trbk, maxTBlevel)
         except:
             excTb = "<no stack trace>"
         return (excName, excArgs, excTb)

#=======================================================================================
# Get WebLogic home
#=======================================================================================	
def getWeblogicHome():
   return wlsHome
   
#=======================================================================================
# Get domain directory
#=======================================================================================
def getDomainDirectory():
    return domainsDirectory + '/' + domainName
	
#=======================================================================================
# Get node manager home
#=======================================================================================
def getNmHomeDirectory():
    return getWeblogicHome() + '/common/nodemanager'

#=======================================================================================
# start and connect to adm server outside node manager process
#=======================================================================================
def startAdmServer():
    url = 't3s://' + adminListenAddress + ':' + adminSslListenPort
    block = 'true'
    startServer(serverName, domainName, url, systemUser, systemPassword, getDomainDirectory(), block, int(wlstServerStartTimeout))
    connect(systemUser, systemPassword, url)


def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # BUG, if domainName is used then is NONE by the time nmConnect is invoked??
        name=domainName
        domainDirectory=getDomainDirectory() 

        # start amdserver
        startAdmServer()

        # start node manager
        #startNodeManager()

        # enroll server
        nmEnroll(domainDirectory, getNmHomeDirectory())

        # shutdown adm server outside node manager process
        shutdown()

        # print message
        print("Starting to log on to NM...")

        # connect to node manager
        nmConnect(nmUserName,nmPassword,adminListenAddress,nmPort,name,domainDirectory)

        # print message
        print("Successfully logged on to NM, will start admserver..")

        # start amdserver in nm process
        nmStart(adminServerName, domainDirectory)

        # print message
        print("Successfully started admserver..")

    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())

main()
exit()
