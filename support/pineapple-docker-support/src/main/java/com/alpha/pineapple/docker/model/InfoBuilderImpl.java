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

import static com.alpha.pineapple.docker.DockerConstants.DEFAULT_CENTOS_REPOSITORY;
import static com.alpha.pineapple.docker.DockerConstants.LATEST_IMAGE_TAG;
import static com.alpha.pineapple.docker.utils.ModelUtils.containsSeparator;

import org.apache.commons.lang.Validate;

public class InfoBuilderImpl implements InfoBuilder {

    @Override
    public ContainerInfo buildContainerInfo(String name, ImageInfo info) {
	Validate.notNull(info, "name is undefined");
	Validate.notEmpty(name, "name is empty");
	Validate.notNull(info, "info is undefined");
	return new ContainerInfoImpl(name, info);
    }

    @Override
    public ContainerInstanceInfo buildInstanceInfo(String id, ContainerInfo info) {
	Validate.notNull(id, "id is undefined");
	Validate.notEmpty(id, "id is empty.");
	Validate.notNull(info, "info is undefined");
	return new ContainerInstanceInfoImpl(id, info);
    }

    @Override
    public ImageInfo buildImageInfo() {
	return new ImageInfoImpl(DEFAULT_CENTOS_REPOSITORY, LATEST_IMAGE_TAG);
    }

    @Override
    public ImageInfo buildImageInfo(String repository, String tag) {
	Validate.notNull(repository, "repository is undefined.");
	Validate.notEmpty(repository, "repository is empty.");
	Validate.notNull(tag, "tag is undefined.");
	return new ImageInfoImpl(repository, tag);
    }

    @Override
    public ImageInfo buildImageInfo(String repository) {
	return buildImageInfo(repository, LATEST_IMAGE_TAG);
    }

    @Override
    public ImageInfo buildImageInfoFromFQName(String name) {
	Validate.notNull(name, "name is undefined.");
	if (containsSeparator(name)) {
	    int index = name.indexOf(":");
	    String repository = name.substring(0, index);
	    String tag = name.substring(index + 1);
	    return buildImageInfo(repository, tag);
	}

	return buildImageInfo(name, "");
    }

}
