
import os
import java

#=======================================================================================
# print pineappe arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))

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
	
def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        print("Starting to log on to NM")

        # connect to node manager
        nmConnect(nmUserName,nmPassword,adminListenAddress,nmPort,domainName,getDomainDirectory())

        print("Successfully logged on to NM, wil kill admserver..")

        # kill admserver in nm process
        nmKill(adminServerName)

        print("Successfully killed admserver..")		

    except:
        dumpStack()

main()
exit()
