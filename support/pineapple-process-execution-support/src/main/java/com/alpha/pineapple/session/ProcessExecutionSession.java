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

package com.alpha.pineapple.session;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.session.Session;

/**
 * Session which provides access to external processes in the operating system.
 */
public interface ProcessExecutionSession extends Session {
	/**
	 * Returns true if resource property is defined.
	 * 
	 * @param name
	 *            Property name.
	 * 
	 * @return true if resource property is defined.
	 */
	boolean isResourcePropertyDefined(String name);

	/**
	 * Get property from resource.
	 * 
	 * @param name
	 *            Property name.
	 * 
	 * @return property from resource.
	 * 
	 * @throws ResourceException
	 *             If property isn't defined.
	 */
	String getResourceProperty(String name) throws ResourceException;

	/**
	 * Execute an external process.
	 * 
	 * @param executable
	 *            The executable to run in the process.
	 * @param arguments
	 *            List of argument to the process.
	 * @param timeout
	 *            Timeout in milliseconds before the external process is terminated.
	 * @param description
	 *            Human readable description which is added to the execution result.
	 * @param parent
	 *            Parent execution result from operation.
	 */
	void execute(String executable, String[] arguments, long timeout, String description, ExecutionResult parent);

	/**
	 * Execute an external process. The external process will be ternimated after
	 * 500 milliseconds of it isn't completed before.
	 * 
	 * @param executable
	 *            The executable to run in the process.
	 * @param arguments
	 *            List of argument to the process.
	 * @param description
	 *            Human readable description which is added to the execution result.
	 * @param parent
	 *            Parent execution result from operation.
	 */
	void execute(String executable, String[] arguments, String description, ExecutionResult parent);

}
