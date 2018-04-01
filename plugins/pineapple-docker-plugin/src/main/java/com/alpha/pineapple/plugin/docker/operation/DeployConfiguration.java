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

import static com.alpha.pineapple.plugin.docker.DockerConstants.LEGAL_CONTENT_TYPES;
import static com.alpha.pineapple.plugin.docker.DockerConstants.TAR_ARCHIVE;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.OperationUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.plugin.docker.model.Container;
import com.alpha.pineapple.plugin.docker.model.ContainerConfiguration;
import com.alpha.pineapple.plugin.docker.model.Docker;
import com.alpha.pineapple.plugin.docker.model.DockerCommand;
import com.alpha.pineapple.plugin.docker.model.Image;
import com.alpha.pineapple.plugin.docker.model.ImageFromDockerfile;
import com.alpha.pineapple.plugin.docker.model.Mapper;
import com.alpha.pineapple.plugin.docker.model.TaggedImage;
import com.alpha.pineapple.session.Session;

/**
 * Implements the deploy-configuration operation.
 * 
 * Creates a set of of Docker images and tagged images defined in the model.
 */
@PluginOperation(OperationNames.DEPLOY_CONFIGURATION)
public class DeployConfiguration implements Operation {

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
	 * Model mapper object.
	 */
	@Resource
	Mapper mapper;

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	/**
	 * Runtime directory provider.
	 */
	@Resource
	RuntimeDirectoryProvider coreRuntimeDirectoryProvider;

	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		// validate parameters
		Validate.notNull(content, "content is undefined.");
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(result, "result is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { content.getClass().getName(), content };
			String message = messageProvider.getMessage("dc.start", args);
			logger.debug(message);
		}

		// validate parameters
		operationUtils.validateContentType(content, LEGAL_CONTENT_TYPES);
		operationUtils.validateSessionType(session, DockerSession.class);

		try {
			// type cast model
			Docker pluginModel = (Docker) content;

			// type cast session
			DockerSession dockerSession = (DockerSession) session;

			// process model
			processModel(pluginModel, dockerSession, result);

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
				String message = messageProvider.getMessage("dc.contination_policy_enforcement_info");
				result.addMessage(ExecutionResult.MSG_MESSAGE, message);
				return;
			}

			if (command instanceof Image) {
				createImage(session, (Image) command, result);
				continue;
			}

			if (command instanceof TaggedImage) {
				createTaggedImage(session, (TaggedImage) command, result);
				continue;
			}

			if (command instanceof ImageFromDockerfile) {
				buildImageFromDockerfile(session, (ImageFromDockerfile) command, result);
				continue;
			}

			if (command instanceof Container) {
				createContainer(session, (Container) command, containerConfigs, result);
				controlContainer(session, (Container) command, result);
				continue;
			}

		}
	}

	/**
	 * Create Docker image.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            create image command.
	 * @param result
	 *            execution result
	 */
	void createImage(DockerSession session, Image command, ExecutionResult result) {
		ImageInfo info = mapper.mapImageForCreation(command);
		dockerClient.createImage(session, info, result);
	}

	/**
	 * Create tagged Docker image.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            create image command.
	 * @param result
	 *            execution result
	 */
	void createTaggedImage(DockerSession session, TaggedImage command, ExecutionResult result) {
		ImageInfo sourceImageInfo = mapper.mapTaggedSourceImageForCreation(command);
		ImageInfo targetImageInfo = mapper.mapTaggedTargetImageImageForCreation(command);
		dockerClient.tagImage(session, sourceImageInfo, targetImageInfo, result);
	}

	/**
	 * Build Docker image from DockerFile.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            build image command.
	 * @param result
	 *            execution result
	 */
	void buildImageFromDockerfile(DockerSession session, ImageFromDockerfile command, ExecutionResult result) {

		// create composite result for building image
		Object[] args = { command.getTargetImage().getRepository(), command.getTargetImage().getTag(),
				command.getSourceDirectory() };
		String message = messageProvider.getMessage("dc.build_image_info", args);
		ExecutionResult compositeResult = result.addChild(message);

		// resolve module path
		File sourceDirectory = coreRuntimeDirectoryProvider.resolveModelPath(command.getSourceDirectory(), result);
		command.setSourceDirectory(sourceDirectory.getAbsolutePath());

		// create path to TAR archive
		File tempDirectory = coreRuntimeDirectoryProvider.getTempDirectory();
		File tarArhive = new File(tempDirectory, TAR_ARCHIVE);

		// create archive
		dockerClient.createTarArchive(sourceDirectory, tarArhive, compositeResult);

		// fail if archive creation failed
		ExecutionResult tarArchiveResult = compositeResult.getFirstChild();
		if (!tarArchiveResult.isSuccess()) {
			// complete composite result
			compositeResult.completeAsFailure(messageProvider, "dc.build_image_create_tar_failed");
			return;
		}

		// build image
		ImageInfo imageInfo = mapper.mapImageFromDockerfileForCreation(command);
		dockerClient.buildImage(session, imageInfo, tarArhive, mapper.getPullImageBehavior(command), compositeResult);

		// complete composite result
		compositeResult.completeAsComputed(messageProvider, "dc.build_image_completed", null, "dc.build_image_failed",
				null);
	}

	/**
	 * Create Docker container.
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
	void createContainer(DockerSession session, Container command, Map<String, ContainerConfiguration> containerConfigs,
			ExecutionResult result) {
		ContainerInfo info = mapper.mapContainerForCreation(command, containerConfigs);
		dockerClient.createContainer(session, info, result);
	}

	/**
	 * Control container.
	 * 
	 * @param session
	 *            Docker session.
	 * @param command
	 *            create container command.
	 * @param result
	 *            execution result
	 */
	void controlContainer(DockerSession session, Container command, ExecutionResult result) {
		if (!isStateDirectiveDefined(command))
			return;
		ContainerInfo info = mapper.mapContainerForControl(command);
		switch (command.getState()) {

		case RUNNING:
			dockerClient.startContainer(session, info, result);
			return;

		case STOPPED:
			dockerClient.stopContainer(session, info, result);
			return;

		case PAUSED:
			dockerClient.pauseContainer(session, info, result);
			return;

		default:
			return;
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
