/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.web.report;

import static com.alpha.pineapple.CoreConstants.REPORTS_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.javautils.SystemUtils;
import com.alpha.pineapple.PineappleCore;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultFactory;
import com.alpha.pineapple.model.report.Report;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Integration test of the {@linkplain ReportRepositoryImpl} class.
 * 
 * The core component is initialized when the test runner processes
 * the @ContextConfiguration annotation which will initialize the core component
 * to the default Pineapple home directory.
 * 
 * The Pineapple home directory is reconfigured to point to the test case
 * directory. The repository and the underlying marshaller resolves the report
 * directory from the runtime provider which resolves the home directory
 * dynamically from the home directory system property.
 * 
 * As a result the core component isn't used directly in this test case even
 * though is is configured as part of Spring context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/webapp-config.xml")
public class ReportRepositoryIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Subject under test.
	 */
	@Resource
	ReportRepository reportRepository;

	/**
	 * Web application core component factory.
	 */
	@Resource
	WebAppCoreFactory webAppCoreFactory;

	/**
	 * Execution result factory.
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	/**
	 * Report directory.
	 */
	File reportsDir;

	/**
	 * Random value.
	 */
	String randomName;

	/**
	 * Random value.
	 */
	String randomName2;

	/**
	 * Random value.
	 */
	String randomDescription;

	/**
	 * Core component.
	 * 
	 * Initialize by web application core component factory.
	 */
	PineappleCore coreComponent;

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomName2 = RandomStringUtils.randomAlphabetic(10);
		randomDescription = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// create reports directory in the defined home directory
		reportsDir = new File(testDirectory, REPORTS_DIR);
		reportsDir.mkdir();

		result = executionResultFactory.startExecution(randomDescription);
	}

	@After
	public void tearDown() throws Exception {

		// clear the pineapple.home.dir system property
		System.getProperties().remove(SystemUtils.PINEAPPLE_HOMEDIR);

		// fail if the the pineapple.home.dir system property is set
		assertNull(System.getProperty(SystemUtils.PINEAPPLE_HOMEDIR));
	}

	/**
	 * Test that instance can be retrieved from application context.
	 */
	@Test
	public void testGetInstanceFromContext() {
		assertNotNull(reportRepository);
	}

	/**
	 * Test that initialized repository returns expected execution result.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testInitializeContainsExpectedResult() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.isExecuting());
		assertEquals(1, result.getChildren().length);
		assertTrue(result.getFirstChild().isSuccess());
	}

	/**
	 * Test that no reports are defined after first initialization.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testNoReportsAreDefinedAfterFirstInitialization() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		assertNotNull(reportRepository.getReports());
		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Test report can be added to repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testAddReport() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();

		// register report
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		Report report = reportRepository.add(randomName, reportResult);

		// test report is generated
		assertNotNull(report);
		assertNotNull(reportRepository.getReports());
		assertEquals(1, reportRepository.getReports().count());
	}

	/**
	 * Test multiple reports can be added to repository.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testAddMultipleReports() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();

		// register report
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		Report report = reportRepository.add(randomName, reportResult);

		// test report is generated
		assertNotNull(report);

		// create report directory with same name as report ID
		reportDirectory = new File(reportsDir, randomName2);
		reportDirectory.mkdir();

		// register report
		reportResult = executionResultFactory.startExecution(randomDescription);
		report = reportRepository.add(randomName2, reportResult);

		// test report is generated
		assertNotNull(report);

		assertNotNull(reportRepository.getReports());
		assertEquals(2, reportRepository.getReports().count());
	}

	/**
	 * Test that addition of report with undefined id fails.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateReportWithUndefinedId() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(null, reportResult);
	}

	/**
	 * Test that addition of report with empty id fails.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToCreateReportWithEmptyId() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add("", reportResult);
	}

	/**
	 * Test that addition of report with non-existing directory return null.
	 * 
	 * @throws Exception
	 *             if test fails
	 */
	@Test
	public void testCreationOFReportWithNonexistingDirectoryReturnsNull() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		assertNull(reportRepository.add(randomName, reportResult));
	}

	/**
	 * Test that reports are retained after initialization.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testReportsAreRetainedAfterInitialization() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);

		// create report directory with same name as report ID
		reportDirectory = new File(reportsDir, randomName2);
		reportDirectory.mkdir();
		reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName2, reportResult);

		reportRepository.initialize(result);

		// test
		assertNotNull(reportRepository.getReports());
		assertEquals(2, reportRepository.getReports().count());
	}

	/**
	 * Test that empty repository can be initialized multiple times.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testEmptyRepositoryCanBeInitializedMultipleTimes() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertNotNull(reportRepository.getReports());
		assertEquals(0, reportRepository.getReports().count());

		reportRepository.initialize(result);
		assertTrue(result.getChildren()[1].isSuccess());

		reportRepository.initialize(result);
		assertTrue(result.getChildren()[2].isSuccess());

		reportRepository.initialize(result);
		assertTrue(result.getChildren()[3].isSuccess());
	}

	/**
	 * Test that repository can be initialized multiple times.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testRepositoryCanBeInitializedMultipleTimes() throws Exception {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);

		assertNotNull(reportRepository.getReports());
		assertEquals(1, reportRepository.getReports().count());

		reportRepository.initialize(result);
		assertTrue(result.getChildren()[1].isSuccess());

		reportRepository.initialize(result);
		assertTrue(result.getChildren()[2].isSuccess());

		reportRepository.initialize(result);
		assertTrue(result.getChildren()[3].isSuccess());
		assertNotNull(reportRepository.getReports());
		assertEquals(1, reportRepository.getReports().count());
	}

	/**
	 * Can get empty set of reports.
	 */
	@Test
	public void testGetEmptySetOfReports() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());

		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Can get set of reports with one report.
	 */
	@Test
	public void testGetSetOfReportsWithOneReport() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);

		// test
		assertEquals(1, reportRepository.getReports().count());
	}

	/**
	 * Can get set of reports with multiple reports.
	 */
	@Test
	public void testGetSetOfReportsWithMultipleReports() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);

		// create report directory with same name as report ID
		reportDirectory = new File(reportsDir, randomName2);
		reportDirectory.mkdir();
		reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName2, reportResult);

		// test
		assertEquals(2, reportRepository.getReports().count());
	}

	/**
	 * Can delete get empty set of reports.
	 */
	@Test
	public void testDeleteEmptySetOfReports() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());
		reportRepository.deleteAll();
		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Can delete set of reports with one report.
	 */
	@Test
	public void testDeleteSetOfReportsWithOneReport() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);
		assertEquals(1, reportRepository.getReports().count());

		// delete
		reportRepository.deleteAll();
		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Can delete set of reports with multiple reports.
	 */
	@Test
	public void testDeleteSetOfReportsWithMultipleReports() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);

		// create report directory with same name as report ID
		reportDirectory = new File(reportsDir, randomName2);
		reportDirectory.mkdir();
		reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName2, reportResult);
		assertEquals(2, reportRepository.getReports().count());

		// delete
		reportRepository.deleteAll();
		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Can delete single report.
	 */
	@Test
	public void testDeleteSingleReport() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// create report directory with same name as report ID
		File reportDirectory = new File(reportsDir, randomName);
		reportDirectory.mkdir();
		ExecutionResult reportResult = executionResultFactory.startExecution(randomDescription);
		reportRepository.add(randomName, reportResult);
		assertEquals(1, reportRepository.getReports().count());

		// delete
		reportRepository.delete(randomName);
		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Can delete single unknown report.
	 */
	@Test
	public void testDeleteSingleUnknownReport() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// delete
		reportRepository.delete(randomName);
		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Fails to delete single report with undefined id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteSingleReportWithUndefinedId() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// delete
		reportRepository.delete(null);
		assertEquals(0, reportRepository.getReports().count());
	}

	/**
	 * Fails to delete single report with empty id.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testFailsToDeleteSingleReportWithEmptyId() {
		reportRepository.initialize(result);
		assertTrue(result.getFirstChild().isSuccess());
		assertEquals(0, reportRepository.getReports().count());

		// delete
		reportRepository.delete("");
		assertEquals(0, reportRepository.getReports().count());
	}

}
