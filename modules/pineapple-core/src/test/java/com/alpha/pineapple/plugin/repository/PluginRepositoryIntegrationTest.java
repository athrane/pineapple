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

package com.alpha.pineapple.plugin.repository;

import static com.alpha.testutils.TestUtilsTestConstants.helloWorldOperation;
import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.testutils.TestUtilsTestConstants;
import com.alpha.testutils.testplugins.nooperations.NoOperationsPluginImpl;
import com.alpha.testutils.testplugins.wildcard.operation.WildcardOperationTestPluginImpl;

/**
 * Integration test of the class {@link PluginRepositoryImpl }.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class PluginRepositoryIntegrationTest {

	/**
	 * First result.
	 */
	static final int FIRST_RESULT = 0;

	/**
	 * Object under test.
	 */
	@Resource
	PluginRuntimeRepository pluginRepository;

	/**
	 * Execution result factory.
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	/**
	 * Random plugin id.
	 */
	String randomPluginId;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	@Before
	public void setUp() throws Exception {
		randomPluginId = RandomStringUtils.randomAlphabetic(10);

		result = executionResultFactory.startExecution("root");
	}

	@After
	public void tearDown() throws Exception {
		pluginRepository = null;
	}

	/**
	 * Validate plugin repository initialization is successful.
	 * 
	 * @return plugin repository result.
	 */
	ExecutionResult validateSuccessfulRepositoryResult() {
		assertTrue(result.isExecuting()); // test parent result is still
		// executing
		ExecutionResult[] childResults = result.getChildren();
		assertNotNull(childResults);
		assertEquals(1, result.getChildren().length);
		ExecutionResult repositoryResult = result.getChildren()[FIRST_RESULT];
		assertTrue(repositoryResult.isSuccess());
		return repositoryResult;
	}

	/**
	 * Validate one plugin was successfully initialized.
	 * 
	 * @param result
	 *            repository result.
	 * @param pluginId
	 *            expected plugin ID.
	 */
	void validateOnePluginWasSuccessfullyInitialized(ExecutionResult result, String pluginId) {
		ExecutionResult[] pluginResults = result.getChildren();
		assertNotNull(pluginResults);
		assertEquals(1, pluginResults.length); // test one plugin is initialized
		ExecutionResult pluginResult = pluginResults[FIRST_RESULT];
		assertTrue(pluginResult.getDescription().contains(pluginId)); // test
		// one
		// plugin
		// is
		// initialized
		assertTrue(pluginResult.isSuccess()); // test repository result is
		// successful
	}

	/**
	 * Test that repository can be lookup from context.
	 */
	@Test
	public void testCanCreateInstance() {
		// test
		assertNotNull(pluginRepository);
	}

	/**
	 * Test that repository can be initialized with an empty list of plugin id's.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializeWithEmptyPluginList() throws Exception {
		String[] pluginIds = {};
		pluginRepository.initialize(result, pluginIds);

		// test execution results
		ExecutionResult repositoryResult = validateSuccessfulRepositoryResult();
		ExecutionResult[] pluginResults = repositoryResult.getChildren();
		assertNotNull(pluginResults);
		assertEquals(0, pluginResults.length); // test no plugins are
		// initialized
	}

	/**
	 * Test that initialization rejects undefined execution results.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitializeRejectsUndefinedResult() throws Exception {
		String[] pluginIds = { pluginIdHelloWorld };
		pluginRepository.initialize(null, pluginIds);
	}

	/**
	 * Test that initialization rejects undefined list of plugin id's.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitializeRejectsUndefinedPluginList() throws Exception {
		pluginRepository.initialize(result, null);
	}

	/**
	 * Test that repository can be initialized with defined plugin id.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializeWithExistingPlugin() throws Exception {
		String[] pluginIds = { pluginIdHelloWorld };
		pluginRepository.initialize(result, pluginIds);

		// get plugin info
		PluginInfo info = pluginRepository.getPluginInfo(pluginIdHelloWorld);

		// test
		assertNotNull(info);
		assertEquals(pluginIdHelloWorld, info.getPluginId());

		// test execution results
		ExecutionResult repositoryResult = validateSuccessfulRepositoryResult();
		validateOnePluginWasSuccessfullyInitialized(repositoryResult, pluginIdHelloWorld);
	}

	/**
	 * Test that repository can be initialized with plugin id which doesn't exist on
	 * the class path.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializeWithNonExistingPlugin() throws Exception {
		String pluginId = "some.nonexisting.plugin";
		String[] pluginIds = { pluginId };
		pluginRepository.initialize(result, pluginIds);

		// test execution results
		assertTrue(result.isExecuting()); // test parent result is still
		// executing
		ExecutionResult[] childResults = result.getChildren();
		assertNotNull(childResults);
		assertEquals(1, result.getChildren().length);
		ExecutionResult repositoryResult = result.getChildren()[FIRST_RESULT];
		assertEquals(ExecutionResult.ExecutionState.FAILURE, repositoryResult.getState());

		// test initialization of plugin failed
		ExecutionResult[] pluginResults = repositoryResult.getChildren();
		assertNotNull(pluginResults);
		assertEquals(1, pluginResults.length);
		ExecutionResult pluginResult = pluginResults[FIRST_RESULT];
		assertTrue(pluginResult.getDescription().contains(pluginId));
		assertEquals(ExecutionResult.ExecutionState.FAILURE, pluginResult.getState());
	}

	/**
	 * Test that repository can be initialized with plugin id which doesn't exist on
	 * the class path.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializeWithPluginWithEmptyName() throws Exception {
		String pluginId = "";
		String[] pluginIds = { pluginId };
		pluginRepository.initialize(result, pluginIds);

		// test execution results
		assertTrue(result.isExecuting()); // test parent result is still
		// executing
		ExecutionResult[] childResults = result.getChildren();
		assertNotNull(childResults);
		assertEquals(1, result.getChildren().length);
		ExecutionResult repositoryResult = result.getChildren()[FIRST_RESULT];
		assertEquals(ExecutionResult.ExecutionState.ERROR, repositoryResult.getState());

		// test initialization of plugin failed
		ExecutionResult[] pluginResults = repositoryResult.getChildren();
		assertNotNull(pluginResults);
		assertEquals(0, pluginResults.length);
	}

	/**
	 * Test that repository can be initialized with plugin id which doesn't contain
	 * any operations.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializePluginWithNoOperations() throws Exception {
		String pluginId = NoOperationsPluginImpl.PLUGIN_ID;
		String[] pluginIds = { pluginId };
		pluginRepository.initialize(result, pluginIds);

		// test execution results
		assertTrue(result.isExecuting()); // test parent result is still
		// executing
		ExecutionResult[] childResults = result.getChildren();
		assertNotNull(childResults);
		assertEquals(1, result.getChildren().length);
		ExecutionResult repositoryResult = result.getChildren()[FIRST_RESULT];
		assertEquals(ExecutionResult.ExecutionState.SUCCESS, repositoryResult.getState());

		// test initialization of plugin failed
		ExecutionResult[] pluginResults = repositoryResult.getChildren();
		assertNotNull(pluginResults);
		assertEquals(1, pluginResults.length);
		ExecutionResult pluginResult = pluginResults[FIRST_RESULT];
		assertTrue(pluginResult.getDescription().contains(pluginId));
		assertEquals(ExecutionResult.ExecutionState.SUCCESS, pluginResult.getState());
	}

	/**
	 * Test that unmarshaller resolution fails for unknown plugin.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = PluginExecutionFailedException.class)
	public void testGetUnmarshallerFailsforUnknownPlugin() throws Exception {
		String[] pluginIds = { TestUtilsTestConstants.pluginIdHelloWorld };
		pluginRepository.initialize(result, pluginIds);

		// get plugin info
		Unmarshaller unmarshaller = pluginRepository.getPluginUnmarshaller(randomPluginId);

		// test
		assertNotNull(unmarshaller);
	}

	/**
	 * Test get plugin ID throws exception for undefined plugin ID.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = PluginNotFoundException.class)
	public void testGetPluginIdThrowsExceptionForUnknownPluginId() throws Exception {
		String randomPluginId = RandomStringUtils.randomAlphabetic(8);

		String[] pluginIds = { TestUtilsTestConstants.pluginIdHelloWorld };
		pluginRepository.initialize(result, pluginIds);

		// get plugin info to trigger exception
		pluginRepository.getPluginInfo(randomPluginId);
	}

	/**
	 * Test that get operation throws exception for undefined plugin ID.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = PluginNotFoundException.class)
	public void testGetOperationThrowsExceptionForUnknownPluginId() throws Exception {
		String randomPluginId = RandomStringUtils.randomAlphabetic(8);

		String[] pluginIds = { pluginIdHelloWorld };
		pluginRepository.initialize(result, pluginIds);

		// get plugin info to trigger exception
		pluginRepository.getOperation(randomPluginId, helloWorldOperation);
	}

	/**
	 * Test that get operation can get defined operation with wild card operation.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testGetOperationCanGetDefinedWildcardOperation() throws Exception {
		String[] pluginIds = { WildcardOperationTestPluginImpl.PLUGIN_ID };
		pluginRepository.initialize(result, pluginIds);

		// get operation
		Operation operation = pluginRepository.getOperation(WildcardOperationTestPluginImpl.PLUGIN_ID,
				helloWorldOperation);

		// test
		assertNotNull(operation);
		assertEquals("class com.alpha.testutils.testplugins.wildcard.operation.WildcardOperationTestPluginImpl",
				operation.getClass().toString());

		// test execution results
		validateSuccessfulRepositoryResult();
	}

	/**
	 * Test that get operation returns wild card operation for any random ID.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testGetOperationWithAnyIdIfItsDefinedAsWildcardOperation() throws Exception {
		String randomOperationId = RandomStringUtils.randomAlphabetic(10);

		String[] pluginIds = { WildcardOperationTestPluginImpl.PLUGIN_ID };
		pluginRepository.initialize(result, pluginIds);

		// get operation
		Operation operation = pluginRepository.getOperation(WildcardOperationTestPluginImpl.PLUGIN_ID,
				randomOperationId);

		// test
		assertNotNull(operation);
		assertEquals("class com.alpha.testutils.testplugins.wildcard.operation.WildcardOperationTestPluginImpl",
				operation.getClass().toString());
	}

	/**
	 * Test that get operation returns wild card operation for empty ID.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testGetOperationWithEmptyIdItsDefinedAsWildcardOperation() throws Exception {
		String[] pluginIds = { WildcardOperationTestPluginImpl.PLUGIN_ID };
		pluginRepository.initialize(result, pluginIds);

		// get operation
		Operation operation = pluginRepository.getOperation(WildcardOperationTestPluginImpl.PLUGIN_ID, "");

		// test
		assertNotNull(operation);
		assertEquals("class com.alpha.testutils.testplugins.wildcard.operation.WildcardOperationTestPluginImpl",
				operation.getClass().toString());
	}

	/**
	 * Test that get operation returns wild card operation for null ID.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testGetOperationWithNullIdItsDefinedAsWildcardOperation() throws Exception {
		String[] pluginIds = { WildcardOperationTestPluginImpl.PLUGIN_ID };
		pluginRepository.initialize(result, pluginIds);

		// get operation
		Operation operation = pluginRepository.getOperation(WildcardOperationTestPluginImpl.PLUGIN_ID, null);

		// test
		assertNotNull(operation);
		assertEquals("class com.alpha.testutils.testplugins.wildcard.operation.WildcardOperationTestPluginImpl",
				operation.getClass().toString());
	}

	/**
	 * Test that get operation can get defined operation.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testGetOperationCanGetDefinedOperation() throws Exception {
		String[] pluginIds = { pluginIdHelloWorld };
		pluginRepository.initialize(result, pluginIds);

		// get operation
		Operation operation = pluginRepository.getOperation(pluginIdHelloWorld, helloWorldOperation);

		// test
		assertNotNull(operation);
		assertEquals("class com.alpha.pineapple.plugin.helloworld.operation.HelloWorldOperationImpl",
				operation.getClass().toString());
	}

	/**
	 * Test that get operation throws exception for undefined operation.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = OperationNotSupportedException.class)
	public void testGetOperationThrowsExceptionForUnknownOperation() throws Exception {
		String randomOperation = RandomStringUtils.randomAlphabetic(8);

		String[] pluginIds = { pluginIdHelloWorld };
		pluginRepository.initialize(result, pluginIds);

		// get operation
		pluginRepository.getOperation(pluginIdHelloWorld, randomOperation);
	}

}
