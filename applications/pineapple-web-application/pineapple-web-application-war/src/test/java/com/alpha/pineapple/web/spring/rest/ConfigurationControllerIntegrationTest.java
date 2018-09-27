/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_CREDENTIAL_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_ENVIRONMENT_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_PROPERTY_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_CREATE_RESOURCE_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_CREDENTIAL_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_ENVIRONMENT_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_PROPERTY_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_DELETE_RESOURCE_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_GET_CREDENTIALS_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_GET_CREDENTIAL_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_GET_RESOURCES_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_REFRESH_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_UPDATE_CREDENTIAL_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_CONFIGURATION_UPDATE_RESOURCE_URI;
import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.text.IsEmptyString;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.credential.CredentialProvider;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.resource.ResourceRepository;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the {@linkplain ConfigurationController} class.
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
public class ConfigurationControllerIntegrationTest {

	/**
	 * Environment configuration name space constant
	 */
	final static Map<String, String> NS = Collections.singletonMap("ns",
			"http://pineapple.dev.java.net/ns/environment_1_0");

	/**
	 * NULL content type constant.
	 */
	final static String NULL_CONTENT_TYPE = null;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Web application core component factory.
	 */
	@Resource
	WebAppCoreFactory webAppCoreFactory;

	/**
	 * Pineapple core component.
	 * 
	 * Initialize by web application core component factory.
	 */
	@Resource
	PineappleCore coreComponent;

	/**
	 * Web application context
	 */
	@Resource
	WebApplicationContext wac;

	/**
	 * Object mother for environment configuration.
	 */
	ObjectMotherEnvironmentConfiguration envConfigMother;

	/**
	 * Mock MVC.
	 */
	MockMvc mockMvc;

	/**
	 * Random environment.
	 */
	String randomEnvironment;

	/**
	 * Random environment.
	 */
	String randomEnvironment2;

	/**
	 * Random description.
	 */
	String randomDescription;

	/**
	 * Random credential.
	 */
	String randomCredentialId;

	/**
	 * Random credential.
	 */
	String randomCredentialId2;

	/**
	 * Random user.
	 */
	String randomUser;

	/**
	 * Random user.
	 */
	String randomUser2;

	/**
	 * Random password.
	 */
	String randomPassword;

	/**
	 * Random password.
	 */
	String randomPassword2;

	/**
	 * Random resource name.
	 */
	String randomResource;

	/**
	 * Random key.
	 */
	String randomKey;

	/**
	 * Random value.
	 */
	String randomValue;

	/**
	 * Modules directory.
	 */
	File modulesDir;

	/**
	 * Conf directory.
	 */
	File confDir;

	@Before
	public void setUp() throws Exception {
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment2 = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);
		randomCredentialId = RandomStringUtils.randomAlphabetic(10);
		randomUser = RandomStringUtils.randomAlphabetic(10);
		randomPassword = RandomStringUtils.randomAlphabetic(10);
		randomCredentialId2 = RandomStringUtils.randomAlphabetic(10);
		randomUser2 = RandomStringUtils.randomAlphabetic(10);
		randomPassword2 = RandomStringUtils.randomAlphabetic(10);
		randomResource = RandomStringUtils.randomAlphabetic(10) + "-resource";
		randomKey = RandomStringUtils.randomAlphabetic(10);
		randomValue = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// define directory names
		modulesDir = new File(testDirectory, "modules");
		confDir = new File(testDirectory, "conf");

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// initialize Spring MVC mock
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();

		createEnvConfigurationAndModule();
		mockMvc.perform(post(REST_CONFIGURATION_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
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

		// create environment configuration and save it
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();
		File resourcesFile = envConfigMother.createResourcesFileName(confDir);
		envConfigMother.jaxbMarshall(configuration, resourcesFile);

		// create credentials configuration and save it
		Configuration credentialsConfiguration = envConfigMother.createEmptyEnvironmentConfiguration();
		File credentialsFile = envConfigMother.createCredentialsFileName(confDir);
		envConfigMother.jaxbMarshall(credentialsConfiguration, credentialsFile);
	}

	/**
	 * Test that POST "Refresh Environment Configuration" service invocation returns
	 * HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostRefreshEnvironmentConfiguration_ReturnsHttpOk() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that POST "Refresh Environment Configuration" service invocation returns
	 * HTTP 501 if refresh fails.
	 * 
	 * To trigger failure the Pineapple home directory is set to garbage.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostRefreshEnvironmentConfiguration_ReturnsHttpServerError() throws Exception {

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, RandomStringUtils.randomNumeric(8));

		mockMvc.perform(post(REST_CONFIGURATION_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * Test that POST "Refresh Environment Configuration" service invocation returns
	 * encoding "utf-8".
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Ignore
	@Test
	public void testPostRefreshEnvironmentConfiguration_ReturnsEncodingUtf8_FIXME() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().encoding("utf-8"));
	}

	/**
	 * Test that POST "Refresh Environment Configuration" service invocation returns
	 * no content
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostRefreshEnvironmentConfiguration_ReturnsNoContent() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that GET "Refresh Environment Configuration" service invocation fails
	 * with HTTP 405.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetRefreshEnvironmentConfiguration_Fails() throws Exception {
		mockMvc.perform(get(REST_CONFIGURATION_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isMethodNotAllowed());
	}

	/**
	 * Test that POST "Create Environment" service invocation returns HTTP 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateEnvironment_ReturnsHttpCreated() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());
	}

	/**
	 * Test that POST "Create Environment" service invocation returns encoding
	 * "utf-8".
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Ignore
	@Test
	public void testPostCreateEnvironment_ReturnsEncodingUtf8_FIXME() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(content().encoding("utf-8"));
	}

	/**
	 * Test that POST "Create Environment" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateEnvironment_ReturnsNoContent() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that POST "Create Environment" service invocation creates environment in
	 * resources repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateEnvironment_EnvironmentIsCreatedInResourceRepository() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

		// get resource repository
		assertNotNull(coreComponent);
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();

		// test
		assertTrue(resourceRepository.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that POST "Create Environment" service invocation creates environment in
	 * credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateEnvironment_EnvironmentIsCreatedInCredentialProvider() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

		// get credential provider
		assertNotNull(coreComponent);
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();

		// test
		assertTrue(provider.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that POST "Create Environment" service invocation returns HTTP 201 if
	 * invoked with the same parameters twice.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateEnvironment_ReturnsHttpCreatedIfInvokedTwice() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());
	}

	/**
	 * Test that POST "Create Environment" service invocation returns HTTP 201 even
	 * if environment already exists in resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateEnvironment_ReturnsHttpCreatedEvenIfEnvironmentAlreadyExists() throws Exception {
		// get resource repository
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();

		// test
		assertFalse(resourceRepository.containsEnvironment(randomEnvironment));

		// create
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		// invoke
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

		// test
		assertTrue(resourceRepository.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that POST "Create Environment" service invocation returns HTTP 201 event
	 * if environment already exists in credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateEnvironment_ReturnsHttpCreatedEvenIfEnvironmentAlreadyExistsInCredentialProvider()
			throws Exception {
		// get credential provider
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();

		// test
		assertFalse(provider.containsEnvironment(randomEnvironment));

		// create
		provider.createEnvironment(randomEnvironment, randomDescription);

		// invoke
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

		// test
		assertTrue(provider.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that PUT "Update Environment" service invocation returns HTTP 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_ReturnsHttpCreated() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		provider.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		// create request body model with identical values.
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment,
				randomDescription);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());
	}

	/**
	 * Test that PUT "Update Environment" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_ReturnsNoContent() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// create request body model with identical values.
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment,
				randomDescription);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration)))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that PUT "Update Environment" service invocation updates the environment
	 * ID in the resource repository and the credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_UpdatesId() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// test
		assertTrue(provider.containsEnvironment(randomEnvironment));
		assertTrue(resourceRepository.containsEnvironment(randomEnvironment));

		// create request body model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment2,
				randomDescription);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertFalse(provider.containsEnvironment(randomEnvironment));
		assertTrue(provider.containsEnvironment(randomEnvironment2));
		assertFalse(resourceRepository.containsEnvironment(randomEnvironment));
		assertTrue(resourceRepository.containsEnvironment(randomEnvironment2));
	}

	/**
	 * Test that PUT "Update Environment" service invocation updates the environment
	 * description in the resource repository and the credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_UpdatesDescription() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// create request body model
		String randomDescription2 = RandomStringUtils.randomAlphabetic(10);
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment,
				randomDescription2);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertEquals(randomDescription2, provider.getEnvironment(randomEnvironment).getDescription());
		assertEquals(randomDescription2, resourceRepository.getEnvironment(randomEnvironment).getDescription());
	}

	/**
	 * Test that PUT "Update Environment" service invocation can updates with
	 * identical value in the resource repository and the credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_UpdatesWithIdenticalValues() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// create request body model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment,
				randomDescription);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertEquals(randomDescription, provider.getEnvironment(randomEnvironment).getDescription());
		assertEquals(randomDescription, resourceRepository.getEnvironment(randomEnvironment).getDescription());
	}

	/**
	 * Test that PUT "Update Environment" service invocation fails if environment is
	 * unknown in the resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_FailsIfEnvironmentIsUnknown() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		// CredentialProvider provider = admin.getCredentialProvider();
		// provider.createEnvironment(randomEnvironment, randomDescription);

		// create request body model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment,
				randomDescription);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update Environment" service invocation fails if environment is
	 * unknown in the credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_FailsIfEnvironmentIsUnknown2() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// create request body model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment,
				randomDescription);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update Environment" service invocation fails with empty
	 * request model.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_FailsWithEmptyRequestModel() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML).content(""))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test that PUT "Update Environment" service invocation fails with no
	 * environment in request model.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateEnvironment_FailsWithNoEnvironmentInRequestModel() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// create request body model
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		// invoke to update environment
		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_ENVIRONMENT_URI, randomEnvironment, randomDescription)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that DELETE "Delete environment" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteEnvironment_ReturnsHttpOk() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(
				delete(REST_CONFIGURATION_DELETE_ENVIRONMENT_URI, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that DELETE "Delete environment" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteEnvironment_ReturnsNoContent() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(
				delete(REST_CONFIGURATION_DELETE_ENVIRONMENT_URI, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that DELETE "Delete environment" service invocation deletes environment
	 * in credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteEnvironment_EnvironmentIsDeletedInCredentialProvder() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// test
		assertTrue(provider.containsEnvironment(randomEnvironment));

		mockMvc.perform(
				delete(REST_CONFIGURATION_DELETE_ENVIRONMENT_URI, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		// test
		assertFalse(provider.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that DELETE "Delete environment" service invocation deletes environment
	 * in resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteEnvironment_EnvironmentIsDeletedInResourceRepository() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// test
		assertTrue(resourceRepository.containsEnvironment(randomEnvironment));

		mockMvc.perform(
				delete(REST_CONFIGURATION_DELETE_ENVIRONMENT_URI, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		// test
		assertFalse(resourceRepository.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that DELETE "Delete environment" service invocation returns HTTP 200 and
	 * deletes environment in resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteEnvironment_SucceedsEvenIfNoEnvironmentIsDefinedInCredentialProvder() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		CredentialProvider provider = admin.getCredentialProvider();

		mockMvc.perform(
				delete(REST_CONFIGURATION_DELETE_ENVIRONMENT_URI, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		// test
		assertFalse(provider.containsEnvironment(randomEnvironment));
		assertFalse(resourceRepository.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that DELETE "Delete environment" service invocation returns HTTP 200 and
	 * deletes environment in credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteEnvironment_SucceedsEvenIfNoEnvironmentIsDefinedInResourceRepository()
			throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(
				delete(REST_CONFIGURATION_DELETE_ENVIRONMENT_URI, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		// test
		assertFalse(provider.containsEnvironment(randomEnvironment));
		assertFalse(resourceRepository.containsEnvironment(randomEnvironment));
	}

	/**
	 * Test that POST "Create Credential" service invocation returns HTTP 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateCredential_ReturnsHttpCreated() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());
	}

	/**
	 * Test that POST "Create Credential" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateCredential_ReturnsNoContent() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that POST "Create Credential" service invocation creates credential in
	 * credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateCredential_CredentialIsCreatedInCredentialProvider() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// test
		assertFalse(provider.contains(randomEnvironment, randomCredentialId));

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

		// test
		assertTrue(provider.contains(randomEnvironment, randomCredentialId));
	}

	/**
	 * Test that POST "Create Credential" service invocation returns HTTP 404 if
	 * target environment isn't defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateCredential_FailsIfEnvironmentDoesntExist() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that POST "Create Credential" service invocation returns HTTP 500 if
	 * credential already exists in credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateCredential_FailsIfCredentialAlreadyExists() throws Exception {
		// get credential provider and create credential
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// invoke
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * Test that GET "Get Credentials" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetCredentials_ReturnsHttpOk() throws Exception {
		mockMvc.perform(get(REST_CONFIGURATION_GET_CREDENTIALS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that GET "Get Credentials" service invocation returns content type
	 * "application/xml".
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetCredentials_ReturnsContentTypeApplicationXml() throws Exception {
		mockMvc.perform(get(REST_CONFIGURATION_GET_CREDENTIALS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	/**
	 * Test that GET "Get Credentials" service invocation returns content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetCredentials_ReturnsContent() throws Exception {
		String environment = "/ns:configuration/ns:environments/ns:environment[@id='%s']";
		String environmentAndCredentials = "/ns:configuration/ns:environments/ns:environment[@id='%s']/ns:credentials";
		String environmentAndCredential = "/ns:configuration/ns:environments/ns:environment[@id='%s']/ns:credentials/ns:credential[@id='%s']";

		// get credential provider and create credential
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(get(REST_CONFIGURATION_GET_CREDENTIALS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(xpath("/ns:configuration", NS).exists())
				.andExpect(xpath("/ns:configuration/ns:environments", NS).exists())
				.andExpect(xpath(environment, NS, randomEnvironment).exists())
				.andExpect(xpath(environmentAndCredentials, NS, randomEnvironment).exists())
				.andExpect(xpath(environmentAndCredential, NS, randomEnvironment, randomCredentialId).exists());
	}

	/**
	 * Test that DELETE "Delete Credential" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteCredential_ReturnsHttpOk() throws Exception {
		// create environment and credential
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

	/**
	 * Test that DELETE "Delete Credential" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteCredential_ReturnsNoContent() throws Exception {
		// create environment and credential
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that DELETE "Delete Credential" service invocation deletes credential in
	 * credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteCredential_CredentialIsDeletedInCredentialProvider() throws Exception {
		// create environment and credential
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// test
		assertTrue(provider.contains(randomEnvironment, randomCredentialId));

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());

		// test
		assertFalse(provider.contains(randomEnvironment, randomCredentialId));
	}

	/**
	 * Test that DELETE "Delete Credential" service invocation returns HTTP 404 if
	 * credential doesn't exists in credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteCredential_FailsIfCredentialIsntDefined() throws Exception {
		// get credential provider and create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// invoke
		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that DELETE "Delete Credential" service invocation returns HTTP 404 if
	 * environment doesn't exists in credential provider.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteCredential_FailsIfEnvironmentIsntDefined() throws Exception {
		// invoke
		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_CREDENTIAL_URI, randomEnvironment, randomCredentialId,
				randomUser, randomPassword).accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update Credential" service invocation returns HTTP 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_ReturnsHttpCreated() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request body model with identical values.
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvironment,
				randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());
	}

	/**
	 * Test that PUT "Update Credential" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_ReturnsNoContent() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request body model with identical values.
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvironment,
				randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration)))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that PUT "Update Credential" service invocation updates id.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_UpdatesId() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvironment,
				randomCredentialId2, randomUser, randomPassword);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertFalse(provider.contains(randomEnvironment, randomCredentialId));
		assertTrue(provider.contains(randomEnvironment, randomCredentialId2));
		assertEquals(randomUser, provider.get(randomEnvironment, randomCredentialId2).getUser());
		assertEquals(randomPassword, provider.get(randomEnvironment, randomCredentialId2).getPassword());
	}

	/**
	 * Test that PUT "Update Credential" service invocation updates id, user and
	 * password.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_UpdatesIdUserAndPassword() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvironment,
				randomCredentialId2, randomUser2, randomPassword2);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertFalse(provider.contains(randomEnvironment, randomCredentialId));
		assertTrue(provider.contains(randomEnvironment, randomCredentialId2));
		assertEquals(randomUser2, provider.get(randomEnvironment, randomCredentialId2).getUser());
		assertEquals(randomPassword2, provider.get(randomEnvironment, randomCredentialId2).getPassword());
	}

	/**
	 * Test that PUT "Update Credential" service invocation updates with identical
	 * values.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_UpdatesWithIdenticalValues() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvironment,
				randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertTrue(provider.contains(randomEnvironment, randomCredentialId));
		assertEquals(randomUser, provider.get(randomEnvironment, randomCredentialId).getUser());
		assertEquals(randomPassword, provider.get(randomEnvironment, randomCredentialId).getPassword());
	}

	/**
	 * Test that PUT "Update Credential" service invocation doesn't update
	 * environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_EnvironmentIsntUpdated() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.createEnvironment(randomEnvironment2, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvironment2,
				randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertFalse(provider.contains(randomEnvironment2, randomCredentialId));
		assertTrue(provider.contains(randomEnvironment, randomCredentialId));
		assertEquals(randomUser, provider.get(randomEnvironment, randomCredentialId).getUser());
		assertEquals(randomPassword, provider.get(randomEnvironment, randomCredentialId).getPassword());
	}

	/**
	 * Test that PUT "Update Credential" service invocation fails with empty request
	 * model.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_FailsWithEmptyRequestModel() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML).content(""))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test that PUT "Update Credential" service invocation fails with request model
	 * - model is defined but no environments.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_FailsWithRequestModelWithNoEnvironments() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request model
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update Credential" service invocation fails with request model
	 * - model is defined but one environment and no credentials.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_FailsWithRequestModelWithCredential() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleEnvironment(randomEnvironment);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update Credential" service invocation returns HTTP 404 if
	 * target environment isn't defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_FailsIfEnvironmentDoesntExist() throws Exception {
		Configuration configuration = envConfigMother.createEmptyEnvironmentConfiguration();

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update Credential" service invocation returns HTTP 404 if
	 * credential isn't defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateCredential_FailsIfCredentialDoesntExist() throws Exception {

		// get credential provider and create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		// create request body model with identical values.
		Configuration configuration = envConfigMother.createEnvConfigWithSingleCredential(randomEnvironment,
				randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that GET "Get Credential" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetCredential_ReturnsHttpOk() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);
		provider.create(randomEnvironment, randomCredentialId, randomUser, randomPassword);

		mockMvc.perform(get(REST_CONFIGURATION_GET_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

	/**
	 * Test that GET "Get Credential" service invocation returns HTTP 404. if target
	 * environment isn't defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetCredential_FailsIfEnvironmentDoesntExist() throws Exception {
		mockMvc.perform(get(REST_CONFIGURATION_GET_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that GET "Get Credential" service invocation returns HTTP 404. if
	 * credential isn't defined.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetCredential_FailsIfCredentialDoesntExist() throws Exception {
		// create environment
		Administration admin = coreComponent.getAdministration();
		CredentialProvider provider = admin.getCredentialProvider();
		provider.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(get(REST_CONFIGURATION_GET_CREDENTIAL_URI, randomEnvironment, randomCredentialId)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that GET "Get Resources" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetResources_ReturnsHttpOk() throws Exception {
		mockMvc.perform(get(REST_CONFIGURATION_GET_RESOURCES_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that GET "Get Resources" service invocation returns content type
	 * "application/xml".
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetResources_ReturnsContentTypeApplicationXml() throws Exception {
		mockMvc.perform(get(REST_CONFIGURATION_GET_RESOURCES_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	/**
	 * Test that GET "Get Resources" service invocation returns content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetResources_ReturnsContent() throws Exception {
		String environment = "/ns:configuration/ns:environments/ns:environment[@id='%s']";
		String environmentAndResources = "/ns:configuration/ns:environments/ns:environment[@id='%s']/ns:resources";
		String environmentAndResource = "/ns:configuration/ns:environments/ns:environment[@id='%s']/ns:resources/ns:resource[@id='%s']";

		// get resource repository and create resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, "pluginid", "credentialidref");

		mockMvc.perform(get(REST_CONFIGURATION_GET_RESOURCES_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(xpath("/ns:configuration", NS).exists())
				.andExpect(xpath("/ns:configuration/ns:environments", NS).exists())
				.andExpect(xpath(environment, NS, randomEnvironment).exists())
				.andExpect(xpath(environmentAndResources, NS, randomEnvironment).exists())
				.andExpect(xpath(environmentAndResource, NS, randomEnvironment, randomResource).exists());
	}

	/**
	 * Test that POST "Create Resource" service invocation returns HTTP 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResource_ReturnsHttpCreated() throws Exception {
		// get resource repository and create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_RESOURCE_URI, randomEnvironment, randomResource,
				pluginIdHelloWorld, randomCredentialId).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isCreated());
	}

	/**
	 * Test that POST "Create Resource" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResource_ReturnsNoContent() throws Exception {
		// get resource repository and create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_RESOURCE_URI, randomEnvironment, randomResource,
				pluginIdHelloWorld, randomCredentialId).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that POST "Create Resource" service invocation creates resource in
	 * resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResource_ResourceIsCreatedInRepository() throws Exception {
		// get resource repository and create environment
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_RESOURCE_URI, randomEnvironment, randomResource,
				pluginIdHelloWorld, randomCredentialId).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isCreated());

		// test
		assertTrue(resourceRepository.contains(randomEnvironment, randomResource));
	}

	/**
	 * Test that POST "Create Resource" service invocation returns HTTP 500 if
	 * resource already exists in repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResource_FailsIfResourceAlreadyExists() throws Exception {
		// get resource repository and create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(post(REST_CONFIGURATION_CREATE_RESOURCE_URI, randomEnvironment, randomResource,
				pluginIdHelloWorld, randomCredentialId).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * Test that POST "Create Resource" service invocation returns HTTP 404 if
	 * environment doesn't exist.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResource_FailsIfEnvironmentDoesntExists() throws Exception {
		mockMvc.perform(post(REST_CONFIGURATION_CREATE_RESOURCE_URI, randomEnvironment, randomResource,
				pluginIdHelloWorld, randomCredentialId).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update resource" service invocation returns HTTP 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateResource_ReturnsHttpCreated() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());
	}

	/**
	 * Test that PUT "Update resource" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateResource_ReturnsNoContent() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration)))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that PUT "Update resource" service invocation environment in the request
	 * body is ignored. The resource isn't moved to another environment.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateResource_EnvironmentIsntUpdated() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment2,
				randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertFalse(resourceRepository.contains(randomEnvironment2, randomResource));
		assertTrue(resourceRepository.contains(randomEnvironment, randomResource));
	}

	/**
	 * Test that PUT "Update resource" service invocation updates resource ID,
	 * plugin Id and Credential ID referencein repository .
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateResource_UpdatesValues() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		// create request model
		String randomResource2 = RandomStringUtils.randomAlphabetic(20);
		String randomPluginId = RandomStringUtils.randomAlphabetic(20);
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource2, randomPluginId, randomCredentialId2);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertFalse(resourceRepository.contains(randomEnvironment, randomResource));
		assertTrue(resourceRepository.contains(randomEnvironment, randomResource2));
		assertEquals(randomPluginId, resourceRepository.get(randomEnvironment, randomResource2).getPluginId());
		assertEquals(randomCredentialId2,
				resourceRepository.get(randomEnvironment, randomResource2).getCredentialIdRef());
	}

	/**
	 * Test that PUT "Update resource" service invocation updates resource with
	 * identical values.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateResource_UpdatesWithIdenticalValues() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isCreated());

		// test
		assertTrue(resourceRepository.contains(randomEnvironment, randomResource));
		assertEquals(pluginIdHelloWorld, resourceRepository.get(randomEnvironment, randomResource).getPluginId());
		assertEquals(randomCredentialId,
				resourceRepository.get(randomEnvironment, randomResource).getCredentialIdRef());
	}

	/**
	 * Test that PUT "Update resource" service invocation fails if resource doesn't
	 * exist.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateResource_FailsIfResouceDoesntExist() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that PUT "Update resource" service invocation fails if environment
	 * doesn't exist.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPutUpdateResource_FailsIfEnvironmentDoesntExist() throws Exception {
		// create request model
		Configuration configuration = envConfigMother.createEnvConfigWithSingleResource(randomEnvironment,
				randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(put(REST_CONFIGURATION_UPDATE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
				.content(envConfigMother.jaxbMarshallToString(configuration))).andExpect(status().isNotFound());
	}

	/**
	 * Test that DELETE "Delete resource" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteResource_ReturnsHttpOk() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

	/**
	 * Test that DELETE "Delete Resource" service invocation returns no content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteResource_ReturnsNoContent() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML)).andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that DELETE "Delete Resource" service invocation deletes resource in
	 * repository .
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteResource_ResourcelIsDeletedInRepository() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		// test
		assertTrue(resourceRepository.contains(randomEnvironment, randomResource));

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());

		// test
		assertFalse(resourceRepository.contains(randomEnvironment, randomCredentialId));
	}

	/**
	 * Test that DELETE "Delete Resource" service invocation returns HTTP 404 if
	 * resource doesn't exists in resource repository..
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteResource_FailsIfResourceIsntDefined() throws Exception {
		// get resource repository, create environment and resource
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		// invoke
		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that DELETE "Delete Resource" service invocation returns HTTP 404 if
	 * environment doesn't exists in resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteResource_FailsIfEnvironmentIsntDefined() throws Exception {
		// invoke
		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_RESOURCE_URI, randomEnvironment, randomResource)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that POST "Create Resource Property" service invocation returns HTTP
	 * 201.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResourceProperty_ReturnsHttpCreated() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(
				post(REST_CONFIGURATION_CREATE_PROPERTY_URI, randomEnvironment, randomResource, randomKey, randomValue)
						.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isCreated());
	}

	/**
	 * Test that POST "Create Resource Property" service invocation returns no
	 * content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResourceProperty_ReturnsNoContent() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(
				post(REST_CONFIGURATION_CREATE_PROPERTY_URI, randomEnvironment, randomResource, randomKey, randomValue)
						.accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that POST "Create Resource Property" service invocation creates resource
	 * property in resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResourceProperty_PropertyIsCreatedInRepository() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(
				post(REST_CONFIGURATION_CREATE_PROPERTY_URI, randomEnvironment, randomResource, randomKey, randomValue)
						.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isCreated());

		// test
		assertTrue(resourceRepository.contains(randomEnvironment, randomResource));
		assertTrue(resourceRepository.get(randomEnvironment, randomResource).containsProperty(randomKey));
		assertEquals(randomValue,
				resourceRepository.get(randomEnvironment, randomResource).getProperty(randomKey).getValue());

	}

	/**
	 * Test that POST "Create Resource Property" service invocation returns HTTP 500
	 * if resource property already exists in repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResourceProperty_FailsIfPropertyAlreadyExists() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);
		resourceRepository.createResourceProperty(randomEnvironment, randomResource, randomKey, randomValue);

		mockMvc.perform(
				post(REST_CONFIGURATION_CREATE_PROPERTY_URI, randomEnvironment, randomResource, randomKey, randomValue)
						.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * Test that POST "Create Resource Property" service invocation returns HTTP 404
	 * if environment doesn't exist.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResourceProperty_FailsIfEnvironmentDoesntExists() throws Exception {
		mockMvc.perform(
				post(REST_CONFIGURATION_CREATE_PROPERTY_URI, randomEnvironment, randomResource, randomKey, randomValue)
						.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that POST "Create Resource Property" service invocation returns HTTP 404
	 * if resource doesn't exist.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostCreateResourceProperty_FailsIfResourceDoesntExists() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(
				post(REST_CONFIGURATION_CREATE_PROPERTY_URI, randomEnvironment, randomResource, randomKey, randomValue)
						.accept(MediaType.APPLICATION_XML))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that POST "Delete Resource Property" service invocation returns HTTP
	 * 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostDeleteResourceProperty_ReturnsHttpOk() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);
		resourceRepository.createResourceProperty(randomEnvironment, randomResource, randomKey, randomValue);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_PROPERTY_URI, randomEnvironment, randomResource, randomKey)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

	/**
	 * Test that POST "Delete Resource Property" service invocation returns no
	 * content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostDeleteResourceProperty_ReturnsNoContent() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);
		resourceRepository.createResourceProperty(randomEnvironment, randomResource, randomKey, randomValue);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_PROPERTY_URI, randomEnvironment, randomResource, randomKey)
				.accept(MediaType.APPLICATION_XML)).andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that POST "Delete Resource Property" service invocation delete property
	 * in resource repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostDeleteResourceProperty_PropertyIsDeleteInRepository() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);
		resourceRepository.createResourceProperty(randomEnvironment, randomResource, randomKey, randomValue);

		// test
		assertTrue(resourceRepository.get(randomEnvironment, randomResource).containsProperty(randomKey));

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_PROPERTY_URI, randomEnvironment, randomResource, randomKey)
				.accept(MediaType.APPLICATION_XML)).andExpect(content().string(IsEmptyString.isEmptyString()));

		// test
		assertFalse(resourceRepository.get(randomEnvironment, randomResource).containsProperty(randomKey));
	}

	/**
	 * Test that POST "Delete Resource Property" service invocation returns HTTP 404
	 * if resource property doesn't exists in repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostDeleteResourceProperty_FailsIfPropertyDoesntExists() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);
		resourceRepository.create(randomEnvironment, randomResource, pluginIdHelloWorld, randomCredentialId);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_PROPERTY_URI, randomEnvironment, randomResource, randomKey)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that POST "Delete Resource Property" service invocation returns HTTP 404
	 * if resource doesn't exists in repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostDeleteResourceProperty_FailsIfResourceDoesntExists() throws Exception {
		Administration admin = coreComponent.getAdministration();
		ResourceRepository resourceRepository = admin.getResourceRepository();
		resourceRepository.createEnvironment(randomEnvironment, randomDescription);

		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_PROPERTY_URI, randomEnvironment, randomResource, randomKey)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that POST "Delete Resource Property" service invocation returns HTTP 404
	 * if environment doesn't exists in repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testPostDeleteResourceProperty_FailsIfEnvironmentDoesntExists() throws Exception {
		mockMvc.perform(delete(REST_CONFIGURATION_DELETE_PROPERTY_URI, randomEnvironment, randomResource, randomKey)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

}