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
@Deprecated
public class WeblogicJMXPluginTestConstants
{

    /**
     * WebLogic user.
     */
	@Deprecated
    public static final String userWeblogic = "weblogic";

    /**
     * WebLogic password.
     */
    @Deprecated
    public static final String passwordWeblogic = "weblogic";

    /**
     * Test-Resource user.
     */
    @Deprecated
    public static final String userTestResource = "test-user";

    /**
     * Test-Resource password.
     */
    @Deprecated
    public static final String passwordTestResource = "test-password";    
    
    /**
     * WebLogic adm url.
     */
    @Deprecated
    public static final String weblogicUrl = "t3://127.0.0.1:7001";

    /**
     * MBean server url path, used to access WebLogic Edit MBean server.
     */
    @Deprecated
    public static final String weblogicEditUrlPath = "weblogic.management.mbeanservers.edit";

    /**
     * MBean server url path, used to access WebLogic Domain Runtime MBean server.
     */
    @Deprecated
    public static final String weblogicRuntimeUrlPath = "weblogic.management.mbeanservers.domainruntime";

    /**
     * WebLogic adm port.
     */
    @Deprecated
    public static int weblogicPort = 7001;

    /**
     * WebLogic adm hostname.
     */
    @Deprecated
    public static String weblogicHostname = "127.0.0.1";

    /**
     * Credential identifier, used to access WebLogic JMX Edit MBean server in the unit tests.
     */
    @Deprecated
    public static final String credentialIdWLSJMXEdit = "wls-edit";

    /**
     * Credential identifier, used to access WebLogic JMX Domain Runtime MBean server in the unit tests.
     */
    @Deprecated
    public static final String credentialIdWLSJMXRuntime = "wls-domainruntime";

    /**
     * Credential identifier, used to access the Test-Resource in the unit tests.
     */
    @Deprecated
    public static final String credentialIdTestResource = "test-resource-credentialidentifier";
    
    /**
     * Credential type, used to access JMX MBean servers in the unit tests.
     */
    @Deprecated
    public static final String credTypeJMX = "jmx";;

    /**
     * The target environment used in unit tests.
     */
    @Deprecated
    public static String targetEnvironment = "alphadomain";

    /**
     * Operation type used in unit tests.
     */
    @Deprecated
    public static String unittestOperationType = "unittest-operation";;

    /**
     * Handler composite-management value.
     */
    @Deprecated
    public static String managementCompositeValueTrue = "true";

    /**
     * Deployment directory where the unit test expects Pineapple-Unit packages to be located.
     */
    @Deprecated
    public static String pineappleunitDir = "c:/deployments";

    /**
     * Name of Pineapple-Unit module used in unit tests.
     */
    @Deprecated
    public static String pineappleunitName = "pineappleunit-test-application-1.0.16";

    /**
     * Runtime directory used by unit test to store data.
     */
    @Deprecated
    public static String runtimeDirectory = "c:/temp/pineapple-unittest/";

    /**
     * Types of platform environment used during unit test.
     */
    @Deprecated
    public enum Environment
    {
        LOCAL, TEST, QA, PRODCTION
    };

    /**
     * Commons Chain catalog name.
     */
    @Deprecated
    public static final String CATALOG_NAME = "pinapple-catalog-1.0";

    /**
     * Null Resource implementation factory used during unit tests. Return nulls from all
     * create methods.
     */
    @Deprecated
    public static final String resourceFactoryNull = "com.alpha.pineapple.session.NullResourceFactory";

    /**
     * Null Resource implementation factory used during unit tests. Return nulls from all
     * create methods.
     */
    @Deprecated
    public static final String resourceFactoryTestResource = "com.alpha.pineapple.resource.test.TestFactoryImpl";
    
    /**
     * Resource identifier for WebLogic JMX Edit MBean server used in unit tests.  
     */
    @Deprecated
    public static final String resourceIdentifierWLSEdit = "wls-edit";

    /**
     * Resource identifier for Test-Resource used in unit tests.  
     */    
    @Deprecated
    public static final String resourceIdentifierTestResource = "test-resource-resourceidentifier";

    /**
     * Resource identifier for Test-Resource used in unit tests.  
     */    
    @Deprecated
    public static final String resourceIdentifierNetworkResource = "network-test";
    
    /**
     * Resource identifier for WebLogic JMX Runtime MBean server used in unit tests.  
     */    
    @Deprecated
    public static final String resourceIdentifierWLSRuntime = "wls-runtime";
    
    /**
     * File name for credentials file used in unit tests.
     */
    @Deprecated
    public static final String credentialsFileName = "credentials.xml";
}
