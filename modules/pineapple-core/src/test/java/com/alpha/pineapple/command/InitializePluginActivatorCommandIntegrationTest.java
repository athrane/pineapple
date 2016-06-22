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

package com.alpha.pineapple.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the class {@link InitializePluginActivatorCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class InitializePluginActivatorCommandIntegrationTest {

    /**
     * Object under test.
     */
    @Resource
    Command initializePluginActivatorCommand;

    /**
     * Object mother for credential provider
     */
    @Resource
    ObjectMotherCredentialProvider providerMother;

    /**
     * Execution result.
     */
    ExecutionResult executionResult;

    /**
     * Chain context.
     */
    Context context;

    /**
     * Object mother for environment configuration.
     */
    ObjectMotherEnvironmentConfiguration envConfigMother;

    @Before
    public void setUp() throws Exception {
	// create context
	context = new ContextBase();

	// create execution result
	executionResult = new ExecutionResultImpl("Root result");

	// create environment configuration object mother
	envConfigMother = new ObjectMotherEnvironmentConfiguration();
    }

    @After
    public void tearDown() throws Exception {
	executionResult = null;
	context = null;
    }

    /**
     * Test that command can be looked up from context.
     */
    @Test
    public void testCanCreateInstance() {
	// test
	assertNotNull(initializePluginActivatorCommand);
    }

    /**
     * Test that command can successfully execute with empty input.
     * 
     * @throws Exception
     *             If test fails.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCanSuccessfullyExecuteWithEmptyInput() throws Exception {
	// create execution result
	ExecutionResult childResult = executionResult.addChild("Test plugin activator initialization..");

	// create arguments
	Configuration envConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
	CredentialProvider provider = providerMother.createEmptyCredentialProvider();

	// set parameters
	context.put(InitializePluginActivatorCommand.CREDENTIAL_PROVIDER_KEY, provider);
	context.put(InitializePluginActivatorCommand.RESOURCES_KEY, envConfiguration);
	context.put(InitializePluginActivatorCommand.EXECUTIONRESULT_KEY, childResult);

	// execute HTTP test command
	initializePluginActivatorCommand.execute(context);

	// test
	assertEquals(ExecutionState.SUCCESS, childResult.getState());
    }

}
