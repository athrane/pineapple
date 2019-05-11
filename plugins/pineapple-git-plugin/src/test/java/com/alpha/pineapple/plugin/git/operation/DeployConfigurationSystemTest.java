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

package com.alpha.pineapple.plugin.git.operation;

import static com.alpha.pineapple.plugin.git.GitConstants.*;
import static com.alpha.pineapple.plugin.git.GitConstants.BRANCH_HEAD;
import static com.alpha.pineapple.plugin.git.GitConstants.PLUGIN_APP_CONTEXT;
import static com.alpha.pineapple.plugin.git.GitTestConstants.TEST_REPO_URI;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

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

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.session.SessionConnectException;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.GitTestHelper;
import com.alpha.testutils.ObjectMotherContent;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test for the <code>DeployConfiguration</code> class.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { PLUGIN_APP_CONTEXT })
public class DeployConfigurationSystemTest {

	/**
	 * Empty string.
	 */
	static final String EMPTY_STR = "";

	/**
	 * Object under test.
	 */
	@Resource
	DeployConfiguration deployOperation;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "gitMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Git test helper.
	 */
	@Resource
	GitTestHelper gitHelper;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object mother for the Git model.
	 */
	ObjectMotherContent contentMother;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

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
	 * Test that deploy configuration operation can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext() {
		assertNotNull(deployOperation);
	}

	/**
	 * Test that the operation can execute with a minimal model.
	 */
	@Test
	public void testCanExecuteWithMinimalModel() throws Exception {
		var session = gitHelper.createDefaultSession();

		// create model
		var content = contentMother.createEmptyGitModel();

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
	}

	/**
	 * Test that the operation can clone repository from "master".
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@Test
	public void testCanCloneRepositoryFromMaster() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);

		// create model
		var dest = destDir.getAbsolutePath();
		var branch = BRANCH_MASTER;
		var content = contentMother.createGitModelWithCloneCommand(branch, destDir.getAbsolutePath());

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that the operation can clone repository from branch "1.0".
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@Test
	public void testCanCloneRepositoryFromBranch1Dot0() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);

		// create model
		var dest = destDir.getAbsolutePath();
		var branch = BRANCH_1_0;
		var content = contentMother.createGitModelWithCloneCommand(branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that the operation can clone repository from empty branch. Empty branch
	 * is resolved to HEAD.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@Test
	public void testCanCloneRepositoryWithEmptyBranch() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);
		var destDir = new File(testDirectory, randomDir);

		// create model
		var dest = destDir.getAbsolutePath();
		var branch = EMPTY_STR;
		var content = contentMother.createGitModelWithCloneCommand(branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(destDir);
		replay(coreRuntimeDirectoryProvider);

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

	/**
	 * Test that the operation fails to clone non-existing repository.
	 */
	@Test(expected = SessionConnectException.class)
	public void testFailsToCloneNonexistingRepository() throws Exception {
		var uri = "https://github.com/athrane/" + randomRepo + ".git";
		gitHelper.initSessionWithNoAuth(uri, randomCredId);
	}

	/**
	 * Test that the operation fails to clone empty repository.
	 */
	@Test(expected = SessionConnectException.class)
	public void testFailsToCloneEmptyRepository() throws Exception {
		gitHelper.initSessionWithNoAuth(EMPTY_STR, randomCredId);
	}

	/**
	 * Test that the operation can clone repository and overwrite if directory
	 * exists.
	 * 
	 * Credential is defined with no user/pwd to disable authentification.
	 */
	@Test
	public void testCanCloneRepositoryAndOverwrite() throws Exception {
		var session = gitHelper.initSessionWithNoAuth(TEST_REPO_URI, randomCredId);

		// create model
		var dest = testDirectory.getAbsolutePath() + File.separator + randomDir;
		var branch = BRANCH_HEAD;
		var content = contentMother.createGitModelWithCloneCommand(branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(new File(dest));
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(new File(dest));
		replay(coreRuntimeDirectoryProvider);

		// invoke operation
		deployOperation.execute(content, session, result);

		// invoke operation again
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);
	}

}
