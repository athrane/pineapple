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
import static com.alpha.pineapple.web.WebApplicationConstants.REPORTS_FILE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.io.file.RuntimeDirectoryProvider;
import com.alpha.pineapple.model.report.Reports;
import com.alpha.pineapple.web.WebAppCoreFactory;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * Integration test of the {@linkplain ReportSetMarshallerImpl} class.
 * 
 * The core component is initialized when the test runner processes
 * the @ContextConfiguration annotation which will initialize the core component
 * to the default Pineapple home directory.
 * 
 * The Pineapple home directory is reconfigured to point to the test case
 * directory. The marshaller resolves the report directory from the runtime
 * provider which resolves the home directory dynamically from the home
 * directory system property.
 * 
 * As a result the core component isn't used directly in this test case even
 * though is is configured as part of Spring context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("integration-test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/webapp-config.xml")
public class ReportSetMarshallerIntegrationTest {

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Subject under test.
	 */
	@Resource(name = "reportSetMarshaller")
	ReportSetMarshaller marshaller;

	/**
	 * Runtime directory resolver.
	 */
	@Resource
	RuntimeDirectoryProvider runtimeDirectoryProvider;

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
	 * Report directory.
	 */
	File reportsDir;

	/**
	 * Random value.
	 */
	String randomName;

	/**
	 * Random environment.
	 */
	String randomEnvironment;

	/**
	 * Core component.
	 * 
	 * Initialize by web application core component factory.
	 */
	PineappleCore coreComponent;

	@Before
	public void setUp() throws Exception {
		randomName = RandomStringUtils.randomAlphabetic(10);
		randomEnvironment = RandomStringUtils.randomAlphabetic(10);

		// create environment configuration object mother
		envConfigMother = new ObjectMotherEnvironmentConfiguration();

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// set the pineapple.home.dir system property
		System.setProperty(SystemUtils.PINEAPPLE_HOMEDIR, testDirectory.getAbsolutePath());

		// define directory names
		reportsDir = new File(testDirectory, REPORTS_DIR);
		reportsDir.mkdir();
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
		assertNotNull(marshaller);
	}

	/**
	 * Test that empty reports file can be saved.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testSaveEmptyReports() throws Exception {
		File reportsFile = new File(runtimeDirectoryProvider.getReportsDirectory(), REPORTS_FILE);
		assertFalse(reportsFile.exists());
		assertFalse(reportsFile.isFile());

		Reports reports = envConfigMother.createEmptyReports();
		marshaller.save(reports);

		// test
		assertTrue(reportsFile.exists());
		assertTrue(reportsFile.isFile());
	}

	/**
	 * Test that configuration can be saved.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testSaveReports() throws Exception {
		File reportsFile = new File(runtimeDirectoryProvider.getReportsDirectory(), REPORTS_FILE);
		assertFalse(reportsFile.exists());
		assertFalse(reportsFile.isFile());

		Reports reports = envConfigMother.createReportsWithOneReport(randomName);
		marshaller.save(reports);

		// test
		assertTrue(reportsFile.exists());
		assertTrue(reportsFile.isFile());
	}

	/**
	 * Test that empty reports file can be loaded.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testLoadEmptyReports() throws Exception {
		File reportsFile = new File(runtimeDirectoryProvider.getReportsDirectory(), REPORTS_FILE);
		assertFalse(reportsFile.exists());
		assertFalse(reportsFile.isFile());

		// save
		Reports reports = envConfigMother.createEmptyReports();
		marshaller.save(reports);

		// load
		ExecutionResultImpl result = new ExecutionResultImpl("Load reports");
		Reports loadedReports = marshaller.load(result);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		ExecutionResult loadResult = result.getFirstChild();

		// test
		assertTrue(loadResult.isSuccess());
		assertNotNull(loadedReports);
		assertTrue(loadedReports.getReport().isEmpty());
	}

	/**
	 * Test that configuration can be loaded with one report.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testLoadReports() throws Exception {
		File reportsFile = new File(runtimeDirectoryProvider.getReportsDirectory(), REPORTS_FILE);
		assertFalse(reportsFile.exists());
		assertFalse(reportsFile.isFile());

		// save
		Reports reports = envConfigMother.createReportsWithOneReport(randomName);
		marshaller.save(reports);

		// load
		ExecutionResultImpl result = new ExecutionResultImpl("Load reports");
		Reports loadedReports = marshaller.load(result);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		ExecutionResult loadResult = result.getFirstChild();

		// test
		assertTrue(loadResult.isSuccess());
		assertNotNull(loadedReports);
		assertEquals(1, loadedReports.getReport().size());
	}

	/**
	 * Test that non-existing configuration can be loaded and then an empty
	 * configuration is returned.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@Test
	public void testLoadNonExistingReportsFile() throws Exception {
		File reportsFile = new File(runtimeDirectoryProvider.getReportsDirectory(), REPORTS_FILE);
		assertFalse(reportsFile.exists());
		assertFalse(reportsFile.isFile());

		// load
		ExecutionResultImpl result = new ExecutionResultImpl("Load reports");
		Reports loadedReports = marshaller.load(result);

		// get child result
		ExecutionResult[] children = result.getChildren();
		assertNotNull(children);
		assertEquals(1, children.length);
		ExecutionResult loadResult = result.getFirstChild();

		// test
		assertTrue(loadResult.isSuccess());
		assertNotNull(loadedReports);
		assertEquals(0, loadedReports.getReport().size());
	}

}
