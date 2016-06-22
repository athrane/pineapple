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

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherResource;

/**
 * Integration test for the <code>DeployConfiguration</code> class.
 * 
 * Test implements {@linkplain ApplicationContextAware} to support
 * injection of the application context which is used as a parameter
 *  for the deployment session mother.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml", "/com.alpha.pineapple.plugin.weblogic.deployment-config.xml" } )
public class DeployConfigurationIntegrationTest {

	/**
     * Current test directory.
     */
	File testDirectory;
		
    /**
     * Object under test.
     */
    @Resource
    DeployConfiguration deployConfiguration;
    
    /**
     * Object mother for the model.
     */
    ObjectMotherContent contentMother;
        
	/**
	 * Session.
	 */
    @Resource(name="processExecutionSession")
	ProcessExecutionSession session;
			
	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;
	
    /**
     * Mock execution info provider.
     */
    @Resource	
    ExecutionInfoProvider coreExecutionInfoProvider;
    
    /**
     * Mock runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    /**
     * Object mother Resource.
     */
    ObjectMotherResource resourceMother;
	
	/** 
	 * Credential.
	 */
	Credential credential;    
	
	/**
	 * Random Name.
	 */
	String randomId; 

	/**
	 * Random Name.
	 */
	String randomUser;

	/**
	 * Random Name.
	 */
	String randomPassword;
    

	@Before
	public void setUp() throws Exception {
		
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		randomId = RandomStringUtils.randomAlphabetic(10);
		randomUser = RandomStringUtils.randomAlphabetic(10);
		randomPassword = RandomStringUtils.randomAlphabetic(10);

        // create object mother
        resourceMother = new ObjectMotherResource();
		
        // create resource
        com.alpha.pineapple.model.configuration.Resource resource;
        resource = resourceMother.createResourceWithNoProperties();
        
        // create credential        
		createCredential();
		
		// connect session
		session.connect(resource, credential);
		
        // create execution result
        executionResult = new ExecutionResultImpl( "Root result" );
        
        // create content mother
        contentMother = new ObjectMotherContent();		                
	}

	/**
	 * Create credential.
	 */
	void createCredential() {
        credential = new Credential();
        credential.setId( randomId );
        credential.setUser( randomUser );
        credential.setPassword( randomPassword );        
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
        assertNotNull( deployConfiguration );
    }

    /**
     * Test that sessions is injected from the context.
     */
    @Test
    public void testSessionIsInjectedFromContext()
    {
        assertNotNull( session );
    }
    
	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

        // create content
        Object content = contentMother.createEmptyDeployment();

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 0 , executionResult.getChildren().length);        
	}
		
}
