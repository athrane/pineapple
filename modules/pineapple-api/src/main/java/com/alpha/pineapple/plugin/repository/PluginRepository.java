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

package com.alpha.pineapple.plugin.repository;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.plugin.PluginInitializationFailedException;

/**
 * Query interface for plugin repository which provides methods for registration
 * of plugins and querying about plugins.
 */
public interface PluginRepository {

	/**
	 * Initialization of the repository before is ready for general usage. The
	 * repository will only scan and initialize plugins which are defined in the
	 * collection of id's which is supplied as argument to the method.
	 * 
	 * @param executionResult
	 *            A child execution result will be added to this result. The child
	 *            result will reflects the outcome of the initialization. If the
	 *            initialization fails the state of the result object will be
	 *            failure or error and will contain a stack trace message.
	 * @param pluginIds
	 *            plugin id's for all registered resources in all environments.
	 *            resources. The collection of plugin id's which the repository will
	 *            initialize.
	 * 
	 * @throws PluginInitializationFailedException
	 *             if repository initialization fails.
	 */
	public void initialize(ExecutionResult executionResult, String[] pluginIds)
			throws PluginInitializationFailedException;

	/**
	 * Initialization of a single plugin.
	 * 
	 * @param executionResult
	 *            A child execution result will be added to this result. The child
	 *            result will reflects the outcome of the initialization. If the
	 *            initialization fails the state of the result object will be
	 *            failure or error and will contain a stack trace message.
	 * @param pluginId
	 *            plugin id which the repository will initialize.
	 * 
	 * @throws PluginInitializationFailedException
	 *             if repository fails to initialize plugin.
	 */
	public void initializePlugin(ExecutionResult executionResult, String pluginId)
			throws PluginInitializationFailedException;

}
