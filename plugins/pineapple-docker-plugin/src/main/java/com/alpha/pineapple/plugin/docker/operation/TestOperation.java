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

package com.alpha.pineapple.plugin.docker.operation;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.Validate;

import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerState;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.docker.DockerConstants;
import com.alpha.pineapple.plugin.docker.model.CommandControlState;
import com.alpha.pineapple.plugin.docker.model.Container;
import com.alpha.pineapple.plugin.docker.model.ContainerConfiguration;
import com.alpha.pineapple.plugin.docker.model.Docker;
import com.alpha.pineapple.plugin.docker.model.DockerCommand;
import com.alpha.pineapple.plugin.docker.model.Mapper;
import com.alpha.pineapple.session.Session;
import com.fasterxml.jackson.core.type.ResolvedType;

/**
 * Implements the test operation.
 * 
 * Creates a report consisting of execution results which lists result of
 * comparing the content of the model with the set of existing Docker images and
 * containers.
 */
@PluginOperation(OperationNames.TEST)
public class TestOperation implements Operation {

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
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		// validate parameters
		Validate.notNull(content, "content is undefined.");
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(result, "result is undefined.");

		// validate parameters
		operationUtils.validateContentType(content, DockerConstants.LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, DockerSession.class);

		try {

			// type cast model
			Docker pluginModel = (Docker) content;

			// type cast session
			DockerSession dockerSession = (DockerSession) session;

			// process model
			processModel(pluginModel, dockerSession, result);

			// compute execution state from children
			result.completeAsComputed(messageProvider, "t.completed", null, "t.failed", null);
		} catch (Exception e) {
			Object[] args = { e };
			String message = messageProvider.getMessage("t.error", args);
			throw new PluginExecutionFailedException(message, e);
		}
	}

	/**
	 * Process model commands.
	 * 
	 * @param dockerModel
	 *            plugin model.
	 * @param session
	 *            Docker session.
	 * @param result
	 *            execution result.
	 */
	void processModel(Docker dockerModel, DockerSession session, ExecutionResult result) {

		Map<String, ContainerConfiguration> containerConfigs = mapper.extractContainerDefinitions(dockerModel);

		List<DockerCommand> agentCommands = dockerModel.getCommands();
		for (DockerCommand command : agentCommands) {

			// enforce continuation policy
			if (!result.getContinuationPolicy().continueExecution()) {
				String message = messageProvider.getMessage("t.contination_policy_enforcement_info");
				result.addMessage(ExecutionResult.MSG_MESSAGE, message);
				return;
			}

			if (command instanceof Container) {
				testContainer(session, (Container) command, containerConfigs, result);
				continue;
			}
		}
	}

	/**
	 * Test container configuration.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            create container command.
	 * @param containerConfigs
	 *            container configurations.
	 * @param result
	 *            execution result
	 */
	void testContainer(DockerSession session, Container command, Map<String, ContainerConfiguration> containerConfigs,
			ExecutionResult result) {

		ContainerState expectedState = resolveStateFromModel(command);
		ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);
		dockerClient.testContainer(session, info, expectedState, result);
	}

	/**
	 * Resolve container state as a string from plugin model.
	 * 
	 * @param command
	 *            plugin command.
	 * 
	 * @return container state from plugin model.
	 */
	ContainerState resolveStateFromModel(Container command) {
		CommandControlState state = CommandControlState.RUNNING;
		if (isStateDirectiveDefined(command))
			state = command.getState();
		switch (state) {
		case STOPPED:
			return ContainerState.STOPPED;
		case PAUSED:
			return ContainerState.PAUSED;
		case RUNNING:
		default:
			return ContainerState.RUNNING;
		}
	}

	/**
	 * Returns true if container state directive is defined.
	 * 
	 * @param command
	 *            create container command.
	 * 
	 * @return true if container state directive is defined.
	 */
	boolean isStateDirectiveDefined(Container command) {
		return (command.getState() != null);
	}

}
