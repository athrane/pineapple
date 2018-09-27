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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

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
import com.alpha.testutils.testplugins.infiniteloop.coop.CooperativeInfiniteLoopPluginImpl;
import com.alpha.testutils.testplugins.infiniteloop.noncoop.NonCoperativeInfiniteLoopPluginImpl;

/**
 * Integration test of the class {@link CoreImpl} which focuses on cancellation
 * of execution.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class Continuation_CoreIntegrationTest {

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
	String randomResource;

	@Before
	public void setUp() throws Exception {

		randomResourceXmlName = RandomStringUtils.randomAlphabetic(10) + ".xml";
		randomDirName = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomModule = RandomStringUtils.randomAlphabetic(10) + "-module";
		randomResource = RandomStringUtils.randomAlphabetic(10) + "-resource";

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
	 * Configures Pineapple with directories, environment configuration and start
	 * the core component.
	 * 
	 * @param pluginId
	 *            Name of plugin id to add as resource in the environment
	 *            configuration.
	 * 
	 * @return Running configured core component.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	PineappleCore configureAndStartPineapple(String pluginId) throws Exception {
		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create pineapple runtime sub directories, e.g conf and modules
		confDir.mkdirs();
		modulesDir.mkdirs();

		// create environment configuration and save it
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource, pluginId);
		File resourcesFile = envConfigMother.createResourcesFileName(confDir);
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create core component
		PineappleCore core = coreFactory.createCore(provider, resourcesFile);

		// test initialization was a success
		assertTrue(getInitializationResultFromCore(core).isSuccess());

		return core;
	}

	/**
	 * Test that operation can cancelled.
	 * 
	 * The plugin implements a infinite loop, whihc co-operatively inspects whehter
	 * the execution ihas been cancelled.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	// @Test

	public void testCanCancelOperationWithCooperativePlugin() throws Exception {
		PineappleCore core = configureAndStartPineapple(CooperativeInfiniteLoopPluginImpl.PLUGIN_ID);

		// create module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, randomResource);
		core.getAdministration().getModuleRepository().initialize(); // refresh
		// modules
		// to pick
		// the
		// newly
		// create
		// one

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		ConcurrencyUtils.waitSomeMilliseconds(200); // wait for execution to
		// start

		// cancel
		core.cancelOperation(executionInfo);
		waitForOperationToComplete(executionInfo); // wait for cancellation to
		// complete

		// test
		assertTrue(executionInfo.getResult().isInterrupted());
	}

	/**
	 * Test that operation can cancelled.
	 * 
	 * The plugin implements a infinite loop, which adds child results, the addition
	 * of child results triggers cancellation of the execution.
	 * 
	 * @throws Exception
	 *             If test fails.
	 */
	// @Test
	public void testCanCancelOperationWithNonCooperativePlugin() throws Exception {
		PineappleCore core = configureAndStartPineapple(NonCoperativeInfiniteLoopPluginImpl.PLUGIN_ID);

		// create module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule, randomEnvironment, randomResource);
		core.getAdministration().getModuleRepository().initialize(); // refresh
		// modules
		// to pick
		// the
		// newly
		// create
		// one

		// execute operation
		ExecutionInfo executionInfo = core.executeOperation(helloWorldOperation, randomEnvironment, randomModule);
		ConcurrencyUtils.waitSomeMilliseconds(100); // wait for execution to
		// start

		// cancel
		core.cancelOperation(executionInfo);
		waitForOperationToComplete(executionInfo); // wait for cancellation to
		// complete

		// test
		assertTrue(executionInfo.getResult().isInterrupted());
	}

}
