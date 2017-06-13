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

package com.alpha.pineapple.docker.model;

import static com.alpha.pineapple.docker.DockerConstants.PORTBINDING_NULL_HOST_IP;

import java.util.List;

import org.apache.commons.lang.Validate;

import com.alpha.javautils.NetworkUtils;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationLabelsMap;
import com.alpha.pineapple.docker.model.jaxb.ContainerConfigurationVolumesMap;
import com.alpha.pineapple.docker.model.jaxb.PortMapMap;
import com.alpha.pineapple.docker.model.jaxb.PortSetMap;
import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfig;
import com.alpha.pineapple.docker.model.rest.ObjectFactory;
import com.alpha.pineapple.docker.model.rest.PortBinding;

/**
 * Implementation of the {@linkplain ContainerInfo} interface.
 */
public class ContainerInfoImpl implements ContainerInfo {

	/**
	 * Error message for port number validation.
	 */
	static final String ILLEGAL_PORT_TEXT = "portNumber is illegal port number. Legal values are between 1 to 65535";

	/**
	 * Image info.
	 */
	ImageInfo info;

	/**
	 * Container name.
	 */
	String name;

	/**
	 * Configuration which is passed to Docker upon creation.
	 */
	ContainerConfiguration configuration;

	/**
	 * Docker object factory.
	 */
	static final ObjectFactory dockerModelObjectFactory = new ObjectFactory();

	/**
	 * ContainerInfoImpl constructor.
	 * 
	 * @param id
	 *            Container ID.
	 * @param info
	 *            image info.
	 */
	ContainerInfoImpl(String id, ImageInfo info) {
		this.name = id;
		this.info = info;
		configuration = dockerModelObjectFactory.createContainerConfiguration();
	}

	@Override
	public ImageInfo getImageInfo() {
		return info;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ContainerConfiguration getContainerConfiguration() {
		return configuration;
	}

	@Override
	public void addExposedTcpPort(int portNumber) {
		addPort(portNumber, "tcp");
	}

	@Override
	public void addExposedUdpPort(int portNumber) {
		addPort(portNumber, "udp");
	}

	@Override
	public String createPortString(int portNumber, String portType) {
		String portKey = new StringBuilder().append(portNumber).append("/").append(portType.toLowerCase()).toString();
		return portKey;
	}

	/**
	 * Add port.
	 * 
	 * If exposed ports map isn't defined in configuration then it is created.
	 * 
	 * @param portNumber
	 *            IP port number.
	 * @param portType
	 *            port type.
	 * 
	 * @throws IllegalArgumentException
	 *             if port number is invalid.
	 */
	void addPort(int portNumber, String portType) {
		NetworkUtils.validatePort(portNumber);
		Validate.notNull(portType, "portType is undefined");
		Validate.notEmpty(portType, "portType is empty");

		String portKey = createPortString(portNumber, portType);
		if (configuration.getExposedPorts() == null)
			configuration.setExposedPorts(new PortSetMap());
		PortSetMap exposedPorts = configuration.getExposedPorts();
		exposedPorts.put(portKey, dockerModelObjectFactory.createPortSetElementNullValue());
	}

	@Override
	public void addVolume(String volume) {
		Validate.notNull(volume, "volume is undefined.");
		Validate.notEmpty(volume, "volume is empty.");

		// get volumes map
		if (configuration.getVolumes() == null)
			configuration.setVolumes(new ContainerConfigurationVolumesMap());
		ContainerConfigurationVolumesMap volumes = configuration.getVolumes();

		// add volume
		volumes.put(volume, dockerModelObjectFactory.createContainerConfigurationVolumeNullValue());
	}

	@Override
	public void addEnvironmentVariable(String name, String value) {
		Validate.notNull(name, "name is undefined.");
		Validate.notEmpty(name, "name is empty.");
		Validate.notNull(value, "value is undefined.");
		Validate.notEmpty(value, "value is empty.");

		List<String> envs = configuration.getEnv();
		String env = new StringBuilder().append(name).append("=").append(value).toString();
		envs.add(env);
	}

	@Override
	public void addLabel(String key, String value) {
		Validate.notNull(key, "key is undefined.");
		Validate.notEmpty(key, "key is empty.");
		Validate.notNull(value, "value is undefined.");
		Validate.notEmpty(value, "value is empty.");

		// get labels map
		if (configuration.getLabels() == null)
			configuration.setLabels(new ContainerConfigurationLabelsMap());
		ContainerConfigurationLabelsMap labels = configuration.getLabels();

		// add label
		labels.put(key, value);
	}

	@Override
	public void addTcpPortBinding(int containerPortNumber, int hostPortNumber) {
		addPortBinding(containerPortNumber, "tcp", hostPortNumber);
	}

	@Override
	public void addUdpPortBinding(int containerPortNumber, int hostPortNumber) {
		addPortBinding(containerPortNumber, "udp", hostPortNumber);
	}

	/**
	 * Add port binding.
	 * 
	 * @param containerPortNumber
	 *            container port number.
	 * @param containerPortType
	 *            container port type.
	 * @param hostPortNumber
	 *            host port number.
	 * 
	 * @throws IllegalArgumentException
	 *             if port number is invalid.
	 */
	void addPortBinding(int containerPortNumber, String containerPortType, int hostPortNumber) {
		NetworkUtils.validatePort(containerPortNumber);
		Validate.notNull(containerPortType, "containerPortType is undefined.");
		Validate.notEmpty(containerPortType, "containerPortType is empty.");
		NetworkUtils.validatePort(hostPortNumber);

		// get host config
		if (configuration.getHostConfig() == null)
			configuration.setHostConfig(dockerModelObjectFactory.createContainerConfigurationHostConfig());
		ContainerConfigurationHostConfig hostConfig = configuration.getHostConfig();

		// get binding map
		if (hostConfig.getPortBindings() == null)
			hostConfig.setPortBindings(new PortMapMap());
		PortMapMap bindings = hostConfig.getPortBindings();

		// add binding
		String portKey = createPortString(containerPortNumber, containerPortType);
		PortBinding bindingValue = dockerModelObjectFactory.createPortBinding();
		PortBinding[] valueArray = new PortBinding[] { bindingValue };
		bindingValue.setHostPort(Integer.toString(hostPortNumber));
		bindingValue.setHostIp(PORTBINDING_NULL_HOST_IP);
		bindings.put(portKey, valueArray);
	}

	@Override
	public ContainerConfigurationHostConfig createHostConfiguration() {
		if (configuration.getHostConfig() == null)
			configuration.setHostConfig(dockerModelObjectFactory.createContainerConfigurationHostConfig());
		return configuration.getHostConfig();
	}

}
