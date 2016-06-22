/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen.
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

package com.alpha.springutils;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import com.alpha.testutils.ObjectMotherIO;

/**
 * Spring test execution listener which creates a test directory prior to
 * execution of a test method. the listener class can be configured to delete
 * the test directory after the test method is executed.
 */
public class DirectoryTestExecutionListener implements TestExecutionListener {

    /**
     * Logger object.
     */
    static Logger logger = Logger.getLogger(DirectoryTestExecutionListener.class.getName());

    /**
     * Current test directory.
     */
    static File testDirectory = null;

    /**
     * Determines whether directories should be deleted after execution of a
     * test method.
     */
    static boolean deleteDirectories = false;

    public void afterTestMethod(TestContext testContext) throws Exception {

	if (deleteDirectories) {
	    // delete directories and files
	    ObjectMotherIO.deleteDirectory(testDirectory);
	}
    }

    public void beforeTestMethod(TestContext testContext) throws Exception {

	// get the current test method
	Method testMethod = testContext.getTestMethod();

	// get simple name
	String testMethodName = testMethod.getName();

	// get class where method is declared
	Class<?> declaringClass = testMethod.getDeclaringClass();

	// get class name
	String simpleClassName = declaringClass.getSimpleName();

	// create test directory name
	StringBuilder testDirectoryName = new StringBuilder();
	testDirectoryName.append(simpleClassName);
	testDirectoryName.append("-");
	testDirectoryName.append(testMethodName);

	// create directory for test
	testDirectory = ObjectMotherIO.createTestMethodDirectory(testDirectoryName.toString());

	// log debug message
	if (logger.isDebugEnabled()) {
	    StringBuilder message = new StringBuilder();
	    message.append("Initialized test directory <");
	    message.append(testDirectory.getAbsolutePath());
	    message.append("> for method <");
	    message.append(simpleClassName);
	    message.append(".");
	    message.append(testMethodName);
	    message.append("(..)>.");
	    logger.debug(message.toString());
	}
    }

    public void prepareTestInstance(TestContext testContext) throws Exception {
	// no implementation here.
    }

    /**
     * Return the current test directory initialized as part of a test case
     * execution.
     * 
     * @return the current test directory.
     */
    public static File getCurrentTestDirectory() {
	return testDirectory;
    }

    /**
     * Set whether test directories should be deleted after execution of a test
     * method. Default is false, which is directory is ignored after execution.
     * 
     * @param deletion
     *            Flag which decides whether test directories should be deleted
     *            after execution of a test method.
     */
    public static void setTestDirectoryDeletion(boolean deletion) {
	deleteDirectories = deletion;
    }

    public void afterTestClass(TestContext testContext) throws Exception {
	// no implementation
    }

    public void beforeTestClass(TestContext testContext) throws Exception {
	// no implementation
    }
}
