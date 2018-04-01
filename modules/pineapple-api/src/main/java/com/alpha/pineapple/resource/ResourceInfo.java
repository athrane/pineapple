/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

import com.alpha.pineapple.model.configuration.Resource;

/**
 * Interface to represent information about a resource in the resource
 * repository.
 */
public interface ResourceInfo {

	/**
	 * Get resource.
	 * 
	 * @return resource.
	 */
	Resource getResource();

	/**
	 * Get plugin ID.
	 * 
	 * @return plugin ID.
	 */
	String getPluginId();

	/**
	 * Get resource ID.
	 * 
	 * @return resource ID.
	 */
	String getId();

	/**
	 * Get credential reference ID. If no credential reference is defined then null
	 * is returned.
	 * 
	 * @return credential reference ID.
	 */
	String getCredentialIdRef();

	/**
	 * Query whether resource contains property.
	 * 
	 * @param key
	 *            property key.
	 * 
	 * @return true if resource contains property.
	 */
	boolean containsProperty(String key);

	/**
	 * Get resource property.
	 * 
	 * @param key
	 *            property key.
	 * 
	 * @return property.
	 * 
	 * @throws ResourceNotFoundException
	 *             If resource doesn't contain property.
	 */
	ResourcePropertyInfo getProperty(String key) throws ResourceNotFoundException;

	/**
	 * Get resource property value
	 * 
	 * @param key
	 *            property key.
	 * @param defaultValue
	 *            default value which is returned if property isn't defined in
	 *            resource.
	 * 
	 * @return property value or default value if property isn't defined.
	 */
	String getPropertyValue(String key, String defaultValue);

	/**
	 * Get resource properties.
	 * 
	 * @return properties. If no properties are defined then an empty array is
	 *         returned.
	 */
	ResourcePropertyInfo[] getProperties();

}
