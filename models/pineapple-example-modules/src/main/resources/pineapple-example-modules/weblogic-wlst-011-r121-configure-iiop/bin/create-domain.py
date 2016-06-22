
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
         excTb = traceback.format_tb(trbk, maxTBlevel)
         return (excName, excArgs, excTb)
	
#=======================================================================================
# Get domain directory
#=======================================================================================
def getDomainDirectory():
    return os.path.join( domainsDirectory, domainName)

#=======================================================================================
# Set System User
#=======================================================================================
def setSystemUser():
    cd('/Security/%s/User/weblogic' % domainName)
    cmo.setName(systemUser)
    cmo.setPassword(systemPassword)

#=======================================================================================
# Configure adm server
#=======================================================================================
def configureAdmServer():
    # cd to the fixed server name 'AdminServer' from the template
    cd('/Servers/AdminServer') 
    cmo.setName(adminServerName)
    cmo.setListenAddress(adminListenAddress)
    cmo.setListenPort(int(adminListenPort))

def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # system argument contains pineappleModulePath
        pineappleModulePath = java.lang.System.getProperty('pineapple.module.path')
        templateFile = os.path.join( str(pineappleModulePath), 'bin', templateName)
        print ("Template file: " + templateFile + "\n")

        readTemplate(templateFile)
        domain = create(domainName, 'Domain')
        setSystemUser()
        configureAdmServer()
        setOption('OverwriteDomain', 'true')
        writeDomain(getDomainDirectory())
        closeTemplate()   

    except:
        dumpStack()
        print 'Unexpected error: ' + str(formatExceptionInfo())
        raise


main()
