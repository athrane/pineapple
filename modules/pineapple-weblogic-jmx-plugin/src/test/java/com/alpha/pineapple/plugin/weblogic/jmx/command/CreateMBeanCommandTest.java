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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.weblogic.jmx.model.mbeans.ResolvedModelJmxGetter;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant.ValueState;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;

/**
 * Unit test of the {@link CreateMBeanCommand} class. 
 */
public class CreateMBeanCommandTest
{

	/**
	 * MBean object name string used in tests.
	 */
	static final String DOMAIN_OJBNAME_STR = "com.bea:Name=localdomain,Type=Domain";
	
	/**
	 * MBean type used in tests.
	 */
	static final String SERVER_TYPE = "Server";

	/**
	 * MBean id used in tests
	 */
	static final String ADMSERVER_ID = "admserver";
	
    /**
     * object under test
     */
    Command command;
    
	/**
	 * Context.
	 */
	Context context;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;    

    /**
     * Mock model JMX access helper 
     */
    ResolvedModelJmxGetter modelJmxGetter;    
    
	/**
	 * Mock JMX session.
	 */
    WeblogicJMXEditSession jmxSession;

	/**
	 * Mock resolved type.
	 */
	ResolvedType resolvedType;

	/**
	 * Mock execution result.
	 */	
	ExecutionResult executionResult;
			
    /**
     * Random name.
     */
	String randomName;
	
    /**
     * Random type.
     */
	String randomName2;
    
    /**
     * Random type.
     */
	String randomType;
	
    @Before
    public void setUp() throws Exception    
    {
		// create random names
		randomName = RandomStringUtils.randomAlphabetic(10);			
		randomName2= RandomStringUtils.randomAlphabetic(10);	
		randomType = RandomStringUtils.randomAlphabetic(10);		
    	
    	// create command
		command = new CreateMBeanCommand();

		// create context
		context = new ContextBase();

        // create mock session      
        jmxSession = EasyMock.createMock( WeblogicJMXEditSession.class );        

        // create execution result
        executionResult = EasyMock.createMock( ExecutionResult.class );

        // create resolved type 
        resolvedType = EasyMock.createMock( ResolvedType.class );

        // create mock getter
        modelJmxGetter = EasyMock.createMock( ResolvedModelJmxGetter.class );
        
        // inject getter
        ReflectionTestUtils.setField( command, "modelJmxGetter", modelJmxGetter );
                
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
        
        // inject message source
        ReflectionTestUtils.setField( command, "messageProvider", messageProvider , MessageProvider.class );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 

        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class )));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                        
    }

    @After
    public void tearDown() throws Exception
    {
    	command = null;
		context = null;
		messageProvider = null;
		jmxSession = null;
		resolvedType = null;
		executionResult = null;
    }

	/**
	 * Test that command fails if session is undefined in context.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedSession() throws Exception {

		// complete mock resolved type setup
		EasyMock.replay(resolvedType); 

		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, null );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType);
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}

	/**
	 * Test that command fails if resolved type is undefined in context.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedResolvedType() throws Exception {

		// complete mock session setup
		EasyMock.replay(jmxSession); 

		// complete mock execution result setup
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, null );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}
    
	/**
	 * Test that command fails if execution result is undefined in context.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedExecutionResult() throws Exception {

		// complete mock session setup
		EasyMock.replay(jmxSession); 

		// complete mock resolved type setup
		EasyMock.replay(resolvedType); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, null );

		// execute command to trigger exception
		command.execute(context);

		fail("Test should never reach here.");
	}
	
    /**
     * Test that command is successful if:
     * 1) Secondary participant value state is set.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCommandSucceedsIfMBeanAlreadyExists() throws Exception
    {
		final String SECONDARY_STR_VALUE = "secondary-value-as-string";
		
		// complete mock session setup
		jmxSession.validateIsObjectName(EasyMock.anyObject());		
		EasyMock.replay(jmxSession); 
		
		// complete mock object name set
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);
		
		// complete mock getter setup
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValue()).andReturn(objName);		
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.SET);
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn(SECONDARY_STR_VALUE);
		EasyMock.replay(secondary);
		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary);
		EasyMock.replay(resolvedType); 

		// complete mock execution result setup
		executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command 
		command.execute(context);    
		
		// Verify mocks		
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);
		verify(objName);
		EasyMock.verify(modelJmxGetter);
    }

    /**
     * Test that if command is successful because:
     * 1) Secondary participant value state is set.
     * 
     * e.g. the MBean already exists,
     * then the secondary participant value state isn't modified.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSecondaryParticipantIsntUpdatedIfMBeanAlreadyExists() throws Exception
    {
		final String SECONDARY_STR_VALUE = "secondary-value-as-string";
		
		// complete mock session setup
		jmxSession.validateIsObjectName(EasyMock.anyObject());		
		EasyMock.replay(jmxSession); 
		
		// complete mock object name
		ObjectName objName = createMock( ObjectName.class);
		replay(objName);
		
		// complete mock getter setup
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValue()).andReturn(objName).times(2);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.SET);
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn(SECONDARY_STR_VALUE);
		EasyMock.replay(secondary);
		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary).times(3);
		EasyMock.replay(resolvedType); 

		// complete mock execution result setup
		executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );
				
		// execute command 
		command.execute(context);
		
		// test
		ResolvedType actualResolvedType = (ResolvedType) context.get(CreateMBeanCommand.RESOLVED_TYPE);
		assertEquals(secondary, actualResolvedType.getSecondaryParticiant());
		assertEquals(objName, actualResolvedType.getSecondaryParticiant().getValue());		
		
		// Verify mocks		
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);
		verify(objName);
		EasyMock.verify(modelJmxGetter);
    }
    
    
    /**
     * Test that command is fails if: 
     * 1) Secondary participant value state is failed (otherwise the test would have succeeded).
     * 2) Secondary participant name isn't set.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCommandFailsIfSecondaryNameIsUndefined() throws Exception
    {				
		// complete mock session setup
		EasyMock.replay(jmxSession); 

		// complete mock getter setup
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.expect(secondary.getName()).andReturn( null);		
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn( randomName );		
		EasyMock.replay(secondary);

		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary).times(2);
		EasyMock.replay(resolvedType); 

		// complete mock execution result setup
		executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command to trigger exception
		command.execute(context);
		
		// Verify mocks
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);
		EasyMock.verify(modelJmxGetter);
    }    	
    
    /**
     * Test that command is fails if: 
     * 1) Secondary participant value state is failed (otherwise the test would have succeeded).
     * 2) Secondary participant name is set (must be set otherwise the test fails). 
     * 3) Secondary type is undefined.
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCommandFailsIfSecondaryTypeIsUndefined() throws Exception
    {
    	final String SECONDARY_NAME_VALUE = "secondary-name";    	
		final Object SECONDARY_TYPE_VALUE = null;
		
		// complete mock session setup
		EasyMock.replay(jmxSession); 

		// complete mock getter setup
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.expect(secondary.getName()).andReturn(SECONDARY_NAME_VALUE).times(2);
		EasyMock.expect(secondary.getType()).andReturn(SECONDARY_TYPE_VALUE);
		EasyMock.replay(secondary);
		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary).times(3);
		EasyMock.replay(resolvedType); 

		// complete mock execution result setup
		executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command to trigger exception
		command.execute(context);
		
		// Verify mocks
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);
		EasyMock.verify(modelJmxGetter);	
		
    }    	

    /**
     * Test that command is fails if: 
     * 1) Secondary participant value state is failed (otherwise the test would have succeeded).
     * 2) Primary participant name is set (must be set otherwise the test fails). 
     * 3) Secondary type is defined (must be defined otherwise the test fails).
     * 4) Parent is undefined. 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCommandFailsIfParentIsUndefined() throws Exception
    {
    	final String SECONDARY_NAME_VALUE = "secondary-name";    	
    	final String SECONDARY_STR_VALUE = "secondary-value-as-string";
		final Object SECONDARY_TYPE_VALUE = String.class;
				
		// complete mock session setup
		EasyMock.replay(jmxSession); 
	
		// complete mock getter setup
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.expect(secondary.getName()).andReturn(SECONDARY_NAME_VALUE);
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn(SECONDARY_STR_VALUE);
		EasyMock.expect(secondary.getType()).andReturn(SECONDARY_TYPE_VALUE);
		EasyMock.replay(secondary);
		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary).times(4);
		EasyMock.expect(resolvedType.getParent()).andReturn( null );
		EasyMock.replay(resolvedType); 

		// complete mock execution result setup
		executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command to trigger exception
		command.execute(context);
		
		// Verify mocks
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);				
		EasyMock.verify(modelJmxGetter);		
    }    	
    

    /**
     * Test that command is fails if: 
     * 1) Secondary participant value state is failed (otherwise the test would have succeeded).
     * 2) Primary participant name is set (must be set otherwise the test fails). 
     * 3) Secondary type is defined (must be defined otherwise the test fails).
     * 4) Parent is defined (must be defined otherwise the test fails). 
     * 5) Parent secondary value state isn't set. 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCommandFailsIfParentSecondaryValueIsntSet() throws Exception
    {
    	final String SECONDARY_NAME_VALUE = "secondary-name";    	
    	final String SECONDARY_STR_VALUE = "secondary-value-as-string";
		final Object SECONDARY_TYPE_VALUE = String.class;
		
		
		// complete mock session setup
		EasyMock.replay(jmxSession); 
		
		// complete mock getter setup
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.expect(secondary.getName()).andReturn(SECONDARY_NAME_VALUE);
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn(SECONDARY_STR_VALUE);
		EasyMock.expect(secondary.getType()).andReturn(SECONDARY_TYPE_VALUE);
		EasyMock.replay(secondary);

		// complete mock parent secondary participant setup
		ResolvedParticipant parentSecondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(parentSecondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.replay(parentSecondary);
		
		// complete mock parent setup
		ResolvedType parent	= EasyMock.createMock( ResolvedType.class);
		EasyMock.expect(parent.getSecondaryParticiant()).andReturn(parentSecondary);
		EasyMock.replay(parent);
		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary).times(4);
		EasyMock.expect(resolvedType.getParent()).andReturn( parent ).times(3);
		EasyMock.replay(resolvedType); 

		// complete mock execution result setup
		executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command to trigger exception
		command.execute(context);
		
		// Verify mocks
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);				
		EasyMock.verify(parent);
		EasyMock.verify(parentSecondary);
		EasyMock.verify(modelJmxGetter);			
    }    	

    /**
     * Test that command is fails if: 
     * 1) Secondary participant value state is failed (otherwise the test would have succeeded).
     * 2) Primary participant name is set (must be set otherwise the test fails). 
     * 3) Secondary type is defined (must be defined otherwise the test fails).
     * 4) Parent is defined (must be defined otherwise the test fails). 
     * 5) Parent secondary value state is set (must be set otherwise the test fails).  
     * 6) Parent MBean doesn't contain MBean factory method. 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testCommandFailsIfParentMBeanDoesntContainFactoryMethod() throws Exception
    {		
		// complete mock object name setup
		ObjectName parentObjectName = createMock( ObjectName.class);
		replay(parentObjectName);
		
		// complete mock object name setup
		ObjectName resultObjectName = createMock( ObjectName.class);
		replay(resultObjectName);

		// complete mock operation info setup
		MBeanOperationInfo operationInfo = createMock( MBeanOperationInfo.class);
		replay(operationInfo );

		// complete mock attribute info setup
		MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class);
		expect(attributeInfo.getName()).andReturn(randomType);
		replay(attributeInfo);
		
		// complete mock session setup
		jmxSession.validateIsObjectName(parentObjectName);
		EasyMock.expect(jmxSession.getCreateMethod(parentObjectName, randomType)).andReturn(null);
		EasyMock.replay(jmxSession); 

		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.expect(secondary.getName()).andReturn(randomName ).times(2);
		EasyMock.expect(secondary.getType()).andReturn(attributeInfo).times(2);
		EasyMock.expect(secondary.getValueAsSingleLineString()).andReturn(randomName2);
		EasyMock.replay(secondary);

		// complete mock parent secondary participant setup
		ResolvedParticipant parentSecondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(parentSecondary.getValueState()).andReturn(ValueState.SET);	
		EasyMock.replay(parentSecondary);
		
		// complete mock parent setup
		ResolvedType parent	= EasyMock.createMock( ResolvedType.class);
		EasyMock.expect(parent.getSecondaryParticiant()).andReturn(parentSecondary);
		EasyMock.replay(parent);
		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary).times(3);		
		EasyMock.expect(resolvedType.getParent()).andReturn( parent ).times(3);		
		EasyMock.replay(resolvedType); 

		// complete mock getter setup
		EasyMock.expect(modelJmxGetter.resolveParentObjectName(resolvedType)).andReturn(parentObjectName);
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock execution result setup
		executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command to trigger exception
		command.execute(context);
		
		// Verify mocks
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);				
		EasyMock.verify(parent);
		EasyMock.verify(parentSecondary);
		EasyMock.verify(modelJmxGetter);
		verify(attributeInfo);
    }    	
    
    

    /**
     * Test execution flow for successful execution of the command.
     *  
     * Requirements for successful execution: 
     * 1) Secondary participant value state is failed (otherwise the test would have succeeded).
     * 2) Primary participant name is set (must be set otherwise the test fails). 
     * 3) Secondary type is defined (must be defined otherwise the test fails). 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfulExecution() throws Exception
    {    	    	
		// complete mock object name setup
		ObjectName parentObjectName = createMock( ObjectName.class);
		replay(parentObjectName);
		
		// complete mock object name setup
		ObjectName resultObjectName = createMock( ObjectName.class);
		replay(resultObjectName);

		// complete mock operation info setup
		MBeanOperationInfo operationInfo = createMock( MBeanOperationInfo.class);
		replay(operationInfo );
		
		// complete mock attribute info setup
		MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class);
		expect(attributeInfo.getName()).andReturn(randomType);
		replay(attributeInfo);
						
		// complete mock session setup
		jmxSession.validateIsObjectName(parentObjectName);
		EasyMock.expect(jmxSession.getCreateMethod(parentObjectName, randomType)).andReturn(operationInfo);
		EasyMock.expect(jmxSession.invokeMethod(
				EasyMock.eq(parentObjectName), 
				EasyMock.eq(operationInfo), 
				EasyMock.aryEq(new Object[] { randomName } )))
			.andReturn(resultObjectName);			
		EasyMock.replay(jmxSession); 

		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.expect(secondary.getName()).andReturn(randomName ).times(3);
		EasyMock.expect(secondary.getType()).andReturn(attributeInfo).times(3);		
		EasyMock.replay(secondary);

		// complete mock parent secondary participant setup
		ResolvedParticipant parentSecondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(parentSecondary.getValueState()).andReturn(ValueState.SET);	
		EasyMock.replay(parentSecondary);
		
		// complete mock parent setup
		ResolvedType parent	= EasyMock.createMock( ResolvedType.class);
		EasyMock.expect(parent.getSecondaryParticiant()).andReturn(parentSecondary);
		EasyMock.replay(parent);
		
		// complete mock resolved type setup
		EasyMock.expect(resolvedType.getSecondaryParticiant()).andReturn(secondary).times(5);		
		EasyMock.expect(resolvedType.getParent()).andReturn( parent ).times(3);
		resolvedType.updateSecondaryParticipant(EasyMock.isA(ResolvedParticipant.class));
		EasyMock.replay(resolvedType); 

		// complete mock getter setup
		EasyMock.expect(modelJmxGetter.resolveParentObjectName(resolvedType)).andReturn(parentObjectName);
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock execution result setup
		executionResult.completeAsSuccessful(
				(MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command
		command.execute(context);
		
		// Verify mocks
		EasyMock.verify(jmxSession);
		EasyMock.verify(resolvedType);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);				
		EasyMock.verify(parent);
		EasyMock.verify(parentSecondary);
		EasyMock.verify(modelJmxGetter);
		verify(attributeInfo);		
    }    	

    /**
     * Test secondary participant is updated if execution is successful, e.g. a new MBean is created,
     *  
     * Requirements for successful execution: 
     * 1) Secondary participant value state is failed (otherwise the test would have succeeded).
     * 2) Primary participant name is set (must be set otherwise the test fails). 
     * 3) Secondary type is defined (must be defined otherwise the test fails). 
     * 
     * @throws Exception If test fails.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSecondaryParticipantIsUpdatedIfMBeanIsCreated() throws Exception
    {    	    	
		// complete mock object name setup
		ObjectName parentObjectName = createMock( ObjectName.class);
		replay(parentObjectName);
		
		// complete mock object name setup
		ObjectName resultObjectName = createMock( ObjectName.class);
		replay(resultObjectName);

		// complete mock operation info setup
		MBeanOperationInfo operationInfo = createMock( MBeanOperationInfo.class);
		replay(operationInfo );

		// complete mock attribute info setup
		MBeanAttributeInfo attributeInfo = createMock( MBeanAttributeInfo.class);
		expect(attributeInfo.getName()).andReturn(randomType);
		replay(attributeInfo);
		
		// complete mock session setup
		jmxSession.validateIsObjectName(parentObjectName);
		EasyMock.expect(jmxSession.getCreateMethod(parentObjectName, randomType)).andReturn(operationInfo);
		EasyMock.expect(jmxSession.invokeMethod(
				EasyMock.eq(parentObjectName), 
				EasyMock.eq(operationInfo), 
				EasyMock.aryEq(new Object[] { randomName } )))
			.andReturn(resultObjectName);			
		EasyMock.replay(jmxSession); 

		// complete mock resolved participant setup
		ResolvedParticipant secondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(secondary.getValueState()).andReturn(ValueState.FAILED);
		EasyMock.expect(secondary.getName()).andReturn(randomName ).times(3);
		EasyMock.expect(secondary.getType()).andReturn(attributeInfo).times(3);		
		EasyMock.replay(secondary);

		// complete mock parent secondary participant setup
		ResolvedParticipant parentSecondary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.expect(parentSecondary.getValueState()).andReturn(ValueState.SET);	
		EasyMock.replay(parentSecondary);

		// complete mock parent secondary participant setup
		ResolvedParticipant primary = EasyMock.createMock( ResolvedParticipant.class);
		EasyMock.replay(primary );
		
		// complete mock parent setup
		ResolvedType parent	= EasyMock.createMock( ResolvedType.class);
		EasyMock.expect(parent.getSecondaryParticiant()).andReturn(parentSecondary);
		EasyMock.replay(parent);
		
		// create proper resolved type due command update
		resolvedType = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);

		// complete mock getter setup
		EasyMock.expect(modelJmxGetter.resolveParentObjectName(resolvedType)).andReturn(parentObjectName);
		EasyMock.replay(modelJmxGetter); 
		
		// complete mock execution result setup
		executionResult.completeAsSuccessful(
				(MessageProvider) EasyMock.isA( MessageProvider.class ),
				(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult); 
		
		// initialize context
		context.put(CreateMBeanCommand.JMXSESSION_KEY, jmxSession );
		context.put(CreateMBeanCommand.RESOLVED_TYPE, resolvedType );
		context.put(CreateMBeanCommand.EXECUTIONRESULT_KEY, executionResult );

		// execute command
		command.execute(context);
		
		// test
		ResolvedType actualResolvedType = (ResolvedType) context.get(CreateMBeanCommand.RESOLVED_TYPE);
		assertNotNull(actualResolvedType.getSecondaryParticiant());		
		assert(secondary == actualResolvedType.getSecondaryParticiant());
		assertEquals( randomName, actualResolvedType.getSecondaryParticiant().getName() );
		assertEquals( attributeInfo, actualResolvedType.getSecondaryParticiant().getType() );
		assertEquals( ValueState.SET, actualResolvedType.getSecondaryParticiant().getValueState() );		
		assertEquals( resultObjectName, actualResolvedType.getSecondaryParticiant().getValue() );		
		
		// Verify mocks
		EasyMock.verify(jmxSession);
		EasyMock.verify(executionResult);		
		EasyMock.verify(secondary);				
		EasyMock.verify(primary);		
		EasyMock.verify(parent);
		EasyMock.verify(parentSecondary);
		EasyMock.verify(modelJmxGetter);
		verify(attributeInfo);
    }    	
    
}
