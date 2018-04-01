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

import static com.alpha.pineapple.CoreConstants.REPORTS_DIR;
import static com.alpha.pineapple.web.WebApplicationConstants.*;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_DELETE_OPERATIONS_URI;
import static com.alpha.pineapple.web.WebApplicationConstants.REST_SCHEDULE_GET_OPERATIONS_URI;
import static com.alpha.testutils.TestUtilsTestConstants.pluginIdHelloWorld;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
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
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.execution.scheduled.ScheduledOperationRespository;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.pineapple.web.report.ReportRepository;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;
import com.alpha.testutils.ObjectMotherModule;

/**
 * Integration test of the {@linkplain ReportController} class.
 * 
 * The core component is initialized when the test runner processes
 * the @ContextConfiguration annotation which will initialize the core component
 * to the default Pineapple home directory.
 * 
 * The Pineapple home directory is reconfigured to point to the test case
 * directory. The controller and the underlying repository & marshaller resolves
 * the report directory from the runtime provider which resolves the home
 * directory dynamically from the home directory system property.
 * 
 * As a result the core component isn't used directly in this test case even
 * though is is configured as part of Spring context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/webapp-config.xml")
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
		DirectoryTestExecutionListener.class })
public class ReportControllerIntegrationTest {

	/**
	 * Environment configuration name space constant
	 */
	final static Map<String, String> NS = Collections.singletonMap("ns", "http://pineapple.dev.java.net/ns/report_1_0");

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
	 * Report repository.
	 */
	@Resource
	ReportRepository reportRepository;

	/**
	 * Execution result factory.
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

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
	 * Report directory.
	 */
	File reportsDir;

	/**
	 * Random Name.
	 */
	String randomName;

	/**
	 * Random description.
	 */
	String randomDescription;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create reports directory in the defined home directory
		reportsDir = new File(testDirectory, REPORTS_DIR);
		reportsDir.mkdir();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// initialize Spring MVC mock
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).alwaysDo(print()).build();

		result = executionResultFactory.startExecution(randomDescription);
	}

	@After
	public void tearDown() throws Exception {

		// clear the pineapple.home.dir system setting
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);
	}

	/**
	 * Test that Get "Get Reports" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetOperation_ReturnsHttpOk() throws Exception {
		reportRepository.initialize(result);

		mockMvc.perform(get(REST_REPORT_GET_REPORTS_URI).accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
	}

	/**
	 * Test that GET "Get Reports" service invocation returns empty content after
	 * initial core initialization.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetReports_ReturnsIntiallyEmptyContent() throws Exception {
		reportRepository.initialize(result);
		assertEquals(0, reportRepository.getReports().count());

		mockMvc.perform(get(REST_REPORT_GET_REPORTS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(xpath("/ns:reports", NS).exists())
				.andExpect(xpath("/ns:reports/ns:report", NS).doesNotExist());
	}

	/**
	 * Test that GET "Get Reports" service invocation returns content.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetReports_ReturnsContent() throws Exception {
		String report = "/ns:reports/ns:report[@id='%s']";
		reportRepository.initialize(result);

		// create report directory and register report
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);
		assertEquals(1, reportRepository.getReports().count());

		mockMvc.perform(get(REST_REPORT_GET_REPORTS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(xpath("/ns:reports", NS).exists()).andExpect(xpath("/ns:reports/ns:report", NS).exists())
				.andExpect(xpath(report, NS, randomName).exists());
	}

	/**
	 * Test that GET "Get Reports" service invocation returns content type
	 * "application/xml".
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testGetGetReports_ReturnsContentTypeApplicationXml() throws Exception {
		mockMvc.perform(get(REST_REPORT_GET_REPORTS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(content().contentType(MediaType.APPLICATION_XML));
	}

	/**
	 * Test that Delete "Delete Reports" service invocation returns HTTP 200.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteReports_ReturnsHttpOk() throws Exception {
		reportRepository.initialize(result);

		mockMvc.perform(delete(REST_REPORT_DELETE_REPORTS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
	}

	/**
	 * Test that Delete "Delete Reports" service invocation deletes reports.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testDeleteDeleteReports_DeletesReports() throws Exception {
		reportRepository.initialize(result);

		// create report directory and register report
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);
		assertEquals(1, reportRepository.getReports().count());

		mockMvc.perform(delete(REST_REPORT_DELETE_REPORTS_URI).accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());

		assertEquals(0, reportRepository.getReports().count());

	}
}