
import os
import traceback
import datetime
import java
import copy

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
         excTb = traceback.format_tb(trbk, maxTBlevel)
         return (excName, excArgs, excTb)

#=======================================================================================
# Copy file
#=======================================================================================
def copyFile(source, dest, buffer_size=1024*1024):
    """
    Copy a file from source to dest. source and dest
    can either be strings or any object with a read or
    write method, like StringIO for example.
    """
    if not hasattr(source, 'read'):
        source = open(source, 'rb')
    if not hasattr(dest, 'write'):
        dest = open(dest, 'wb')

    while 1:
        copy_buffer = source.read(buffer_size)
        if copy_buffer:
            dest.write(copy_buffer)
        else:
            break

    source.close()
    dest.close()

#=======================================================================================
# Create Node Manager home directory
#=======================================================================================
def createNodeManagerHome():

    # create destination dir
    if not os.path.exists(nmHome):
        os.makedirs(nmHome)
	
#=======================================================================================
# Copy node manager service script
#=======================================================================================
def copyNodeManagerServiceScript():
    modulepath = str(java.lang.System.getProperty('pineapple.module.path'))    
    sourceFile = os.path.join( modulepath, 'bin', nmServiceScript)
    print('Nodemanager service script source path: ' + sourceFile)

    destinationFile = '/etc/init.d/nodemanager'
    print('Copies Nodemanager service script to: ' + destinationFile)

    # if file exists at destination then delete it
    if os.path.exists(destinationFile):
        os.remove(destinationFile)

    # copy file
    copyFile(sourceFile, destinationFile)
	
    # set file permissions
    command = 'chmod a+x '+ destinationFile
    os.system(command)


#=======================================================================================
# Install node manager service script
#=======================================================================================
def installNodeManagerService():
    # install node manager script as service
    command = '/sbin/chkconfig --add nodemanager'
    os.system(command)

    # start node manager service 
    command = '/sbin/service nodemanager start'
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
            f.write("Created by Pineapple at: " + str(datetime.datetime.now())+"\n\n")            
            f.write('PropertiesVersion=12.1' +"\n") 
            f.write('DomainsFile=' +str(nmDomainsPath) +"\n")
            f.write('LogLimit=0' +"\n")  
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
            f.write('SecureListener=false' +"\n") 
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
        print 'Unexpected error: ' + str(formatExceptionInfo())

#=======================================================================================
#  Create NodeManager domains file
#=======================================================================================
def createNodeManagerDomainsFile():
    nmHomePath = os.path.normpath(nmHome)
    nmPropertiesPath = os.path.join(nmHomePath, "nodemanager.properties")
    nmDomainsPath = os.path.join(nmHomePath, "nodemanager.domains")
                    
    try:
        # open file
        f = open(nmPropertiesPath, "w")

        try:                        
            f.write("# Created by Pineapple at: " + str(datetime.datetime.now())+"\n\n")            
            print ("Wrote NodeManager domains files to: " + str())

        finally:
            f.close()

    except IOError:
        print 'Unexpected error: ' + str(formatExceptionInfo())


def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # create node manager home directory
        createNodeManagerHome()

        # create node manager properties
        createNodeManagerPropertiesFile()

        # create node manager domains file
        createNodeManagerDomainsFile()

        # install node manager service
        copyNodeManagerServiceScript()
        installNodeManagerService()

    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())

main()
