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

package com.alpha.testutils;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Random;

import com.alpha.javautils.NetworkUtils;
import com.alpha.pineapple.plugin.docker.model.Container;
import com.alpha.pineapple.plugin.docker.model.ContainerConfiguration;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationEnvironmentVariable;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationEnvironmentVariables;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationExposedPort;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationExposedPorts;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationHostConfig;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationHostConfigPortBinding;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationHostConfigPortBindings;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationLabel;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationLabels;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationVolume;
import com.alpha.pineapple.plugin.docker.model.ContainerConfigurationVolumes;
import com.alpha.pineapple.plugin.docker.model.Docker;
import com.alpha.pineapple.plugin.docker.model.DockerCommand;
import com.alpha.pineapple.plugin.docker.model.EmbeddedContainerConfiguration;
import com.alpha.pineapple.plugin.docker.model.Image;
import com.alpha.pineapple.plugin.docker.model.ImageFromDockerfile;
import com.alpha.pineapple.plugin.docker.model.IpPort;
import com.alpha.pineapple.plugin.docker.model.ObjectFactory;
import com.alpha.pineapple.plugin.docker.model.TaggedImage;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing by creating content for operations.
 */
public class ObjectMotherContent {

	/**
	 * Docker object factory.
	 */
	ObjectFactory dockerFactory;

	/**
	 * ObjectMotherContent constructor.
	 */
	public ObjectMotherContent() {
		dockerFactory = new ObjectFactory();
	}

	/**
	 * Create empty Docker document.
	 * 
	 * @return empty Docker document.
	 */
	public Docker createEmptyDockerModel() {
		return dockerFactory.createDocker();
	}

	/**
	 * Create image command.
	 * 
	 * @param repo
	 *            image repository.
	 * @param tag
	 *            image tag.
	 * 
	 * @return image command.
	 */
	public Image createImageCommand(String repo, String tag) {
		Image command = dockerFactory.createImage();
		command.setRepository(repo);
		command.setTag(tag);
		return command;
	}

	/**
	 * Create Docker document with image command.
	 * 
	 * @param repo
	 *            image repository.
	 * @param tag
	 *            image tag.
	 * 
	 * @return Docker document with image command.
	 */
	public Docker createDockerModelWithImageCommand(String repo, String tag) {
		Docker model = dockerFactory.createDocker();
		List<DockerCommand> cmds = model.getCommands();
		cmds.add(createImageCommand(repo, tag));
		return model;
	}

	/**
	 * Create Docker document with image from Dcokerfile command.
	 * 
	 * @param targetRepo
	 *            image repository.
	 * @param targetTag
	 *            image tag.
	 * @param sourceDirectory
	 *            source directory.
	 * @param pullImage
	 *            pull image.
	 * 
	 * @return Docker document with image command.
	 */
	public Docker createDockerModelWithImageFromDockerfileCommand(String targetRepo, String targetTag,
			String sourceDirectory, boolean pullImage) {
		Docker model = dockerFactory.createDocker();
		List<DockerCommand> cmds = model.getCommands();
		cmds.add(createImageFromDockerfileCommand(targetRepo, targetTag, sourceDirectory, pullImage));
		return model;
	}

	/**
	 * Create Docker document with tagged image command.
	 * 
	 * @param sourceRepo
	 *            image repository.
	 * @param sourceTag
	 *            image tag.
	 * @param targetRepo
	 *            image repository.
	 * @param targetTag
	 *            image tag.
	 * 
	 * @return Docker document with tagged image command.
	 */
	public Docker createDockerModelWithTaggedImageCommand(String sourceRepo, String sourceTag, String targetRepo,
			String targetTag) {
		Docker model = dockerFactory.createDocker();
		List<DockerCommand> cmds = model.getCommands();
		cmds.add(createTaggedImageCommand(sourceRepo, sourceTag, targetRepo, targetTag));
		return model;
	}

	/**
	 * Create Docker document with container command.
	 * 
	 * @param id
	 *            container ID.
	 * @param repo
	 *            image repository.
	 * @param tag
	 *            image tag.
	 * 
	 * @return Docker document with container command.
	 */
	public Docker createDockerModelWithContainerCommand(String id, String repo, String tag) {
		Docker model = dockerFactory.createDocker();
		List<DockerCommand> cmds = model.getCommands();
		cmds.add(createContainerCommand(id, repo, tag));
		return model;
	}

	/**
	 * Create Docker document with container command with undefined image info.
	 * 
	 * @param id
	 *            container ID.
	 * 
	 * @return Docker document with container command with undefined image info
	 */
	public Docker createDockerModelWithContainerCommandWithUndefinedImageInfo(String id) {
		Container command = dockerFactory.createContainer();
		command.setName(id);
		command.setImage(null);
		Docker model = dockerFactory.createDocker();
		List<DockerCommand> cmds = model.getCommands();
		cmds.add(command);
		return model;
	}

	/**
	 * Create tagged image command.
	 * 
	 * @param sourceRepo
	 *            image repository.
	 * @param sourceTag
	 *            image tag.
	 * @param targetRepo
	 *            image repository.
	 * @param targetTag
	 *            image tag.
	 * 
	 * @return image command.
	 */
	public TaggedImage createTaggedImageCommand(String sourceRepo, String sourceTag, String targetRepo,
			String targetTag) {
		TaggedImage command = dockerFactory.createTaggedImage();

		Image sourceImage = dockerFactory.createImage();
		sourceImage.setRepository(sourceRepo);
		sourceImage.setTag(sourceTag);
		command.setSourceImage(sourceImage);

		Image targetImage = dockerFactory.createImage();
		targetImage.setRepository(targetRepo);
		targetImage.setTag(targetTag);
		command.setTargetImage(targetImage);
		return command;
	}

	/**
	 * Create image command from Dockerfile.
	 * 
	 * @param targetRepo
	 *            image repository.
	 * @param targetTag
	 *            image tag.
	 * @param sourceDirectory
	 *            source directory.
	 * @param pullImage
	 *            pull image.
	 * 
	 * @return image from Dockerfile command.
	 */
	public ImageFromDockerfile createImageFromDockerfileCommand(String targetRepo, String targetTag,
			String sourceDirectory, boolean pullImage) {

		ImageFromDockerfile command = dockerFactory.createImageFromDockerfile();
		command.setSourceDirectory(sourceDirectory);
		command.setPullImage(pullImage);

		Image targetImage = dockerFactory.createImage();
		targetImage.setRepository(targetRepo);
		targetImage.setTag(targetTag);
		command.setTargetImage(targetImage);
		return command;
	}

	/**
	 * Create container command.
	 * 
	 * @param id
	 *            container ID.
	 * @param imageRepo
	 *            image repository.
	 * @param imageTag
	 *            image tag.
	 * 
	 * @return container command.
	 */
	public Container createContainerCommand(String id, String imageRepo, String imageTag) {
		Image image = dockerFactory.createImage();
		image.setRepository(imageRepo);
		image.setTag(imageTag);

		Container command = dockerFactory.createContainer();
		command.setName(id);
		command.setImage(image);
		return command;
	}

	/**
	 * Create container command with empty container configuration.
	 * 
	 * @param id
	 *            container ID.
	 * @param imageRepo
	 *            image repository.
	 * @param imageTag
	 *            image tag.
	 * 
	 * @return container command.
	 */
	public Container createContainerCommandWithContainerConfig(String id, String imageRepo, String imageTag) {
		Container command = createContainerCommand(id, imageRepo, imageTag);
		EmbeddedContainerConfiguration containerConfig = dockerFactory.createEmbeddedContainerConfiguration();
		containerConfig.setHostConfig(dockerFactory.createContainerConfigurationHostConfig());
		command.setConfiguration(containerConfig);
		return command;
	}

	/**
	 * Create referenced container configuration with default values. The container
	 * configuration is created with a host config defined.
	 * 
	 * @param id
	 *            ID of the created configuration.
	 * 
	 * @return referenced container configuration with default values.
	 */
	public ContainerConfiguration ReferencedContainerConfiguration(String id) {
		ContainerConfiguration configuration = dockerFactory.createContainerConfiguration();
		configuration.setHostConfig(dockerFactory.createContainerConfigurationHostConfig());
		configuration.setId(id);
		return configuration;
	}

	/**
	 * Create container command with referenced container configuration.
	 * 
	 * @param id
	 *            container ID.
	 * @param imageRepo
	 *            image repository.
	 * @param imageTag
	 *            image tag.
	 * @param configurationRef
	 *            Container configuration reference.
	 * 
	 * @return container command.
	 */
	public Container createContainerCommandWithReferencedContainerConfig(String id, String imageRepo, String imageTag,
			String configurationRef) {

		Container command = createContainerCommandWithContainerConfig(id, imageRepo, imageTag);
		EmbeddedContainerConfiguration configuration = command.getConfiguration();
		configuration.setRef(configurationRef);
		return command;
	}

	/**
	 * Add exposed port to container configuration.
	 * 
	 * If exposed ports container is undefined then it is added.
	 * 
	 * @param containerCmd
	 *            container command.
	 * @param random
	 *            random generator to generate port from.
	 */
	public void addExposedPort(Container containerCmd, Random random) {
		EmbeddedContainerConfiguration srcContainerConfig = containerCmd.getConfiguration();
		assertNotNull(srcContainerConfig);

		if (srcContainerConfig.getExposedPorts() == null) {
			ContainerConfigurationExposedPorts ports = dockerFactory.createContainerConfigurationExposedPorts();
			srcContainerConfig.setExposedPorts(ports);
		}

		ContainerConfigurationExposedPort port = dockerFactory.createContainerConfigurationExposedPort();
		port.setType(IpPort.TCP);
		port.setValue(NetworkUtils.getRandomPort(random));

		ContainerConfigurationExposedPorts ports = srcContainerConfig.getExposedPorts();
		List<ContainerConfigurationExposedPort> portList = ports.getPort();
		portList.add(port);
	}

	/**
	 * Add exposed port to container configuration.
	 * 
	 * If exposed ports container is undefined then it is added.
	 * 
	 * @param containerCmd
	 *            container command.
	 * @param portNumber
	 *            exposed port.
	 */
	public void addExposedPort(Container containerCmd, int portNumber) {
		EmbeddedContainerConfiguration srcContainerConfig = containerCmd.getConfiguration();
		assertNotNull(srcContainerConfig);
		NetworkUtils.validatePort(portNumber);

		if (srcContainerConfig.getExposedPorts() == null) {
			ContainerConfigurationExposedPorts ports = dockerFactory.createContainerConfigurationExposedPorts();
			srcContainerConfig.setExposedPorts(ports);
		}

		ContainerConfigurationExposedPort port = dockerFactory.createContainerConfigurationExposedPort();
		port.setType(IpPort.TCP);
		port.setValue(portNumber);

		ContainerConfigurationExposedPorts ports = srcContainerConfig.getExposedPorts();
		List<ContainerConfigurationExposedPort> portList = ports.getPort();
		portList.add(port);
	}

	/**
	 * Add volume to container configuration.
	 * 
	 * If volumes container is undefined then it is added.
	 * 
	 * @param containerCmd
	 *            container command.
	 * @param mountpoint
	 *            volume mount point.
	 */
	public void addVolume(Container containerCmd, String mountpoint) {
		EmbeddedContainerConfiguration srcContainerConfig = containerCmd.getConfiguration();
		assertNotNull(srcContainerConfig);

		if (srcContainerConfig.getVolumes() == null) {
			ContainerConfigurationVolumes volumes = dockerFactory.createContainerConfigurationVolumes();
			srcContainerConfig.setVolumes(volumes);
		}

		ContainerConfigurationVolume volume = dockerFactory.createContainerConfigurationVolume();
		volume.setMountpoint(mountpoint);

		ContainerConfigurationVolumes volumes = srcContainerConfig.getVolumes();
		List<ContainerConfigurationVolume> volumeList = volumes.getVolume();
		volumeList.add(volume);
	}

	/**
	 * Add environment variable to container configuration.
	 * 
	 * If envs container is undefined then it is added.
	 * 
	 * @param containerCmd
	 *            container command.
	 * @param name
	 *            variable name.
	 * @param name
	 *            variable value.
	 */
	public void addEnvironmentVariable(Container containerCmd, String name, String value) {
		EmbeddedContainerConfiguration srcContainerConfig = containerCmd.getConfiguration();
		assertNotNull(srcContainerConfig);

		if (srcContainerConfig.getEnv() == null) {
			ContainerConfigurationEnvironmentVariables envs = dockerFactory
					.createContainerConfigurationEnvironmentVariables();
			srcContainerConfig.setEnv(envs);
		}

		ContainerConfigurationEnvironmentVariable env = dockerFactory.createContainerConfigurationEnvironmentVariable();
		env.setName(name);
		env.setValue(value);

		ContainerConfigurationEnvironmentVariables envs = srcContainerConfig.getEnv();
		List<ContainerConfigurationEnvironmentVariable> envList = envs.getVariable();
		envList.add(env);
	}

	/**
	 * Add label to container configuration.
	 * 
	 * If labels container is undefined then it is added.
	 * 
	 * @param containerCmd
	 *            container command.
	 * @param name
	 *            label key.
	 * @param name
	 *            label value.
	 */
	public void addLabel(Container containerCmd, String key, String value) {
		EmbeddedContainerConfiguration srcContainerConfig = containerCmd.getConfiguration();
		assertNotNull(srcContainerConfig);

		if (srcContainerConfig.getLabels() == null) {
			ContainerConfigurationLabels labels = dockerFactory.createContainerConfigurationLabels();
			srcContainerConfig.setLabels(labels);
		}

		ContainerConfigurationLabel label = dockerFactory.createContainerConfigurationLabel();
		label.setKey(key);
		label.setValue(value);

		ContainerConfigurationLabels labels = srcContainerConfig.getLabels();
		List<ContainerConfigurationLabel> labelList = labels.getLabel();
		labelList.add(label);
	}

	/**
	 * Add TCP port binding to container configuration. Container and host are
	 * randomized.
	 * 
	 * If host config container is undefined then it is added. If port binding
	 * container is undefined then it is added.
	 * 
	 * @param containerCmd
	 *            container command.
	 * @param random
	 *            random generator to generate port from.
	 */
	public void addTcpPortBinding(Container containerCmd, Random random) {
		addTcpPortBinding(containerCmd, NetworkUtils.getRandomPort(random), NetworkUtils.getRandomPort(random));
	}

	/**
	 * Add TCP port binding to container configuration. Container and host are
	 * randomized.
	 * 
	 * If host config container is undefined then it is added. If port binding
	 * container is undefined then it is added.
	 * 
	 * @param containerCmd
	 *            container command.
	 * @param containerPort
	 *            bound container port.
	 * @param hostPort
	 *            bound host port.
	 */
	public void addTcpPortBinding(Container containerCmd, int containerPort, int hostPort) {
		EmbeddedContainerConfiguration srcContainerConfig = containerCmd.getConfiguration();
		assertNotNull(srcContainerConfig);

		if (srcContainerConfig.getHostConfig() == null) {
			ContainerConfigurationHostConfig hostConfig = dockerFactory.createContainerConfigurationHostConfig();
			srcContainerConfig.setHostConfig(hostConfig);
		}

		ContainerConfigurationHostConfig hostConfig = srcContainerConfig.getHostConfig();
		if (hostConfig.getPortBindings() == null) {
			ContainerConfigurationHostConfigPortBindings bindings = dockerFactory
					.createContainerConfigurationHostConfigPortBindings();
			hostConfig.setPortBindings(bindings);
		}

		ContainerConfigurationHostConfigPortBinding binding = dockerFactory
				.createContainerConfigurationHostConfigPortBinding();
		binding.setType(IpPort.TCP);
		binding.setContainerPort(containerPort);
		binding.setHostPort(hostPort);

		ContainerConfigurationHostConfigPortBindings bindings = hostConfig.getPortBindings();
		bindings.getBind().add(binding);
	}

}
