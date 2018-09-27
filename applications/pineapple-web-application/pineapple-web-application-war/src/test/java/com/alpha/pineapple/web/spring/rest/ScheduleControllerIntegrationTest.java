/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen.
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

package com.alpha.pineapple.web.spring.rest;

import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_DELETE_OPERATIONS_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_DELETE_OPERATION_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_GET_OPERATIONS_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_SCHEDULE_URI;
import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
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
import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationRespository;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the {@linkplain ScheduledOperationController} class.
 * 
 * PLEASE NOTICE: The {@linkplain WebAppCoreFactory} is used in the test to
 * reinitialize the core component. The core component is reinitialized to make
 * it pick up the definition of the Pineapple home directory in the setup()
 * method.
 * 
 * The core component is initialized when the test runner processes
 * the @ContextConfiguration annotation which will initialize the core component
 * to the default Pineapple home directory. To enable each test to have
 * individual configurations the core component is reinitialized.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/webapp-config.xml")
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
public class ScheduleControllerIntegrationTest {

	/**
	 * Environment configuration name space constant
	 */
	final static Map<String, String> NS = Collections.singletonMap("ns",
			"http://pineapple.dev.java.net/ns/scheduled_operation_1_0");

	/**
	 * CRON value scheduled for daily execution.
	 */
	static final String CRON_DAILY = "0 0 * * * *";

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
	 * Web application core component factory.
	 */
	@Resource
	WebAppCoreFactory webAppCoreFactory;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Core component.
	 * 
	 * Initialize by web application core component factory.
	 */
	PineappleCore coreComponent;

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
	 * Random Name.
	 */
	String randomName;

	/**
	 * Random description.
	 */
	String randomDescription;

	/**
	 * Random module.
	 */
	String randomModule;

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
		randomOperation = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomResource = RandomStringUtils.randomAlphabetic(10) + "-resource";
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);

		// create module object mother
		moduleMother = new ObjectMotherModule();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// define directory names
		modulesDir = new File(testDirectory, CoreConstants.MODULES_DIR);
		confDir = new File(testDirectory, CoreConstants.CONF_DIR);

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
	 * @throws Exception
	 *             if test fails.
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
	 * Configure core component.
	 * 
	 * Re-create and re-initialize core component to make it initialize itself from
	 * the defined Pineapple home directory system property. See class note above!!
	 * 
	 * Capturing event listener is registered.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	void reinitializeCoreComponent() throws Exception {
		coreComponent = webAppCoreFactory.createCore();

		// TODO: FIX THIS
		coreComponent.getAdministration().getModuleRepository().initialize();
	}

	/**
	 * Test that Get "Get Scheduled Operations" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetOperations_ReturnsHttpOk() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(get(REST_SCHEDULE_GET_OPERATIONS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that GET "Get Scheduled Operations" service invocation returns empty
	 * content after initial core initialization.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetOperations_ReturnsIntiallyEmptyContent() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		assertEquals(0, repository.getOperations().count());

		mockMvc.perform(get(REST_SCHEDULE_GET_OPERATIONS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(xpath("/ns:scheduledOperations", NS).exists());
	}

	/**
	 * Test that GET "Get Scheduled Operations" service invocation returns content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetOperations_ReturnsContent() throws Exception {
		String operation = "/ns:scheduledOperations/ns:scheduledOperation[@name='%s']";

		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		repository.create(randomName, randomModule, randomOperation, randomEnvironment, randomName, CRON_DAILY);
		assertEquals(1, repository.getOperations().count());

		mockMvc.perform(get(REST_SCHEDULE_GET_OPERATIONS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(xpath("/ns:scheduledOperations", NS).exists())
				.andExpect(xpath("/ns:scheduledOperations/ns:scheduledOperation", NS).exists())
				.andExpect(xpath(operation, NS, randomName).exists());
	}

	/**
	 * Test that GET "Get Scheduled Operations" service invocation returns content
	 * type "application/xml".
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetOperations_ReturnsContentTypeApplicationXml() throws Exception {
		mockMvc.perform(get(REST_SCHEDULE_GET_OPERATIONS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	/**
	 * Test that POST "Schedule Operation" service invocation returns HTTP 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostScheduleOperation_ReturnsHttpIsCreated() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		assertEquals(0, repository.getOperations().count());

		mockMvc.perform(post(REST_SCHEDULE_SCHEDULE_URI, randomName, randomModule, randomEnvironment, randomOperation,
				CRON_DAILY, randomDescription).accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

		assertEquals(1, repository.getOperations().count());
	}

	/**
	 * Test that POST "Schedule Operation" service invocation returns HTTP 500 for
	 * illegal scheduling expression.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostScheduleOperation_ReturnsHttpInternalServerError() throws Exception {
		Object randomCron = RandomStringUtils.randomAlphabetic(10);
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(post(REST_SCHEDULE_SCHEDULE_URI, randomName, randomModule, randomEnvironment, randomOperation,
				randomCron, randomDescription).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * Test that POST "Schedule Operation" service invocation returns HTTP 500 for
	 * duplicate name.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostScheduleOperation_ReturnsHttpInternalServerError2() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		assertEquals(0, repository.getOperations().count());
		mockMvc.perform(post(REST_SCHEDULE_SCHEDULE_URI, randomName, randomModule, randomEnvironment, randomOperation,
				CRON_DAILY, randomDescription).accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());
		assertEquals(1, repository.getOperations().count());
		mockMvc.perform(post(REST_SCHEDULE_SCHEDULE_URI, randomName, randomModule, randomEnvironment, randomOperation,
				CRON_DAILY, randomDescription).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * Test that Delete "Delete Scheduled Operation" service invocation returns HTTP
	 * 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteOperation_ReturnsHttpOk() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		repository.create(randomName, randomModule, randomOperation, randomEnvironment, randomName, CRON_DAILY);
		assertEquals(1, repository.getOperations().count());

		mockMvc.perform(delete(REST_SCHEDULE_DELETE_OPERATION_URI, randomName).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

	}

	/**
	 * Test that Delete "Delete Scheduled Operation" service invocation deletes
	 * scheduled operation.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteOperation_DeletesOperation() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		repository.create(randomName, randomModule, randomOperation, randomEnvironment, randomName, CRON_DAILY);
		assertEquals(1, repository.getOperations().count());

		mockMvc.perform(delete(REST_SCHEDULE_DELETE_OPERATION_URI, randomName).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
		assertEquals(0, repository.getOperations().count());
	}

	/**
	 * Test that Delete "Delete Scheduled Operations" service invocation returns
	 * HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteOperations_ReturnsHttpOk() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(delete(REST_SCHEDULE_DELETE_OPERATIONS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that Delete "Delete Scheduled Operation" service invocation returns HTTP
	 * 404 for unknown name.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteOperation_FailsIfNameIsUnknown() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		assertEquals(0, repository.getOperations().count());
		mockMvc.perform(delete(REST_SCHEDULE_DELETE_OPERATION_URI, randomName).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that Delete "Delete Scheduled Operations" service invocation delete
	 * scheduled operation.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteOperations_DeletesOperation() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ScheduledOperationRespository repository = coreComponent.getAdministration().getScheduledOperationRespository();
		repository.create(randomName, randomModule, randomOperation, randomEnvironment, randomName, CRON_DAILY);
		assertEquals(1, repository.getOperations().count());

		mockMvc.perform(delete(REST_SCHEDULE_DELETE_OPERATIONS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
		assertEquals(0, repository.getOperations().count());
	}

}