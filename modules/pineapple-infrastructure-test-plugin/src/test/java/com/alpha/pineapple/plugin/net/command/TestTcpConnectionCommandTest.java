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


package com.alpha.pineapple.plugin.net.command;

import static org.junit.Assert.fail;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
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

/**
 * Unit test of the class {@link TestTcpConnectionCommand}.
 */
public class TestTcpConnectionCommandTest
{

    /**
     * Command under test.
     */
    Command command;

    /**
     * Chain context.
     */
    Context context;
    
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
        // create command
        command = new TestTcpConnectionCommand();

        // create context
        context = new ContextBase();
        
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
        
        EasyMock.replay(messageProvider);                
    }

    @After
    public void tearDown() throws Exception
    {
        command = null;
        context = null;
        executionResult = null;
        messageProvider = null;
    }

	/**
	 * Test that undefined execution result is rejected.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testRejectsUndefinedExecutionResult() throws Exception {
				
		// setup parameters
		context.put(TestTcpConnectionCommand.EXECUTIONRESULT_KEY, null );
		context.put(TestTcpConnectionCommand.HOST_KEY, "tiamat");		
		context.put(TestTcpConnectionCommand.PORTS_KEY, new int[]{1});		
		
		// execute command
		command.execute(context);

		// test
		fail("Test should never reach here.");
	}	    
	
    /**
     * Test that command fails if host is undefined in context.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	@Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfHostIsUndefinedInContext() throws Exception
    {        
        // setup parameters
		context.put(TestTcpConnectionCommand.EXECUTIONRESULT_KEY, executionResult );            
		context.put(TestTcpConnectionCommand.PORTS_KEY, new int[]{1});        

        // execute command
        command.execute( context );
        
        fail( "Test should never reach here." );
    }

    /**
     * Test that command fails if port is undefined in context.
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
	@Test( expected = CommandInitializationFailedException.class )
    public void testCommandFailsIfPortIsUndefinedInContext() throws Exception
    {        
        // setup parameters
		context.put(TestTcpConnectionCommand.EXECUTIONRESULT_KEY, executionResult );            
		context.put(TestTcpConnectionCommand.HOST_KEY, "tiamat");

        // execute command
        command.execute( context );
        
        fail( "Test should never reach here." );
    }

}
