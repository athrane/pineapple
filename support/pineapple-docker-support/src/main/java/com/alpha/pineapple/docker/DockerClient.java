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

import java.io.File;

import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ContainerInstanceInfo;
import com.alpha.pineapple.docker.model.ContainerState;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.model.rest.InspectedContainer;
import com.alpha.pineapple.docker.model.rest.InspectedImage;
import com.alpha.pineapple.docker.model.rest.ListedContainer;
import com.alpha.pineapple.docker.model.rest.ListedImage;
import com.alpha.pineapple.docker.session.DockerSession;
import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Interface for the Docker client.
 */
public interface DockerClient {

    /**
     * Return true if image exists in Docker repository.
     * 
     * @param session
     *            Docker session.
     * @param imageInfo
     *            Docker image info.
     * 
     * @return true if image exists in Docker repository.
     * 
     * @throws {@link
     *             DockerClientException} if query fails.
     */
    public boolean imageExists(DockerSession session, ImageInfo imageInfo);

    /**
     * Create Docker image.
     * 
     * If image exist in repository then the operation is aborted with a
     * successful result. If no image creation info's was received then
     * operation is aborted with a failed child result. and a
     * {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param imageInfo
     *            image info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of the
     *            image creation.
     */
    public void createImage(DockerSession session, ImageInfo imageInfo, ExecutionResult result);

    /**
     * Build Docker image from TAR archive.
     * 
     * If image exist in repository then the operation is aborted with a
     * successful result.
     * 
     * @param session
     *            Docker session.
     * @param imageInfo
     *            image info.
     * @param tarArchive
     *            TAR archive to build image from.
     * @param pullImageBehavior
     *            if true then then the image is be pulled from registry even if
     *            local (older) copy exist.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of the
     *            image creation.
     */
    public void buildImage(DockerSession session, ImageInfo imageInfo, File tarArchive, Boolean pullImageBehavior,
	    ExecutionResult result);

    /**
     * Delete Docker image in repository.
     * 
     * If image doesn't exist then the operation is aborted with a failed child
     * result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param imageInfo
     *            image info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of the
     *            image deletion.
     */
    public void deleteImage(DockerSession session, ImageInfo imageInfo, ExecutionResult result);

    /**
     * Tag Docker image into repository.
     * 
     * If source image doesn't exist then the operation is aborted with a failed
     * child result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of the
     *            image deletion.
     * 
     * @param session
     *            Docker session.
     * @param sourceImageInfo
     *            source image info.
     * @param targetImageInfo
     *            target image info.
     */
    public void tagImage(DockerSession session, ImageInfo sourceImageInfo, ImageInfo targetImageInfo,
	    ExecutionResult result);

    /**
     * List images.
     * 
     * @param session
     *            Docker session.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            listing the images.
     * 
     * @return list of images.
     */
    public ListedImage[] listImages(DockerSession session, ExecutionResult result);

    /**
     * Create report from images.
     * 
     * The report lists and inspects the images. The information is returned in
     * a tree of child execution result objects.
     * 
     * @param session
     *            Docker session.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            reporting on the images.
     */
    public void reportOnImages(DockerSession session, ExecutionResult result);

    /**
     * Inspect image.
     * 
     * If image doesn't exist then the operation is aborted with a failed child
     * result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker image info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            inspecting the container.
     * 
     * @return Inspected image info object.
     * 
     * @throws Exception
     *             if query fails.
     */
    public InspectedImage inspectImage(DockerSession session, ImageInfo info, ExecutionResult result);

    /**
     * Create Docker container.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            container info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of the
     *            container creation.
     * 
     * @return container instance info.
     * 
     * @throws {@link
     *             DockerClientException} if creation fails.
     */
    public ContainerInstanceInfo createContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * Delete Docker container.
     * 
     * If container doesn't exist then the operation is aborted with a failed
     * child result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            container info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of the
     *            container deletion.
     * 
     * @throws {@link
     *             DockerClientException} if deletion fails.
     */
    public void deleteContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * Return true if container exists in Docker. Identification is based on the
     * name.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker container info.
     * 
     * @return true if container exists in Docker.
     * 
     * @throws {@link
     *             DockerClientException} if query fails.
     */
    public boolean containerExists(DockerSession session, ContainerInfo info);

    /**
     * Return true if container exists in Docker. Identification is based on the
     * container ID.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker container instance info.
     * 
     * @return true if container exists in Docker.
     * 
     * @throws {@link
     *             DockerClientException} if query fails.
     */
    public boolean containerExistsQueryById(DockerSession session, ContainerInstanceInfo info);

    /**
     * Start Docker container.
     * 
     * If container doesn't exist then the operation is aborted with a failed
     * child result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker container info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            starting the container.
     * 
     * @throws {@link
     *             DockerClientException} if starting the container fails.
     */
    public void startContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * Stop Docker container.
     * 
     * If container doesn't exist then the command is aborted with a failed
     * execution result. If container is paused then the container is unpaused
     * and stopped.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker instance info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            stopping the container.
     */
    public void stopContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * Kill Docker container.
     * 
     * If container doesn't exist then the operation is aborted with a failed
     * child result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker container info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            killing the container.
     */
    public void killContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * Pause Docker container.
     * 
     * If container doesn't exist then the operation is aborted with a failed
     * child result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker instance info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            pausing the container.
     */
    public void pauseContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * Unpause Docker container.
     * 
     * If container doesn't exist then the operation is aborted with a failed
     * child result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker instance info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            unpausing container.
     */
    public void unpauseContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * List containers.
     * 
     * @param session
     *            Docker session.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            listing the containers.
     * 
     * @return list of containers.
     */
    public ListedContainer[] listContainers(DockerSession session, ExecutionResult result);

    /**
     * Inspect container.
     * 
     * If container doesn't exist then the operation is aborted with a failed
     * child result. and a {@linkplain DockerClientException} is thrown.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker container info.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            inspecting the container.
     * 
     * @return Inspected container info object.
     * 
     * @throws Exception
     *             if query fails.
     */
    public InspectedContainer inspectContainer(DockerSession session, ContainerInfo info, ExecutionResult result);

    /**
     * Create report from containers.
     * 
     * The report lists and inspects the containers. The information is returned
     * in a tree of child execution result objects.
     * 
     * @param session
     *            Docker session.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            reporting on the containers.
     */
    public void reportOnContainers(DockerSession session, ExecutionResult result);

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
    public boolean isContainerRunning(DockerSession session, ContainerInstanceInfo info);

    /**
     * Return true if container is running in Docker.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker container info.
     * 
     * @return true if container is running in Docker.
     * 
     * @throws Exception
     *             if query fails.
     */
    public boolean isContainerRunning(DockerSession session, ContainerInfo info);

    /**
     * Return true if container is paused in Docker.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Docker container info.
     * 
     * @return true if container is paused in Docker.
     * 
     * @throws Exception
     *             if query fails.
     */
    public boolean isContainerPaused(DockerSession session, ContainerInfo info);

    /**
     * Create TAR archive for building a Docker image.
     * 
     * @param sourceDirectory
     *            source directory which is compressed as a TAR archive.
     * @param tarArchive
     *            absolute path and name of the created TAR archive.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            creating the archive.
     */
    public void createTarArchive(File sourceDirectory, File tarArchive, ExecutionResult result);

    /**
     * Test container state against the container info and its embedded
     * configuration.
     * 
     * @param session
     *            Docker session.
     * @param info
     *            Expected Docker container info.
     * @param state
     *            Expected Docker container state.
     * @param result
     *            Root execution result. A child execution result is added to
     *            the root result. The child result describes the outcome of
     *            testing the container.
     */
    public void testContainer(DockerSession session, ContainerInfo info, ContainerState state, ExecutionResult result);

}
