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

package com.alpha.pineapple.plugin;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.session.Session;

/**
 * Interface for single management operation which is implemented by a plugin.
 */
public interface Operation {

    /**
     * Execute operation implemented by a plugin.
     * 
     * @param content
     *            Unmarshalled module model which is used as input by the
     *            operation..
     * @param session
     *            connection session object which provides access to the
     *            resource which managed by the plugin.
     * @param result
     *            Execution result object which is used to collection
     *            information about the outcome of the execution of the
     *            operation and any part of it.
     * 
     * @throws PluginExecutionFailedException
     *             If execution of the operation fails.
     */
    public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException;

}
