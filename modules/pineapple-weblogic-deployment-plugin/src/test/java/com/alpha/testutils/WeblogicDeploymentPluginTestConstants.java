/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
 * 
 * This file is part of Pineapple.
 * 
 * Pineapple is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 * 
 * Pineapple is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public 
 * license for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/


package com.alpha.testutils;


/**
 * Definition of constants used in the test cases.
 */
public class WeblogicDeploymentPluginTestConstants
{
	/**
     * Spring configuration file for the JMX plugin.
     */    
    static final String JMX_SPRING_CONFIG = "/com.alpha.pineapple.plugin.weblogic.deployment-config.xml";

	/**
	 * Spring bean id for message provider.
	 */
    static final String MESSAGEPROVIDER_BEAN_ID = "messageProvider";

	/**
	 * Spring bean id for resource property getter.
	 */
    static final String RESOURCE_PROPERTY_GETTER_BEAN_ID = "propertyGetter";
    
    /**
     * WebLogic user.
     */
    public static final String userWeblogic = "weblogic";

    /**
     * WebLogic password.
     */
    public static final String passwordWeblogic = "Weblogic11g"; 

    /**
     * Test-Resource user.
     */
    public static final String userTestResource = "test-user";

    /**
     * Test-Resource password.
     */
    public static final String passwordTestResource = "test-password";    
    
    /**
     * MBean server url path, used to access WebLogic Edit MBean server.
     */
    public static final String weblogicEditUrlPath = "weblogic.management.mbeanservers.edit";

    /**
     * MBean server url path, used to access WebLogic Domain Runtime MBean server.
     */
    public static final String weblogicRuntimeUrlPath = "weblogic.management.mbeanservers.domainruntime";

    /**
     * WebLogic adm port.
     */
    public static int weblogicPort = 7001;

    /**
     * WebLogic adm hostname.
     */
    public static String weblogicHostname = "127.0.0.1";

    /**
     * Credential identifier, used to access WebLogic JMX Edit MBean server in the unit tests.
     */
    public static final String credentialIdWLSJMXEdit = "wls-edit";

    /**
     * Credential identifier, used to access WebLogic JMX Domain Runtime MBean server in the unit tests.
     */
    public static final String credentialIdWLSJMXRuntime = "wls-domainruntime";

    /**
     * Credential identifier, used to access the Test-Resource in the unit tests.
     */
    public static final String credentialIdTestResource = "test-resource-credentialidentifier";

    /**
     * Credential identifier, used to access the WebLogic deployment resource in the unit tests.
     */
    public static final String credentialIdWeblogicDeployment = "weblogic-deployment-credential";
    
    /**
     * Credential type, used to access JMX MBean servers in the unit tests.
     */
    public static final String credTypeJMX = "jmx";;

    /**
     * The target environment used in unit tests.
     */
    public static String targetEnvironment = "localdomain";

    /**
     * Deployment directory where the unit test expects the Pineapple modules to be located.
     */
    public static String runtimeRootDir = "c:\\deployments";

    /**
     * Name of management data module used in unit tests.
     */
    public static String managementDataName = "pineappleunit-test-application-1.0.16";

    /**
     * Name of management data module used in unit tests.
     */
    public static String managmentDataName2 = "manually-defined-app-name";

    /**
     * Name of management data source used in unit tests.
     */    
    public static String  managementDataSource = "manually-defined-source";    

    /**
     * Name of management data deployment plan used in unit tests.
     */     
    public static String managementDataPlan = "manually-defined-plan";
    
    /**
     * Name of ear-application used in unit tests.
     */
    public static String earApplicationName = "my-test-application-1.0.ear";
        
    /**
     * Runtime directory used by unit test to store data.
     */
    public static String runtimeDirectory = "c:/temp/pineapple-unittest/";
        
    /**
     * Resource identifier for WebLogic JMX Edit MBean server used in unit tests.  
     */
    public static final String resourceIdentifierWLSEdit = "wls-edit";

    /**
     * Resource identifier for Test-Resource used in unit tests.  
     */    
    public static final String resourceIdentifierTestResource = "test-resource-resourceidentifier";

    /**
     * Resource identifier for Test-Resource used in unit tests.  
     */    
    public static final String resourceIdentifierNetworkResource = "network-test";
    
    /**
     * Resource identifier for WebLogic JMX Runtime MBean server used in unit tests.  
     */    
    public static final String resourceIdentifierWLSRuntime = "wls-runtime";

    /**
     * Resource identifier for WebLogic deployment resource used in unit tests.  
     */
    public static final String resourceIdentifierWeblogicDeployment = "weblogic-deployment";
    
    /**
     * File name for credentials file used in unit tests.
     */
    public static final String credentialsFileName = "credentials.xml";
    
    /**
     * WebLogic administration protocol.
     */
    public static final String weblogicProtocol = "t3";
    
    /**
     * Management data "app" directory name.
     */
    public static final String managementdataAppDirName= "app";

    /**
     * target used in unit test.
     */    
    public static final String targetOne = "target1";

    /**
     * target used in unit test.
     */    
    public static final String targetTwo = "target2";

    /**
     * targets used in unit test.
     */    
    public static final String targetOneAndTwoCombined = "target1,target2";
        
}
