
import os
import traceback
import java

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
# Print depending on verbose settings
#=======================================================================================
def printVerbose(str):
    if isVerbose:
        print(str)

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
# start and connect to adm server outside node manager process
#=======================================================================================
def startAdmServer():

    # print message
    printVerbose("Starting to start admserver...")

    url = 't3://' + adminListenAddress + ':' + adminListenPort    
    block = 'true'
    timeout = 60000

    startServer(serverName, domainName, url, systemUser, systemPassword, getDomainDirectory(), block, timeout)
    connect(systemUser, systemPassword, url)

    # print message
    printVerbose("Successfully started admserver.")

#=======================================================================================
# create  monitor user
#=======================================================================================
def createMonitorUser():
    createUser(userMonitor_name, userMonitor_password, userMonitor_description )
    addUserToGroup(userMonitor_name, userMonitor_group)

#=======================================================================================
# create user
#=======================================================================================
def createUser(userName, password, description):
   
    # navigate to domain root
    cd('/')   
    
    # create security configuration
    securityConfiguration = getMBean('/SecurityConfiguration/' + domainName)
    if securityConfiguration == None:
        securityConfiguration = create(domainName, 'SecurityConfiguration')

    # get authenticator
    defaultAuthenticator = securityConfiguration.getDefaultRealm().lookupAuthenticationProvider("DefaultAuthenticator")

    # remove user if it exsist
    if (defaultAuthenticator.userExists(userName) == 1):
        print('User: ' + userName + ' already exists. Will be removed.')       
        defaultAuthenticator.removeUser(userName)

    # create user    
    defaultAuthenticator.createUser(userName, password, description)
    print('Created user: ' + userName)	

#=======================================================================================
# add user to group 
#=======================================================================================
def addUserToGroup(userName, groupName):
   
    # navigate to domain root
    cd('/')   
    
    # create security configuration
    securityConfiguration = getMBean('/SecurityConfiguration/' + domainName)
    if securityConfiguration == None:
        securityConfiguration = create(domainName, 'SecurityConfiguration')

    # get authenticator
    defaultAuthenticator = securityConfiguration.getDefaultRealm().lookupAuthenticationProvider("DefaultAuthenticator")
    
    # add to group 
    defaultAuthenticator.addMemberToGroup(groupName, userName)


def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # start amdserver
        startAdmServer()

        # create user
        createMonitorUser()		

    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())

main()
exit()
