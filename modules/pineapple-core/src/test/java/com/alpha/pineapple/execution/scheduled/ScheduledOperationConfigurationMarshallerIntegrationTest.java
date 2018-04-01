/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
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
package com.alpha.pineapple.execution.scheduled;

import static com.alpha.pineapple.CoreConstants.OPERATIONS_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperations;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the {@linkplain ScheduledOperationRepositoryImpl} class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class ScheduledOperationConfigurationMarshallerIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Subject under test.
	 */
	@Resource(name = "scheduledOperationConfigurationMarshaller")
	ScheduledOperationConfigurationMarshaller marshaller;

	/**
	 * Runtime directory resolver.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Random value.
	 */
	String randomName;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		deleteOperationsConfiguration();
	}

	@After
	public void tearDown() throws Exception {
		deleteOperationsConfiguration();

		// clear the pineapple.home.dir system property
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

		// fail if the the pineapple.home.dir system property is set
		assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
	}

	/**
	 * Delete operations configuration file.
	 */
	void deleteOperationsConfiguration() {
		File operationsFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), OPERATIONS_FILE);
		if (operationsFile.exists())
			FileUtils.deleteQuietly(operationsFile);
	}

	/**
	 * Test that instance can be retrieved from application context.
	 */
	@Test
	public void testGetInstanceFromContext() {
		assertNotNull(marshaller);
	}

	/**
	 * Test that empty configuration can be saved.
	 */
	@Test
	public void testSaveEmptyConfiguration() {
		ScheduledOperations operations = envConfigMother.createEmptyScheduledOperationConfiguration();
		ExecutionResult result = new ExecutionResultImpl("Save configuration");
		marshaller.save(result, operations);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		ExecutionResult saveResult = result.getFirstChild();

		// test
		assertTrue(saveResult.isSuccess());
		File configFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), OPERATIONS_FILE);
		assertTrue(configFile.exists());
		assertTrue(configFile.isFile());
	}

	/**
	 * Test that configuration can be saved.
	 */
	@Test
	public void testSaveConfiguration() {
		ScheduledOperations operations = envConfigMother
				.createScheduledOperationConfigurationWithSingleOperation(randomName);
		ExecutionResult result = new ExecutionResultImpl("Save configuration");
		marshaller.save(result, operations);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		ExecutionResult saveResult = result.getFirstChild();

		// test
		assertTrue(saveResult.isSuccess());
		File resourcesFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), OPERATIONS_FILE);
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
	}

	/**
	 * Test that empty configuration can be loaded.
	 */
	@Test
	public void testLoadEmptyConfiguration() {
		ScheduledOperations operations = envConfigMother.createEmptyScheduledOperationConfiguration();
		ExecutionResult result = new ExecutionResultImpl("Save configuration");
		marshaller.save(result, operations);

		result = new ExecutionResultImpl("Load configuration");
		ScheduledOperations loadedOperations = marshaller.load(result);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		ExecutionResult loadResult = result.getFirstChild();

		// test
		assertTrue(loadResult.isSuccess());
		assertNotNull(loadedOperations);
		assertTrue(loadedOperations.getScheduledOperation().isEmpty());
	}

	/**
	 * Test that configuration can be loaded with one scheduled operation.
	 */
	@Test
	public void testLoadConfiguration() {
		ScheduledOperations operations = envConfigMother
				.createScheduledOperationConfigurationWithSingleOperation(randomName);
		ExecutionResult result = new ExecutionResultImpl("Save configuration");
		marshaller.save(result, operations);

		result = new ExecutionResultImpl("Load configuration");
		ScheduledOperations loadedOperations = marshaller.load(result);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		ExecutionResult loadResult = result.getFirstChild();

		// test
		assertTrue(loadResult.isSuccess());
		assertNotNull(loadedOperations);
		assertEquals(1, loadedOperations.getScheduledOperation().size());
	}

	/**
	 * Test that non-existing configuration can be loaded and then an empty
	 * configuration is returned.
	 */
	@Test
	public void testLoadNonExistingConfiguration() {
		File resourcesFile = new File(runtimeDirectoryProvider.getConfigurationDirectory(), OPERATIONS_FILE);
		assertFalse(resourcesFile.exists());

		ExecutionResultImpl result = new ExecutionResultImpl("Load configuration");
		ScheduledOperations loadedOperations = marshaller.load(result);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(0, children.length);

		// test
		assertNotNull(loadedOperations);
		assertEquals(0, loadedOperations.getScheduledOperation().size());
	}

}
