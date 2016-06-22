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
import com.alpha.pineapple.plugin.infrastructure.model.LoadBalancingTest;
import com.alpha.pineapple.plugin.net.http.HttpConfiguration;
import com.alpha.pineapple.plugin.net.http.HttpConfigurationImpl;
import com.alpha.pineapple.plugin.net.model.Mapper;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherHttpServer;


/**
 * Integration test for the <code>TestHttpCommand</code> class
 * when it is setup to function as an load balancing test case.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.net-config.xml" } )
public class TestLoadBalancingCommandIntegrationTest {
	
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
	 * Mock repository
	 */
	ResultRepository repository;
		
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

        // create mock result repository
        repository = EasyMock.createMock( ResultRepository.class );
        
        // create execution result
		executionResult = new ExecutionResultImpl(repository, "Root result" );
		
		// create HTTP configuration
		httpConfig = new HttpConfigurationImpl();
				
	}
		
	@After
	public void tearDown() throws Exception {
		contentMother = null;
        command = null;
        context = null;
        httpServer.stop();
        httpServer = null;
        httpServerMother = null;
        httpConfig = null;
	}
	
    /**
     * Test load balancing succeeds with single URL.
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testSucceedsWithSingleUrl()
    {
        try
        {        	        	
        	// create execution result        	
        	ExecutionResult childResult = executionResult.addChild("Test response properties");  
        	
        	LoadBalancingTest test  = contentMother.createLoadBalancingTest();        	        	
        	
			// create model
        	mapper.mapLoadBalancingTest(
        			test, 
        			context, 
        			contentMother.createHttpConfigurationMapWithSingleDefaultHttpConfig());
        	
        	// set parameters
    		context.put( TestHttpCommand.EXECUTIONRESULT_KEY, childResult );
        	
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
