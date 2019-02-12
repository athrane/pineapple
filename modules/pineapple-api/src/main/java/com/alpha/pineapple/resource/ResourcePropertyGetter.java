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

import static com.alpha.javautils.ArgumentUtils.notNull;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;

/**
 * Helper class which is used to access properties from a {@link Resource}
 * object.
 */
public class ResourcePropertyGetter {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * The resource which is accessed.
	 */
	Resource resource;

	/**
	 * Cached resource properties.
	 */
	HashMap<String, String> cache;

	/**
	 * ResourcePropertyAccessor constructor.
	 */
	public ResourcePropertyGetter() {
	}

	/**
	 * ResourcePropertyAccessor constructor.
	 */
	public ResourcePropertyGetter(Resource resource) {
		setResource(resource);
	}

	/**
	 * Set resource which contains properties.
	 * 
	 * @param resource
	 *            resource which contains properties.
	 */
	public void setResource(Resource resource) {
		notNull(resource, "resource is undefined.");

		this.resource = resource;

		// cache properties in hash map
		cache = new HashMap<String, String>();

		// get properties
		List<Property> properties = resource.getProperty();

		// exit if properties is undefined
		if (properties != null) {
			processProperties(properties);
		}

	}

	/**
	 * Process properties.
	 * 
	 * @param properties
	 *            properties object.
	 */
	void processProperties(List<Property> properties) {
		// iterate over properties
		for (Property property : properties) {

			// get key
			String key = property.getKey();

			// if key is null the skip to next property
			if (key == null)
				continue;

			// get value
			String value = property.getValue();

			// if values is null the skip to next property
			if (value == null)
				continue;

			// store value in cache
			cache.put(key, value);
		}
	}

	/**
	 * Returns value of contained property. If the property has multiple values
	 * defined for a property, then the values are returned as a comma separated
	 * list.
	 * 
	 * @param key
	 *            Name of property.
	 * 
	 * @return value of contained property. If the property has multiple values
	 *         defined for a property, then the values are returned as a comma
	 *         separated list.
	 * 
	 * @throws ResourceException
	 *             If property isn't defined.
	 */
	public String getProperty(String key) throws ResourceException {
		// validate property exists
		if (!cache.containsKey(key)) {

			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Property lookup failed with key <");
			message.append(key);
			message.append("> in resource <");
			message.append(this);
			message.append(">. Property is not defined.");

			throw new ResourceException(message.toString());
		}

		// get value
		return cache.get(key);
	}

	/**
	 * Returns value of contained property. If the property has multiple values
	 * defined for a property, then the values are returned as a comma separated
	 * list.
	 * 
	 * @param key
	 *            Name of property.
	 * @param defaultValue
	 *            default value returned if property isn't defined.
	 * 
	 * @return value of contained property. If the property has multiple values
	 *         defined for a property, then the values are returned as a comma
	 *         separated list.
	 */
	public String getProperty(String key, String defaultValue) {
		if (!cache.containsKey(key))
			return defaultValue;
		return cache.get(key);
	}

	/**
	 * Returns true if property is defined by contained resource.
	 * 
	 * @param key
	 *            Name of property.
	 * 
	 * @return true if property is defined by contained resource.
	 */
	public boolean containsProperty(String key) {
		return cache.containsKey(key);
	}

}
