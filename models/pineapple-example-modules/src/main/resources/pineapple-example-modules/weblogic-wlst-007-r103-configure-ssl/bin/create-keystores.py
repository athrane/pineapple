
import os
import java
import copy


#=======================================================================================
# print pineappe arguments 
#=======================================================================================
def printPineappleArguments():
    print ("Pineapple module path: " + str(java.lang.System.getProperty('pineapple.module.path')))    

#=======================================================================================
# Create absolution path to keytool path from java.home
#=======================================================================================
def createKeyToolPath():

    javaHome =str(java.lang.System.getProperty('java.home'))

    # create path
    keyToolHome = os.path.join(javaHome, 'bin' , 'keytool') 
    print('Keytool home: ' + keyToolHome)

    return keyToolHome

#=======================================================================================
# Create identity key store file name
#=======================================================================================
def createIdentityKeyStoreFileName(alias):
    return alias + '_identity.jks'

#=======================================================================================
# Create absolution path to identity key store
#=======================================================================================
def createIdentityKeyStorePath(alias):
    tempDir = str(java.lang.System.getProperty('java.io.tmpdir'))
    path = os.path.join(tempDir, createIdentityKeyStoreFileName(alias))
    print('Identity keystore path: ' + path)
    return path

#=======================================================================================
# Create trust key store file name
#=======================================================================================
def createTrustKeyStoreFileName(alias):
    return alias + '_trust.jks'

#=======================================================================================
# Create absolution path to trust key store
#=======================================================================================
def createTrustKeyStorePath(alias):
    tempDir = str(java.lang.System.getProperty('java.io.tmpdir'))
    path = os.path.join(tempDir, createTrustKeyStoreFileName(alias))
    print('Trust keystore path: ' + path)
    return path

#=======================================================================================
# Create digital certificate file name
#=======================================================================================	
def createCertificateFileName(alias):
    return alias + '_cert.cer'

#=======================================================================================
# Create absolution path to digital certificate file
#=======================================================================================	
def createCertificatePath(alias):
    tempDir = str(java.lang.System.getProperty('java.io.tmpdir'))
    path = os.path.join(tempDir, createCertificateFileName(alias))
    print('Certificate path: ' + path)
    return path

#=======================================================================================
# Create key store path in domain
#=======================================================================================
def createKeyStorePathInDomain(keyStoreName):
    destinationDir = os.path.join(domainsDirectory, domainName, 'servers', adminServerName, 'security')
    path= os.path.join(destinationDir, keyStoreName )
    print('Keystore destination directory: ' + path)	
    return path	

#=======================================================================================
# Create identity key store which contains:
# - private key
# - public key
# - digital certificate (which contains the public key)
#=======================================================================================
def createIdentityKeyStore(alias, entity):
    
    # create command line
    command = createKeyToolPath() +' -genkey'
    command = command + ' -alias ' + alias
    command = command + ' -keyalg RSA -keysize 2048 -validity 3650'
    command = command + ' -keypass ' + keyPassword
    command = command + ' -keystore "' + createIdentityKeyStorePath(alias) + '"'
    command = command + ' -storepass ' + storePassword
    command = command + ' -dname "' + entity + '"'

    # invoke to create keystore
    os.system(command)

#=======================================================================================
# Selfsign certificate in identity keystore
#=======================================================================================
def selfsignCertificate(alias, entity):
    
    # create command line
    command = createKeyToolPath() +' -selfcert'
    command = command + ' -alias ' + alias
    command = command + ' -dname "' + entity + '"'
    command = command + ' -keypass ' + keyPassword
    command = command + ' -keystore "' + createIdentityKeyStorePath(alias) + '"'
    command = command + ' -storepass ' + storePassword

    # invoke to sign
    os.system(command)

#=======================================================================================
# Export certificate from identity keystore 
#=======================================================================================
def exportCertificate(alias):
    
    # create command line
    command = createKeyToolPath() +' -export'
    command = command + ' -alias ' + alias
    command = command + ' -file ' + createCertificatePath(alias)
    command = command + ' -keystore "' + createIdentityKeyStorePath(alias) + '"'
    command = command + ' -storepass ' + storePassword

    # invoke to export
    os.system(command)

#=======================================================================================
# Create trust key store and import digital certificate into the store.
#=======================================================================================
def createTrustKeyStore(alias):
    
    # create command line
    command = createKeyToolPath() +' -import'
    command = command + ' -alias ' + alias
    command = command + ' -file ' + createCertificatePath(alias)
    command = command + ' -keystore "' + createTrustKeyStorePath(alias) + '"'
    command = command + ' -storepass ' + storePassword
    command = command + ' -noprompt'

    # invoke to create keystore
    os.system(command)

#=======================================================================================
# Copy keystore to server directory in domain
#=======================================================================================
def copyKeyStoreToDomain(name, sourceFile, keyStoreName):
    print('Keystore source path: ' + sourceFile)
    destinationDir = os.path.join(domainsDirectory, domainName, 'servers', name, 'security')
    path = os.path.join(destinationDir, keyStoreName)
    print('Copies keystore to: ' + path)

    # create destination dir
    if not os.path.exists(destinationDir):
        os.makedirs(destinationDir)

    # if keystore exists at destination then delete it
    if os.path.exists(path):
        os.remove(path)

    # move key store
    os.rename(sourceFile, path)


def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # create identity key store
        createIdentityKeyStore(adminServerName, admServerOrgEntity)

        # selfsign certificate in keystore
        selfsignCertificate(adminServerName, admServerOrgEntity)

        # export certificate from keystore 
        exportCertificate(adminServerName)

        # copy identity store to domain
        copyKeyStoreToDomain(adminServerName, createIdentityKeyStorePath(adminServerName), createIdentityKeyStoreFileName(adminServerName))

        # create trust key store and import certificate
        createTrustKeyStore(adminServerName)

        # copy identity store to domain
        copyKeyStoreToDomain(adminServerName, createTrustKeyStorePath(adminServerName), createTrustKeyStoreFileName(adminServerName))
		
        # delete certificate at working dir
        os.remove(createCertificatePath(adminServerName))

    except:
        dumpStack()

main()
