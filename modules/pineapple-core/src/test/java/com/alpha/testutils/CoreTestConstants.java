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

package com.alpha.testutils;

import java.io.File;

/**
 * Definition of constants used in the test cases.
 */
public class CoreTestConstants {

	/**
	 * Credential identifier, used in unit tests.
	 */
	public static final String credentialIdOne = "credential-1";

	/**
	 * Credential identifier, used in unit tests.
	 */
	public static final String credentialIdTwo = "credential-2";

	/**
	 * The target environment used in unit tests.
	 */
	public static String targetEnvironment = "localdomain";

	/**
	 * The environment used in unit tests.
	 */
	public static String environmentIdOne = "environment-1";

	/**
	 * Deployment directory where the unit test expects the Pineapple modules to be
	 * located.
	 */
	public static String runtimeRootDir2 = "c:/deployments";

	/**
	 * Deployment directory where the unit test expects the Pineapple modules to be
	 * located.
	 */
	public static File runtimeRootDir = new File(runtimeRootDir2);

	/**
	 * Name of Pineapple module used in unit tests.
	 */
	public static String moduleName = "test-application-1.0.16";

	/**
	 * Resource identifier for Test-Resource used in unit tests.
	 */
	public static final String resourceIdentifierTestResource = "test-resource-resourceidentifier";

}
