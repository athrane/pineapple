
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
# start and connect to adm server outside node manager process
#=======================================================================================
def startAdmServer():
    url = 't3://' + adminListenAddress + ':' + adminListenPort    
    block = 'true'
    startServer(serverName, domainName, url, systemUser, systemPassword, getDomainDirectory(), block, int(wlstServerStartTimeout))
    connect(systemUser, systemPassword, url)

#=======================================================================================
# shutdown adm server outside node manager process
#=======================================================================================
def shutdownAdmServer():
    shutdown()

#=======================================================================================
# Create identity key store file name
#=======================================================================================
def createIdentityKeyStoreFileName(alias):
    return alias + '_identity.jks'

#=======================================================================================
# Create absolution path to identity key store in domain
#=======================================================================================
def createIdentityKeyStorePathInDomain(alias):
    destinationDir = domainsDirectory +'/'+domainName+'/servers/'+alias+'/security/'
    path = destinationDir + createIdentityKeyStoreFileName(alias)
    print('Identity keystore path: ' + path)
    return path

#=======================================================================================
# Create trust key store file name
#=======================================================================================
def createTrustKeyStoreFileName(alias):
    return alias + '_trust.jks'

#=======================================================================================
# Create absolution path to trust key store in domain
#=======================================================================================
def createTrustKeyStorePathInDomain(alias):
    destinationDir = domainsDirectory +'/'+domainName+'/servers/'+alias+'/security/'
    path = destinationDir + createTrustKeyStoreFileName(alias)
    print('Trust keystore path: ' + path)
    return path

#=======================================================================================
# Configure server keystores 
#=======================================================================================
def configureServerKeyStores(server):   
    server.setKeyStores('CustomIdentityAndCustomTrust')

    # define custom identity key store
    server.setCustomIdentityKeyStoreFileName(createIdentityKeyStorePathInDomain(server.getName()))
    server.setCustomIdentityKeyStoreType('jks')
    server.setCustomIdentityKeyStorePassPhrase(storePassword)

    # define custom trust key store   
    server.setCustomTrustKeyStoreFileName(createTrustKeyStorePathInDomain(server.getName()))
    server.setCustomTrustKeyStoreType('jks')
    server.setCustomTrustKeyStorePassPhrase(storePassword)

#=======================================================================================
# Configure SSL
#=======================================================================================
def configureServerSSL(server,port):
    cd('/Servers/%s/SSL/%s' % (server.getName(),server.getName()))
    cmo.setEnabled(true)
    cmo.setListenPort(int(port))
    cmo.setHostnameVerifier(None)
    cmo.setHostnameVerificationIgnored(true)
    cmo.setClientCertificateEnforced(false)
    cmo.setTwoWaySSLEnabled(false)
    cmo.setSSLRejectionLoggingEnabled(true)

    # define key stores for identity and trust
    cmo.setServerPrivateKeyAlias(server.getName())
    cmo.setServerPrivateKeyPassPhrase(keyPassword)
    cmo.setIdentityAndTrustLocations("keyStores")

#=======================================================================================
# Main method
#=======================================================================================
def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # start amdserver
        startAdmServer()

        # set online edit mode
        edit()
        startEdit()

        # configure SSL
        cd('/Servers/%s' % adminServerName )
        admserver = cmo
        configureServerKeyStores(admserver)
        configureServerSSL(admserver,adminSslListenPort)

        #store wlst online change
        showChanges()
        save()
        activate(block='true')	

        # shutdown admserver
        shutdownAdmServer()

    except:
        dumpStack()

main()
