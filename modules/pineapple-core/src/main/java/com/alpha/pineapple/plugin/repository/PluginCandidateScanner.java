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

import org.springframework.context.ApplicationContext;

import com.alpha.pineapple.plugin.PluginInitializationFailedException;

/**
 * Scanner which scan for plugin candidates in packages.
 */
public interface PluginCandidateScanner {

    /**
     * Do component scan for plugin classes. Prior to component scan the class
     * loader to used to resolve all plugin id's. Otherwise the component
     * scanner can't find to plugin classes.
     * 
     * @param pluginIds
     *            Array of plugin id.
     * 
     * @return ApplicationContext which contains found plugins.
     * 
     * @throws PluginInitializationFailedException
     *             If plugin scan fails.
     */
    ApplicationContext scanForPlugins(String[] pluginIds) throws PluginInitializationFailedException;

}
