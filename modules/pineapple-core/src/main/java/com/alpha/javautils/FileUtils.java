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

package com.alpha.javautils;

import static com.alpha.javautils.ArgumentUtils.notNull;
import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.File;
import java.util.Properties;

import javax.annotation.Resource;

import com.alpha.pineapple.i18n.MessageProvider;

/**
 * <p>
 * Helper class for handling files and directories.
 * </p>
 *
 * <p>
 * Implementation note: Please notice that this class can't be refactored to the
 * pineapple-api project because its unit test requires disk access which is
 * implemented by the {@linkplain DirectoryTestExecutionListener} test executor
 * located in the pineapple-testutils project. The pineapple-api project can't
 * have a dependency to pineapple-testutils because the pineapple-testutils
 * already have a dependency to the pineapple-api project.
 * </p>
 */
public class FileUtils {

	/**
	 * Windows Users directory.
	 */
	public static final String USERS_DIR = "Users";

	/**
	 * Search string for 8.3 format directories.
	 */
	static final String TILDE_STR = "~";

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Java System properties.
	 */
	@Resource
	Properties systemProperties;

	/**
	 * Java system utilities.
	 */
	@Resource
	SystemUtils systemUtils;

	/**
	 * Convert Windows 8.3 DOS formated path to long representation (e.g. all ~1
	 * path elements are converted into the full name).
	 * 
	 * If the OS isn't windows then the original path is returned unchanged.
	 * 
	 * If the path doesn't represent a directory (but a file) then then the original
	 * path is returned unchanged.
	 * 
	 * Only paths with ~1 is supported. If paths with ~2+ is encountered then an
	 * exception is thrown.
	 * 
	 * @param path path which is converted.
	 * 
	 * @return converted path.
	 * 
	 * @throws ConversionFailedException If conversion fails.
	 */
	@Deprecated
	public File convertToLongWindowsPath(File path) throws ConversionFailedException {

		// if OS isn't windows then exit
		if (!systemUtils.isWindowsOperatingSystem(systemProperties))
			return path;

		// if path is a file then exit
		if (path.isFile())
			return path;

		// if path doesn't contain ~ then convert parent and exit
		if (!(path.getName().contains(TILDE_STR))) {

			// if parent is undefined then return path
			if (path.getParentFile() == null)
				return path;

			// convert parent
			File convertedParent = convertToLongWindowsPath(path.getParentFile());

			return new File(convertedParent, path.getName());
		}

		// get ~n number
		int index = path.getName().indexOf(TILDE_STR);
		char number = path.getName().charAt(index + 1);

		// throw exception if name contains ~2+, currently only ~1 is supported.
		if (number != '1') {
			Object[] args = { path.getAbsolutePath() };
			String message = messageProvider.getMessage("fu.get_convertpath_error2", args);
			throw new ConversionFailedException(message);
		}

		// get path substring
		String childSubString = path.getName().substring(0, index - 1).toLowerCase();

		// get parent
		File parent = path.getParentFile();

		// get files
		File[] files = parent.listFiles();

		// iterate over the files
		for (File fileObject : files) {

			// skip iteration if object isn't directory
			if (!fileObject.isDirectory()) {
				continue;
			}

			// match directory names
			String name = fileObject.getName().toLowerCase();
			if (name.startsWith(childSubString)) {

				// convert parent
				File convertedParent = convertToLongWindowsPath(parent);

				return new File(convertedParent, fileObject.getName());
			}
		}

		// throw exception
		Object[] args = { path.getAbsolutePath() };
		String message = messageProvider.getMessage("fu.get_convertpath_error", args);
		throw new ConversionFailedException(message);
	}

	/**
	 * Convert Windows 8.3 DOS formated path to long representation (e.g. all ~ path
	 * elements are converted into the full name).
	 * 
	 * If the OS isn't windows then the original path is returned unchanged. If the
	 * path doesn't represent a directory (but a file) then then the original path
	 * is returned unchanged.
	 * 
	 * @param path path which is converted.
	 * 
	 * @return converted path.
	 * 
	 * @throws ConversionFailedException If conversion fails.
	 */
	@Deprecated
	public File convertToLongWindowsPath(String path) throws ConversionFailedException {
		return convertToLongWindowsPath(new File(path));
	}

	/**
	 * Returns true if the path is located in the "Users" directory on Windows.
	 * 
	 * @param path     String which represents a path.
	 * @param userName User name from the Java system properties.
	 * 
	 * @return Returns true if the path is located in the "Users" directory on
	 *         Windows, by matching the pattern "\Users\USER", where USER is the
	 *         supplied user name.
	 */
	public boolean isPathInUsersDir(String path, String userName) {
		notEmpty(path, "path is empty");
		notNull(path, "path is undefined");
		notNull(userName, "user is undefined");

		// define expected pattern
		StringBuilder expected = new StringBuilder();
		expected.append(File.separatorChar);
		expected.append(USERS_DIR);
		expected.append(File.separatorChar);
		expected.append(userName);

		// validate if user.home is located in the "Users" directory
		return (path.contains(expected.toString()));
	}

}
