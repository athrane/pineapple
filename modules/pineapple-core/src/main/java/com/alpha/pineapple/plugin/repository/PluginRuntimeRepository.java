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

package com.alpha.pineapple.plugin.repository;

import org.springframework.oxm.Unmarshaller;

import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.session.Session;

/**
 * Internal interface for plugin repository which exposes methods for runtime
 * execution of plugins.
 */
public interface PluginRuntimeRepository extends PluginRepository {
	/**
	 * Get plugin unmarshaller from repository.
	 * 
	 * @param plugin
	 *            Plugin id.
	 * 
	 * @return plugin unmarshaller from repository.
	 * 
	 * @throws PluginExecutionFailedException
	 *             If getting unmarshaller fails.
	 */
	public Unmarshaller getPluginUnmarshaller(String plugin) throws PluginExecutionFailedException;

	/**
	 * Get operation for plugin.
	 * 
	 * @param plugin
	 *            plugin id.
	 * @param operation
	 *            operation id.
	 * 
	 * @return operation for plugin.
	 */
	public Operation getOperation(String plugin, String operation);

	/**
	 * Get unconnected session for plugin.
	 * 
	 * @param pluginId
	 *            plugin id.
	 * 
	 * @return unconnected session for plugin.
	 */
	public Session getSession(String pluginId);

	/**
	 * Get plugin info for plugin.
	 * 
	 * @param pluginId
	 *            plugin id.
	 * 
	 * @return plugin info for plugin.
	 * 
	 * @throws PluginNotFoundException
	 *             if no plugin is defined with plugin ID.
	 */
	public PluginInfo getPluginInfo(String pluginId) throws PluginNotFoundException;

}
