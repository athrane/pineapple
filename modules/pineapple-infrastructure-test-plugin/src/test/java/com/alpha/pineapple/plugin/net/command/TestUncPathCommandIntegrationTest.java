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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;

/**
 * integration test of the class {@link TestUncPathCommand}.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.net-config.xml" } )
public class TestUncPathCommandIntegrationTest
{

    /**
     * Chain context.
     */
    Context context;
    
    /**
     * Command under test.
     */
    @Resource( name="testUncPathCommand")	
    Command command;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;    
        
    @Before
    public void setUp() throws Exception
    {
        // create context
        context = new ContextBase();
        
        // create execution result
		executionResult = new ExecutionResultImpl("Root result" );
        
    }

    @After
    public void tearDown() throws Exception
    {
        command = null;
        context = null;
        executionResult = null;
    }

    /**
     * Test that command signal test-error of the name can't resolve to 
     * expected IP address.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testTestCaseFailsIfShareCantBeAccessed()
    {

        try
        {
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test UNC path.");        	
        	
            // setup parameters
    		context.put( TestUncPathCommand.EXECUTIONRESULT_KEY, childResult );        	        	
            context.put( TestUncPathCommand.HOSTNAME_KEY, "wintermute" );            
            context.put( TestUncPathCommand.SHARE_KEY, "non-existing-share-name" );            

            // execute command
            command.execute( context );

            // test
            assertEquals( ExecutionState.FAILURE, childResult.getState());
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }
    
}
