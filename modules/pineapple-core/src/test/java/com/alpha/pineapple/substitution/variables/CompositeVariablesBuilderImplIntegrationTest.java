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
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the class {@linkplain CompositeVariablesBuilderImpl}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CompositeVariablesBuilderImplIntegrationTest {

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
    ObjectFactory<CompositeVariablesBuilder> compositeVariablesBuilderFactory;

    /**
     * Factory.
     */
    @Resource
    ObjectFactory<ModelVariablesBuilder> modelVariablesBuilderFactory;

    /**
     * Object mother for module.
     */
    ObjectMotherModule moduleMother;

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
     * Random variable value.
     */
    String randomVariableValue3;

    /**
     * Random name.
     */
    String randomName;

    /**
     * Random name.
     */
    String randomName2;

    /**
     * Random name.
     */
    String randomName3;

    /**
     * Model object factory.
     */
    com.alpha.pineapple.model.module.model.ObjectFactory modelFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	randomName = RandomStringUtils.randomAlphabetic(10);
	randomName2 = RandomStringUtils.randomAlphabetic(10);
	randomName3 = RandomStringUtils.randomAlphabetic(10);
	randomVarName = RandomStringUtils.randomAlphabetic(10) + "-var";
	randomVarName2 = RandomStringUtils.randomAlphabetic(10) + "-var";
	randomResourceId = RandomStringUtils.randomAlphabetic(10) + "-res";
	randomVariableValue = RandomStringUtils.randomAlphabetic(10);
	randomVariableValue2 = RandomStringUtils.randomAlphabetic(10);
	randomVariableValue3 = RandomStringUtils.randomAlphabetic(10);

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
     * Add variable to model.
     * 
     * @param model
     *            model where variable is added.
     */
    void addVariable(Models model, String key, String value) {
	List<Variable> modelVariablesList = model.getVariables().getVariable();
	Variable modelVariable = modelFactory.createVariable();
	modelVariable.setKey(key);
	modelVariable.setValue(value);
	modelVariablesList.add(modelVariable);
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
     * Test that factory can be looked up from the context.
     */
    @Test
    public void testCanGetFactoryFromContext() {
	assertNotNull(compositeVariablesBuilderFactory);
    }

    /**
     * Test builder can be created from factory.
     */
    @Test
    public void testCanCreateBuilder() {
	assertNotNull(compositeVariablesBuilderFactory.getObject());
    }

    /**
     * Test that variables construction succeeds with no variables builder
     * registered.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithNoBuildersRegistered() throws Exception {
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	assertNotNull(variables.getMap());
	assertEquals(0, variables.getMap().size());
    }

    /**
     * Test that variables construction succeeds with one empty builder
     * registered.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithOneBuilderRegisteredWithZeroWars() throws Exception {
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	VariablesBuilder nullBuilder = new NullVariablesBuilderImpl();
	builder.addBuilder(randomName, nullBuilder);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	assertNotNull(variables.getMap());
	assertEquals(0, variables.getMap().size());
    }

    /**
     * Test that variables construction succeeds with two empty builders
     * registered.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithTwoEmptyBuildersRegistered() throws Exception {
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	VariablesBuilder nullBuilder = new NullVariablesBuilderImpl();
	VariablesBuilder nullBuilder2 = new NullVariablesBuilderImpl();
	builder.addBuilder(randomName, nullBuilder);
	builder.addBuilder(randomName2, nullBuilder2);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	assertNotNull(variables.getMap());
	assertEquals(0, variables.getMap().size());
    }

    /**
     * Test that variables construction succeeds with empty builders registered
     * twice.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithOneEmptyBuilderRegisteredTwice() throws Exception {
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	VariablesBuilder nullBuilder = new NullVariablesBuilderImpl();
	builder.addBuilder(randomName, nullBuilder);
	builder.addBuilder(randomName, nullBuilder);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	assertNotNull(variables.getMap());
	assertEquals(0, variables.getMap().size());
    }

    /**
     * Test that variables construction succeeds with one builder registered.
     * With one variable defined.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithOneBuilderRegisteredWithOneVar() throws Exception {
	Models model = createModel();
	addVariable(model, randomVarName, randomVariableValue);

	ModelVariablesBuilder moduleBuilder = modelVariablesBuilderFactory.getObject();
	moduleBuilder.setModel(model);
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	builder.addBuilder(randomName, moduleBuilder);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	Map<String, String> variablesMap = variables.getMap();
	assertNotNull(variablesMap);
	assertEquals(2, variablesMap.size());
	assertEquals(randomVariableValue, variablesMap.get(randomVarName));
	assertEquals(randomVariableValue, variablesMap.get(randomName + "." + randomVarName));
    }

    /**
     * Test that variables construction succeeds with one builder registered.
     * With two variables defined.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithOneBuilderRegisteredWithTwoVars() throws Exception {
	Models model = createModel();
	addVariable(model, randomVarName, randomVariableValue);
	addVariable(model, randomVarName2, randomVariableValue2);

	ModelVariablesBuilder moduleBuilder = modelVariablesBuilderFactory.getObject();
	moduleBuilder.setModel(model);
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	builder.addBuilder(randomName, moduleBuilder);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	Map<String, String> variablesMap = variables.getMap();
	assertNotNull(variablesMap);
	assertEquals(4, variablesMap.size());
	assertEquals(randomVariableValue, variablesMap.get(randomVarName));
	assertEquals(randomVariableValue, variablesMap.get(randomName + "." + randomVarName));
	assertEquals(randomVariableValue2, variablesMap.get(randomVarName2));
	assertEquals(randomVariableValue2, variablesMap.get(randomName + "." + randomVarName2));
    }

    /**
     * Test that variables construction succeeds with two builders registered.
     * With one (different) variable defined in each.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithTwoBuildersRegisteredWithOneDifferentVar() throws Exception {
	Models model = createModel();
	addVariable(model, randomVarName, randomVariableValue);
	Models model2 = createModel();
	addVariable(model2, randomVarName2, randomVariableValue2);

	ModelVariablesBuilder moduleBuilder = modelVariablesBuilderFactory.getObject();
	moduleBuilder.setModel(model);
	ModelVariablesBuilder moduleBuilder2 = modelVariablesBuilderFactory.getObject();
	moduleBuilder2.setModel(model2);
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	builder.addBuilder(randomName, moduleBuilder);
	builder.addBuilder(randomName2, moduleBuilder2);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	Map<String, String> variablesMap = variables.getMap();
	assertNotNull(variablesMap);
	assertEquals(4, variablesMap.size());
	assertEquals(randomVariableValue, variablesMap.get(randomVarName));
	assertEquals(randomVariableValue, variablesMap.get(randomName + "." + randomVarName));
	assertEquals(randomVariableValue2, variablesMap.get(randomVarName2));
	assertEquals(randomVariableValue2, variablesMap.get(randomName2 + "." + randomVarName2));
    }

    /**
     * Test that variables construction succeeds with two builders registered.
     * With one identical variable (identical key and value) defined in each.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithTwoBuildersRegisteredWithOneIdenticalVar() throws Exception {
	Models model = createModel();
	addVariable(model, randomVarName, randomVariableValue);
	Models model2 = createModel();
	addVariable(model2, randomVarName, randomVariableValue);

	ModelVariablesBuilder moduleBuilder = modelVariablesBuilderFactory.getObject();
	moduleBuilder.setModel(model);
	ModelVariablesBuilder moduleBuilder2 = modelVariablesBuilderFactory.getObject();
	moduleBuilder2.setModel(model2);
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	builder.addBuilder(randomName, moduleBuilder);
	builder.addBuilder(randomName2, moduleBuilder2);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	Map<String, String> variablesMap = variables.getMap();
	assertNotNull(variablesMap);
	assertEquals(3, variablesMap.size());
	assertEquals(randomVariableValue, variablesMap.get(randomVarName));
	assertEquals(randomVariableValue, variablesMap.get(randomName + "." + randomVarName));
	assertEquals(randomVariableValue, variablesMap.get(randomName2 + "." + randomVarName));
    }

    /**
     * Test that variables construction succeeds with two builders registered.
     * With one identical variable (identical key but different value) defined
     * in each.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithTwoBuildersRegisteredWithOneIdenticalVarDifferentValue() throws Exception {
	Models model = createModel();
	addVariable(model, randomVarName, randomVariableValue);
	Models model2 = createModel();
	addVariable(model2, randomVarName, randomVariableValue2);

	ModelVariablesBuilder moduleBuilder = modelVariablesBuilderFactory.getObject();
	moduleBuilder.setModel(model);
	ModelVariablesBuilder moduleBuilder2 = modelVariablesBuilderFactory.getObject();
	moduleBuilder2.setModel(model2);
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	builder.addBuilder(randomName, moduleBuilder);
	builder.addBuilder(randomName2, moduleBuilder2);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	Map<String, String> variablesMap = variables.getMap();
	assertNotNull(variablesMap);
	assertEquals(3, variablesMap.size());
	assertEquals(randomVariableValue, variablesMap.get(randomVarName));
	assertEquals(randomVariableValue, variablesMap.get(randomName + "." + randomVarName));
	assertEquals(randomVariableValue2, variablesMap.get(randomName2 + "." + randomVarName));
    }

    /**
     * Test that variables construction succeeds with three builders registered.
     * With one identical variable (identical key but different value) defined
     * in each.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetVariablesWithThreeBuildersRegisteredWithOneIdenticalVarDifferentValue() throws Exception {
	Models model = createModel();
	addVariable(model, randomVarName, randomVariableValue);
	Models model2 = createModel();
	addVariable(model2, randomVarName, randomVariableValue2);
	Models model3 = createModel();
	addVariable(model3, randomVarName, randomVariableValue3);

	ModelVariablesBuilder moduleBuilder = modelVariablesBuilderFactory.getObject();
	moduleBuilder.setModel(model);
	ModelVariablesBuilder moduleBuilder2 = modelVariablesBuilderFactory.getObject();
	moduleBuilder2.setModel(model2);
	ModelVariablesBuilder moduleBuilder3 = modelVariablesBuilderFactory.getObject();
	moduleBuilder3.setModel(model3);
	CompositeVariablesBuilder builder = compositeVariablesBuilderFactory.getObject();
	builder.addBuilder(randomName, moduleBuilder);
	builder.addBuilder(randomName2, moduleBuilder2);
	builder.addBuilder(randomName3, moduleBuilder3);
	Variables variables = builder.getVariables();

	// test
	assertNotNull(variables);
	Map<String, String> variablesMap = variables.getMap();
	assertNotNull(variablesMap);
	assertEquals(4, variablesMap.size());
	assertEquals(randomVariableValue, variablesMap.get(randomVarName));
	assertEquals(randomVariableValue, variablesMap.get(randomName + "." + randomVarName));
	assertEquals(randomVariableValue2, variablesMap.get(randomName2 + "." + randomVarName));
	assertEquals(randomVariableValue3, variablesMap.get(randomName3 + "." + randomVarName));
    }

}
