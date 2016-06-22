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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.model.module.model.Variable;
import com.alpha.pineapple.substitution.VariableSubstitutionException;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the class {@linkplain ModelVariablesBuilderImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ModelVariablesBuilderImplIntegrationTest {

    /**
     * Empty string.
     */
    static final String EMPTY_STRING = "";

    /**
     * Null string.
     */
    static final String NULL_STRING = null;

    /**
     * Object mother for module.
     */
    ObjectMotherModule moduleMother;

    /**
     * Factory.
     */
    @Resource
    ObjectFactory<ModelVariablesBuilder> modelVariablesBuilderFactory;

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
     * Model object factory.
     */
    com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;

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

	// create module object mother
	moduleMother = new ObjectMotherModule();

	// create model object factory
	modelFactory = new com.alpha.pineapple.model.module.model.ObjectFactory();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Create model
     * 
     * @return model.
     */
    Models createModel() {
	Models model = moduleMother.createModelObjectWithEmptyVariables();
	return model;
    }

    /**
     * Build variables.
     * 
     * @param model
     *            model to build variables from.
     * 
     * @return variables.
     * 
     * @throws VariableSubstitutionException
     *             if build fails.
     */
    Variables buildVariables(Models model) throws VariableSubstitutionException {
	ModelVariablesBuilder builder = modelVariablesBuilderFactory.getObject();
	builder.setModel(model);
	Variables variables = builder.getVariables();
	return variables;
    }

    /**
     * Test that factory can be looked up from the context.
     */
    @Test
    public void testCanGetFactoryFromContext() {
	assertNotNull(modelVariablesBuilderFactory);
    }

    /**
     * Test builder can be created from factory.
     */
    @Test
    public void testCanCreateBuilder() {
	assertNotNull(modelVariablesBuilderFactory.getObject());
    }

    /**
     * Test that variables construction fails if model isn't configured.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test(expected = VariableSubstitutionException.class)
    public void testVariablesConstructionFailsIfModelIsntSet() throws Exception {
	ModelVariablesBuilder builder = modelVariablesBuilderFactory.getObject();
	builder.setModel(null);
	builder.getVariables();
    }

    /**
     * Test that variables can be created from model with no variables section
     * defined.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesFromModelWithNoVariablesSectionDefined() throws Exception {

	Models model = moduleMother.createModelObject();
	model.setVariables(null);

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(0, map.size());
    }

    /**
     * Test that variables can be created from model with empty variables
     * section.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesFromModelWithNoVariablesDefined() throws Exception {

	// complete mock setup
	Models model = moduleMother.createModelObjectWithEmptyVariables();

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(0, map.size());
    }

    /**
     * Test that variables can be created from model with single variable
     * defined.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesFromModelWithSingleVariableDefined() throws Exception {

	Models model = createModel();
	moduleMother.addVariable(model, randomVarName, randomVariableValue);

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(1, map.size());
    }

    /**
     * Test that variables can be created from model with two variable defined.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesFromModelWithTwoVariablesDefined() throws Exception {

	Models model = createModel();
	moduleMother.addVariable(model, randomVarName, randomVariableValue);
	moduleMother.addVariable(model, randomVarName2, randomVariableValue2);

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(2, map.size());
    }

    /**
     * Test that variables with identical keys is only added once.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithIdentialVariablesIsOnlyAddedOnce() throws Exception {

	Models model = createModel();
	moduleMother.addVariable(model, randomVarName, randomVariableValue);
	moduleMother.addVariable(model, randomVarName, randomVariableValue2);

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(1, map.size());
    }

    /**
     * Test that variable with null key isn't added to variables.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testVariableWithNullKeyIsntAddedToVariables() throws Exception {

	Models model = createModel();
	moduleMother.addVariable(model, null, randomVariableValue);

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(0, map.size());
    }

    /**
     * Test that variable with empty key isn't added to variables.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testVariableWithEmptyKeyIsntAddedToVariables() throws Exception {

	Models model = createModel();
	moduleMother.addVariable(model, "", randomVariableValue);

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(0, map.size());
    }

    /**
     * Test that variable with null value isn't added to variables.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testVariableWithNullValueIsntAddedToVariables() throws Exception {

	Models model = createModel();
	moduleMother.addVariable(model, randomVarName, null);

	Variables variables = buildVariables(model);

	// test
	assertNotNull(variables);
	Map<String, String> map = variables.getMap();
	assertNotNull(map);
	assertEquals(0, map.size());
    }

}
