/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen.
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

package com.alpha.testutils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * testing Resource from model classes.
 */
public class ObjectMotherResource {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Add properties to resource.
	 * 
	 * @param properties
	 *            Hash map containing properties which is added to resource .
	 * @param resource
	 *            Target resource.
	 */
	void addProperties(HashMap<String, String> properties, Resource resource) {
		// get properties
		List<Property> resourceProps = resource.getProperty();

		// map properties to resource properties
		Set<String> keys = properties.keySet();
		for (String key : keys) {
			String value = properties.get(key);

			// create new property
			Property resourceProp = new Property();
			resourceProp.setKey(key);
			resourceProp.setValue(value);

			// store property
			resourceProps.add(resourceProp);
		}
	}

	/**
	 * Create resource with no properties.
	 */
	public Resource createResourceWithNoProperties() {
		return createResourceWithNoProperties("some-id", "some-plugin-id");
	}

	/**
	 * Create resource with no properties.
	 * 
	 * @param id
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 */
	public Resource createResourceWithNoProperties(String id) {
		return createResourceWithNoProperties(id, "some-plugin-id");
	}

	/**
	 * Create resource with no properties.
	 * 
	 * @param id
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 */
	public Resource createResourceWithNoProperties(String id, String pluginId) {
		Resource resource = new Resource();
		resource.setId(id);
		resource.setCredentialIdRef("some-credential-id");
		resource.setPluginId(pluginId);
		return resource;
	}

	/**
	 * Create resource with no properties.
	 * 
	 * @param id
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 */
	public Resource createResourceWithNoPropertiesNoCredential(String id, String pluginId) {
		Resource resource = new Resource();
		resource.setId(id);
		resource.setPluginId(pluginId);
		return resource;
	}

	/**
	 * Create resource with no properties.
	 * 
	 * @param id
	 *            resource ID.
	 * @param pluginId
	 *            plugin ID.
	 * @param credentialIdRef
	 *            credential ID reference
	 */
	public Resource createResourceWithNoPropertiesNoCredential(String id, String pluginId, String credentialIdRef) {
		Resource resource = new Resource();
		resource.setId(id);
		resource.setPluginId(pluginId);
		resource.setCredentialIdRef(credentialIdRef);
		return resource;
	}

	/**
	 * Create test-resource with no properties.
	 */
	public Resource createTestResourceWithNoProperties() {
		Resource resource = new Resource();
		resource.setId(TestUtilsTestConstants.resourceIdTestResource);
		resource.setCredentialIdRef(TestUtilsTestConstants.credentialIdTestResource);
		resource.setPluginId(TestUtilsTestConstants.pluginIdTestResource);
		return resource;
	}

	/**
	 * Create resource with properties.
	 * 
	 * @param properties
	 *            Properties in a hash map.
	 */
	public Resource createResourceWithProperties(HashMap<String, String> properties) {
		Resource resource = createResourceWithNoProperties();
		addProperties(properties, resource);
		return resource;
	}

	/**
	 * Create test-resource with properties.
	 * 
	 * @param properties
	 *            Properties in a hash map.
	 */
	public Resource createTestResourceWithProperties(HashMap<String, String> properties) {
		Resource resource = createTestResourceWithNoProperties();
		addProperties(properties, resource);
		return resource;
	}

}
