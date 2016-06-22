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
 * Definition of constants used in the integration test cases.
 * 
 * WJPIntTestConstants is a short name for: 
 * Weblogic-JMX-Plugin Integration Test Constants. 
 */
public class WJPIntTestConstants
{
	/**
     * Spring configuration file for the JMX plugin.
     */    
    static final String JMX_SPRING_CONFIG = "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml";
	
	/**
	 * Spring bean id for message provider.
	 */
    static final String MESSAGEPROVIDER_BEAN_ID = "messageProvider";

	/**
	 * Spring bean id for MBean meta data repository.
	 */
    static final String METADATAREPOSITOY_BEAN_ID = "mbeanMetadataRepository";

	/**
	 * Spring bean id for JMX Service URL factory factory.
	 */
    static final String SERVICEURLFACTORYFACTORY_BEAN_ID = "jmxServiceUrlFactoryFactory";
    
    /**
     * WebLogic user.
     */
    public static final String userWeblogic = "weblogic";

    /**
     * WebLogic password.
     */
    public static final String passwordWeblogic = "Weblogic99"; 
    
    /**
     * WebLogic administration port.
     */
    public static final int weblogicAdmPort = 7001;

    /**
     * WebLogic JMX port.
     */
    public static final int weblogicJmxPort = 7091;
    
    /**
     * WebLogic administration host name.
     */
    public static final String weblogicHostname = "localhost";
    
    /**
     * Credential identifier, used to access WebLogic JMX Edit MBean server in the unit tests.
     */
    public static final String credentialIdWLSJMXEdit = "wls-edit";

    /**
     * The target environment used in unit tests.
     */
    public static final String targetEnvironment = "alphadomain-jmxplugin-inttest";

    /**
     * Configuration file for target domain.
     */
   public static final String environmentConfigFile = targetEnvironment + "-config.xml";
   
   /**
    * Number of WebLogic 10.3.5 schema property.
    */
  public static final int numberOf1035SchemaProperties = 91;

  /**
   * Number of Realm attributes.
   */  
  public static final int numberRealmAttributes = 27;
  
  /**
   * Name of administration server in target environment.
   */
  public static String administrationServerName = "admserver";

  /**
   * Security realm name in target environment.
   */
  public static final String realmName = "myrealm";

  /**
   * Name of default authenticator in in target environment.
   */  
  public static final String defaultAuthenticator = "DefaultAuthenticator";

  /**
   * Name of default role mapper in in target environment.
   */    
  public static final String defaultRoleMapper = "XACMLRoleMapper";

  /**
   * Name of default adjudicator in in target environment.
   */      
  public static final String defaultAdjudicator = "DefaultAdjudicator";
  
}
