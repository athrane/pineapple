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

package com.alpha.pineapple.execution;

import com.alpha.pineapple.module.ModuleInfo;

/**
 * Captures information about the execution of an operation on a module.
 */
public interface ExecutionInfo {

	/**
	 * Returns module info for the executed module.
	 * 
	 * @return module info for the executed module.
	 */
	ModuleInfo getModuleInfo();

	/**
	 * Returns the environment where the module is executed in.
	 * 
	 * @return the environment where the module is executed in.
	 */
	String getEnvironment();

	/**
	 * Returns the operation which where executed on the module.
	 * 
	 * @return the operation which where executed on the module.
	 */
	String getOperation();

	/**
	 * Returns execution result which describes the outcome of the execution of the
	 * module.
	 * 
	 * @return execution result which describes the outcome of the execution of the
	 *         module.
	 */
	ExecutionResult getResult();
}
