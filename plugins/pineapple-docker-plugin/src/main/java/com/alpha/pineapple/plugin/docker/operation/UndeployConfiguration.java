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
import static com.alpha.javautils.ArgumentUtils.notNull;

import java.util.List;

import javax.annotation.Resource;

import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.docker.DockerConstants;
import com.alpha.pineapple.plugin.docker.model.Container;
import com.alpha.pineapple.plugin.docker.model.Docker;
import com.alpha.pineapple.plugin.docker.model.DockerCommand;
import com.alpha.pineapple.plugin.docker.model.Image;
import com.alpha.pineapple.plugin.docker.model.ImageFromDockerfile;
import com.alpha.pineapple.plugin.docker.model.Mapper;
import com.alpha.pineapple.plugin.docker.model.TaggedImage;
import com.alpha.pineapple.session.Session;

/**
 * Implements the undeploy-configuration operation.
 * 
 * Deletes the set of of Docker images and tagged images defined in the model.
 */
@PluginOperation(OperationNames.UNDEPLOY_CONFIGURATION)
public class UndeployConfiguration implements Operation {

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
	 * Docker client
	 */
	@Resource
	DockerClient dockerClient;

	/**
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		notNull(content, "content is undefined.");
		notNull(session, "session is undefined.");
		notNull(result, "result is undefined.");

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
			result.completeAsComputed(messageProvider, "uc.completed", null, "uc.failed", null);
		} catch (Exception e) {
			Object[] args = { e.toString() };
			String message = messageProvider.getMessage("uc.error", args);
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

		List<DockerCommand> agentCommands = dockerModel.getCommands();
		for (DockerCommand command : agentCommands) {

			// enforce continuation policy
			if (!result.getContinuationPolicy().continueExecution()) {
				String message = messageProvider.getMessage("uc.contination_policy_enforcement_info");
				result.addMessage(ExecutionResult.MSG_MESSAGE, message);
				return;
			}

			if (command instanceof Image) {
				deleteImage(session, (Image) command, result);
				continue;
			}

			if (command instanceof TaggedImage) {
				deleteTaggedImage(session, (TaggedImage) command, result);
				continue;
			}

			if (command instanceof ImageFromDockerfile) {
				deleteImageFromDockerfile(session, (ImageFromDockerfile) command, result);
				continue;
			}

			if (command instanceof Container) {
				deleteContainer(session, (Container) command, result);
				continue;
			}
		}
	}

	/**
	 * Delete Docker image.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            image command.
	 * @param result
	 *            execution result
	 */
	void deleteImage(DockerSession session, Image command, ExecutionResult result) {
		ImageInfo info = mapper.mapImageForDeletion(command);
		dockerClient.deleteImage(session, info, result);
	}

	/**
	 * Delete tagged Docker image.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            image command.
	 * @param result
	 *            execution result
	 */
	void deleteTaggedImage(DockerSession session, TaggedImage command, ExecutionResult result) {
		ImageInfo info = mapper.mapTaggedImageForDeletion(command);
		dockerClient.deleteImage(session, info, result);
	}

	/**
	 * Delete Docker image from Dockerfile.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            image from Dockerfile command.
	 * @param result
	 *            execution result
	 */
	void deleteImageFromDockerfile(DockerSession session, ImageFromDockerfile command, ExecutionResult result) {
		ImageInfo info = mapper.mapImageFromDockerfileForDeletion(command);
		dockerClient.deleteImage(session, info, result);
	}

	/**
	 * Delete Docker container.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            create container command.
	 * @param result
	 *            execution result
	 */
	void deleteContainer(DockerSession session, Container command, ExecutionResult result) {
		ContainerInfo info = mapper.mapContainerForDeletion(command);
		dockerClient.deleteContainer(session, info, result);
	}

}
