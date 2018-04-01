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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;

/**
 * Unit test of the class {@link ResourcePropertyGetter }.
 */
public class ResourcePropertyGetterTest {

	/**
	 * Resource.
	 */
	Resource resource;

	/**
	 * Object under test.
	 */
	ResourcePropertyGetter getter;

	/**
	 * Random key.
	 */
	String randomKey;

	/**
	 * Random key.
	 */
	String randomKey2;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Random value.
	 */
	String randomValue2;

	/**
	 * Random default value.
	 */
	String randomDefaultValue;

	@Before
	public void setUp() throws Exception {
		randomKey = RandomStringUtils.randomAlphabetic(10);
		randomKey2 = RandomStringUtils.randomAlphabetic(10);
		randomValue = RandomStringUtils.randomAlphabetic(10);
		randomValue2 = RandomStringUtils.randomAlphabetic(10);
		randomDefaultValue = RandomStringUtils.randomAlphabetic(10);

	}

	@After
	public void tearDown() throws Exception {
		resource = null;
		getter = null;
	}

	/**
	 * Create resource with no properties.
	 */
	public Resource createResourceWithNoProperties() {
		// create resource
		Resource resource = new Resource();
		resource.setId("some-id");
		resource.setCredentialIdRef("some-credential-id");
		resource.setPluginId("some-package-id");
		return resource;
	}

	/**
	 * Create resource with properties.
	 * 
	 * @param properties
	 *            Properties in a hash map.
	 */
	public Resource createResourceWithProperties(HashMap<String, String> properties) {
		// create resource
		Resource resource = createResourceWithNoProperties();

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

		return resource;
	}

	/**
	 * Test that instance can be created.
	 */
	@Test
	public void testCanCreateInstance() {
		// create resource
		resource = createResourceWithNoProperties();

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// test
		assertNotNull(getter);
	}

	/**
	 * Test that instance can be created with no-arg constructor.
	 */
	@Test
	public void testCanCreateInstanceWithNoArgConstructor() {
		// create getter
		getter = new ResourcePropertyGetter();

		// test
		assertNotNull(getter);
	}

	/**
	 * Test that undefined resource is rejected.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedResource() {
		// create getter to force exception
		getter = new ResourcePropertyGetter(null);

		fail("Test should never reach here.");
	}

	/**
	 * Test that undefined property lookup results in exception
	 */
	@Test(expected = ResourceException.class)
	public void testThrowsExceptionIfPropertyIsUnknown() throws Exception {
		// create object
		resource = createResourceWithNoProperties();

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// lookup a non-existing property to force exception
		getter.getProperty(randomKey);

		fail("Test should never reach here.");
	}

	/**
	 * Test that property lookup returns correct value.
	 */
	@Test
	public void testCanGetProperty() throws Exception {
		// setup parameters
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);

		// create object
		resource = createResourceWithProperties(properties);

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// lookup property
		String actual = getter.getProperty(randomKey);

		assertEquals(randomValue, actual);
	}

	/**
	 * Test that property lookup returns correct value also with default value is
	 * defined.
	 */
	@Test
	public void testCanGetProperty2() throws Exception {
		// setup parameters
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);

		// create object
		resource = createResourceWithProperties(properties);

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// lookup property
		String actual = getter.getProperty(randomKey, randomDefaultValue);

		assertEquals(randomValue, actual);
	}

	/**
	 * Test that property lookup returns default value if property isn't defined.
	 */
	@Test
	public void testReturnsDefaultValueIfPropertyIsntDefined() throws Exception {
		// setup parameters
		HashMap<String, String> properties = new HashMap<String, String>();

		// create object
		resource = createResourceWithProperties(properties);

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// lookup property
		String actual = getter.getProperty(randomKey, randomDefaultValue);

		assertEquals(randomDefaultValue, actual);
	}

	/**
	 * Test that returned default value can be null.
	 */
	@Test
	public void testReturnsNullDefaultValueIfPropertyIsntDefined() throws Exception {
		// setup parameters
		HashMap<String, String> properties = new HashMap<String, String>();

		// create object
		resource = createResourceWithProperties(properties);

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// lookup property
		String actual = getter.getProperty(randomKey, null);

		assertEquals(null, actual);
	}

	/**
	 * Test that property lookup returns correct value for multiple properties.
	 */
	@Test
	public void testCanGetMultipleProperties() throws Exception {
		// setup parameters
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);
		properties.put(randomKey2, randomValue2);

		// create object
		resource = createResourceWithProperties(properties);

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// lookup property
		String actual = getter.getProperty(randomKey);
		String actual2 = getter.getProperty(randomKey2);

		// test
		assertEquals(randomValue, actual);
		assertEquals(randomValue2, actual2);
	}

	/**
	 * Test that contains query returns correct value.
	 */
	@Test
	public void testContainsPropertyReturnTrue() throws Exception {
		// setup parameters
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);

		// create object
		resource = createResourceWithProperties(properties);

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// query about property
		boolean actual = getter.containsProperty(randomKey);

		assertTrue(actual);
	}

	/**
	 * Test that contains query returns correct value.
	 */
	@Test
	public void testContainsPropertyReturnFalse() throws Exception {
		// setup parameters
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);

		// create object
		resource = createResourceWithProperties(properties);

		// create getter
		getter = new ResourcePropertyGetter(resource);

		// query about property
		boolean actual = getter.containsProperty("unknown-property");

		assertFalse(actual);
	}

}
