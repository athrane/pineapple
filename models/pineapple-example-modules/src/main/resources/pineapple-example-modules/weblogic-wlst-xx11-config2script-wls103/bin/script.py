
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
		
def main():   

    try:
        # listing system arguments
        printSystemProperties()
        printPineappleArguments()

        # system argument contains pineappleModulePath
        configToScript(domainDirectory, outputDirectory)

    except:
        dumpStack()

main()
