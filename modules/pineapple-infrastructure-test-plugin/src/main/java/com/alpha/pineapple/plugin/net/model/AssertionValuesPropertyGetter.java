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

package com.alpha.pineapple.plugin.net.model;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.infrastructure.model.AssertionValues;
import com.alpha.pineapple.plugin.infrastructure.model.Properties;
import com.alpha.pineapple.plugin.infrastructure.model.Property;

/**
 * Helper class which is used to access properties from a
 * {@link AssertionValues} object.
 */
public class AssertionValuesPropertyGetter {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * The AssertionValues which is accessed.
	 */
	AssertionValues assertionValues;

	/**
	 * Cached AssertionValues properties.
	 */
	HashMap<String, String> cache;

	/**
	 * AssertionValuesPropertyGetter constructor.
	 */
	public AssertionValuesPropertyGetter(AssertionValues assertionValues) {
		// validate parameters
		Validate.notNull(assertionValues, "assertionValues  is undefined.");

		this.assertionValues = assertionValues;

		// cache properties in hash map
		cache = new HashMap<String, String>();

		// get properties
		Properties propertiesContainer = assertionValues.getProperties();

		// exit if properties is undefined
		if (propertiesContainer == null) {
			return;
		}

		List<Property> properties = propertiesContainer.getProperty();
		// exit if properties is undefined
		if (properties == null) {
			return;
		}

		// iterate over properties
		for (Property property : properties) {

			// get key
			String key = property.getName();

			// if key is null the skip to next property
			if (key == null)
				continue;

			// get value
			String value = property.getValue();

			// store value in cache
			cache.put(key, value);
		}

	}

	/**
	 * Returns value of contained property.
	 * 
	 * @param key
	 *            Name of property.
	 * 
	 * @return value of contained property. If the property is undefined then null
	 *         is returned.
	 * 
	 */
	public String getProperty(String key) {
		// validate property exists
		if (!cache.containsKey(key)) {

			if (logger.isDebugEnabled()) {
				// create error message
				StringBuilder message = new StringBuilder();
				message.append("Property lookup failed with key <");
				message.append(key);
				message.append("> in assertion values <");
				message.append(this);
				message.append(">. Property is not defined. Null string 'null' is returned.");
				logger.debug(message.toString());
			}

			return "null";
		}

		// get value
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
