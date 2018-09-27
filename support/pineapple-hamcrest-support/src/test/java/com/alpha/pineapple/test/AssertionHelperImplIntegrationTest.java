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

package com.alpha.pineapple.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * integration test of the {@link AssertionHelperImpl} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.hamcrest-config.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
public class AssertionHelperImplIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource
	AssertionHelper asserterHelper;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Test directory.
	 */
	File directory;

	/**
	 * Random directory name.
	 */
	String randomDirName;

	/**
	 * Random file name.
	 */
	String randomFileName;

	@Before
	public void setUp() throws Exception {

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create mock result
		executionResult = new ExecutionResultImpl("root");

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();

		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
				(Object[]) EasyMock.isA(Object[].class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();

		EasyMock.replay(messageProvider);

		// initialize random file name
		randomFileName = RandomStringUtils.randomAlphabetic(10);

		// initialize directory
		randomDirName = RandomStringUtils.randomAlphabetic(10);
		directory = new File(testDirectory, randomDirName);
	}

	@After
	public void tearDown() throws Exception {
		asserterHelper = null;
		executionResult = null;
		testDirectory = null;
	}

	/**
	 * Test that existing directory is a positive match
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testExistingDirectoryIsPositiveMatch() throws Exception {

		// initialize directory
		FileUtils.forceMkdir(directory);

		// test
		ExecutionResult result = asserterHelper.assertDirectoryExist(directory, messageProvider, "key",
				executionResult);
		assertTrue(result.getState() == ExecutionState.SUCCESS);
	}

	/**
	 * Test non existing directory is negative match.
	 */
	@Test
	public void testNonExistingDirectoryIsNegativeMatch() {

		// test
		ExecutionResult result = asserterHelper.assertDirectoryExist(directory, messageProvider, "key",
				executionResult);
		assertTrue(result.getState() != ExecutionState.SUCCESS);
	}

	/**
	 * Test file is negative match.
	 * 
	 * @throws Exception
	 *             If test fails
	 */
	@Test
	public void testFileIsFailure() throws Exception {

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// create file content
		Collection<String> lines = new ArrayList<String>();
		lines.add("<hello-pineapple />");

		// write the file
		FileUtils.writeLines(file, lines);

		// test
		ExecutionResult result = asserterHelper.assertDirectoryExist(file, messageProvider, "key", executionResult);
		assertTrue(result.getState() == ExecutionState.FAILURE);
	}

	/**
	 * Test that existing file is a positive match
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testExistingFileIsPositiveMatch() throws Exception {

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// create file content
		Collection<String> lines = new ArrayList<String>();
		lines.add("<hello-pineapple />");

		// write the file
		FileUtils.writeLines(file, lines);

		// test
		ExecutionResult result = asserterHelper.assertFileExist(file, messageProvider, "key", executionResult);
		assertTrue(result.getState() == ExecutionState.SUCCESS);
	}

	/**
	 * Test that non existing file is a negative match
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testNonExistingFileIsFailure() throws Exception {

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// test
		ExecutionResult result = asserterHelper.assertFileExist(file, messageProvider, "key", executionResult);
		assertTrue(result.getState() == ExecutionState.FAILURE);
	}

	/**
	 * Test that existing file is a negative match
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testExistingFileIsNegativeMatch() throws Exception {

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// create file content
		Collection<String> lines = new ArrayList<String>();
		lines.add("<hello-pineapple />");

		// write the file
		FileUtils.writeLines(file, lines);

		// test
		ExecutionResult result = asserterHelper.assertFileDoesntExist(file, messageProvider, "key", executionResult);
		assertTrue(result.getState() == ExecutionState.FAILURE);
	}

	/**
	 * Test that non existing file is a positive match
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testNonExistingFileIsPositiveMatch() throws Exception {

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// test
		ExecutionResult result = asserterHelper.assertFileDoesntExist(file, messageProvider, "key", executionResult);
		assertTrue(result.getState() == ExecutionState.SUCCESS);
	}

}
