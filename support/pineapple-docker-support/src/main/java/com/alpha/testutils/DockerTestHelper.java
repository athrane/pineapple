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

package com.alpha.testutils;

import static com.alpha.pineapple.docker.DockerConstants.DEFAULT_CENTOS_REPOSITORY;
import static com.alpha.pineapple.docker.DockerConstants.DEFAULT_CONNECT_TIMEOUT;
import static com.alpha.pineapple.docker.DockerConstants.DEFAULT_HOST;
import static com.alpha.pineapple.docker.DockerConstants.DEFAULT_PORT;
import static com.alpha.pineapple.docker.DockerConstants.DOCKERFILE_NAME;
import static com.alpha.pineapple.docker.DockerConstants.LATEST_IMAGE_TAG;
import static com.alpha.pineapple.docker.DockerConstants.TAR_ARCHIVE_EXTENSION;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_BASE_UBUNTU_IMAGE;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_DEFAULT_CENTOS_IMAGE;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_DEFAULT_REGISTRY_IMAGE;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_DEFAULT_REGISTRY_TAG;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_TINY_IMAGE;
import static com.alpha.testutils.DockerTestConstants.TEST_DOCKER_USER_REPOSITORY;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.alpha.pineapple.docker.DockerClient;
import com.alpha.pineapple.docker.DockerConstants;
import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.InfoBuilder;
import com.alpha.pineapple.docker.model.rest.ContainerJson;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.docker.session.DockerSessionImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.resource.ResourcePropertyGetter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper class which supports usage of Docker in test cases.
 */
public class DockerTestHelper {

	/**
	 * Docker client.
	 */
	@Resource
	DockerClient dockerClient;

	/**
	 * Message provider for I18N support. Used to support creation of Docker
	 * session.
	 */
	@Resource(name = "dockerMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Resource property getter. Used to support creation of Docker session.
	 */
	@Resource
	ResourcePropertyGetter propertyGetter;

	/**
	 * Rest template. Used to support creation of Docker session.
	 */
	@Resource
	RestTemplate restTemplate;

	/**
	 * Rest template. Used to support creation of Docker session.
	 */
	@Resource
	RestTemplate restTemplateWithoutMessageConverters;

	/**
	 * Jackson object mapper. Used to support creation of Docker session.
	 */
	@Resource
	ObjectMapper jacksonObjectMapper;

	/**
	 * Docker info object builder.
	 */
	@Resource
	InfoBuilder dockerInfoBuilder;

	/**
	 * Execution result factory.
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	/**
	 * Create connected Docker session with default settings: host: 192.168.34.10
	 * port: 8082 timeout: 5000
	 * 
	 * @return connected Docker session with default settings.
	 */
	public DockerSession createDefaultSession() {
		return createDefaultSession(messageProvider, propertyGetter, restTemplate, restTemplateWithoutMessageConverters,
				jacksonObjectMapper);
	}

	/**
	 * Create connected Docker session with default settings: host: 192.168.34.10
	 * port: 8082 timeout: 5000
	 * 
	 * @return connected Docker session with default settings and RestTemaplate
	 *         configured with {@linkplain LoggingRequestInterceptorImpl} for
	 *         logging HTTP responses.
	 */
	public DockerSession createDefaultSessionWithLoggingInterceptor() {
		DockerSession session = createDefaultSession(messageProvider, propertyGetter, restTemplate,
				restTemplateWithoutMessageConverters, jacksonObjectMapper);

		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		interceptors.add(new LoggingRequestInterceptorImpl());
		restTemplate.setInterceptors(interceptors);
		return session;
	}

	/**
	 * Create connected Docker session with default settings: host: 192.168.34.10
	 * port: 8082 timeout: 5000
	 * 
	 * @param messageProvider
	 *            I18N message provider.
	 * @param propertyGetter
	 *            resource property getter.
	 * @param restTemplate
	 *            REST Template with message converters.
	 * @param restTemplateWithoutMessageConverters
	 *            REST Template without message converters.
	 * @param objectMapper
	 *            Jackson object mapper.
	 * 
	 * @return connected Docker session with default settings.
	 */
	public DockerSession createDefaultSession(MessageProvider messageProvider, ResourcePropertyGetter propertyGetter,
			RestTemplate restTemplate, RestTemplate restTemplateWithoutMessageConverters, ObjectMapper objectMapper) {

		String randomUser = RandomStringUtils.randomAlphabetic(10);
		String randomPassword = RandomStringUtils.randomAlphabetic(10);

		try {

			// create session and connect
			DockerSession session = new DockerSessionImpl(messageProvider, propertyGetter, restTemplate,
					restTemplateWithoutMessageConverters, jacksonObjectMapper);

			session.connect(DEFAULT_HOST, Integer.parseInt(DEFAULT_PORT), randomUser, randomPassword,
					Integer.parseInt(DEFAULT_CONNECT_TIMEOUT));

			return session;

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.create_default_session_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Create image info for randomized image in the user repository named
	 * "pineapple-ci/RANDOM-VALUE" with the tag "latest".
	 * 
	 * @return image info for randomized image.
	 */
	public ImageInfo createDefaultTaggedImageInfo() {
		String targetRepository = new StringBuilder().append(TEST_DOCKER_USER_REPOSITORY)
				.append(DockerConstants.DOCKER_NAME_PREFIX).append(RandomStringUtils.randomAlphabetic(10).toLowerCase())
				.toString();
		return dockerInfoBuilder.buildImageInfo(targetRepository, LATEST_IMAGE_TAG);
	}

	/**
	 * Create image info for tiny (as in small size in MB) image for repository test
	 * of image operations.
	 * 
	 * @return image info for randomized image.
	 */
	public ImageInfo createDefaultTinyImageInfo() {
		return dockerInfoBuilder.buildImageInfo(TEST_DOCKER_TINY_IMAGE, LATEST_IMAGE_TAG);
	}

	/**
	 * Create image info for "ubuntu:latest" image.
	 * 
	 * @return image info for "ubuntu:latest" image.
	 */
	public ImageInfo createUbuntuBaseImageInfo() {
		return dockerInfoBuilder.buildImageInfo(TEST_DOCKER_BASE_UBUNTU_IMAGE, LATEST_IMAGE_TAG);
	}

	/**
	 * Create image info for root CentOS image, e.g. "centos:latest"
	 * 
	 * @return image info for root CentOS image.
	 */
	public ImageInfo createRootCentOSImageInfo() {
		return dockerInfoBuilder.buildImageInfo(DEFAULT_CENTOS_REPOSITORY, LATEST_IMAGE_TAG);
	}

	/**
	 * Create image info for default CentOS image, e.g. "pineapple_ci/centos"
	 * 
	 * @return image info for root CentOS image.
	 */
	public ImageInfo createDefaultCentOSImageInfo() {
		return dockerInfoBuilder.buildImageInfo(TEST_DOCKER_DEFAULT_CENTOS_IMAGE);
	}

	/**
	 * Create image info for default Docker registry "registry:2"
	 * 
	 * @return image info for default Docker registry "registry:2".
	 */
	public ImageInfo createDefaultRegistryImageInfo() {
		return dockerInfoBuilder.buildImageInfo(TEST_DOCKER_DEFAULT_REGISTRY_IMAGE, TEST_DOCKER_DEFAULT_REGISTRY_TAG);
	}

	/**
	 * Create default Docker image.
	 * 
	 * Pulls root CentOS image and tags root CentOS image into "pineapple_ci/centos"
	 * repository which is intended to be used by tests.
	 * 
	 * @param session
	 *            Docker session.
	 * 
	 * @return image info for created default Docker image.
	 * 
	 * @throws Exception
	 *             if query fails.
	 */
	public ImageInfo createDefaultImage(DockerSession session) throws Exception {

		// create root CentOS image
		ImageInfo rootImageInfo = createRootCentOSImageInfo();
		createImage(session, rootImageInfo);

		// exit if default image exists
		ImageInfo defaultImageInfo = createDefaultCentOSImageInfo();
		if (dockerClient.imageExists(session, defaultImageInfo))
			return defaultImageInfo;

		// tag image
		tagImage(session, rootImageInfo, defaultImageInfo);
		return defaultImageInfo;
	}

	/**
	 * Validate image exists in repository.
	 * 
	 * @param session
	 *            Docker session.
	 * @param imageInfo
	 *            Docker image info.
	 * 
	 * @throws DockerTestHelperException
	 *             if validation fails.
	 */
	void validateImageExistsInRepository(DockerSession session, ImageInfo imageInfo) {
		if (dockerClient.imageExists(session, imageInfo))
			return;
		Object[] args = { imageInfo.getFullyQualifiedName() };
		String message = messageProvider.getMessage("dh.image_doesnt_exist_failure", args);
		throw new DockerTestHelperException(message);
	}

	/**
	 * Create Docker image.
	 * 
	 * If image exist in repository then the operation is aborted.
	 * 
	 * @param session
	 *            Docker session.
	 * @param imageInfo
	 *            image info.
	 */
	public void createImage(DockerSession session, ImageInfo imageInfo) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Create Docker image");
			dockerClient.createImage(session, imageInfo, result);
			validateResultIsSuccessful(result.getFirstChild());
			validateImageExistsInRepository(session, imageInfo);

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.create_image_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Tag Docker image into repository.
	 * 
	 * If image doesn't exist in repository then the operation fails.
	 * 
	 * @param session
	 *            Docker session.
	 * @param sourceImageInfo
	 *            source image info.
	 * @param targetImageInfo
	 *            target image info.
	 */
	public void tagImage(DockerSession session, ImageInfo sourceImageInfo, ImageInfo targetImageInfo) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Tag Docker image");
			dockerClient.tagImage(session, sourceImageInfo, targetImageInfo, result);
			validateResultIsSuccessful(result.getFirstChild());
			validateImageExistsInRepository(session, targetImageInfo);

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.tag_image_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Delete Docker image in repository.
	 * 
	 * If image doesn't exist in repository then the operation is aborted.
	 * 
	 * @param session
	 *            Docker session.
	 * @param imageInfo
	 *            image info.
	 */
	public void deleteImage(DockerSession session, ImageInfo imageInfo) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Delete Docker image");
			if (!dockerClient.imageExists(session, imageInfo))
				return;
			dockerClient.deleteImage(session, imageInfo, result);
			validateResultIsSuccessful(result.getFirstChild());
		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.delete_image_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Return true if container exists in Docker. Identification is based on the
	 * name.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker container info.
	 * 
	 * @return true if container exists in Docker repository.
	 * 
	 * @throws Exception
	 *             if query fails.
	 */
	@Deprecated
	public boolean containerExists(DockerSession session, ContainerInfo info) {
		try {
			return dockerClient.containerExists(session, info);
		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.container_doesnt_exist_failure", args);
			throw new DockerTestHelperException(message, e);
		}
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
	@Deprecated
	public boolean containerExistsQueryById(DockerSession session, ContainerInstanceInfo info) throws Exception {
		try {
			return dockerClient.containerExistsQueryById(session, info);
		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.container_doesnt_exist_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Return true if container is running in Docker.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker container instance info.
	 * 
	 * @return true if container is running in Docker.
	 * 
	 * @throws Exception
	 *             if query fails.
	 */
	@Deprecated
	public boolean isContainerRunning(DockerSession session, ContainerInstanceInfo info) throws Exception {
		return dockerClient.isContainerRunning(session, info);
	}

	/**
	 * Inspect container.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker container info.
	 * 
	 * @return Inspected container info object.
	 * 
	 * @throws Exception
	 *             if query fails.
	 */
	public ContainerJson inspectContainer(DockerSession session, ContainerInfo info) throws Exception {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Inspect Docker container");
			ContainerJson inspectedContainer = dockerClient.inspectContainer(session, info, result);
			validateResultIsSuccessful(result.getFirstChild());
			return inspectedContainer;

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.inspect_container_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Create Docker container.
	 * 
	 * @param session
	 *            Docker session.
	 * @param name
	 *            container name.
	 * @param info
	 *            image info.
	 * 
	 * @return container instance info.
	 */
	public ContainerInstanceInfo createContainer(DockerSession session, String name, ImageInfo info) {
		ContainerInfo containerInfo = dockerInfoBuilder.buildContainerInfo(name, info);
		return createContainer(session, containerInfo);
	}

	/**
	 * Create Docker container.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            container info.
	 * 
	 * @return container instance info.
	 */
	public ContainerInstanceInfo createContainer(DockerSession session, ContainerInfo info) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Create Docker container");
			ContainerInstanceInfo instanceInfo = dockerClient.createContainer(session, info, result);
			validateResultIsSuccessful(result.getFirstChild());
			return instanceInfo;

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.create_container_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Delete Docker container.
	 * 
	 * @param session
	 *            Docker session.
	 * @param containerInstanceInfo
	 *            container info.
	 */
	public void deleteContainer(DockerSession session, ContainerInfo info) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Delete Docker container");
			dockerClient.deleteContainer(session, info, result);
			validateResultIsSuccessful(result.getFirstChild());

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.delete_container_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Start Docker container.
	 * 
	 * If container doesn't exist then the operation is aborted.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker container info.
	 */
	public void startContainer(DockerSession session, ContainerInfo info) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Start Docker container");
			dockerClient.startContainer(session, info, result);
			validateResultIsSuccessful(result.getFirstChild());

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.start_container_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Kill Docker container.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker container info.
	 */
	public void killContainer(DockerSession session, ContainerInfo info) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Kill Docker container");
			dockerClient.killContainer(session, info, result);
			validateResultIsSuccessful(result.getFirstChild());

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.kill_container_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Stop Docker container.
	 * 
	 * If container doesn't exist then the operation is aborted.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker instance info.
	 */
	public void stopContainer(DockerSession session, ContainerInfo info) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Stop Docker container");
			dockerClient.stopContainer(session, info, result);
			validateResultIsSuccessful(result.getFirstChild());

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.stop_container_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Pause Docker container.
	 * 
	 * If container doesn't exist then the operation is aborted.
	 * 
	 * @param session
	 *            Docker session.
	 * @param info
	 *            Docker container info.
	 */
	public void pauseContainer(DockerSession session, ContainerInfo info) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Pause Docker container");
			dockerClient.pauseContainer(session, info, result);
			validateResultIsSuccessful(result.getFirstChild());

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.pause_container_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Create DockerFile in target directory with FROM command.
	 * 
	 * If target directory doesn't exist then it is created. the created Docker file
	 * will contain a a FROM command with the image info supplied as parameter.
	 * 
	 * @param targetDirectory
	 *            directory where Docker it created.
	 * @param imageInfo
	 */
	public void createDockerFileWithFromCommand(File targetDirectory, ImageInfo imageInfo) {

		try {

			// create directory if it doesn't exist
			if (targetDirectory.exists())
				targetDirectory.mkdir();

			File dockerFile = new File(targetDirectory, DOCKERFILE_NAME);

			// create file content
			Collection<String> lines = new ArrayList<String>();
			lines.add("FROM " + imageInfo.getFullyQualifiedName());

			// write the file
			FileUtils.writeLines(dockerFile, lines);

		} catch (IOException e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.create_dockerfile_failure", args);
			throw new DockerTestHelperException(message, e);
		}

	}

	/**
	 * Create DockerFile in target directory with FROM and MAINTAINER commands.
	 * 
	 * If target directory doesn't exist then it is created. the created Docker file
	 * will contain a MAINTAINER and a FROM command with the image info supplied as
	 * parameter.
	 * 
	 * @param targetDirectory
	 *            directory where Docker it created.
	 * @param imageInfo
	 */
	public void createDockerFileWithFromAndMaintainerCommands(File targetDirectory, ImageInfo imageInfo) {

		try {

			// create directory if it doesn't exist
			if (targetDirectory.exists())
				targetDirectory.mkdir();

			File dockerFile = new File(targetDirectory, DOCKERFILE_NAME);

			// create file content
			Collection<String> lines = new ArrayList<String>();
			lines.add("FROM " + imageInfo.getFullyQualifiedName());
			lines.add("MAINTAINER The Pineapple project");

			// write the file
			FileUtils.writeLines(dockerFile, lines);

		} catch (IOException e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.create_dockerfile_failure", args);
			throw new DockerTestHelperException(message, e);
		}

	}

	/**
	 * Create TAR archive for building a Docker image.
	 * 
	 * @param sourceDirectory
	 *            source directory which is compressed as a TAR archive.
	 * @param tarArhive
	 *            absolute path and name of the created TAR archive.
	 */
	public void createTarArchive(File sourceDirectory, File tarArhive) {
		try {
			ExecutionResult result = executionResultFactory.startExecution("Create TAR archive");
			dockerClient.createTarArchive(sourceDirectory, tarArhive, result);
			validateResultIsSuccessful(result.getFirstChild());

		} catch (Exception e) {
			Object[] args = { e.getMessage() };
			String message = messageProvider.getMessage("dh.create_tar_archive_failure", args);
			throw new DockerTestHelperException(message, e);
		}
	}

	/**
	 * Create name for TAR archive.
	 * 
	 * @param targetDirectory
	 *            directory where archive is placed.
	 * @param nameWithoutExtension
	 *            archive name without extension.
	 * @return TAR archive name in target directory with extension .tar
	 */
	public File createTarArchiveName(File targetDirectory, String nameWithoutExtension) {
		String fileName = nameWithoutExtension + TAR_ARCHIVE_EXTENSION;
		return new File(targetDirectory, fileName);
	}

	/**
	 * Get name from container info while testing that the value is defined.
	 * 
	 * @param container
	 *            info
	 * 
	 * @return container name.
	 */
	public String getContainerName(ContainerInfo info) {
		Validate.notNull(info, "Container info is undefined");

		// get name
		String name = info.getName();

		Validate.notNull(name, "Container name is undefined");
		Validate.notEmpty(name, "Container name is empty");
		return name;
	}

	/**
	 * Get ID from container instance info while testing that the value is defined.
	 * 
	 * @param container
	 *            instance info
	 * 
	 * @return container ID.
	 */
	public String getContainerId(ContainerInstanceInfo info) {
		Validate.notNull(info.getContainerInfo(), "Container info is undefined");

		// get id
		String id = info.getId();

		Validate.notNull(id, "Container id is undefined");
		Validate.notEmpty(id, "Container id is empty");
		return id;
	}

	/**
	 * Validate execution result is successful.
	 * 
	 * @param result
	 *            execution result.
	 * 
	 * @throws DockerTestHelperException
	 *             if validation fails.
	 */
	public void validateResultIsSuccessful(ExecutionResult result) {
		if (result.isSuccess())
			return;

		Object[] args = { result.getDescription() };
		String message = messageProvider.getMessage("dh.unsuccessful_result_failure", args);
		throw new DockerTestHelperException(message);
	}

	/**
	 * Validate all child execution result are successful.
	 * 
	 * @param result
	 *            execution result.
	 * 
	 * @throws DockerTestHelperException
	 *             if validation fails.
	 */
	public void validateChildResultsAreSuccessful(ExecutionResult result) {
		for (ExecutionResult childResult : result.getChildren()) {
			validateResultIsSuccessful(childResult);
		}
	}

}
