
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
    return os.path.join( domainsDirectory, domainName)

#=======================================================================================
# Configure IIOP
#=======================================================================================
def configureIiop():
    cd('/Servers/'+adminServerName)
    cmo.setTunnelingEnabled(true)

def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # configure domain
        readDomain(getDomainDirectory())
        configureIiop()
        updateDomain()

    except:
        dumpStack()

main()
