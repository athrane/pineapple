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


package com.alpha.pineapple.plugin.weblogic.deployment.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.easymock.classextension.EasyMock.*;

import java.io.File;

import javax.annotation.Resource;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.session.Session;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherWebLogicDeploymentSession;

/**
 * Integration test for the <code>UndeployConfiguration</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml", "/com.alpha.pineapple.plugin.weblogic.deployment-config.xml" } )
public class UndeployConfigurationIntegrationTest {

	/**
     * Current test directory.
     */
	File testDirectory;
		
    /**
     * Object under test.
     */
    @Resource
    UndeployConfiguration undeployConfiguration;
    
    /**
     * Object mother for the model.
     */
    ObjectMotherContent contentMother;
    
    /**
     * Object mother for session.
     */
    ObjectMotherWebLogicDeploymentSession sessionMother;
    
	/**
	 * Session.
	 */
	Session session;
			
	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;
	
    
	@Before
	public void setUp() throws Exception {
	
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
		        
        // create mock resource
        com.alpha.pineapple.model.configuration.Resource resource;
        resource = createMock(com.alpha.pineapple.model.configuration.Resource.class);
        
        // create mock credential        
		Credential credential = createMock(Credential .class);

        // create session mother
        sessionMother = new ObjectMotherWebLogicDeploymentSession();
        
        session = sessionMother.createUnconnectedWebLogicDeploymentSession(); 
		
		// connect session - DONE in the session mother...
		
        // create execution result
        executionResult = new ExecutionResultImpl( "Root result" );
        
        // create content mother
        contentMother = new ObjectMotherContent();		                
	}

	@After
	public void tearDown() throws Exception {
        testDirectory = null;
		session = null;
		executionResult = null;
		contentMother = null;
	}
	
    /**
     * Test that operation can be looked up from the context.
     */
    @Test
    public void testCanGetOperationFromContext()
    {
        assertNotNull( undeployConfiguration );
    }
	
	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

        // create content
        Object content = contentMother.createEmptyDeployment();

        // invoke operation
        undeployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 0 , executionResult.getChildren().length);        
	}
		
}
