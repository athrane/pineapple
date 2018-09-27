/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2016 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.command;

import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_OPERATION;
import static com.alpha.pineapple.CoreConstants.TRIGGER_WILDCARD_RESULT;
import static org.junit.Assert.*;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionInfoImpl;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.module.model.AggregatedModel;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.model.test.ItemType;
import com.alpha.pineapple.model.test.ObjectFactory;
import com.alpha.pineapple.model.test.Root;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleInfoImpl;
import com.alpha.pineapple.plugin.activation.PluginActivator;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test {@linkplain CommandFacadeImpl}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CommandFacadeIntegrationTest {

	/**
	 * No environments defined for module.
	 */
	static final String[] NO_ENVIRONENTS = {};

	/**
	 * Defines whether module descriptor is defined.
	 */
	static final boolean DESCRIPTOR_IS_DEFINED = true;

	/**
	 * Subject under test.
	 */
	@Resource
	CommandFacade commandFacade;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Configuration object mother
	 */
	ObjectMotherEnvironmentConfiguration configMother;

	/**
	 * Object mother for module.
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Object mother for credential provider
	 */
	@Resource
	ObjectMotherCredentialProvider providerMother;

	/**
	 * Random name.
	 */
	String randomName;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create execution result
		executionResult = new ExecutionResultImpl("Root result");

		// configuration object mother
		configMother = new ObjectMotherEnvironmentConfiguration();

		// create module object mother
		moduleMother = new ObjectMotherModule();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that file can be loaded/unmarshalled.
	 */
	@Test
	public void testUnmarshallJaxbObjects() {
		File file = new File(testDirectory, randomName + ".xml");

		// create document
		ObjectFactory factory = new ObjectFactory();
		Root root = factory.createRoot();
		root.setContainer(factory.createContainerType());
		ItemType item1 = factory.createItemType();
		item1.setName("item1");
		ItemType item2 = factory.createItemType();
		item2.setName("item2");
		root.getContainer().getItems().add(item1);
		root.getContainer().getItems().add(item2);

		// marshall objects
		configMother.jaxbMarshall(root, file);

		// load file
		Object untypedObject = commandFacade.unmarshallJaxbObjects(file, Root.class, executionResult);

		// test
		assertNotNull(untypedObject);
		assertTrue(untypedObject instanceof Root);
		assertTrue(executionResult.isExecuting());
		assertTrue(executionResult.getFirstChild().isSuccess());
	}

	/**
	 * Test that file can be loaded.
	 */
	@Test
	public void testLoadJaxbObjectsFromAbsolutePath() {
		File file = new File(testDirectory, randomName + ".xml");

		// create document
		ObjectFactory factory = new ObjectFactory();
		Root root = factory.createRoot();
		root.setContainer(factory.createContainerType());
		ItemType item1 = factory.createItemType();
		item1.setName("item1");
		ItemType item2 = factory.createItemType();
		item2.setName("item2");
		root.getContainer().getItems().add(item1);
		root.getContainer().getItems().add(item2);

		// marshall objects
		configMother.jaxbMarshall(root, file);

		// load file
		Object untypedObject = commandFacade.unmarshallJaxbObjects(file, Root.class, executionResult);

		// test
		assertNotNull(untypedObject);
		assertTrue(untypedObject instanceof Root);
		assertTrue(executionResult.isExecuting());
		assertTrue(executionResult.getFirstChild().isSuccess());
	}

	/**
	 * Test that file can be loaded from class path.
	 */
	@Test
	public void testLoadJaxbObjectsFromClassPath() {
		File file = new File("resources.xml");

		// load file
		Object untypedObject = commandFacade.unmarshallJaxbObjects(file, Configuration.class, executionResult);

		// test
		assertNotNull(untypedObject);
		assertTrue(untypedObject instanceof Configuration);
		assertTrue(executionResult.isExecuting());
		assertTrue(executionResult.getFirstChild().isSuccess());
	}

	/**
	 * Test that a {@linkplain CommandFacadeException} is thrown if load fails.
	 */
	@Test(expected = CommandFacadeException.class)
	public void testFailsToLoadUnknownFile() {
		File file = new File(testDirectory, "non-exsting-file.xml");
		commandFacade.unmarshallJaxbObjects(file, Root.class, executionResult);
	}

	/**
	 * Test that thrown {@linkplain CommandFacadeException} contains embedded
	 * {@linkplain ExecutionResult}.
	 */
	@Test()
	public void testFailureToLoadUnknownFileContainsEmbeddedException() {
		File file = new File(testDirectory, "non-exsting-file.xml");
		try {
			commandFacade.unmarshallJaxbObjects(file, Root.class, executionResult);
		} catch (CommandFacadeException e) {
			assertNotNull(e.getResult());
			assertTrue(executionResult.isExecuting());
			assertTrue(executionResult.getFirstChild().isError());
		}
	}

	/**
	 * Test that file can be saved/marshalled.
	 */
	@Test
	public void testMarshallJaxbObjects() {
		File file = new File(testDirectory, randomName + ".xml");

		// create document
		ObjectFactory factory = new ObjectFactory();
		Root root = factory.createRoot();
		root.setContainer(factory.createContainerType());
		ItemType item1 = factory.createItemType();
		item1.setName("item1");
		ItemType item2 = factory.createItemType();
		item2.setName("item2");
		root.getContainer().getItems().add(item1);
		root.getContainer().getItems().add(item2);

		commandFacade.marshallJaxbObjects(file, root, executionResult);

		// test
		assertTrue(executionResult.isExecuting());
		assertTrue(executionResult.getFirstChild().isSuccess());
		assertTrue(file.exists());
	}

	/**
	 * Test that file can be saved/marshalled.
	 */
	@Test
	public void testSaveJaxbObjects() {
		File file = new File(testDirectory, randomName + ".xml");

		// create document
		ObjectFactory factory = new ObjectFactory();
		Root root = factory.createRoot();
		root.setContainer(factory.createContainerType());
		ItemType item1 = factory.createItemType();
		item1.setName("item1");
		ItemType item2 = factory.createItemType();
		item2.setName("item2");
		root.getContainer().getItems().add(item1);
		root.getContainer().getItems().add(item2);

		commandFacade.marshallJaxbObjects(file, root, executionResult);

		// test
		assertTrue(executionResult.isExecuting());
		assertTrue(executionResult.getFirstChild().isSuccess());
		assertTrue(file.exists());
	}

	/**
	 * Test that a {@linkplain CommandFacadeException} is thrown if save fails.
	 */
	@Test(expected = CommandFacadeException.class)
	public void testFailsToSaveToUnknownDirectory() {
		File file = new File(randomName, randomName + ".xml");

		// create document
		ObjectFactory factory = new ObjectFactory();
		Root root = factory.createRoot();
		root.setContainer(factory.createContainerType());
		ItemType item1 = factory.createItemType();
		item1.setName("item1");
		ItemType item2 = factory.createItemType();
		item2.setName("item2");
		root.getContainer().getItems().add(item1);
		root.getContainer().getItems().add(item2);

		commandFacade.marshallJaxbObjects(file, root, executionResult);
	}

	/**
	 * Test that thrown {@linkplain CommandFacadeException} contains embedded
	 * {@linkplain ExecutionResult}.
	 */
	@Test
	public void testFailureToSaveContainsEmbeddedException() {
		File file = new File(randomName, randomName + ".xml");
		try {
			commandFacade.unmarshallJaxbObjects(file, Root.class, executionResult);
		} catch (CommandFacadeException e) {
			assertNotNull(e.getResult());
			assertTrue(executionResult.isExecuting());
			assertTrue(executionResult.getFirstChild().isError());
		}
	}

	/**
	 * Test that command can execute successfully.
	 */
	@Test
	public void testCanCopyExamples() throws Exception {
		File destinationDir = new File(testDirectory, randomName);

		// test
		assertFalse(destinationDir.exists());

		commandFacade.copyExampleModules(destinationDir, executionResult);

		// test
		assertTrue(executionResult.isExecuting());
		assertTrue(executionResult.getFirstChild().isSuccess());
	}

	/**
	 * Test that a {@linkplain CommandFacadeException} is thrown if file name is
	 * null.
	 */
	@Test(expected = CommandFacadeException.class)
	public void testFailsToCopyToUndefinedDirectory() {
		commandFacade.copyExampleModules(null, executionResult);
	}

	/**
	 * Test that a {@linkplain CommandFacadeException} is thrown if file name is
	 * empty.
	 */
	@Test(expected = CommandFacadeException.class)
	public void testFailsToCopyToEmptyDirectory() {
		commandFacade.copyExampleModules(new File(""), executionResult);
	}

	/**
	 * Test that a {@linkplain CommandFacadeException} is thrown if provider is
	 * null.
	 */
	@Test(expected = CommandFacadeException.class)
	public void testFailsToInitializPluginActivatorWithUndefinedProvider() {
		Configuration configuration = configMother.createEmptyEnvironmentConfiguration();
		commandFacade.initializePluginActivator(null, configuration, executionResult);
	}

	/**
	 * Test that plugin activator can be initialized with an empty configuration.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testCanInitializePluginProviderWithEmptyConfiguration() throws Exception {
		Configuration configuration = configMother.createEmptyEnvironmentConfiguration();
		CredentialProvider provider = providerMother.createEmptyCredentialProvider();
		PluginActivator activator = commandFacade.initializePluginActivator(provider, configuration, executionResult);

		// test
		assertNotNull(activator);
		assertTrue(executionResult.isExecuting());
		assertTrue(executionResult.getFirstChild().isSuccess());
	}

	/**
	 * Test that a model with no triggers can be executed successfully.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testExecuteModelWithNoTriggers() throws Exception {

		// create model
		String randomTargetResource = RandomStringUtils.randomAlphabetic(10);
		Models model = moduleMother.createModelObjectWithModelWithTargetResourceAttribute(randomTargetResource);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// create execution result's
		ExecutionResultImpl modelResult = new ExecutionResultImpl("model result");

		// create module info
		String randomId = RandomStringUtils.randomAlphabetic(10);
		File randomDirectory = new File(RandomStringUtils.randomAlphabetic(10));
		ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
				randomDirectory);

		// create execution info
		String randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		String randomOperation = RandomStringUtils.randomAlphabetic(10);
		ExecutionInfoImpl executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
				executionResult);

		commandFacade.executeTriggers(aggregatedModel, executionInfo, modelResult, executionResult);

		// test
		assertTrue(executionResult.getFirstChild().isSuccess());
	}

	/**
	 * Test that a {@linkplain CommandFacadeException} is thrown if trigger module
	 * is unknown.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test(expected = CommandFacadeException.class)
	public void testFailsIfTriggerModuleIsUnknown() throws Exception {

		// create model
		String randomTargetResource = RandomStringUtils.randomAlphabetic(10);
		String triggerOnResult = TRIGGER_WILDCARD_RESULT;
		String triggerOnOperation = TRIGGER_WILDCARD_OPERATION;
		Models model = moduleMother.createModelObjectWithModelWithTrigger(randomTargetResource, triggerOnResult,
				triggerOnOperation);

		// get aggregated model
		AggregatedModel aggregatedModel = model.getModel().iterator().next();

		// create execution result's
		ExecutionResultImpl modelResult = new ExecutionResultImpl("model result");

		// create module info
		String randomId = RandomStringUtils.randomAlphabetic(10);
		File randomDirectory = new File(RandomStringUtils.randomAlphabetic(10));
		ModuleInfo moduleInfo = ModuleInfoImpl.getInstance(randomId, NO_ENVIRONENTS, DESCRIPTOR_IS_DEFINED,
				randomDirectory);

		// create execution info
		String randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		String randomOperation = RandomStringUtils.randomAlphabetic(10);
		ExecutionInfoImpl executionInfo = new ExecutionInfoImpl(moduleInfo, randomEnvironment, randomOperation,
				executionResult);

		commandFacade.executeTriggers(aggregatedModel, executionInfo, modelResult, executionResult);
	}

}
