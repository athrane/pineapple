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
import org.eclipse.jetty.server.Server;
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
import com.alpha.testutils.ObjectMotherHttpServer;

/**
 * Integration test of the class {@link TestTcpConnectionCommand}.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.net-config.xml" } )
public class TestTcpConnectionCommandIntegrationTest
{
	/**
	 * TCP port for  HTTP server.
	 */
    static final int HTTP_PORT = 3000;

	/**
	 * TCP ports for  HTTP server.
	 */
    static final int[] HTTP_PORTS = new int[] { HTTP_PORT };
    
    
    /**
     * Command under test.
     */
    @Resource( name="testTcpConnectionCommand")	
    Command command;

    /**
     * Chain context.
     */
    Context context;

    /**
     * HTTP server object mother 
     */
    ObjectMotherHttpServer httpServerMother;

    /**
     * HTTP server. 
     */
    Server httpServer;
        
	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;    
    
    @Before
    public void setUp() throws Exception
    {
        // create context
        context = new ContextBase();
        
        // create HTTP server object mother
        httpServerMother = new ObjectMotherHttpServer();

        // create HTTP server
        httpServer = httpServerMother.createHttpServer(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        
        // create execution result
		executionResult = new ExecutionResultImpl("Root result" );
    }

    @After
    public void tearDown() throws Exception
    {
        command = null;
        context = null;
        httpServer.stop();
        httpServer = null;
        httpServerMother = null;        
    }

    /**
     * Test that command can bind to port 3000 on in the IP address for the local host.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsListeningToLocalHostOn3000()
    {    	
        try
        {
        	// get host IP address
        	String hostIpAddress = httpServerMother.getHostIPAddress(true);    	

        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test host listen port.");        	
        	
            // setup parameters
    		context.put( TestTcpConnectionCommand.EXECUTIONRESULT_KEY, childResult );        	
            context.put( TestTcpConnectionCommand.HOST_KEY, hostIpAddress );
            context.put( TestTcpConnectionCommand.PORTS_KEY, HTTP_PORTS );

            // execute command
            command.execute( context );

            // test
            assertEquals( ExecutionState.SUCCESS, childResult.getState());

        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }

    /**
     * Test that command can bind to port 3000 on in the IP address for the local host.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsListeningToUnknownLocalHostOn4000()
    {    	
        try
        {
        	// get host IP address
        	String hostIpAddress = httpServerMother.getHostIPAddress(true);    	

        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test host listen port.");        	
        	
            // setup parameters
    		context.put( TestTcpConnectionCommand.EXECUTIONRESULT_KEY, childResult );        	
            context.put( TestTcpConnectionCommand.HOST_KEY, hostIpAddress );
            context.put( TestTcpConnectionCommand.PORTS_KEY, new int[] { 4000 });

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
