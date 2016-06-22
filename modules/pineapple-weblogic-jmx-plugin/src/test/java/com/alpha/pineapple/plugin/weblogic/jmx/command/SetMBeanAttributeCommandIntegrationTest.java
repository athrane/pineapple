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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectName;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
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
import com.alpha.pineapple.plugin.weblogic.jmx.management.JMXManagmentException;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.pineapple.resolvedmodel.traversal.ModelResolver;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class SetMBeanAttributeCommandIntegrationTest
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
    Command setMBeanAttributeCommand;
	
    /**
     * MBean model resolver.
     */
    @Resource
    ModelResolver mbeansObjectNameBasedModelResolver;        
    
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
     * Some random server name.
     */
    String randomServerName;
    	
    /**
     * Random name.
     */
    String randomName;
    
    /**
     * Random name.
     */    
    int randomInteger;
    
    /**
     * Random boolean.
     */
    boolean randomBoolean;
    
    /**
     * Some random WLDF name.
     */
    String randomWldfName;

    /**
     * Some random WLDF Resource name.
     */
    String randomWldfResourceName;
    
    /**
     * Some random WLDF name.
     */
    String randomWldfNotificationName;    

    /**
     * Some random WLDF name.
     */
    String randomWldfWatchName;

    /**
     * Some random machine name.
     */
    String randomMachineName;
    
    @Before
    public void setUp() throws Exception
    {
    	// generate random name
    	randomVirtualHostName = RandomStringUtils.randomAlphabetic(10)+"-vh";
    	randomServerName = RandomStringUtils.randomAlphabetic(10)+"-server";
    	randomMachineName = RandomStringUtils.randomAlphabetic(10)+"-machine";
    	randomWldfName = RandomStringUtils.randomAlphabetic(10)+"-wldf";
    	randomWldfResourceName = RandomStringUtils.randomAlphabetic(10)+"-wldf";    	
    	randomWldfNotificationName = RandomStringUtils.randomAlphabetic(10)+"-wldf";
    	randomWldfWatchName = RandomStringUtils.randomAlphabetic(10)+"-wldf";
    	randomName = RandomStringUtils.randomAlphabetic(10);
    	randomBoolean = RandomUtils.nextBoolean();
    	randomInteger = RandomUtils.nextInt();
    	
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
				    	
		// set session
		mbeansObjectNameBasedModelResolver.setSession(session);
    }

    @After
    public void tearDown() throws Exception
    {
		// delete virtual host if it exists
		jmxMother.deleteVirtualHost(session, randomVirtualHostName);

		// delete WLDF system resource if it exists
		jmxMother.deleteWldfSystemResource(session, randomWldfName);
						
		// delete server if it exists
		jmxMother.deleteServer(session, randomServerName );
		
		// delete machine if it exists
		jmxMother.deleteMachine(session, randomMachineName);
					
        // nullify references
        jmxMother = null;
        session = null;
        resolvedType = null;
        executionResult = null;
    }

    /**
     * Set VirtualHost.Notes String attribute.
     * 
     * @return Resolved type 
     * 
     * @throws Exception If command fails.
     */
	@SuppressWarnings("unchecked")
	ResolvedType setNotesAttributeOnVirtualHost() throws Exception {
		final String attributeID = "Notes";
    	
		// create virtual host 
		jmxMother.createVirtualHost(session, randomVirtualHostName);

		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
		
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomName);
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
		
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
        
        return parent;
	}
    
    /**
     * Test that command can set attribute of type String,
     * and that the correct state can be observed in the MBean model. 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringAttribute_MBeanModelHasCorrectState() throws Exception
    {
    	ResolvedType parent = setNotesAttributeOnVirtualHost();		
        
		// get attribute
    	ObjectName objName = (ObjectName) parent.getSecondaryParticiant().getValue();    	
		String value = (String) jmxMother.getAttributeByXPath(session, objName, "/Notes");
		
		// test
        assertEquals( randomName, value );    	
    }

    /**
     * Test that command can set attribute of type String,
     * and that the correct state is returned in the resolved participant. 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringAttribute_RsolvedModelHasCorrectState() throws Exception
    {		
    	setNotesAttributeOnVirtualHost();	
    	
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );    	
    }

    
    /**
     * Test that command can set attribute of type String.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringAttribute2() throws Exception
    {
		final String attributeID = "Notes";

		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is the domain MBean
		ResolvedType parent = jmxMother.createDomainObjName_ResolvedType(WJPIntTestConstants.targetEnvironment, session);
		
		// create resolved type
		ObjectName domainObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomName);
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, domainObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String value = (String) jmxMother.getAttributeByXPath(session, domainObjName, "/"+attributeID);
		
		// test
        assertEquals( randomName, value );
    }
    
        
    /**
     * Test that command can set attribute of type boolean.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetBooleanAttribute() throws Exception
    {
		final String attributeID = "SendServerHeaderEnabled";

		// create virtual host 
		jmxMother.createVirtualHost(session, randomVirtualHostName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
		
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createBooleanResolvedParticipant(attributeID, randomBoolean);
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		boolean actual = (Boolean) jmxMother.getAttributeByXPath(session, vhObjName, "/"+attributeID);        
        assertEquals( randomBoolean, actual );
    }
    
    
    /**
     * Test that command can set attribute of type boolean.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetBooleanAttribute2() throws Exception
    {
		final String attributeID = "AutoRestart";

		// create server 
		jmxMother.createServer(session, randomServerName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a server
		ResolvedType parent = jmxMother.createServerObjName_ResolvedType(session, randomServerName);
		
		// create resolved type
		ObjectName serverObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createBooleanResolvedParticipant(attributeID, randomBoolean);
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, serverObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		boolean actual = (Boolean) jmxMother.getAttributeByXPath(session, serverObjName, "/"+attributeID);        
        assertEquals( randomBoolean, actual );    	
    }

    
    /**
     * Test that command can set attribute of type boolean
     * from a string value.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetBooleanAttributeFromString() throws Exception
    {
		final String attributeID = "AutoRestart";

		// create server 
		jmxMother.createServer(session, randomServerName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a server
		ResolvedType parent = jmxMother.createServerObjName_ResolvedType(session, randomServerName);
		
		// create resolved type
		ObjectName serverObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, Boolean.toString(randomBoolean));
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, serverObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		boolean actual = (Boolean) jmxMother.getAttributeByXPath(session, serverObjName, "/"+attributeID);        
        assertEquals( randomBoolean, actual );    	    	
    }
    
    
    /**
     * Test that command can set attribute of enum type.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetEnumAttribute() throws Exception
    {
		final String attributeID = "AdministrationProtocol";
		final String expectedValue = "http";

		// create server 
		jmxMother.createServer(session, randomServerName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a server
		ResolvedType parent = jmxMother.createServerObjName_ResolvedType(session, randomServerName);
		
		// create resolved type
		ObjectName serverObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, expectedValue);
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, serverObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String actual = (String) jmxMother.getAttributeByXPath(session, serverObjName, "/"+attributeID);        
        assertEquals( expectedValue, actual );    	    	    	
    }

    
    /**
     * Test that command fails to set attribute illegal value of enum type.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test(expected=JMXManagmentException.class)
    public void testFailsToSetIllegalEnumAttribute() throws Exception
    {
		final String attributeID = "AdministrationProtocol";
		final String expectedValue = "illegal-value";

		// create server 
		jmxMother.createServer(session, randomServerName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a server
		ResolvedType parent = jmxMother.createServerObjName_ResolvedType(session, randomServerName);
		
		// create resolved type
		ObjectName serverObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, expectedValue);
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, serverObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String actual = (String) jmxMother.getAttributeByXPath(session, serverObjName, "/"+attributeID);        
        assertEquals( expectedValue, actual );    	    	    	
    }

    
    /**
     * Test that command can set target attribute of type string array, i.e. String[]
     * where source attribute is string array, i.e. String[]
     * with a three values in the array.
     *  
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringsArrayFromStringArrayWithThreeValues() throws Exception
    {
        // create random string
        String str1 = RandomStringUtils.randomAlphabetic(16);
        String str2 = RandomStringUtils.randomAlphabetic(16);
        String str3 = RandomStringUtils.randomAlphabetic(16);        
        String[] expectedValue = new String[] { str1, str2, str3 };
    	
		final String attributeID = "VirtualHostNames";

		// create server 
		jmxMother.createVirtualHost(session, randomVirtualHostName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
		
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringArrayResolvedParticipant(attributeID, expectedValue );		
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String[] actual = (String[]) jmxMother.getAttributeByXPath(session, vhObjName, "/"+attributeID);
        assertArrayEquals(expectedValue, actual );
    }

    /**
     * Test that command can set target attribute of type string array, i.e. String[]
     * where source attribute is string array, i.e. String[]
     * with a single value in the array.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringsArrayFromStringArrayWithOneValue() throws Exception
    {
        // create random string
        String str1 = RandomStringUtils.randomAlphabetic(16);        
        String[] expectedValue = new String[] { str1 };
    	
		final String attributeID = "VirtualHostNames";
		
		// create server 
		jmxMother.createVirtualHost(session, randomVirtualHostName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
		
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringArrayResolvedParticipant(attributeID, expectedValue );		
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String[] actual = (String[]) jmxMother.getAttributeByXPath(session, vhObjName, "/"+attributeID);
        assertArrayEquals(expectedValue, actual );   	
    }

    /**
     * 
     * Test that command can set target attribute of type string array, i.e. String[]
     * where source attribute is string array, i.e. String[]
     * with no values in the array.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringsArrayFromStringArrayWithZeroValues() throws Exception
    {
        // create random string
        String[] expectedValue = new String[] { };
    	
		final String attributeID = "VirtualHostNames";
		
		// create server 
		jmxMother.createVirtualHost(session, randomVirtualHostName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
		
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringArrayResolvedParticipant(attributeID, expectedValue );		
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String[] actual = (String[]) jmxMother.getAttributeByXPath(session, vhObjName, "/"+attributeID);
        assertArrayEquals(expectedValue, actual );
    }    

    /**
     * Test that command can set target attribute of type string array, i.e. String[]
     * where source attribute is string i.e. String
     * with a single value in the array.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringsArrayFromStringWithOneValue() throws Exception
    {
        // create random string
        String str1 = RandomStringUtils.randomAlphabetic(16);        
        String[] value = new String[] { str1 };        
        String valueAsStr = str1;
    	
		final String attributeID = "VirtualHostNames";
		
		// create server 
		jmxMother.createVirtualHost(session, randomVirtualHostName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
		
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, valueAsStr );		
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String[] actual = (String[]) jmxMother.getAttributeByXPath(session, vhObjName, "/"+attributeID);
        assertArrayEquals(value, actual );    	
    }

    /**
     * Test that command can set target attribute of type string array, i.e. String[]
     * where source attribute is string i.e. String
     * with a three values in the array.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetStringsArrayFromStringWithThreeValues() throws Exception
    {
        // create random string
        String str1 = RandomStringUtils.randomAlphabetic(16);
        String str2 = RandomStringUtils.randomAlphabetic(16);
        String str3 = RandomStringUtils.randomAlphabetic(16);
        
        String[] value = new String[] { str1, str2, str3 };
        String valueAsStr = str1 + "," + str2 + "," + str3 + ",";        
    	
		final String attributeID = "VirtualHostNames";
		
		// create server 
		jmxMother.createVirtualHost(session, randomVirtualHostName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
		
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, valueAsStr );		
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		String[] actual = (String[]) jmxMother.getAttributeByXPath(session, vhObjName, "/"+attributeID);
        assertArrayEquals(value, actual );    	    
    }
    
    
    /**
     * Test that command can set attribute of type integer.
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetIntegerAttribute() throws Exception
    {
		final String attributeID = "MaxPostSize";

		// create server 
		jmxMother.createVirtualHost(session, randomVirtualHostName);
		
		// start edit session
		jmxMother.startEdit(session);		
		
		// create parent, which is a virtual host 
		ResolvedType parent = jmxMother.createVirtualHostObjName_ResolvedType(session, randomVirtualHostName);
					
		// create resolved type
		ObjectName vhObjName = (ObjectName) parent.getSecondaryParticiant().getValue();		
		ResolvedParticipant primary = jmxMother.createIntResolvedParticipant("MaxPostSize", randomInteger );
		ResolvedParticipant secondary = jmxMother.createUnamedObjName_ResolvedParticipant(session, attributeID, vhObjName ); 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute   
		int value = (Integer) jmxMother.getAttributeByXPath(session, vhObjName, "/"+attributeID);
		
		// test
        assertEquals( randomInteger, value );    	    	
    }

    
    /**
     * Test that command can set attribute of type object name for Server.Machine.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetServerMachineObjectNameAttribute() throws Exception
    {    	
    	final String attributeID = "Machine";

		// create server 
    	ObjectName objName = jmxMother.createServer(session, randomServerName);
    	
		// create machine 
		jmxMother.createMachine(session, randomMachineName);
		
		// start edit session
		jmxMother.startEdit(session);		
    	
        // create parent, which is a server
		ResolvedType parent = jmxMother.createServerObjName_ResolvedType(session, randomServerName);
		
		// create resolved type
		ObjectName serverObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), serverObjName, attributeID);		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomMachineName);
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(attributeID, attributeInfo);				 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        session.saveAndActivate();
        
		// get attribute   
        ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, objName, "/"+attributeID);
        
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        assertEquals( randomMachineName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( attributeID, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        
    }
        

    /**
     * Test that command can set attribute of type object name for WLDF system resource.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetWldfSystemResourceTargetObjectNameAttribute() throws Exception
    {    	
    	final String attributeID = "Targets";
    	
    	// create WLDF system resource
    	ObjectName objName = jmxMother.createWldfSystemResource(session, randomWldfName); 

		// create server 
		jmxMother.createServer(session, randomServerName);
    	
		// start edit session
		jmxMother.startEdit(session);		
    	
        // create parent, which is a WLDF system resource
		ResolvedType parent = jmxMother.createWldfSystemResourceObjName_ResolvedType(session, randomWldfName);
		
		// create resolved type
		ObjectName wldfSystemResourceObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), wldfSystemResourceObjName, attributeID);		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomServerName );
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(attributeID, attributeInfo);				 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                        
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );
		
		// get attribute   
        ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, objName, "/Targets");
                
        // test
        assertEquals( randomServerName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( "Server", value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        		
    }
    
    /**
     * Test that command fails set attribute of type object name for WLDF Watch Notification
     * since the field is read-only
     * 
     * @throws Exception If test fails.
     */
    /**
    @SuppressWarnings("unchecked")
	@Test(expected=ModelResolutionFailedException.class)
    public void testFailsToSetReadOnlyWldfSnmpNotificationObjectNameAttribute() throws Exception
    {
    	final String attributeID = "SNMPNotifications";

    	// create WLDF system resource
    	jmxMother.createWLDFSystemResource(session, randomWldfName); 

    	// create WLDF watch notification to be "targeted"
    	jmxMother.createWldfWatchNotificationObjName(session, randomWldfName, randomWldfNotificationName);
    	
    	// create WLDF watch
    	//wldfWatch = jmxMother.createWLDFWatchType(session, wldfSystemResource.getName(), randomWldfWatchName );    	
    	
		// start edit session
		jmxMother.startEdit(session);		
    	
        // create parent, which is a WLDF watch type	
		ResolvedType parent = jmxMother.createWldfWatchObjName_ResolvedType(session, randomWldfName);
    	
		// create resolved type, which defines "targeting" of the watch notification to the watch		
		ObjectName wldfWatchObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), wldfWatchObjName, attributeID );
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomServerName );
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(randomWldfNotificationName, attributeInfo );		
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        

        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command to trigger exception
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);

        // test
		//assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		//assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		//assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        
		// get attribute 
		//String[] actual = (String[]) jmxMother.getAttributeByXPath(session, wldfWatchObjName, "/"+attributeID);
        //assertEquals(1, actual.length );        
    }
    **/

    /**
     * Test that command can set attribute of type object name for Machine.NodeManager.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetMachineNodeManagerObjectNameAttribute() throws Exception
    {
    	
    	fail();
    	
    	final String attributeID = "Machine";

		// create server 
    	ObjectName objName = jmxMother.createServer(session, randomServerName);
    	
		// create machine 
		jmxMother.createMachine(session, randomMachineName);
		
		// start edit session
		jmxMother.startEdit(session);		
    	
        // create parent, which is a server
		ResolvedType parent = jmxMother.createServerObjName_ResolvedType(session, randomServerName);
		
		// create resolved type
		ObjectName serverObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), serverObjName, attributeID);		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomMachineName);
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(attributeID, attributeInfo);				 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        session.saveAndActivate();
        
		// get attribute   
        ObjectName value = (ObjectName) jmxMother.getAttributeByXPath(session, objName, "/"+attributeID);
        
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		
        assertEquals( randomMachineName, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_NAME_KEYPROPERTY));        
        assertEquals( attributeID, value.getKeyProperty(WebLogicMBeanConstants.OBJNAME_TYPE_KEYPROPERTY));        
    }

    /**
     * Test that command can fails set WLDFResource.Name attribute with different after creation
     * because it isn't writable.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsToSetWldfResourceNameNameAttributeWithDifferentValue() throws Exception
    {    	
    	final String attributeID = "Name";
    	
    	// create WLDF System resource 
    	ObjectName wldfSystemResourceObjName = jmxMother.createWldfSystemResource(session, randomWldfName);
    	    	
		// start edit session
		jmxMother.startEdit(session);		
    	
        // create parent, which is a WLDF resource
		ResolvedType parent = jmxMother.createWldfResourceObjName_ResolvedType(session, randomWldfName);
		
		// create resolved type
		ObjectName wldfResourceObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), wldfSystemResourceObjName, attributeID);		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomName);
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(attributeID, attributeInfo);				 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
        
		assertEquals(ExecutionState.FAILURE, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );		        
    }

    /**
     * Test that command can fails set WLDFResource.Name attribute with different after creation
     * because it isn't writable.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSetWldfResourceNameNameAttributeWithSameValue() throws Exception
    {
    	final String attributeID = "Name";
    	
    	// create WLDF System resource 
    	ObjectName wldfSystemResourceObjName = jmxMother.createWldfSystemResource(session, randomWldfName);
    	    	
		// start edit session
		jmxMother.startEdit(session);		
    	
        // create parent, which is a WLDF resource
		ResolvedType parent = jmxMother.createWldfResourceObjName_ResolvedType(session, randomWldfName);
		
		// create resolved type
		ObjectName wldfResourceObjName = (ObjectName) parent.getSecondaryParticiant().getValue();
    	MBeanAttributeInfo attributeInfo = jmxMother.findAttributeInfo(session.getMBeanServerConnection(), wldfSystemResourceObjName, attributeID);		
		ResolvedParticipant primary = jmxMother.createStringResolvedParticipant(attributeID, randomWldfName);
		ResolvedParticipant secondary = jmxMother.createFailedNamedObjName_ResolvedParticipant(attributeID, attributeInfo);				 		  					
		resolvedType = ResolvedTypeImpl.createResolvedType(parent, primary, secondary);        
    	
        // add command parameters to context
        context.put( SetMBeanAttributeCommand.JMXSESSION_KEY, session );
        context.put( SetMBeanAttributeCommand.RESOLVED_TYPE, resolvedType );
        context.put( SetMBeanAttributeCommand.EXECUTIONRESULT_KEY, executionResult);

        // execute command
        setMBeanAttributeCommand.execute( context );

        // save and activate
        jmxMother.saveAndActivate(session);
                        
        // test
		assertEquals(ExecutionState.SUCCESS, executionResult.getState());
		assertTrue(resolvedType.getSecondaryParticiant().isResolutionSuccesful() );	
		assertEquals(resolvedType.getSecondaryParticiant().getValueState(), ResolvedParticipant.ValueState.SET );
		
		// get attribute   
        String value = (String) jmxMother.getAttributeByXPath(session, wldfResourceObjName, "/Name");
                
        // test
        assertEquals( randomWldfName, value);                		
    }
    
}
