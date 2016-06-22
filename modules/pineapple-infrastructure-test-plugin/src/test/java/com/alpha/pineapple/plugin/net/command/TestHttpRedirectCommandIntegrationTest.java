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
import org.easymock.EasyMock;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectTest;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpConfigurationImpl;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.model.Mapper;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherHttpServer;


/**
 * Integration test for the <code>TestHttpRedirecCommand</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.net-config.xml" } )
public class TestHttpRedirectCommandIntegrationTest {
	
    /**
     * Single host.
     */
    static final String HTTP_REMOTE_HOST = "http://127.0.0.1:3000/sniffer";    

    /**
     * Redirecting host.
     */
    static final String HTTP_REDIRECTING_HOST = "http://127.0.0.1:3000/redirect";    
    
    /**
     * Host which return 404.
     */
    static final String HTTP_404_HOST = "http://127.0.0.1:3000/kost";    

    /**
     * Unknown host.
     */
    static final String HTTP_UNKNOWN_HOST = "http://127.1.1.1/sniffer";    
    
	/**
	 * TCP port for  HTTP server.
	 */
    static final int HTTP_PORT = 3000;
    
    /**
     * Object under test.
     */
    @Resource( name ="testHttpCommand")
    Command command;
        
    /**
     * Model mapper object.
     */
    @Resource    
    Mapper mapper;
    
    /**
     * Chain context.
     */
    Context context;
    
    /**
     * Object mother for the infrastructure model.
     */
    ObjectMotherContent contentMother;
    
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

        // create content mother
        contentMother = new ObjectMotherContent();		        
        
        // create HTTP server object mother
        httpServerMother = new ObjectMotherHttpServer();

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
     * Test HTTP redirect test fails with unknown URL.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsWithUnknownUrl()
    {
        try
        {
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect302(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_UNKNOWN_HOST;
			String redirectUrl = HTTP_UNKNOWN_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
            command.execute( context );

            // test
            assertEquals( ExecutionState.FAILURE, childResult.getState());
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }
    }                  

    /**
     * Test HTTP redirect test fails if host returns HTTP status code 404.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsWith404()
    {
        try
        {  
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect302(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_404_HOST;
			String redirectUrl = HTTP_UNKNOWN_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
            command.execute( context );

            // test
            assertEquals( ExecutionState.FAILURE, childResult.getState());
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }    	
    }                      
    
    /**
     * Test that command succeeds 
     * with an URL which is redirected with 301 and a "Location" header.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsRedirectedUrlWithStatus301()
    {
        try {
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect301(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_REDIRECTING_HOST ;
			String redirectUrl = HTTP_REMOTE_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
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
     * Test that command succeeds 
     * with an URL which is redirected with 302 and a "Location" header.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsRedirectedUrlWithStatus302()
    {
        try {
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect302(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_REDIRECTING_HOST ;
			String redirectUrl = HTTP_REMOTE_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
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
     * Test that command succeeds 
     * with an URL which is redirected with 303 and a "Location" header.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsRedirectedUrlWithStatus303()
    {
        try {
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect303(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_REDIRECTING_HOST ;
			String redirectUrl = HTTP_REMOTE_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
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
     * Test that command succeeds 
     * with an URL which is redirected with 304 and a "Location" header.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsRedirectedUrlWithStatus304()
    {
        try {
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect304(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_REDIRECTING_HOST ;
			String redirectUrl = HTTP_REMOTE_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
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
     * Test that command succeeds 
     * with an URL which is redirected with 305 and a "Location" header.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsRedirectedUrlWithStatus305()
    {
        try {
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect305(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_REDIRECTING_HOST ;
			String redirectUrl = HTTP_REMOTE_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
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
     * Test that command fails
     * with an URL which is redirected with 306 and a "Location" header.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsRedirectedUrlWithStatus306()
    {
        try {
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect306(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_REDIRECTING_HOST ;
			String redirectUrl = HTTP_REMOTE_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
            command.execute( context );

            // test
            assertEquals( ExecutionState.FAILURE, childResult.getState());
        }
        catch ( Exception e )
        {
            fail( StackTraceHelper.getStrackTrace( e ) );
        }    	    	
    }              

    
    
    /**
     * Test that command fails if the server returns 200
     * and no "Location" header is defined.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsWith200()
    {
        try
        {   
            // create HTTP server
            httpServer = httpServerMother.createHttpServerWithRedirect302(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	

			// create test case
        	String url = HTTP_REMOTE_HOST;
			String redirectUrl = HTTP_REMOTE_HOST;
        	HttpRedirectTest test = contentMother.createHttpRedirectTest(url, redirectUrl);
        	
			// create model
        	mapper.mapHttpRedirectTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
            // execute HTTP test command
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
