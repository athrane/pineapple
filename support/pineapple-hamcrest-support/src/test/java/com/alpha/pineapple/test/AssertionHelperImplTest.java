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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Unit test of the {@link AssertionHelperImpl} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
public class AssertionHelperImplTest {

	/**
	 * Object under test.
	 */
	AssertionHelper helper;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Mock asserter.
	 */
	Asserter asserter;

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

		// create helper
		helper = new AssertionHelperImpl();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// create mock asserter
		asserter = EasyMock.createMock(Asserter.class);

		// inject asserter
		ReflectionTestUtils.setField(helper, "asserter", asserter);

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
		helper = null;
		executionResult = null;
		testDirectory = null;
	}

	/**
	 * Test that existing directory can be asserted.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testExistingDirectoryCanBeAsserted() throws Exception {

		// complete mock initialization
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		EasyMock.replay(childResult);
		EasyMock.replay(executionResult);

		asserter.setExecutionResult(executionResult);
		EasyMock.expect(asserter.assertObject((File) EasyMock.isA(File.class), (Matcher) EasyMock.isA(Matcher.class),
				(String) EasyMock.isA(String.class))).andReturn(childResult);
		EasyMock.replay(asserter);

		// initialize directory
		FileUtils.forceMkdir(directory);

		// test
		ExecutionResult result = helper.assertDirectoryExist(directory, messageProvider, "key", executionResult);
		assertEquals(result, childResult);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that existing file can be asserted.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testExistingfileCanBeAsserted() throws Exception {

		// complete mock initialization
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		EasyMock.replay(childResult);
		EasyMock.replay(executionResult);

		asserter.setExecutionResult(executionResult);
		EasyMock.expect(asserter.assertObject((File) EasyMock.isA(File.class), (Matcher) EasyMock.isA(Matcher.class),
				(String) EasyMock.isA(String.class))).andReturn(childResult);
		EasyMock.replay(asserter);

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// test
		ExecutionResult result = helper.assertFileExist(file, messageProvider, "key", executionResult);
		assertEquals(result, childResult);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(childResult);
	}

	/**
	 * Test that non existing file can be asserted.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testnonExistingfileCanBeAsserted() throws Exception {

		// complete mock initialization
		ExecutionResult childResult = EasyMock.createMock(ExecutionResult.class);
		EasyMock.replay(childResult);
		EasyMock.replay(executionResult);

		asserter.setExecutionResult(executionResult);
		EasyMock.expect(asserter.assertObject((File) EasyMock.isA(File.class), (Matcher) EasyMock.isA(Matcher.class),
				(String) EasyMock.isA(String.class))).andReturn(childResult);
		EasyMock.replay(asserter);

		// initialize file
		File file = new File(this.testDirectory, randomFileName);

		// test
		ExecutionResult result = helper.assertFileDoesntExist(file, messageProvider, "key", executionResult);
		assertEquals(result, childResult);

		// verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(asserter);
		EasyMock.verify(childResult);
	}

}
