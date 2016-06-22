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


package com.alpha.pineapple.plugin.net.operation;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.net.model.Mapper;
import com.alpha.pineapple.session.Session;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Unit test of the class <code>TestOperation</code>.
 */
public class TestOperationTest {

	/**
	 * Object under test.
	 */
	TestOperation operation;
	
	/**
	 * Mock session.
	 */
	Session session;
		
	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;
	
	/**
	 * Mock command runner.
	 */
	CommandRunner runner;	

	/**
	 * Mock model mapper.
	 */
	Mapper mapper;	
	
    /**
     * Object mother for the infrastructure model.
     */
    ObjectMotherContent contentMother;

    /**
     * Mock message provider.
     */
    MessageProvider messageProvider;
    
	@Before
	public void setUp() throws Exception {
		
        // create content mother
        contentMother = new ObjectMotherContent();		
		
        // create operation
        operation = new TestOperation();
		
        // create mock session
        session = EasyMock.createMock( Session.class );
                
        // create mock command runner
        runner = EasyMock.createMock( CommandRunner.class );       
        
        // inject runner into operation
        ReflectionTestUtils.setField( operation, "commandRunner", runner, CommandRunner.class );
        
        // create mock model mapper
        mapper = EasyMock.createMock( Mapper.class );       
        
        // inject runner into operation
        ReflectionTestUtils.setField( operation, "mapper", mapper, Mapper.class );        

        // create mock result
        executionResult = EasyMock.createMock( ExecutionResult.class );
     
        // create mock provider
        messageProvider = EasyMock.createMock( MessageProvider.class );
                
        // inject message provider
        ReflectionTestUtils.setField( operation, "messageProvider", messageProvider, MessageProvider.class );
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 
        
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ),
        		(Object[]) EasyMock.isA( Object[].class ) ));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        
        EasyMock.replay(messageProvider);                
        
	}

	@After
	public void tearDown() throws Exception {
		
		operation = null;		
		contentMother = null;
		session = null;
		runner = null;
		mapper = null;
		executionResult = null;
	}

	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

		// complete execution result initialization
    	executionResult.completeAsComputed((MessageProvider) EasyMock.isA( MessageProvider.class ), 
    			(String) EasyMock.isA( String.class ), 
    			(Object[]) EasyMock.isNull(), 
    			(String) EasyMock.isA( String.class ), 
    			(Object[]) EasyMock.isNull());
		EasyMock.replay( executionResult );		
		
        // create content
        Object content = contentMother.createEmptyInfrastructure();

        // invoke operation
        operation.execute( content, session, executionResult );
        
        // test
        EasyMock.verify( executionResult );
	}

	   /**
     * Test that operation fails if the content argument doesn't contain an object of the type
     * <code>Infrastructure</code>.
     */
    @Test( expected = PluginExecutionFailedException.class )
    public void testFailsIfContentDoesntContainObjectOfInfrastructureType() throws Exception
    {
		// complete execution result initialization
		EasyMock.replay( executionResult );		
    	
        // create content
        Object content = "a-string-object";

        // invoke operation
        operation.execute( content, session, executionResult );
    }
	
    /**
     * Test that operation fails if the content is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsIfContentIsUndefined() throws Exception
    {
		// complete execution result initialization
		EasyMock.replay( executionResult );		

        // invoke operation
        operation.execute( null, session, executionResult );
    }
    
    /**
     * Test that operation fails if the session is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsIfSessionIsUndefined() throws Exception
    {
		// complete execution result initialization
		EasyMock.replay( executionResult );		

        // create content
        Object content = contentMother.createEmptyInfrastructure();

        // invoke operation
        operation.execute( content, null, executionResult );
    }

    /**
     * Test that operation fails if the execution result is null.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testFailsIfResultIsUndefined() throws Exception
    {
		// complete execution result initialization
		EasyMock.replay( executionResult );		

        // create content
        Object content = contentMother.createEmptyInfrastructure();

        // invoke operation
        operation.execute( content, session, null );
    }
    
}
