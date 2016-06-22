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


package com.alpha.pineapple.plugin.weblogic.jmx.operation;

import static com.alpha.testutils.WJPIntTestConstants.targetEnvironment;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.plugin.weblogic.jmx.WebLogicMBeanConstants;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;
import com.oracle.xmlns.weblogic.domain.DomainType.ConfigurationAuditType;


/**
 * Integration test for the <code>DeployConfiguration</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class DeployConfigurationIntegrationTest
{
    /**
     * Object mother for the WebLogic domain model.
     */
    ObjectMotherContent contentMother;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		

	/**
	 * JMX session.
	 */
	WeblogicJMXEditSession session;

	/**
	 * WebLogic JMX session object mother.
	 */
	ObjectMotherWeblogicJmxSession sessionMother;
    
    /**
     * Some random virtual host name.
     */
    String randomVirtualHostName;

    /**
     * Some random server name.
     */
    String randomServerName;
	
    /**
     * Some random WLDf resource name.
     */
    String randomWldfName; 

    /**
     * Random machine name.
     */
    String randomMachineName;

    /**
     * Random listen address.
     */
    String randomListenAddress;

    /**
     * Random Work Manager.
     */
    String randomWorkManagerName;
    
    /**
     * Trigger Name .
     */
    String randomTriggerName;     
    
    /**
     * Object under test.
     */
    @Resource
    DeployConfiguration deployConfiguration;

    @Before
    public void setUp() throws Exception
    {
    	// generate random name
    	randomVirtualHostName = RandomStringUtils.randomAlphabetic(10)+"-vh";
    	randomServerName = RandomStringUtils.randomAlphabetic(10)+"-server";
    	randomWldfName = RandomStringUtils.randomAlphabetic(10)+"-wldf";
    	randomMachineName = RandomStringUtils.randomAlphabetic(10)+"-machine";
    	randomListenAddress = RandomStringUtils.randomAlphabetic(10);
    	randomWorkManagerName = RandomStringUtils.randomAlphabetic(10)+"-wm";
    	randomTriggerName = RandomStringUtils.randomAlphabetic(10)+"-wmst";
    	    	
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();

        // create content mother
        contentMother = new ObjectMotherContent();

		// create session object mother;
		sessionMother = new ObjectMotherWeblogicJmxSession();

        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();        
        
		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();
        
        // create execution result
        result = new ExecutionResultImpl( "Root result" );        
    }

    @After
    public void tearDown() throws Exception
    {
		// delete stuff
		jmxMother.deleteVirtualHost(session, randomVirtualHostName);
		jmxMother.deleteWldfSystemResource(session, randomWldfName);
		jmxMother.deleteMachine(session, randomMachineName);
		//jmxMother.deleteWorkManager(session, randomWorkManagerName);
		
        // nullify references
        jmxMother = null;
        session = null;		
        sessionMother = null;
        contentMother = null;
        result = null;
    }

    /**
     * Test that TestOperation can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( deployConfiguration );
    }

    /**
     * Test that operation can execute with a minimal domain, i.e a named domain.
     * 
     * @throws Exception If test fails. 
     */
    @Test
    public void testCanExecuteWithMinimalDomain() throws Exception
    {
        // create content
        Object content = contentMother.createMinimalDomain( targetEnvironment );

        // invoke operation
        deployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        
    }

    /**
     * Test that operation can execute with a domain 
     * with single object: Domain
     * and single primitive attribute: Notes
     * 
     * @throws Exception If test fails.
     */    
    @Test
    public void testCanExecuteWithDomainPrimitiveNotes() throws Exception
    {
		final String attributeID = "Notes";
		
    	String randomStr = RandomStringUtils.randomAlphabetic(10);
    	
        // create content
        Object content = contentMother.createDomainWithPrimitiveNotes( targetEnvironment, randomStr );

        // invoke operation
        deployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());        
        
		// start edit session
		jmxMother.startEdit(session);		
        
		// get attribute
		String value = (String) jmxMother.getAttributeByXPath(session, "/"+attributeID);
		
		// test
        assertEquals( randomStr, value );        
    }

    /**
     * Test that operation can execute with a domain 
     * with single object: Domain
     * and single enum attribute: ConfigurationAuditType
     * 
     * @throws Exception If test fails.
     */    
    @Test
    public void testCanExecuteWithDomainEnumConfigurationAuditType() throws Exception
    {
		final String attributeID = "ConfigurationAuditType";
		
		// start edit session
		jmxMother.startEdit(session);		
    	
    	// clear enum 
		ObjectName domainObjName = (ObjectName) session.getDomainMBeanObjectName();
		MBeanAttributeInfo info = session.getAttributeInfo(domainObjName, attributeID);
		session.setAttribute(domainObjName, info, "none");
		
		// save
        jmxMother.saveAndActivate(session);

		// start edit session
		jmxMother.startEdit(session);		
        		
		// test value was cleared
		domainObjName = (ObjectName) session.getDomainMBeanObjectName();
		String value = (String) jmxMother.getAttributeByXPath(session, domainObjName, "/"+attributeID);		
        assertEquals("none", value);
                
		// create content
        Object content = contentMother.createDomainWithEnumConfigurationAuditType( targetEnvironment, ConfigurationAuditType.LOG );
        
        // invoke operation
        deployConfiguration.execute( content, session, result );

		// start edit session
		jmxMother.startEdit(session);		

        // test		
		value = (String) jmxMother.getAttributeByXPath(session, "/"+attributeID);		
        assertEquals("log", value);
    }
    
    
 
    /**
     * Test that operation can execute with a domain 
     * with two objects: Domain and virtual host.
     * And a multi value attribute: virtual host names.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithDomainAndMultiValueAttribute() throws Exception
    {
        // create random string
        String str1 = RandomStringUtils.randomAlphabetic(16);        
        String[] value = new String[] { str1 };        
    	        
		// create content
        Object content = contentMother.createDomainWithVirtualHostAndVHNames(targetEnvironment, randomVirtualHostName, value);

        // invoke operation
        deployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "VirtualHosts", randomVirtualHostName));        						
        
		// get Domain object name
    	ObjectName domainObjName = session.getDomainMBeanObjectName();
		
        // get virtual host
		final String query = "/VirtualHosts[@Name='"+ randomVirtualHostName+ "']";
		Object result = jmxMother.getAttributeByXPath(session, domainObjName , query);
		assertNotNull(result);
		assertNotNull(result instanceof ObjectName);
		
		// get virtual host names  
		ObjectName objName = (ObjectName) result;  		
		String[] actual = (String[]) jmxMother.getAttributeByXPath(session, objName, "/VirtualHostNames");
				        
        // test
        assertArrayEquals("some message", value, actual);
    }

    /**
     * Test that operation can execute with a domain 
     * with single object: Domain
     * and single primitive attribute: Notes
     * 
     * The test is run twice.
     * 
     * @throws Exception If test fails.
     */    
    @Test
    public void testCanExecuteTwiceWithDomainPrimitiveNotes() throws Exception
    {
		final String attributeID = "Notes";
    	
    	String randomStr = RandomStringUtils.randomAlphabetic(10);
    	String randomStr2 = RandomStringUtils.randomAlphabetic(10);
    	
    	// create child result for operation #1
    	ExecutionResult operationResult1 = result.addChild("operation #1");
    	    	
        // create content
        Object content = contentMother.createDomainWithPrimitiveNotes( targetEnvironment, randomStr );

        // invoke operation
        deployConfiguration.execute( content, session, operationResult1 );
        
        // test
        assertEquals(ExecutionState.SUCCESS, operationResult1.getState());        
        
		// start edit session
		jmxMother.startEdit(session);		
        
		// get attribute
		ObjectName domainObjName = (ObjectName) session.getDomainMBeanObjectName();		
		String value = (String) jmxMother.getAttributeByXPath(session, domainObjName, "/"+attributeID);
		
		// test
        assertEquals( randomStr, value );        

    	// create child result for operation #2
    	ExecutionResult operationResult2 = result.addChild("operation #2");
        
        // create content
        content = contentMother.createDomainWithPrimitiveNotes( targetEnvironment, randomStr2 );

        // invoke operation
        deployConfiguration.execute( content, session, operationResult2 );

        // test
        assertEquals(ExecutionState.SUCCESS, operationResult2.getState());        
        
		// start edit session
		jmxMother.startEdit(session);		
        
		// get attribute
		value = (String) jmxMother.getAttributeByXPath(session, "/"+attributeID);
		
		// test
        assertEquals( randomStr2, value );                
    }
    
    /**
     * Test that operation can execute with a domain 
     * with single WLDF system resource.
     * 
     * @throws Exception If test fails.
     */    
    @Test
    public void testCanExecuteWithDomainAndWldfSystemResource() throws Exception
    {
		final String query = "/WLDFSystemResources[@Name='"+ randomWldfName+ "']";
    	
        // create content
        Object content = contentMother.createDomainWithWldfsystemResource( targetEnvironment, randomWldfName );

        // invoke operation
        deployConfiguration.execute( content, session, result );

        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());        
        
		// start edit session
		jmxMother.startEdit(session);		
        
		// get attribute
		ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, query);
		
		// test
        assertEquals( randomWldfName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "WLDFSystemResource", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
    }
    
    /**
     * Test that operation can execute with a domain with single WLDF system resource 
     * and WLDF using the WLDF schema.
     * 
     * @throws Exception If test fails.
     */    
    @Test
    public void testCanExecuteWithDomainAndWldfDocument() throws Exception
    {
        // create content
        Object domainContent = contentMother.createDomainWithWldfsystemResource( targetEnvironment, randomWldfName );
        Object wldfContent = contentMother.createMinimalWldfResource( randomWldfName );

        // invoke operation #1
    	ExecutionResult operationResult1 = result.addChild("operation #1");
        deployConfiguration.execute( domainContent, session, operationResult1 );

        // test
        assertEquals(ExecutionState.SUCCESS, operationResult1.getState());        
        
        // invoke operation #1
    	ExecutionResult operationResult2 = result.addChild("operation #2");        
        deployConfiguration.execute( wldfContent, session, operationResult2 );
                
        // test
        assertEquals(ExecutionState.SUCCESS, operationResult2.getState());       
    }

	/**
	 * Test that operation can execute with a domain 
	 * with two objects: Domain and virtual host.
	 * 
	 * @throws Exception If test fails. 
	 */    
	@Test
	public void testCanExecuteWithDomainAndVirtualHost() throws Exception
	{
	    // create content
	    Object content = contentMother.createDomainWithVirtualHost( targetEnvironment, randomVirtualHostName );
	
	    // invoke operation
	    deployConfiguration.execute( content, session, result );
	    
	    // test
	    assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "VirtualHosts", randomVirtualHostName));        						
	}

    /**
     * Test that operation can execute with a domain 
     * with two objects: Domain and machine.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithDomainAndMachine() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithMachine( targetEnvironment, randomMachineName );

        // invoke operation
        deployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "Machines", randomMachineName));        						
    }

    /**
     * Test that operation can execute with a domain 
     * with three objects: Domain, machine and NodeManager
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithDomainAndMachineAndNodeManager() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithMachineAndNodeManager( targetEnvironment, randomMachineName );

        // invoke operation
        deployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "Machines", randomMachineName));        						
    }

    /**
     * Test that operation can execute with a domain 
     * with three objects: Domain, machine and NodeManager
     * Test that NodeManager is created.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithDomainAndMachineAndNodeManager2() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithMachineAndNodeManager( targetEnvironment, randomMachineName );

        // invoke operation
        deployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());

        jmxMother.startEdit(session);        
        final String query = "/Machines[@Name='"+ randomMachineName+ "']/NodeManager";
		ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, query);

		// test node manager has expected values				
        assertEquals( randomMachineName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "NodeManager", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        							    		
    }

    /**
     * Test that operation can execute with a domain 
     * with three objects: Domain, machine and NodeManager
     * Test that ListenAddress attribute is set.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithDomainAndMachineAndNodeManagerWithAttribute() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithMachineAndNodeManagerWithAttribute( targetEnvironment, randomMachineName, randomListenAddress );

        // invoke operation
        deployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());

        // test
        jmxMother.startEdit(session);        
        final String query = "/Machines[@Name='"+ randomMachineName+ "']/NodeManager/ListenAddress";
		String actual = (String) jmxMother.getAttributeByXPath(session, query);
        assertEquals(randomListenAddress, actual);				
    }

    /**
     * Test that operation can execute with a domain 
     * with one object: WorkManager.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithWorkManager() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithWorkManager( targetEnvironment, randomWorkManagerName );

        // invoke operation
        deployConfiguration.execute( content, session, result );

        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        
        // test
        jmxMother.startEdit(session);        
        final String query = "/SelfTuning/WorkManagers[@Name='"+ randomWorkManagerName+ "']";
        ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, query);
        assertEquals( randomWorkManagerName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "WorkManager", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        							    
    }
    
    /**
     * Test that operation can execute with a domain 
     * with two objects: WorkManager and ShutdownTrigger.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithWorkManagerAndShutdownTrigger() throws Exception
    {
		final String domainName = WJPIntTestConstants.targetEnvironment;
    	final String triggerAttrName = "WorkManagerShutdownTrigger";
		final String query = "/SelfTuning[@Name='"+ domainName+"']/WorkManagers[@Name='"+ randomWorkManagerName+ "']/"+triggerAttrName;
    	
        // create content
        Object content = contentMother.createDomainWithWorkManagerAndShutdownTrigger( targetEnvironment, randomWorkManagerName );

        // invoke operation
        deployConfiguration.execute( content, session, result );

        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        
        // test
        jmxMother.startEdit(session);        
		ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, query);
        assertEquals( randomWorkManagerName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "WorkManagerShutdownTrigger", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        							    				
    }
    
    /**
     * Test that operation can execute with a domain 
     * with two objects: WorkManager, ShutdownTrigger and a custom value for the Name attribute.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithWorkManagerAndShutdownTriggerWithCustomName() throws Exception
    {
		final String domainName = WJPIntTestConstants.targetEnvironment;
    	final String triggerAttrName = "WorkManagerShutdownTrigger";
		final String query = "/SelfTuning[@Name='"+ domainName+"']/WorkManagers[@Name='"+ randomWorkManagerName+ "']/"+triggerAttrName;
    	
        // create content
        Object content = contentMother.createDomainWithWorkManagerAndShutdownTrigger( targetEnvironment, randomWorkManagerName, randomTriggerName );

        // invoke operation
        deployConfiguration.execute( content, session, result );

        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());
        
        // test
        jmxMother.startEdit(session);        
		ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, query);
        assertEquals( randomTriggerName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "WorkManagerShutdownTrigger", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        							    				
    }
    
}
