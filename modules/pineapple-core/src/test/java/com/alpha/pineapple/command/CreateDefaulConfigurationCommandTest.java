/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.command;

import java.io.File;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang3.RandomStringUtils;
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
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Unit test of the class {@link CreateDefaultEnvironmentConfigurationCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
public class CreateDefaulConfigurationCommandTest {

	/**
	 * Name of test module of class path.
	 */
	static final String ARCHIVE_ON_CLASSPATH = "test-archive-on-plasspath.jar";

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Object under test.
	 */
	Command command;

	/**
	 * Context.
	 */
	Context context;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Configuration object mother
	 */
	ObjectMotherEnvironmentConfiguration configMother;

	/**
	 * Random name.
	 */
	String randomName;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);

		// create context
		context = new ContextBase();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// configuration object mother
		configMother = new ObjectMotherEnvironmentConfiguration();

		// create command
		command = new CreateDefaultEnvironmentConfigurationCommand();

		// create execution result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message source
		ReflectionTestUtils.setField(command, "messageProvider", messageProvider, MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();

		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.expect(
				messageProvider.getMessage((String) EasyMock.isA(String.class), (Object[]) EasyMock.anyObject()));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.replay(messageProvider);
	}

	@After
	public void tearDown() throws Exception {
		testDirectory = null;
		configMother = null;
		executionResult = null;
		messageProvider = null;
	}

	/**
	 * Test that command fails if context is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedContext() throws Exception {
		// create context
		context = null;

		// complete mock execution result setup
		EasyMock.replay(executionResult);

		// execute command
		command.execute(context);

		// Verify mocks
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that command fails if example modules file property is undefined.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfResourcesFileKeyIsUndefinedInContext() throws Exception {
		// complete mock execution result setup
		EasyMock.replay(executionResult);

		// setup context
		// NO-OP

		// execute command
		command.execute(context);

		// Verify mocks
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that command fails if example modules file property is string.
	 */
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfExampleModulesFileKeyIsStringInContext() throws Exception {
		// complete mock execution result setup
		EasyMock.replay(executionResult);

		// setup context
		// NO-OP

		// execute command
		command.execute(context);

		// Verify mocks
		EasyMock.verify(executionResult);
	}

}
