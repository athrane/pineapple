/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.git.command;

import static com.alpha.pineapple.execution.ExecutionResult.MSG_MESSAGE;
import static com.alpha.pineapple.plugin.git.GitConstants.BRANCH_1_0;
import static com.alpha.pineapple.plugin.git.GitConstants.BRANCH_HEAD;
import static com.alpha.pineapple.plugin.git.GitConstants.BRANCH_MASTER;
import static com.alpha.pineapple.plugin.git.GitConstants.PLUGIN_APP_CONTEXT;
import static com.alpha.pineapple.plugin.git.GitTestConstants.TEST_REPO_URI;
import static com.alpha.pineapple.plugin.git.command.CloneRepositoryCommand.BRANCH_KEY;
import static com.alpha.pineapple.plugin.git.command.CloneRepositoryCommand.DESTINATION_KEY;
import static com.alpha.pineapple.plugin.git.command.CloneRepositoryCommand.EXECUTIONRESULT_KEY;
import static com.alpha.pineapple.plugin.git.command.CloneRepositoryCommand.SESSION_KEY;
import static com.alpha.pineapple.test.matchers.PineappleMatchers.doesDirectoryExist;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.git.GitConstants;
import com.alpha.pineapple.plugin.git.session.GitSession;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.GitTestHelper;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test for the {@linkplain CloneRepositoryCommand} class.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { PLUGIN_APP_CONTEXT })
public class CloneRepositoryCommandIntegrationTest {

	/**
	 * Empty string.
	 */
	static final String EMPTY_STR = "";

	/**
	 * Object under test.
	 */
	@Resource
	Command cloneRepositoryCommand;

	/**
	 * Git test helper.
	 */
	@Resource
	GitTestHelper gitHelper;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "gitMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Context.
	 */
	Context context;

	/**
	 * Object mother for the Git model.
	 */
	ObjectMotherContent contentMother;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Git session.
	 */
	GitSession session;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	/**
	 * Mock runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	/**
	 * Random value.
	 */
	String randomRepo;

	/**
	 * Random value.
	 */
	String randomBranch;

	/**
	 * Random value.
	 */
	String randomDir;

	/**
	 * Random value.
	 */
	String randomCredId;

	@Before
	public void setUp() throws Exception {
		randomDir = RandomStringUtils.randomAlphanumeric(10);
		randomRepo = RandomStringUtils.randomAlphanumeric(10);
		randomBranch = RandomStringUtils.randomAlphanumeric(10);
		randomCredId = RandomStringUtils.randomAlphanumeric(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create session
		session = gitHelper.createDefaultSession();

		// create context
		context = new ContextBase();

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// create content mother
		contentMother = new ObjectMotherContent();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// reset plugin provider
		reset(coreRuntimeDirectoryProvider);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Assert the expected number of files in a cloned repository.
	 * 
	 * @param destDir cloned repository directory.
	 */
	void assertRepositoryFiles(File destDir) {
		assertThat(destDir, doesDirectoryExist());
		var dotGitDir = new File(destDir, ".git");
		assertEquals(1 + 2, destDir.listFiles().length);
		assertThat(dotGitDir, doesDirectoryExist());
	}

	/**
	 * Set up the directory provider with no-op resolution, i.e. the input path is
	 * is returned unchanged.
	 * 
	 * @param destDir returned path.
	 */
	void setupRuntimeDirectoryProviderWithNoOp(File destDir) {
		expect(coreRuntimeDirectoryProvider.resolveModelPath(destDir.getAbsolutePath(), result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that deploy configuration operation can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext() {
		assertNotNull(cloneRepositoryCommand);
	}

	/**
	 * Test that repository can be cloned from HEAD.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCloneFromHead() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_HEAD);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that cloning repository from HEAD contains the expected set of files.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCloneFromHeadContainsExpectedFiles() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_HEAD);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that repository can be cloned from "master" branch.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCloneFromMaster() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that cloning repository from "master" contains the expected set of
	 * files.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCloneFromMasterContainsExpectedFiles() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that repository can be cloned from branch "1.0".
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCloneFromBranch1Dot0() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_1_0);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that cloning repository from branch "1.0" contains the expected set of
	 * files.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCloneFromBranch1Dot0ContainsExpectedFiles() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_HEAD);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that repository can be cloned from empty branch.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCloneFromEmptyBranch() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, EMPTY_STR);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that cloning repository from empty branch contains the expected set of
	 * files.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCloneFromEmptyBranchContainsExpectedFiles() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_HEAD);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that cloning repository from empty branch is resolved to HEAD..
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCloneFromEmptyBranchIsResolvedToHead() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_HEAD);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		result.getMessages().get(MSG_MESSAGE).contains("Will clone repository branch: HEAD");
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that repository can be cloned from non-existing branch.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCloneFromNonExistingBranch() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, randomBranch);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that cloning repository from non-existing branch doesn contains any
	 * files.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCloneFromNonExistingBranchDoesntContainsAnyFiles() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, randomBranch);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		assertThat(destDir, doesDirectoryExist());
		var dotGitDir = new File(destDir, ".git");
		assertEquals(1, destDir.listFiles().length); // only the .git dir exists
		assertThat(dotGitDir, doesDirectoryExist());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that repository can be cloned to alternative destination.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCloneRepositoryToAlternativeDestination() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();
		setupRuntimeDirectoryProviderWithNoOp(destDir);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that repository can be cloned to blank destination.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCloneToBlankDestination() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, session.getProjectName());
		var dest = GitConstants.MODULES_EXP + session.getProjectName();

		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, EMPTY_STR);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that repository fails to clone to undefined destination.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected=CommandInitializationFailedException.class)
	public void testFailsToCloneToUndefinedDestination() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, session.getProjectName());
		var dest = GitConstants.MODULES_EXP + session.getProjectName();

		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, null);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);
	}
	
	/**
	 * Test that cloned repository can be deleted after session disconnect, meaning
	 * that the JGit APi has release all internal handles on files as part of the
	 * disconnect.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanDeleteClone() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();

		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// close Git repository through session disconnect
		session.disconnect();

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);

		// delete cloned repository
		FileUtils.deleteDirectory(destDir);
	}

	/**
	 * Test that cloned repository fails to be deleted die to lack of release of
	 * resource (fils) by JGit API, due to lacking session disconnect.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = IOException.class)
	public void testFailsToDeleteCloneWithoutSessionDisconnect() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();

		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke command
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// no release of Git resource (in repository) through lack of session disconnect

		// test
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);

		// delete cloned repository
		FileUtils.deleteDirectory(destDir);
	}

	/**
	 * Test that cloning can delete directory if it exists.
	 * 
	 * Command is invoked wit overwrite, i.e. deletion of existing local repository.
	 * Command is executed twice to delete repository when execute the second time.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanCloneTwiceAndOverwrite() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);
		var dest = destDir.getAbsolutePath();

		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke command with overwrite
		context.put(BRANCH_KEY, BRANCH_MASTER);
		context.put(DESTINATION_KEY, dest);
		context.put(EXECUTIONRESULT_KEY, result);
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test #1
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);

		// close Git repository through session disconnect to release resources (file)
		session.disconnect();

		// connect session
		session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);

		// clone and delete/overwrite
		context.put(SESSION_KEY, session);
		cloneRepositoryCommand.execute(context);

		// test #2
		assertTrue(result.isSuccess());
		assertRepositoryFiles(destDir);
		verify(coreRuntimeDirectoryProvider);
	}

}
