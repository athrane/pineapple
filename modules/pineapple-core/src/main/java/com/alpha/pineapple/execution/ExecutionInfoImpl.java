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

package com.alpha.pineapple.execution;

import com.alpha.pineapple.module.ModuleInfo;

/**
 * Implementation of the {@link ExecutionInfo} interface.
 */
public class ExecutionInfoImpl implements ExecutionInfo {

	ModuleInfo moduleInfo;
	String environment;
	String operation;
	ExecutionResult result;

	public ExecutionInfoImpl(ModuleInfo moduleInfo, String environment, String operation, ExecutionResult result) {
		super();
		this.moduleInfo = moduleInfo;
		this.environment = environment;
		this.operation = operation;
		this.result = result;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getOperation() {
		return operation;
	}

	public ExecutionResult getResult() {
		return result;
	}

}
