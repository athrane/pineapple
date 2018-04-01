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

package com.alpha.pineapple.substitution.variables;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.substitution.VariableSubstitutionException;

/**
 * Integration test of the class {@linkplain ResourceVariablesBuilderImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ResourceVariablesBuilderImplIntegrationTest {

	/**
	 * Empty string.
	 */
	static final String EMPTY_STRING = "";

	/**
	 * Null string.
	 */
	static final String NULL_STRING = null;

	/**
	 * Factory.
	 */
	@Resource
	ObjectFactory<ResourceVariablesBuilder> resourceVariablesBuilderFactory;

	/**
	 * Mock resource.
	 */
	com.alpha.pineapple.model.configuration.Resource sessionResource;

	/**
	 * Random variable name.
	 */
	String randomVarName;

	/**
	 * Random variable name.
	 */
	String randomVarName2;

	/**
	 * Random resource name.
	 */
	String randomResourceId;

	/**
	 * Random variable value.
	 */
	String randomVariableValue;

	/**
	 * Random variable value.
	 */
	String randomVariableValue2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomVarName = RandomStringUtils.randomAlphabetic(10) + "-var";
		randomVarName2 = RandomStringUtils.randomAlphabetic(10) + "-var";
		randomResourceId = RandomStringUtils.randomAlphabetic(10) + "-res";
		randomVariableValue = RandomStringUtils.randomAlphabetic(10);
		randomVariableValue2 = RandomStringUtils.randomAlphabetic(10);

		// create resource
		sessionResource = createMock(com.alpha.pineapple.model.configuration.Resource.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Create property list with one variable. The name of the variable is random
	 * variable name #1
	 * 
	 * @param propertyKey
	 *            property key.
	 * @param propertyValue
	 *            property value.
	 * 
	 * @return property list with one variable.
	 */
	List<Property> createPropertyListWithSingleProperty(String propertyKey, String propertyValue) {
		List<Property> propertyList = new ArrayList<Property>();
		Property property = new Property();
		property.setKey(propertyKey);
		property.setValue(propertyValue);
		propertyList.add(property);
		return propertyList;
	}

	/**
	 * Complete setup of resource mocks.
	 * 
	 * @param propertyList
	 *            property list which defined the properties on the session
	 *            resource.
	 */
	void completeMocksSetup(List<Property> propertyList) {
		expect(sessionResource.getId()).andReturn(randomResourceId).anyTimes();
		expect(sessionResource.getProperty()).andReturn(propertyList);
		replay(sessionResource);
	}

	/**
	 * Test that factory can be looked up from the context.
	 */
	@Test
	public void testCanGetFactoryFromContext() {
		assertNotNull(resourceVariablesBuilderFactory);
	}

	/**
	 * Test builder can be created from factory.
	 */
	@Test
	public void testCanCreateBuilder() {
		assertNotNull(resourceVariablesBuilderFactory.getObject());
	}

	/**
	 * Test that variables can be created from resource with no properties defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetVariablesFromResourceWithNoPropertiesDefined() throws Exception {
		List<Property> propertyList = new ArrayList<Property>();

		// complete mock setup
		expect(sessionResource.getProperty()).andReturn(propertyList);
		replay(sessionResource);

		// builder
		ResourceVariablesBuilder builder = resourceVariablesBuilderFactory.getObject();
		builder.setResource(sessionResource);
		Variables variables = builder.getVariables();

		// test
		assertNotNull(variables);
		Map<String, String> map = variables.getMap();
		assertNotNull(map);
		assertEquals(0, map.size());
		verify(sessionResource);
	}

	/**
	 * Test that variables can be created from resource with single properties
	 * defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetVariablesFromResourceWithSinglePropertyDefined() throws Exception {
		List<Property> propertyList = createPropertyListWithSingleProperty(randomVarName, randomVariableValue);

		completeMocksSetup(propertyList);

		// builder
		ResourceVariablesBuilder builder = resourceVariablesBuilderFactory.getObject();
		builder.setResource(sessionResource);
		Variables variables = builder.getVariables();

		// test
		assertNotNull(variables);
		Map<String, String> map = variables.getMap();
		assertNotNull(map);
		assertEquals(1, map.size());
		verify(sessionResource);
	}

	/**
	 * Test that variables can be created from resource with two properties defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetVariablesFromResourceWithTwoPropertiesDefined() throws Exception {
		List<Property> propertyList = createPropertyListWithSingleProperty(randomVarName, randomVariableValue);
		Property property = new Property();
		property.setKey(randomVarName2);
		property.setValue(randomVariableValue2);
		propertyList.add(property);

		completeMocksSetup(propertyList);

		// builder
		ResourceVariablesBuilder builder = resourceVariablesBuilderFactory.getObject();
		builder.setResource(sessionResource);
		Variables variables = builder.getVariables();

		// test
		assertNotNull(variables);
		Map<String, String> map = variables.getMap();
		assertNotNull(map);
		assertEquals(2, map.size());
		verify(sessionResource);
	}

	/**
	 * Test that variables construction fails if resource isn't configured.
	 * 
	 * @throws if
	 *             test fails.
	 */
	@Test(expected = VariableSubstitutionException.class)
	public void testVariablesConstructionFailsIfResourceIsntSet() throws Exception {
		ResourceVariablesBuilder builder = resourceVariablesBuilderFactory.getObject();
		builder.getVariables();
	}

	/**
	 * Test that resource property with null key isn't added variables.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetVariablesResourcePropertyWithNullKeyIsntAddedToVariables() throws Exception {
		List<Property> propertyList = createPropertyListWithSingleProperty(NULL_STRING, randomVariableValue);

		completeMocksSetup(propertyList);

		// builder
		ResourceVariablesBuilder builder = resourceVariablesBuilderFactory.getObject();
		builder.setResource(sessionResource);
		Variables variables = builder.getVariables();

		// test
		assertNotNull(variables);
		Map<String, String> map = variables.getMap();
		assertNotNull(map);
		assertEquals(0, map.size());
		verify(sessionResource);
	}

	/**
	 * Test that resource property with empty key isn't added variables.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetVariablesResourcePropertyWithEmptyKeyIsntAddedToVariables() throws Exception {
		List<Property> propertyList = createPropertyListWithSingleProperty(EMPTY_STRING, randomVariableValue);

		completeMocksSetup(propertyList);

		// builder
		ResourceVariablesBuilder builder = resourceVariablesBuilderFactory.getObject();
		builder.setResource(sessionResource);
		Variables variables = builder.getVariables();

		// test
		assertNotNull(variables);
		Map<String, String> map = variables.getMap();
		assertNotNull(map);
		assertEquals(0, map.size());
		verify(sessionResource);
	}

	/**
	 * Test that resource property with null value isn't added variables.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetVariablesResourcePropertyWithNullValueIsntAddedToVariables() throws Exception {
		List<Property> propertyList = createPropertyListWithSingleProperty(randomVarName, NULL_STRING);

		completeMocksSetup(propertyList);

		// builder
		ResourceVariablesBuilder builder = resourceVariablesBuilderFactory.getObject();
		builder.setResource(sessionResource);
		Variables variables = builder.getVariables();

		// test
		assertNotNull(variables);
		Map<String, String> map = variables.getMap();
		assertNotNull(map);
		assertEquals(0, map.size());
		verify(sessionResource);
	}

}
