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

import static com.alpha.pineapple.plugin.git.GitConstants.BRANCH_1_0;
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
import com.alpha.pineapple.plugin.git.model.Git;
import com.alpha.pineapple.plugin.git.session.GitSession;
import com.alpha.pineapple.plugin.git.session.GitSessionImpl;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>DeployConfiguration</code> class.
 */
@ActiveProfiles("integration-test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { PLUGIN_APP_CONTEXT })
public class DeployConfigurationSystemTest {

	/**
	 * Object under test.
	 */
	@Resource
	DeployConfiguration deployOperation;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Current test directory.
	 */
	File testDirectory;
	
	/**
	 * Object mother for the docker model.
	 */
	ObjectMotherContent contentMother;

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
	 * Random key.
	 */
	String randomDir;

	@Before
	public void setUp() throws Exception {
		randomDir = RandomStringUtils.randomAlphanumeric(10);
		randomRepo = RandomStringUtils.randomAlphanumeric(10);
		randomBranch = RandomStringUtils.randomAlphanumeric(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create mock session
		session = createDefaultSession();

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// create content mother
		contentMother = new ObjectMotherContent();

		// reset plugin provider
		reset(coreRuntimeDirectoryProvider);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create connected Git session with default settings: 
	 * 
	 * @return connected Git session with default settings.
	 * 
	 * @throws Exception if session creation fails.
	 */
	public GitSession createDefaultSession() throws Exception {
		GitSession session = new GitSessionImpl(messageProvider);
		return session;
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
		session = createDefaultSession();

		// create model
		Git content = contentMother.createEmptyGitModel();

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
	}

	/**
	 * Test that the operation can clone repository from HEAD.
	 */
	@Test
	public void testCanCloneRepositoryFromHead() throws Exception {
		session = createDefaultSession();
				
		// create model
		String dest = testDirectory.getAbsolutePath();
		String branch = BRANCH_HEAD;
		String uri = TEST_REPO_URI;
		Git content = contentMother.createGitModelWithCloneCommand(uri, branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(testDirectory);		
		replay(coreRuntimeDirectoryProvider);
		
		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);		
	}

	/**
	 * Test that the operation can clone repository from branch "1.0".
	 */
	@Test
	public void testCanCloneRepositoryFrom1Dot0() throws Exception {
		session = createDefaultSession();

		// create model
		String dest = testDirectory.getAbsolutePath();
		String branch = BRANCH_1_0;
		String uri = TEST_REPO_URI;
		Git content = contentMother.createGitModelWithCloneCommand(uri, branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(testDirectory);		
		replay(coreRuntimeDirectoryProvider);
		
		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);		
	}

	/**
	 * Test that the operation can clone repository from empty branch.
	 */
	@Test
	public void testCanCloneRepositoryWithEmptyBranch() throws Exception {
		session = createDefaultSession();

		// create model
		String dest = testDirectory.getAbsolutePath();
		String branch = "";
		String uri = TEST_REPO_URI;
		Git content = contentMother.createGitModelWithCloneCommand(uri, branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(testDirectory);		
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
	@Test
	public void testFailsToCloneNonexistingRepository() throws Exception {
		session = createDefaultSession();

		// create model
		String dest = testDirectory.getAbsolutePath();
		String branch = BRANCH_HEAD;
		String uri = "https://github.com/athrane/" + randomRepo + ".git";
		Git content = contentMother.createGitModelWithCloneCommand(uri, branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(testDirectory);		
		replay(coreRuntimeDirectoryProvider);
		
		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isFailed());
		verify(coreRuntimeDirectoryProvider);						
	}

	/**
	 * Test that the operation fails to clone empty repository.
	 */
	@Test
	public void testFailsToCloneEmptyRepository() throws Exception {
		session = createDefaultSession();

		// create model
		String dest = testDirectory.getAbsolutePath();
		String branch = BRANCH_HEAD;
		String uri = "";
		Git content = contentMother.createGitModelWithCloneCommand(uri, branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(testDirectory);		
		replay(coreRuntimeDirectoryProvider);
		
		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isFailed());
		verify(coreRuntimeDirectoryProvider);						
	}
	
	/**
	 * Test that the operation fails to clone repository with non-existing branch.
	 */
	@Test
	public void testCanCloneRepositoryWithNonexistingBranch() throws Exception {
		session = createDefaultSession();

		// create model
		String dest = testDirectory.getAbsolutePath();
		String branch = randomBranch;
		String uri = TEST_REPO_URI;
		Git content = contentMother.createGitModelWithCloneCommand(uri, branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(testDirectory);		
		replay(coreRuntimeDirectoryProvider);
		
		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);								
	}

	/**
	 * Test that the operation can clone repository to other destination.
	 */
	@Test
	public void testCanCloneRepositoryToOtherDestination() throws Exception {
		session = createDefaultSession();
				
		// create model
		String dest = testDirectory.getAbsolutePath() + File.separator+ randomDir;
		String branch = BRANCH_HEAD;
		String uri = TEST_REPO_URI;
		Git content = contentMother.createGitModelWithCloneCommand(uri, branch, dest);

		// complete runtime provider setup
		expect(coreRuntimeDirectoryProvider.resolveModelPath(dest, result)).andReturn(new File(dest));		
		replay(coreRuntimeDirectoryProvider);
		
		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(coreRuntimeDirectoryProvider);		
	}
	
}
