/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.resource;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Implementation of the {@link EnvironmentInfo} interface.
 */
public class EnvironmentInfoImpl implements EnvironmentInfo {

	/**
	 * Environment ID.
	 */
	String id;

	/**
	 * Description.
	 */
	String description;

	/**
	 * Resource info's.
	 */
	TreeMap<String, ResourceInfo> resourceInfos;

	/**
	 * EnvironmentInfoImpl constructor.
	 * 
	 * @param description
	 *            environment description.
	 * @param resourceInfos
	 *            Resource info's.
	 */
	public EnvironmentInfoImpl(String id, String description, TreeMap<String, ResourceInfo> resourceInfos) {
		super();
		this.id = id;
		this.description = description;
		this.resourceInfos = resourceInfos;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean containsResource(String id) {
		return resourceInfos.containsKey(id);
	}

	@Override
	public ResourceInfo getResource(String id) {
		if (!containsResource(id))
			return null;
		return resourceInfos.get(id);
	}

	@Override
	public ResourceInfo[] getResources() {
		Collection<ResourceInfo> values = resourceInfos.values();
		return (ResourceInfo[]) values.toArray(new ResourceInfo[values.size()]);
	}

	/**
	 * Add resource
	 * 
	 * @param resourceInfo
	 *            resource to add.
	 */
	void addResource(ResourceInfo resourceInfo) {
		if (containsResource(resourceInfo.getId()))
			return;
		resourceInfos.put(resourceInfo.getId(), resourceInfo);
	}

	/**
	 * Delete resource.
	 * 
	 * @param resourceInfo
	 */
	public void deleteResource(ResourceInfo resourceInfo) {
		if (!containsResource(resourceInfo.getId()))
			return;
		resourceInfos.remove(resourceInfo.getId());
	}

}
