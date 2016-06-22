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

package com.alpha.pineapple.plugin.docker.model;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.alpha.pineapple.docker.model.ContainerInfo;
import com.alpha.pineapple.docker.model.ImageInfo;
import com.alpha.pineapple.docker.session.DockerSession;

/**
 * Maps values from the schema generated objects into the command context.
 */
public interface Mapper {

    /**
     * Map command to image info.
     * 
     * @param command
     *            model command.
     * 
     * @return image info.
     */
    ImageInfo mapImageForCreation(Image command);

    /**
     * Map command to command context.
     * 
     * @param command
     *            model command.
     * 
     * @return image info.
     */
    ImageInfo mapImageForDeletion(Image command);

    /**
     * Map command to tagged source image info.
     * 
     * @param command
     *            model command.
     * @param session
     *            operation session.
     * 
     * @return image info.
     */
    ImageInfo mapTaggedSourceImageForCreation(TaggedImage command);

    /**
     * Map command to tagged target image info.
     * 
     * @param command
     *            model command.
     * @param session
     *            operation session.
     * 
     * @return image info.
     */
    ImageInfo mapTaggedTargetImageImageForCreation(TaggedImage command);

    /**
     * Map command to command context.
     * 
     * @param command
     *            model command.
     * 
     * @return image info.
     */
    ImageInfo mapTaggedImageForDeletion(TaggedImage command);

    /**
     * Map command to image info.
     * 
     * @param command
     *            model command.
     * 
     * @return mapped image info.
     */
    ImageInfo mapImageFromDockerfileForCreation(ImageFromDockerfile command);

    /**
     * Resolve the pull image behavior from the model.
     * 
     * @param command
     *            model command.
     * 
     * @return pull image behavior resolved from the model.
     */
    Boolean getPullImageBehavior(ImageFromDockerfile command);

    /**
     * Map command to command context.
     * 
     * @param command
     *            model command.
     * 
     * @return mapped image info.
     */
    ImageInfo mapImageFromDockerfileForDeletion(ImageFromDockerfile command);

    /**
     * Map session to command context.
     * 
     * @param context
     *            command context.
     * @param session
     *            operation session.
     */
    void mapListImages(Context context, DockerSession session);

    /**
     * Map session to command context.
     * 
     * @param context
     *            command context.
     * @param session
     *            operation session.
     */
    void mapListContainers(Context context, DockerSession session);

    /**
     * Map command to container info.
     * 
     * @param command
     *            model command.
     * @param containerConfigs
     *            referenced container configurations.
     * 
     * @return container info.
     */
    ContainerInfo mapContainerForCreation(Container command, Map<String, ContainerConfiguration> containerConfigs);

    /**
     * Map command to command context.
     * 
     * @param command
     *            model command.
     * @param context
     *            command context.
     * @param session
     *            operation session.
     * 
     * @return container info.
     */
    ContainerInfo mapContainerForDeletion(Container command);

    /**
     * Map command to command context.
     * 
     * @param command
     *            model command.
     * @param context
     *            command context.
     * @param session
     *            operation session.
     * 
     * @return container info.
     */
    ContainerInfo mapContainerForControl(Container command);

    /**
     * Process docker model to extract container configurations into a map.
     * Configurations are stored by their ID. If the ID is null or empty then a
     * configuration isn't added to the map.
     * 
     * @param dockerModel
     *            plugin model.
     * @return map of container configurations registered by ID.
     */
    Map<String, ContainerConfiguration> extractContainerDefinitions(Docker dockerModel);

}
