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
package com.alpha.pineapple.docker.model;

/**
 * Interface for building info objects, mainly the configuration heavy
 * {@linkplain ContainerInfo}.
 */
public interface InfoBuilder {

	/**
	 * Create Docker container info.
	 * 
	 * @param id
	 *            container name.
	 * @param info
	 *            image info.
	 * 
	 * @return Docker container info.
	 * 
	 * @throws IllegalArgumentException
	 *             if info is null.
	 */
	public ContainerInfo buildContainerInfo(String name, ImageInfo info);

	/**
	 * Create Docker instance info.
	 * 
	 * @param id
	 *            container ID.
	 * @param info
	 *            container info.
	 * 
	 * @return Docker container instance info.
	 * 
	 * @throws IllegalArgumentException
	 *             if id is null.
	 * @throws IllegalArgumentException
	 *             if id is empty.
	 * @throws IllegalArgumentException
	 *             if info is null.
	 */
	public ContainerInstanceInfo buildInstanceInfo(String id, ContainerInfo info);

	/**
	 * Create Default Docker image info with repository and tag: "centos:latest".
	 * 
	 * @return Docker image info with with repository and tag: "centos:latest".
	 */
	public ImageInfo buildImageInfo();

	/**
	 * Create Docker image info.
	 * 
	 * @param repository
	 *            image repository.
	 * @param tag
	 *            image tag.
	 * 
	 * @return Docker image info.
	 * 
	 * @throws IllegalArgumentException
	 *             if repository is null.
	 * @throws IllegalArgumentException
	 *             if tag null.
	 */
	public ImageInfo buildImageInfo(String repository, String tag);

	/**
	 * Create Docker image info with "latest" tag.
	 * 
	 * @param repository
	 *            image repository.
	 * 
	 * @return Docker image info with "latest" tag.
	 * 
	 * @throws IllegalArgumentException
	 *             if repository is null.
	 */
	public ImageInfo buildImageInfo(String repository);

	/**
	 * Create Docker image info from fully qualified name, i.e. "repository:tag".
	 * 
	 * @param repository
	 *            image fully qualified name, i.e. "repository:tag".
	 * 
	 * @return Docker image info.
	 * 
	 * @throws IllegalArgumentException
	 *             if name is null.
	 */
	public ImageInfo buildImageInfoFromFQName(String image);

}
