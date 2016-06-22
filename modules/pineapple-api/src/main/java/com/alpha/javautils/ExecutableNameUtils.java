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

package com.alpha.javautils;

import java.io.File;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * A Helper class for resolving executable names.
 */
public class ExecutableNameUtils {

    /**
     * Java bin directory.
     */
    static final String BIN_DIR = "bin";

    /**
     * Java executable.
     */
    static final String JAVA_EXECUTABLE = "java";

    /**
     * Java executable with extension
     */
    static final String WIN_JAVA_EXECUTABLE = "java.exe";

    /**
     * Resolve OS specific name for Java executable without extension.
     * 
     * @param parent
     *            Parent directory.
     * 
     * @return OS specific Java executable name without extension.
     */
    static public File resolveJavaExecutable(File parent) {

	if (SystemUtils.IS_OS_WINDOWS) {
	    File jvmExecutableDir = new File(parent, BIN_DIR);
	    return new File(jvmExecutableDir, JAVA_EXECUTABLE);
	}

	if (SystemUtils.IS_OS_LINUX) {
	    File jvmExecutableDir = new File(parent, BIN_DIR);
	    return new File(jvmExecutableDir, JAVA_EXECUTABLE);
	}

	throw new NotImplementedException("Java executable not supported for this OS");
    }

    /**
     * Resolve OS specific name for Java executable with extension.
     * 
     * @param parent
     *            Parent directory.
     * 
     * @return OS specific Java executable name without extension.
     */
    static public File resolveJavaExecutableWithExtension(File parent) {

	if (SystemUtils.IS_OS_WINDOWS) {
	    File jvmExecutableDir = new File(parent, BIN_DIR);
	    return new File(jvmExecutableDir, WIN_JAVA_EXECUTABLE);
	}

	if (SystemUtils.IS_OS_LINUX) {
	    File jvmExecutableDir = new File(parent, BIN_DIR);
	    return new File(jvmExecutableDir, JAVA_EXECUTABLE);
	}

	throw new NotImplementedException("Java executable not supported for this OS");
    }

    /**
     * if Executable contains spaces then add """
     * 
     * @param executable
     *            Executable path.
     * 
     * @return Escaped Executable path.
     */
    static public File escapeExecutable(File executable) {
	return new File(escapeExecutable(executable.toString()));
    }

    /**
     * if Executable contains spaces then add """
     * 
     * @param executable
     *            Executable path.
     * 
     * @return Escaped Executable path.
     */
    static public String escapeExecutable(String executable) {

	// if executable contains spaces then add """
	if (StringUtils.contains(executable, ' ')) {
	    return "\"" + executable + "\"";
	}

	return executable;
    }

    /**
     * if path contains spaces then add """
     * 
     * @param path
     *            Path.
     * 
     * @return Escaped path.
     */
    static public String escapePath(String path) {

	// if path contains spaces then add """
	return escapeExecutable(path);
    }

    /**
     * if Executable contains spaces then add "'"
     * 
     * @param executable
     *            Executable path.
     * 
     * @return Escaped Executable path.
     */
    static public String escapeExecutableWithSingleQuotes(String executable) {

	// if executable contains spaces then add """
	if (StringUtils.contains(executable, ' ')) {
	    return "'" + executable + "'";
	}

	return executable;
    }

}
