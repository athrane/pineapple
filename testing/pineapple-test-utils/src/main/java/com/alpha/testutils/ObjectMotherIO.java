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

import static com.alpha.testutils.TestUtilsTestConstants.TEST_DEFAULT_DIRECTORY;
import static com.alpha.testutils.TestUtilsTestConstants.TEST_RUNTIME_ROOT_DIRECTORY;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Implementation of the ObjectMother pattern, provides IO operations for unit
 * test.
 */
public class ObjectMotherIO {

	/**
	 * Logger object.
	 */
	static Logger logger = Logger.getLogger(ObjectMotherIO.class.getName());

	/**
	 * ObjectMotherIO constructor.
	 */
	private ObjectMotherIO() {
	}

	/**
	 * Create test method directory name. The created name is:
	 * "&lt;java.io.tmpdir&gt;/testcases/&lt;testMethodName&gt;". The name is
	 * created using the constants defined by the UnittestConstants class.
	 * 
	 * @param testMethodName
	 *            Name of the test method.
	 * 
	 * @return Path to the the local test method directory.
	 */
	public static String createTestMethodDirectoryName(String testMethodName) {
		String name;
		name = new StringBuffer().append(TEST_RUNTIME_ROOT_DIRECTORY).append(File.separatorChar)
				.append(TEST_DEFAULT_DIRECTORY).append(File.separatorChar).append(testMethodName).toString();
		return name;
	}

	/**
	 * Create local test method directory.
	 * 
	 * @param testMethodName
	 *            Name of the test method.
	 * @return File object defining the local test method directory. The name of the
	 *         test method directory is
	 *         "&lt;java.io.tmpdir&gt;/testcases/&lt;testMethodName&gt;"
	 */
	public static File createTestMethodDirectory(String testMethodName) {
		String directoryName;
		directoryName = createTestMethodDirectoryName(testMethodName);
		File directory;
		directory = new File(directoryName);

		// validate whether directory already exists
		if (directory.exists()) {
			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("The directory <");
			message.append(directory);
			message.append("> already exists, will delete it.");
			logger.debug(message.toString());

			// delete directory
			deleteDirectory(directory);
		}

		// create dir
		directory.mkdirs();

		// validate directory is created
		if (directory.exists()) {
			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("Successfully created the directory: ");
			message.append(directory);
			logger.debug(message.toString());
		} else {
			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("Failed to create the directory: ");
			message.append(directory);
			logger.debug(message.toString());

			// fail test
			fail("Directory creation failed: " + directory);
		}

		return directory;
	}

	/**
	 * Deletes all files and sub directories under directory. Returns true if all
	 * deletions were successful.If a deletion fails, the method stops attempting to
	 * delete and returns false.
	 * 
	 * @param directory
	 *            The directory which should be deleted.
	 * @return true if all deletions were successful.
	 */
	private static boolean deleteDirectoryRecursive(File directory) {

		if (directory.isDirectory()) {
			String[] children = directory.list();
			for (int i = 0; i < children.length; i++) {
				boolean success;
				File nextFile = new File(directory, children[i]);
				success = deleteDirectoryRecursive(nextFile);
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return directory.delete();
	}

	/**
	 * Deletes all files and sub directories under directory. Returns true if all
	 * deletions were successful.If a deletion fails, the method stops attempting to
	 * delete and returns false.
	 * 
	 * @param directory
	 *            The directory which should be deleted.
	 * @return true if all deletions were successful.
	 */
	public static void deleteDirectory(File directory) {
		boolean isDeleted;
		isDeleted = deleteDirectoryRecursive(directory);

		// validate whether deletion is successful
		if (isDeleted) {
			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("Succesfully deleted the directory: ");
			message.append(directory);
			logger.debug(message.toString());
		} else {
			// log debug message
			StringBuilder message = new StringBuilder();
			message.append("Failed to delete the directory: ");
			message.append(directory);
			logger.debug(message.toString());

			// fail test
			fail("Directory deletion failed:" + directory);
		}
	}

	/**
	 * Create sub directory.
	 * 
	 * @param rootDir
	 *            Root directory where the sub directory should be created.
	 * @param subDirName
	 *            Name of the sub directory.
	 * 
	 * @return File object defining the sub directory.
	 */
	public static File createSubDirectory(File rootDir, String subDirName) {

		// create directory name
		StringBuilder dirName;
		dirName = new StringBuilder();
		dirName.append(rootDir.getAbsolutePath());
		dirName.append(File.separatorChar);
		dirName.append(subDirName);

		// create sub directory
		File subDir;
		subDir = new File(dirName.toString());
		subDir.mkdirs();

		// log debug message
		StringBuilder message = new StringBuilder();
		message.append("Successfully created the directory: ");
		message.append(subDir);
		logger.debug(message.toString());

		return subDir;
	}

}
