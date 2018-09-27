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

package com.alpha.pineapple.plugin.agent.operation;

import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.admin.AdministrationProvider;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.agent.AgentConstants;
import com.alpha.pineapple.plugin.agent.model.AgentCommand;
import com.alpha.pineapple.plugin.agent.model.CreateEnvironment;
import com.alpha.pineapple.plugin.agent.model.DeleteAllScheduledOperations;
import com.alpha.pineapple.plugin.agent.model.DeleteModule;
import com.alpha.pineapple.plugin.agent.model.DeleteScheduledOperation;
import com.alpha.pineapple.plugin.agent.model.DistributeAndExecuteOperation;
import com.alpha.pineapple.plugin.agent.model.DistributeModule;
import com.alpha.pineapple.plugin.agent.model.ExecuteOperation;
import com.alpha.pineapple.plugin.agent.model.Mapper;
import com.alpha.pineapple.plugin.agent.model.ObjectFactory;
import com.alpha.pineapple.plugin.agent.model.PineappleAgent;
import com.alpha.pineapple.plugin.agent.model.RefreshEnvironmentConfiguration;
import com.alpha.pineapple.plugin.agent.model.ScheduleOperation;
import com.alpha.pineapple.plugin.agent.session.AgentSession;
import com.alpha.pineapple.session.Session;

@PluginOperation(OperationNames.WILDCARD_OPERATION)
public class DefaultOperation implements Operation {
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
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	/**
	 * Distribute module command.
	 */
	@Resource
	Command distributeModuleCommand;

	/**
	 * Delete module command.
	 */
	@Resource
	Command deleteModuleCommand;

	/**
	 * Refresh environment configuration command.
	 */
	@Resource
	Command refreshEnvironmentConfigurationCommand;

	/**
	 * Create environment command.
	 */
	@Resource
	Command createEnvironmentCommand;

	/**
	 * Execute operation command.
	 */
	@Resource
	Command executeOperationCommand;

	/**
	 * Schedule operation command.
	 */
	@Resource
	Command scheduleOperationCommand;

	/**
	 * Delete scheduled operation command.
	 */
	@Resource
	Command deleteScheduledOperationCommand;

	/**
	 * Delete all scheduled operations command.
	 */
	@Resource
	Command deleteAllScheduledOperationsCommand;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

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
		operationUtils.validateContentType(content, AgentConstants.LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, AgentSession.class);

		// configure command runner with execution result
		commandRunner.setExecutionResult(result);

		try {
			// type cast model
			JAXBElement<PineappleAgent> jaxbElement = (JAXBElement<PineappleAgent>) content;
			PineappleAgent agentModel = jaxbElement.getValue();

			// type cast session
			AgentSession agentSession = (AgentSession) session;

			// process model
			processModel(agentModel, agentSession, result);

			// compute execution state from children
			result.completeAsComputed(messageProvider, "do.completed", null, "do.failed", null);
		} catch (Exception e) {
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("do.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	/**
	 * Process model commands.
	 * 
	 * @param agentModel
	 *            agent model.
	 * @param session
	 *            agent session.
	 * @param result
	 *            execution result.
	 */
	void processModel(PineappleAgent agentModel, AgentSession session, ExecutionResult result) {

		List<AgentCommand> agentCommands = agentModel.getCommands();
		for (AgentCommand command : agentCommands) {

			// enforce continuation policy
			if (!result.getContinuationPolicy().continueExecution()) {
				Object[] args = { getCommandDescription(command, session) };
				String message = messageProvider.getMessage("do.contination_policy_enforcement_info", args);
				result.addMessage(ExecutionResult.MSG_MESSAGE, message);
				return;
			}

			if (command instanceof DistributeModule) {
				distributeModule(session, (DistributeModule) command, result);
				continue;
			}
			if (command instanceof DeleteModule) {
				deleteModule(session, (DeleteModule) command, result);
				continue;
			}
			if (command instanceof ExecuteOperation) {
				executeOperation(session, (ExecuteOperation) command, result);
				continue;
			}
			if (command instanceof DistributeAndExecuteOperation) {
				distributeAndexecuteOperation(session, (DistributeAndExecuteOperation) command, result);
				continue;
			}
			if (command instanceof CreateEnvironment) {
				executeOperation(session, (ExecuteOperation) command, result);
				continue;
			}
			if (command instanceof RefreshEnvironmentConfiguration) {
				refreshConfiguration(session, (RefreshEnvironmentConfiguration) command, result);
				continue;
			}
			if (command instanceof ScheduleOperation) {
				scheduleOperation(session, (ScheduleOperation) command, result);
				continue;
			}
			if (command instanceof DeleteScheduledOperation) {
				deleteScheduleOperation(session, (DeleteScheduledOperation) command, result);
				continue;
			}
			if (command instanceof DeleteAllScheduledOperations) {
				deleteAllScheduledOperations(session, (DeleteAllScheduledOperations) command, result);
				continue;
			}

		}
	}

	/**
	 * Create command description.
	 * 
	 * @param agentCommand
	 *            command to describe.
	 * 
	 * @return command description.
	 */
	String getCommandDescription(AgentCommand agentCommand, AgentSession session) {
		if (agentCommand instanceof DistributeModule) {
			DistributeModule command = (DistributeModule) agentCommand;
			Object[] args = { command.getModule(), session.getHostName() };
			return messageProvider.getMessage("do.distribute_module_info", args);
		}
		if (agentCommand instanceof DeleteModule) {
			DeleteModule command = (DeleteModule) agentCommand;
			Object[] args = { command.getModule(), session.getHostName() };
			return messageProvider.getMessage("do.delete_module_info", args);
		}
		if (agentCommand instanceof ExecuteOperation) {
			ExecuteOperation command = (ExecuteOperation) agentCommand;
			Object[] args = { command.getOperation(), command.getModule(), command.getEnvironment() };
			return messageProvider.getMessage("do.executeoperation_info", args);
		}
		if (agentCommand instanceof DistributeAndExecuteOperation) {
			DistributeAndExecuteOperation command = (DistributeAndExecuteOperation) agentCommand;
			Object[] args = { command.getModule() };
			return messageProvider.getMessage("do.distributeandexecuteoperation_info", args);
		}
		if (agentCommand instanceof CreateEnvironment) {
			CreateEnvironment command = (CreateEnvironment) agentCommand;
			Object[] args = { command.getEnvironment(), session.getHostName() };
			return messageProvider.getMessage("do.create_environment_info", args);
		}
		if (agentCommand instanceof RefreshEnvironmentConfiguration) {
			Object[] args = { session.getHostName() };
			return messageProvider.getMessage("do.refresh_environment_configuration_info", args);
		}
		if (agentCommand instanceof ScheduleOperation) {
			Object[] args = { session.getHostName() };
			return messageProvider.getMessage("do.schedule_operation_info", args);
		}
		if (agentCommand instanceof DeleteScheduledOperation) {
			Object[] args = { session.getHostName() };
			return messageProvider.getMessage("do.delete_scheduled_operation_info", args);
		}
		if (agentCommand instanceof DeleteAllScheduledOperations) {
			Object[] args = { session.getHostName() };
			return messageProvider.getMessage("do.delete_all_scheduled_operations_info", args);
		}
		return messageProvider.getMessage("do.no_description_available");
	}

	/**
	 * Execute remote operation.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            execute operation command.
	 * @param result
	 *            execution result
	 */
	void executeOperation(AgentSession session, ExecuteOperation command, ExecutionResult result) {

		// create description
		Object[] args = { command.getOperation(), command.getModule(), command.getEnvironment() };
		String message = messageProvider.getMessage("do.executeoperation_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapModel(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(executeOperationCommand, message, context);
	}

	/**
	 * Distribute module and execute it.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            distribute and execute operation command.
	 * @param result
	 *            execution result
	 */
	void distributeAndexecuteOperation(AgentSession session, DistributeAndExecuteOperation command,
			ExecutionResult result) {

		// create description
		Object[] args = { command.getModule() };
		String message = messageProvider.getMessage("do.distributeandexecuteoperation_info", args);

		// create execution result
		ExecutionResult distributeAndExecuteResult = result.addChild(message);

		try {

			// distribute module
			ObjectFactory factory = new ObjectFactory();
			DistributeModule distributeModule = factory.createDistributeModule();
			distributeModule.setModule(command.getModule());
			distributeModule(session, distributeModule, distributeAndExecuteResult);

			// if distribution failed then exit
			if (!commandRunner.lastExecutionSucceeded()) {
				result.completeAsComputed(messageProvider, "do.distributeandexecuteoperation_completed", null,
						"do.distributeandexecuteoperation_failed", null);
				return;
			}

			// execute
			ExecuteOperation executeOperation = factory.createExecuteOperation();
			executeOperation.setModule(command.getModule());
			executeOperation.setEnvironment(command.getEnvironment());
			executeOperation.setOperation(command.getOperation());
			executeOperation(session, executeOperation, distributeAndExecuteResult);

			// delete module even if execution fails
			DeleteModule deleteModule = factory.createDeleteModule();
			deleteModule.setModule(command.getModule());
			deleteModule(session, deleteModule, distributeAndExecuteResult);

			// compute result
			distributeAndExecuteResult.completeAsComputed(messageProvider, "do.distributeandexecuteoperation_completed",
					null, "do.distributeandexecuteoperation_failed", null);

		} catch (Exception e) {
			distributeAndExecuteResult.completeAsError(messageProvider, "do.distributeandexecuteoperation_error", e);
		}
	}

	/**
	 * Distribute compressed module to remote Pineapple.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            distribute module command.
	 * @param result
	 *            operation execution result.
	 */
	void distributeModule(AgentSession session, DistributeModule command, ExecutionResult result) {

		// create description
		Object[] args = { command.getModule(), session.getHostName() };
		String message = messageProvider.getMessage("do.distribute_module_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapDistributeModule(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(distributeModuleCommand, message, context);
	}

	/**
	 * Delete module from remote Pineapple.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            delete module command.
	 * @param result
	 *            operation execution result.
	 */
	void deleteModule(AgentSession session, DeleteModule command, ExecutionResult result) {

		// create description
		Object[] args = { command.getModule(), session.getHostName() };
		String message = messageProvider.getMessage("do.delete_module_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapDeleteModule(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(deleteModuleCommand, message, context);
	}

	/**
	 * Refresh environment configuration at remote Pineapple.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            delete module command.
	 * @param result
	 *            operation execution result.
	 */
	void refreshConfiguration(AgentSession session, RefreshEnvironmentConfiguration command, ExecutionResult result) {

		// create description
		Object[] args = { session.getHostName() };
		String message = messageProvider.getMessage("do.refresh_environment_configuration_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapRefreshEnvironmentConfiguration(context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(refreshEnvironmentConfigurationCommand, message, context);
	}

	/**
	 * Create environment in to remote Pineapple.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            create environment command.
	 * @param result
	 *            operation execution result.
	 */
	void createEnvironment(AgentSession session, CreateEnvironment command, ExecutionResult result) {

		// create description
		Object[] args = { command.getEnvironment(), session.getHostName() };
		String message = messageProvider.getMessage("do.create_environment_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapModel(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		// commandRunner.run(distributeModuleCommand, message, context);
	}

	/**
	 * Schedule operation.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            schedule operation command.
	 * @param result
	 *            execution result
	 */
	void scheduleOperation(AgentSession session, ScheduleOperation command, ExecutionResult result) {

		// create description
		Object[] args = { command.getName(), command.getModule(), command.getEnvironment(), command.getOperation(),
				command.getSchedulingExpression() };
		String message = messageProvider.getMessage("do.schedule_operation_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapScheduleOperation(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(scheduleOperationCommand, message, context);
	}

	/**
	 * Delete Schedule operation.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            delete scheduled operation command.
	 * @param result
	 *            execution result
	 */
	void deleteScheduleOperation(AgentSession session, DeleteScheduledOperation command, ExecutionResult result) {

		// create description
		Object[] args = { command.getName() };
		String message = messageProvider.getMessage("do.delete_schedule_operation_info", args);

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapModel(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(deleteScheduledOperationCommand, message, context);
	}

	/**
	 * Delete All Scheduled operations.
	 * 
	 * @param session
	 *            plugin session.
	 * @param command
	 *            delete all scheduled operations command.
	 * @param result
	 *            execution result
	 */
	void deleteAllScheduledOperations(AgentSession session, DeleteAllScheduledOperations command,
			ExecutionResult result) {

		// create description
		String message = messageProvider.getMessage("do.delete_all_scheduled_operations_info");

		// map model content to context
		Context context = commandRunner.createContext();
		mapper.mapModel(command, context, session);

		// run command
		commandRunner.setExecutionResult(result);
		commandRunner.run(deleteAllScheduledOperationsCommand, message, context);
	}

}
