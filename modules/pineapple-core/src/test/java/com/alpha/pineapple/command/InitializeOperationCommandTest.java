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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Unit test of the class {@link InitializeOperationCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DirectoryTestExecutionListener.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class InitializeOperationCommandTest {
	/**
	 * Module object mother
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Configuration object mother
	 */
	ObjectMotherConfiguration configMother;

	/**
	 * Command under test.
	 */
	Command command;

	/**
	 * Mock message provider.
	 */
	MessageProvider messageProvider;

	/**
	 * Mock execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Random environment
	 */
	String randomEnvironment;

	@Before
	public void setUp() throws Exception {
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create configuration object mother
		configMother = new ObjectMotherConfiguration();

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// create command
		command = new InitializeOperationCommand();

		// create mock provider
		messageProvider = EasyMock.createMock(MessageProvider.class);

		// inject message provider
		ReflectionTestUtils.setField(command, "messageProvider", messageProvider, MessageProvider.class);

		// complete mock source initialization
		IAnswer<String> answer = new MessageProviderAnswerImpl();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
				(Object[]) EasyMock.isA(Object[].class)));
		EasyMock.expectLastCall().andAnswer(answer).anyTimes();
		EasyMock.replay(messageProvider);

		// create mock result
		executionResult = EasyMock.createMock(ExecutionResult.class);
	}

	@After
	public void tearDown() throws Exception {
		testDirectory = null;
		configMother = null;
		moduleMother = null;
		command = null;
		messageProvider = null;
		executionResult = null;
	}

	/**
	 * Complete mock module info initialization.
	 * 
	 * @param moduleDirectory
	 *            Module directory
	 * 
	 * @return module info.
	 */
	ModuleInfo completeMockModuleInfoInitialization(File moduleDirectory) {
		ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
		EasyMock.expect(moduleInfo.getDirectory()).andReturn(moduleDirectory);
		EasyMock.replay(moduleInfo);
		return moduleInfo;
	}

	/**
	 * Complete mock execution info initialization.
	 * 
	 * @param environment
	 *            Environment.
	 * @param moduleInfo
	 *            Module info.
	 * 
	 * @return execution info.
	 */
	ExecutionInfo completeMockExecutionInfoInitialization(String environment, ModuleInfo moduleInfo) {
		ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
		EasyMock.expect(info.getModuleInfo()).andReturn(moduleInfo);
		EasyMock.expect(info.getEnvironment()).andReturn(environment);
		EasyMock.replay(info);
		return info;
	}

	/**
	 * Test that command fails if context is undefined.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedContext() throws Exception {

		// setup test
		Context context = null;

		// execute command
		command.execute(context);
	}

	/**
	 * Test that {@link LoadModuleCommand#MODULE_FILE_KEY} is defined in context
	 * after execution.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleFileNameKeyIsDefinedInContextAfterExecution() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object value;
		value = context.get(InitializeOperationCommand.MODULE_FILE_KEY);
		assertNotNull(value);

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link LoadModuleCommand#MODULE_FILE_KEY} contains File object.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleFileNameKeyContainsFileObject() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object value;
		value = context.get(InitializeOperationCommand.MODULE_FILE_KEY);
		assertTrue(value instanceof File);

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link LoadModuleCommand#MODULE_FILE_KEY} contains File object
	 * which is a file.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleFileNameKeyContainsFileObjectWhichRepresentsFile() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		File file;
		file = (File) context.get(InitializeOperationCommand.MODULE_FILE_KEY);
		assertTrue(file.isFile());

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link LoadModuleCommand#MODULE_FILE_KEY} contains File object
	 * which is a file with the name "module.xml".
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleFileNameKeyContainsFileWithCorrectFileName() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		File file;
		file = (File) context.get(InitializeOperationCommand.MODULE_FILE_KEY);
		assertEquals("module.xml", file.getName());

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link LoadModuleCommand#MODULE_FILE_KEY} contains File object
	 * where the file is located at the correct path.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleFileNameKeyContainsFileWithCorrectPath() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		File file = (File) context.get(InitializeOperationCommand.MODULE_FILE_KEY);
		assertEquals(moduleDirectory, file.getParentFile());

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that command fails if the module.xml file can't be found.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandSucceedsIfModuleFileCantBeFound() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = EasyMock.createMock(ExecutionInfo.class);
		EasyMock.expect(info.getModuleInfo()).andReturn(moduleInfo);
		EasyMock.expect(info.getEnvironment()).andReturn(randomEnvironment);
		EasyMock.replay(info);

		// create execution result mock
		executionResult.completeAsSuccessful((MessageProvider) EasyMock.anyObject(), (String) EasyMock.anyObject());
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// create module without a module file
		Models model = moduleMother.createModelObject();
		moduleMother.createModuleDirectoryWithModelsDirectory(module, modulesDirectory);
		moduleMother.addModelFile(randomEnvironment, model);

		// execute command
		command.execute(context);

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that command fails if model file can't be found.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandFailsIfModelFileCantBeFound() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsFailure((MessageProvider) EasyMock.anyObject(), (String) EasyMock.anyObject(),
				(Object[]) EasyMock.anyObject());
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// create module without model
		Module moduleObject = moduleMother.createModuleObject();
		moduleMother.createModuleDirectoryWithModelsDirectory(module, modulesDirectory);
		moduleMother.addModuleFile(moduleObject);

		// execute command
		command.execute(context);

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link LoadModuleModelCommand#EXAMPLE_MODULES_DIR_KEY} is defined
	 * in context after execution.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleModuelFileNameKeyIsDefinedInContextAfterExecution() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";
		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object value;
		value = context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY);
		assertNotNull(value);

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link InitializeOperationCommand#MODULE_MODEL_FILE_KEY} contains
	 * File object.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleModelFileNameKeyContainsFileObject() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";

		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		Object value;
		value = context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY);
		assertTrue(value instanceof File);

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link InitializeOperationCommand#MODULE_MODEL_FILE_KEY} contains
	 * File object which is a file.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleModelFileNameKeyContainsFileObjectWhichRepresentsFile() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";

		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// test
		File file;
		file = (File) context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY);
		assertTrue(file.isFile());

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link InitializeOperationCommand#MODULE_MODEL_FILE_KEY} contains
	 * File object which is a file with the name of the invoked environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleModelFileNameKeyContainsFileWithCorrectFileName() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";

		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// create model file name
		StringBuilder fileName = new StringBuilder();
		fileName.append(randomEnvironment);
		fileName.append(".xml");

		// test
		File file;
		file = (File) context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY);
		assertEquals(fileName.toString(), file.getName());

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

	/**
	 * Test that {@link InitializeOperationCommand#MODULE_MODEL_FILE_KEY} contains
	 * File object where the file is located at the correct path.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testModuleModelFileNameKeyContainsFileWithCorrectPath() throws Exception {
		// create module
		File modulesDirectory = testDirectory;
		String module = "some-module";

		moduleMother.createModuleWithSingleEmptyModel(modulesDirectory, module, randomEnvironment);
		File moduleDirectory = new File(modulesDirectory, module);

		// create mocks
		ModuleInfo moduleInfo = completeMockModuleInfoInitialization(moduleDirectory);
		ExecutionInfo info = completeMockExecutionInfoInitialization(randomEnvironment, moduleInfo);

		// create execution result mock
		executionResult.completeAsSuccessful(messageProvider, "ioc.succeed");
		EasyMock.replay(executionResult);

		// setup context
		Context context = new ContextBase();
		context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);
		context.put(InitializeOperationCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		command.execute(context);

		// create expected models directory
		File modelsDir = new File(moduleDirectory, "models");

		// test
		File file;
		file = (File) context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY);
		assertEquals(modelsDir, file.getParentFile());

		// verify mocks
		EasyMock.verify(info);
		EasyMock.verify(moduleInfo);
		EasyMock.verify(executionResult);
	}

}
