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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.EasyMock;
import org.eclipse.jetty.server.Server;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpConfigurationImpl;
import com.alpha.pineapple.plugin.net.http.HttpInvocationsSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSetImpl;
import com.alpha.testutils.ObjectMotherHttpServer;
/**
 * Integration test for the <code>TestResponsePropertiesCommand</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.net-config.xml" } )
public class TestResponsePropertiesCommandIntegrationTest {
	
    /**
     * URL array with single host.
     */
    static final String[] HTTP_REMOTE_HOST = {"http://127.0.0.1:3000/sniffer"};    

	/**
	 * TCP port for  HTTP server.
	 */
    static final int HTTP_PORT = 3000;
    
    /**
     * Object under test.
     */
    @Resource( name ="testResponsePropertiesCommand")
    Command command;

    /**
     * HTTP Get command.
     */
    @Resource( name ="invokeHttpGetMethodCommand")
    Command httpGetCommand;
    
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

	/**
	 * Mock repository
	 */
	ResultRepository repository;

    /**
     * Response properties set.
     */
	ResponsePropertyInfoSet responseProperties;
		
	/**
	 * HTTP Configuration object.
	 */
	HttpConfiguration httpConfig;	
		
	@Before
	public void setUp() throws Exception {
		
        // create context
        context = new ContextBase();
        
        // create HTTP server object mother
        httpServerMother = new ObjectMotherHttpServer();

        // create HTTP server
        httpServer = httpServerMother.createHttpServer(httpServerMother.getHostIPAddress(true), HTTP_PORT);

        // create mock result repository
        repository = EasyMock.createMock( ResultRepository.class );
        
        // create execution result
		executionResult = new ExecutionResultImpl(repository, "Root result" );
				
		// create HTTP configuration
		httpConfig = new HttpConfigurationImpl();		
	}
		
	@After
	public void tearDown() throws Exception {
        command = null;
        context = null;
        httpServer.stop();
        httpServer = null;
        httpServerMother = null;
        responseProperties = null;
        httpConfig = null;
	}

    /**
     * Test HTTP Get can be executed with single URL.
     */
    @SuppressWarnings( "unchecked" )
    HttpInvocationsSet initializeResultSet(int numberRequests )
    {
        try
        {
        	// configure HTTP configuration
        	httpConfig.setHttpFollowRedirects(true);
        	
        	// create execution result
        	ExecutionResult childResult = executionResult.addChild("HTTP get"); 
        	
            // setup parameters
    		context.put( InvokeHttpGetMethodCommand.EXECUTIONRESULT_KEY, childResult );        	
            context.put( InvokeHttpGetMethodCommand.URLS_KEY, HTTP_REMOTE_HOST );
            context.put( InvokeHttpGetMethodCommand.REQUESTS_KEY, numberRequests );
            context.put( InvokeHttpGetMethodCommand.RESET_KEY, false );
            context.put( InvokeHttpGetMethodCommand.RESPONSEPROPERTIES_SET_KEY, responseProperties );
            context.put( InvokeHttpGetMethodCommand.HTTPCONFIGURATION_KEY, httpConfig );

            // execute HTTP get command
            httpGetCommand.execute( context );

            // test
            Object resultSet = context.get(InvokeHttpGetMethodCommand.RESULTS_KEY );
            assertNotNull( resultSet );

            // test
            assertEquals( ExecutionState.SUCCESS, childResult.getState());
            
            return (HttpInvocationsSet) resultSet; 
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
            return null;
        }
    }              
	
    /**
     * Test HTTP Get can be executed with aero sequences.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithZeroSequences()
    {
    	int numberSequences = 0;
    	
        try
        {
        	// configure HTTP configuration
        	httpConfig.setHttpFollowRedirects(true);
        	
        	// create response properties
        	responseProperties = new ResponsePropertyInfoSetImpl();
        	
        	// create response property info for HTTP status code
        	Matcher<?> matcher = CoreMatchers.everyItem(CoreMatchers.is(200));
			responseProperties.addProperty("statuscode", "method/statusCode", matcher, matcher);

        	// create result set
        	Object resultSet = initializeResultSet(numberSequences);
        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test response properties");        	
        	
            // setup parameters
    		context.put( TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, childResult );        	
            context.put( TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, resultSet );
            context.put( TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, responseProperties );
            

            // execute HTTP get command
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
     * Test HTTP Get can be executed with single sequence.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithOneSequence()
    {
    	int numberSequences = 1;
    	
        try
        {
        	// configure HTTP configuration
        	httpConfig.setHttpFollowRedirects(true);
        	
        	// create response properties
        	responseProperties = new ResponsePropertyInfoSetImpl();
        	
        	// create response property info for HTTP status code
        	Matcher<?> matcher = CoreMatchers.everyItem(CoreMatchers.is(200));
			responseProperties.addProperty("statuscode", "method/statusCode", matcher, matcher);

        	// create result set
        	Object resultSet = initializeResultSet(numberSequences);
        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test response properties");        	
        	
            // setup parameters
    		context.put( TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, childResult );        	
            context.put( TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, resultSet );
            context.put( TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, responseProperties );

            // execute HTTP get command
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
     * Test HTTP Get can be executed with single sequence.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithTwoSequences()
    {
    	int numberSequences = 2;
    	
        try
        {
        	// create response properties
        	responseProperties = new ResponsePropertyInfoSetImpl();
        	
        	// create response property info for HTTP status code
        	Matcher<?> matcher = CoreMatchers.everyItem(CoreMatchers.is(200));
			responseProperties.addProperty("statuscode", "method/statusCode", matcher, matcher);

        	// create result set
        	Object resultSet = initializeResultSet(numberSequences);
        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test response properties");        	
        	
            // setup parameters
    		context.put( TestResponsePropertiesCommand.EXECUTIONRESULT_KEY, childResult );        	
            context.put( TestResponsePropertiesCommand.INVOCATIONRESULTS_SET_KEY, resultSet );
            context.put( TestResponsePropertiesCommand.RESPONSEPROPERTIES_SET_KEY, responseProperties );           

            // execute HTTP get command
            command.execute( context );

            // test
            assertEquals( ExecutionState.SUCCESS, childResult.getState());
                        
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }              
    
    
}
