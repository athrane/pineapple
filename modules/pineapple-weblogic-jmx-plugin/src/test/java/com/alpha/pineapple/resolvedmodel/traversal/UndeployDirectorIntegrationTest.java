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


package com.alpha.pineapple.resolvedmodel.traversal;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.plugin.weblogic.jmx.session.WeblogicJMXEditSession;
import com.alpha.pineapple.resolvedmodel.ResolvedObject;
import com.alpha.pineapple.resolvedmodel.ResolvedParticipant;
import com.alpha.pineapple.resolvedmodel.ResolvedType;
import com.alpha.pineapple.resolvedmodel.ResolvedTypeImpl;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherWebLogicJMX;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;
import com.oracle.xmlns.weblogic.domain.DomainDocument;

/**
 * Integration test of the Spring configured bean named <code>undeployDirector</code>
 * which is an instance of the class <code>PostOrderDirectorImpl</code>.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class UndeployDirectorIntegrationTest {

    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWeblogicJmxSession sessionMother;

    /**
     * WeblogicJMXSession object mother.
     */
    ObjectMotherWebLogicJMX jmxMother;
    
	/**
	 * JMX session.
	 */
	WeblogicJMXEditSession session;
    
    /**
     * Object mother for the WebLogic domain model.
     */
    ObjectMotherContent contentMother;
    
    /**
     * Object under test.
     */
    @Resource
    ModelTraversalDirector undeployDirector;
	
	/**
	 * Execution result.
	 */
	ExecutionResult result;
        
    /**
     * Some random server name.
     */
    String randomServerName;
		
	@Before
	public void setUp() throws Exception {
	
    	// generate random name
    	randomServerName = RandomStringUtils.randomAlphabetic(10)+"-server";
		
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
	public void tearDown() throws Exception {
		
		// delete server if it exists
		jmxMother.deleteServer(session, randomServerName );
		
		undeployDirector = null;
        sessionMother = null;
        contentMother = null;
        jmxMother = null;
        result = null;
	}	
	
    /**
     * Test that instance can be looked up from the context.
     */
    @Test
    public void testCanInstanceFromContext()
    {
        assertNotNull( undeployDirector);
    }	
           
    /**
     * Test that builder can be invoked a minimal domain, i.e a named domain.
     * @throws Exception If test fails
     */
    @Test
    public void testCanExecuteWithMinimalDomain() throws Exception
    {
        // create content
        DomainDocument domainDoc = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );
                    
		// start edit session 
    	jmxMother.startEdit(session);
        
        // create participant
        ResolvedParticipant primary = contentMother.createDomainResolvedParticipant(domainDoc);            
        ResolvedParticipant secondary = jmxMother.createDomainObjName_ResolvedParticipant(session);
        ResolvedType parent = null;

        // create resolved type at the root of the models
        ResolvedObject resolvedRoot = ResolvedTypeImpl.createResolvedObject(parent, primary, secondary);
                                                                    
        // build
        undeployDirector.startTraversal(session, resolvedRoot, result);
        
        // result from builder
        ExecutionResult builderResult = result.getChildren()[0];
        
        // test
        assertEquals(ExecutionResult.ExecutionState.SUCCESS, builderResult.getState());            
    }
                   
}


