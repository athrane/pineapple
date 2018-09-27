/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

package com.alpha.pineapple;

import static com.alpha.pineapple.CoreConstants.CREDENTIALS_FILE;
import static com.alpha.pineapple.CoreConstants.MSG_ENVIRONMENT;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE;
import static com.alpha.pineapple.CoreConstants.MSG_MODULE_FILE;
import static com.alpha.pineapple.CoreConstants.MSG_OPERATION;
import static com.alpha.pineapple.CoreConstants.RESOURCE_FILE;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.command.CommandFacade;
import com.alpha.pineapple.command.CommandFacadeException;
import com.alpha.pineapple.command.CreateDefaultEnvironmentConfigurationCommand;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.credential.FileBasedCredentialProviderImpl;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.execution.ResultRepository;
import com.alpha.pineapple.execution.continuation.ContinuationPolicy;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationRespository;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.plugin.activation.PluginActivator;

/**
 * Implementation of the PineappleCore interface.
 */
class CoreImpl implements PineappleCore {

	/**
	 * Example modules directory.
	 */
	static final File EXAMPLES_MODULES = new File("example-modules");

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
	 * Scheduled operation repository.
	 */
	@Resource
	ScheduledOperationRespository scheduledOperationRepository;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Asynchronous operation task.
	 */
	@Resource
	OperationTask asyncOperationTask;

	/**
	 * Command facade.
	 */
	@Resource
	CommandFacade commandFacade;

	/**
	 * Create default environment configuration command.
	 */
	@Resource
	Command createDefaultEnvironmentConfigurationCommand;

	/**
	 * Initialize home directories command.
	 */
	@Resource
	Command initializeHomeDirectoriesCommand;

	/**
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Administration provider.
	 */
	@Resource
	Administration administrationProvider;

	/**
	 * File based credential provider implementation.
	 */
	@Resource
	FileBasedCredentialProviderImpl uninitializedFileBasedCredentialProvider;

	/**
	 * Credential provider.
	 */
	CredentialProvider credentialProvider;

	/**
	 * Plugin activator.
	 */
	PluginActivator pluginActivator;

	/**
	 * Resources configuration.
	 */
	Configuration resourcesConfiguration;

	/**
	 * Initialization info.
	 */
	ExecutionResult initializationInfoResult;

	/**
	 * CoreImpl no-arg constructor.
	 */
	CoreImpl() {
	}

	/**
	 * Initialize the core component.
	 * 
	 * Will attempt to load environment configuration file which contains resource
	 * definitions from class path using the name "resources.xml".
	 * 
	 * @param provider
	 *            Credential provider, which can provide credentials for external
	 *            resources.
	 * 
	 * @throws CoreException
	 *             If initialization fails.
	 * @throws NullPointerException
	 *             if provider parameter is undefined.
	 */
	void initialize(CredentialProvider provider) throws CoreException {
		initialize(provider, new File(RESOURCE_FILE));
	}

	/**
	 * Initialize the core component.
	 * 
	 * @param resources
	 *            Environment configuration file which contains resource
	 *            definitions.
	 * @param provider
	 *            Credential provider, which can provide credentials for external
	 *            resources.
	 * 
	 * @throws CoreException
	 *             If initialization fails.
	 * @throws NullPointerException
	 *             if provider parameter is undefined.
	 */
	void initialize(CredentialProvider provider, File resources) throws CoreException {
		Validate.notNull(provider, "provider is undefined.");
		Validate.notNull(resources, "resources is undefined.");

		// create execution result
		String message = messageProvider.getMessage("ci.initialize_info");
		ExecutionResult executionResult = resultRepository.startExecution(message);

		// configure command runner with execution result
		commandRunner.setExecutionResult(executionResult);

		// set provider
		this.credentialProvider = provider;

		try {
			addMetadataForReport(executionResult);

			// initialize components
			loadResourceConfiguration(resources, executionResult);
			initializePluginActivator(executionResult);
			initializeAdministration();
			initializeModuleRepository();
			initializeScheduledOperationRepository(executionResult);

		} catch (CommandFacadeException e) {
			// catch error to abort initialization
		}

		// compute execution state from children
		executionResult.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed",
				null);

		// store initialization info result
		initializationInfoResult = executionResult;

		// log info message
		logInitializationInfo();
	}

	/**
	 * Initialize the core component.
	 * 
	 * @throws CoreException
	 *             If initialization fails.
	 */
	void initialize() throws CoreException {

		// create execution result
		String message = messageProvider.getMessage("ci.initialize_info");
		ExecutionResult executionResult = resultRepository.startExecution(message);

		// configure command runner with execution result
		commandRunner.setExecutionResult(executionResult);

		// get conf directory
		File confDirectory = runtimeDirectoryProvider.getConfigurationDirectory();

		// define resources
		File resources = new File(confDirectory, RESOURCE_FILE);

		// define credentials file
		File credentials = new File(confDirectory, CREDENTIALS_FILE);

		try {

			addMetadataForReport(executionResult);

			// initialize components
			initializeHomeDirectories();
			createDefaultConfiguration();
			initializeCredentialProvider(credentials);
			loadResourceConfiguration(resources, executionResult);
			initializePluginActivator(executionResult);
			initializeAdministration();
			initializeModuleRepository();
			initializeScheduledOperationRepository(executionResult);

		} catch (CommandFacadeException e) {
			// catch error to abort initialization
		}

		// compute execution state from children
		executionResult.completeAsComputed(messageProvider, "ci.initialize_completed", null, "ci.initialize_failed",
				null);

		// store initialization info result
		initializationInfoResult = executionResult;

		// log info message
		logInitializationInfo();
	}

	/**
	 * Get execution result from initialization.
	 * 
	 * @return execution result from initialization. If component isn't initialized
	 *         then null is returned.
	 */

	@Override
	public String getInitializationInfoAsString() {
		if (initializationInfoResult == null) {
			return messageProvider.getMessage("ci.initialization_info_notinitialized");
		}

		Object[] args = { Long.toString(initializationInfoResult.getTime()) };
		if (initializationInfoResult.isSuccess()) {
			return messageProvider.getMessage("ci.initialization_info_success", args);
		}
		return messageProvider.getMessage("ci.initialization_info_failure", args);
	}

	@Override
	public ExecutionResult getInitializationInfo() {
		return initializationInfoResult;
	}

	/**
	 * Initialize file based credential provider.
	 * 
	 * @param credential
	 *            Defines absolute path where the credentials should be loaded from.
	 * 
	 * @throws CoreException
	 *             If initialization fails.
	 */
	void initializeCredentialProvider(File credentials) throws CoreException {

		// get credential provider
		FileBasedCredentialProviderImpl provider = this.uninitializedFileBasedCredentialProvider;

		// initialize provider
		provider.initialize(credentials);

		// set provider
		this.credentialProvider = provider;
	}

	/**
	 * Initialize home directories.
	 */
	void initializeHomeDirectories() {

		// create description and setup context
		String message = messageProvider.getMessage("ci.initialize_homedirectories_info");
		Context context = commandRunner.createContext();

		// run command
		commandRunner.run(initializeHomeDirectoriesCommand, message, context);
	}

	/**
	 * Create default resources configuration.
	 */
	@SuppressWarnings("unchecked")
	void createDefaultConfiguration() {

		// create description and setup context
		Object[] args = { runtimeDirectoryProvider.getConfigurationDirectory() };
		String message = messageProvider.getMessage("ci.create_defaultconfiguration_info", args);
		Context context = commandRunner.createContext();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, EXAMPLES_MODULES);

		// run command
		commandRunner.run(createDefaultEnvironmentConfigurationCommand, message, context);
	}

	/**
	 * Load resources configuration.
	 * 
	 * @param resources
	 *            Resources file object. Defines location/filename where the
	 *            resources file should be loaded from.
	 * @param result
	 *            execution result.
	 * 
	 * @throws CommandFacadeException
	 *             if load fails.
	 */
	void loadResourceConfiguration(File resources, ExecutionResult result) {
		Validate.notNull(resources, "resources is undefined.");
		Validate.notNull(result, "result is undefined.");
		Object config = commandFacade.loadJaxbObjects(resources, Configuration.class, result);
		resourcesConfiguration = Configuration.class.cast(config);
	}

	/**
	 * initialize plugin activator.
	 * 
	 * @param result
	 *            execution result.
	 * 
	 * @throws CommandFacadeException
	 *             if load fails.
	 */
	void initializePluginActivator(ExecutionResult result) {
		Validate.notNull(result, "result is undefined.");
		pluginActivator = commandFacade.initializePluginActivator(credentialProvider, resourcesConfiguration, result);
	}

	/**
	 * Initialize the administration API by adding credential provider to the
	 * administration API.
	 */
	void initializeAdministration() {
		administrationProvider.setCredentialProvider(credentialProvider);
	}

	/**
	 * Initialize the module repository.
	 */
	void initializeModuleRepository() {
		moduleRepository.initialize();
	}

	/**
	 * Initialize the scheduled operation repository.
	 * 
	 * @param result
	 *            execution result.
	 */
	void initializeScheduledOperationRepository(ExecutionResult result) {
		scheduledOperationRepository.initialize(result);
	}

	public ExecutionInfo executeOperation(String operation, String environment, String module) {

		// declare info objects
		ExecutionInfo executionInfo = null;
		ModuleInfo moduleInfo = null;

		try {
			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { operation };
				String message = messageProvider.getMessage("ci.invoke_operation_info", args);
				logger.debug(message);
			}

			// validate arguments
			validateExcutionArguments(operation, environment, module);

			// get module info
			moduleInfo = moduleRepository.resolveModule(module, environment);

			// create execution info
			executionInfo = resultRepository.startExecution(moduleInfo, environment, operation);

			// execute operation
			asyncOperationTask.execute(executionInfo);

			// log debug message
			if (logger.isDebugEnabled()) {
				String message = messageProvider.getMessage("ci.invoke_operation_exit");
				logger.debug(message);
			}

			return executionInfo;
		} catch (Exception e) {
			// if module info is undefined then create null info
			if (moduleInfo == null) {
				moduleInfo = ModuleInfoImpl.getNullInstance();
			}

			// if execution info is undefined then create it
			if (executionInfo == null) {
				executionInfo = resultRepository.startExecution(moduleInfo, environment, operation);
			}

			// terminate execute with execution
			Object[] args = { e.getMessage() };
			ExecutionResult result = executionInfo.getResult();
			result.completeAsError(messageProvider, "ci.invoke_operation_error", args, e);

			return executionInfo;
		}
	}

	@Override
	public void cancelOperation(ExecutionInfo executionInfo) {
		Validate.notNull(executionInfo, "executionInfo is undefined.");

		// cancel operation
		ExecutionResult result = executionInfo.getResult();
		ContinuationPolicy policy = result.getContinuationPolicy();
		policy.setCancelled();

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { executionInfo.getOperation(), executionInfo.getModuleInfo().getId(),
					executionInfo.getEnvironment() };
			String message = messageProvider.getMessage("ci.cancel_operation_info", args);
			logger.debug(message);
		}
	}

	/**
	 * Validate operation arguments. Throws an {@link IllegalArgumentException} if
	 * an argument doesn't pass validation.
	 * 
	 * @param operation
	 *            Operation argument.
	 * @param environment
	 *            Environment argument.
	 * @param Module
	 *            Module argument.
	 */
	void validateExcutionArguments(String operation, String environment, String module) {
		Validate.notNull(operation, "operation is undefined.");
		Validate.notEmpty(operation, "operation is empty string.");
		Validate.notNull(environment, "environment is undefined.");
		Validate.notEmpty(environment, "environment is empty string.");
		Validate.notNull(module, "module is undefined.");
		Validate.notEmpty(module, "module is empty string.");
	}

	public void addListener(ResultListener listener) {
		this.resultRepository.addListener(listener);
	}

	public void addListeners(ResultListener[] listeners) {
		Validate.notNull(listeners, "listeners is undefined.");

		// add listeners
		for (ResultListener listener : listeners) {
			addListener(listener);
		}
	}

	@Override
	public ResultListener[] getListeners() {
		return resultRepository.getListeners();
	}

	@Override
	public void removeListener(ResultListener listener) {
		resultRepository.removeListener(listener);
	}

	@Override
	public Administration getAdministration() {
		return administrationProvider;
	}

	/**
	 * Log the initialization info.
	 */
	void logInitializationInfo() {
		if (logger.isInfoEnabled()) {
			logger.info(toString());
			logger.info(getInitializationInfoAsString());
		}
	}

	/**
	 * Add some meta data for reports.
	 * 
	 * @param executionResult
	 *            root execution result to which the data is added.
	 */
	void addMetadataForReport(ExecutionResult executionResult) {
		String message = messageProvider.getMessage("ci.report_metadata_operation_info");
		executionResult.addMessage(MSG_OPERATION, message);
		message = messageProvider.getMessage("ci.report_metadata_environment_info");
		executionResult.addMessage(MSG_ENVIRONMENT, message);
		message = messageProvider.getMessage("ci.report_metadata_module_info");
		executionResult.addMessage(MSG_MODULE, message);
		message = messageProvider.getMessage("ci.report_metadata_module_file_info");
		executionResult.addMessage(MSG_MODULE_FILE, message);
	}

	@Override
	public String toString() {

		if (messageProvider != null) {
			StringBuilder message = new StringBuilder();
			message.append(messageProvider.getMessage("ci.name"));
			message.append(", version ");
			message.append(messageProvider.getMessage("ci.version"));
			message.append(", Copyright (C) ");
			message.append(messageProvider.getMessage("ci.year"));
			message.append(" ");
			message.append(messageProvider.getMessage("ci.organization"));
			message.append(".");
			return message.toString();
		} else {
			return super.toString();
		}
	}

}
