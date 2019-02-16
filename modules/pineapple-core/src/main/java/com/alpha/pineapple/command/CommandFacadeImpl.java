/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2018 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.command;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.plugin.activation.PluginActivator;

/**
 * Implementation of the {@linkplain CommandFacade} interface.
 */
public class CommandFacadeImpl implements CommandFacade {

	/**
	 * Unmarshall command.
	 * 
	 * Please notice: The command isn't set by constructor injection (i.e. using
	 * the @Resource annotation) but injected using setter injection. The purpose is
	 * to avoid circular dependencies between the facade and the commands.
	 */
	Command unmarshallJAXBObjectsCommand;

	/**
	 * Marshall JAXB objects command.
	 * 
	 * Please notice: The command isn't set by constructor injection (i.e. using
	 * the @Resource annotation) but injected using setter injection. The purpose is
	 * to avoid circular dependencies between the facade and the commands.
	 */
	Command marshallJAXBObjectsCommand;

	/**
	 * Copy example modules command.
	 * 
	 * Please notice: The command isn't set by constructor injection (i.e. using
	 * the @Resource annotation) but injected using setter injection. The purpose is
	 * to avoid circular dependencies between the facade and the commands.
	 */
	Command copyExampleModulesCommand;

	/**
	 * Initialize plugin activator command.
	 * 
	 * Please notice: The command isn't set by constructor injection (i.e. using
	 * the @Resource annotation) but injected using setter injection. The purpose is
	 * to avoid circular dependencies between the facade and the commands.
	 */
	Command initializePluginActivatorCommand;

	/**
	 * Initialize home directories command.
	 * 
	 * Please notice: The command isn't set by constructor injection (i.e. using
	 * the @Resource annotation) but injected using setter injection. The purpose is
	 * to avoid circular dependencies between the facade and the commands.
	 */
	Command initializeHomeDirectoriesCommand;

	/**
	 * Create default environment configuration command.
	 * 
	 * Please notice: The command isn't set by constructor injection (i.e. using
	 * the @Resource annotation) but injected using setter injection. The purpose is
	 * to avoid circular dependencies between the facade and the commands.
	 */
	Command createDefaultEnvironmentConfigurationCommand;

	/**
	 * Invoke triggers command.
	 * 
	 * Please notice: The command isn't set by constructor injection (i.e. using
	 * the @Resource annotation) but injected using setter injection. The purpose is
	 * to avoid circular dependencies between the facade and the commands.
	 */
	Command invokeTriggersCommand;

	/**
	 * Command runner.
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Set command. Implemented to avoid circular dependencies between the facade
	 * and the command(s).
	 * 
	 * @param command inject command into facade.
	 */
	@Lazy
	@Autowired
	public void setCommand(UnmarshallJAXBObjectsCommand command) {
		this.unmarshallJAXBObjectsCommand = command;
	}

	/**
	 * Set command. Implemented to avoid circular dependencies between the facade
	 * and the command(s).
	 * 
	 * @param command inject command into facade.
	 */
	@Lazy
	@Autowired
	public void setCommand(MarshallJAXBObjectsCommand command) {
		this.marshallJAXBObjectsCommand = command;
	}

	/**
	 * Set command. Implemented to avoid circular dependencies between the facade
	 * and the command(s).
	 * 
	 * @param command inject command into facade.
	 */
	@Lazy
	@Autowired
	public void setCommand(CopyExampleModulesCommand command) {
		this.copyExampleModulesCommand = command;
	}

	/**
	 * Set command. Implemented to avoid circular dependencies between the facade
	 * and the command(s).
	 * 
	 * @param command inject command into facade.
	 */
	@Lazy
	@Autowired
	public void setCommand(InitializePluginActivatorCommand command) {
		this.initializePluginActivatorCommand = command;
	}

	/**
	 * Set command. Implemented to avoid circular dependencies between the facade
	 * and the command(s).
	 * 
	 * @param command inject command into facade.
	 */
	@Lazy
	@Autowired
	public void setCommand(InitializeHomeDirectoriesCommand command) {
		this.initializeHomeDirectoriesCommand = command;
	}

	/**
	 * Set command. Implemented to avoid circular dependencies between the facade
	 * and the command(s).
	 * 
	 * @param command inject command into facade.
	 */
	@Lazy
	@Autowired
	public void setCreateDefaultEnvironmentConfigurationCommand(CreateDefaultEnvironmentConfigurationCommand command) {
		this.createDefaultEnvironmentConfigurationCommand = command;
	}

	/**
	 * Set command. Implemented to avoid circular dependencies between the facade
	 * and the command(s).
	 * 
	 * @param command inject command into facade.
	 */
	@Resource
	public void setCommand(InvokeTriggersCommand command) {
		this.invokeTriggersCommand = command;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unmarshallJaxbObjects(File file, Class<T> targetClass, ExecutionResult result) {

		// create execution result
		Object[] args = { file.getAbsolutePath() };
		String message = messageProvider.getMessage("cf.unmarshal_jaxb_objects", args);
		ExecutionResult commandResult = result.addChild(message);

		// create and setup context
		Context context = commandRunner.createContext();
		Package packageName = targetClass.getPackage();
		context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, file);
		context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, packageName);

		// run command
		commandRunner.run(unmarshallJAXBObjectsCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new CommandFacadeException(commandResult);

		// handle successful execution
		// return objects if load was a success
		return targetClass.cast(context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY));
	}

	@Override
	public <T> T loadJaxbObjects(File file, Class<T> targetClass, ExecutionResult result) {
		return unmarshallJaxbObjects(file, targetClass, result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void marshallJaxbObjects(File file, Object root, ExecutionResult result) {

		// create execution result
		Object[] args = { file.getAbsolutePath() };
		String message = messageProvider.getMessage("cf.marshal_jaxb_objects", args);
		ExecutionResult commandResult = result.addChild(message);

		// create and setup context
		Context context = commandRunner.createContext();
		context.put(MarshallJAXBObjectsCommand.FILE_KEY, file);
		context.put(MarshallJAXBObjectsCommand.ROOT_KEY, root);

		// run command
		commandRunner.run(marshallJAXBObjectsCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new CommandFacadeException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	public void saveJaxbObjects(File file, Object root, ExecutionResult result) {
		marshallJaxbObjects(file, root, result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyExampleModules(File modulesDirectory, ExecutionResult result) {

		// create execution result
		Object[] args = { modulesDirectory };
		String message = messageProvider.getMessage("cf.copy_examplemodules", args);
		ExecutionResult commandResult = result.addChild(message);

		// create and setup context
		Context context = commandRunner.createContext();
		context.put(CopyExampleModulesCommand.DESTINATION_DIR_KEY, modulesDirectory);

		// run command
		commandRunner.run(copyExampleModulesCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new CommandFacadeException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public PluginActivator initializePluginActivator(CredentialProvider provider, Configuration configuration,
			ExecutionResult result) {

		// create execution result
		String message = messageProvider.getMessage("cf.init_pluginactivator_info");
		ExecutionResult commandResult = result.addChild(message);

		// create and setup context
		Context context = commandRunner.createContext();
		context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);
		context.put(InitializePluginActivatorCommand.RESOURCES_KEY, configuration);

		// run command
		commandRunner.run(initializePluginActivatorCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new CommandFacadeException(commandResult);

		// handle successful execution
		return (PluginActivator) context.get(InitializePluginActivatorCommand.PLUGIN_ACTIVATOR_KEY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initializeHomeDirectories(ExecutionResult result) {

		// create execution result
		String message = messageProvider.getMessage("cf.init_homedirectories_info");
		ExecutionResult commandResult = result.addChild(message);

		// create and setup context
		Context context = commandRunner.createContext();

		// run command
		commandRunner.run(initializeHomeDirectoriesCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new CommandFacadeException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	public void createDefaultConfiguration(ExecutionResult result) {

		// create execution result
		String message = messageProvider.getMessage("cf.create_defaultconfiguration_info");
		ExecutionResult commandResult = result.addChild(message);

		// create and setup context
		Context context = commandRunner.createContext();

		// run command
		commandRunner.run(createDefaultEnvironmentConfigurationCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new CommandFacadeException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public void executeTriggers(AggregatedModel model, ExecutionInfo executionInfo, ExecutionResult modelResult,
			ExecutionResult result) {

		// create execution result
		Object[] args = { model.getDescription() };
		String message = messageProvider.getMessage("cf.execute_triggers", args);
		ExecutionResult commandResult = result.addChild(message);

		// create and setup context
		Context context = commandRunner.createContext();
		context.put(InvokeTriggersCommand.AGGREGATED_MODEL_KEY, model);
		context.put(InvokeTriggersCommand.EXECUTION_INFO_KEY, executionInfo);
		context.put(InvokeTriggersCommand.MODEL_RESULT_KEY, modelResult);
		context.put(InvokeTriggersCommand.EXECUTIONRESULT_KEY, result);

		// run command
		commandRunner.run(invokeTriggersCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new CommandFacadeException(commandResult);

		// handle successful execution
		// NO-OP
	}

}
