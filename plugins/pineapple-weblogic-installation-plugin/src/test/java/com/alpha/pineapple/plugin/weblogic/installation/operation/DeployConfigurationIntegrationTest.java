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


package com.alpha.pineapple.plugin.weblogic.installation.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
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
import com.alpha.pineapple.session.Session;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>DeployConfiguration</code> class.  
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
@ContextConfiguration( locations = { "/com.alpha.pineapple.testutils-config.xml", "/com.alpha.pineapple.plugin.weblogic.installation-config.xml" } )
public class DeployConfigurationIntegrationTest {

	/**
	 * Test installer name.
	 * Downloaded from: http://www.paulsadowski.com/wsh/cmdprogs.htm
	 */
	static final String TEST_SCRIPT_EXE = "echo.exe";

	/**
     * Current test directory.
     */
	File testDirectory;

	/**
	 * Test script directory
	 */
	File testScriptsDir = new File(SystemUtils.getUserDir(), "/src/test/resources/");
	
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
     * Object mother for the WebLogic installation model.
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
	
	/**
	 * Random directory.
	 */
	File randomDir;
    
	@Before
	public void setUp() throws Exception {
	
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
		        
        // create random directory object
        randomDir = new File(testDirectory, RandomStringUtils.randomAlphabetic(10)); 
        
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
		File installerFile = new File(testDirectory, "installer.exe");
		
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
        Object content = contentMother.createEmptyWeblogicInstallation();

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );

        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 0 , executionResult.getChildren().length);        
	}


	/**
	 * Test that the operation fails if the installer can't be found.
	 */
	@Test
	public void testFailsIfRelease9InstallerDoesntExist() throws Exception {
		File installerFile = new File(testDirectory, "installer.cmd");		
		String nonExistingTargetDir = randomDir.getAbsolutePath();

		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installerFile.getAbsolutePath())).andReturn(false);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease9WeblogicInstallation(installerFile.getAbsolutePath(), nonExistingTargetDir);

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.FAILURE, executionResult.getState());
        assertEquals( 2 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}

	/**
	 * Test that the operation fails if the installer can't be found.
	 */
	@Test
	public void testFailsIfRelease10InstallerDoesntExist() throws Exception {

		File installerFile = new File(testDirectory, "installer.cmd");
		String nonExistingTargetDir = randomDir.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");		

		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installerFile.getAbsolutePath())).andReturn(false);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease10WeblogicInstallation(
        		installerFile.getAbsolutePath(), 
        		nonExistingTargetDir, 
        		localJvm.getAbsolutePath() );

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.FAILURE, executionResult.getState());
        assertEquals( 2 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}

	/**
	 * Test that the operation fails if the installer can't be found.
	 */
	@Test
	public void testFailsIfRelease12InstallerDoesntExist() throws Exception {

		File installerFile = new File(testDirectory, "installer.cmd");		
		String nonExistingTargetDir = randomDir.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");		

		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installerFile.getAbsolutePath())).andReturn(false);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease12WeblogicInstallation(
        		installerFile.getAbsolutePath(), 
        		nonExistingTargetDir, 
        		localJvm.getAbsolutePath() );

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.FAILURE, executionResult.getState());
        assertEquals( 2 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}
	
	
	/**
	 * Test that the operation can execute with a WebLogic 9.x model.
	 */
	@Test
	public void testCanExecuteWithRelease9Model() throws Exception {
		File installerFile = new File(testScriptsDir, TEST_SCRIPT_EXE);
		String nonExistingTargetDir = randomDir.getAbsolutePath();

		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory).times(2);
		EasyMock.expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installerFile.getAbsolutePath())).andReturn(false);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease9WeblogicInstallation(installerFile.getAbsolutePath(), nonExistingTargetDir);

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 4, executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}
	
	/**
	 * Test that the operation can execute with a WebLogic 10.x model.
	 */
	@Test
	public void testCanExecuteWithRelease10Model() throws Exception {
		File installerFile = new File(testScriptsDir, TEST_SCRIPT_EXE);
		String nonExistingTargetDir = randomDir.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");
		
		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory).times(2);
		EasyMock.expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installerFile.getAbsolutePath())).andReturn(false);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease10WeblogicInstallation(
        		installerFile.getAbsolutePath(), 
        		nonExistingTargetDir, 
        		localJvm.getAbsolutePath() );

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 4 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}
	
	/**
	 * Test that the operation can execute with a WebLogic 12.x model.
	 */
	@Test
	public void testCanExecuteWithRelease12Model() throws Exception {
		File installerFile = new File(testScriptsDir, TEST_SCRIPT_EXE);
		String nonExistingTargetDir = randomDir.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");
		
		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.expect(coreRuntimeDirectoryProvider.getTempDirectory()).andReturn(testDirectory).times(2);
		EasyMock.expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(installerFile.getAbsolutePath())).andReturn(false);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease12WeblogicInstallation(
        		installerFile.getAbsolutePath(), 
        		nonExistingTargetDir, 
        		localJvm.getAbsolutePath() );

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 4 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}

	/**
	 * Test that the operation skip installation if production is already installed,
	 * with a WebLogic 9.x model.
	 */
	@Test
	public void testSkipInstallationIfProductIsAlreadyInstalledWithRelease9Model() throws Exception {

		File installerFile = writeInstaller();		
		String exitingTargetDir = testDirectory.getAbsolutePath();

		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease9WeblogicInstallation(installerFile.getAbsolutePath(), exitingTargetDir);

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 1 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}

	/**
	 * Test that the operation skip installation if production is already installed,
	 * with a WebLogic 10.x model.
	 */
	@Test
	public void testSkipInstallationIfProductIsAlreadyInstalledWithRelease10Model() throws Exception {

		File installerFile = writeInstaller();		
		String existingTargetDir = testDirectory.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");

		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease10WeblogicInstallation(
        		installerFile.getAbsolutePath(), 
        		existingTargetDir, 
        		localJvm.getAbsolutePath() );

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 1 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}

	/**
	 * Test that the operation skip installation if production is already installed,
	 * with a WebLogic 12.x model.
	 */
	@Test
	public void testSkipInstallationIfProductIsAlreadyInstalledWithRelease12Model() throws Exception {

		File installerFile = writeInstaller();		
		String existingTargetDir = testDirectory.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");

		// complete mock runtime coreRuntimeDirectoryProvider setup
		EasyMock.replay(coreRuntimeDirectoryProvider);
		
		// create content
        Object content = contentMother.createRelease12WeblogicInstallation(
        		installerFile.getAbsolutePath(), 
        		existingTargetDir, 
        		localJvm.getAbsolutePath() );

        // invoke operation
        deployConfiguration.execute( content, session, executionResult );
    
        // test
        assertEquals( ExecutionState.SUCCESS, executionResult.getState());
        assertEquals( 1 , executionResult.getChildren().length);
        
        // verify mocks
        EasyMock.verify(coreRuntimeDirectoryProvider);                
	}
	
}
