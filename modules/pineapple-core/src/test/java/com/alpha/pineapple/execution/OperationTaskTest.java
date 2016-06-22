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

package com.alpha.pineapple.execution;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.easymockutils.MessageProviderAnswerImpl;
import com.alpha.pineapple.command.InitializeOperationCommand;
import com.alpha.pineapple.command.InvokePluginsCommand;
import com.alpha.pineapple.command.UnmarshallJAXBObjectsCommand;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.module.ModuleInfo;

/**
 * Unit test of the class <code>OperationTaskImpl</code>.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class OperationTaskTest {

    /**
     * Object under test.
     */
    OperationTask operationTask;

    /**
     * Mock message provider for I18N support.
     */
    MessageProvider messageProvider;

    /**
     * Mock command runner
     */
    CommandRunner commandRunner;

    /**
     * Mock initialize operation command.
     */
    Command initializeOperationCommand;

    /**
     * Unmarshall JAXB objects command.
     */
    @Resource
    Command unmarshallJAXBObjectsCommand;

    /**
     * Mock invoke plugins command.
     */
    Command invokePluginsCommand;

    /**
     * Mock execution info.
     */
    ExecutionInfo executionInfo;

    /**
     * Mock execution context repository.
     */
    ExecutionContextRepository executionContextRepository;

    @Before
    public void setUp() throws Exception {

	// create task
	operationTask = new OperationTaskImpl();

	// create mock provider
	messageProvider = EasyMock.createMock(MessageProvider.class);

	// inject message provider
	ReflectionTestUtils.setField(operationTask, "messageProvider", messageProvider, MessageProvider.class);

	// complete mock source initialization
	IAnswer<String> answer = new MessageProviderAnswerImpl();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.expect(messageProvider.getMessage((String) EasyMock.isA(String.class),
		(Object[]) EasyMock.isA(Object[].class)));
	EasyMock.expectLastCall().andAnswer(answer).anyTimes();
	EasyMock.replay(messageProvider);

	// create mocks
	commandRunner = EasyMock.createMock(CommandRunner.class);
	initializeOperationCommand = EasyMock.createMock(Command.class);
	unmarshallJAXBObjectsCommand = EasyMock.createMock(Command.class);
	invokePluginsCommand = EasyMock.createMock(Command.class);
	executionContextRepository = EasyMock.createMock(ExecutionContextRepository.class);

	// inject
	ReflectionTestUtils.setField(operationTask, "commandRunner", commandRunner);
	ReflectionTestUtils.setField(operationTask, "initializeOperationCommand", initializeOperationCommand);
	ReflectionTestUtils.setField(operationTask, "unmarshallJAXBObjectsCommand", unmarshallJAXBObjectsCommand);
	ReflectionTestUtils.setField(operationTask, "invokePluginsCommand", invokePluginsCommand);
	ReflectionTestUtils.setField(operationTask, "executionContextRepository", executionContextRepository);
    }

    @After
    public void tearDown() throws Exception {
	operationTask = null;
    }

    /**
     * Verify mocks
     * 
     * @param initCmdsResult
     *            mock command result
     * @param operationResult
     *            mock operation result
     * @param context
     *            mock command contenxt.
     */
    void verifyMocks(ExecutionResult initCmdsResult, ExecutionResult operationResult, Context context) {
	EasyMock.verify(operationResult);
	EasyMock.verify(initCmdsResult);
	EasyMock.verify(context);
	EasyMock.verify(executionInfo);
	EasyMock.verify(commandRunner);
	EasyMock.verify(executionContextRepository);
    }

    /**
     * Test task fails if execution info is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testExecuteFailsIfExecutionInfoIsNull() {
	// execute
	operationTask.execute(null);
    }

    /**
     * Test task executes with expected invocations.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteTask() {
	final String MODULE_ID = "some-module-id";
	Object SOMETING = null; // something returned by the context.
	final String SOME_MESSAGE = "some-message";

	// messages returned by cmd results
	Map<String, String> initCmdMessages = EasyMock.createMock(Map.class);
	EasyMock.expect(initCmdMessages.get(ExecutionResult.MSG_MESSAGE)).andReturn(SOME_MESSAGE).times(3);
	EasyMock.replay(initCmdMessages);

	// execution result returned by command runner for running
	// initialization commands
	ExecutionResult initCmdsResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(initCmdsResult.getMessages()).andReturn(initCmdMessages).times(3);
	EasyMock.replay(initCmdsResult);

	// create mock execution result
	ExecutionResult operationResult = EasyMock.createMock(ExecutionResult.class);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, SOME_MESSAGE);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, SOME_MESSAGE);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, SOME_MESSAGE);
	EasyMock.replay(operationResult);

	// create mock module info
	ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
	EasyMock.expect(moduleInfo.getId()).andReturn(MODULE_ID).times(2);
	EasyMock.expect(moduleInfo.isDescriptorDefined()).andReturn(true);
	EasyMock.replay(moduleInfo);

	// create mock execution info
	executionInfo = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo).times(3);
	EasyMock.expect(executionInfo.getResult()).andReturn(operationResult).times(4);
	EasyMock.replay(executionInfo);

	// create context
	Context context = EasyMock.createMock(Context.class);
	EasyMock.expect(context.get(InitializeOperationCommand.MODULE_FILE_KEY)).andReturn(SOMETING);
	EasyMock.expect(context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, SOMETING)).andReturn(SOMETING);
	EasyMock.expect(context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, Module.class.getPackage()))
		.andReturn(SOMETING);
	EasyMock.expect(context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY)).andReturn(SOMETING);
	EasyMock.expect(context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY)).andReturn(SOMETING);
	EasyMock.expect(context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, SOMETING)).andReturn(SOMETING);
	EasyMock.expect(context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, Models.class.getPackage()))
		.andReturn(SOMETING);
	EasyMock.expect(context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY)).andReturn(SOMETING);
	EasyMock.expect(context.put(InvokePluginsCommand.MODULE_KEY, SOMETING)).andReturn(SOMETING);
	EasyMock.expect(context.put(InvokePluginsCommand.MODULE_MODEL_KEY, SOMETING)).andReturn(SOMETING);
	EasyMock.expect(context.put(InvokePluginsCommand.EXECUTION_INFO_KEY, executionInfo)).andReturn(SOMETING);
	EasyMock.replay(context);

	// create mock execution context repository
	executionContextRepository.register(executionInfo, context);
	executionContextRepository.unregister(context);
	EasyMock.replay(executionContextRepository);

	// complete mock command runner setup
	commandRunner.setExecutionResult((ExecutionResult) EasyMock.isA(ExecutionResult.class));
	EasyMock.expect(commandRunner.createContext()).andReturn(context);
	EasyMock.expect(commandRunner.lastExecutionSucceeded()).andReturn(true).times(6);
	EasyMock.expect(commandRunner.run(initializeOperationCommand,
		messageProvider.getMessage("aot.initialize_operation_info"), context)).andReturn(initCmdsResult);
	EasyMock.expect(commandRunner.run(unmarshallJAXBObjectsCommand,
		messageProvider.getMessage("aot.load_module_info"), context)).andReturn(initCmdsResult);
	EasyMock.expect(commandRunner.run(unmarshallJAXBObjectsCommand,
		messageProvider.getMessage("aot.load_modulemodel_info"), context)).andReturn(initCmdsResult);
	EasyMock.expect(commandRunner.run(invokePluginsCommand, operationResult, context)).andReturn(initCmdsResult);
	EasyMock.replay(commandRunner);

	// execute
	operationTask.execute(executionInfo);

	verifyMocks(initCmdsResult, operationResult, context);
    }

    /**
     * Test task executes with expected invocations with undefined module.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteTaskWithUndefinedModule() {
	final String MODULE_ID = "some-module-id";
	Object SOMETING = null; // something returned by the context.
	final String SOME_MESSAGE = "some-message";

	// messages returned by cmd results
	Map<String, String> initCmdMessages = EasyMock.createMock(Map.class);
	EasyMock.expect(initCmdMessages.get(ExecutionResult.MSG_MESSAGE)).andReturn(SOME_MESSAGE).times(3);
	EasyMock.replay(initCmdMessages);

	// execution result returned by command runner for running
	// initialization commands
	ExecutionResult initCmdsResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(initCmdsResult.getMessages()).andReturn(initCmdMessages).times(2);
	EasyMock.replay(initCmdsResult);

	// create mock execution result
	ExecutionResult operationResult = EasyMock.createMock(ExecutionResult.class);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, SOME_MESSAGE);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, SOME_MESSAGE);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, "aot.null_module_info");
	EasyMock.replay(operationResult);

	// create mock module info
	ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
	EasyMock.expect(moduleInfo.getId()).andReturn(MODULE_ID).times(3);
	EasyMock.expect(moduleInfo.isDescriptorDefined()).andReturn(false);
	EasyMock.replay(moduleInfo);

	// create mock execution info
	executionInfo = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo).times(4);
	EasyMock.expect(executionInfo.getResult()).andReturn(operationResult).times(4);
	EasyMock.replay(executionInfo);

	// create context
	Context context = EasyMock.createMock(Context.class);
	EasyMock.expect(context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY)).andReturn(SOMETING);
	EasyMock.expect(context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, SOMETING)).andReturn(SOMETING);
	EasyMock.expect(context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, Models.class.getPackage()))
		.andReturn(SOMETING);
	EasyMock.expect(context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY)).andReturn(SOMETING);
	EasyMock.expect(context.put(EasyMock.eq(InvokePluginsCommand.MODULE_KEY), EasyMock.isA(Module.class)))
		.andReturn(SOMETING);
	EasyMock.expect(context.put(InvokePluginsCommand.MODULE_MODEL_KEY, SOMETING)).andReturn(SOMETING);
	EasyMock.expect(context.put(InvokePluginsCommand.EXECUTION_INFO_KEY, executionInfo)).andReturn(SOMETING);
	EasyMock.replay(context);

	// create mock execution context repository
	executionContextRepository.register(executionInfo, context);
	executionContextRepository.unregister(context);
	EasyMock.replay(executionContextRepository);

	// complete mock command runner setup
	commandRunner.setExecutionResult((ExecutionResult) EasyMock.isA(ExecutionResult.class));
	EasyMock.expect(commandRunner.createContext()).andReturn(context);
	EasyMock.expect(commandRunner.lastExecutionSucceeded()).andReturn(true).times(4);
	EasyMock.expect(commandRunner.run(initializeOperationCommand,
		messageProvider.getMessage("aot.initialize_operation_info"), context)).andReturn(initCmdsResult);
	EasyMock.expect(commandRunner.run(unmarshallJAXBObjectsCommand,
		messageProvider.getMessage("aot.load_modulemodel_info"), context)).andReturn(initCmdsResult);
	EasyMock.expect(commandRunner.run(invokePluginsCommand, operationResult, context)).andReturn(initCmdsResult);
	EasyMock.replay(commandRunner);

	// execute
	operationTask.execute(executionInfo);

	verifyMocks(initCmdsResult, operationResult, context);
    }

    /**
     * Test task aborts if initialize operation command fails.
     * 
     * It is assumed that the command runner captured an exception, which is
     * retrieved at CHECK POINT #1.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testTaskAbortsIfOperationInitializationCmdFails_WithException() {
	final String PATH = "some-path";
	final String MODULE_ID = "some-module-id";
	final String ENVIRONMENT = "some-environment";
	Object SOMETING = null; // something returned by the context.
	final String SOME_MESSAGE = "some-message";

	// messages returned by cmd results
	Map<String, String> initCmdMessages = EasyMock.createMock(Map.class);
	EasyMock.expect(initCmdMessages.get(ExecutionResult.MSG_MESSAGE)).andReturn(SOME_MESSAGE);
	EasyMock.expect(initCmdMessages.containsKey(ExecutionResult.MSG_STACKTRACE)).andReturn(true); // CHECK
												      // POINT
												      // #1
	EasyMock.expect(initCmdMessages.get(ExecutionResult.MSG_STACKTRACE)).andReturn(SOME_MESSAGE);
	EasyMock.replay(initCmdMessages);

	// execution result returned by command runner for running
	// initialization commands
	ExecutionResult initCmdsResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(initCmdsResult.getMessages()).andReturn(initCmdMessages).times(2);
	EasyMock.expect(initCmdsResult.getState()).andReturn(ExecutionState.FAILURE).times(2);	
	EasyMock.replay(initCmdsResult);

	// create mock execution result
	ExecutionResult operationResult = EasyMock.createMock(ExecutionResult.class);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, SOME_MESSAGE);
	operationResult.addMessage(ExecutionResult.MSG_STACKTRACE, SOME_MESSAGE);
	EasyMock.expect(operationResult.getState()).andReturn(ExecutionState.EXECUTING);
	operationResult.completeAsFailure(messageProvider, "aot.initialize_operation_failed");
	EasyMock.replay(operationResult);

	// create mock module info
	ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
	EasyMock.expect(moduleInfo.getId()).andReturn(MODULE_ID).times(2);
	EasyMock.replay(moduleInfo);

	// create mock execution info
	executionInfo = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo).times(2);
	EasyMock.expect(executionInfo.getResult()).andReturn(operationResult).times(2);
	EasyMock.replay(executionInfo);

	// create context
	Context context = EasyMock.createMock(Context.class);
	EasyMock.expect(context.put(InvokePluginsCommand.EXECUTION_INFO_KEY, executionInfo)).andReturn(SOMETING);
	EasyMock.replay(context);

	// create mock execution context repository
	executionContextRepository.register(executionInfo, context);
	executionContextRepository.unregister(context);
	EasyMock.replay(executionContextRepository);

	// complete mock command runner setup
	commandRunner.setExecutionResult((ExecutionResult) EasyMock.isA(ExecutionResult.class));
	EasyMock.expect(commandRunner.createContext()).andReturn(context);
	EasyMock.expect(commandRunner.lastExecutionSucceeded()).andReturn(false);
	EasyMock.expect(commandRunner.run(initializeOperationCommand,
		messageProvider.getMessage("aot.initialize_operation_info"), context)).andReturn(initCmdsResult);
	EasyMock.replay(commandRunner);

	// execute
	operationTask.execute(executionInfo);

	// verify
	verifyMocks(initCmdsResult, operationResult, context);
    }

    /**
     * Test task aborts if initialize operation command fails.
     * 
     * It is assumed that the command runner didn't capture an exception, so no
     * stack trace can be retrieved at CHECK POINT #1.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testTaskAbortsIfOperationInitializationCmdFails_WithoutException() {
	final String PATH = "some-path";
	final String MODULE_ID = "some-module-id";
	final String ENVIRONMENT = "some-environment";
	Object SOMETING = null; // something returned by the context.
	final String SOME_MESSAGE = "some-message";

	// messages returned by cmd results
	Map<String, String> initCmdMessages = EasyMock.createMock(Map.class);
	EasyMock.expect(initCmdMessages.get(ExecutionResult.MSG_MESSAGE)).andReturn(SOME_MESSAGE);
	EasyMock.expect(initCmdMessages.containsKey(ExecutionResult.MSG_STACKTRACE)).andReturn(false); // CHECK
												       // POINT
												       // #1
	EasyMock.replay(initCmdMessages);

	// execution result returned by command runner for running
	// initialization commands
	ExecutionResult initCmdsResult = EasyMock.createMock(ExecutionResult.class);
	EasyMock.expect(initCmdsResult.getMessages()).andReturn(initCmdMessages).times(2);
	EasyMock.expect(initCmdsResult.getState()).andReturn(ExecutionState.FAILURE).times(2);		
	EasyMock.replay(initCmdsResult);

	// create mock execution result
	ExecutionResult operationResult = EasyMock.createMock(ExecutionResult.class);
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, SOME_MESSAGE);
	EasyMock.expect(operationResult.getState()).andReturn(ExecutionState.EXECUTING);
	operationResult.completeAsFailure(messageProvider, "aot.initialize_operation_failed");
	EasyMock.replay(operationResult);

	// create mock module info
	ModuleInfo moduleInfo = EasyMock.createMock(ModuleInfo.class);
	EasyMock.expect(moduleInfo.getId()).andReturn(MODULE_ID).times(2);
	EasyMock.replay(moduleInfo);

	// create mock execution info
	executionInfo = EasyMock.createMock(ExecutionInfo.class);
	EasyMock.expect(executionInfo.getModuleInfo()).andReturn(moduleInfo).times(2);
	EasyMock.expect(executionInfo.getResult()).andReturn(operationResult).times(2);
	EasyMock.replay(executionInfo);

	// create context
	Context context = EasyMock.createMock(Context.class);
	EasyMock.expect(context.put(InvokePluginsCommand.EXECUTION_INFO_KEY, executionInfo)).andReturn(SOMETING);
	EasyMock.replay(context);

	// create mock execution context repository
	executionContextRepository.register(executionInfo, context);
	executionContextRepository.unregister(context);
	EasyMock.replay(executionContextRepository);

	// complete mock command runner setup
	commandRunner.setExecutionResult((ExecutionResult) EasyMock.isA(ExecutionResult.class));
	EasyMock.expect(commandRunner.createContext()).andReturn(context);
	EasyMock.expect(commandRunner.lastExecutionSucceeded()).andReturn(false);
	EasyMock.expect(commandRunner.run(initializeOperationCommand,
		messageProvider.getMessage("aot.initialize_operation_info"), context)).andReturn(initCmdsResult);
	EasyMock.replay(commandRunner);

	// execute
	operationTask.execute(executionInfo);

	// verify
	verifyMocks(initCmdsResult, operationResult, context);
    }

}
