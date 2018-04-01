/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.pineapple.report.basichtml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.execution.ExecutionResultNotificationImpl;
import com.alpha.pineapple.execution.ResultListener;
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * Integration test for the <code>BasicHtmlReportGeneratorImpl</code> class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.report.basichtml-config.xml" })
public class BasicHtmlReportGeneratorImplIntegrationTest {

	/**
	 * Operation name.
	 */
	static final String OPERATION = "operation on module";

	/**
	 * Object under test.
	 */
	@Resource(name = BasicHtmlReportGeneratorImpl.GENERATOR_BEAN_ID)
	ResultListener generator;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Execution result notification.
	 */
	ExecutionResultNotification notification;

	/**
	 * Root execution result.
	 */
	ExecutionResult rootResult;

	@Before
	public void setUp() throws Exception {

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();
	}

	@After
	public void tearDown() throws Exception {

		testDirectory = null;
	}

	/**
	 * Create result notification.
	 * 
	 * @return
	 */
	ExecutionResultNotification createNotificationAndRootResult() {
		rootResult = new ExecutionResultImpl(OPERATION);
		rootResult.setState(ExecutionState.SUCCESS);
		notification = ExecutionResultNotificationImpl.getInstance(rootResult, ExecutionState.SUCCESS);
		return notification;
	}

	/**
	 * Return first file with suffix.
	 * 
	 * @param directory
	 *            Directory to search.
	 * @param suffix
	 *            Suffix to search for.
	 * 
	 * @return Return first file with suffix.
	 */
	File getFile(File[] directory, String suffix) {

		for (File content : directory) {
			if (content.getName().endsWith(suffix)) {
				return content;
			}
		}
		return null;
	}

	/**
	 * Get content of time stamped directory.
	 * 
	 * @param generator
	 *            Report generator.
	 * 
	 * @return content of time stamped directory.
	 */
	File[] getTimeStampedDirectoryContent(ResultListener generator) {

		// type cast
		ReportGeneratorInfo generatorInfo = (ReportGeneratorInfo) generator;

		// get directory and files
		File reportDirectory = generatorInfo.getCurrentReportDirectory();
		File[] reportdirectoryContent = reportDirectory.listFiles();

		// get time stamped directory
		File timestamedDirectory = reportdirectoryContent[0];
		File[] timestamedDirectoryContent = timestamedDirectory.listFiles();
		return timestamedDirectoryContent;
	}

	/**
	 * Test that generator can be obtained from the Spring context.
	 */
	@Test
	public void testCanGetInstanceFromSpringContext() {
		assertNotNull(generator);
	}

	/**
	 * Test that generator can be obtained from no-arg factory method.
	 */
	@Test
	public void testCanGetInstanceFromNoArgFactoryMethod() {
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance();
		assertNotNull(factoryCreatedGenerator);
	}

	/**
	 * Test that generator can be obtained from factory method.
	 */
	@Test
	public void testCanGetInstanceFromFactoryMethod() {

		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);
		assertNotNull(factoryCreatedGenerator);
	}

	/**
	 * Test that factory method rejects an undefined report directory.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRejectsUndefinedReportDirectory() {

		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(null);
		assertNotNull(factoryCreatedGenerator);
	}

	/**
	 * Test that generator creates a single time stamped directory for a report.
	 */
	@Test
	public void testCreatesTimeStampedDirectory() {

		notification = createNotificationAndRootResult();

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// type cast
		ReportGeneratorInfo generatorInfo = (ReportGeneratorInfo) factoryCreatedGenerator;

		// get directory and files
		File reportDirectory = generatorInfo.getCurrentReportDirectory();
		File[] reportdirectoryContent = reportDirectory.listFiles();

		// test
		assertEquals(1, reportdirectoryContent.length);
	}

	/**
	 * Test that generator creates a single time stamped directory with a correct
	 * name.
	 */
	@Test
	public void testTimeStampedDirectoryHasCorrectName() {

		notification = createNotificationAndRootResult();

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// type cast
		ReportGeneratorInfo generatorInfo = (ReportGeneratorInfo) factoryCreatedGenerator;

		// get directory and files
		File reportDirectory = generatorInfo.getCurrentReportDirectory();
		File[] reportdirectoryContent = reportDirectory.listFiles();

		// get time stamped directory
		File timestamedDirectory = reportdirectoryContent[0];

		// test
		assertTrue(timestamedDirectory.getName().startsWith("report-"));
	}

	/**
	 * Test that generator creates a single time stamped directory which contains an
	 * XML report file.
	 */
	@Test
	public void testTimeStampedDirectoryContainsXmlFile() {

		notification = createNotificationAndRootResult();

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get content
		File[] timestamedDirectoryContent = getTimeStampedDirectoryContent(factoryCreatedGenerator);

		// test
		assertNotNull(getFile(timestamedDirectoryContent, ".xml"));
	}

	/**
	 * Test that generator creates a single time stamped directory which contains an
	 * Html report file.
	 */
	@Test
	public void testTimeStampedDirectoryContainsHtmlFile() {

		notification = createNotificationAndRootResult();

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get content
		File[] timestamedDirectoryContent = getTimeStampedDirectoryContent(factoryCreatedGenerator);

		// test
		assertNotNull(getFile(timestamedDirectoryContent, ".html"));
	}

	/**
	 * Test that generator can create simple report with the operation a single
	 * node.
	 */
	@Test
	public void testCanCreateSimpleReport() {

		notification = createNotificationAndRootResult();

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get content
		File[] timestamedDirectoryContent = getTimeStampedDirectoryContent(factoryCreatedGenerator);

		// test
		assertNotNull(getFile(timestamedDirectoryContent, ".xml"));
		assertNotNull(getFile(timestamedDirectoryContent, ".html"));
	}

	/**
	 * Test that generator can create a report with a single model node.
	 */
	@Test
	public void testCanCreateReportWithSingleModel() {

		notification = createNotificationAndRootResult();

		ExecutionResult child = rootResult.addChild("model#1");
		child.setState(ExecutionState.SUCCESS);

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get content
		File[] timestamedDirectoryContent = getTimeStampedDirectoryContent(factoryCreatedGenerator);

		// test
		assertNotNull(getFile(timestamedDirectoryContent, ".xml"));
		assertNotNull(getFile(timestamedDirectoryContent, ".html"));
	}

	/**
	 * Test that generator can create a report with a two model nodes.
	 */
	@Test
	public void testCanCreateReportWithTwoModels() {

		notification = createNotificationAndRootResult();

		ExecutionResult child = rootResult.addChild("model#1");
		child.setState(ExecutionState.SUCCESS);

		ExecutionResult child2 = rootResult.addChild("model#2");
		child2.setState(ExecutionState.SUCCESS);

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get content
		File[] timestamedDirectoryContent = getTimeStampedDirectoryContent(factoryCreatedGenerator);

		// test
		assertNotNull(getFile(timestamedDirectoryContent, ".xml"));
		assertNotNull(getFile(timestamedDirectoryContent, ".html"));
	}

	/**
	 * Test that generator can create a report with a single model node with a
	 * single child.
	 */
	@Test
	public void testCanCreateReportWithSingleModelAndChild() {

		notification = createNotificationAndRootResult();

		ExecutionResult modelResult = rootResult.addChild("model#1");
		modelResult.setState(ExecutionState.SUCCESS);

		ExecutionResult child = modelResult.addChild("child#1");
		child.setState(ExecutionState.SUCCESS);

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get content
		File[] timestamedDirectoryContent = getTimeStampedDirectoryContent(factoryCreatedGenerator);

		// test
		assertNotNull(getFile(timestamedDirectoryContent, ".xml"));
		assertNotNull(getFile(timestamedDirectoryContent, ".html"));
	}

	/**
	 * Test that generator can create a report with a single model node with a
	 * single child with two messages.
	 */
	@Test
	public void testCanCreateReportWithSingleModelAndChildAndTwoMessages() {

		notification = createNotificationAndRootResult();

		ExecutionResult modelResult = rootResult.addChild("model#1");
		modelResult.setState(ExecutionState.SUCCESS);

		ExecutionResult child = modelResult.addChild("child#1");
		child.setState(ExecutionState.SUCCESS);
		child.addMessage("Message", "...some message");
		child.addMessage("AnotherMessage", "...another message");

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get content
		File[] timestamedDirectoryContent = getTimeStampedDirectoryContent(factoryCreatedGenerator);

		// test
		assertNotNull(getFile(timestamedDirectoryContent, ".xml"));
		assertNotNull(getFile(timestamedDirectoryContent, ".html"));
	}

	/**
	 * Test that generator adds report ID to root execution result.
	 */
	@Test
	public void testAddsReportIdToRootResult() {

		notification = createNotificationAndRootResult();

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// test
		Map<String, String> messages = rootResult.getMessages();
		assertTrue(messages.containsKey(ExecutionResult.MSG_REPORT));
		assertNotNull(messages.get(ExecutionResult.MSG_REPORT));
	}

	/**
	 * Test that returned report ID corresponds to report directory.
	 */
	@Test
	public void testAddsReportIdCorrespondsToReportDir() {

		notification = createNotificationAndRootResult();

		// create generator
		File rootDirectory = new File(testDirectory, "reports");
		ResultListener factoryCreatedGenerator;
		factoryCreatedGenerator = BasicHtmlReportGeneratorImpl.getInstance(rootDirectory);

		// create report
		factoryCreatedGenerator.notify(notification);

		// get report ID
		Map<String, String> messages = rootResult.getMessages();
		String reportId = messages.get(ExecutionResult.MSG_REPORT);

		// type cast
		ReportGeneratorInfo generatorInfo = (ReportGeneratorInfo) factoryCreatedGenerator;

		// get directory and files
		File reportDirectory = generatorInfo.getCurrentReportDirectory();
		File[] reportdirectoryContent = reportDirectory.listFiles();

		// get time stamped directory
		File timestamedDirectory = reportdirectoryContent[0];

		// test
		assertEquals(reportId, timestamedDirectory.getName());

	}

}
