
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
# Configure HTTP tunneling
#=======================================================================================
def configureHttpTunneling():
    cd('/Servers/'+adminServerName)
    cmo.setTunnelingEnabled(true)
    
def main():   

    try:
        # listing system arguments
        printPineappleArguments()

        # configure domain
        readDomain(getDomainDirectory())
        configureHttpTunneling()
        updateDomain()

    except:
        dumpStack()

main()
