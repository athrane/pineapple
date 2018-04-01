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

package com.alpha.pineapple.substitution;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.module.Module;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.model.test.ItemType;
import com.alpha.pineapple.model.test.ObjectFactory;
import com.alpha.pineapple.model.test.Root;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the class {@linkplain ModelVariableSubstitutorImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ModelVariableSubstitutorImplIntegrationTest {

	/**
	 * Sub under test.
	 */
	@Resource
	ModelVariableSubstitutor modelVariableSubstitutor;

	/**
	 * Object mother for module.
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Mock resource.
	 */
	com.alpha.pineapple.model.configuration.Resource sessionResource;

	/**
	 * Random variable name.
	 */
	String randomVarName;

	/**
	 * Random resource name.
	 */
	String randomResourceId;

	/**
	 * Random variable value.
	 */
	String randomVariableValue;

	/**
	 * Random name.
	 */
	String randomName;

	/**
	 * Target object factory.
	 */
	ObjectFactory targetObjecFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomVarName = RandomStringUtils.randomAlphabetic(10) + "-var";
		randomResourceId = RandomStringUtils.randomAlphabetic(10) + "-res";
		randomVariableValue = RandomStringUtils.randomAlphabetic(10);

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// create target object factory
		targetObjecFactory = new ObjectFactory();

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
	void completeResourceMockSetup(List<Property> propertyList) {
		expect(sessionResource.getId()).andReturn(randomResourceId).anyTimes();
		expect(sessionResource.getProperty()).andReturn(propertyList).anyTimes();
		replay(sessionResource);
	}

	/**
	 * Create target model.
	 * 
	 * @return model.
	 */
	Root createTargetModel(String value) {
		ItemType item = targetObjecFactory.createItemType();
		item.setName(value);
		Root root = targetObjecFactory.createRoot();
		root.setContainer(targetObjecFactory.createContainerType());
		root.getContainer().getItems().add(item);
		return root;
	}

	/**
	 * Test that instance can be looked up from the context.
	 */
	@Test
	public void testCanGetInstanceFromContext1() {
		assertNotNull(modelVariableSubstitutor);
	}

	/**
	 * Test that substituted object can be create.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanCreateSubstitutedObject() throws Exception {
		List<Property> propertyList = new ArrayList<Property>();
		completeResourceMockSetup(propertyList);

		// create models
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		Module module = moduleMother.createModuleObjectWithEmptyVariables();
		Root targetObject = createTargetModel("some value");

		// create object
		Object substitutedObject = modelVariableSubstitutor.createObjectWithSubstitution(module, model, sessionResource,
				targetObject);

		// test
		assertNotNull(substitutedObject);
		verify(sessionResource);
	}

	/**
	 * Test that variable can be substituted with variable defined in session
	 * resource.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanSubstituteValueDefinedInSessionResource() throws Exception {
		List<Property> propertyList = createPropertyListWithSingleProperty(randomVarName, randomVariableValue);
		completeResourceMockSetup(propertyList);

		// create string for substitution
		String subStr = new StringBuilder().append("${").append(randomVarName).append("}").toString();

		// create models
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		Module module = moduleMother.createModuleObjectWithEmptyVariables();
		Root targetObject = createTargetModel(subStr);

		// create object
		Root substitutedObject = modelVariableSubstitutor.createObjectWithSubstitution(module, model, sessionResource,
				targetObject);

		// test
		assertNotNull(substitutedObject);
		assertEquals(randomVariableValue, substitutedObject.getContainer().getItems().get(0).getName());
		verify(sessionResource);
	}

	/**
	 * Test that variable can be substituted with resource variable defined in
	 * session resource.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanSubstitutePrefixedValueDefinedInSessionResource() throws Exception {
		List<Property> propertyList = createPropertyListWithSingleProperty(randomVarName, randomVariableValue);
		completeResourceMockSetup(propertyList);

		// create string for substitution
		String subStr = new StringBuilder().append("${resource.").append(randomVarName).append("}").toString();

		// create models
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		Module module = moduleMother.createModuleObjectWithEmptyVariables();
		Root targetObject = createTargetModel(subStr);

		// create object
		Root substitutedObject = modelVariableSubstitutor.createObjectWithSubstitution(module, model, sessionResource,
				targetObject);

		// test
		assertNotNull(substitutedObject);
		assertEquals(randomVariableValue, substitutedObject.getContainer().getItems().get(0).getName());
		verify(sessionResource);
	}

	/**
	 * Test that variable can be substituted with variable defined in module
	 * descriptor.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanSubstituteValueDefinedInModuleDescriptor() throws Exception {
		List<Property> propertyList = new ArrayList<Property>();
		completeResourceMockSetup(propertyList);

		// create string for substitution
		String subStr = new StringBuilder().append("${").append(randomVarName).append("}").toString();

		// create models
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		Module module = moduleMother.createModuleObjectWithEmptyVariables();
		moduleMother.addVariable(module, randomVarName, randomVariableValue);
		Root targetObject = createTargetModel(subStr);

		// create object
		Root substitutedObject = modelVariableSubstitutor.createObjectWithSubstitution(module, model, sessionResource,
				targetObject);

		// test
		assertNotNull(substitutedObject);
		assertEquals(randomVariableValue, substitutedObject.getContainer().getItems().get(0).getName());
		verify(sessionResource);
	}

	/**
	 * Test that variable can be substituted with variable defined in module
	 * descriptor.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanSubstitutePrefixedValueDefinedInModuleDescriptor() throws Exception {
		List<Property> propertyList = new ArrayList<Property>();
		completeResourceMockSetup(propertyList);

		// create string for substitution
		String subStr = new StringBuilder().append("${module.").append(randomVarName).append("}").toString();

		// create models
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		Module module = moduleMother.createModuleObjectWithEmptyVariables();
		moduleMother.addVariable(module, randomVarName, randomVariableValue);
		Root targetObject = createTargetModel(subStr);

		// create object
		Root substitutedObject = modelVariableSubstitutor.createObjectWithSubstitution(module, model, sessionResource,
				targetObject);

		// test
		assertNotNull(substitutedObject);
		assertEquals(randomVariableValue, substitutedObject.getContainer().getItems().get(0).getName());
		verify(sessionResource);
	}

	/**
	 * Test that variable can be substituted with variable defined in model.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanSubstituteValueDefinedInModel() throws Exception {
		List<Property> propertyList = new ArrayList<Property>();
		completeResourceMockSetup(propertyList);

		// create string for substitution
		String subStr = new StringBuilder().append("${").append(randomVarName).append("}").toString();

		// create models
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		moduleMother.addVariable(model, randomVarName, randomVariableValue);
		Module module = moduleMother.createModuleObjectWithEmptyVariables();
		Root targetObject = createTargetModel(subStr);

		// create object
		Root substitutedObject = modelVariableSubstitutor.createObjectWithSubstitution(module, model, sessionResource,
				targetObject);

		// test
		assertNotNull(substitutedObject);
		assertEquals(randomVariableValue, substitutedObject.getContainer().getItems().get(0).getName());
		verify(sessionResource);
	}

	/**
	 * Test that variable can be substituted with variable defined in model.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testCanSubstitutePreficedValueDefinedInModel() throws Exception {
		List<Property> propertyList = new ArrayList<Property>();
		completeResourceMockSetup(propertyList);

		// create string for substitution
		String subStr = new StringBuilder().append("${model.").append(randomVarName).append("}").toString();

		// create models
		Models model = moduleMother.createModelObjectWithEmptyVariables();
		moduleMother.addVariable(model, randomVarName, randomVariableValue);
		Module module = moduleMother.createModuleObjectWithEmptyVariables();
		Root targetObject = createTargetModel(subStr);

		// create object
		Root substitutedObject = modelVariableSubstitutor.createObjectWithSubstitution(module, model, sessionResource,
				targetObject);

		// test
		assertNotNull(substitutedObject);
		assertEquals(randomVariableValue, substitutedObject.getContainer().getItems().get(0).getName());
		verify(sessionResource);
	}

}
