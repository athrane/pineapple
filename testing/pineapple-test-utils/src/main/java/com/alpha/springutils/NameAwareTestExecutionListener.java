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

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Spring test execution listener which provides access to the name of the
 * current test method executing.
 */
public class NameAwareTestExecutionListener implements TestExecutionListener {

	/**
	 * Logger object.
	 */
	static Logger logger = Logger.getLogger(NameAwareTestExecutionListener.class.getName());

	/**
	 * Name of the currently execution test method.
	 */
	static String testMethodName;

	public void afterTestMethod(TestContext testContext) throws Exception {
		testMethodName = "";
	}

	public void beforeTestMethod(TestContext testContext) throws Exception {

		// get the current test method
		Method testMethod = testContext.getTestMethod();

		// get simple name
		testMethodName = testMethod.getName();
	}

	public void prepareTestInstance(TestContext testContext) throws Exception {
		// no implementation here.
	}

	/**
	 * Return the name of current test method executing. This method will only
	 * return a method during execution of a test method after execution or if this
	 * method is invoke will no test running then the method will return "".
	 * 
	 * @return the name of the current test method executing.
	 */
	public static String getCurrentTestMethodName() {
		return testMethodName;
	}

	public void afterTestClass(TestContext testContext) throws Exception {
		// no implementation
	}

	public void beforeTestClass(TestContext testContext) throws Exception {
		// no implementation
	}

}
