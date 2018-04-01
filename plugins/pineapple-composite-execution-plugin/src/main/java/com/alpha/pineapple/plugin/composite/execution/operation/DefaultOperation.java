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

package com.alpha.pineapple.plugin.composite.execution.operation;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.OperationUtils;
import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.admin.AdministrationProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionInfoImpl;
import com.alpha.pineapple.execution.ExecutionInfoProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.OperationTask;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.composite.execution.model.Composite;
import com.alpha.pineapple.plugin.composite.execution.model.CompositeExecution;
import com.alpha.pineapple.session.Session;

@PluginOperation(OperationNames.WILDCARD_OPERATION)
public class DefaultOperation implements Operation {

	/**
	 * Legal content types supported by plugin operations.
	 */
	public static final Class<?>[] LEGAL_CONTENT_TYPES = { CompositeExecution.class };

	/**
	 * Null environments.
	 */
	static final String[] NULL_ENVIRONMENTS = {};

	/**
	 * Null directory.
	 */
	static final File NULL_DIRECTORY = null;

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
	 * Operation utilities.
	 */
	@Resource
	OperationUtils operationUtils;

	/**
	 * Core administration provider.
	 */
	@Resource
	AdministrationProvider coreAdministrationProvider;

	/**
	 * Execution info provider.
	 */
	@Resource
	ExecutionInfoProvider coreExecutionInfoProvider;

	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		// validate parameters
		Validate.notNull(content, "content is undefined.");
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(result, "result is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { content.getClass().getName(), content };
			String message = messageProvider.getMessage("do.start", args);
			logger.debug(message);
		}

		// validate parameters
		operationUtils.validateContentType(content, LEGAL_CONTENT_TYPES);

		try {
			// type cast model
			CompositeExecution model = (CompositeExecution) content;

			// process model
			procesModel(model, result);

			// compute result
			result.completeAsComputed(messageProvider, "do.completed", null, "do.failed", null);

		} catch (Exception e) {
			Object[] args = { StackTraceHelper.getStrackTrace(e) };
			String message = messageProvider.getMessage("do.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	/**
	 * Process model.
	 * 
	 * @param model
	 *            composite execution model.
	 * @param result
	 *            execution result.
	 */
	void procesModel(CompositeExecution model, ExecutionResult result) {

		// get composites
		List<Composite> composites = model.getModule();
		if (composites == null)
			return;

		// iterate over composites
		for (Composite composite : composites) {
			String module = composite.getName();

			// enforce continuation policy
			if (!result.getContinuationPolicy().continueExecution()) {
				Object[] args = { module };
				String message = messageProvider.getMessage("do.contination_policy_enforcement_info", args);
				result.addMessage(ExecutionResult.MSG_MESSAGE, message);
				return;
			}

			executeComposite(module, result);
		}
	}

	/**
	 * Execute operation for composite module.
	 * 
	 * @param module
	 *            module.
	 * @param operationResult
	 *            parent result.
	 * 
	 * @return execution info for operation.
	 */
	void executeComposite(String module, ExecutionResult operationResult) {

		// declare info objects
		ExecutionInfo executionInfo = null;
		ModuleInfo moduleInfo = null;

		// get meta data
		String operation = getOeration(operationResult);
		String environment = getEnvironment(operationResult);

		try {

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { operation };
				String message = messageProvider.getMessage("do.invoke_operation_info", args);
				logger.debug(message);
			}

			// validate arguments
			validateExcutionArguments(operation, environment, module);

			// get module info
			ModuleRepository moduleRepository = coreAdministrationProvider.getModuleRepository();
			moduleInfo = moduleRepository.resolveModule(module, environment);

			// create execution info
			executionInfo = startCompositeExecution(moduleInfo, environment, operation, operationResult);

			// execute operation
			OperationTask operationTask = coreAdministrationProvider.getOperationTask();
			operationTask.execute(executionInfo);

			// log debug message
			if (logger.isDebugEnabled()) {
				String message = messageProvider.getMessage("do.invoke_operation_exit");
				logger.debug(message);
			}

		} catch (Exception e) {

			// if module info is undefined then create null info
			if (moduleInfo == null) {
				String description = messageProvider.getMessage("do.composite_nofound_info");
				moduleInfo = ModuleInfoImpl.getInstance(description, NULL_ENVIRONMENTS, false, NULL_DIRECTORY);
			}

			// add result for failed composite
			ExecutionResult compositeResult = AddResultForCompositeExecution(moduleInfo, operationResult);

			// terminate execution with error
			Object[] args = { e.getMessage() };
			compositeResult.completeAsError(messageProvider, "do.invoke_operation_error", args, e);
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

	/**
	 * Get environment for operation.
	 * 
	 * @param result
	 *            operation execution result.
	 * 
	 * @return environment for operation
	 */
	String getEnvironment(ExecutionResult result) {
		ExecutionInfo info = coreExecutionInfoProvider.get(result);
		return info.getEnvironment();
	}

	/**
	 * Get operation.
	 * 
	 * @param result
	 *            operation execution result.
	 * 
	 * @return operation.
	 */
	String getOeration(ExecutionResult result) {
		ExecutionInfo info = coreExecutionInfoProvider.get(result);
		return info.getOperation();
	}

	/**
	 * Start composite execution.
	 * 
	 * @param moduleInfo
	 *            module info for composite.
	 * @param environment
	 *            environment.
	 * @param operation
	 *            operation.
	 * @param result
	 *            operation result.
	 * 
	 * @return execution info for composite execution.
	 */
	ExecutionInfo startCompositeExecution(ModuleInfo moduleInfo, String environment, String operation,
			ExecutionResult result) {
		ExecutionResult childResult = AddResultForCompositeExecution(moduleInfo, result);

		// create execution info
		ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, environment, operation, childResult);
		return executionInfo;
	}

	/**
	 * Add execution result for composite execution.
	 * 
	 * @param moduleInfo
	 *            module info for composite.
	 * 
	 * @return execution result.
	 */
	ExecutionResult AddResultForCompositeExecution(ModuleInfo moduleInfo, ExecutionResult result) {

		// create execution result description
		Object[] args = { moduleInfo.getId() };
		String description = messageProvider.getMessage("do.start_composite_operation_info", args);

		// add execution result
		ExecutionResult childResult = result.addChild(description);
		return childResult;
	}

}
