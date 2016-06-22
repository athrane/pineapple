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


package com.alpha.pineapple.plugin.fusion.installation.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.easymock.classextension.EasyMock;
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
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.plugin.fusion.installation.operation.DeployConfiguration;
import com.alpha.pineapple.session.Session;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>DeployConfiguration</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml", "/com.alpha.pineapple.plugin.fusion.installation-config.xml" } )
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
     * Mock Runtime directory provider.
     */
    @Resource
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
    
    /**
     * Object mother for the infrastructure model.
     */
    ObjectMotherContent contentMother;
    
	/**
	 * Implementation note:
	 * The session object from the process execution support project
	 * is used since the session impl. in this project is just a
	 * naked sub class of that class.
	 */
    @Resource(name="processExecutionSession")
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
        resource = EasyMock.createMock(com.alpha.pineapple.model.configuration.Resource.class);
        
        // create mock credential        
		Credential credential = EasyMock.createMock(Credential .class);
		
		// connect session
        session.connect(resource, credential);
		
        // create execution result
        executionResult = new ExecutionResultImpl( "Root result" );
        
        // create content mother
        contentMother = new ObjectMotherContent();		        
        
        // reset plugin provider
		EasyMock.reset(coreRuntimeDirectoryProvider);        
	}

	@After
	public void tearDown() throws Exception {
        testDirectory = null;
		session = null;
		executionResult = null;
		contentMother = null;
	}

	File writeInstaller() throws IOException {
		// Initialize installer  
		File installerFile = new File(testDirectory, "installer.cmd");
		
		// write installer
		Collection<String> lines = new ArrayList<String>();
		lines.add("@ECHO OFF");
		lines.add("@ECHO JRI [%1] [%2] [%3] [%4] [%5] [%6]");		
		
		// write the file
		FileUtils.writeLines(installerFile, lines);
		return installerFile;
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
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {

        // create content
        Object content = contentMother.createEmptyFusionInstallation();

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 0 , executionResult.getChildren().length);        
	}

	
	/**
	 * Test that the operation can execute with a Application 
	 * Development Runtime 11.x model.
	 */
	@Test
	public void testCanExecuteWithAppDevRuntimeRelease11Model() throws Exception {

		File installerFile = writeInstaller();		
		String target = testDirectory.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");
		
		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory).times(2);
		EasyMock.expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installerFile.getAbsolutePath())).andReturn(false);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createApplicationDevelopementRuntimeRelease11Installation(
        		installerFile.getAbsolutePath(), 
        		target, 
        		localJvm.getAbsolutePath() );

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 3 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}
	
	
}
