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

package com.alpha.testutils;

/**
 * SSH Constants for test cases.
 */
public interface SshTestConstants {

	/**
	 * Integration test profile for SSH plugin.
	 */
	public static final String SSHPLUGIN_INTTEST_PROFILE = "ssh-plugin-integration-test";

	/**
	 * Port on the SSH test server (vagrant VM).
	 */
	public static final int TESTSERVER_PORT = 22;

	/**
	 * Host name of SSH test server (vagrant VM).
	 */
	public static final String TESTSERVER_IP = "192.168.100.10";

	/**
	 * User to access the SSH test server (vagrant VM).
	 */
	public static final String TESTSERVER_USER = "vagrant";

	/**
	 * Password to access the SSH test server (vagrant VM).
	 */
	public static final String TESTSERVER_PWD = "vagrant";

	/**
	 * shared guest Linux directory.
	 */
	static final String SHARED_GUEST_LINUX_DIR = "/" + TestUtilsTestConstants.TEST_DEFAULT_DIRECTORY;

	/**
	 * Test Default connect timeout (in ms).
	 */
	public static final int TEST_TIMEOUT = 10000;

}
