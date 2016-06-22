/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.   
 */

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
import com.alpha.springutils.DirectoryTestExecutionListener;

/**
 * integration test of the class {@link CopyExampleModulesCommand}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirectoryTestExecutionListener.class })
@ContextConfiguration(locations = { "/com.alpha.pineapple.core-config.xml" })
public class CopyExampleModulesCommandIntegrationTest {

    /**
     * Current test directory.
     */
    File testDirectory;

    /**
     * Object under test.
     */
    @Resource
    Command copyExampleModulesCommand;

    /**
     * Context.
     */
    Context context;

    /**
     * Mock execution result.
     */
    ExecutionResult executionResult;

    /**
     * Random value.
     */
    String randomValue;

    /**
     * Random value.
     */
    String randomValue2;

    /**
     * Random value.
     */
    String randomValue3;

    @Before
    public void setUp() throws Exception {

	// get the test directory
	testDirectory = DirectoryTestExecutionListener.getCurrentTestDirectory();

	randomValue = RandomStringUtils.randomAlphabetic(10);
	randomValue2 = RandomStringUtils.randomAlphabetic(10);

	// create context
	context = new ContextBase();

	// create execution result
	executionResult = new ExecutionResultImpl("Root result");
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test that command can execute successfully.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCanCopyExamples() throws Exception {
	// destination directory
	File destinationDir = new File(testDirectory, randomValue);

	// test
	assertFalse(destinationDir.exists());

	// setup context
	context.put(CopyExampleModulesCommand.DESTINATION_DIR_KEY, destinationDir);
	context.put(CopyExampleModulesCommand.EXECUTIONRESULT_KEY, executionResult);

	// execute command
	copyExampleModulesCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertFalse(executionResult.isExecuting());
    }

    /**
     * Test that destination directory is created.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDestinationDirectoryIsCreated() throws Exception {
	// destination directory
	File destinationDir = new File(testDirectory, randomValue);

	// test
	assertFalse(destinationDir.exists());

	// setup context
	context.put(CopyExampleModulesCommand.DESTINATION_DIR_KEY, destinationDir);
	context.put(CopyExampleModulesCommand.EXECUTIONRESULT_KEY, executionResult);

	// execute command
	copyExampleModulesCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertFalse(executionResult.isExecuting());
	assertTrue(destinationDir.exists());
	assertTrue(destinationDir.isDirectory());
    }

    /**
     * Test that destination directory isn't empty.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDestinationDirectoryIsntEmpty() throws Exception {
	// destination directory
	File destinationDir = new File(testDirectory, randomValue);

	// test
	assertFalse(destinationDir.exists());

	// setup context
	context.put(CopyExampleModulesCommand.DESTINATION_DIR_KEY, destinationDir);
	context.put(CopyExampleModulesCommand.EXECUTIONRESULT_KEY, executionResult);

	// execute command
	copyExampleModulesCommand.execute(context);

	// test
	assertTrue(executionResult.isSuccess());
	assertFalse(executionResult.isExecuting());
	assertTrue(destinationDir.listFiles().length > 0);
    }

}
