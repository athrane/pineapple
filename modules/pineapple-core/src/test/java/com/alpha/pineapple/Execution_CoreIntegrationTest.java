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

package com.alpha.pineapple;

import static com.alpha.testutils.TestUtilsTestConstants.helloWorldOperation;
import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the class {@link CoreImpl} which focuses on execution of
 * operations.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class Execution_CoreIntegrationTest {

	/**
	 * NULL credential reference.
	 */
	static final String NULL_CRED_REF = null;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Core factory.
	 */
	@Resource
	CoreFactory coreFactory;

	/**
	 * Object mother for credential provider
	 */
	@Resource
	ObjectMotherCredentialProvider providerMother;

	/**
	 * Credential provider.
	 */
	CredentialProvider provider;

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
	 * Random resource name.
	 */
	String randomResourceName;

	@Before
	public void setUp() throws Exception {

		randomResourceXmlName = RandomStringUtils.randomAlphabetic(10) + ".xml";
		randomDirName = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomModule = RandomStringUtils.randomAlphabetic(10) + "-module";
		randomResourceName = RandomStringUtils.randomAlphabetic(10) + "-resource";

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// create credential provider
		provider = providerMother.createEmptyCredentialProvider();

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
	 * Wait until module execution is completed
	 * 
	 * @param executionInfo
	 *            Execution info.
	 * @throws InterruptedException
	 *             if test fails
	 */
	void waitForOperationToComplete(ExecutionInfo executionInfo) throws InterruptedException {
		while (executionInfo.getResult().isExecuting()) {
			ConcurrencyUtils.waitSomeMilliseconds(2);
		}
	}

	/**
	 * Get execution result from core which contains the state of the initialization
	 * 
	 * @param core
	 *            The core component.
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
	 * Create custom environment configuration.
	 * 
	 * @return resource file.
	 */
	File createCustomEnvConfig() {
		// create pineapple runtime sub directories, e.g conf and modules
		confDir.mkdirs();
		modulesDir.mkdirs();

		// create environment configuration and save it
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResourceName, pluginIdHelloWorld);
		File resourcesFile = envConfigMother.createResourcesFileName(confDir);
		envConfigMother.jaxbMarshall(configuration, resourcesFile);
		return resourcesFile;
	}

	/**
	 * Test that test operation can be executed with empty test model with a
	 * generated default configuration
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationSucceeds_WithEmptyModel_WithDefaultEnvConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		PineappleCore core = coreFactory.createCore();
		assertTrue(getInitializationResultFromCore(core).isSuccess());
		moduleMother.createModuleWithSingleEmptyModel(modulesDir, randomModule, randomEnvironment);
		core.getAdministration().getModuleRepository().initialize();
		core.getAdministration().getResourceRepository().createEnvironment(randomEnvironment,
				"description for" + randomEnvironment);
		core.getAdministration().getResourceRepository().create(randomEnvironment, randomResourceName,
				pluginIdHelloWorld, NULL_CRED_REF);

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());
	}

	/**
	 * Test that test operation can be executed with empty test model with a custom
	 * environment configuration
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationSucceeds_WithEmptyModel_WithCustomConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		File resourcesFile = createCustomEnvConfig();
		moduleMother.createModuleWithSingleEmptyModel(modulesDir, randomModule, randomEnvironment);
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());
	}

	/**
	 * Test that execution of test operation fails with a simple test model with a
	 * custom environment configuration due to non-existing module.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationFailsDueToNonExistingModule_WithCustomConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		File resourcesFile = createCustomEnvConfig();
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		assertFalse(executionInfo.getResult().isSuccess());
		Map<String, String> messages = executionInfo.getResult().getMessages();
		String message = messages.get(ExecutionResult.MSG_STACKTRACE);
		assertTrue(message.contains("com.alpha.pineapple.module.ModuleNotFoundException: Failed to locate module "));
	}

	/**
	 * Test that execution of test operation fails with a simple test model with a
	 * custom environment configuration due to module doen't contain model for
	 * selected non-existing environment.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationFailsDueToNonExistingModel_WithCustomConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		File resourcesFile = createCustomEnvConfig();
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, pluginIdHelloWorld);
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, "non-existing-env", randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertFalse(executionInfo.getResult().isSuccess());
		Map<String, String> messages = executionInfo.getResult().getMessages();
		String message = messages.get(ExecutionResult.MSG_STACKTRACE);
		assertTrue(message
				.contains("com.alpha.pineapple.module.ModelNotFoundException: Failed to locate model for environment"));
	}

	/**
	 * Test that execution of test operation fails with a simple test model with a
	 * custom environment configuration due resource not defined for selected
	 * environment.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationFailsDueToNonExistingResource_WithCustomConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		File resourcesFile = createCustomEnvConfig();
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, pluginIdHelloWorld);
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertFalse(executionInfo.getResult().isSuccess());
		Map<String, String> messages = executionInfo.getResult().getMessages();
		String message = messages.get(ExecutionResult.MSG_STACKTRACE);
		assertTrue(
				message.contains("com.alpha.pineapple.resource.ResourceNotFoundException: Failed to locate resource"));
	}

	/**
	 * Test that execution of XYZ operation fails with a simple test model with a
	 * custom environment configuration due operation not supported for plugin.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationFailsDueToUnspportedOperation_WithCustomConfig() throws Exception {
		String randomOperation = RandomStringUtils.randomAlphabetic(8);

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		File resourcesFile = createCustomEnvConfig();
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, randomResourceName);
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(randomOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertFalse(executionInfo.getResult().isSuccess());
		ExecutionResult[] childResults = executionInfo.getResult().getChildren();
		assertEquals(1, childResults.length);
		ExecutionResult operationResult = childResults[0];
		assertFalse(operationResult.isSuccess());
		Map<String, String> messages = operationResult.getMessages();
		String message = messages.get(ExecutionResult.MSG_STACKTRACE);
		assertTrue(message.contains("com.alpha.pineapple.plugin.repository.OperationNotSupportedException"));
	}

	/**
	 * Test that test operation can be executed twice with empty model. with a
	 * generated default configuration
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationTwiceSucceeds_WithEmptyModel_WithDefaultEnvConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		PineappleCore core = coreFactory.createCore();
		assertTrue(getInitializationResultFromCore(core).isSuccess());
		moduleMother.createModuleWithSingleEmptyModel(modulesDir, randomModule, randomEnvironment);
		core.getAdministration().getModuleRepository().initialize();
		core.getAdministration().getResourceRepository().createEnvironment(randomEnvironment,
				"description for" + randomEnvironment);
		core.getAdministration().getResourceRepository().create(randomEnvironment, randomResourceName,
				pluginIdHelloWorld, NULL_CRED_REF);

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());

		// execute operation
		ExecutionInfo executionInfo2 = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo2);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());
	}

	/**
	 * Test that test operation can be executed twice with empty model. with a
	 * custom configuration
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationTwiceSucceeds_WithEmptyModel_WithCustomConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		File resourcesFile = createCustomEnvConfig();
		moduleMother.createModuleWithSingleEmptyModel(modulesDir, randomModule, randomEnvironment);
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());

		// execute operation
		ExecutionInfo executionInfo2 = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo2);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());
	}

	/**
	 * Test that test operation can be executed with simple test model with a custom
	 * environment configuration
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationSucceeds_WithSimpleModelAndHelloWorldPlugin_WithCustomConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		File resourcesFile = createCustomEnvConfig();
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, randomResourceName);
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());
	}

	/**
	 * Test that test operation can be executed with simple test model with a
	 * generated default configuration
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	@Test
	public void testOperationSucceeds_WithSimpleModelAndHelloWorldPlugin_WithDefaultEnvConfig() throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create core component
		PineappleCore core = coreFactory.createCore();
		assertTrue(getInitializationResultFromCore(core).isSuccess());
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, randomResourceName);
		core.getAdministration().getResourceRepository().createEnvironment(randomEnvironment,
				"description for" + randomEnvironment);
		core.getAdministration().getResourceRepository().create(randomEnvironment, randomResourceName,
				pluginIdHelloWorld, NULL_CRED_REF);
		core = coreFactory.createCore(); // "create" new core to get full
		// re-initialization

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		waitForOperationToComplete(executionInfo);
		ConcurrencyUtils.waitOneSec();

		// test
		assertTrue(executionInfo.getResult().isSuccess());
	}

}
