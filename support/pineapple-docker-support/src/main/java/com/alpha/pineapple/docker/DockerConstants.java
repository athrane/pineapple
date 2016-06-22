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

package com.alpha.pineapple.docker;

/**
 * Pineapple Docker Constants.
 */
public interface DockerConstants {

    /**
     * Default Docker host.
     */
    //public static final String DEFAULT_HOST = "192.168.99.11";
    public static final String DEFAULT_HOST = "192.168.34.10";

    /**
     * Default port.
     */
    public static final String DEFAULT_PORT = "8082";

    /**
     * Default connect timeout milli seconds.
     */
    public static final String DEFAULT_CONNECT_TIMEOUT = "5000";

    /**
     * Default container stop timeout (in seconds).
     */
    public static final Integer CONTAINER_STOP_TIMEOUT = Integer.valueOf(5);

    /**
     * Default image repository for CentOS images.
     */
    public static final String DEFAULT_CENTOS_REPOSITORY = "centos";

    /**
     * Default image tag.
     */
    public static final String LATEST_IMAGE_TAG = "latest";

    /**
     * Dummy creation command. Docker can't currently handle an empty command
     * set.
     */
    public static final String DUMMY_COMMAND = "/bin/bash";

    /**
     * Get version service URI.
     */
    public static final String VERSION_URI = "/version";

    /**
     * Create container REST service URI.
     */
    public static final String CREATE_CONTAINER_URI = "/containers/create";

    /**
     * Build image from docker file REST service URI.
     */
    public static final String BUILD_IMAGE_URI = "/build?t={tag}&pull={pull}";

    /**
     * Rename container REST service URI.
     */
    public static final String RENAME_CONTAINER_URI = "/containers/{id}/rename?name={newname}";

    /**
     * Start container REST service URI.
     */
    public static final String START_CONTAINER_URI = "/containers/{id}/start";

    /**
     * Inspect container REST service URI.
     */
    public static final String INSPECT_CONTAINER_URI = "/containers/{id}/json";

    /**
     * Kill container REST service URI.
     */
    public static final String KILL_CONTAINER_URI = "/containers/{id}/kill";

    /**
     * Delete container REST service URI.
     */
    public static final String DELETE_CONTAINER_URI = "/containers/{id}";

    /**
     * Stop container REST service URI.
     */
    public static final String STOP_CONTAINER_URI = "/containers/{id}/stop?t={timeout}";

    /**
     * Pause container REST service URI.
     */
    public static final String PAUSE_CONTAINER_URI = "/containers/{id}/pause";

    /**
     * Unpause container REST service URI.
     */
    public static final String UNPAUSE_CONTAINER_URI = "/containers/{id}/unpause";

    /**
     * List containers REST service URI.
     */
    public static final String LIST_CONTAINERS_URI = "/containers/json?all={all}";

    /**
     * Create image REST service URI.
     */
    public static final String CREATE_IMAGE_URI = "/images/create?fromImage={image}";

    /**
     * Create tagged image REST service URI.
     */
    public static final String CREATE_TAGGED_IMAGE_URI = "/images/create?fromImage={image}&tag={tag}";

    /**
     * Delete image REST service URI.
     */
    public static final String DELETE_IMAGE_URI = "/images/{image}?force={force}";

    /**
     * Tag image REST service URI.
     */
    public static final String TAG_IMAGE_URI = "/images/{image}/tag?repo={repository}&tag={tag}";

    /**
     * List images REST service URI.
     */
    public static final String LIST_IMAGES_URI = "/images/json?all={all}";

    /**
     * Inspect image REST service URI.
     */
    public static final String INSPECT_IMAGE_URI = "/images/{id}/json";

    /**
     * Content type key.
     */
    public static final String CONTENT_TYPE_KEY = "Content-Type";

    /**
     * Content type TAR.
     */
    public static final String CONTENT_TYPE_TAR = "application/tar";

    /**
     * Extension for TAR archive.
     */
    static final String TAR_ARCHIVE_EXTENSION = ".tar";

    /**
     * File name for Docker file.
     */
    static final String DOCKERFILE_NAME = "Dockerfile";

    /**
     * undefined repo tag.
     */
    static final String UNDEFINED_REPO_TAG = "<none>:<none>";

    /**
     * Null host port in port binding.
     */
    public static final String PORTBINDING_NULL_HOST_IP = null;

    /**
     * Docker name prefix.
     */
    public static final String DOCKER_NAME_PREFIX = "/";

    /**
     * Package name for generated JAXB classes from Docker REST schema.
     */
    public static final String MODEL_PACKAGE = "com.alpha.pineapple.docker.model.rest";

    /**
     * Packages for custom JAXB maps.
     */
    public static final String CUSTOM_JAXB_MAPS = "com.alpha.pineapple.docker.model.jaxb";

}
