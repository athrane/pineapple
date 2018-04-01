/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.ssh.operation;

import static com.alpha.testutils.SshTestConstants.TESTSERVER_IP;
import static com.alpha.testutils.SshTestConstants.TESTSERVER_PORT;
import static com.alpha.testutils.SshTestConstants.TESTSERVER_USER;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.ssh.session.SshSession;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.substitution.VariableSubstitutionProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherSshSession;

/**
 * Integration test for the <code>DefaultOperation</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.ssh-config.xml" })
public class DefaultOperationIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object mother for the SSH model.
	 */
	ObjectMotherContent contentMother;

	/**
	 * Object under test.
	 */
	SshSession session;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	/**
	 * SshSession object mother.
	 */
	@Resource(name = "objectMotherSshSession")
	ObjectMotherSshSession sessionMother;

	/**
	 * Object under test.
	 */
	@Resource
	DefaultOperation defaultOperation;

	/**
	 * Mock Runtime directory provider. From the "integration-test" profile.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	/**
	 * Mock variable substitution provider. From the "integration-test" profile.
	 */
	@Resource
	VariableSubstitutionProvider coreVariableSubstitutionProvider;

	/**
	 * Local directory (on host) shared between unit test and VM.
	 */
	File localSharedTestDirectory;

	/**
	 * remote directory (on guest VM) shared between unit test and VM.
	 */
	String remoteSharedTestDirectory;

	/**
	 * Local directory (on host) used to stage file prior to copy.
	 */
	File localStageDirectory;

	/**
	 * Random file name.
	 */
	String randomFileName;

	/**
	 * Random file name.
	 */
	String randomFileName2;

	/**
	 * Random file name.
	 */
	String randomFileName3;

	/**
	 * Random file name.
	 */
	String randomFileName4;

	@Before
	public void setUp() throws Exception {
		randomFileName = RandomStringUtils.randomAlphabetic(10) + ".txt";
		randomFileName2 = RandomStringUtils.randomAlphabetic(10) + ".txt";
		randomFileName3 = RandomStringUtils.randomAlphabetic(10) + ".txt";
		randomFileName4 = RandomStringUtils.randomAlphabetic(10) + ".txt";

		// create mother's
		contentMother = new ObjectMotherContent();

		// create session
		session = sessionMother.createConnectedSshSessionWithPasswordAuthentication(TESTSERVER_IP, TESTSERVER_PORT,
				TESTSERVER_USER, TESTSERVER_USER);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// define and local shared vagrant directory
		localSharedTestDirectory = testDirectory;

		// define remote shared vagrant directory
		remoteSharedTestDirectory = contentMother.resolveRemoteSharedDirectory_OnLinux(testDirectory);

		// define local stage directory
		localStageDirectory = new File(testDirectory, "local-stage");
		if (localStageDirectory.exists())
			FileUtils.deleteDirectory(localStageDirectory);
		localStageDirectory.mkdirs();

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// reset plugin provider
		EasyMock.reset(coreRuntimeDirectoryProvider);
		EasyMock.reset(coreVariableSubstitutionProvider);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that TestOperation can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext() {
		assertNotNull(defaultOperation);
	}

	/**
	 * Test that operation can execute with a minimal model.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {
		// create content
		Object content = contentMother.createEmptySSH();

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that operation can copy a single file. With variable substitution
	 * enabled.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanCopySingleFileWithVariableSubstution() throws Exception {
		// create local test file
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

		// create processed file
		File processedFile = new File(localStageDirectory, randomFileName2);
		FileUtils.write(processedFile, randomFileName2);

		// create remote file name
		String remoteFileName = new StringBuilder().append(remoteSharedTestDirectory).append("/").append(randomFileName)
				.toString();

		// create FTP'ed file name as it appears in the local shared VM directory
		File createdFile = new File(localSharedTestDirectory, randomFileName);

		// setup mocks
		expect(coreVariableSubstitutionProvider.createSubstitutedFile(EasyMock.isA(File.class),
				EasyMock.isA(Session.class), EasyMock.isA(ExecutionResult.class))).andReturn(processedFile);
		replay(coreVariableSubstitutionProvider);
		expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class))).andReturn(false).anyTimes();
		replay(coreRuntimeDirectoryProvider);

		// create content
		Object content = contentMother.createModelWithSingleFileCopy(localFile.getAbsolutePath(), remoteFileName, true);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(createdFile.exists());
		assertTrue(createdFile.isFile());
		verify(coreVariableSubstitutionProvider);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that operation can copy a single file. With variable substitution
	 * disabled.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanCopySingleFileWithVariableSubstutionDisabled() throws Exception {
		// create local test file
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

		// create processed file
		File processedFile = new File(localStageDirectory, randomFileName2);
		FileUtils.write(processedFile, randomFileName2);

		// create remote file name
		String remoteFileName = new StringBuilder().append(remoteSharedTestDirectory).append("/").append(randomFileName)
				.toString();

		// create FTP'ed file name as it appears in the local shared VM directory
		File createdFile = new File(localSharedTestDirectory, randomFileName);

		// setup mocks
		replay(coreVariableSubstitutionProvider);
		expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class))).andReturn(false).anyTimes();
		replay(coreRuntimeDirectoryProvider);

		// create content
		Object content = contentMother.createModelWithSingleFileCopy(localFile.getAbsolutePath(), remoteFileName,
				false);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(createdFile.exists());
		assertTrue(createdFile.isFile());
		verify(coreVariableSubstitutionProvider);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that operation can copy a two files.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanCopyTwoFiles() throws Exception {
		// create local test files
		File localFile = new File(localStageDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);
		File localFile2 = new File(localStageDirectory, randomFileName2);
		FileUtils.write(localFile2, randomFileName2);

		// create processed files
		File processedFile = new File(localStageDirectory, randomFileName3);
		FileUtils.write(processedFile, randomFileName3);
		File processedFile2 = new File(localStageDirectory, randomFileName3);
		FileUtils.write(processedFile, randomFileName3);

		// create remote file name
		String remoteFileName = new StringBuilder().append(remoteSharedTestDirectory).append("/").append(randomFileName)
				.toString();

		// create remote file name
		String remoteFileName2 = new StringBuilder().append(remoteSharedTestDirectory).append("/")
				.append(randomFileName2).toString();

		// create FTP'ed file name as it appears in the local shared VM directory
		File createdFile = new File(localSharedTestDirectory, randomFileName);
		File createdFile2 = new File(localSharedTestDirectory, randomFileName2);

		// setup mocks
		expect(coreVariableSubstitutionProvider.createSubstitutedFile(EasyMock.isA(File.class),
				EasyMock.isA(Session.class), EasyMock.isA(ExecutionResult.class))).andReturn(processedFile);
		expect(coreVariableSubstitutionProvider.createSubstitutedFile(EasyMock.isA(File.class),
				EasyMock.isA(Session.class), EasyMock.isA(ExecutionResult.class))).andReturn(processedFile2);
		replay(coreVariableSubstitutionProvider);
		expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(isA(String.class))).andReturn(false).anyTimes();
		replay(coreRuntimeDirectoryProvider);

		// create content
		Object content = contentMother.createModelWithTwoFilesToCopy(localFile.getAbsolutePath(), remoteFileName,
				localFile2.getAbsolutePath(), remoteFileName2);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
		assertTrue(createdFile.exists());
		assertTrue(createdFile.isFile());
		assertTrue(createdFile2.exists());
		assertTrue(createdFile2.isFile());
		verify(coreVariableSubstitutionProvider);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that operation fails if attempt to copy non existing single file.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCopyOfNonexistingFileFails() throws Exception {
		// create local test file
		File localFile = new File(testDirectory, randomFileName);
		// FileUtils.write(localFile, randomFileName);

		// create remote file name
		String remoteFileName = new StringBuilder().append(remoteSharedTestDirectory).append("/").append(randomFileName)
				.toString();

		// create FTP'ed file name as it appears in the local shared VM directory
		File createdFile = new File(localSharedTestDirectory, randomFileName);

		// create content
		Object content = contentMother.createModelWithSingleFileCopy(localFile.getAbsolutePath(), remoteFileName, true);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertEquals(ExecutionState.FAILURE, result.getState());
		assertFalse(createdFile.exists());
	}

	/**
	 * Test that operation can execute a remote command.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanExecuteRemoteCommand() throws Exception {

		// create content
		Object content = contentMother.createModelWithSingleExecuteCommand("ls -an");

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertEquals(ExecutionState.SUCCESS, result.getState());
	}

	/**
	 * Test that operation fails if remote command execution fails.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanExecuteRemoteCommandFailsWithIllegalCommand() throws Exception {

		// create content with illegal command
		Object content = contentMother.createModelWithSingleExecuteCommand(randomFileName);

		// invoke operation
		defaultOperation.execute(content, session, result);

		// test
		assertEquals(ExecutionState.FAILURE, result.getState());
	}

}
