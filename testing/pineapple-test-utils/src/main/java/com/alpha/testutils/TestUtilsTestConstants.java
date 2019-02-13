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

package com.alpha.testutils;

import java.io.File;

/**
 * Definition of constants used in the test cases.
 */
public class TestUtilsTestConstants {
	/**
	 * Runtime directory used by unit test to store data.
	 */
	public static final String TEST_RUNTIME_ROOT_DIRECTORY = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();

	/**
	 * Default directory for directory based unit tests.
	 */
	public static final String TEST_DEFAULT_DIRECTORY = "testcases";

	/**
	 * Plugin id for hello world plugin.
	 */
	public static final String pluginIdHelloWorld = "com.alpha.pineapple.plugin.helloworld";

	/**
	 * Resource id for Test-Resource used in unit tests.
	 */
	public static final String resourceIdTestResource = "test-resource-resource-id";

	/**
	 * Plugin id for Test-Resource used in unit tests.
	 */
	public static final String pluginIdTestResource = "com.alpha.pineapple.plugin.test";

	/**
	 * Credential id, used to access the Test-Resource in the unit tests.
	 */
	public static final String credentialIdTestResource = "test-resource-credential-id";

	/**
	 * The operation supported by the test plugin in the
	 * com.alpha.pineapple.plugin.test package.
	 */
	public static final String helloWorldOperation = "hello-world";

	/**
	 * File name for credentials file used in unit tests.
	 */
	public static final String credentialsFileName = "credentials.xml";

	/**
	 * Credential identifier, used in unit tests.
	 */
	public static final String credentialIdOne = "credential-1";

	/**
	 * Credential identifier, used in unit tests.
	 */
	public static final String credentialIdTwo = "credential-2";

	/**
	 * The environment used in unit tests.
	 */
	public static final String environmentIdOne = "environment-1";

	/**
	 * Test-Resource user.
	 */
	public static final String userTestResource = "test-user";

	/**
	 * Test-Resource password.
	 */
	public static final String passwordTestResource = "test-password";

	/**
	 * File name for credentials file used in unit tests.
	 */
	public static final String resourcesFileName = "resources.xml";

}
