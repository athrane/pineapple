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

package com.alpha.pineapple.plugin.git.operation;

import static com.alpha.javautils.ArgumentUtils.notNull;
import static com.alpha.pineapple.OperationNames.DEPLOY_CONFIGURATION;
import static com.alpha.pineapple.execution.ExecutionResult.MSG_MESSAGE;
import static com.alpha.pineapple.plugin.git.GitConstants.BRANCH_HEAD;
import static com.alpha.pineapple.plugin.git.GitConstants.LEGAL_CONTENT_TYPES;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.git.model.CloneRepository;
import com.alpha.pineapple.plugin.git.model.Git;
import com.alpha.pineapple.plugin.git.model.GitCommand;
import com.alpha.pineapple.plugin.git.model.Log;
import com.alpha.pineapple.plugin.git.session.GitSession;
import com.alpha.pineapple.session.Session;

/**
 * Implements the deploy-configuration operation.
 * 
 * Creates a set Git commands defined in the model.
 */
@PluginOperation(DEPLOY_CONFIGURATION)
public class DeployConfiguration implements Operation {

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
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		notNull(content, "content is undefined.");
		notNull(session, "session is undefined.");
		notNull(result, "result is undefined.");

		// validate parameters
		operationUtils.validateContentType(content, LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, GitSession.class);

		try {
			// type cast model
			Git pluginModel = (Git) content;

			// type cast session
			GitSession pluginSession = (GitSession) session;

			// process model
			processModel(pluginModel, pluginSession, result);

			// compute execution state from children
			result.completeAsComputed(messageProvider, "dc.completed", null, "dc.failed", null);

		} catch (Exception e) {
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("dc.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	/**
	 * Process model commands.
	 * 
	 * @param gitModel plugin model.
	 * @param session  plugin session.
	 * @param result   execution result.
	 * 
	 * @throws Exception if processing model fails.
	 */
	void processModel(Git gitModel, GitSession session, ExecutionResult result) throws Exception {

		List<GitCommand> gitCommands = gitModel.getCommands();
		for (GitCommand command : gitCommands) {

			// enforce continuation policy
			if (!result.getContinuationPolicy().continueExecution()) {
				String message = messageProvider.getMessage("dc.contination_policy_enforcement_info");
				result.addMessage(ExecutionResult.MSG_MESSAGE, message);
				return;
			}

			if (command instanceof CloneRepository) {
				cloneRepository(session, (CloneRepository) command, result);
				continue;
			}

			if (command instanceof Log) {
				log(session, (CloneRepository) command, result);
				continue;
			}

		}
	}

	/**
	 * Clone repository .
	 * 
	 * @param session Git session.
	 * @param command Clone repository command.
	 * @param result  execution result
	 */
	void cloneRepository(GitSession session, CloneRepository command, ExecutionResult result) {

		// create execution result
		Object[] args = { command.getUri() };
		String message = messageProvider.getMessage("dc.clone_repository_info", args);
		ExecutionResult cloneResult = result.addChild(message);

		try {

			// if branch is undefined set to HEAD
			String branch = command.getBranch();
			if (branch.isBlank())
				branch = BRANCH_HEAD;

			// add branch info message
			Object[] args2 = { branch };
			message = messageProvider.getMessage("dc.clone_repository_branch_info", args2);
			cloneResult.addMessage(MSG_MESSAGE, message);

			// resolve destination dir
			File destination = coreRuntimeDirectoryProvider.resolveModelPath(command.getDestination(), result);
			
			// add destination info message
			Object[] args3 = { command.getDestination(), destination };
			message = messageProvider.getMessage("dc.clone_repository_destination_info", args3);
			cloneResult.addMessage(MSG_MESSAGE, message);

			// create destination dir if it doesn't exist
			if(!destination.exists()) {
				destination.mkdirs();
				
				// add info message
				Object[] args4 = { destination };
				message = messageProvider.getMessage("dc.clone_repository_create_destination_info", args4);
				cloneResult.addMessage(MSG_MESSAGE, message);				
			}
			
			// clone
			session.cloneRepository(command.getUri(), branch, destination);

		} catch (Exception e) {
			Object[] args5 = { e.toString() };
			cloneResult.completeAsFailure(messageProvider, "dc.clone_repository_failure", args5);
			return;
		}

		// handle successful execution
		cloneResult.completeAsSuccessful(messageProvider, "dc.clone_repository_success");
	}

	/**
	 * log repository .
	 * 
	 * @param session Git session.
	 * @param command Clone repository command.
	 * @param result  execution result
	 */
	void log(GitSession session, CloneRepository command, ExecutionResult result) {
		// TOOD: implement
	}

}
