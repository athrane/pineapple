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

package com.alpha.pineapple.execution.process;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Executes an external process in the operating system.
 */
@Deprecated
public interface ProcessRunner {

	/**
	 * Root directory where the process is run.
	 * 
	 * @param processDirectory
	 *            Root directory where the process is run.
	 */
	void setDirectory(File processDirectory);

	/**
	 * Set the root execution result object for the process runner.
	 * 
	 * @param result
	 *            The root execution result object.
	 */
	public void setExecutionResult(ExecutionResult result);

	/**
	 * Execute an external process in the operating system.
	 * 
	 * @param description
	 *            Human readable description of the process which should be
	 *            executed.
	 * @param argumentList
	 *            list of arguments to the process.
	 * @param environment
	 *            Map of environment settings for the process runner.
	 * 
	 * @return Execution result which contains information describing the the result
	 *         of the execution of the external process, including the provided
	 *         description.
	 */
	ExecutionResult execute(String description, List<String> argumentList, Map<String, String> environment);
}
