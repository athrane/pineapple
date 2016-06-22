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

package com.alpha.pineapple.plugin.weblogic.installation.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.weblogic.installation.model.WeblogicInstallation;
import com.alpha.pineapple.session.ProcessExecutionSession;
import com.alpha.pineapple.test.AssertionHelper;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Unit test for the {@linkplain InstallationHelperImpl}class.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@TestExecutionListeners( {DirectoryTestExecutionListener.class})
public class InstallationHelperImplTest {

	/**
	 * Test installer name.
	 * Downloaded from: http://www.paulsadowski.com/wsh/cmdprogs.htm
	 */
	static final String TEST_SCRIPT_EXE = "echo.exe";
	
	/**
	 * First array index,
	 */
	static final int FIRST_INDEX = 0;

	/**
     * Current test directory.
     */
	File testDirectory;

    /**
     * object under test
     */
    InstallationHelperImpl helper;
	
    /**
     * Mock message provider for I18N support.
     */
    MessageProvider messageProvider;
	        
    /**
     * Mock runtime directory provider.
     */
    RuntimeDirectoryProvider coreRuntimeDirectoryProvider;
        
    /**
     * Mock execution info provider.
     */
    ExecutionInfoProvider coreExecutionInfoProvider;
    
    /**
     * Mock assertion helper.
     */
    AssertionHelper assertionHelper;

	/**
	 * Mock execution result.
	 */	
	ExecutionResult executionResult;
    
    /**
     * Object mother for the WebLogic installation model.
     */
    ObjectMotherContent contentMother;
	
	/**
	 * Random WebLogic home.
	 */
	File randomWlsHome;

	/**
	 * Random Middleware home.
	 */
	File randomMwHome;

	/**
	 * Random installer.
	 */
	File randomInstaller;
	
	@Before
	public void setUp() throws Exception {
		
        // get the test directory
        testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// generate names 
		randomMwHome = new File( testDirectory, RandomStringUtils.randomAlphabetic(10)+"-mwhome" ); 
		randomWlsHome = new File( randomMwHome, RandomStringUtils.randomAlphabetic(10)+"-wlshome" ); 
		randomInstaller = new File( testDirectory, RandomStringUtils.randomAlphabetic(10)+"-installer" ); 
		
        // create content mother
        contentMother = new ObjectMotherContent();		        
				
    	// create helper 
        helper = new InstallationHelperImpl();

        // create mocks
        executionResult = new ExecutionResultImpl("execution result");
        assertionHelper = EasyMock.createMock( AssertionHelper.class );
        messageProvider = EasyMock.createMock( MessageProvider.class );
        coreRuntimeDirectoryProvider = EasyMock.createMock( RuntimeDirectoryProvider.class );
        coreExecutionInfoProvider = EasyMock.createMock( ExecutionInfoProvider.class );

        // reset mock provider
        EasyMock.reset(coreRuntimeDirectoryProvider);
        
        // inject mocks
        ReflectionTestUtils.setField( helper, "assertionHelper", assertionHelper );
        ReflectionTestUtils.setField( helper, "coreRuntimeDirectoryProvider", coreRuntimeDirectoryProvider );                
        ReflectionTestUtils.setField( helper, "coreExecutionInfoProvider", coreExecutionInfoProvider );
        ReflectionTestUtils.setField( helper, "messageProvider", messageProvider );        
        
        // complete mock source initialization        
        IAnswer<String> answer = new MessageProviderAnswerImpl(); 

        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class )));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();
        EasyMock.expect( messageProvider.getMessage(
        		(String) EasyMock.isA( String.class ), 
        		(Object[]) EasyMock.anyObject()));
        EasyMock.expectLastCall().andAnswer(answer).anyTimes();        
        EasyMock.replay(messageProvider);                                                
	}
    
	@After
	public void tearDown() throws Exception {
	}


	/**
	 * Test that true is returned if installer exists.
	 */	
	@Test
	public void testIsInstallerValid() {
		ProcessExecutionSession session = EasyMock.createMock( ProcessExecutionSession.class );		

		// create assertion result mock		
		ExecutionResult assertionResult = EasyMock.createMock( ExecutionResult.class );
		assertionResult.addMessage("ihi.assert_installer_exists_info", randomInstaller.getAbsolutePath());
		EasyMock.expect(assertionResult.isSuccess()).andReturn(true);
		EasyMock.replay(assertionResult);				
				
		// activate mocks		
		EasyMock.expect(assertionHelper.assertFileExist(randomInstaller, 
				messageProvider, 
				"ihi.assert_installer_exists",
				executionResult))
				.andReturn(assertionResult);		
		EasyMock.replay(assertionHelper);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(session);		
		
		// test
		assertFalse(helper.isInstallerValid(randomInstaller, executionResult));
		
		// verify mocks
		//EasyMock.verify(assertionResult);		
		EasyMock.verify(assertionHelper);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(session);		
	}

	/**
	 * Test that false is returned if target directory doesn't exists.
	 */
	@Test
	public void testIsProductInstalledReturnsFalseIfDirectoryDoesntExist() {
		ProcessExecutionSession session = EasyMock.createMock( ProcessExecutionSession.class );

		// create assertion result mock		
		ExecutionResult assertionResult = EasyMock.createMock( ExecutionResult.class );
		EasyMock.expect(assertionResult.isSuccess()).andReturn(false).times(2);
		EasyMock.replay(assertionResult);				
				
		// activate mocks		
		EasyMock.expect(assertionHelper.assertDirectoryExist(randomWlsHome, 
				messageProvider, 
				"ihi.assert_weblogic_installed"))
				.andReturn(assertionResult);		
		EasyMock.replay(assertionHelper);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(session);		
		
		// test
		assertFalse(helper.isProductInstalled(session, executionResult, randomWlsHome));
		
		// verify mocks
		EasyMock.verify(assertionResult);		
		EasyMock.verify(assertionHelper);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(session);		
	}
	
	/**
	 * Test that true is returned if target directory exists.
	 */
	@Test
	public void testIsProductInstalledReturnsTrueIfDirectoryExist() {

		ProcessExecutionSession session = EasyMock.createMock( ProcessExecutionSession.class );

		// create assertion result mock		
		ExecutionResult assertionResult = EasyMock.createMock( ExecutionResult.class );
		EasyMock.expect(assertionResult.isSuccess()).andReturn(true).times(2);
		EasyMock.replay(assertionResult);				
				
		// activate mocks		
		EasyMock.expect(assertionHelper.assertDirectoryExist(randomWlsHome, 
				messageProvider, 
				"ihi.assert_weblogic_installed"))
				.andReturn(assertionResult);		
		EasyMock.replay(assertionHelper);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(session);		
		
		// test
		assertTrue(helper.isProductInstalled(session, executionResult, randomWlsHome));
		
		// verify mocks
		EasyMock.verify(assertionResult);		
		EasyMock.verify(assertionHelper);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(session);		
	}

	/**
	 * Test that successful child result is added if target directory doesn't exists.
	 */
	@Test
	public void testIsProductInstalledAddsSuccessfulChildResultIfDirectoryDoesntExist() {
		
		ProcessExecutionSession session = EasyMock.createMock( ProcessExecutionSession.class );

		// create assertion result mock		
		ExecutionResult assertionResult = EasyMock.createMock( ExecutionResult.class );
		EasyMock.expect(assertionResult.isSuccess()).andReturn(false).times(2);
		EasyMock.replay(assertionResult);				
				
		// activate mocks		
		EasyMock.expect(assertionHelper.assertDirectoryExist(randomWlsHome, 
				messageProvider, 
				"ihi.assert_weblogic_installed"))
				.andReturn(assertionResult);		
		EasyMock.replay(assertionHelper);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(session);		
		
		// invoke
		helper.isProductInstalled(session, executionResult, randomWlsHome);
		
		// test
		assertEquals(1, executionResult.getChildren().length);
		assertEquals(1, executionResult.getChildrenWithState(ExecutionState.SUCCESS).length);
		Map<String, String> messages = executionResult.getChildren()[FIRST_INDEX].getMessages();
		assertEquals("ihi.assert_weblogic_installed_failed", messages.get(ExecutionResult.MSG_MESSAGE));
		
		// verify mocks
		EasyMock.verify(assertionResult);		
		EasyMock.verify(assertionHelper);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(session);		
	}

	/**
	 * Test that successful child result is added if target directory exists.
	 */
	@Test
	public void testIsProductInstalledAddsSuccessfulChildResultIfDirectoryExist() {
		
		ProcessExecutionSession session = EasyMock.createMock( ProcessExecutionSession.class );

		// create assertion result mock		
		ExecutionResult assertionResult = EasyMock.createMock( ExecutionResult.class );
		EasyMock.expect(assertionResult.isSuccess()).andReturn(true).times(2);
		EasyMock.replay(assertionResult);				
				
		// activate mocks		
		EasyMock.expect(assertionHelper.assertDirectoryExist(randomWlsHome, 
				messageProvider, 
				"ihi.assert_weblogic_installed"))
				.andReturn(assertionResult);		
		EasyMock.replay(assertionHelper);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(session);		
		
		// invoke
		helper.isProductInstalled(session, executionResult, randomWlsHome);
		
		// test
		assertEquals(1, executionResult.getChildren().length);
		assertEquals(1, executionResult.getChildrenWithState(ExecutionState.SUCCESS).length);
		Map<String, String> messages = executionResult.getChildren()[FIRST_INDEX].getMessages();
		assertEquals("ihi.assert_weblogic_installed_succeed", messages.get(ExecutionResult.MSG_MESSAGE));		
		
		// verify mocks
		EasyMock.verify(assertionResult);		
		EasyMock.verify(assertionHelper);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(session);		
	}

	/**
	 * Test that installation directory info is added to child result if target directory doesn't exists.
	 */
	@Test
	public void testIsProductInstalledAddsInstallationDirectoryInfoIfDirectoryDoesntExist() {
		
		ProcessExecutionSession session = EasyMock.createMock( ProcessExecutionSession.class );

		// create assertion result mock		
		ExecutionResult assertionResult = EasyMock.createMock( ExecutionResult.class );
		EasyMock.expect(assertionResult.isSuccess()).andReturn(false).times(2);
		EasyMock.replay(assertionResult);				
				
		// activate mocks		
		EasyMock.expect(assertionHelper.assertDirectoryExist(randomWlsHome, 
				messageProvider, 
				"ihi.assert_weblogic_installed"))
				.andReturn(assertionResult);		
		EasyMock.replay(assertionHelper);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(session);		
		
		// invoke
		helper.isProductInstalled(session, executionResult, randomWlsHome);
		
		// test
		assertEquals(1, executionResult.getChildren().length);
		assertEquals(1, executionResult.getChildrenWithState(ExecutionState.SUCCESS).length);
		Map<String, String> messages = executionResult.getChildren()[FIRST_INDEX].getMessages();
		assertEquals(randomWlsHome.getAbsolutePath(), messages.get("ihi.assert_weblogic_installed_info"));
		
		// verify mocks
		EasyMock.verify(assertionResult);		
		EasyMock.verify(assertionHelper);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(session);		
	}

	/**
	 * Test that installation directory info is added to child result if target directory exists.
	 */
	@Test
	public void testIsProductInstalledAddsInstallationDirectoryInfoIfDirectoryExist() {
		
		ProcessExecutionSession session = EasyMock.createMock( ProcessExecutionSession.class );

		// create assertion result mock		
		ExecutionResult assertionResult = EasyMock.createMock( ExecutionResult.class );
		EasyMock.expect(assertionResult.isSuccess()).andReturn(true).times(2);
		EasyMock.replay(assertionResult);				
				
		// activate mocks		
		EasyMock.expect(assertionHelper.assertDirectoryExist(randomWlsHome, 
				messageProvider, 
				"ihi.assert_weblogic_installed"))
				.andReturn(assertionResult);		
		EasyMock.replay(assertionHelper);
		EasyMock.replay(coreRuntimeDirectoryProvider);
		EasyMock.replay(coreExecutionInfoProvider);
		EasyMock.replay(session);		
		
		// invoke
		helper.isProductInstalled(session, executionResult, randomWlsHome);
		
		// test
		assertEquals(1, executionResult.getChildren().length);
		assertEquals(1, executionResult.getChildrenWithState(ExecutionState.SUCCESS).length);
		Map<String, String> messages = executionResult.getChildren()[FIRST_INDEX].getMessages();
		assertEquals(randomWlsHome.getAbsolutePath(), messages.get("ihi.assert_weblogic_installed_info"));
		
		// verify mocks
		EasyMock.verify(assertionResult);		
		EasyMock.verify(assertionHelper);
		EasyMock.verify(coreRuntimeDirectoryProvider);
		EasyMock.verify(coreExecutionInfoProvider);
		EasyMock.verify(session);		
	}
	
	
	/**
	 * Tests that no model content is defined for an empty model.
	 */
	@Test
	public void testIsModelContentDefinedReturnsFalseForEmptyModel() {
		
		WeblogicInstallation model = contentMother.createEmptyWeblogicInstallation();

		// test
		assertFalse(helper.isModelContentDefined(model));
	}

	/**
	 * Tests that model content is defined for release 9 model.
	 */
	@Test
	public void testIsModelContentDefinedReturnsTrueForRelease9Model() {
		File installerFile = new File(testDirectory, TEST_SCRIPT_EXE);
		String nonExistingTargetDir = randomWlsHome.getAbsolutePath();
		
		WeblogicInstallation model = (WeblogicInstallation) contentMother.createRelease9WeblogicInstallation(
				installerFile.getAbsolutePath(), 
				nonExistingTargetDir);

		// test
		assertTrue(helper.isModelContentDefined(model));
	}

	/**
	 * Tests that model content is defined for release 10 model.
	 */
	@Test
	public void testIsModelContentDefinedReturnsTrueForRelease10Model() {
		File installerFile = new File(testDirectory, TEST_SCRIPT_EXE);
		String nonExistingTargetDir = randomWlsHome.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");		
		
		WeblogicInstallation model = (WeblogicInstallation) contentMother.createRelease10WeblogicInstallation(
				installerFile.getAbsolutePath(), 
				nonExistingTargetDir,
				localJvm.getAbsolutePath());

		// test
		assertTrue(helper.isModelContentDefined(model));
	}

	/**
	 * Tests that model content is defined for release 12 model.
	 */
	@Test
	public void testIsModelContentDefinedReturnsTrueForRelease12Model() {
		File installerFile = new File(testDirectory, TEST_SCRIPT_EXE);
		String nonExistingTargetDir = randomWlsHome.getAbsolutePath();
		File localJvm = new File(testDirectory, "jvm");		
		
		WeblogicInstallation model = (WeblogicInstallation) contentMother.createRelease12WeblogicInstallation(
				installerFile.getAbsolutePath(), 
				nonExistingTargetDir,
				localJvm.getAbsolutePath());

		// test
		assertTrue(helper.isModelContentDefined(model));
	}
	
	@Test
	public void testCreateSilentLogFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testResolveModulePath() {
		fail("Not yet implemented");
	}

}
