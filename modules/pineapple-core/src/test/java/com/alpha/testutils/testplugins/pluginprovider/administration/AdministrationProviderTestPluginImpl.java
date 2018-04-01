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

package com.alpha.testutils.testplugins.pluginprovider.administration;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.alpha.pineapple.admin.AdministrationProvider;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.Plugin;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.session.Session;

/**
 * Plugin class for test plugin which accesses the administration provider and
 * reports its state.
 */
@Plugin()
@PluginOperation(AdministrationProviderTestPluginImpl.OPERATION)
public class AdministrationProviderTestPluginImpl implements Operation {
	public static final String OPERATION = "no-operation";
	public static final String PLUGIN_ID = "com.alpha.testutils.testplugins.pluginprovider.administration";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	@Resource
	AdministrationProvider coreAdministrationProvider;

	@Override
	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {

		// test
		result.addMessage("toString", ReflectionToStringBuilder.toString(coreAdministrationProvider));

		// compute execution state from children
		result.setState(ExecutionState.SUCCESS);
	}

}
