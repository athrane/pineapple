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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.configuration.Environment;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * integration test of the class
 * {@link CreateDefaultEnvironmentConfigurationCommand}.
 * 
 * Please notice that the runtime directory provider is mocked to enable
 * override of the configured directories.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CreateDefaulConfigurationCommandIntegrationTest {

	/**
	 * Name of test module of class path.
	 */
	static final String ARCHIVE_ON_CLASSPATH = "test-archive-on-plasspath.jar";

	/**
	 * Object under test.
	 */
	@Resource
	Command createDefaultEnvironmentConfigurationCommand;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Configuration object mother
	 */
	ObjectMotherEnvironmentConfiguration configMother;

	/**
	 * Random resouces name.
	 */
	String randomXmlResourceName;

	/**
	 * Random credentials name.
	 */
	String randomXmlCredentialsName;

	/**
	 * Random name.
	 */
	String randomName;

	/**
	 * Random dir name.
	 */
	String randomDirName;

	/**
	 * Random dir name.
	 */
	String randomDirName2;

	/**
	 * Mock Runtime directory resolver.
	 */
	RuntimeDirectoryProvider runtimeDirectoryProvider;

	/**
	 * Example modules directory.
	 */
	File exampleModulesdir = new File("example-modules");

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomXmlResourceName = RandomStringUtils.randomAlphabetic(10) + ".xml";
		randomXmlCredentialsName = RandomStringUtils.randomAlphabetic(10) + ".xml";
		randomDirName = RandomStringUtils.randomAlphabetic(10);
		randomDirName2 = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create execution result
		executionResult = new ExecutionResultImpl("Root result");

		// configuration object mother
		configMother = new ObjectMotherEnvironmentConfiguration();

		// create mock provider
		runtimeDirectoryProvider = EasyMock.createMock(RuntimeDirectoryProvider.class);

		// inject provider
		ReflectionTestUtils.setField(createDefaultEnvironmentConfigurationCommand, "runtimeDirectoryProvider",
				runtimeDirectoryProvider);
	}

	@After
	public void tearDown() throws Exception {
		testDirectory = null;
		configMother = null;
		createDefaultEnvironmentConfigurationCommand = null;
		executionResult = null;
	}

	/**
	 * Test that files are create if none exists.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultConfigurationIsCreated() throws Exception {
		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(testDirectory);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(testDirectory);
		EasyMock.replay(runtimeDirectoryProvider);

		// create file names
		File resourcesFile = new File(testDirectory, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(testDirectory, CoreConstants.CREDENTIALS_FILE);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
		assertTrue(credentailsFile.exists());
		assertTrue(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that files are create if none exists and even if all directories doesn't
	 * exist
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultConfigurationIsCreatedIfDirectoriesDoesnExist() throws Exception {
		File finalTestDir = new File(testDirectory, randomDirName);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalTestDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(testDirectory);
		EasyMock.replay(runtimeDirectoryProvider);

		// create file names
		File resourcesFile = new File(finalTestDir, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(finalTestDir, CoreConstants.CREDENTIALS_FILE);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
		assertTrue(credentailsFile.exists());
		assertTrue(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that creating the configuration fails if directory is illegal.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testFailsIfdirectoryIsIllegal() throws Exception {
		// create file names
		File resourcesFile = new File(randomName, randomXmlResourceName);
		File credentailsFile = new File(randomName, randomXmlCredentialsName);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(new File(randomName));
		EasyMock.replay(runtimeDirectoryProvider);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(resourcesFile.isFile());
		assertFalse(credentailsFile.exists());
		assertFalse(credentailsFile.isFile());
		assertFalse(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that creation is skipped if resources configuration file exists.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSkipsCreationIfResourcesConfigurationExists() throws Exception {
		File finalTestDir = new File(testDirectory, randomDirName);
		FileUtils.forceMkdir(finalTestDir);

		// create file names
		File resourcesFile = new File(finalTestDir, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(finalTestDir, CoreConstants.CREDENTIALS_FILE);

		// create resources configuration
		configMother.jaxbMarshall(configMother.createEmptyEnvironmentConfiguration(), resourcesFile);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalTestDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// test
		assertTrue(resourcesFile.exists());
		assertFalse(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
		assertFalse(credentailsFile.exists());
		assertFalse(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test that creation is skipped if credential configuration file exists.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSkipsCreationIfCredentialConfigurationExists() throws Exception {
		File finalTestDir = new File(testDirectory, randomDirName);
		FileUtils.forceMkdir(finalTestDir);

		// create file names
		File resourcesFile = new File(finalTestDir, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(finalTestDir, CoreConstants.CREDENTIALS_FILE);

		// create credentials configuration
		configMother.jaxbMarshall(configMother.createEmptyEnvironmentConfiguration(), credentailsFile);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalTestDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// test
		assertFalse(resourcesFile.exists());
		assertTrue(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(resourcesFile.isFile());
		assertTrue(credentailsFile.exists());
		assertTrue(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test created resources configuration contains expected number environments.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultResourcesConfigurationContainsExpectedNumberEnvironments() throws Exception {
		File finalTestDir = new File(testDirectory, randomDirName);
		FileUtils.forceMkdir(finalTestDir);

		// create file names
		File resourcesFile = new File(finalTestDir, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(finalTestDir, CoreConstants.CREDENTIALS_FILE);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalTestDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(testDirectory);
		EasyMock.replay(runtimeDirectoryProvider);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
		assertTrue(credentailsFile.exists());
		assertTrue(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// load resources configuration
		Package targetPackage = Configuration.class.getPackage();
		Configuration resourcesConfig = (Configuration) configMother.jaxbUnmarshall(resourcesFile, targetPackage);

		assertEquals(4, resourcesConfig.getEnvironments().getEnvironment().size());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test created credentials configuration contains expected number environments.
	 * *
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultCredentialsConfigurationContainsExpectedNumberEnvironments() throws Exception {
		File finalTestDir = new File(testDirectory, randomDirName);
		FileUtils.forceMkdir(finalTestDir);

		// create file names
		File resourcesFile = new File(finalTestDir, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(finalTestDir, CoreConstants.CREDENTIALS_FILE);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalTestDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(testDirectory);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
		assertTrue(credentailsFile.exists());
		assertTrue(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// load resources configuration
		Package targetPackage = Configuration.class.getPackage();
		Configuration resourcesConfig = (Configuration) configMother.jaxbUnmarshall(credentailsFile, targetPackage);

		assertEquals(3, resourcesConfig.getEnvironments().getEnvironment().size());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test created configuration contains example modules.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultContainsExampleModules() throws Exception {
		File finalConfDir = new File(testDirectory, randomDirName);
		FileUtils.forceMkdir(finalConfDir);
		File finalModulesDir = new File(testDirectory, randomDirName2);
		FileUtils.forceMkdir(finalConfDir);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalConfDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(finalModulesDir);
		EasyMock.replay(runtimeDirectoryProvider);

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(finalModulesDir.exists());
		assertTrue(!finalModulesDir.isFile());
		assertTrue(finalModulesDir.listFiles().length > 0);
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test created resources configuration contains expected wild card
	 * environments.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultResourcesConfigurationContainsWildcardEnvironment() throws Exception {
		File finalTestDir = new File(testDirectory, randomDirName);
		FileUtils.forceMkdir(finalTestDir);

		// create file names
		File resourcesFile = new File(finalTestDir, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(finalTestDir, CoreConstants.CREDENTIALS_FILE);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalTestDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(testDirectory);
		EasyMock.replay(runtimeDirectoryProvider);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
		assertTrue(credentailsFile.exists());
		assertTrue(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// load resources configuration
		Package targetPackage = Configuration.class.getPackage();
		Configuration resourcesConfig = (Configuration) configMother.jaxbUnmarshall(resourcesFile, targetPackage);

		// test
		List<Environment> environments = resourcesConfig.getEnvironments().getEnvironment();
		assertEquals("*", environments.get(0).getId());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

	/**
	 * Test created resources configuration contains expected wild card
	 * environments.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultResourcesConfigurationWildcardEnvironmentContainsCompositeExecutionResource()
			throws Exception {
		File finalTestDir = new File(testDirectory, randomDirName);
		FileUtils.forceMkdir(finalTestDir);

		// create file names
		File resourcesFile = new File(finalTestDir, CoreConstants.RESOURCE_FILE);
		File credentailsFile = new File(finalTestDir, CoreConstants.CREDENTIALS_FILE);

		// complete mock setup
		EasyMock.expect(runtimeDirectoryProvider.getConfigurationDirectory()).andReturn(finalTestDir);
		EasyMock.expect(runtimeDirectoryProvider.getModulesDirectory()).andReturn(testDirectory);
		EasyMock.replay(runtimeDirectoryProvider);

		// test
		assertFalse(resourcesFile.exists());
		assertFalse(credentailsFile.exists());

		// setup context
		Context context = new ContextBase();
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXECUTIONRESULT_KEY, executionResult);
		context.put(CreateDefaultEnvironmentConfigurationCommand.EXAMPLE_MODULES_KEY, exampleModulesdir);
		context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		createDefaultEnvironmentConfigurationCommand.execute(context);

		// test
		assertTrue(resourcesFile.exists());
		assertTrue(resourcesFile.isFile());
		assertTrue(credentailsFile.exists());
		assertTrue(credentailsFile.isFile());
		assertTrue(executionResult.isSuccess());
		assertFalse(executionResult.isExecuting());

		// load resources configuration
		Package targetPackage = Configuration.class.getPackage();
		Configuration resourcesConfig = (Configuration) configMother.jaxbUnmarshall(resourcesFile, targetPackage);

		// test
		List<Environment> environments = resourcesConfig.getEnvironments().getEnvironment();
		Environment wildCardEnv = environments.get(0);
		List<com.alpha.pineapple.model.configuration.Resource> resources = wildCardEnv.getResources().getResource();
		assertEquals("composite-execution", resources.get(1).getId());

		// verify
		EasyMock.verify(runtimeDirectoryProvider);
	}

}
