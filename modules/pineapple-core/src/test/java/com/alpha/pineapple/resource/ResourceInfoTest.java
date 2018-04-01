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

package com.alpha.pineapple.resource;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.model.configuration.Resource;

/**
 * Unit test of the class {@link ResourceInfoImpl }.
 */
public class ResourceInfoTest {
	/**
	 * Object under test.
	 */
	ResourceInfo info;

	/**
	 * Random ID.
	 */
	String randomId;

	/**
	 * Random plugin ID.
	 */
	String randomPluginId;

	/**
	 * Random ID.
	 */
	String randomCredentialRefId;

	/**
	 * Mock resource configuration marshaller.
	 */
	ResourceConfigurationMarshaller marshaller;

	@Before
	public void setUp() throws Exception {
		randomId = RandomStringUtils.randomAlphabetic(10);
		randomPluginId = RandomStringUtils.randomAlphabetic(10);
		randomCredentialRefId = RandomStringUtils.randomAlphabetic(10);

		// create marshaller
		marshaller = createMock(ResourceConfigurationMarshaller.class);
	}

	@After
	public void tearDown() throws Exception {
		info = null;
	}

	/**
	 * Constructor test, i.e. that cache entry can be created.
	 */
	@Test
	public void testCanCreateInstance() {
		Map<String, ResourcePropertyInfo> properties = new HashMap<String, ResourcePropertyInfo>();
		info = new ResourceInfoImpl(randomId, randomPluginId, randomCredentialRefId, properties, marshaller);

		// test
		assertNotNull(info);
	}

	/**
	 * Constructor rejects undefined id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorRejectsUndefinedId() {
		Map<String, ResourcePropertyInfo> properties = new HashMap<String, ResourcePropertyInfo>();
		info = new ResourceInfoImpl(null, randomPluginId, randomCredentialRefId, properties, marshaller);
	}

	/**
	 * Constructor rejects undefined plugin id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorRejectsUndefinedPluginId() {
		Map<String, ResourcePropertyInfo> properties = new HashMap<String, ResourcePropertyInfo>();
		info = new ResourceInfoImpl(randomId, null, randomCredentialRefId, properties, marshaller);
	}

	/**
	 * Constructor accepts undefined credential reference id.
	 */
	@Test
	public void testConstructorAcceptsUndefinedCredentialReferenceId() {
		Map<String, ResourcePropertyInfo> properties = new HashMap<String, ResourcePropertyInfo>();
		info = new ResourceInfoImpl(randomId, randomPluginId, null, properties, marshaller);

		// test
		assertNotNull(info);
	}

	/**
	 * Constructor rejects undefined properties.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructorRejectsUndefinedProperties() {
		info = new ResourceInfoImpl(randomId, randomPluginId, randomCredentialRefId, null, marshaller);
	}

	/**
	 * Can get resource.
	 */
	@Test
	public void testCanGetResource() {

		Resource resource = createMock(Resource.class);

		Map<String, ResourcePropertyInfo> properties = new HashMap<String, ResourcePropertyInfo>();
		info = new ResourceInfoImpl(randomId, randomPluginId, randomCredentialRefId, properties, marshaller);

		// create marshaller and inject
		expect(marshaller.mapToResource(info)).andReturn(resource);
		ReflectionTestUtils.setField(info, "resourceConfigurationMarshaller", marshaller);
		replay(marshaller);

		// test
		assertEquals(resource, info.getResource());

		// verify mock
		verify(marshaller);
	}

}
