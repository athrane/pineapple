/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
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
package com.alpha.pineapple.docker;

import static com.alpha.pineapple.docker.DockerConstants.CONTAINER_STOP_TIMEOUT;
import static com.alpha.pineapple.docker.DockerConstants.DOCKER_NAME_PREFIX;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.Validate;

import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.docker.command.BuildImageCommand;
import com.alpha.pineapple.docker.command.CreateContainerCommand;
import com.alpha.pineapple.docker.command.CreateImageCommand;
import com.alpha.pineapple.docker.command.CreateTaggedImageCommand;
import com.alpha.pineapple.docker.command.CreateTarArchiveCommand;
import com.alpha.pineapple.docker.command.DeleteContainerCommand;
import com.alpha.pineapple.docker.command.DeleteImageCommand;
import com.alpha.pineapple.docker.command.InspectContainerCommand;
import com.alpha.pineapple.docker.command.InspectImageCommand;
import com.alpha.pineapple.docker.command.KillContainerCommand;
import com.alpha.pineapple.docker.command.ListAllContainersCommand;
import com.alpha.pineapple.docker.command.ListAllImagesCommand;
import com.alpha.pineapple.docker.command.PauseContainerCommand;
import com.alpha.pineapple.docker.command.ReportOnContainersCommand;
import com.alpha.pineapple.docker.command.ReportOnImagesCommand;
import com.alpha.pineapple.docker.command.StartContainerCommand;
import com.alpha.pineapple.docker.command.StopContainerCommand;
import com.alpha.pineapple.docker.command.TestContainerCommand;
import com.alpha.pineapple.docker.command.UnpauseContainerCommand;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ContainerState;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.rest.ContainerJson;
import com.alpha.pineapple.docker.model.rest.ImageInspect;
import com.alpha.pineapple.docker.model.rest.InspectedContainerState;
import com.alpha.pineapple.docker.model.rest.ListedContainer;
import com.alpha.pineapple.docker.model.rest.ListedImage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Default implementation of the {@linkplain DockerClient} interface.
 */
public class DockerClientImpl implements DockerClient {

	/**
	 * Message provider for I18N support. Used to support creation of Docker
	 * session.
	 */
	@Resource(name = "dockerMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Create container command.
	 */
	@Resource
	Command createContainerCommand;

	/**
	 * Delete container command.
	 */
	@Resource
	Command deleteContainerCommand;

	/**
	 * List all containers command.
	 */
	@Resource
	Command listAllContainersCommand;

	/**
	 * Start container command.
	 */
	@Resource
	Command startContainerCommand;

	/**
	 * Kill container command.
	 */
	@Resource
	Command killContainerCommand;

	/**
	 * Stop container command.
	 */
	@Resource
	Command stopContainerCommand;

	/**
	 * Pause container command.
	 */
	@Resource
	Command pauseContainerCommand;

	/**
	 * Unpause container command.
	 */
	@Resource
	Command unpauseContainerCommand;

	/**
	 * Inspect container command.
	 */
	@Resource
	Command inspectContainerCommand;

	/**
	 * List all images command.
	 */
	@Resource
	Command listAllImagesCommand;

	/**
	 * Inspect image command.
	 */
	@Resource
	Command inspectImageCommand;

	/**
	 * Create image command.
	 */
	@Resource
	Command createImageCommand;

	/**
	 * Delete image command.
	 */
	@Resource
	Command deleteImageCommand;

	/**
	 * Create tagged image command.
	 */
	@Resource
	Command createTaggedImageCommand;

	/**
	 * Create TAR archive command.
	 */
	@Resource
	Command createTarArchiveCommand;

	/**
	 * Build image from Dockerfile command.
	 */
	@Resource
	Command buildImageCommand;

	/**
	 * Create report from images command.
	 */
	@Resource
	Command reportOnImagesCommand;

	/**
	 * Create report from containers command.
	 */
	@Resource
	Command reportOnContainersCommand;

	/**
	 * Test container command.
	 */
	@Resource
	Command testContainerCommand;

	/**
	 * Execution result factory.
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	@Override
	public boolean imageExists(DockerSession session, ImageInfo imageInfo) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(imageInfo, "imageInfo is undefined.");

		Validate.notNull(session, "session is undefined.");
		Validate.notNull(imageInfo, "imageInfo is undefined.");

		ExecutionResult result = executionResultFactory.startExecution("List Docker images");
		ListedImage[] images = listImages(session, result);

		if (images == null)
			return false;
		if (images.length == 0)
			return false;

		// iterate over images
		for (ListedImage image : images) {
			List<String> imageTags = image.getRepoTags();

			// skip image if no tags are defined
			if (imageTags == null)
				continue;
			if (imageTags.size() == 0)
				continue;

			// iterate over tags
			for (String imageTag : imageTags) {
				if (imageTag == null)
					continue;
				if (imageTag.isEmpty())
					continue;
				String imageFqName = imageInfo.getFullyQualifiedName();
				if (imageFqName.equalsIgnoreCase(imageTag))
					return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void createImage(DockerSession session, ImageInfo imageInfo, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(imageInfo, "imageInfo is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { imageInfo.getFullyQualifiedName() };
		String message = messageProvider.getMessage("dci.create_image_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(CreateImageCommand.SESSION_KEY, session);
		context.put(CreateImageCommand.IMAGE_INFO_KEY, imageInfo);

		// run command
		commandRunner.run(createImageCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buildImage(DockerSession session, ImageInfo imageInfo, File tarArchive, Boolean pullImageBehavior,
			ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(imageInfo, "imageInfo is undefined.");
		Validate.notNull(pullImageBehavior, "pullImageBehavior is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { imageInfo.getFullyQualifiedName() };
		String message = messageProvider.getMessage("dci.build_image_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// create context
		Context context = commandRunner.createContext();
		context.put(BuildImageCommand.SESSION_KEY, session);
		context.put(BuildImageCommand.IMAGE_INFO_KEY, imageInfo);
		context.put(BuildImageCommand.TAR_ARCHIVE_KEY, tarArchive);
		context.put(BuildImageCommand.PULL_IMAGE_KEY, pullImageBehavior);

		// run command
		commandRunner.run(buildImageCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteImage(DockerSession session, ImageInfo imageInfo, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(imageInfo, "imageInfo is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { imageInfo.getFullyQualifiedName() };
		String message = messageProvider.getMessage("dci.delete_image_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(DeleteImageCommand.SESSION_KEY, session);
		context.put(DeleteImageCommand.IMAGE_INFO_KEY, imageInfo);

		// run command
		commandRunner.run(deleteImageCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	@SuppressWarnings("unchecked")
	public void tagImage(DockerSession session, ImageInfo sourceImageInfo, ImageInfo targetImageInfo,
			ExecutionResult result) {

		Validate.notNull(session, "session is undefined.");
		Validate.notNull(sourceImageInfo, "sourceImageInfo is undefined.");
		Validate.notNull(targetImageInfo, "targetImageInfo is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { targetImageInfo.getFullyQualifiedName(), sourceImageInfo.getFullyQualifiedName() };
		String message = messageProvider.getMessage("dci.tag_image_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(CreateTaggedImageCommand.SESSION_KEY, session);
		context.put(CreateTaggedImageCommand.SOURCE_IMAGE_INFO_KEY, sourceImageInfo);
		context.put(CreateTaggedImageCommand.TARGET_IMAGE_INFO_KEY, targetImageInfo);

		// run command
		commandRunner.run(createTaggedImageCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public ListedImage[] listImages(DockerSession session, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		String message = messageProvider.getMessage("dci.list_images_info");
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(ListAllImagesCommand.SESSION_KEY, session);

		// run command
		commandRunner.run(listAllImagesCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		return (ListedImage[]) context.get(ListAllImagesCommand.IMAGES_KEY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void reportOnImages(DockerSession session, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		String message = messageProvider.getMessage("dci.report_images_info");
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(ReportOnImagesCommand.SESSION_KEY, session);

		// run command
		commandRunner.run(reportOnImagesCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public ImageInspect inspectImage(DockerSession session, ImageInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { info.getFullyQualifiedName() };
		String message = messageProvider.getMessage("dci.inspect_image_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// exit if image doesn't exists in repository
		if (!imageExists(session, info)) {
			Object[] args2 = { info.getFullyQualifiedName() };
			commandResult.completeAsFailure(messageProvider, "dci.inspect_image_notfound_failure", args2);
			throw new DockerClientException(commandResult);
		}

		// setup context
		Context context = commandRunner.createContext();
		context.put(InspectImageCommand.SESSION_KEY, session);
		context.put(InspectImageCommand.IMAGE_INFO_KEY, info);

		// run command
		commandRunner.run(inspectImageCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		return (ImageInspect) context.get(InspectImageCommand.INSPECTED_IMAGE_KEY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ContainerInstanceInfo createContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { info.getName(), info.getImageInfo().getFullyQualifiedName() };
		String message = messageProvider.getMessage("dci.create_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(CreateContainerCommand.SESSION_KEY, session);
		context.put(CreateContainerCommand.CONTAINER_INFO_KEY, info);

		// run command
		commandRunner.run(createContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		return (ContainerInstanceInfo) context.get(CreateContainerCommand.CONTAINER_INSTANCE_INFO_KEY);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void deleteContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info), info.getImageInfo().getFullyQualifiedName() };
		String message = messageProvider.getMessage("dci.delete_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(DeleteContainerCommand.SESSION_KEY, session);
		context.put(DeleteContainerCommand.CONTAINER_INFO_KEY, info);

		// run command
		commandRunner.run(deleteContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containerExists(DockerSession session, ContainerInfo info) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");

		// create prefixed container name
		String name = new StringBuilder().append(DOCKER_NAME_PREFIX).append(getContainerName(info)).toString();

		// setup context
		Context context = commandRunner.createContext();
		context.put(ListAllContainersCommand.SESSION_KEY, session);

		// run command
		ExecutionResult result = executionResultFactory.startExecution("List Docker containers");
		ExecutionResult commandResult = commandRunner.run(listAllContainersCommand, result, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// get listed containers
		ListedContainer[] listedContainers = null;
		listedContainers = (ListedContainer[]) context.get(ListAllContainersCommand.CONTAINERS_KEY);

		if (listedContainers == null)
			return false;
		if (listedContainers.length == 0)
			return false;

		// iterate over images
		for (ListedContainer listedContainer : listedContainers) {
			List<String> names = listedContainer.getNames();
			if (names.contains(name))
				return true;
		}

		return false;
	}

	/**
	 * Return true if container exists in Docker. Identification is based on the
	 * container ID.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker container instance info.
	 * 
	 * @return true if container exists in Docker repository.
	 * 
	 * @throws Exception
	 *             if query fails.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean containerExistsQueryById(DockerSession session, ContainerInstanceInfo info) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");

		String id = info.getId();

		// setup context
		Context context = commandRunner.createContext();
		context.put(ListAllContainersCommand.SESSION_KEY, session);

		// run command
		ExecutionResult result = executionResultFactory.startExecution("List Docker containers");
		ExecutionResult commandResult = commandRunner.run(listAllContainersCommand, result, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// get listed containers
		ListedContainer[] listedContainers = null;
		listedContainers = (ListedContainer[]) context.get(ListAllContainersCommand.CONTAINERS_KEY);

		if (listedContainers == null)
			return false;
		if (listedContainers.length == 0)
			return false;

		// iterate over images
		for (ListedContainer listedContainer : listedContainers) {
			String containerId = listedContainer.getId();
			if (containerId.equals(id))
				return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void startContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info) };
		String message = messageProvider.getMessage("dci.start_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(StartContainerCommand.SESSION_KEY, session);
		context.put(StartContainerCommand.CONTAINER_INFO_KEY, info);

		// execute command
		commandRunner.run(startContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	@SuppressWarnings("unchecked")
	public void stopContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info) };
		String message = messageProvider.getMessage("dci.stop_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(StopContainerCommand.SESSION_KEY, session);
		context.put(StopContainerCommand.CONTAINER_INFO_KEY, info);
		context.put(StopContainerCommand.TIMEOUT_ID_KEY, CONTAINER_STOP_TIMEOUT);

		// run command
		commandRunner.run(stopContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	@SuppressWarnings("unchecked")
	public void killContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info) };
		String message = messageProvider.getMessage("dci.kill_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// exit if container doesn't exists in repository
		if (!containerExists(session, info)) {
			Object[] args2 = { getContainerName(info) };
			commandResult.completeAsFailure(messageProvider, "dci.kill_container_notfound_failure", args2);
			throw new DockerClientException(commandResult);
		}

		// setup context
		Context context = commandRunner.createContext();
		context.put(KillContainerCommand.SESSION_KEY, session);
		context.put(KillContainerCommand.CONTAINER_INFO_KEY, info);

		// run command
		commandRunner.run(killContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public void pauseContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info) };
		String message = messageProvider.getMessage("dci.pause_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// exit if container doesn't exists in repository
		if (!containerExists(session, info)) {
			Object[] args2 = { getContainerName(info) };
			commandResult.completeAsFailure(messageProvider, "dci.pause_container_notfound_failure", args2);
			throw new DockerClientException(commandResult);
		}

		// setup context
		Context context = commandRunner.createContext();
		context.put(PauseContainerCommand.SESSION_KEY, session);
		context.put(PauseContainerCommand.CONTAINER_INFO_KEY, info);

		// run command
		commandRunner.run(pauseContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unpauseContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info) };
		String message = messageProvider.getMessage("dci.unpause_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// exit if container doesn't exists in repository
		if (!containerExists(session, info)) {
			Object[] args2 = { getContainerName(info) };
			commandResult.completeAsFailure(messageProvider, "dci.unpause_container_notfound_failure", args2);
			throw new DockerClientException(commandResult);
		}

		// setup context
		Context context = commandRunner.createContext();
		context.put(UnpauseContainerCommand.SESSION_KEY, session);
		context.put(UnpauseContainerCommand.CONTAINER_INFO_KEY, info);

		// run command
		commandRunner.run(unpauseContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP

	}

	@SuppressWarnings("unchecked")
	@Override
	public ListedContainer[] listContainers(DockerSession session, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		String message = messageProvider.getMessage("dci.list_containers_info");
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(ListAllContainersCommand.SESSION_KEY, session);

		// run command
		commandRunner.run(listAllContainersCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		return (ListedContainer[]) context.get(ListAllContainersCommand.CONTAINERS_KEY);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ContainerJson inspectContainer(DockerSession session, ContainerInfo info, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info) };
		String message = messageProvider.getMessage("dci.inspect_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// exit if container doesn't exists in repository
		if (!containerExists(session, info)) {
			Object[] args2 = { getContainerName(info) };
			commandResult.completeAsFailure(messageProvider, "dci.inspect_container_notfound_failure", args2);
			throw new DockerClientException(commandResult);
		}

		// setup context
		Context context = commandRunner.createContext();
		context.put(InspectContainerCommand.SESSION_KEY, session);
		context.put(InspectContainerCommand.CONTAINER_INFO_KEY, info);

		// run command
		commandRunner.run(inspectContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		return (ContainerJson) context.get(InspectContainerCommand.INSPECTED_CONTAINER_KEY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void reportOnContainers(DockerSession session, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		String message = messageProvider.getMessage("dci.report_containers_info");
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(ReportOnContainersCommand.SESSION_KEY, session);

		// run command
		commandRunner.run(reportOnContainersCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@SuppressWarnings("unchecked")
	@Override
	public void testContainer(DockerSession session, ContainerInfo info, ContainerState state, ExecutionResult result) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");
		Validate.notNull(state, "state is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { getContainerName(info) };
		String message = messageProvider.getMessage("dci.test_container_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(TestContainerCommand.SESSION_KEY, session);
		context.put(TestContainerCommand.CONTAINER_INFO_KEY, info);
		context.put(TestContainerCommand.CONTAINER_STATE_KEY, state);

		// run command
		commandRunner.run(testContainerCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	@Override
	public boolean isContainerRunning(DockerSession session, ContainerInstanceInfo info) {
		return isContainerRunning(session, info.getContainerInfo());
	}

	@Override
	public boolean isContainerRunning(DockerSession session, ContainerInfo info) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");

		ExecutionResult result = executionResultFactory.startExecution("Inspect container");
		ContainerJson inspectedContainer = inspectContainer(session, info, result);
		InspectedContainerState state = inspectedContainer.getState();
		return state.isRunning();
	}

	@Override
	public boolean isContainerPaused(DockerSession session, ContainerInfo info) {
		Validate.notNull(session, "session is undefined.");
		Validate.notNull(info, "info is undefined.");

		ExecutionResult result = executionResultFactory.startExecution("Inspect container");
		ContainerJson inspectedContainer = inspectContainer(session, info, result);
		InspectedContainerState state = inspectedContainer.getState();
		return state.isPaused();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void createTarArchive(File sourceDirectory, File tarArchive, ExecutionResult result) {
		Validate.notNull(sourceDirectory, "sourceDirectory is undefined.");
		Validate.notNull(tarArchive, "tarArchive is undefined.");
		Validate.notNull(result, "result is undefined.");

		// create execution result
		Object[] args = { sourceDirectory };
		String message = messageProvider.getMessage("dci.create_tar_archive_info", args);
		ExecutionResult commandResult = result.addChild(message);

		// setup context
		Context context = commandRunner.createContext();
		context.put(CreateTarArchiveCommand.SOURCE_DIRECTORY_KEY, sourceDirectory);
		context.put(CreateTarArchiveCommand.TAR_ARCHIVE_KEY, tarArchive);

		// run command
		commandRunner.run(createTarArchiveCommand, commandResult, context);

		// handle unsuccessful execution
		if (!commandResult.isSuccess())
			throw new DockerClientException(commandResult);

		// handle successful execution
		// NO-OP
	}

	/**
	 * Get name from container info while testing that the value is defined.
	 * 
	 * @param container
	 *            info
	 * 
	 * @return container name.
	 */
	String getContainerName(ContainerInfo info) {
		Validate.notNull(info, "Container info is undefined");

		// get name
		String name = info.getName();

		Validate.notNull(name, "Container name is undefined");
		Validate.notEmpty(name, "Container name is empty");
		return name;
	}

}
