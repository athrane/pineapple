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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.apache.commons.lang.RandomStringUtils;
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
import com.alpha.pineapple.model.test.ItemType;
import com.alpha.pineapple.model.test.ObjectFactory;
import com.alpha.pineapple.model.test.Root;
import com.alpha.springutils.DirectoryTestExecutionListener;
import com.alpha.testutils.ObjectMotherEnvironmentConfiguration;

/**
 * integration test of the class {@link MarshallJAXBObjectsCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class MarshallJAXBObjectsCommandIntegrationTest {

    /**
     * Object under test.
     */
    @Resource
    Command marshallJAXBObjectsCommand;

    /**
     * Execution result.
     */
    ExecutionResult executionResult;

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Configuration object mother
     */
    ObjectMotherEnvironmentConfiguration configMother;

    /**
     * Random file name.
     */
    String randomXmlFileName;

    /**
     * Random name.
     */
    String randomName;

    @Before
    public void setUp() throws Exception {
	randomXmlFileName = RandomStringUtils.randomAlphabetic(10) + ".xml";
	randomName = RandomStringUtils.randomAlphabetic(10);

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	// create execution result
	executionResult = new ExecutionResultImpl("Root result");

	// configuration object mother
	configMother = new ObjectMotherEnvironmentConfiguration();
    }

    @After
    public void tearDown() throws Exception {
	testDirectory = null;
	configMother = null;
	marshallJAXBObjectsCommand = null;
	executionResult = null;
    }

    /**
     * Test that file is defined after execution.
     * 
     * @throws Exception
     *             If test fails.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFileIsDefinedAfterExecution() throws Exception {

	// create context
	Context context = new ContextBase();

	// create file
	File file = new File(testDirectory, randomXmlFileName);

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

	// setup context
	context.put(MarshallJAXBObjectsCommand.ROOT_KEY, root);
	context.put(MarshallJAXBObjectsCommand.FILE_KEY, file);
	context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

	// test
	assertFalse(file.exists());

	// execute command
	marshallJAXBObjectsCommand.execute(context);

	// test
	assertTrue(file.exists());
	assertTrue(file.isFile());
	assertTrue(executionResult.isSuccess());
	assertFalse(executionResult.isExecuting());
    }

    /**
     * Test that file is defined after execution.
     * 
     * @throws Exception
     *             If test fails.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testWillCreateSubDirectoriesPriorToMarshalling() throws Exception {

	// create context
	Context context = new ContextBase();

	// create directory
	String randomDirName = RandomStringUtils.randomAlphabetic(10);
	File finalTestDir = new File(testDirectory, randomDirName);

	// create file
	File file = new File(finalTestDir, randomXmlFileName);

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

	// setup context
	context.put(MarshallJAXBObjectsCommand.ROOT_KEY, root);
	context.put(MarshallJAXBObjectsCommand.FILE_KEY, file);
	context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

	// test
	assertFalse(file.exists());

	// execute command
	marshallJAXBObjectsCommand.execute(context);

	// test
	assertTrue(file.exists());
	assertTrue(file.isFile());
	assertTrue(executionResult.isSuccess());
	assertFalse(executionResult.isExecuting());
    }

    /**
     * Test that file is defined after execution.
     * 
     * @throws Exception
     *             If test fails.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testWillCreateSubDirectoriesPriorToMarshalling2() throws Exception {

	// create context
	Context context = new ContextBase();

	// create directory
	String randomDirName = RandomStringUtils.randomAlphabetic(10);
	String randomDirName2 = RandomStringUtils.randomAlphabetic(10);
	File finalTestDir = new File(testDirectory, randomDirName);
	File finalTestDir2 = new File(finalTestDir, randomDirName2);

	// create file
	File file = new File(finalTestDir2, randomXmlFileName);

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

	// setup context
	context.put(MarshallJAXBObjectsCommand.ROOT_KEY, root);
	context.put(MarshallJAXBObjectsCommand.FILE_KEY, file);
	context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

	// test
	assertFalse(file.exists());

	// execute command
	marshallJAXBObjectsCommand.execute(context);

	// test
	assertTrue(file.exists());
	assertTrue(file.isFile());
	assertTrue(executionResult.isSuccess());
	assertFalse(executionResult.isExecuting());
    }

    /**
     * Test that marshalling fails if directory is illegal.
     * 
     * @throws Exception
     *             If test fails.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFailsIfdirectoryIsIllegal() throws Exception {

	// create context
	Context context = new ContextBase();

	// create file
	File file = new File(new File(randomName), randomXmlFileName);

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

	// setup context
	context.put(MarshallJAXBObjectsCommand.ROOT_KEY, root);
	context.put(MarshallJAXBObjectsCommand.FILE_KEY, file);
	context.put(MarshallJAXBObjectsCommand.EXECUTIONRESULT_KEY, executionResult);

	// test
	assertFalse(file.exists());

	// execute command
	marshallJAXBObjectsCommand.execute(context);

	// test
	assertTrue(executionResult.isFailed());
	assertFalse(executionResult.isExecuting());
    }

}
