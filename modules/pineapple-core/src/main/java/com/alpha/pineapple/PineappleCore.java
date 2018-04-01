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

package com.alpha.pineapple;

import com.alpha.pineapple.admin.Administration;
import com.alpha.pineapple.execution.ExecutionInfo;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ResultListener;

/**
 * Pineapple Core component interface. This is the main interface for Pineapple
 * core which defines the entry point for scheduling and execution of
 * operations.
 */
public interface PineappleCore {
	/**
	 * Execute operation asynchronously.
	 * 
	 * @param operation
	 *            Name of the operation which should be executed.
	 * @param environment
	 *            Environment for which the operation should be executed.
	 * @param module
	 *            Module id.
	 * 
	 * @return returns an initialized execution info object which contains a new
	 *         {@link ExecutonResult} which reflects the state of the executing
	 *         operation.
	 */
	ExecutionInfo executeOperation(String operation, String environment, String module);

	/**
	 * Cancel executing operation.
	 * 
	 * @param executionInfo
	 *            execution info for operation which should be cancelled.
	 */
	void cancelOperation(ExecutionInfo executionInfo);

	/**
	 * Register execution result listener. The listener is notified during execution
	 * of an operation about how it proceeds.
	 * 
	 * @param listener
	 *            Execution result listener which is notified during execution of
	 *            operations.
	 */
	void addListener(ResultListener listener);

	/**
	 * Register execution result listeners. A listener is notified during execution
	 * of an operation about how it operation proceeds.
	 * 
	 * @param listener
	 *            Execution result listener which is notified during execution of
	 *            operations.
	 */
	void addListeners(ResultListener[] listeners);

	/**
	 * Get set of registered execution result listeners.
	 * 
	 * @return listener set of registered execution result listeners.
	 */
	ResultListener[] getListeners();

	/**
	 * Unregister execution result listener. The listener is notified during
	 * execution of operation about how it proceeds.
	 * 
	 * @param listener
	 *            Execution result listener which is notified during execution of
	 *            operations.
	 */
	void removeListener(ResultListener listener);

	/**
	 * Get core component administration interface.
	 * 
	 * @return core component administration interface.
	 */
	Administration getAdministration();

	/**
	 * Get core component initialization info. The info is returned as a single
	 * string.
	 * 
	 * @return core component initialization info. If core component hasn't been
	 *         initialized a null message is returned.
	 */
	String getInitializationInfoAsString();

	/**
	 * Get core component initialization info. The info is returned as a execution
	 * result.
	 * 
	 * @return core component initialization info. If core component hasn't been
	 *         initialized then null is returned.
	 */
	ExecutionResult getInitializationInfo();

}
