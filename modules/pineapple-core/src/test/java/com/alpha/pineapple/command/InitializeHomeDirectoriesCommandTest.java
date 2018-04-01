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

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
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
import com.alpha.pineapple.command.initialization.CommandInitializationFailedException;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Unit test of the class {@link InitializeHomeDirectoriesCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
public class InitializeHomeDirectoriesCommandTest {

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
	 * Mock Runtime directory resolver.
	 */
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Configuration object mother
	 */
	ObjectMotherEnvironmentConfiguration configMother;

	/**
	 * Random name.
	 */
	String randomHomeName;

	/**
	 * Random name.
	 */
	String randomConfName;

	/**
	 * Random name.
	 */
	String randomModulesName;

	/**
	 * Random name.
	 */
	String randomReportsName;

	@Before
	public void setUp() throws Exception {
		randomHomeName = RandomStringUtils.randomAlphanumeric(10);
		randomConfName = RandomStringUtils.randomAlphanumeric(10);
		randomModulesName = RandomStringUtils.randomAlphanumeric(10);
		randomReportsName = RandomStringUtils.randomAlphanumeric(10);

		// create context
		context = new ContextBase();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// configuration object mother
		configMother = new ObjectMotherEnvironmentConfiguration();

		// create command
		command = new InitializeHomeDirectoriesCommand();

		// create execution result
		executionResult = EasyMock.createMock(ExecutionResult.class);

		// create mock provider
		runtimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);

		// inject provider
		ReflectionTestUtils.setField(command, "runtimeDirectoryProvider", runtimeDirectoryProvider);

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message source
		ReflectionTestUtils.setField(command, "messageProvider", messageProvider);

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
	 * Test that command fails if execution result property is undefined.
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = CommandInitializationFailedException.class)
	public void testCommandFailsIfExecutionResultIsUndefinedInContext() throws Exception {
		// complete mock execution result setup
		EasyMock.replay(executionResult);

		// setup context
		context.put(InitializeHomeDirectoriesCommand.EXECUTIONRESULT_KEY, null);

		// execute command
		command.execute(context);

		// Verify mocks
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that command can create home directories.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateHomeDirectories() throws Exception {
		// define directories
		File homeDir = new File(testDirectory, randomHomeName);
		File configDir = new File(homeDir, randomConfName);
		File modulesDir = new File(homeDir, randomModulesName);
		File reportsDir = new File(homeDir, randomReportsName);

		// complete mock execution result setup
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.homedir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.confdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.modulesdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.reportsdir_created_info"));
		executionResult.completeAsSuccessful(EasyMock.eq(messageProvider), EasyMock.contains("irdc.succeed"));
		EasyMock.replay(executionResult);

		// complete mock provider setup
		EasyMock.expect(runtimeDirectoryProvider.getHomeDirectory()).andReturn(homeDir);
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(configDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(modulesDir);
		EasyMock.expect(runtimeDirectoryProvider.getReportsDirectory()).andReturn(reportsDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		context.put(InitializeHomeDirectoriesCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		assertTrue(homeDir.exists());
		assertTrue(configDir.exists());
		assertTrue(modulesDir.exists());
		assertTrue(reportsDir.exists());

		// Verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that command fails if home directory isn't absolute.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFailsIfHomeDirectoryIsntAbsolute() throws Exception {
		// define directories
		File homeDir = new File(randomHomeName);

		// complete mock execution result setup
		executionResult.completeAsFailure(EasyMock.eq(messageProvider),
				EasyMock.contains("irdc.homedir_notabsolute_failure"), EasyMock.isA(Object[].class));
		EasyMock.replay(executionResult);

		// complete mock provider setup
		EasyMock.expect(runtimeDirectoryProvider.getHomeDirectory()).andReturn(homeDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		context.put(InitializeHomeDirectoriesCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// Verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that command skips creation of home directory if it already exists.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSkipsCreatingHomeDirectoryIfItExists() throws Exception {
		// define directories
		File homeDir = new File(testDirectory, randomHomeName);
		FileUtils.forceMkdir(homeDir);
		File configDir = new File(homeDir, randomConfName);
		File modulesDir = new File(homeDir, randomModulesName);
		File reportsDir = new File(homeDir, randomReportsName);

		// complete mock execution result setup
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.homedir_exists_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.confdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.modulesdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.reportsdir_created_info"));
		executionResult.completeAsSuccessful(EasyMock.eq(messageProvider), EasyMock.contains("irdc.succeed"));
		EasyMock.replay(executionResult);

		// complete mock provider setup
		EasyMock.expect(runtimeDirectoryProvider.getHomeDirectory()).andReturn(homeDir);
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(configDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(modulesDir);
		EasyMock.expect(runtimeDirectoryProvider.getReportsDirectory()).andReturn(reportsDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		context.put(InitializeHomeDirectoriesCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		assertTrue(homeDir.exists());
		assertTrue(configDir.exists());
		assertTrue(modulesDir.exists());
		assertTrue(reportsDir.exists());

		// Verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that command skips creation of configuration directory if it already
	 * exists.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSkipsCreatingConfigurationDirectoryIfItExists() throws Exception {
		// define directories
		File homeDir = new File(testDirectory, randomHomeName);
		File configDir = new File(homeDir, randomConfName);
		FileUtils.forceMkdir(configDir);
		File modulesDir = new File(homeDir, randomModulesName);
		File reportsDir = new File(homeDir, randomReportsName);

		// complete mock execution result setup
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.homedir_exists_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.confdir_exists_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.modulesdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.reportsdir_created_info"));
		executionResult.completeAsSuccessful(EasyMock.eq(messageProvider), EasyMock.contains("irdc.succeed"));
		EasyMock.replay(executionResult);

		// complete mock provider setup
		EasyMock.expect(runtimeDirectoryProvider.getHomeDirectory()).andReturn(homeDir);
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(configDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(modulesDir);
		EasyMock.expect(runtimeDirectoryProvider.getReportsDirectory()).andReturn(reportsDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		context.put(InitializeHomeDirectoriesCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		assertTrue(homeDir.exists());
		assertTrue(configDir.exists());
		assertTrue(modulesDir.exists());
		assertTrue(reportsDir.exists());

		// Verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that command skips creation of modules directory if it already exists.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSkipsCreatingModulesDirectoryIfItExists() throws Exception {
		// define directories
		File homeDir = new File(testDirectory, randomHomeName);
		File configDir = new File(homeDir, randomConfName);
		File modulesDir = new File(homeDir, randomModulesName);
		FileUtils.forceMkdir(modulesDir);
		File reportsDir = new File(homeDir, randomReportsName);

		// complete mock execution result setup
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.homedir_exists_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.confdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.modulesdir_exists_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.reportsdir_created_info"));
		executionResult.completeAsSuccessful(EasyMock.eq(messageProvider), EasyMock.contains("irdc.succeed"));
		EasyMock.replay(executionResult);

		// complete mock provider setup
		EasyMock.expect(runtimeDirectoryProvider.getHomeDirectory()).andReturn(homeDir);
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(configDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(modulesDir);
		EasyMock.expect(runtimeDirectoryProvider.getReportsDirectory()).andReturn(reportsDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		context.put(InitializeHomeDirectoriesCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		assertTrue(homeDir.exists());
		assertTrue(configDir.exists());
		assertTrue(modulesDir.exists());
		assertTrue(reportsDir.exists());

		// Verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that command skips creation of reports directory if it already exists.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSkipsCreatingReportsDirectoryIfItExists() throws Exception {
		// define directories
		File runtimeDir = new File(testDirectory, randomHomeName);
		File configDir = new File(runtimeDir, randomConfName);
		File modulesDir = new File(runtimeDir, randomModulesName);
		File reportsDir = new File(runtimeDir, randomReportsName);
		FileUtils.forceMkdir(reportsDir);

		// complete mock execution result setup
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.homedir_exists_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.confdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.modulesdir_created_info"));
		executionResult.addMessage(EasyMock.contains(ExecutionResult.MSG_MESSAGE),
				EasyMock.contains("irdc.reportsdir_exists_info"));
		executionResult.completeAsSuccessful(EasyMock.eq(messageProvider), EasyMock.contains("irdc.succeed"));
		EasyMock.replay(executionResult);

		// complete mock provider setup
		EasyMock.expect(runtimeDirectoryProvider.getHomeDirectory()).andReturn(runtimeDir);
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(configDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(modulesDir);
		EasyMock.expect(runtimeDirectoryProvider.getReportsDirectory()).andReturn(reportsDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		context.put(InitializeHomeDirectoriesCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		assertTrue(runtimeDir.exists());
		assertTrue(configDir.exists());
		assertTrue(modulesDir.exists());
		assertTrue(reportsDir.exists());

		// Verify mocks
		EasyMock.verify(executionResult);
		EasyMock.verify(runtimeDirectoryProvider);
	}

}
