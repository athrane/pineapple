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

package com.alpha.testutils.testplugins.infiniteloop.coop;

import org.apache.log4j.Logger;

import com.alpha.javautils.ConcurrencyUtils;
import com.alpha.pineapple.OperationNames;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.plugin.Operation;
import com.alpha.pineapple.plugin.Plugin;
import com.alpha.pineapple.plugin.PluginExecutionFailedException;
import com.alpha.pineapple.plugin.PluginOperation;
import com.alpha.pineapple.session.Session;

/**
 * Plugin class for test plugin which executes in infinite loop. The loop
 * co-operatively queries whether the execution has been cancelled.
 * 
 * Used to test cancellation of execution.
 */
@Plugin()
@PluginOperation(OperationNames.WILDCARD_OPERATION)
public class CooperativeInfiniteLoopPluginImpl implements Operation {
	public static final String PLUGIN_ID = "com.alpha.testutils.testplugins.infiniteloop.coop";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	public void execute(Object content, Session session, ExecutionResult result) throws PluginExecutionFailedException {
		try {
			// infinite loop
			while (true) {

				// co-operatively test for cancellation
				if (!result.getContinuationPolicy().continueExecution()) {
					result.setState(ExecutionState.INTERRUPTED);
					return;
				}

				logger.debug("Will wait for 100 ms.");
				ConcurrencyUtils.waitSomeMilliseconds(100);
			}
		} catch (InterruptedException e) {
			result.setState(ExecutionState.ERROR);
			throw new PluginExecutionFailedException("Plugin execution was interrupted due to: " + e.getMessage(), e);
		}
	}

}
