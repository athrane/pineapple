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

package com.alpha.testutils.testplugins.exception.channeled;

import com.alpha.pineapple.plugin.Plugin;
import com.alpha.pineapple.plugin.session.RetrySessionHandlerImpl;

/**
 * Plugin class for test plugin which throws channeled exception to simulate the
 * behavior of the {@linkplain RetrySessionHandlerImpl}.
 * 
 * The thrown exception is controlled by the operation name.
 */
@Plugin()
public class PluginImpl {

	public static final String PLUGIN_ID = "com.alpha.testutils.testplugins.exception.channeled";

}
