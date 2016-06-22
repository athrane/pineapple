
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
         try:
             excTb = traceback.format_tb(trbk, maxTBlevel)
         except:
             excTb = "<no stack trace>"
         return (excName, excArgs, excTb)
   
#=======================================================================================
# Get domain directory
#=======================================================================================
def getDomainDirectory():
    return os.path.join( domainsDirectory, domainName)    
	
#=======================================================================================
# Get node manager home
#=======================================================================================
def getNmHomeDirectory():
    return nmHome

#=======================================================================================
# start and connect to adm server outside node manager process
#=======================================================================================
def startAdmServer():
    url = 't3://' + adminListenAddress + ':' + adminListenPort    
    block = 'true'
    timeout = 60000
    
    startServer(serverName, domainName, url, systemUser, systemPassword, getDomainDirectory(), block, timeout)
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
        print("Successfully started admserver")

        # start node manager
        #startNodeManager()

        # enroll server
        nmEnroll(domainDirectory, getNmHomeDirectory())
        print("Successfully enrolled admserver under NodeManager control")

        # shutdown adm server outside node manager process
        shutdown()
        print("Successfully shutdown admserver")

        # connect to node manager       
        nmConnect(nmUserName,nmPassword,adminListenAddress,nmListenPort,domainName,domainDirectory,nmType)
        print("Successfully logged on to NodeManager")

        # start admserver in nm process
        nmStart(adminServerName, domainDirectory)
        print("Successfully started admserver ["+ adminServerName + "] under NodeManager control..")

    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())
        raise

main()
exit()
