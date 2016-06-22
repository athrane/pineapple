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

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.InitializeOperationCommand;
import com.alpha.pineapple.command.InvokePluginsCommand;
import com.alpha.pineapple.command.UnmarshallJAXBObjectsCommand;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.ObjectFactory;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleRepository;

/**
 * Implementation of the {@link OperationTask} interface for synchronous
 * execution of an operation.
 */
public class OperationTaskImpl implements OperationTask {

    /**
     * Default version for null modules.
     */
    static final String DEFAULT_NULL_VERSION = "1.0.0";

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    /**
     * Execution context repository.
     */
    @Resource
    ExecutionContextRepository executionContextRepository;

    /**
     * Result repository.
     */
    @Resource
    ResultRepository resultRepository;

    /**
     * Module repository.
     */
    @Resource
    ModuleRepository moduleRepository;
    
    /**
     * Command runner
     */
    @Resource
    CommandRunner commandRunner;

    /**
     * Initialize operation command.
     */
    @Resource
    Command initializeOperationCommand;

    /**
     * Unmarshall JAXB objects command.
     */
    @Resource
    Command unmarshallJAXBObjectsCommand;

    /**
     * Invoke plugins command.
     */
    @Resource
    Command invokePluginsCommand;

    public void execute(ExecutionInfo info) {
	// validate parameters
	Validate.notNull(info, "execution info is undefined.");

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { info.getModuleInfo().getId(), Thread.currentThread().getName() };
	    String message = messageProvider.getMessage("aot.invoke_operation_info", args);
	    logger.debug(message);
	}

	// create detached result for initialization
	ExecutionResult initResult = new ExecutionResultImpl("Initialization result");

	// configure command runner with execution result
	commandRunner.setExecutionResult(initResult);

	// create and register context
	Context context = commandRunner.createContext();
	executionContextRepository.register(info, context);

	// execute
	executeWithRegisteredContext(info, context);

	// unregister
	executionContextRepository.unregister(context);

	// log debug message
	if (logger.isDebugEnabled()) {
	    Object[] args = { info.getModuleInfo().getId(), Thread.currentThread().getName() };
	    String message = messageProvider.getMessage("aot.invoke_operation_completed", args);
	    logger.debug(message);
	}
    }

    @Override
    public ExecutionInfo execute(String operation, String environment, String module) {

	// get module info
	ModuleInfo moduleInfo = moduleRepository.resolveModule(module, environment);

	// create execution info
	ExecutionInfo executionInfo = resultRepository.startExecution(moduleInfo, environment, operation);

	execute(executionInfo);	
	return executionInfo;
    }
    
    @Override
    public ExecutionInfo executeComposite(String operation, String environment, String module, String description, ExecutionResult result) {
	
	// get module info
	ModuleInfo moduleInfo = moduleRepository.resolveModule(module, environment);
	
	// create execution info
	ExecutionInfo executionInfo = resultRepository.startCompositeExecution(moduleInfo, environment, operation, description, result);

	execute(executionInfo);	
	return executionInfo;
    }
    
    /**
     * Execute operation with a registered context with the execution context
     * repository.
     * 
     * @param info
     *            execution info for the operation
     * @param context
     *            operation context.
     */
    void executeWithRegisteredContext(ExecutionInfo info, Context context) {

	// initialize operation
	ExecutionResult commandResult = initializeOperation(info, context);

	// exit if command failed
	if (!commandRunner.lastExecutionSucceeded()) {
	    handleUnsuccessfulExecution(info, commandResult, "aot.initialize_operation_failed");
	    return;
	}

	// load module
	if (isModuleDefined(info)) {
	    commandResult = loadModule(info, context);

	    // exit if command failed
	    if (!commandRunner.lastExecutionSucceeded()) {
		handleUnsuccessfulExecution(info, commandResult, "aot.load_module_failed");
		return;
	    }
	} else {
	    handleUndefinedModule(info, context);
	}

	// load model
	commandResult = loadModuleModel(info, context);

	// exit if command failed
	if (!commandRunner.lastExecutionSucceeded()) {
	    handleUnsuccessfulExecution(info, commandResult, "aot.load_modulemodel_failed");
	    return;
	}

	// invoke plugins
	commandResult = invokePlugins(info, context);

	// exit if command failed
	if (!commandRunner.lastExecutionSucceeded()) {
	    handleUnsuccessfulExecution(info, commandResult, "aot.invoke_plugins_failed");
	    return;
	}

    }

    /**
     * initialize operation.
     * 
     * Returns true if the command succeeded.
     * 
     * @param info
     * @param context
     * 
     * @return command result Execution result for executed command.
     */
    @SuppressWarnings("unchecked")
    ExecutionResult initializeOperation(ExecutionInfo info, Context context) {

	// setup context
	context.put(InitializeOperationCommand.EXECUTION_INFO_KEY, info);

	// get message
	String message = messageProvider.getMessage("aot.initialize_operation_info");

	// run command
	ExecutionResult commandResult = commandRunner.run(initializeOperationCommand, message, context);

	// command result message to operation message
	addCommandMessageToOperationMessage(info, commandResult);

	return commandResult;
    }

    /**
     * Load module.
     * 
     * @param info
     *            Execution info.
     * @param context
     *            Command context.
     * 
     * @return command result Execution result for executed command.
     */
    @SuppressWarnings("unchecked")
    ExecutionResult loadModule(ExecutionInfo info, Context context) {

	// get message
	String message = messageProvider.getMessage("aot.load_module_info");

	// get module file
	File moduleFile = (File) context.get(InitializeOperationCommand.MODULE_FILE_KEY);

	Package packageName = Module.class.getPackage();
	context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, moduleFile);
	context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, packageName);

	// run command
	ExecutionResult commandResult = commandRunner.run(unmarshallJAXBObjectsCommand, message, context);

	// store result if command was successful
	if (commandRunner.lastExecutionSucceeded()) {

	    // get result
	    Object result = context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY);

	    // type cast to module
	    Module module = Module.class.cast(result);

	    // store result in context
	    context.put(InvokePluginsCommand.MODULE_KEY, module);
	}

	// add command result message to operation message
	addCommandMessageToOperationMessage(info, commandResult);

	return commandResult;

    }

    /**
     * Handle case where module is undefined. A null module is created and added
     * to the command context.
     * 
     * @param info
     *            Execution info.
     * @param context
     *            Command context.
     */
    @SuppressWarnings("unchecked")
    void handleUndefinedModule(ExecutionInfo info, Context context) {

	// create null module
	ObjectFactory moduleFactory = new ObjectFactory();
	Module nullModule = moduleFactory.createModule();

	// set id and version
	nullModule.setId(info.getModuleInfo().getId());
	nullModule.setVersion(DEFAULT_NULL_VERSION);

	// store result in context
	context.put(InvokePluginsCommand.MODULE_KEY, nullModule);

	// get message
	Object[] args = { nullModule.getId(), nullModule.getVersion() };
	String message = messageProvider.getMessage("aot.null_module_info", args);

	// add message to operation message
	addMessageToOperationMessage(info, message);
    }

    /**
     * Load module model.
     * 
     * @param info
     *            Execution info.
     * @param context
     *            Command context.
     * 
     * @return command result Execution result for executed command.
     */
    @SuppressWarnings("unchecked")
    ExecutionResult loadModuleModel(ExecutionInfo info, Context context) {

	// get message
	String message = messageProvider.getMessage("aot.load_modulemodel_info");

	// get model file
	File modelFile = (File) context.get(InitializeOperationCommand.MODULE_MODEL_FILE_KEY);

	Package packageName = Models.class.getPackage();
	context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, modelFile);
	context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, packageName);

	// run command
	ExecutionResult commandResult = commandRunner.run(unmarshallJAXBObjectsCommand, message, context);

	// store result if command was successful
	if (commandRunner.lastExecutionSucceeded()) {

	    // get result
	    Object result = context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY);

	    // type cast to model
	    Models model = Models.class.cast(result);

	    // store result in context
	    context.put(InvokePluginsCommand.MODULE_MODEL_KEY, model);
	}

	// command result message to operation message
	addCommandMessageToOperationMessage(info, commandResult);

	return commandResult;
    }

    /**
     * Invoke plugins.
     * 
     * @param info
     *            Execution info.
     * @param context
     *            Command context.
     * 
     * @return command result Execution result for executed command.
     */
    ExecutionResult invokePlugins(ExecutionInfo info, Context context) {

	// get operation result
	ExecutionResult operationResult = info.getResult();

	// run command with the operation execution result
	ExecutionResult commandResult = commandRunner.run(invokePluginsCommand, operationResult, context);

	return commandResult;
    }

    /**
     * Add message from command to operation message.
     * 
     * @param info
     *            execution info which contains the operation message to which
     *            the command message is added.
     * @param commandResult
     *            command result whose message is added to the operation
     *            message.
     */
    void addCommandMessageToOperationMessage(ExecutionInfo info, ExecutionResult commandResult) {

	// get command message
	Map<String, String> messages = commandResult.getMessages();
	String commandMessage = messages.get(ExecutionResult.MSG_MESSAGE);

	addMessageToOperationMessage(info, commandMessage);
    }

    /**
     * Add message to operation message.
     * 
     * @param info
     *            execution info which contains the operation message to which
     *            the message is added.
     * @param message
     *            the message which is added to the operation message.
     */
    void addMessageToOperationMessage(ExecutionInfo info, String message) {

	// get operation result
	ExecutionResult operationResult = info.getResult();

	// append initialize operation message
	operationResult.addMessage(ExecutionResult.MSG_MESSAGE, message);
    }

    /**
     * Handle unsuccessful operation.
     * 
     * @param info
     *            execution info.
     * @param commandResult
     *            Command result.
     * @param Message
     *            key for failure.
     */
    void handleUnsuccessfulExecution(ExecutionInfo info, ExecutionResult commandResult, String key) {

	// handle failure or error
	if (isCompletedWithFailureOrError(commandResult.getState())) {
	    failOperationAndAddStacktrace(info, commandResult, key);
	}

	// handle interruption
	if (isCompletedWithInterruption(commandResult.getState())) {
	    interruptOperation(info, commandResult, key);
	}
    }

    /**
     * Mark operation as failed. If the command result contains a stack trace
     * then it is added to the operation result.
     * 
     * @param info
     *            execution info.
     * @param commandResult
     *            Command result.
     * @param Message
     *            key for failure.
     */
    void failOperationAndAddStacktrace(ExecutionInfo info, ExecutionResult commandResult, String key) {

	// get operation result
	ExecutionResult operationResult = info.getResult();

	// set as failed if it isn't already reflected in the operation result
	// if command result is from execution of plugin this can be the case
	if (!isCompletedWithFailureOrError(operationResult.getState())) {
	    operationResult.completeAsFailure(messageProvider, key);
	}

	// add stack trace if it exists
	Map<String, String> messages = commandResult.getMessages();
	if (messages.containsKey(ExecutionResult.MSG_STACKTRACE)) {
	    String commandMessage = messages.get(ExecutionResult.MSG_STACKTRACE);
	    operationResult.addMessage(ExecutionResult.MSG_STACKTRACE, commandMessage);
	}
    }

    /**
     * Mark operation as interrupted.
     * 
     * @param info
     *            execution info.
     * @param commandResult
     *            Command result.
     * @param Message
     *            key for interruption.
     */
    void interruptOperation(ExecutionInfo info, ExecutionResult commandResult, String key) {

	// get operation result
	ExecutionResult operationResult = info.getResult();

	// set as interrupted if it isn't already reflected in the operation
	// result
	// if command result is from execution of plugin this can be the case
	if (!isCompletedWithInterruption(operationResult.getState())) {
	    operationResult.completeAsInterrupted(messageProvider, key);
	}
    }

    /**
     * Returns true if result completed with failure or error.
     * 
     * @param state
     *            Execution state to test.
     * 
     * @return true if result completed with failure or error..
     */
    boolean isCompletedWithFailureOrError(ExecutionState state) {
	if (state == ExecutionState.FAILURE)
	    return true;
	if (state == ExecutionState.ERROR)
	    return true;
	return false;
    }

    /**
     * Returns true if result completed with interruption.
     * 
     * @param state
     *            Execution state to test.
     * 
     * @return true if result completed with interruption.
     */
    boolean isCompletedWithInterruption(ExecutionState state) {
	return (state == ExecutionState.INTERRUPTED);
    }

    /**
     * Return true if module is defined.
     * 
     * @param info
     *            execution info.
     * 
     * @return true if module is defined.
     */
    boolean isModuleDefined(ExecutionInfo info) {
	return info.getModuleInfo().isDescriptorDefined();
    }

}
