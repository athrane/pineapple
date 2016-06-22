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


package com.alpha.pineapple.plugin.weblogic.jmx.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;

/**
 * Integration test for WeblogicJMXEditSession.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class WeblogicJMXEditSessionIntegrationTest
{
	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
	
	/**
	 * Object under test. 
	 */
    WeblogicJMXEditSession session;

    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;
    
    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		
    
    /**
     * Some random WLDF name.
     */
	String randomWldfName;
	
    /**
     * Random name.
     */
    String randomWorkManagerName;    
	    
    @Before
    public void setUp() throws Exception
    {
    	randomWldfName = RandomStringUtils.randomAlphabetic(10)+"-wldf";    	
    	randomWorkManagerName = RandomStringUtils.randomAlphabetic(10) +"-wm";    	
    	
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();
        
		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();                		        
    }

    @After
    public void tearDown() throws Exception
    {
		jmxMother.deleteWorkManager(session, randomWorkManagerName);
    	
    	if(session != null) {    		
    		if (session.isConnected()) {
    			
    	    	// only delete WLDF System Resource if session hasn't been actively disconnected    			
    	    	//jmxMother.deleteWldfSystemResource(session, randomWldfName);
    	    	
    	    	session.disconnect();
    		}    		
    	}    	
    }


    /**
     * Test session can connect 
     * on the administration server listen port 
     * using the WebLogic T3 protocol  
     */
    @Test
    public void testCanConnect_UsingWlsT3Protocol()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsT3Protocol();
    	
        // test
        assertNotNull( session.getMBeanServerConnection() );
    }

    
    /**
     * Test session can connect 
     * on the administration server listen port 
     * using the WebLogic HTTP protocol.  
     */
    @Test
    public void testCanConnect_UsingWlsHttpProtocol()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsHttpProtocol();
    	
        // test
        assertNotNull( session.getMBeanServerConnection() );
    }

    /**
     * Test session can connect 
     * on the administration server listen port 
     * using the WebLogic IIOP protocol  
     */
    @Test
    public void testCanConnect_UsingWlsIiopProtocol()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsIiopProtocol();
    	
        // test
        assertNotNull( session.getMBeanServerConnection() );
    }

    /**
     * Test session can connect 
     * on the administration server listen port 
     * using the WebLogic RMI protocol  
     */
    @Test
    public void testCanConnect_UsingWlsRmiProtocol()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsRmiProtocol();
    	
        // test
        assertNotNull( session.getMBeanServerConnection() );
    }

    /**
     * Test session can connect 
     * on the administration server listen port 
     * using the JDK IIOP protocol  
     */
    @Test
    public void testCanConnect_UsingJdkIiopProtocol()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSessionUsingJdkIiopProtocol();
    	
        // test
        assertNotNull( session.getMBeanServerConnection() );
    }

    /**
     * Test session can connect 
     * on the JMX connector listen port 
     * using the JDK RMI protocol  
     */
    @Test
    public void testCanConnect_UsingJdkRmiProtocol()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSessionUsingJdkRmiProtocol();
    	
        // test
        assertNotNull( session.getMBeanServerConnection() );
    }
    
    
    /**
     * Test that connection is defined after connect.
     */
    @Test
    public void testJmxConnectionIsDefinedAfterConnect()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();
    	
        // test
        assertNotNull( session.getMBeanServerConnection() );
    }

    /**
     * Test that connection is undefined after disconnect.
     * @throws Exception If test fails.
     */
    @Test
    public void testJmxConnectionIsDefinedAfterDisconnect() throws Exception
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();

        // test
        assertNotNull( session.getMBeanServerConnection() );
        
        // disconnect
        session.disconnect();

        // test
        assertNull( session.getMBeanServerConnection() );        
    }
    
    /**
     * Test that IsConnected() returns true after connect.
     */
    @Test
    public void testIsConnectedQueryReturnsTrueAfterConnect()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();
    	
        // test
        assertTrue( session.isConnected() );
    }

    /**
     * Test that IsConnected() returns false after connect.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testIsConnectedQueryReturnsFalseAfterDisconnect() throws Exception
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();
    	
        // test
        assertTrue( session.isConnected() );
        
        // disconnect
        session.disconnect();

        // test
        assertFalse( session.isConnected() );        
    }
    
    /**
     * Test that session isn't configured for editing after connect.
     */
    @Test
    public void testSessionIsntConfiguredForEditAfterConnect()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();

        // test
        assertFalse( session.isSessionConfiguredForEditing() );
    }
    
    /**
     * Test that session is configured for editing after invocation of the startEdit() method.
     * @throws Exception If test fails.
     */
    @Test
    public void testSessionIsConfiguredForEditAfterStartEdit() throws Exception
    {
        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);

        // test
        assertTrue( session.isSessionConfiguredForEditing() );
    }
    
    /**
     * Test that session isn't configured for editing after disconnect.
     * @throws Exception If test fails.
     */
    @Test
    public void testSessionIsntConfiguredForEditAfterDisconnect() throws Exception
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();

        // test 
        assertFalse( session.isSessionConfiguredForEditing() );
        
        // disconnect
        session.disconnect();

        // test
        assertFalse( session.isSessionConfiguredForEditing() );        
    }

    
    /**
     * Test that getDomainMBean method returns null 
     * if JMX session is connected but hasn't started an edit session.
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsntDefinedAfterConnect() throws Exception
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();
    	
    	// get object name
        ObjectName objName = session.getDomainMBeanObjectName();
        
        // test
        assertNull( objName );
    }

    /**
     * Test that DomainMBean object name is null 
     * if JMX session is connected but hasn't started an edit session.
     */
    @Test
    public void testDomainMBeanObjectNameIsntDefinedAfterConnect()
    {
        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();
    	
        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNull( objName );
    }
    
    /**
     * Test that domain MBean object name is defined after startEdit().
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsDefinedAfterStartEdit() throws Exception
    {
        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );
    }

    /**
     * Test that domain MBean object name is defined after startEdit()
     * 
     * With session connected 
     * on the administration server listen port 
     * using the WebLogic T3 protocol.  
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsDefinedAfterStartEdit_UsingWlsT3Protocol() throws Exception
    {
        // create session and start edit.
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsT3Protocol();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );
    }
    
    /**
     * Test that domain MBean object name is defined after startEdit()
     * 
     * With session connected 
     * on the administration server listen port 
     * using the WebLogic HTTP protocol.  
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsDefinedAfterStartEdit_UsingWlsHttpProtocol() throws Exception
    {
        // create session and start edit.
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsHttpProtocol();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );
    }

    /**
     * Test that domain MBean object name is defined after startEdit()
     * 
     * With session connected 
     * on the administration server listen port 
     * using the WebLogic IIOP protocol.  
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsDefinedAfterStartEdit_UsingWlsIiopProtocol() throws Exception
    {
        // create session and start edit.
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsIiopProtocol();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );
    }

    /**
     * Test that domain MBean object name is defined after startEdit()
     * 
     * With session connected 
     * on the administration server listen port 
     * using the WebLogic RMI protocol.  
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsDefinedAfterStartEdit_UsingWlsRmiProtocol() throws Exception
    {
        // create session and start edit.
        session = sessionMother.createConnectedWlsJmxEditSessionUsingWlsRmiProtocol();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );
    }

    /**
     * Test that domain MBean object name is defined after startEdit()
     * 
     * With session connected 
     * on the administration server listen port 
     * using the JDK IIOP protocol.  
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsDefinedAfterStartEdit_UsingJdkIiopProtocol() throws Exception
    {
        // create session and start edit.
        session = sessionMother.createConnectedWlsJmxEditSessionUsingJdkIiopProtocol();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );
    }

    /**
     * Test that domain MBean object name is defined after startEdit()
     * 
     * With session connected 
     * on the JMX connector listen port 
     * using the JDK RMI protocol.  
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsDefinedAfterStartEdit_UsingJdkRmiProtocol() throws Exception
    {
        // create session and start edit.
        session = sessionMother.createConnectedWlsJmxEditSessionUsingJdkRmiProtocol();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );
    }
    
    /**
     * Test that domain MBean is undefined after save and activate.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testDomainMBeanIsUndefinedAfterSaveAndActivate() throws Exception
    {
        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);

        // test
        Object objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );

        // save
        session.saveAndActivate();
        
        // test
        objName = session.getDomainMBeanObjectName();
        assertNull( objName );            
    }

    /**
     * Test that start edit can be invoked multiple times in a row.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanInvokeStartEditMultipleTimes() throws Exception
    {
        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);

        // test
        ObjectName objName = session.getDomainMBeanObjectName();
        assertNotNull( objName );
        assertTrue( objName instanceof ObjectName );

    	// start edit
        session.startEdit();
        
        // test
        ObjectName objName2 = session.getDomainMBeanObjectName();
        assertNotNull( objName2 );
        assertTrue( objName2 instanceof ObjectName );        
        assertEquals(objName, objName);
    }
 
    /**
     * Test that attribute "Server" is remapped to "Servers" on Domain object name.
     */
    @Test
    public void testServerAttributeOnDomainIsRemappedToServers() throws Exception
    {
        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
    	
    	// test 
        assertNotNull( domainObjName );
    	
		// get name    	
    	MBeanAttributeInfo attributeInfo = session.getAttributeInfo(domainObjName, "Server");    	
    			
		// test
		assertNotNull(attributeInfo);
		assertEquals(attributeInfo.getName(), "Servers");
    }

    /**
     * Test that attribute "WorkmanagerShutdownTrigger" can be read from work manager.
     */
    @Test
    public void testGetWorkManagerShutdownTriggerFromWorkManager() throws Exception
    {    	
    	final String query = "/SelfTuning/WorkManagers[@Name='"+ randomWorkManagerName+ "']";

        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
        
		// create virtual host 
		jmxMother.createWorkManager(session, randomWorkManagerName);
            	
    	// get work manager 
    	ObjectName wmObjName = (ObjectName) jmxMother.getAttributeByXPath(session, query );

		// test
		assertNotNull(wmObjName );
    	assertEquals(ObjectName.class, wmObjName.getClass());
    	
		// get work manager shut down trigger    	
    	Object value = session.getAttribute(wmObjName, "WorkManagerShutdownTrigger");
    	
		// test
		assertNotNull(value);		
		assertEquals(ObjectName.class, value.getClass());    	    	    	
    }    
    
    /**
     * Test that name attribute can be read from Domain MBean.
     */
    @Test
    public void testGetNameFromDomainObjName() throws Exception
    {
        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	// test 
        assertNotNull( domainObjName );
    	
		// get name    	
    	String name = session.getNameAttributeFromObjName(domainObjName);
    			
		// test
		assertNotNull(name);
		assertEquals(WJPIntTestConstants.targetEnvironment, name);
    }
    

    /**
     * Test that name attribute can be read from SecurityConfiguration MBean.
     * 
     */
    @Test
    public void testGetNameFromSecurityConfiguration() throws Exception
    {
    	final String query = "/SecurityConfiguration";

        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	// test 
        assertNotNull( domainObjName );
    	
    	// get security configuration
		Object secConfObjName = jmxMother.getAttributeByXPath(session, domainObjName, query);    	
    	    
    	// test
		assertNotNull(secConfObjName);
    	assertEquals(ObjectName.class, secConfObjName.getClass());    	

		// get name    	
    	String name = session.getNameAttributeFromObjName( (ObjectName) secConfObjName);
    			
		// test
		assertNotNull(name);		
		assertEquals(WJPIntTestConstants.targetEnvironment, name);
    }

    
    /**
     * Test that name attribute can be read from first realm.
     * 
     */
    @Test
    public void testGetNameFromFirstRealm() throws Exception
    {
    	final String query = "/SecurityConfiguration/Realms[0]";

        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	// test 
        assertNotNull( domainObjName );
    	
    	// get first realm   
    	Object realmObjName = jmxMother.getAttributeByXPath(session, domainObjName, query);    	
    	    			    	
		// get name    	
    	String name = session.getNameAttributeFromObjName((ObjectName) realmObjName);
    			
		// test
		assertNotNull(name);		
		assertEquals(WJPIntTestConstants.realmName, name);    	
    }    	

    
    /**
     * Test that name attribute can be read from first authentication provider. 
     */
    @Test
    public void testGetNameFromFirstAuthenticationProvider() throws Exception
    {
    	final String query = "/SecurityConfiguration/Realms[0]/AuthenticationProviders[0]";

        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	// test 
        assertNotNull( domainObjName );
    	
    	// get first provider
    	Object providerObjName = jmxMother.getAttributeByXPath(session, domainObjName, query );    	
    	
		// get name    	
    	String name = session.getNameAttributeFromObjName( (ObjectName) providerObjName);
    			
		// test
		assertNotNull(name);		
		assertEquals(WJPIntTestConstants.defaultAuthenticator, name);    	    	
    }
            

    /**
     * Test that name attribute can be read from role mapper. 
     */
    @Test
    public void testGetNameFromRoleMapper() throws Exception
    {
    	final String query = "/SecurityConfiguration/Realms[0]/RoleMappers[0]";

        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	// test 
        assertNotNull( domainObjName );
    	
    	// get role mapper   
    	Object mapperObjName = jmxMother.getAttributeByXPath(session, domainObjName, query );
    	
		// get name    	
    	String name = session.getNameAttributeFromObjName((ObjectName) mapperObjName);
    			
		// test
		assertNotNull(name);		
		assertEquals(WJPIntTestConstants.defaultRoleMapper, name);    	    	
    }
    

    /**
     * Test that name attribute can be read from adjudicator. 
     */
    @Test
    public void testGetNameFromAdjudicator() throws Exception
    {
    	final String query = "/SecurityConfiguration/Realms[0]/Adjudicator";

        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	// test 
        assertNotNull( domainObjName );
    	
    	// get adjudicator
    	Object adjudicatorObjName = jmxMother.getAttributeByXPath(session, domainObjName, query );

		// test
		assertNotNull(adjudicatorObjName );
    	assertEquals(ObjectName.class, adjudicatorObjName.getClass());
    	
		// get name    	
    	String name = session.getNameAttributeFromObjName((ObjectName) adjudicatorObjName);
    			
		// test
		assertNotNull(name);		
		assertEquals(WJPIntTestConstants.defaultAdjudicator, name);    	    	
    }
    
    
    /**
     * Test that name attribute can be read from WLDFHarvesterBean.
     * 
     * @throws Exception If test fails.
     */
    @Test    
    public void testGetNameFromWldfHarvesterBean() throws Exception
    {
    	final String query = "/WLDFSystemResources[@Name='"+randomWldfName+"']/WLDFResource/Harvester";

        // create session and start edit.      
        session = sessionMother.createConnectedWlsJmxEditSession();
        jmxMother.startEdit(session);
    	
    	// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();

    	// test 
        assertNotNull( domainObjName );
    			
    	// create WLDF system resource
    	jmxMother.createWldfSystemResource(session, randomWldfName);
    	
    	// get WLDF harvester
    	Object harvesterObjName = jmxMother.getAttributeByXPath(session, domainObjName, query );
    	
		// test
		assertNotNull(harvesterObjName);
    	assertEquals(ObjectName.class, harvesterObjName.getClass());
    	
		// get name    	
    	String name = session.getNameAttributeFromObjName((ObjectName) harvesterObjName);
    			
		// test
		assertNotNull(name);		
		assertEquals(randomWldfName, name);    	    	    	
    }
    
}
