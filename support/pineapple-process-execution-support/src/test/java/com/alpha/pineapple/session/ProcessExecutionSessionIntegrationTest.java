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

package com.alpha.pineapple.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.testutils.ObjectMotherResource;
import com.alpha.testutils.ObjectMotherScript;

/**
 * Unit test of the {@link ProcessExecutionSessionImpl} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.process.execution-config.xml" })
public class ProcessExecutionSessionIntegrationTest {

	/**
	 * Expected number of results.
	 */
	static final int EXPECTED_RESULTS = 6;

	/**
	 * Object under test.
	 */
	@Resource
	ProcessExecutionSession processExecutionSession;

	/**
	 * Test script directory
	 */
	File testScriptsDir = new File(SystemUtils.getUserDir(), "/src/test/resources/");

	/**
	 * Script mother.
	 */
	ObjectMotherScript mother = new ObjectMotherScript();

	/**
	 * Object mother Resource.
	 */
	ObjectMotherResource resourceMother;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Credential.
	 */
	Credential credential;

	/**
	 * Random Name.
	 */
	String randomId;

	/**
	 * Random Name.
	 */
	String randomUser;

	/**
	 * Random Name.
	 */
	String randomPassword;

	@Before
	public void setUp() throws Exception {

		randomId = RandomStringUtils.randomAlphabetic(10);
		randomUser = RandomStringUtils.randomAlphabetic(10);
		randomPassword = RandomStringUtils.randomAlphabetic(10);

		// create object mother
		resourceMother = new ObjectMotherResource();

		// create execution result
		executionResult = new ExecutionResultImpl("Root result");
	}

	@After
	public void tearDown() throws Exception {

		executionResult = null;
	}

	/**
	 * Create credential.
	 */
	void createCredential() {
		credential = new Credential();
		credential.setId(randomId);
		credential.setUser(randomUser);
		credential.setPassword(randomPassword);
	}

	/**
	 * Test that session can be looked up from the context.
	 */
	@Test
	public void testCanGetSessionFromContext() {
		assertNotNull(processExecutionSession);
	}

	/**
	 * Test that session can be connected.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanConnectSession() throws Exception {
		// create resource
		com.alpha.pineapple.model.configuration.Resource resource;
		resource = resourceMother.createResourceWithNoProperties();

		// create credential
		createCredential();

		processExecutionSession.connect(resource, credential);
	}

	/**
	 * Test that resource is defined after connect.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testResourceIsDefinedInConnectedSession() throws Exception {
		// create resource
		com.alpha.pineapple.model.configuration.Resource resource;
		resource = resourceMother.createResourceWithNoProperties();

		// create credential
		createCredential();

		// connect
		processExecutionSession.connect(resource, credential);

		// test
		assertEquals(resource, processExecutionSession.getResource());

	}

	/**
	 * Test that credential is defined after connect.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCredentialIsDefinedInConnectedSession() throws Exception {
		// create resource
		com.alpha.pineapple.model.configuration.Resource resource;
		resource = resourceMother.createResourceWithNoProperties();

		// create credential
		createCredential();

		// connect
		processExecutionSession.connect(resource, credential);

		// test
		assertEquals(credential, processExecutionSession.getCredential());

	}

	/**
	 * Test that property is defined after connect.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testPropertyIsDefinedInConnectedSession() throws Exception {
		String randomKey = RandomStringUtils.randomAlphabetic(10);
		String randomValue = RandomStringUtils.randomAlphabetic(10);

		// create resource
		com.alpha.pineapple.model.configuration.Resource resource;
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);

		resource = resourceMother.createResourceWithProperties(properties);

		// create credential
		createCredential();

		// connect
		processExecutionSession.connect(resource, credential);

		// test
		assertTrue(processExecutionSession.isResourcePropertyDefined(randomKey));
	}

	/**
	 * Test that property value can be read in after connect.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanGetPropertyValueInConnectedSession() throws Exception {
		String randomKey = RandomStringUtils.randomAlphabetic(10);
		String randomValue = RandomStringUtils.randomAlphabetic(10);

		// create resource
		com.alpha.pineapple.model.configuration.Resource resource;
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);

		resource = resourceMother.createResourceWithProperties(properties);

		// create credential
		createCredential();

		// connect
		processExecutionSession.connect(resource, credential);

		// test
		assertEquals(randomValue, processExecutionSession.getResourceProperty(randomKey));
	}

	/**
	 * Test that reading undefined property value results in exception.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = ResourceException.class)
	public void testThrowsExceptionForUndefinedPropertyInConnectedSession() throws Exception {
		String randomKey = RandomStringUtils.randomAlphabetic(10);
		String randomKey2 = RandomStringUtils.randomAlphabetic(10);
		String randomValue = RandomStringUtils.randomAlphabetic(10);

		// create resource
		com.alpha.pineapple.model.configuration.Resource resource;
		HashMap<String, String> properties = new HashMap<String, String>();
		properties.put(randomKey, randomValue);

		resource = resourceMother.createResourceWithProperties(properties);

		// create credential
		createCredential();

		// connect
		processExecutionSession.connect(resource, credential);

		// test
		processExecutionSession.getResourceProperty(randomKey2);
	}

	/**
	 * Test that process can be executed.
	 * 
	 * @throws Exception
	 *             If test fails
	 */
	@Test
	public void testCanExecuteProcess() throws Exception {

		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/test");
		String[] arguments = {};
		String description = "..some description...";

		// invoke session
		processExecutionSession.execute(testscript.toString(), arguments, description, executionResult);

		// test
		assertEquals(ExecutionState.EXECUTING, executionResult.getState());
		assertEquals(1, executionResult.getChildren().length);

		ExecutionResult childResult = executionResult.getChildren()[0];
		assertEquals(ExecutionState.SUCCESS, childResult.getState());
		assertEquals(EXPECTED_RESULTS, childResult.getMessages().size());

		Map<String, String> messages = childResult.getMessages();
		assertEquals("TEST..", messages.get("Standard Out"));

	}

	/**
	 * Test that process can be execution fails if executable is unknown
	 * 
	 * @throws Exception
	 *             If test fails
	 */
	@Test
	public void testCanExecutionFailsIfExecutableIsUnknown() throws Exception {

		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/unknown-test");
		String[] arguments = {};
		String description = "..some description...";

		// invoke session
		processExecutionSession.execute(testscript.toString(), arguments, description, executionResult);

		// test
		assertEquals(ExecutionState.EXECUTING, executionResult.getState());
		assertEquals(1, executionResult.getChildren().length);

		ExecutionResult childResult = executionResult.getChildren()[0];
		assertEquals(ExecutionState.ERROR, childResult.getState());
		assertEquals(EXPECTED_RESULTS, childResult.getMessages().size());

		Map<String, String> messages = childResult.getMessages();
		assertEquals("", messages.get("Standard Out"));
	}

	/**
	 * Test that process can be executed with single argument
	 * 
	 * @throws Exception
	 *             If test fails
	 */
	@Test
	public void testCanExecuteProcessWithSingleArgument() throws Exception {

		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/test");
		String[] arguments = { "ARG1" };
		String description = "..some description...";

		// invoke session
		processExecutionSession.execute(testscript.toString(), arguments, description, executionResult);

		// test
		assertEquals(ExecutionState.EXECUTING, executionResult.getState());
		assertEquals(1, executionResult.getChildren().length);

		ExecutionResult childResult = executionResult.getChildren()[0];
		assertEquals(ExecutionState.SUCCESS, childResult.getState());
		assertEquals(EXPECTED_RESULTS, childResult.getMessages().size());

		Map<String, String> messages = childResult.getMessages();
		assertEquals("TEST..ARG1", messages.get("Standard Out"));
	}

	/**
	 * Test that process is killed after 5000ms if no time out value is defined.
	 * 
	 * @throws Exception
	 *             If test fails
	 */
	@Test
	public void testProcessIsKilledAfter5000msByDefault() throws Exception {

		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/forever");
		String[] arguments = {};
		String description = "..some description...";

		// invoke session
		processExecutionSession.execute(testscript.toString(), arguments, description, executionResult);

		// test
		assertEquals(ExecutionState.EXECUTING, executionResult.getState());
		assertEquals(1, executionResult.getChildren().length);

		ExecutionResult childResult = executionResult.getChildren()[0];
		assertEquals(ExecutionState.ERROR, childResult.getState());
		assertEquals(EXPECTED_RESULTS, childResult.getMessages().size());

		Map<String, String> messages = childResult.getMessages();
		assertEquals("x.\r\nx.\r\nx.\r\nx.\r\nx.", messages.get("Standard Out"));
	}

	/**
	 * Test that process is killed after 1000ms if time out value is defined.
	 * 
	 * @throws Exception
	 *             If test fails
	 */
	@Test
	public void testProcessIsKilledAfter1000ms() throws Exception {

		File testscript = mother.resolveOsSpecificScriptName(testScriptsDir + "/forever");
		String[] arguments = {};
		long timeout = 1000;
		String description = "..some description...";

		// invoke session
		processExecutionSession.execute(testscript.toString(), arguments, timeout, description, executionResult);

		// test
		assertEquals(ExecutionState.EXECUTING, executionResult.getState());
		assertEquals(1, executionResult.getChildren().length);

		ExecutionResult childResult = executionResult.getChildren()[0];
		assertEquals(ExecutionState.ERROR, childResult.getState());
		assertEquals(EXPECTED_RESULTS, childResult.getMessages().size());

		Map<String, String> messages = childResult.getMessages();
		assertEquals("x.", messages.get("Standard Out"));
	}

}
