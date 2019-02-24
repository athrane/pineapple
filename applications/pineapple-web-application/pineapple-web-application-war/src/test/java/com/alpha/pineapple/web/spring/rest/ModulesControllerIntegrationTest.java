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

import static com.alpha.pineapple.ApiConstants.ZIP_FILE_POSTFIX;
import static com.alpha.pineapple.web.WebApplicationConstants.DISTRIBUTE_MODULE_FILE_PART;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_CREATE_MODEL_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_DELETE_MODEL_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_DELETE_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_GET_MODULES_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_REFRESH_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_MODULE_UPLOAD_URI;
import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.text.IsEmptyString;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alpha.javautils.SystemUtils;
import com.alpha.javautils.ZipUtils;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.module.ModuleInfo;
import com.alpha.pineapple.module.ModuleRepository;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the {@linkplain ModulesController} class.
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
public class ModulesControllerIntegrationTest {

	/**
	 * Environment configuration name space constant
	 */
	final static Map<String, String> NS = Collections.singletonMap("ns",
			"http://pineapple.dev.java.net/ns/module_info_1_0");

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
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

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
	 * Random environment.
	 */
	String randomEnvironment2;

	/**
	 * Random resource name.
	 */
	String randomResource;

	@Before
	public void setUp() throws Exception {
		randomModule = RandomStringUtils.randomAlphabetic(10);
		randomOperation = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment2 = RandomStringUtils.randomAlphabetic(10);
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

		// create pineapple runtime sub directories, e.g conf and modules
		confDir.mkdirs();
		modulesDir.mkdirs();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// initialize Spring MVC mock
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();
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
	 * @throws Exception if test fails.
	 */
	void reinitializeCoreComponent() throws Exception {
		coreComponent = webAppCoreFactory.createCore();

		// TODO: FIX THIS
		coreComponent.getAdministration().getModuleRepository().initialize();
	}

	@After
	public void tearDown() throws Exception {

		// clear the pineapple.home.dir system setting
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);
	}

	/**
	 * Test that POST "Refresh Modules" service invocation returns HTTP 200.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostRefreshModules_ReturnsHttpOk() throws Exception {
		mockMvc.perform(post(REST_MODULE_REFRESH_URI).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

	/**
	 * Test that POST "Refresh Modules" service invocation returns encoding "utf-8".
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	@Ignore
	public void testPostRefreshModules_ReturnsEncodingUtf8_FIXME() throws Exception {
		mockMvc.perform(post(REST_MODULE_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().encoding("utf-8"));
	}

	/**
	 * Test that POST "Refresh Modules" service invocation returns no content
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostRefreshModules_ReturnsNoContent() throws Exception {
		mockMvc.perform(post(REST_MODULE_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that GET "Refresh Modules" service invocation fails with HTTP 405.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testGetRefreshModules_Fails() throws Exception {
		mockMvc.perform(get(REST_MODULE_REFRESH_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isMethodNotAllowed());
	}

	/**
	 * Test that POST "Delete Module" service invocation fails with HTTP 405.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostDeleteModule_Fails() throws Exception {
		mockMvc.perform(post(REST_MODULE_DELETE_URI, randomModule).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isMethodNotAllowed());
	}

	/**
	 * Test that GET "Delete Module" service invocation fails with HTTP 405.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testGetDeleteModule_Fails() throws Exception {
		mockMvc.perform(get(REST_MODULE_DELETE_URI, randomModule).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isMethodNotAllowed());
	}

	/**
	 * Test that DELETE "Delete Module" service invocation fails with HTTP 404 for
	 * unknown module.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testDeleteDeleteModule_FailsWithUnknownModule() throws Exception {
		mockMvc.perform(delete(REST_MODULE_DELETE_URI, randomModule).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isNotFound());
	}

	/**
	 * Test that DELETE "Delete Module" service invocation can delete module.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testDeleteDeleteModule_ModuleIsDeletedFromModulesRepository() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		// test
		ModuleRepository modulesRepository = coreComponent.getAdministration().getModuleRepository();
		assertTrue(modulesRepository.contains(randomModule));

		mockMvc.perform(delete(REST_MODULE_DELETE_URI, randomModule).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		// test
		assertFalse(modulesRepository.contains(randomModule));
	}

	/**
	 * Test that DELETE "Delete Model" service invocation returns HTTP 200.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testDeleteDeleteModel_ReturnsHttpOk() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(
				delete(REST_MODULE_DELETE_MODEL_URI, randomModule, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that DELETE "Delete Model" service invocation returns no content.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testDeleteDeleteModel_ReturnsNoContent() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(
				delete(REST_MODULE_DELETE_MODEL_URI, randomModule, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that DELETE "Delete Model" service invocation result in the deletion of
	 * the model from the model.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testDeleteDeleteModel_ModelIsDeletedFromModulesRepository() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		// test
		ModuleRepository modulesRepository = coreComponent.getAdministration().getModuleRepository();
		ModuleInfo info = modulesRepository.get(randomModule);
		assertTrue(info.containsModel(randomEnvironment));

		mockMvc.perform(
				delete(REST_MODULE_DELETE_MODEL_URI, randomModule, randomEnvironment).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		// test
		info = modulesRepository.get(randomModule);
		assertFalse(info.containsModel(randomEnvironment));
	}

	/**
	 * Test that DELETE "Delete Model" service invocation returns HTTP 404 if module
	 * doesn't exists.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testDeleteDeleteModel_FailsIfModuleIsntDefined() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(delete(REST_MODULE_DELETE_MODEL_URI, "nonexisting-module", randomEnvironment)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that DELETE "Delete Model" service invocation returns HTTP 404 if model
	 * doesn't exists.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testDeleteDeleteModel_FailsIfModelIsntDefined() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(delete(REST_MODULE_DELETE_MODEL_URI, randomModule, "nonexisting-model")
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that POST "Create Model" service invocation returns HTTP 200.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostCreateModel_ReturnsHttpOk() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(
				post(REST_MODULE_CREATE_MODEL_URI, randomModule, randomEnvironment2).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that POST "Create Model" service invocation returns no content.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostCreateModel_ReturnsNoContent() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(
				post(REST_MODULE_CREATE_MODEL_URI, randomModule, randomEnvironment2).accept(MediaType.APPLICATION_XML))
				.andExpect(content().string(IsEmptyString.isEmptyString()));
	}

	/**
	 * Test that POST "Create Model" service invocation creates model in modules
	 * repository.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostCreateModel_ModelIsCreatedInModulesRepository() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		// test
		ModuleRepository modulesRepository = coreComponent.getAdministration().getModuleRepository();
		ModuleInfo info = modulesRepository.get(randomModule);
		assertFalse(info.containsModel(randomEnvironment2));

		mockMvc.perform(
				post(REST_MODULE_CREATE_MODEL_URI, randomModule, randomEnvironment2).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		// test
		info = modulesRepository.get(randomModule);
		assertTrue(info.containsModel(randomEnvironment2));
	}

	/**
	 * Test that POST "Create Model" service invocation returns HTTP 404 if module
	 * doesn't exists.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostCreateModel_FailsIfModuleIsntDefined() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(post(REST_MODULE_CREATE_MODEL_URI, "nonexisting-module", randomEnvironment2)
				.accept(MediaType.APPLICATION_XML)).andExpect(status().isNotFound());
	}

	/**
	 * Test that Get "Get Modules" service invocation returns HTTP 200.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testGetGetModules_ReturnsHttpOk() throws Exception {
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();

		mockMvc.perform(get(REST_MODULE_GET_MODULES_URI).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

	/**
	 * Test that GET "Get Modules" service invocation returns content.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testGetGetModules_ReturnsContent() throws Exception {
		String module = "/ns:modules/ns:module[@id='%s']";
		createEnvConfigurationAndModule();
		reinitializeCoreComponent();
		ModuleRepository modulesRepository = coreComponent.getAdministration().getModuleRepository();
		assertTrue(modulesRepository.contains(randomModule));

		mockMvc.perform(get(REST_MODULE_GET_MODULES_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(xpath("/ns:modules", NS).exists()).andExpect(xpath("/ns:modules/ns:module", NS).exists())
				.andExpect(xpath(module, NS, randomModule).exists());
	}

	/**
	 * Test that GET "Get Modules" service invocation returns content type
	 * "application/xml".
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testGetGetCredentials_ReturnsContentTypeApplicationXml() throws Exception {
		mockMvc.perform(get(REST_MODULE_GET_MODULES_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	/**
	 * Test that POST "Upload module" service invocation returns HTTP 400 if
	 * multi-part request isn't defined..
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostUploadModule_FailsIfMultipartRequestIsntDefined() throws Exception {
		reinitializeCoreComponent();

		mockMvc.perform(
				post(REST_MODULE_UPLOAD_URI, randomModule, randomEnvironment2).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test that POST "Upload module" service invocation returns HTTP 400 if
	 * multi-part file isn't defined.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostUploadModule_FailsIfMultipartFileIsntDefined() throws Exception {
		reinitializeCoreComponent();

		mockMvc.perform(
				post(REST_MODULE_UPLOAD_URI, randomModule, randomEnvironment2).accept(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isBadRequest());
	}

	/**
	 * Test that POST "Upload module" service invocation returns HTTP 200 if
	 * multi-part file is defined an upload succeeds.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostUploadModule_ReturnsHttpOk() throws Exception {
		reinitializeCoreComponent();

		// define module directory
		File moduleStageDir = new File(testDirectory, randomModule);

		// create module
		moduleMother.createModuleWithSingleModel(testDirectory, randomModule, randomEnvironment, randomResource);

		// zip module directory
		String archiveFileName = new StringBuilder().append(randomModule).append(ZIP_FILE_POSTFIX).toString();
		File archiveFile = new File(moduleStageDir, archiveFileName);
		ZipUtils.zipFolder(moduleStageDir, archiveFile);

		// create multi-part file
		FileInputStream input = new FileInputStream(archiveFile);
		MockMultipartFile multipartFile = new MockMultipartFile(DISTRIBUTE_MODULE_FILE_PART, archiveFileName,
				MediaType.MULTIPART_FORM_DATA.toString(), input);

		// upload module
		mockMvc.perform(MockMvcRequestBuilders.fileUpload(REST_MODULE_UPLOAD_URI).file(multipartFile))
				.andExpect(status().isOk());

		// close stream
		input.close();

	}

	/**
	 * Test that POST "Upload module" adds and registers module.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostUploadModule_ModuleIsAddedToRepository() throws Exception {
		reinitializeCoreComponent();

		// define module directory
		File moduleStageDir = new File(testDirectory, randomModule);

		// create module
		moduleMother.createModuleWithSingleModel(testDirectory, randomModule, randomEnvironment, randomResource);

		// zip module directory
		String archiveFileName = new StringBuilder().append(randomModule).append(ZIP_FILE_POSTFIX).toString();
		File archiveFile = new File(moduleStageDir, archiveFileName);
		ZipUtils.zipFolder(moduleStageDir, archiveFile);

		// create multi-part file
		FileInputStream input = new FileInputStream(archiveFile);
		MockMultipartFile multipartFile = new MockMultipartFile(DISTRIBUTE_MODULE_FILE_PART, archiveFileName,
				MediaType.MULTIPART_FORM_DATA.toString(), input);

		// test
		ModuleRepository modulesRepository = coreComponent.getAdministration().getModuleRepository();
		assertFalse(modulesRepository.contains(randomModule));

		// upload module
		mockMvc.perform(MockMvcRequestBuilders.fileUpload(REST_MODULE_UPLOAD_URI).file(multipartFile))
				.andExpect(status().isOk());

		// test
		modulesRepository = coreComponent.getAdministration().getModuleRepository();
		assertTrue(modulesRepository.contains(randomModule));
		ModuleInfo info = modulesRepository.get(randomModule);
		assertTrue(info.containsModel(randomEnvironment));

		// close stream
		input.close();
	}

	/**
	 * Test that POST "Upload module" service invocation returns HTTP 500 if
	 * multi-part file isn't a valid ZIP archive.
	 * 
	 * @throws Exception if test fails.
	 */
	@Test
	public void testPostUploadModule_FailsIfMultipartFileIsntValidZipArchive() throws Exception {
		reinitializeCoreComponent();

		// create multi-part file
		String archiveFileName = new StringBuilder().append(randomModule).append(ZIP_FILE_POSTFIX).toString();
		MockMultipartFile multipartFile = new MockMultipartFile(DISTRIBUTE_MODULE_FILE_PART, archiveFileName,
				MediaType.MULTIPART_FORM_DATA.toString(), "...some invalid input".getBytes());

		// upload module
		mockMvc.perform(MockMvcRequestBuilders.fileUpload(REST_MODULE_UPLOAD_URI).file(multipartFile))
				.andExpect(status().isInternalServerError());
	}

}