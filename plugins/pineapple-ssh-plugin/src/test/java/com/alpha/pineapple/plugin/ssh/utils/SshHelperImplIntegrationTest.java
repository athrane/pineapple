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

package com.alpha.pineapple.plugin.ssh.utils;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
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

import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Integration test for the <code>SshHelperImpl</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DirectoryTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.plugin.ssh-config.xml" })
public class SshHelperImplIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	/**
	 * Object under test.
	 */
	@Resource
	SshHelper sshHelper;

	/**
	 * Mock Runtime directory provider. From the "integration-test" profile.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	/**
	 * Mock Execution info provider. From the "integration-test" profile.
	 */
	@Resource
	ExecutionInfoProvider coreExecutionInfoProvider;

	/**
	 * Random file name.
	 */
	String randomFileName;

	@Before
	public void setUp() throws Exception {

		randomFileName = RandomStringUtils.randomAlphabetic(10) + ".txt";

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create execution result
		result = new ExecutionResultImpl("Root result");

		// reset plugin provider
		EasyMock.reset(coreRuntimeDirectoryProvider);
		EasyMock.reset(coreExecutionInfoProvider);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that helper can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext() {
		assertNotNull(sshHelper);
	}

	/**
	 * Test if local file is valid
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsLocalFileValid() throws Exception {

		// create and write local test file
		File localFile = new File(testDirectory, randomFileName);
		FileUtils.write(localFile, randomFileName);

		// test
		assertTrue(sshHelper.isLocalFileValid(localFile, result));
	}

	/**
	 * Test if local file test fails if local doesn't exist.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testIsLocalFileValidFailsIfFileDoesntExist() throws Exception {

		// create local test file
		File localFile = new File(testDirectory, randomFileName);

		// test
		assertFalse(sshHelper.isLocalFileValid(localFile, result));
	}

	/**
	 * Test that same file name is returned as input, if input doesn't start with
	 * modulepath prefix.
	 */
	@Test
	public void testResolveModulePathReturnInputIfPahtDoesntStartWithPrefix() {
		// create local test file
		File localFile = new File(testDirectory, randomFileName);

		// test
		assertEquals(localFile.getAbsolutePath(), sshHelper.resolveModulePath(result, localFile.getAbsolutePath()));
	}

	/**
	 * Test that module path is resolved.
	 */
	@Test
	public void testResolveModulePath() {
		String localFile = new StringBuilder().append("modulepath:").append(randomFileName).toString();

		// setup mocks
		File resolvedPath = new File(testDirectory, randomFileName);
		ModuleInfo moduleInfo = createMock(ModuleInfo.class);
		replay(moduleInfo);
		ExecutionInfo executionInfo = createMock(ExecutionInfo.class);
		expect(executionInfo.getModuleInfo()).andReturn(moduleInfo);
		replay(executionInfo);
		expect(coreRuntimeDirectoryProvider.startsWithModulePathPrefix(localFile)).andReturn(true);
		expect(coreRuntimeDirectoryProvider.resolveModelPath(localFile, moduleInfo)).andReturn(resolvedPath);
		replay(coreRuntimeDirectoryProvider);
		expect(coreExecutionInfoProvider.get(result)).andReturn(executionInfo);
		replay(coreExecutionInfoProvider);

		// test
		assertEquals(resolvedPath.getAbsolutePath(), sshHelper.resolveModulePath(result, localFile));
		verify(coreRuntimeDirectoryProvider);
		verify(coreExecutionInfoProvider);
		verify(moduleInfo);
	}

}
