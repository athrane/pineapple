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


package com.alpha.pineapple.plugin.weblogic.jmx.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
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
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant.ValueState;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;

/**
 * Integration test for the <code>CreateMBeanCommand</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class CreateMBeanCommandIntegrationTest
{
	/**
     * Logger object.
     */
    Logger logger = Logger.getLogger( this.getClass().getName() );
		
    /**
     * Context.
     */
    Context context;
    
    /**
     * Resolved type.
     */
    ResolvedType resolvedType;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult; 
	
    /**
     * Object under test.
     */
    @Resource
    Command createMBeanCommand;
			
	/**
	 * JMX session.
	 */
	WeblogicJMXEditSession session;

	/**
	 * WebLogic JMX session object mother.
	 */
	ObjectMotherWeblogicJmxSession sessionMother;
	
    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		
	
    /**
     * Some random virtual host name.
     */
    String randomVirtualHostName;

    /**
     * Some random WLDF name.
     */
    String randomWldfName;

    /**
     * Some random WLDF name.
     */
    String randomWldfName2;

    /**
     * Some random machine name.
     */
    String randomMachineName;

    /**
     * Some random WorkManager name.
     */
    String randomWorkManagerName;

    /**
     * Some random WorkManager shutdown trigger name.
     */
    String randomWorkManagerShutdownTriggerName;
    
    @Before
    public void setUp() throws Exception    
    {
    	// generate random name
    	randomVirtualHostName = RandomStringUtils.randomAlphabetic(10)+"-vh";
    	randomWldfName = RandomStringUtils.randomAlphabetic(10)+"-wldf";
    	randomWldfName2 = RandomStringUtils.randomAlphabetic(10)+"-wldf";
    	randomMachineName = RandomStringUtils.randomAlphabetic(10)+"-machine";
    	randomWorkManagerName = RandomStringUtils.randomAlphabetic(10)+"-wm";
    	randomWorkManagerShutdownTriggerName = RandomStringUtils.randomAlphabetic(10)+"-wmst";
    	
        // create context
        context = new ContextBase();

        // create result
        executionResult = new ExecutionResultImpl("root");        
    	
		// create session object mother;
		sessionMother = new ObjectMotherWeblogicJmxSession();

        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();        
        
		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();		
    }

    @After
    public void tearDown() throws Exception
    {    	
		// delete stuff if it exists
		jmxMother.deleteVirtualHost(session, randomVirtualHostName);
		jmxMother.deleteWldfSystemResource(session, randomWldfName);
		jmxMother.deleteMachine(session, randomMachineName);
		jmxMother.deleteWorkManager(session, randomWorkManagerName);
				
    	executionResult = null;
		context = null;
		sessionMother = null;
		
		// destroy session
		if (session != null) {
			session.disconnect();
		}
		session = null;    	
		jmxMother = null;
    }
	
    /**
     * Test that command can successfully create virtual host,
     * and that the correct state can be observed in the MBean model.
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateVirtualHost_MBeanModelHasCorrectState() throws Exception
    {    	    	
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "VirtualHosts", randomVirtualHostName));        						
    }
    
    /**
     * Test that command can successfully create virtual host,
     * and that the correct state is returned in the resolved participant.
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateVirtualHost_ResolvedModelHasCorrectState() throws Exception
    {   
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );
		assertTrue(resolvedType.getSecondaryParticiant().getValue() instanceof ObjectName);
		
		// get value
		ObjectName value = (ObjectName) resolvedType.getSecondaryParticiant().getValue();
                
        // test
        assertEquals( randomVirtualHostName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "VirtualHost", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
    }
    
    
    /**
     * Test that command can successfully create WLDFHarvestedTypeBean,
     * and that the correct state can be observed in the MBean model. 
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateWLDFHarvestedType_MBeanModelHasCorrectState() throws Exception
    {           	
    	// create WLDF system resource
    	jmxMother.createWldfSystemResource(session, randomWldfName);
    	
		// start edit session 
    	jmxMother.startEdit(session);
    	    			    	
		// create parent, which contains a WLDF harvester
		ResolvedType parent = jmxMother.createWldfHarvesterObjName_ResolvedType(session, randomWldfName);

		// create resolved type, which is a failed resolved WLDF harvested type		
		ObjectName wldfHarvesterObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), wldfHarvesterObjName, "HarvestedTypes");
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomWldfName2, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		Object result = jmxMother.getAttributeByXPath(session, wldfHarvesterObjName, "/HarvestedTypes");
		assertNotNull(result);
		assertNotNull(result instanceof ObjectName);
		
		// get name attribute from WLDF harvested type  
		ObjectName objName = (ObjectName) result;  		
		Object name = jmxMother.getAttributeByXPath(session, objName, "/Name");
				
		// test		
		assertEquals(randomWldfName2, name);				
    }

    /**
     * Test that command can successfully create WLDFHarvestedTypeBean,
     * and that the correct state is returned in the resolved participant.
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateWLDFHarvestedType_ResolvedModelHasCorrectState() throws Exception
    {       	
    	// create WLDF system resource
    	jmxMother.createWldfSystemResource(session, randomWldfName);

		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create parent, which contains a WLDF harvester
		ResolvedType parent = jmxMother.createWldfHarvesterObjName_ResolvedType(session, randomWldfName);

		// create resolved type, which is a failed resolved WLDF harvested type		
		ObjectName wldfHarvesterObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), wldfHarvesterObjName, "HarvestedTypes");
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomWldfName2, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );
		assertTrue(resolvedType.getSecondaryParticiant().getValue() instanceof ObjectName);
		
		// get value
		ObjectName value = (ObjectName) resolvedType.getSecondaryParticiant().getValue();
                
        // test
        assertEquals( randomWldfName2, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "weblogic.diagnostics.descriptor.WLDFHarvestedTypeBean", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
		
    }
    

    /**
     * Test that command succeeds if (VirtualHost) MBean already exists,
     * and that the correct state can be observed in the MBean model. 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSucceedsIfVirtualHostAlreadyExists_MBeanModelHasCorrectState() throws Exception
    {
    	// create virtual host
    	ObjectName vhObjName = jmxMother.createVirtualHost(session, randomVirtualHostName);
    	
		// start edit session 
    	jmxMother.startEdit(session);
		
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a successful resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();		
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo, vhObjName );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 

        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "VirtualHosts", randomVirtualHostName));        						
    }

    /**
     * Test that command succeeds if (VirtualHost) MBean already exists.
     * and that the correct state is returned in the resolved participant. 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSucceedsIfVirtualHostAlreadyExists_ResolvedModelHasCorrectState() throws Exception
    {
    	// create virtual host
    	ObjectName vhObjName = jmxMother.createVirtualHost(session, randomVirtualHostName);
    	
		// start edit session 
    	jmxMother.startEdit(session);
		
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a successful resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();		
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo, vhObjName );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 

        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );
		assertTrue(resolvedType.getSecondaryParticiant().getValue() instanceof ObjectName);
		
		// get value
		ObjectName value = (ObjectName) resolvedType.getSecondaryParticiant().getValue();
                
        // test
        assertEquals( randomVirtualHostName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "VirtualHost", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
    }
 

    /**
     * Test that command can successfully create a single VirtualHost MBean from collection.
     * 
     * Since collections are presented as a explicit resolved collections , it 
     * represents the problem that the parent (i.e. the resolved collection)
     * can produce a valid object name to invoke the factory method on. 
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCanSuccessfullyCreateVirtualHostFromResolvedCollection_MBeanModelHasCorrectState() throws Exception
    {
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 

        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "VirtualHosts", randomVirtualHostName));        						
    }

    /**
     * Test that command can successfully create a single VirtualHost MBean from collection.
     * 
     * Since collections are presented as a explicit resolved collections , it 
     * represents the problem that the parent (i.e. the resolved collection)
     * can produce a valid object name to invoke the factory method on. 
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCanSuccessfullyCreateVirtualHostFromResolvedCollection_ResolvedModelHasCorrectState() throws Exception
    {
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 

        // save and activate
		jmxMother.saveAndActivate(session);

		// get value
		ObjectName value = (ObjectName) resolvedType.getSecondaryParticiant().getValue();
                
        // test
        assertEquals( randomVirtualHostName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "VirtualHost", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        								
    }
    
    /**
     * Test that command fails to create a single VirtualHost MBean from collection
     * if the grand parent is null.
     * 
     * Since collections are presented as a explicit resolved collections , it 
     * represents the problem that the parent (i.e. the resolved collection)
     * can produce a valid object name to invoke the factory method on. 
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsToCreateVirtualHostFromResolvedCollectionIfGrandParentIsNull_MBeanModelHasCorrectState() throws Exception
    {   
		// create grand parent, which is NULL
		ResolvedType grandParent = null;    

		// start edit session 
    	jmxMother.startEdit(session);
		
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);

		// start edit session 
    	jmxMother.startEdit(session);

		// get created MBean 
		final String query = "/VirtualHosts[@Name='"+ randomVirtualHostName + "']";
        ObjectName[] value = (ObjectName[]) jmxMother.getAttributeByXPath(session, query);
                
        // test
        assertEquals(0,  value.length); // empty array, no MBean found.
    }

    /**
     * Test that command fails to create a single VirtualHost MBean from collection
     * if the grand parent is null.
     * 
     * Since collections are presented as a explicit resolved collections , it 
     * represents the problem that the parent (i.e. the resolved collection)
     * can produce a valid object name to invoke the factory method on. 
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsToCreateVirtualHostFromResolvedCollectionIfGrandParentIsNull_ResolvedModelHasCorrectState() throws Exception
    {   
		// create grand parent, which is NULL
		ResolvedType grandParent = null;    

		// start edit session 
    	jmxMother.startEdit(session);
		
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createVirtualHostObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved virtual host
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomVirtualHostName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertEquals(ExecutionState.FAILURE, executionResult.getState()  );
		assertFalse(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.FAILED );		
    }

    /**
     * Test that command can successfully create machine,
     * and that the correct state can be observed in the MBean model.
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateMachine_MBeanModelHasCorrectState() throws Exception
    {    	    	
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a virtual host collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createMachinesObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved machine
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomMachineName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "Machines", randomMachineName));        						
    }

    /**
     * Test that command can successfully create machine,
     * and that the correct state is returned in the resolved participant.
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateMachine_ResolvedModelHasCorrectState() throws Exception
    {   
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a machine collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createMachinesObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved machine
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomMachineName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );
		assertTrue(resolvedType.getSecondaryParticiant().getValue() instanceof ObjectName);
		
		// get value
		ObjectName value = (ObjectName) resolvedType.getSecondaryParticiant().getValue();
                
        // test
        assertEquals( randomMachineName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "Machine", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
    }
    


    /**
     * Test that command can successfully create node manager,
     * the case being that the node manager is already created by the machine 
     * and that the correct NodeManagerstate can be observed in the MBean model.
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateNodeManager_MBeanModelHasCorrectState() throws Exception
    {    	    	
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the domain MBean
		ResolvedType grandParent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);    	
    	
		// create parent, which contains a machine collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createMachinesObjNameCollection_ResolvedParticipant(session);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved machine
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomMachineName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "Machines", randomMachineName));

	    // test node manager is created		
		final String query = "/Machines[@Name='"+ randomMachineName+ "']/NodeManager";
		ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, query);
		
	    // test node manager has expected values				
        assertEquals( randomMachineName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "NodeManager", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
	    
    }
    

    /**
     * Test that command succeeds if (SelfTuning) MBean already exists,
     * and that the correct state can be observed in the MBean model. 
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSucceedsIfSelfTuningAlreadyExists_MBeanModelHasCorrectState() throws Exception
    {
		final String domainName = WJPIntTestConstants.targetEnvironment;
    	
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create parent, which is the domain MBean
		ResolvedType parent = jmxMother.createDomainObjName_ResolvedType(domainName, session);
		
		// get domain Object name
		ObjectName domainObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	
		// create resolved type, which is a successfully resolved SelfTuning thingy
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), domainObjName, "SelfTuning");    	
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();
		ObjectName stObjName = (ObjectName) jmxMother.getAttribute(session.getMBeanServerConnection(), domainObjName, attributeInfo.getName());
		ResolvedParticipant secondary = jmxMother.createNamedObjName_ResolvedParticipant(domainName, attributeInfo , stObjName);
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);    	
		
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 
		
        // save and activate
		jmxMother.saveAndActivate(session);
		
		// test self tuning has expected values				
		ObjectName value= (ObjectName) jmxMother.getAttributeByXPath(session, "/SelfTuning[@Name='"+domainName+"']");
        assertEquals( domainName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "SelfTuning", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
    }
    
    /**
     * Test that command can successfully create a single WorkManager MBean.
     * 
     * Since collections are presented as a explicit resolved collections , it 
     * represents the problem that the parent (i.e. the resolved collection)
     * can produce a valid object name to invoke the factory method on. 
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCanSuccessfullyCreateWorkManagerFromResolvedCollection_MBeanModelHasCorrectState() throws Exception
    {
		final String domainName = WJPIntTestConstants.targetEnvironment;
    	
		// start edit session 
    	jmxMother.startEdit(session);
    	
		// create grand parent, which is the SelfTuning MBean
		ResolvedType grandParent = jmxMother.createSelfTuningObjName_ResolvedType(session, domainName);    	
    	
		// create parent, which contains a WorkManagers collection
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant parentSecondary = jmxMother.createWorkManagerObjNameCollection_ResolvedParticipant(session, domainName);
		ResolvedType parent = ResolvedTypeImpl.createResolvedCollection(grandParent, parentPrimary, parentSecondary);    	

		// create resolved type, which is a failed resolved WorkManager
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) parent.getSecondaryParticiant().getType();
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomWorkManagerName, attributeInfo );
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	    	
		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 

        // save and activate
		jmxMother.saveAndActivate(session);

	    // test node manager has expected values				
		final String query = "/SelfTuning[@Name='"+ domainName+"']/WorkManagers[@Name='"+ randomWorkManagerName+ "']";		
		ObjectName value= (ObjectName) jmxMother.getAttributeByXPath(session, query );
        assertEquals( randomWorkManagerName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "WorkManager", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
    }

    /**
     * Test that command can successfully create a single WorkManagerShutdownTrigger MBean.
     * 
     * Since collections are presented as a explicit resolved collections , it 
     * represents the problem that the parent (i.e. the resolved collection)
     * can produce a valid object name to invoke the factory method on. 
     *  
     * @throws Exception If test fails.
     */
    
   
    /**
     * Test that command succeeds if WorkManagerShutdownTriggger can be created
     * and that the correct state can be observed in the MBean model. 
     *  
     * @throws Exception If test fails.
     */    
    @SuppressWarnings("unchecked")
	@Test
    public void testCreateWorkManagerShutdownTrigger_MBeanModelHasCorrectState() throws Exception
    {
		final String domainName = WJPIntTestConstants.targetEnvironment;
    	final String triggerAttrName = "WorkManagerShutdownTrigger";
		final String query = "/SelfTuning[@Name='"+ domainName+"']/WorkManagers[@Name='"+ randomWorkManagerName+ "']/"+triggerAttrName;
		
	   	// create work manager
    	ObjectName wmObjName = jmxMother.createWorkManager(session, randomWorkManagerName);
 		
		// start edit session 
    	jmxMother.startEdit(session);

		// create grand parent, which contains a WorkManagers collection
		ResolvedParticipant grandParentPrimary = jmxMother.createNullResolvedParticipant();
		ResolvedParticipant grandParentSecondary = jmxMother.createWorkManagerObjNameCollection_ResolvedParticipant(session, domainName);
		ResolvedType grandParent = ResolvedTypeImpl.createResolvedCollection(null, grandParentPrimary, grandParentSecondary);    	

		// create parent, which is a successfully resolved work manager
		MBeanAttributeInfo attributeInfo = (MBeanAttributeInfo) grandParent.getSecondaryParticiant().getType();
		ResolvedParticipant parentPrimary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant parentSecondary = jmxMother.createNamedObjName_ResolvedParticipant("WorkManagers", attributeInfo, wmObjName);
		ResolvedType parent = ResolvedTypeImpl.createResolvedType(grandParent, parentPrimary, parentSecondary);        
    	    	
		// create resolved type, which is a successfully resolved work manager shutdown trigger
		MBeanAttributeInfo triggerAttributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), wmObjName, triggerAttrName);	
		ResolvedParticipant primary = jmxMother.createNullResolvedParticipant();		
		ResolvedParticipant secondary = ResolvedParticipantImpl.createSuccessfulResult( triggerAttrName, triggerAttributeInfo, null, ValueState.NIL );								
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        

		// setup context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, session);
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);
		
		// execute command
		createMBeanCommand.execute(context);		 

        // save and activate
		jmxMother.saveAndActivate(session);
		
	    // test node manager shut down trigger has expected values				
		ObjectName value= (ObjectName) jmxMother.getAttributeByXPath(session, query );
        // test shut down trigger has same name as work manager		
        assertEquals( randomWorkManagerName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));
        assertEquals( triggerAttrName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        						
    }
    
}
