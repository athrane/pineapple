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

import com.alpha.pineapple.plugin.PluginInitializationFailedException;

/**
 * Initialize a plugin candidate into a ready-to-use plugin.
 */
public interface PluginInitializer {

	/**
	 * Initialize plugin candidate into plugin with all meta data ready for usage.
	 * 
	 * @param pluginClass
	 *            Plugin class for plugin candidate.
	 * 
	 * @return plugin info which contains fully initialized plugin.
	 * 
	 * @throws PluginInitializationFailedException
	 *             If initialization of the plugin fails.
	 */
	PluginInfo initializePlugin(Object pluginClass) throws PluginInitializationFailedException;
}
