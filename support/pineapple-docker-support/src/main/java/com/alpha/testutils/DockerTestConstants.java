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

/**
 * Docker constants for test cases.
 */
public interface DockerTestConstants {

	/**
	 * Default connect timeout (in ms).
	 */
	public static final int TEST_TIMEOUT = 10000;

	/**
	 * Default Docker image for testing image operations - not creating containers
	 * and the like.
	 */
	public static final String TEST_DOCKER_TINY_IMAGE = "busybox";

	/**
	 * Docker image for latest base Ubuntu image.
	 */
	public static final String TEST_DOCKER_BASE_UBUNTU_IMAGE = "ubuntu";

	/**
	 * Default Docker user repository.
	 */
	public static final String TEST_DOCKER_USER_REPOSITORY = "pineapple_ci";

	/**
	 * Root busybox Docker image.
	 */
	public static final String TEST_DOCKER_ROOT_BUSYBOX_IMAGE = "busybox";

	/**
	 * Default CentOS Docker image.
	 */
	public static final String TEST_DOCKER_DEFAULT_CENTOS_IMAGE = TEST_DOCKER_USER_REPOSITORY + "/centos";

	/**
	 * Default Docker registry image.
	 */
	public static final String TEST_DOCKER_DEFAULT_REGISTRY_IMAGE = "registry";

	/**
	 * Default Docker registry tag.
	 */
	public static final String TEST_DOCKER_DEFAULT_REGISTRY_TAG = "2";

	/**
	 * Spring configuration file for the docker support project.
	 */
	public static final String DOCKER_SPRING_CONFIG = "/com.alpha.pineapple.docker-config.xml";

	/**
	 * REST JSON content type.
	 */
	public static final String REST_JSON_CONTENTTYPE = "application/json;charset=UTF-8";
}
