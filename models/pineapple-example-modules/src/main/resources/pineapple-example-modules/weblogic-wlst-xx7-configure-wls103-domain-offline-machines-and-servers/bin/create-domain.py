
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
        printPineappleArguments()

        # system argument contains pineapple Module Path
        pineappleModulePath = java.lang.System.getProperty('pineapple.module.path')
        templateFile = str(pineappleModulePath) + '/bin/' + templateName
        print ("TemplateFile: " + templateFile + "\n")

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
