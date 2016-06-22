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

import static org.junit.Assert.fail;

import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipantImpl;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;

public class TestCollectionValueCommandTest
{
	static final ResolvedType NULL_PARENT = null;
	
    /**
     * Context.
     */
    Context context;
    
    /**
     * Resolved type 
     */
    ResolvedType resolvedType;

    /**
     * Object under test.
     */
    TestCollectionValueCommand command;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult; 
    
    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;
    
    @Before
    public void setUp() throws Exception
    {
        // create context
        context = new ContextBase();

        // create command
        command = new TestCollectionValueCommand();

        // create mock result
        executionResult = EasyMock.createMock( ExecutionResult.class );

        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
                
        // inject message source
        ReflectionTestUtils.setField( command, "messageProvider", messageProvider, MessageProvider.class );
        
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
        resolvedType = null;
        context = null;
        messageProvider = null;
        executionResult = null;        
    }

    /**
     * Test that the command fails of the resolved type isn't an resolved collection.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfResolvedTypeIsntAnCollection()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
					 (String) EasyMock.isA( String.class ), 
					 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type            
            ResolvedParticipant prp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, new String[0]);
            ResolvedParticipant srp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, new String[0]);        	
            resolvedType =  ResolvedTypeImpl.createResolvedType(NULL_PARENT, prp, srp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }
        
    /**
     * Test that two identical arrays are compared correct.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfullyComparesTwoEmptyArrays()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsComputed((MessageProvider) EasyMock.isA( MessageProvider.class ),
        										 (String) EasyMock.isA( String.class ),
        										 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type            
            ResolvedParticipant prp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, new String[0]);
            ResolvedParticipant srp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, new String[0]);        	
            resolvedType =  ResolvedTypeImpl.createResolvedCollection(NULL_PARENT, prp, srp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }

    /**
     * Test that two identical array are compared correct.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfullyComparesTwoArraysWithOneEntry()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsComputed((MessageProvider) EasyMock.isA( MessageProvider.class ),
        										 (String) EasyMock.isA( String.class ),
        										 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type
            String[] pp = new String[] { "nobi nobi boy"};
            String[] sp = new String[] { "nobi nobi boy"};
            ResolvedParticipant prp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, pp);
            ResolvedParticipant srp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, sp);        	
            resolvedType =  ResolvedTypeImpl.createResolvedCollection(NULL_PARENT, prp, srp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );        
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }
    
    /**
     * Test that two identical array are compared correct.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfullyComparesTwoArraysWithTwoEntries()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsComputed((MessageProvider) EasyMock.isA( MessageProvider.class ),
        										 (String) EasyMock.isA( String.class ),
        										 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type
            String[] pp = new String[] { "nobi nobi boy", "carp"};
            String[] sp = new String[] { "nobi nobi boy", "carp"};     
            ResolvedParticipant prp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, pp);
            ResolvedParticipant srp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, sp);        	
            resolvedType =  ResolvedTypeImpl.createResolvedCollection(NULL_PARENT, prp, srp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );        
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }    
    /**
     * Test that two null strings are compared correct.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccessfullyComparesTwoNullArrays()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsSuccessful((MessageProvider) EasyMock.isA( MessageProvider.class ),
        										 (String) EasyMock.isA( String.class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type
            ResolvedType parent = null;
            ResolvedParticipant prp = ResolvedParticipantImpl.createSuccessfulResult( "array", String[].class, null );
            ResolvedParticipant srp = ResolvedParticipantImpl.createSuccessfulResult( "array", String[].class, null );
            resolvedType =  ResolvedTypeImpl.createResolvedCollection(NULL_PARENT, prp, srp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            
            
            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }
    
    
    /**
     * Test that different arrays are compared correct.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testSuccedsIfArrayContentAreDifferent()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsComputed((MessageProvider) EasyMock.isA( MessageProvider.class ),
        										 (String) EasyMock.isA( String.class ),
        										 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type
            String[] pp = new String[] { "nobi nobi boy", "carp"};
            String[] sp = new String[] { "nobi nobi boy2", "carp2"};
            ResolvedParticipant prp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, pp);
            ResolvedParticipant srp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, sp);        	
            resolvedType =  ResolvedTypeImpl.createResolvedCollection(NULL_PARENT, prp, srp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );        
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }

    /**
     * Test that the test fails if value of primary participant is 
     * null and the value of secondary isn't.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfPrimaryParticipantValueIsNull()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
					 (String) EasyMock.isA( String.class ), 
					 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type
            ResolvedType parent = null;
            String[] spValue = new String[] { "nobi nobi boy2", "carp2"};
            ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, null );
            ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, spValue );
            resolvedType =  ResolvedTypeImpl.createResolvedType( parent, pp, sp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );        
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }    

    
    /**
     * Test that the test fails if value of secondary participant is 
     * null and the value of primary isn't.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfSecondaryParticipantValueIsNull()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
					 (String) EasyMock.isA( String.class ), 
					 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
        	
            // initialize resolved type
            ResolvedType parent = null;
            String[] ppValue = new String[] { "nobi nobi boy", "carp"};            
            ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, ppValue );
            ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, null );
            resolvedType =  ResolvedTypeImpl.createResolvedType( parent, pp, sp );
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );        
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }    
    

    /**
     * Test that the test fails if the primary participant wasn't resolved successfully.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfPrimaryParticipantWasntSuccessfullyResolved()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
					 (String) EasyMock.isA( String.class ), 
					 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
            
            // initialize resolved type
            Exception exception = new Exception("unsuccesfully resolution exception");
            String[] spValue = new String[] { "nobi nobi boy2", "carp2"};            
            ResolvedParticipant pp = ResolvedParticipantImpl.createUnsuccessfulResult( "attr", null, null, exception );
            ResolvedParticipant sp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, spValue );
            resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );        
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            

            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }

    /**
     * Test that the test fails if the secondary participant wasn't resolved successfully.
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testFailsIfSecondaryParticipantWasntSuccessfullyResolved()
    {
        try
        {
    		// complete execution result initialization
        	executionResult.completeAsFailure((MessageProvider) EasyMock.isA( MessageProvider.class ),
					 (String) EasyMock.isA( String.class ), 
					 (Object[]) EasyMock.isA( Object[].class ));
    		EasyMock.replay( executionResult );		
            
            // initialize resolved type
            Exception exception = new Exception("unsuccesfully resolution exception");
            String[] ppValue = new String[] { "nobi nobi boy", "carp"};            
            ResolvedParticipant pp = ResolvedParticipantImpl.createSuccessfulResult( "attr", String[].class, ppValue );
            ResolvedParticipant sp = ResolvedParticipantImpl.createUnsuccessfulResult( "attr", null, null, exception );            
            resolvedType =  ResolvedTypeImpl.createResolvedType( null, pp, sp);
            
            // initialize context
            context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );
    		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );            
            
            // execute command             
            command.execute(context);
            
    		// verify mocks
    		EasyMock.verify(executionResult);		
        }
        catch ( Exception e )
        {
            fail (StackTraceHelper.getStrackTrace( e ));
        }
    }
    
	/**
	 * Test that undefined execution result is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedExecutionResult() throws Exception {
				
		// setup parameters
        context.put(TestCollectionValueCommand.RESOLVED_TYPE, resolvedType );
		
		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}	

	/**
	 * Test that undefined resolved type is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedResolvedType() throws Exception {
				
		// setup parameters
		context.put(TestCollectionValueCommand.EXECUTIONRESULT_KEY, executionResult );
		
		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}	
    
}
