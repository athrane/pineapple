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

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.session.Session;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherWeblogicJmxSession;
import com.alpha.testutils.WJPIntTestConstants;
import com.oracle.xmlns.weblogic.domain.DomainType.ConfigurationAuditType;


/**
 * Integration test for the <code>UndeployConfiguration</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/com.alpha.pineapple.plugin.weblogic.jmx-config.xml" } )
public class UndeployConfigurationIntegrationTest
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
	 * Execution result.
	 */
	ExecutionResult result;
    
    /**
     * Object under test.
     */
    @Resource
    UndeployConfiguration undeployConfiguration;

    @Before
    public void setUp() throws Exception
    {
        // create session mother
        sessionMother = new ObjectMotherWeblogicJmxSession();

        // create content mother
        contentMother = new ObjectMotherContent();
        
        // create execution result
        result = new ExecutionResultImpl( "Root result" );        
    }

    @After
    public void tearDown() throws Exception
    {
        sessionMother = null;
        contentMother = null;
        result = null;
    }

    /**
     * Test that undeploy configuration operation can be looked up from the context.
     */
    @Test
    public void testCanGetInstanceFromContext()
    {
        assertNotNull( undeployConfiguration );
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
        Object content = contentMother.createMinimalDomain( WJPIntTestConstants.targetEnvironment );

        // create session
        Session session = sessionMother.createConnectedWlsJmxEditSession();

        // invoke operation
        undeployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());                
    }

    /**
     * Test that operation can execute with a domain 
     * with two objects: Domain and Machine.
     * 
     * @throws Exception If test fails. 
     */    
    @Test
    public void testCanExecuteWithTwoObjectsDomainAndMachine() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithObjectMachine( 
        		WJPIntTestConstants.targetEnvironment, "internalmachine1" );

        // create session
        Session session = sessionMother.createConnectedWlsJmxEditSession();

        // invoke operation
        undeployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());                
    }

    /**
     * Test that operation can execute with a domain 
     * with single object: Domain
     * and single primitive attribute: Notes
     * 
     * @throws Exception If test fails.
     */    
    @Test
    public void testCanExecuteWithDomainPrimitiveNotes() throws Exception
    {
        // create content
        Object content = contentMother.createDomainWithPrimitiveNotes(
        		WJPIntTestConstants.targetEnvironment, "some-note" );
        
        // create session
        Session session = sessionMother.createConnectedWlsJmxEditSession();

        // invoke operation
        undeployConfiguration.execute( content, session, result );
        
        // test
        assertEquals(ExecutionState.SUCCESS, result.getState());                
    }

    /**
     * Test that operation can execute with a domain 
     * with single object: Domain
     * and single enum attribute: ConfigurationAuditType
     * 
     * @throws Exception If test fails.
     */    
    @Test
    public void testCanExecuteWithDomainEnumConfigurationAuditType() throws Exception
    {
		// create content
        Object content = contentMother.createDomainWithEnumConfigurationAuditType(
        		WJPIntTestConstants.targetEnvironment, 
        		ConfigurationAuditType.LOG );
        
        // create session
        Session session = sessionMother.createConnectedWlsJmxEditSession();

        // invoke operation
        undeployConfiguration.execute( content, session, result );
    }
    
}
