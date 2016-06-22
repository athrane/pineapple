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

/**
 * Interface for definition of a Docker image. The definition is used is input
 * for the the creation of an image.
 */
public interface ImageInfo {

    /**
     * Get repository where image is defined. Example of official repository
     * name is "ubuntu" which holds Ubuntu images. Example of user repository
     * (which has the name format user/repo) is thrane/pineapple
     * 
     * @return Docker repository where image is defined.
     */
    String getRepository();

    /**
     * Get image tag. Example of image tags are "latest" and "12.10".
     * 
     * @return image tag.
     */
    String getTag();

    /**
     * Get fully qualified image name. The name is the combination of the
     * repository and the image tag, i.e. REPO:TAG. Example of the fully
     * qualified name for an image defined in a official repository name is
     * "ubuntu:latest".
     * 
     * @return fully qualified image name.
     */
    String getFullyQualifiedName();

    /**
     * Return true if image tag is defined, i.e. not null or empty.
     * 
     * @return true if image tag is defined, i.e. not null or empty.
     */
    boolean isTagDefined();
}
