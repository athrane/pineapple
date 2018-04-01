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

package com.alpha.pineapple.plugin.ssh.utils;

import java.io.File;

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Common functionality for SSH.
 */
public interface SshHelper {

	/**
	 * Asserts whether the local file path points to a valid file.
	 * 
	 * The result of the of the assertion is added as an execution result to current
	 * execution result object graph.
	 * 
	 * @param localFile
	 *            Path to local file.
	 * @param result
	 *            ExecutionResult result where the assertion result is added as a
	 *            child result.
	 * 
	 * @return True if the installer exists as an file.
	 */
	public boolean isLocalFileValid(File localFile, ExecutionResult result);

	/**
	 * Resolve module path.
	 * 
	 * @param result
	 *            Execution result used to look up execution info.
	 * 
	 * @param localFile
	 *            Unresolved local file path.
	 * 
	 * @return Resolved local file path where 'modulepath' prefix is resolved to
	 *         physical module directory.
	 */
	public String resolveModulePath(ExecutionResult result, String localFile);
}
