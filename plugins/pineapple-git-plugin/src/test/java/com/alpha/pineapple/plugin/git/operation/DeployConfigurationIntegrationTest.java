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

import static com.alpha.pineapple.plugin.git.GitConstants.PLUGIN_APP_CONTEXT;
import static org.easymock.EasyMock.createMock;
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
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.git.model.Git;
import com.alpha.pineapple.plugin.git.session.GitSession;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherContent;

/**
 * Integration test for the <code>DeployConfiguration</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { PLUGIN_APP_CONTEXT })
public class DeployConfigurationIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	@Resource
	DeployConfiguration deployOperation;

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
	String randomTag;

	/**
	 * Random environment.
	 */
	String randomEnvironment;

	/**
	 * Random module.
	 */
	String randomModule;

	/**
	 * Random key.
	 */
	String randomKey;

	@Before
	public void setUp() throws Exception {
		randomKey = RandomStringUtils.randomAlphanumeric(10);
		randomModule = RandomStringUtils.randomAlphanumeric(10);
		randomEnvironment = RandomStringUtils.randomAlphanumeric(10);
		randomRepo = RandomStringUtils.randomAlphanumeric(10);
		randomTag = RandomStringUtils.randomAlphanumeric(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create mock session
		session = createMock(GitSession.class);

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

		// complete session initialization
		replay(session);

		// create content
		Git content = contentMother.createEmptyGitModel();

		// invoke operation
		deployOperation.execute(content, session, result);

		// test
		assertTrue(result.isSuccess());
		verify(session);
	}
	
}
