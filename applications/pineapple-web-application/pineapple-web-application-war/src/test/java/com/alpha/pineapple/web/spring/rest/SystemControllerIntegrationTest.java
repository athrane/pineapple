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

import static com.alpha.pineapple.web.WebApplicationConstants.REST_SYSTEM_SIMPLE_STATUS_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SYSTEM_VERSION_URI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
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
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the {@linkplain SystemController} class.
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
public class SystemControllerIntegrationTest {

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

    @Before
    public void setUp() throws Exception {

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
     * Test that GET "Get Version" service invocation returns HTTP 200.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetGetVersion_ReturnsHttpOk() throws Exception {
	mockMvc.perform(get(REST_SYSTEM_VERSION_URI).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
    }

    /**
     * Test that GET "Get Version" service invocation returns content.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetGetVersion_ReturnsContent() throws Exception {
	mockMvc.perform(get(REST_SYSTEM_VERSION_URI).accept(MediaType.APPLICATION_XML))
		.andExpect(content().string(CoreMatchers.containsString("Pineapple Core Component, version")));
    }

    /**
     * Test that POST "Get Version" service invocation fails with HTTP 405.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostGetVersion_Fails() throws Exception {
	mockMvc.perform(post(REST_SYSTEM_VERSION_URI).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test that DELETE "Get Version" service invocation fails with HTTP 405.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDeleteGetVersion_Fails() throws Exception {
	mockMvc.perform(delete(REST_SYSTEM_VERSION_URI).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test that GET "Get Simple Status" service invocation returns HTTP 200.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetGetSimpleStatus_ReturnsHttpOk() throws Exception {
	mockMvc.perform(get(REST_SYSTEM_SIMPLE_STATUS_URI).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isOk());
    }

    /**
     * Test that GET "Get Simple Status" service invocation returns content.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testGetGetSimpleStatus_ReturnsContent() throws Exception {
	mockMvc.perform(get(REST_SYSTEM_SIMPLE_STATUS_URI).accept(MediaType.APPLICATION_XML)).andExpect(
		content().string(CoreMatchers.containsString("Pineapple Core Component was initialized successfully")));
    }

    /**
     * Test that POST "Get Simple Status" service invocation fails with HTTP
     * 405.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testPostGetSimpleStatus_Fails() throws Exception {
	mockMvc.perform(post(REST_SYSTEM_SIMPLE_STATUS_URI).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isMethodNotAllowed());
    }

    /**
     * Test that DELETE "Get Simple Status" service invocation fails with HTTP
     * 405.
     * 
     * @throws Exception
     *             if test fails.
     */
    @Test
    public void testDeleteGetSimpleStatus_Fails() throws Exception {
	mockMvc.perform(delete(REST_SYSTEM_SIMPLE_STATUS_URI).accept(MediaType.APPLICATION_XML))
		.andExpect(status().isMethodNotAllowed());
    }

}