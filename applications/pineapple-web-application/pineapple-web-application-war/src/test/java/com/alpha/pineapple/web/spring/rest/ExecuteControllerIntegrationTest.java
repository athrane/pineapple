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

import static com.alpha.pineapple.web.WebApplicationConstants.LOCATION_HEADER;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_EXECUTE_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_STATUS_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_EXECUTE_URI;
import static com.alpha.testutils.TestUtilsTestConstants.helloWorldOperation;
import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.hamcrest.core.StringContains;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of class {@linkplain ExecuteController}.
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
public class ExecuteControllerIntegrationTest {

    /**
     * First list index.
     */
    final static int FIRST_INDEX = 0;

    /**
     * Event capturing result listener.
     */
    class CapturingResultListener implements ResultListener {

	/**
	 * Captured result notfications.
	 */
	List<ExecutionResultNotification> notifications;

	/**
	 * Captured root results.
	 */
	List<ExecutionResult> rootResults;

	/**
	 * CapturingResultListener constructor.
	 */
	public CapturingResultListener() {
	    super();
	    notifications = new CopyOnWriteArrayList<ExecutionResultNotification>();
	}

	@Override
	public void notify(ExecutionResultNotification notification) {
	    notifications.add(notification);
	}

    }

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
     * Random execution ID.
     */
    String randomExecutionID;

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

    /**
     * Capturing result listener.
     */
    CapturingResultListener capturingListener;

    @Before
    public void setUp() throws Exception {
	randomExecutionID = RandomStringUtils.randomAlphabetic(10);
	randomModule = RandomStringUtils.randomAlphabetic(10);
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

	// create listener
	capturingListener = new CapturingResultListener();

	// set the pineapple.home.dir system property
	System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

	// initialize Spring MVC mock
	mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();

    }

    @After
    public void tearDown() throws Exception {

	// clear the pineapple.home.dir system setting
	System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

	// remove result listener
	if (coreComponent == null)
	    return;
	coreComponent.removeListener(capturingListener);
    }

    /**
     * Create environment configuration and module.
     * 
     * The environment configuration contains a random environment with resource
     * for hello world plugin.
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
     * Re-create and re-initialize core component to make it initialize itself
     * from the defined Pineapple home directory system property. See class note
     * above!!
     * 
     * Capturing event listener is registered.
     * 
     * @throws Exception
     *             if test fails.
     */
    void reinitializeCoreComponent() throws Exception {
	coreComponent = webAppCoreFactory.createCore();
	coreComponent.addListener(capturingListener);

	// TODO: FIX THIS
	coreComponent.getAdministration().getModuleRepository().initialize();
    }

    /**
     * Wait until execution is completed.
     * 
     * @throws Exception
     *             if test fails.
     */
    void waitForOperationToComplete() throws Exception {

	// wait for first result
	while (capturingListener.notifications.isEmpty()) {
	    ConcurrencyUtils.waitSomeMilliseconds(10);
	}

	// get first result
	ExecutionResultNotification notification = capturingListener.notifications.get(FIRST_INDEX);

	// wait until completed
	while (notification.getResult().isExecuting()) {
	}
    }

    /**
     * Test that POST "Execute" service invocation returns HTTP 201.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostExecute_ReturnsHttpCreated() throws Exception {
	createEnvConfigurationAndModule();
	reinitializeCoreComponent();

	// execute operation asynchronously
	mockMvc.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
		.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated());

	// wait to avoid untimely test tear down
	waitForOperationToComplete();
    }

    /**
     * Test that POST "Execute" service invocation returns location header.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostExecute_ReturnsLocationHeader() throws Exception {
	createEnvConfigurationAndModule();
	reinitializeCoreComponent();

	// execute operation
	mockMvc.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
		.accept(MediaType.APPLICATION_XML)).andExpect(status().isCreated())
		.andExpect(header().string(LOCATION_HEADER, StringContains.containsString(REST_EXECUTE_URI)));

	// wait to avoid untimely test tear down
	waitForOperationToComplete();
    }

    /**
     * Test that POST "Execute" service invocation returns content type
     * "application/xml".
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostExecute_ReturnsContentTypeApplicationXml() throws Exception {
	createEnvConfigurationAndModule();

	// execute operation
	mockMvc.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
		.accept(MediaType.APPLICATION_XML)).andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    /**
     * Test that POST "Execute" service invocation returns encoding "utf-8".
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostExecute_ReturnsEncodingUtf8_FIXME() throws Exception {
	createEnvConfigurationAndModule();

	// execute operation
	mockMvc.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
		.accept(MediaType.APPLICATION_XML)).andExpect(content().encoding("utf-8"));

	// wait to avoid untimely test tear down
	waitForOperationToComplete();
    }

    /**
     * Test that POST "Execute" service invocation returns HTTP 404 for
     * execution of an unknown module.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostExecute_ReturnsNotFoundForUnknownModule() throws Exception {
	mockMvc.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, randomOperation, randomEnvironment)
		.accept(MediaType.TEXT_PLAIN)).andExpect(status().isNotFound());
    }

    /**
     * Test that POST "Execute" service invocation returns error message for
     * execution of an unknown module which contains the name of the unknown
     * module.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostExecute_ReturnsErrorMessageForUnknownModule() throws Exception {
	mockMvc.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, randomOperation, randomEnvironment)
		.accept(MediaType.TEXT_PLAIN)).andExpect(content().string(StringContains.containsString(randomModule)));
    }

    /**
     * Test that GET "Get Operation Status" service invocation returns HTTP 200.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetOperationStatus_ReturnsHttpOk() throws Exception {
	createEnvConfigurationAndModule();
	reinitializeCoreComponent();

	// execute operation
	MvcResult mvcResult = mockMvc
		.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
			.accept(MediaType.APPLICATION_XML))
		.andExpect(status().isCreated())
		.andExpect(header().string(LOCATION_HEADER, StringContains.containsString(REST_EXECUTE_URI)))
		.andReturn();

	// wait to avoid untimely test tear down
	waitForOperationToComplete();

	// get execution ID
	String locationHeader = mvcResult.getResponse().getHeader(LOCATION_HEADER);
	String executionID = locationHeader.substring(locationHeader.lastIndexOf("/"));

	// get status
	mockMvc.perform(get(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());
    }

    /**
     * Test that GET "Get Operation Status" service invocation returns HTTP 200
     * despite multiple invocations.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetOperationStatus_ReturnsHttpOkWithMultipleIncocations() throws Exception {
	createEnvConfigurationAndModule();
	reinitializeCoreComponent();

	// execute operation
	MvcResult mvcResult = mockMvc
		.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
			.accept(MediaType.APPLICATION_XML))
		.andExpect(status().isCreated())
		.andExpect(header().string(LOCATION_HEADER, StringContains.containsString(REST_EXECUTE_URI)))
		.andReturn();

	// wait to avoid untimely test tear down
	waitForOperationToComplete();

	// get execution ID
	String locationHeader = mvcResult.getResponse().getHeader(LOCATION_HEADER);
	String executionID = locationHeader.substring(locationHeader.lastIndexOf("/"));

	// get status
	mockMvc.perform(get(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());

	// get status
	mockMvc.perform(get(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());

	// get status
	mockMvc.perform(get(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());
    }

    /**
     * Test that GET "Get Operation Status" service invocation returns HTTP 404
     * for deleted execution ID.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetOperationStatus_ReturnsHttpNotFoundForDeleteExecutionId() throws Exception {
	createEnvConfigurationAndModule();
	reinitializeCoreComponent();

	// execute operation
	MvcResult mvcResult = mockMvc
		.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
			.accept(MediaType.APPLICATION_XML))
		.andExpect(status().isCreated())
		.andExpect(header().string(LOCATION_HEADER, StringContains.containsString(REST_EXECUTE_URI)))
		.andReturn();

	// wait to avoid untimely test tear down
	waitForOperationToComplete();

	// get execution ID
	String locationHeader = mvcResult.getResponse().getHeader(LOCATION_HEADER);
	String executionID = locationHeader.substring(locationHeader.lastIndexOf("/"));

	// delete status
	mockMvc.perform(delete(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());

	// get status
	mockMvc.perform(get(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isNotFound());
    }

    /**
     * Test that GET "Get Operation Status" service invocation fails with HTTP
     * 404.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetOperationStatus_ReturnsNotFoundForUnknownExecutionId() throws Exception {
	mockMvc.perform(get(REST_EXECUTE_STATUS_URI, randomExecutionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isNotFound());
    }

    /**
     * Test that POST "Get Operation Status" service invocation fails with HTTP
     * 405.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostGetOperationStatus_ReturnsMethodNotAllowed() throws Exception {
	mockMvc.perform(post(REST_EXECUTE_STATUS_URI, randomExecutionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test that DELETE "Delete Operation Status" service invocation returns
     * HTTP 200 for known execution ID.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDeleteOperationStatus_ReturnsHttpOk() throws Exception {
	createEnvConfigurationAndModule();
	reinitializeCoreComponent();

	// execute operation
	MvcResult mvcResult = mockMvc
		.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
			.accept(MediaType.APPLICATION_XML))
		.andExpect(status().isCreated())
		.andExpect(header().string(LOCATION_HEADER, StringContains.containsString(REST_EXECUTE_URI)))
		.andReturn();

	// wait to avoid untimely test tear down
	waitForOperationToComplete();

	// get execution ID
	String locationHeader = mvcResult.getResponse().getHeader(LOCATION_HEADER);
	String executionID = locationHeader.substring(locationHeader.lastIndexOf("/"));

	// delete status
	mockMvc.perform(delete(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());
    }

    /**
     * Test that DELETE "Delete Operation Status" service invocation fails for
     * unknown execution ID
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDeleteOperationStatus_ReturnsNotFoundForUnknownExecutionId() throws Exception {
	mockMvc.perform(delete(REST_EXECUTE_STATUS_URI, randomExecutionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isNotFound());
    }

    /**
     * Test that DELETE "Delete Operation Status" service invocation returns
     * HTTP 404 for deleted execution ID.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDeleteOperationStatus_ReturnsHttpNotFoundForDeletedExecutionId() throws Exception {
	createEnvConfigurationAndModule();
	reinitializeCoreComponent();

	// execute operation
	MvcResult mvcResult = mockMvc
		.perform(post(REST_EXECUTE_EXECUTE_URI, randomModule, helloWorldOperation, randomEnvironment)
			.accept(MediaType.APPLICATION_XML))
		.andExpect(status().isCreated())
		.andExpect(header().string(LOCATION_HEADER, StringContains.containsString(REST_EXECUTE_URI)))
		.andReturn();

	// wait to avoid untimely test tear down
	waitForOperationToComplete();

	// get execution ID
	String locationHeader = mvcResult.getResponse().getHeader(LOCATION_HEADER);
	String executionID = locationHeader.substring(locationHeader.lastIndexOf("/"));

	// delete status
	mockMvc.perform(delete(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());

	// delete status sencond time
	mockMvc.perform(delete(REST_EXECUTE_STATUS_URI, executionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isNotFound());
    }

    /**
     * Test that POST "Delete Operation Status" service invocation fails with
     * HTTP 405.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostDeleteOperationStatus_ReturnsMethodNotAllowed() throws Exception {
	mockMvc.perform(post(REST_EXECUTE_STATUS_URI, randomExecutionID).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isMethodNotAllowed());
    }

}
