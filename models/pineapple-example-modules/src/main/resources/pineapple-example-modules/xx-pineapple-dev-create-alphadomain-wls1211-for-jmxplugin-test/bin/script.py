
import os
import java

#=======================================================================================
# print pineappe arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))    

#=======================================================================================
# print system arguments 
#=======================================================================================		
def printSystemProperties():
    properties = java.lang.System.getProperties()
    keys = properties.keySet()
    for key in keys:
        value = java.lang.System.getProperty(key)
        print(str(key) + '=' + str(value))

#=======================================================================================
# Set System User
#=======================================================================================
def setSystemUser():
    # navigate to domain root
    cd('/')
    sysUser = cd('/Security/%s/User/weblogic' % domainName)
    sysUser.setName(systemUser)
    sysUser.setPassword(systemPassword)

#=======================================================================================
# Configure adm server
#=======================================================================================
def configureAdmServer():
    # navigate to domain root
    cd('/')
    cd('Servers/AdminServer')
    set('ListenAddress',adminListenAddress)
    set('ListenPort', adminListenPort)
		
def main():   

    try:
        # listing system arguments
        printSystemProperties()
        printPineappleArguments()

        # system argument contains pineappleModulePath
        pineappleModulePath = java.lang.System.getProperty('pineapple.module.path')
        templateFile = str(pineappleModulePath) + '/bin/' + templateName
        print ("Template file: " + templateFile + "\n")

        readTemplate(templateFile)
        domain = create(domainName, 'Domain')
        setSystemUser()    
        domainDirectory = domainsDirectory + '/' + domainName
        setOption('OverwriteDomain', 'true')
        writeDomain(domainDirectory)
        closeTemplate()   

    except:
        dumpStack()

main()
