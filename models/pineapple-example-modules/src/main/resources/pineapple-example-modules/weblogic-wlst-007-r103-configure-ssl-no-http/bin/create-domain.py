
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
    cmo.setListenPortEnabled(false)	
    return cmo

#=======================================================================================
# Configure SSL
#=======================================================================================
def configureServerSSL(server,port):
    ssl = create(server.getName(), 'SSL')
    ssl.setEnabled(true)
    ssl.setListenPort(int(port))
    ssl.setHostnameVerifier(None)
    ssl.setHostnameVerificationIgnored(true)
    ssl.setClientCertificateEnforced(false)
    ssl.setTwoWaySSLEnabled(false)
    ssl.setSSLRejectionLoggingEnabled(true)

#=======================================================================================
# Main method
#=======================================================================================	
def main():

    try:
        # listing system arguments
        printPineappleArguments()

        # system argument contains pineappleModulePath
        pineappleModulePath = java.lang.System.getProperty('pineapple.module.path')
        templateFile = os.path.join( str(pineappleModulePath), 'bin', templateName)
        print ("Template file: " + templateFile + "\n")

        # read template
        readTemplate(templateFile)
        domain = create(domainName, 'Domain')

        # configure domain
        setSystemUser()
        admserver = configureAdmServer()
        configureServerSSL(admserver,adminSslListenPort)		

        # save domain
        domainDirectory = os.path.join( domainsDirectory, domainName)
        setOption('OverwriteDomain', 'true')
        writeDomain(domainDirectory)
        closeTemplate()   

    except:
        dumpStack()

main()
