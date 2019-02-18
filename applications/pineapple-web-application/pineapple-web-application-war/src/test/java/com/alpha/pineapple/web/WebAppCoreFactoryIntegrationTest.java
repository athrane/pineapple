/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen.
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

package com.alpha.pineapple.web;

import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alpha.javautils.SystemUtils;
import com.alpha.junitutils.CreateTestDirectoryRule;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.report.basichtml.BasicHtmlReportGeneratorImpl;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of class {@linkplain WebAppCoreFactory}.
 * 
 * The core component is initialized when the test runner processes
 * the @ContextConfiguration annotation which will initialize the core component
 * to the default Pineapple home directory.
 * 
 * To enable each test to have individual configurations the core component is
 * reinitialized using the {@linkplain WebAppCoreFactory} factory methods.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/webapp-config.xml")
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class WebAppCoreFactoryIntegrationTest {

	/**
	 * First array index.
	 */
	final static int FIRST_INDEX = 0;

	/**
	 * Event capturing result listener.
	 */
	class CapturingResultListener implements ResultListener {

		/**
		 * Captured results.
		 */
		List<ExecutionResultNotification> results;

		/**
		 * CapturingResultListener constructor.
		 */
		public CapturingResultListener() {
			super();
			results = new ArrayList<ExecutionResultNotification>();
		}

		@Override
		public void notify(ExecutionResultNotification notification) {
			results.add(notification);
		}

	}

	//@Rule
	//public CreateTestDirectoryRule testDirRule = new CreateTestDirectoryRule();

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Web application context
	 */
	@Resource
	WebApplicationContext wac;

	/**
	 * Object under test.
	 */
	@Resource
	WebAppCoreFactory webAppCoreFactory;

	/**
	 * Core component.
	 */
	@Resource
	PineappleCore coreComponent;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Mock MVC.
	 */
	MockMvc mockMvc;

	/**
	 * Modules directory.
	 */
	File modulesDir;

	/**
	 * Conf directory.
	 */
	File confDir;

	/**
	 * Object mother for module.
	 */
	ObjectMotherModule moduleMother;

	/**
	 * Random module.
	 */
	String randomModule;

	/**
	 * Random module.
	 */
	String randomModule2;

	/**
	 * Random operation.
	 */
	String randomOperation;

	/**
	 * Random environment.
	 */
	String randomEnvironment;

	/**
	 * Random resource name.
	 */
	String randomResource;

	@Before
	public void setUp() throws Exception {
		randomModule = RandomStringUtils.randomAlphabetic(10);
		randomModule2 = RandomStringUtils.randomAlphabetic(10);
		randomOperation = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomResource = RandomStringUtils.randomAlphabetic(10) + "-resource";

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// define directory names
		modulesDir = new File(testDirectory, "modules");
		confDir = new File(testDirectory, "conf");

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// initialize Spring MVC mock
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
	}

	@After
	public void tearDown() throws Exception {

		// clear the pineapple.home.dir system setting
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);
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
	 * Test that factory is initialized by the Spring context configuration as part
	 * of the test setup.
	 */
	@Test
	public void testFactoryIsInitializedBySpringContextConfig() throws Exception {

		// test
		assertNotNull(webAppCoreFactory);
	}

	/**
	 * Test that core component is already initialized by the Spring context
	 * configuration as part of the test setup.
	 */
	@Test
	public void testCoreIsInitializedBySpringContextConfig() throws Exception {

		// test
		assertNotNull(coreComponent);
	}

	/**
	 * Test that core component configured by web application core factory as part
	 * of the Spring configuration is configured with report generator.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCoreInitializedBySpringIsConfiguredWithReportGenerator() throws Exception {
		ResultListener[] listeners = coreComponent.getListeners();

		// test
		assertEquals(1, listeners.length);
		assertThat(BasicHtmlReportGeneratorImpl.class.getName(),
				StringContains.containsString(listeners[FIRST_INDEX].getClass().getName()));
	}

	/**
	 * Test that factory method returns defined core component.
	 * 
	 * The core component initializes is runtime directories from the pineapple
	 * system property.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCreateCoreReturnsCoreComponent() throws Exception {
		createEnvConfigurationAndModule();

		// test
		assertNotNull(webAppCoreFactory.createCore());
	}

	/**
	 * Test core component returned by factory is same instance which is created by
	 * Spring context,
	 * 
	 * The core component initializes is runtime directories from the pineapple
	 * system property.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCoreCreatedByFactoryIsSameInstanceAsCreateBySpringContext() throws Exception {
		createEnvConfigurationAndModule();

		PineappleCore core = webAppCoreFactory.createCore();

		// test
		assertEquals(core.hashCode(), coreComponent.hashCode());
	}

	/**
	 * Test that core component is initialised successfully.
	 *
	 * The core component initializes is runtime directories from the pineapple
	 * system property.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCoreIsInitialisedSuccessfully() throws Exception {
		createEnvConfigurationAndModule();

		// create core
		PineappleCore core = webAppCoreFactory.createCore();

		// test
		assertTrue(core.getInitializationInfo().isSuccess());
	}

	/**
	 * Test that core component created by web application core factory as part of
	 * the Spring configuration is configured with report generator.
	 *
	 * The core component initializes is runtime directories from the pineapple
	 * system property.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCoreCreatedByFactoryIsConfiguredWithReportGenerator() throws Exception {
		createEnvConfigurationAndModule();

		// create core
		PineappleCore core = webAppCoreFactory.createCore();

		// get listeners
		ResultListener[] listeners = core.getListeners();

		// test
		assertEquals(1, listeners.length);
		assertThat(BasicHtmlReportGeneratorImpl.class.getName(),
				StringContains.containsString(listeners[FIRST_INDEX].getClass().getName()));
	}

	/**
	 * Test that factory method returns the same core component.
	 *
	 * The core component initializes is runtime directories from the pineapple
	 * system property.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testFactoryReturnsTheSameCoreInstance() throws Exception {
		createEnvConfigurationAndModule();

		PineappleCore core = webAppCoreFactory.createCore();
		PineappleCore core2 = webAppCoreFactory.createCore();

		// test
		assertEquals(core.hashCode(), core2.hashCode());
	}

	/**
	 * Test that factory method returns core with one report generator instance
	 * despite multiple core creations (idempotence).
	 * 
	 * The factory registers the report generator created by the Spring context,
	 * i.e. the same report generator instance each time.
	 * 
	 * The core component (e.g. the result repository) only adds a listener if it
	 * doesn't already exists. Result is that the listener is added the first time
	 * only.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testFactoryReturnsCoreWithOneResultListenerDespiteMultipleCoreCreations() throws Exception {
		createEnvConfigurationAndModule();

		PineappleCore core = webAppCoreFactory.createCore();
		ResultListener[] listeners = core.getListeners();

		// test
		assertEquals(1, listeners.length);

		core = webAppCoreFactory.createCore();
		listeners = core.getListeners();

		// test
		assertEquals(1, listeners.length);

		core = webAppCoreFactory.createCore();
		listeners = core.getListeners();

		// test
		assertEquals(1, listeners.length);
	}

	/**
	 * Test that factory method returns same report generator instance.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testFactoryReturnsCoreWithSameReportGeneratorInstance() throws Exception {
		createEnvConfigurationAndModule();

		PineappleCore core = webAppCoreFactory.createCore();
		ResultListener[] listeners = core.getListeners();

		// test
		assertEquals(1, listeners.length);

		// get hash code
		int reportGeneratorHashCode = listeners[FIRST_INDEX].hashCode();

		core = webAppCoreFactory.createCore();
		listeners = core.getListeners();

		// test
		assertEquals(1, listeners.length);

		// get hash code
		int reportGeneratorHashCode2 = listeners[FIRST_INDEX].hashCode();

		// test
		assertEquals(reportGeneratorHashCode, reportGeneratorHashCode2);
	}

	/**
	 * Test core component returned by factory is re-initialized on each invocation.
	 * 
	 * The core component initializes is runtime directories from the pineapple
	 * system property.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testCoreCreatedByFactoryIsReinitialized() throws Exception {
		createEnvConfigurationAndModule();

		// add listener to core component created by Spring
		CapturingResultListener capturingListener = new CapturingResultListener();
		coreComponent.addListener(capturingListener);

		// create new core to re-initialize
		PineappleCore core = webAppCoreFactory.createCore();

		// test captured events
		assertFalse(capturingListener.results.isEmpty());
		ExecutionResultNotification notification = capturingListener.results.get(FIRST_INDEX);
		ExecutionResult initResult = notification.getResult();

		assertThat(initResult.getDescription(),
				StringContains.containsString("Pineapple core component initialization"));
		assertTrue(initResult.isSuccess());
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
	public void testModulesIsRefreshedWhenCoreComponentisReinitialized() throws Exception {
		createEnvConfigurationAndModule();

		// create new core to re-initialize
		PineappleCore core = webAppCoreFactory.createCore();

		// test module repository contains created module only
		ModuleInfo[] moduleInfos = core.getAdministration().getModuleRepository().getInfos();
		assertEquals(1, moduleInfos.length);
		assertTrue(core.getAdministration().getModuleRepository().contains(randomModule));

		// create additional module
		moduleMother.createModuleWithSingleModel(modulesDir, randomModule2, randomEnvironment, randomResource);

		// create new core to re-initialize
		core = webAppCoreFactory.createCore();

		// test module repository contains two modules
		moduleInfos = core.getAdministration().getModuleRepository().getInfos();
		assertEquals(2, moduleInfos.length);
		assertTrue(core.getAdministration().getModuleRepository().contains(randomModule));
		assertTrue(core.getAdministration().getModuleRepository().contains(randomModule2));
	}

}
