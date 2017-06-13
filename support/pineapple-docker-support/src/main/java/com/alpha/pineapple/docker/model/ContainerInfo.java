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

import com.alpha.pineapple.docker.model.rest.ContainerConfiguration;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationHostConfig;
import com.alpha.pineapple.docker.model.rest.ContainerConfigurationVolumeNullValue;

/**
 * Interface for definition of a Docker container. The definition is used is
 * input for the the creation of a container.
 */
public interface ContainerInfo {

	/**
	 * Return image info.
	 * 
	 * @return image info.
	 */
	ImageInfo getImageInfo();

	/**
	 * Return container name.
	 * 
	 * @return container name.
	 */
	String getName();

	/**
	 * Get container configuration.
	 * 
	 * @return container configuration.
	 */
	ContainerConfiguration getContainerConfiguration();

	/**
	 * Add exposed TCP port to container configuration.
	 * 
	 * The port is added with the expected Docker format &lt;port&gt;/tcp and a
	 * null value object {@linkplain ContainerConfigurationExposedPortNullValue}
	 * .
	 * 
	 * @param port
	 *            number. Must be a value between 0 and 65535.
	 */
	void addExposedTcpPort(int portNumber);

	/**
	 * Add exposed UDP port to container configuration.
	 * 
	 * The port is added with the expected Docker format &lt;port&gt;/tcp and a
	 * null value object {@linkplain ContainerConfigurationExposedPortNullValue}
	 * .
	 * 
	 * @param port
	 *            number. Must be a value between 0 and 65535.
	 */
	void addExposedUdpPort(int portNumber);

	/**
	 * Create port string according to the Docker format:
	 * &lt;port&gt;/&lt;tcp|udp&gt;
	 * 
	 * @param portNumber
	 *            IP port number.
	 * @param portType
	 *            port type. Should either be "udp" or "tcp".
	 * 
	 * @return port string.
	 */
	String createPortString(int portNumber, String portType);

	/**
	 * Add volume to container configuration.
	 * 
	 * The volume is added with a null value object
	 * {@linkplain ContainerConfigurationVolumeNullValue}.
	 * 
	 * @param volume.
	 */
	void addVolume(String volume);

	/**
	 * Add environment variable.
	 * 
	 * @param name
	 *            variable name.
	 * @param value
	 *            variable value.
	 */
	void addEnvironmentVariable(String name, String value);

	/**
	 * Add label.
	 * 
	 * @param key
	 *            label key.
	 * @param value
	 *            label value.
	 */
	void addLabel(String key, String value);

	/**
	 * Add bound TCP port to host configuration. Container port is bound to host
	 * port.
	 * 
	 * @param containerPortNumber
	 *            Container host IP port number.
	 * @param hostPortNumber
	 *            host host IP port number.
	 */
	void addTcpPortBinding(int containerPortNumber, int hostPortNumber);

	/**
	 * Add bound UDP port to host configuration. Container port is bound to host
	 * port.
	 * 
	 * @param containerPortNumber
	 *            Container host IP port number.
	 * @param hostPortNumber
	 *            host host IP port number.
	 */
	void addUdpPortBinding(int containerPortNumber, int hostPortNumber);

	/**
	 * Create container host configuration object.
	 * 
	 * @return container host configuration object.
	 */
	public ContainerConfigurationHostConfig createHostConfiguration();

}
