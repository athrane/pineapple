/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.command;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultImpl;
import com.alpha.pineapple.model.configuration.Configuration;
import com.alpha.pineapple.model.test.ItemType;
import com.alpha.pineapple.model.test.ObjectFactory;
import com.alpha.pineapple.model.test.Root;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * integration test of the class {@link UnmarshallJAXBObjectsCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class UnmarshallJAXBObjectsCommandIntegrationTest {

	/**
	 * Object under test.
	 */
	@Resource
	Command unmarshallJAXBObjectsCommand;

	/**
	 * Execution result.
	 */
	ExecutionResult executionResult;

	/**
	 * Current test directory.
	 */
	File testDirectory;

	/**
	 * Random value.
	 */
	String randomXmlFileName;

	/**
	 * Random value.
	 */
	String randomDescription;

	/**
	 * Configuration object mother
	 */
	ObjectMotherEnvironmentConfiguration configMother;

	@Before
	public void setUp() throws Exception {
		randomXmlFileName = RandomStringUtils.randomAlphabetic(10) + ".xml";
		randomDescription = RandomStringUtils.randomAlphabetic(10);

		// get the test directory
		testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

		// create execution result
		executionResult = new ExecutionResultImpl(randomDescription);

		// configuration object mother
		configMother = new ObjectMotherEnvironmentConfiguration();
	}

	@After
	public void tearDown() throws Exception {
		testDirectory = null;
		configMother = null;
		unmarshallJAXBObjectsCommand = null;
		executionResult = null;
	}

	/**
	 * Test that file with absolute path can be loaded.
	 * 
	 * @throws Exception
	 *             if test fails.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanLoadFileWithAbsolutePath() throws Exception {

		// create context
		Context context = new ContextBase();

		// create file name
		StringBuilder fileName = new StringBuilder().append(testDirectory.getAbsolutePath()).append(File.separatorChar)
				.append(randomXmlFileName);

		// create file
		File file = new File(fileName.toString());

		// create document
		ObjectFactory factory = new ObjectFactory();
		Root root = factory.createRoot();
		root.setContainer(factory.createContainerType());
		ItemType item1 = factory.createItemType();
		item1.setName("item1");
		ItemType item2 = factory.createItemType();
		item2.setName("item2");
		root.getContainer().getItems().add(item1);
		root.getContainer().getItems().add(item2);

		// marshall objects
		configMother.jaxbMarshall(root, file);

		// setup context
		context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, file);
		context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, Root.class.getPackage());
		context.put(UnmarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		unmarshallJAXBObjectsCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		Root expected = (Root) context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY);
		assertNotNull(expected);
	}

	/**
	 * Test that command fails if file name property contains non-existing file in
	 * directory.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandFailsIfFileDoesntExist() throws Exception {

		// create context
		Context context = new ContextBase();

		// create configuration file name
		String fileName = new StringBuilder().append(testDirectory.getAbsolutePath()).append(File.separatorChar)
				.append("non-existing-config.xml").toString();

		// create file
		File file = new File(fileName.toString());

		// add configuration file name into context
		context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, file);
		context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, Root.class.getPackage());
		context.put(UnmarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		unmarshallJAXBObjectsCommand.execute(context);

		// test
		assertTrue(executionResult.isError());
	}

	/**
	 * Test that command fails if file name property contains non-existing file on
	 * class path.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCommandFailsIfFileDoesntExist2() throws Exception {

		// create context
		Context context = new ContextBase();

		// create file
		File file = new File("non-existing-config.xml");

		// add configuration file name into context
		context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, file);
		context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, Root.class.getPackage());
		context.put(UnmarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		unmarshallJAXBObjectsCommand.execute(context);

		// test
		assertTrue(executionResult.isFailed());
	}

	/**
	 * Test that command can locate an unmarshall a file from the class path.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCanLoadFromClassPath() throws Exception {
		// create context
		Context context = new ContextBase();

		// create file
		File file = new File("resources.xml");

		// add configuration file name into context
		context.put(UnmarshallJAXBObjectsCommand.FILE_KEY, file);
		context.put(UnmarshallJAXBObjectsCommand.PACKAGE_KEY, Configuration.class.getPackage());
		context.put(UnmarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

		// execute command
		unmarshallJAXBObjectsCommand.execute(context);

		// test
		assertTrue(executionResult.isSuccess());
		Configuration expected;
		expected = (Configuration) context.get(UnmarshallJAXBObjectsCommand.UNMARSHALLING_RESULT_KEY);
		assertTrue(expected instanceof Configuration);

	}

}
