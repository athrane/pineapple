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


package com.alpha.pineapple.plugin.weblogic.jmx.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import static com.alpha.testutils.WJPIntTestConstants.*;

/**
 * Integration test for the <code>CreateReport</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class CreateReportIntegrationTest
{

    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;

    /**
     * Object mother for the WebLogic domain model.
     */
    ObjectMotherContent contentMother;

    /**
     * WebLogic JMX object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;		
    
	/**
	 * Execution result.
	 */
	ExecutionResult result;
    
	/**
	 * JMX session.
	 */
	WeblogicJMXEditSession session;
	
    /**
     * Object under test.
     */
    @Resource
    CreateReport createReport;

    /**
     * Some random virtual host name.
     */
    String randomVirtualHostName;
    
    @Before
    public void setUp() throws Exception
    {
    	// generate random name
    	randomVirtualHostName = RandomStringUtils.randomAlphabetic(10)+"-vh";
    	
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();

        // create session      
        session = sessionMother.createConnectedWlsJmxEditSession();        
        
        // create content mother
        contentMother = new ObjectMotherContent();

		// create JMX mother
		jmxMother = new ObjectMotherWebLogicJMX();        
		
        // create execution result
        result = new ExecutionResultImpl( "Root result" );        
    }

    @After
    public void tearDown() throws Exception
    {
		// delete virtual host if it exists
		jmxMother.deleteVirtualHost(session, randomVirtualHostName);
    	
    	randomVirtualHostName = null;
        sessionMother = null;
        contentMother = null;
        result = null;
    }

    /**
     * Test that TestOperation can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( createReport );
    }

    /**
     * Test that operation can execute with a minimal domain, i.e a named domain.
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanExecuteWithMinimalDomain() throws Exception
    {
        // create content
        Object content = contentMother.createMinimalDomain( targetEnvironment );

        // invoke operation
        createReport.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());        
    }

    
    /**
     * Test that operation can execute and succeeds
     * with a domain with a single machine. 
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteAndSucceedsWithDomainWithSingleMachine() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithObjectMachine( targetEnvironment, "internalmachine1" );

        // invoke operation
        createReport.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());        
    }

    
    /**
     * Test that operation can execute with a domain 
     * with two objects: Domain and virtual host.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithDomainAndVirtualHost() throws Exception
    {
    	// create virtual host
    	jmxMother.createVirtualHost(session, randomVirtualHostName);
    	
    	// assert the virtual host exist before we run the test
		assertTrue(jmxMother.mbeanExistsAsDomainAttribute(session, "VirtualHosts", randomVirtualHostName));        						
    	
        // create content
        Object content = contentMother.createDomainWithVirtualHost( targetEnvironment, randomVirtualHostName );
        
        // invoke operation
        createReport.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());                
    }
    
    
    /**
     * Test that operation can execute with a live domain. 
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanTestLiveDomain() throws Exception
    {
        // create content
        Object content = contentMother.loadDomainfromFile( "/"+ environmentConfigFile );            

        // invoke operation
        createReport.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());        
    }
 
    /**
     * Test that operation can execute with a minimal domain (i.e a named domain) twice).
     * 
     * @throws Exception If test fails.
     */
    @Test
    public void testCanExecuteTwiceWithMinimalDomain() throws Exception
    {
    	// create child result for operation #1
    	ExecutionResult operationResult1 = result.addChild("operation #1");
    	
        // create content
        Object content = contentMother.createMinimalDomain( targetEnvironment );

        // invoke operation
        createReport.execute( content, session, operationResult1 );
        
        // test
        assertEquals(ExecutionState.SUCCESS, operationResult1.getState());

    	// create child result for operation #2
    	ExecutionResult operationResult2 = result.addChild("operation #2");
        
        // create content
        content = contentMother.createMinimalDomain( targetEnvironment );

        // invoke operation
        createReport.execute( content, session, operationResult2 );
        
        // test
        assertEquals(ExecutionState.SUCCESS, operationResult2.getState());                       
    }
    
    
}
