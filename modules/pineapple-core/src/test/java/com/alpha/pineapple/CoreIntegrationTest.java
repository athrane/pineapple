/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

package com.alpha.pineapple;

import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.spring.config.IntegrationTestSpringConfig;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the class {@link CoreImpl} which focuses on
 * initialization.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
@ContextHierarchy({ @ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" }),
		@ContextConfiguration(classes = IntegrationTestSpringConfig.class) })
public class CoreIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource
	PineappleCore uninitializedPineappleCore;

	/**
	 * Object under test.
	 */
	@Resource
	CoreFactory coreFactory;

	/**
	 * Credential provider.
	 */
	CredentialProvider provider;

	/**
	 * Object mother for credential provider
	 */
	@Resource
	ObjectMotherCredentialProvider providerMother;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Object mother for module.
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Modules directory.
	 */
	File modulesDir;

	/**
	 * Conf directory.
	 */
	File confDir;

	/**
	 * Random file name.
	 */
	String randomResourceXmlName;

	/**
	 * Random directory name.
	 */
	String randomDirName;

	/**
	 * Random environment..
	 */
	String randomEnvironment;

	/**
	 * Random module.
	 */
	String randomModule;

	/**
	 * Random module.
	 */
	String randomModule2;

	/**
	 * Random resource name.
	 */
	String randomResource;

	@Before
	public void setUp() throws Exception {

		randomResourceXmlName = RandomStringUtils.randomAlphabetic(10) + ".xml";
		randomDirName = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomModule = RandomStringUtils.randomAlphabetic(10) + "-module";
		randomModule2 = RandomStringUtils.randomAlphabetic(10) + "-module";
		randomResource = RandomStringUtils.randomAlphabetic(10) + "-resource";

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// define directory names
		modulesDir = new File(testDirectory, "modules");
		confDir = new File(testDirectory, "conf");

		// clear the pineapple.home.dir system property
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

		// fail if the the pineapple.home.dir system property is set
		assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
	}

	@After
	public void tearDown() throws Exception {

		// clear the pineapple.home.dir system setting
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);
	}

	/**
	 * Set the pineapple.home.dir system property.
	 */
	void setPineappleHome() {
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());
	}

	/**
	 * Get execution result from core which contains the state of the initialization
	 * 
	 * @param core The core component.
	 * 
	 * @return execution result from core which contains the state of the
	 *         initialization
	 */
	ExecutionResult getInitializationResultFromCore(PineappleCore core) {
		CoreImpl coreImpl = (CoreImpl) core;
		ExecutionResult result = coreImpl.getInitializationInfo();
		return result;
	}

	/**
	 * Create environment configuration and module.
	 * 
	 * The environment configuration contains a random environment with resource for
	 * hello world plugin.
	 * 
	 * The module contains a model which uses the hello world plugin.
	 * 
	 * @throws Exception if test fails.
	 */
	void createEnvConfigurationAndModule() throws Exception {
		// create pineapple runtime sub directories, e.g conf and modules
		confDir.mkdirs();
		modulesDir.mkdirs();

		// create module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, randomResource);

		// create environment configuration and save it
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource, pluginIdHelloWorld);
		File resourcesFile = envConfigMother.createResourcesFileName(confDir);
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create credentials configuration and save it
		Configuration credentialsConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		File credentialsFile = envConfigMother.createCredentialsFileName(confDir);
		envConfigMother.jaxbMarshall(credentialsConfiguration, credentialsFile);
	}

	/**
	 * Test that core component can be looked up from the context.
	 */
	@Test
	public void testCanGetCoreFromContext() {
		assertNotNull(uninitializedPineappleCore);
	}

	/**
	 * Test that core factory can be looked up from the context.
	 */
	@Test
	public void testCanGetFactoryFromContext() {
		assertNotNull(coreFactory);
	}

	/**
	 * Factory test.
	 * 
	 * If the factory is created with its dependencies injected by Spring, then it
	 * will return the same core instance when the factory method is invoked as
	 * returned by the spring context.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testFactoryWithInjectedDependeciesReturnsSameCoreInstanceAsSpringContext() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();
		PineappleCore core = coreFactory.createCore(provider);
		assertEquals(uninitializedPineappleCore, core);
	}

	/**
	 * Factory test.
	 * 
	 * If the factory is created with its dependencies injected by Spring, then it
	 * will return the same core instance when invoked multiple times, e.g. the
	 * method is idempotent.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testFactoryWithInjectedDependeciesIsIdempotentForCoreFactoryMethod() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		PineappleCore core = coreFactory.createCore(provider);
		PineappleCore core2 = coreFactory.createCore(provider);

		// test
		assertEquals(uninitializedPineappleCore, core);
		assertEquals(uninitializedPineappleCore, core2);
	}

	/**
	 * Factory test, i.e. that core instance can be created from {@link CoreFactory}
	 * and that it is successfully initialized.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * Initialization FAILS since the plugins defined in the default configuration
	 * isn't available on the class path.
	 * 
	 * @throws CoreException If test fails.
	 */
	@Test
	public void testInitialize_WithPineappleHome_AndDefaultConfiguration_InitFails() throws CoreException {
		setPineappleHome();

		// create core component
		PineappleCore core = coreFactory.createCore();

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertFalse(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Factory test, i.e. that core instance can be created from {@link CoreFactory}
	 * and that is it contains four child results (initialize home dir, create
	 * default configuration, load resource configuration and initialize plugin
	 * activator) since "initialize scheduled operations" is skipped when the plugin
	 * initialization fails.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * Initialization FAILS since the plugins defined in the default configuration
	 * isn't available on the class path.
	 * 
	 * @throws CoreException If test fails.
	 */
	@Test
	public void testInitialize_WithPineappleHome_AndDefaultConfiguration_Contains4xExecutionResults()
			throws CoreException {
		setPineappleHome();

		// create core component
		PineappleCore core = coreFactory.createCore();

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertEquals(4, result.getChildren().length);
	}

	/**
	 * Factory test, i.e. that core instance can be created from {@link CoreFactory}
	 * and that it is successfully initialized.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testInitialize_WithPineappleHome_AndCredentialProvider_InitSucceeds() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create core component
		PineappleCore core = coreFactory.createCore(provider);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Factory test, i.e. that core instance can be created from {@link CoreFactory}
	 * and that is it contains three child results (load resource configuration,
	 * initialize plugin activator and initialize scheduled operations) since
	 * "initialize home dir" and "create default configuration" are skipped.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testInitialize_WithPineappleHome_AndCredentialProvider_Contains3xExecutionResults() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create core component
		PineappleCore core = coreFactory.createCore(provider);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertEquals(3, result.getChildren().length);
	}

	/**
	 * Factory test, i.e. that core instance can be created from {@link CoreFactory}
	 * and that a default configuration is created.
	 * 
	 * The pineapple-home.dir property is defined to defined where the the core
	 * component will load the resources file from.
	 * 
	 * The resource file activate the test plugin "com.alpha.pineapple.plugin.test"
	 * located in the test-utils project.
	 * 
	 * @throws CoreException If test fails.
	 */
	@Test
	public void testInitializeWithPineappleHome_DefaultConfigurationIsCreated() throws CoreException {
		File homeDir = new File(testDirectory, randomDirName);
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, homeDir.getAbsolutePath());

		// test
		assertFalse(homeDir.exists());

		// create core component
		PineappleCore core = coreFactory.createCore();

		// test
		assertNotNull(core);
		assertTrue(homeDir.exists());

		File[] subDirs = homeDir.listFiles();

		// test
		assertEquals(3, subDirs.length); // only 3, since logs is in another
		// place
	}

	/**
	 * Factory test, i.e. that core instance can be created from {@link CoreFactory}
	 * and that it is successfully initialized.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testInitializeMultipleTimesWithPineappleHome_InitSucceeds() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create core component
		PineappleCore core = coreFactory.createCore(provider);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());

		// re-initialize
		((CoreImpl) core).initialize(provider);

		// get initialization result form core
		ExecutionResult result2 = getInitializationResultFromCore(core);

		// test
		assertNotNull(result2);
		assertFalse(result2.isExecuting());
		assertTrue(result2.isSuccess());
		assertTrue(result2.isRoot());

	}

	/**
	 * Factory test, i.e. that core instance can be created from
	 * {@link CoreFactory}.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstanceFromFactory_InitSucceeds() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create core component
		PineappleCore core = coreFactory.createCore(provider);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Factory test, i.e. that core instance can be created from
	 * {@link CoreFactory}.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstanceFromFactory_WithZeroListeners() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();
		ResultListener[] listeners = {};

		// create core component
		PineappleCore core = coreFactory.createCore(provider, listeners);

		// test
		assertNotNull(core);

		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Factory test, i.e. that core instance can be created from
	 * {@link CoreFactory}.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstanceFromFactory_WithOneListener() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create mock listeners
		ResultListener mockListener = createMock(ResultListener.class);
		mockListener.notify(isA(ExecutionResultNotification.class));
		expectLastCall().anyTimes();
		replay(mockListener);

		ResultListener[] listeners = { mockListener };

		// create core component
		PineappleCore core = coreFactory.createCore(provider, listeners);

		// test
		assertNotNull(core);

		// test
		verify(mockListener);
	}

	/**
	 * Factory test, i.e. that core instance can be created from
	 * {@link CoreFactory}.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The core component will load the resources file from the class path
	 * src/test/resources/resources.xml and activate the test plugin
	 * "com.alpha.pineapple.plugin.test" located in the test-utils project.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstanceFromFactory_WithTwoListeners() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create mock listeners
		ResultListener mockListener = createMock(ResultListener.class);
		mockListener.notify(isA(ExecutionResultNotification.class));
		expectLastCall().anyTimes();
		replay(mockListener);

		// create mock listeners #3
		ResultListener mockListener2 = createMock(ResultListener.class);
		mockListener2.notify(isA(ExecutionResultNotification.class));
		expectLastCall().anyTimes();
		replay(mockListener2);

		ResultListener[] listeners = { mockListener, mockListener2 };

		// create core component
		PineappleCore core = coreFactory.createCore(provider, listeners);

		// test
		assertNotNull(core);

		// test
		verify(mockListener);
		verify(mockListener2);
	}

	/**
	 * Initialization test. Tests that core instance can be created with external
	 * resources file. The file is specified by an absolute file name.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstance_WithExternalResourcesFile() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create test resources configuration
		Configuration configuration;
		configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

		// define resources file
		File resourcesFile = envConfigMother.createResourcesFileName(testDirectory);

		// save file
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Initialization test. Tests that core instance can be created with external
	 * resources file, which isn't named "resources.xml". The resources file is
	 * specified by an absolute file name.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * @throws CoreException If test fails.
	 */
	@Test
	public void testCreateInstance_WithExternalResourcesFile2() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create test resources configuration
		Configuration configuration;
		configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

		// define resources file
		File resourcesFile = new File(testDirectory, randomResourceXmlName);

		// save file
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Initialization test, tests that core instance can be created with internal
	 * resources file, with a named resources file, f.x. "resources.xml". The test
	 * case will attempt to load the file from the class path which contains
	 * src/test/resources/resources.xml.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * The resources file defines the test plugin "com.alpha.pineapple.plugin.test"
	 * located in the test-utils project.
	 * 
	 * @throws Exception if test fails
	 */
	@Test
	public void testCreateInstance_WithNamedInternalResourcesFile() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// convert file name to factory
		File resourcesFile = new File("resources.xml");

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Initialization test. Tests that core instance can be created with external
	 * resources file. The file is specified by an absolute file name.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstance_WithExternalResourcesFile_WithZeroListeners() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		ResultListener[] listeners = {};

		// create test resources configuration
		Configuration configuration;
		configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

		// define resources file
		File resourcesFile = envConfigMother.createResourcesFileName(testDirectory);

		// save file
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile, listeners);

		// test
		assertNotNull(core);

		// get initialization result form core
		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertTrue(result.isSuccess());
		assertTrue(result.isRoot());
	}

	/**
	 * Initialization test. Tests that core instance can be created with external
	 * resources file. The file is specified by an absolute file name.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstance_WithExternalResourcesFile_WithOneListener() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create mock listeners
		ResultListener mockListener = createMock(ResultListener.class);
		mockListener.notify(isA(ExecutionResultNotification.class));
		expectLastCall().anyTimes();
		replay(mockListener);

		ResultListener[] listeners = { mockListener };

		// create test resources configuration
		Configuration configuration;
		configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

		// define resources file
		File resourcesFile = envConfigMother.createResourcesFileName(testDirectory);

		// save file
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile, listeners);

		// test
		assertNotNull(core);
	}

	/**
	 * Initialization test. Tests that core instance can be created with external
	 * resources file. The file is specified by an absolute file name.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateInstance_WithExternalResourcesFile_WithTwoListeners() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create mock listeners
		ResultListener mockListener = createMock(ResultListener.class);
		mockListener.notify(isA(ExecutionResultNotification.class));
		expectLastCall().anyTimes();
		replay(mockListener);

		// create mock listeners #3
		ResultListener mockListener2 = createMock(ResultListener.class);
		mockListener2.notify(isA(ExecutionResultNotification.class));
		expectLastCall().anyTimes();
		replay(mockListener2);

		ResultListener[] listeners = { mockListener, mockListener2 };

		// create test resources configuration
		Configuration configuration;
		configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

		// define resources file
		File resourcesFile = envConfigMother.createResourcesFileName(testDirectory);

		// save file
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile, listeners);

		// test
		assertNotNull(core);
	}

	/**
	 * Rejects undefined credential provider.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitializationRejectsUndefinedProvider() throws Exception {
		coreFactory.createCore((CredentialProvider) null);
	}

	/**
	 * Rejects undefined resources file.
	 * 
	 * @throws Exception If test fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitializationRejectsUndefinedResourcesFile() throws Exception {
		coreFactory.createCore(provider, (File) null);
	}

	/**
	 * Rejects undefined listeners.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitializationRejectsUndefinedListeners() throws Exception {
		coreFactory.createCore((ResultListener[]) null);
	}

	/**
	 * Rejects undefined listeners.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitializationRejectsUndefinedListeners2() throws Exception {
		coreFactory.createCore(provider, (ResultListener[]) null);
	}

	/**
	 * Rejects undefined listeners.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInitializationRejectsUndefinedListeners3() throws Exception {
		// create test resources configuration
		Configuration configuration;
		configuration = envConfigMother.createEnvConfigWithSingleTestResourceWithSingleProperty();

		// define resources file
		File resourcesFile = envConfigMother.createResourcesFileName(testDirectory);

		// save file
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile, null);
	}

	/**
	 * Rejects undefined resources file.
	 * 
	 * The Pineapple home system property is set to use the test directory.
	 * 
	 * The credential provider will write the credentials to the Pineapple home set
	 * by the system variable.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testInitializationFailsIfResourcesFileDoesntExists() throws Exception {
		setPineappleHome();
		provider = providerMother.createEmptyCredentialProvider();

		// create resource file name
		File resourcesFile = new File(testDirectory, randomResourceXmlName);

		// invoke to provoke exception
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);

		ExecutionResult result = getInitializationResultFromCore(core);

		// test
		assertNotNull(result);
		assertFalse(result.isExecuting());
		assertFalse(result.isSuccess());
	}

	/**
	 * Test that initialization result is defined after initialization.
	 * 
	 * @throws CoreException If test fails.
	 */
	@Test
	public void testGetInitializationResultIsDefinedAfterInitialization() throws CoreException {
		setPineappleHome();

		// create core component
		PineappleCore core = coreFactory.createCore();

		// test
		assertNotNull(core);

		// type cast
		CoreImpl coreImpl = (CoreImpl) core;

		// test
		assertNotNull(coreImpl);
		assertNotNull(coreImpl.getInitializationInfo());
	}

	/**
	 * Test that initialization result isn't defined prior to initialization.
	 * 
	 * @throws CoreException If test fails.
	 */
	@Test
	public void testGetInitializationResultIsntDefinedPriorToInitialization() throws CoreException {
		// type cast
		CoreImpl coreImpl = (CoreImpl) uninitializedPineappleCore;

		// test
		assertNull(coreImpl.getInitializationInfo());
	}

	/**
	 * Test module repository in core component is updated when core component is
	 * re-initialized.
	 * 
	 * The core component is re-initialized with one module and then re-initialized
	 * with a second module added.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testModulesIsRefreshedWhenbCoreComponentisReinitialized() throws Exception {

		setPineappleHome();

		createEnvConfigurationAndModule();

		// create new core to re-initialize
		PineappleCore core = coreFactory.createCore();

		// test module repository contains created module only
		ModuleInfo[] moduleInfos = core.getAdministration().getModuleRepository().getInfos();
		assertEquals(1, moduleInfos.length);
		assertTrue(core.getAdministration().getModuleRepository().contains(randomModule));

		// create additional module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule2, randomEnvironment, randomResource);

		// create new core to re-initialize
		core = coreFactory.createCore();

		// test module repository contains two modules
		moduleInfos = core.getAdministration().getModuleRepository().getInfos();
		assertEquals(2, moduleInfos.length);
		assertTrue(core.getAdministration().getModuleRepository().contains(randomModule));
		assertTrue(core.getAdministration().getModuleRepository().contains(randomModule2));
	}

}
