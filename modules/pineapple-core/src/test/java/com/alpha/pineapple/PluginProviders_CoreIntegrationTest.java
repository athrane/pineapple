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

import static org.junit.Assert.assertEquals;
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
import org.springframework.test.context.ContextHierarchy;
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
import com.alpha.spring.config.IntegrationTestSpringConfig;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherCredentialProvider;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;
import com.alpha.testutils.testplugins.pluginprovider.administration.AdministrationProviderTestPluginImpl;
import com.alpha.testutils.testplugins.pluginprovider.executioninfo.NoOperationImpl;
import com.alpha.testutils.testplugins.pluginprovider.runtimedirectories.PluginImpl;

/**
 * Integration test of the class {@link CoreImpl} which focuses on runtime
 * access to runtime providers from within plugins.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
@ContextHierarchy({ @ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" }),
		@ContextConfiguration(classes = IntegrationTestSpringConfig.class) })
public class PluginProviders_CoreIntegrationTest {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

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
	String randomModuleName;

	/**
	 * Random resource name.
	 */
	String randomResourceName;

	@Before
	public void setUp() throws Exception {

		randomResourceXmlName = RandomStringUtils.randomAlphabetic(10) + ".xml";
		randomDirName = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomModuleName = RandomStringUtils.randomAlphabetic(10) + "-module";
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
	 * Configures Pineapple with directories, environment configuration and start
	 * the core component.
	 * 
	 * @param pluginId Name of plugin id to add as resource in the environment
	 *                 configuration.
	 * 
	 * @return Running configured core component.
	 * 
	 * @throws Exception if test fails.
	 */
	PineappleCore configureAndStartPineapple(String pluginId) throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create pineapple runtime sub directories, e.g conf and modules
		confDir.mkdirs();
		modulesDir.mkdirs();

		// create environment configuration and save it
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResourceName, pluginId);
		File resourcesFile = envConfigMother.createResourcesFileName(confDir);
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);

		// test initialization was a success
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		return core;
	}

	/**
	 * Execute operation.
	 * 
	 * @param core      core component.
	 * @param operation operation name.
	 * 
	 * @return execution result.
	 * 
	 * @throws Exception if test fails.
	 */
	ExecutionInfo executeOperationAndWait(PineappleCore core, String operation) throws Exception {

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(operation, randomEnvironment, randomModuleName);

		// wait until module execution is completed
		while (executionInfo.getResult().isExecuting()) {
			ConcurrencyUtils.waitSomeMilliseconds(2);
		}

		// sleep a sec for state to settle
		ConcurrencyUtils.waitOneSec();
		return executionInfo;
	}

	/**
	 * Test that operation can be executed with plugin which can access the runtime
	 * directory provider.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testExecuteTestOperationSucceeds_CanAccessRuntimeDirectoryProvider() throws Exception {
		PineappleCore core = configureAndStartPineapple(PluginImpl.PLUGIN_ID);

		// create module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModuleName, randomEnvironment, randomResourceName);
		core.getAdministration().getModuleRepository().initialize(); // refresh
		// modules
		// to pick
		// the
		// newly
		// create
		// one

		// execute
		ExecutionInfo executionInfo = executeOperationAndWait(core, PluginImpl.OPERATION);

		// test
		assertTrue(executionInfo.getResult().isSuccess());

		// get messages
		Map<String, String> messages = executionInfo.getResult().getMessages();

		// test
		assertEquals("...", messages.get("toString"));
	}

	/**
	 * Test that operation can be executed with plugin which can access the runtime
	 * info provider.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testExecuteTestOperationSucceeds_CanAccessRuntimeInfoProvider() throws Exception {
		PineappleCore core = configureAndStartPineapple(NoOperationImpl.PLUGIN_ID);

		// create module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModuleName, randomEnvironment, randomResourceName);
		core.getAdministration().getModuleRepository().initialize(); // refresh
		// modules
		// to pick
		// the
		// newly
		// create
		// one

		// execute
		ExecutionInfo executionInfo = executeOperationAndWait(core, NoOperationImpl.OPERATION);

		// test
		assertTrue(executionInfo.getResult().isSuccess());

		// get messages
		Map<String, String> messages = executionInfo.getResult().getMessages();

		// test
		assertEquals("...", messages.get("toString"));
	}

	/**
	 * Test that operation can be executed with plugin which can access the
	 * administration provider.
	 * 
	 * @throws Exception If test fails.
	 */
	@Test
	public void testExecuteTestOperationSucceeds_CanAccessAdministrationProvider() throws Exception {
		PineappleCore core = configureAndStartPineapple(AdministrationProviderTestPluginImpl.PLUGIN_ID);

		// create module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModuleName, randomEnvironment, randomResourceName);
		core.getAdministration().getModuleRepository().initialize(); // refresh
		// modules
		// to pick
		// the
		// newly
		// create
		// one

		// execute
		ExecutionInfo executionInfo = executeOperationAndWait(core, AdministrationProviderTestPluginImpl.OPERATION);

		// test
		assertTrue(executionInfo.getResult().isSuccess());

		// get messages
		Map<String, String> messages = executionInfo.getResult().getMessages();

		// test
		assertEquals("...", messages.get("toString"));
	}

}
