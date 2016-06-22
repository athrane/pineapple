
import os
import java

#=======================================================================================
# print pineappe arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))    

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

#=======================================================================================
# Configure domain administration port
#=======================================================================================
def configureDomainAdministrationPort():
    # navigate to domain root
    cd('/')
    print(cmo)
    cmo.setAdministrationPortEnabled(true)
    cmo.setAdministrationPort(adminListenPort)
	
	
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
        configureDomainAdministrationPort()		
        domainDirectory = os.path.join( domainsDirectory, domainName)
        setOption('OverwriteDomain', 'true')
        writeDomain(domainDirectory)
        closeTemplate()   

    except:
        dumpStack()

main()
