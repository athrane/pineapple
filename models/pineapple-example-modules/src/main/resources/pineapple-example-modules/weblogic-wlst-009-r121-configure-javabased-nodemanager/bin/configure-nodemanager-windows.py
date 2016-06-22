
import os
import java
import copy
import datetime

#=======================================================================================
# print pineapple arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))    

#=======================================================================================
# Create Node Manager Windows service name
#=======================================================================================
def createNodeManagerServiceName():
    name= 'Oracle WebLogic '
    name = name + 'NodeManager'
    name = name + '('
    name = name.replace('\\', '_')
    name = name.replace('/', '_')
    name = name + ')'
    return name
    
#=======================================================================================
# Create Node Manager home directory
#=======================================================================================
def createNodeManagerHomeDirectory():
    nmHomePath = os.path.normpath(nmHome)
    
    # create destination dir
    if not os.path.exists(nmHomePath):
        os.makedirs(nmHomePath)
        print ("Created NodeManager home directory: " + str(nmHomePath))                

#=======================================================================================
# Create Node Manager log directory
#=======================================================================================
def createNodeManagerLogDirectory():
    nmLogDir = os.path.dirname(nmLogFile)

    # create destination dir
    if not os.path.exists(nmLogDir):
        os.makedirs(nmLogDir)
        print ("Created NodeManager log directory: " + str(nmLogDir))        
        
#=======================================================================================
# Install node manager service script
#=======================================================================================
def installNodeManagerService():
    # install node manager script as service
    command = '/sbin/chkconfig --add nodemanager'
    os.system(command)

    # start node manager service 
    command = 'net start '
    command = command + nmServiceName
    os.system(command)
    

#=======================================================================================
#  Create NodeManager properties file
#=======================================================================================
def createNodeManagerPropertiesFile():
    nmHomePath = os.path.normpath(nmHome)
    nmPropertiesPath = os.path.join(nmHomePath, "nodemanager.properties")
    nmDomainsPath = os.path.join(nmHomePath, "nodemanager.domains")
    nmLogPath = os.path.normpath(nmLogFile)
            
    # if properties exists at destination then delete it
    if os.path.exists(nmPropertiesPath):
        os.remove(nmPropertiesPath)
        
    try:
        # open file
        f = open(nmPropertiesPath, "w")

        try:                        
            f.write("#Created by Pineapple at: " + str(datetime.datetime.now())+"\n\n")            
            f.write('PropertiesVersion=10.3' +"\n") 
            f.write('DomainsFile=' +str(nmDomainsPath) +"\n")
            f.write('LogLimit=0' +"\n") 
            f.write('PropertiesVersion=10.3' +"\n") 
            f.write('DomainsDirRemoteSharingEnabled=false' +"\n") 
            f.write('javaHome=/u01/app/oracle/product/fmw/jrockit-jdk1.6.0_31-R28.2.3-4.1.0' +"\n") 
            f.write('AuthenticationEnabled=true' +"\n") 
            f.write('NodeManagerHome=' +str(nmHomePath) +"\n")
            f.write('LogLevel=INFO' +"\n") 
            f.write('DomainsFileEnabled=true' +"\n") 
            f.write('StartScriptName=startWebLogic.sh' +"\n") 
            f.write('ListenAddress=' +"\n") 
            f.write('NativeVersionEnabled=true' +"\n") 
            f.write('ListenPort='+nmListenPort +"\n") 
            f.write('LogToStderr=true' +"\n") 
            f.write('SecureListener=true' +"\n") 
            f.write('LogCount=1' +"\n") 
            f.write('DomainRegistrationEnabled=false' +"\n") 
            f.write('StopScriptEnabled=false' +"\n") 
            f.write('QuitEnabled=false' +"\n") 
            f.write('LogAppend=true' +"\n") 
            f.write('StateCheckInterval=500' +"\n") 
            f.write('CrashRecoveryEnabled=true' +"\n") 
            f.write('StartScriptEnabled=true' +"\n") 
            f.write('LogFile='+str(nmLogPath) +"\n")
            f.write('ListenBacklog='+nmListenBacklog +"\n") 

            print ("Wrote NodeManager properties to: " + str(nmPropertiesPath))

        finally:
            f.close()

    except IOError:
        pass

def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # create node manager directories
        createNodeManagerHomeDirectory()
        createNodeManagerLogDirectory()

        # create node manager properties
        createNodeManagerPropertiesFile()

        # install node manager service
        #copyNodeManagerServiceScript()
        #installNodeManagerService()

    except:
        dumpStack()

main()
