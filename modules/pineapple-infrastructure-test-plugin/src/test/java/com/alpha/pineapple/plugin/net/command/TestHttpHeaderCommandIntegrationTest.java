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

import java.util.HashMap;

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
import com.alpha.pineapple.plugin.infrastructure.model.HttpHeaderTest;
import com.alpha.pineapple.plugin.infrastructure.model.HttpRedirectTest;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpConfigurationImpl;
import com.alpha.pineapple.plugin.net.http.ResponsePropertyInfoSet;
import com.alpha.pineapple.plugin.net.model.Mapper;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherHttpServer;


/**
 * Integration test for the <code>TestHttpHeaderCommand</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.net-config.xml" } )
public class TestHttpHeaderCommandIntegrationTest {
	
	/**
	 * Jetty Version.
	 */
    static final String JETTY_SERVER_VERSION = "Jetty(9.2.0.v20140526)";

	/**
     * URL array with single host.
     */
    static final String HTTP_REMOTE_HOST = "http://127.0.0.1:3000/sniffer";    

    /**
     * URL array with single host which return 404.
     */
    static final String HTTP_404_HOST = "http://127.0.0.1:3000/kost";    

    /**
     * URL array with single unknown host.
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
     * HTTP server object mother 
     */
    ObjectMotherHttpServer httpServerMother;

    /**
     * Object mother for the infrastructure model.
     */
    ObjectMotherContent contentMother;
    
    /**
     * HTTP server. 
     */
    Server httpServer;
        
	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;    

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
        
        // create HTTP server
        httpServer = httpServerMother.createHttpServer(httpServerMother.getHostIPAddress(true), HTTP_PORT);
        
        // create execution result
		executionResult = new ExecutionResultImpl("Root result" );
		
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
     * Test HTTP headers test fails with unknown URL.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsWithUnknownUrl()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();        	
            String[] nonPresentHeaders = new String[0];
        	
			// create test case
        	String url = HTTP_UNKNOWN_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * Test HTTP headers fails if host returns HTTP status code 404,
     * although it also returns the expected headers.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsWith404()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Server", JETTY_SERVER_VERSION);            
            String[] nonPresentHeaders = new String[0];
        	
			// create test case
        	String url = HTTP_404_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * with one header and no non present headers defined.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithOneExpectedHeaders()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Server", JETTY_SERVER_VERSION);            
            String[] nonPresentHeaders = new String[0];
            
			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * Test that command succeeds with no headers 
     * and no non present headers defined.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithNoHeadersAndNoNonPresentHeaders()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();        	
            String[] nonPresentHeaders = new String[0];
        	
			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * with two headers and no non present headers defined.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithTwoExpectedHeaders()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Server", JETTY_SERVER_VERSION);
            headers.put("Content-Type", "text/html; charset=ISO-8859-1");            
            String[] nonPresentHeaders = new String[0];

			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * with zero expected headers 
     * and one non present headers defined.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithOneNonPresentHeader()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            String[] nonPresentHeaders = new String[1];
            nonPresentHeaders[0] = "ServerXX";
        	
			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * with zero expected headers 
     * and one non present headers defined,
     * but with the non present header returned from the server.
     * 
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsIfOneNonPresentHeaderIsReturnedfromServer()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            String[] nonPresentHeaders = new String[1];
            nonPresentHeaders[0] = "Server";

			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * Test that command fails
     * with zero expected headers 
     * and two non present headers defined,
     * but with the non present headers returned from the server.
     * 
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsIfTwoNonPresentHeaderIsReturnedfromServer()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            String[] nonPresentHeaders = new String[2];
            nonPresentHeaders[0] = "Server";
            nonPresentHeaders[1] = "Transfer-Encoding";            
        	
			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * Test that command fails
     * with one expected headers 
     * and one non present headers defined,
     * but with the non present header returned from the server.
     * 
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsIfOneNonPresentHeaderIsReturnedfromServerAndOneExpectedHeader()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Server", JETTY_SERVER_VERSION);            
            String[] nonPresentHeaders = new String[1];
            nonPresentHeaders[0] = "Content-Type";
        	
			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
     * Test that command fails
     * with one expected headers 
     * and one non present headers defined,
     * where they share the same/value and the header is returned from the server
     * 
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testFailsIfSameNonPresentHeaderAndExpectedHeaderIsReturnedFfromServer()
    {
        try
        {
        	// create headers
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Server", JETTY_SERVER_VERSION);            
            String[] nonPresentHeaders = new String[1];
            nonPresentHeaders[0] = "Server";
        	
			// create test case
        	String url = HTTP_REMOTE_HOST;
        	HttpHeaderTest test = contentMother.createHttpHeaderTest(url, headers, nonPresentHeaders );

			// create model
        	mapper.mapHttpHeaderTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test HTTP headers.");        	
        	
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
