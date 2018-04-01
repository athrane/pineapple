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

package com.alpha.pineapple.execution;

import com.alpha.pineapple.module.ModuleInfo;

/**
 * Repository which contains the results of executing operations and implements
 * the role of subject in the observer pattern where interested parties can
 * register themselves for notification when operations are executed.
 */
public interface ResultRepository extends ExecutionResultFactory {

	/**
	 * Start new execution of operation on and module.
	 * 
	 * Creates, stores and returns a {@link ExecutionInfo} object which contains the
	 * passed arguments and is initialized with a new {@link ExecutonResult} whose
	 * state is running.
	 * 
	 * The execution info object is initialized with the modules directory that the
	 * repository is previously configured with. If the modules directory isn't
	 * configured then an exception is thrown.
	 * 
	 * @param moduleInfo
	 *            module info object describing the executing module.
	 * @param environment
	 *            Environment where the module is executing in.
	 * @param operation
	 *            Operation which is executed on the module.
	 * 
	 * @return returns an initialized execution info object which contains a new
	 *         {@link ExecutonResult} whose state is running.
	 */
	ExecutionInfo startExecution(ModuleInfo moduleInfo, String environment, String operation);

	/**
	 * Start composite execution of operation on and module. The composite execution
	 * is attached to existing execution.
	 * 
	 * Creates, stores and returns a {@link ExecutionInfo} object which contains the
	 * passed arguments and is initialized with a new {@link ExecutonResult} whose
	 * state is running.
	 * 
	 * The execution info object is initialized with the modules directory that the
	 * repository is previously configured with. If the modules directory isn't
	 * configured then an exception is thrown.
	 * 
	 * @param moduleInfo
	 *            module info object describing the executing module.
	 * @param environment
	 *            Environment where the module is executing in.
	 * @param operation
	 *            Operation which is executed on the module.
	 * @param description
	 *            description of the composite execution.
	 * @param result
	 *            root result of existing execution that the composite execution is
	 *            attached to.
	 * 
	 * @return returns an initialized execution info object which contains a new
	 *         {@link ExecutonResult} whose state is running.
	 */
	ExecutionInfo startCompositeExecution(ModuleInfo moduleInfo, String environment, String operation,
			String description, ExecutionResult result);

	/**
	 * Notifies repository that a execution result object has changed its state.
	 * 
	 * @param result
	 *            Execution result object which changed its state.
	 */
	void notifyOfResultStateChange(ExecutionResult result);

	/**
	 * Register execution result listener. The listener is notified during execution
	 * of operation about how they proceeds.
	 * 
	 * @param listener
	 *            Execution result listener which is notified during execution of
	 *            operations.
	 * 
	 * @throws IllegalArgumentException
	 *             if listener is null.
	 */
	void addListener(ResultListener listener);

	/**
	 * Unregister execution result listener.
	 * 
	 * @param listener
	 *            Registered execution result listener.
	 */
	void removeListener(ResultListener listener);

	/**
	 * Return array of registered result listeners.
	 * 
	 * @return array of registered result listeners.
	 */
	ResultListener[] getListeners();

	/**
	 * Get result notification sequence where execution results are sorted in the
	 * order of which they are created.
	 * 
	 * The result sequence starts at index 0.
	 * 
	 * @param executionInfo
	 *            execution info object which represents a running or past
	 *            execution.
	 * @param firstIndex
	 *            first index of execution result included in sub sequence.
	 * @param lastIndex
	 *            last index of execution result excluded in sub sequence.
	 * 
	 *            Querying with indexes 0..0 returns the empty sequence. Querying
	 *            with indexes 0..1 returns a single result.
	 * 
	 * @return sequence of execution result notifications sorted in the order of
	 *         which they are created.
	 * 
	 * @throws ExecutionInfoNotFoundException
	 *             if execution info is unknown.
	 * @throws IllegalArgumentException
	 *             if first index is less than zero.
	 * @throws IllegalArgumentException
	 *             if last index is less than zero.
	 * @throws IllegalArgumentException
	 *             if last index is less than first index.
	 * @throws IllegalArgumentException
	 *             if last index is larger than current index.
	 */
	ExecutionResultNotification[] getResultSequence(ExecutionInfo executionInfo, int firstIndex, int lastIndex)
			throws ExecutionInfoNotFoundException;

	/**
	 * Get the current (maximum) index used store the last result for execution.
	 * 
	 * @param executionInfo
	 *            info to get index information for.
	 * 
	 * @return the current (maximum) index used store the last result for execution.
	 * 
	 * @throws ExecutionInfoNotFoundException
	 *             if execution info is unknown.
	 */
	int getCurrentResultIndex(ExecutionInfo executionInfo) throws ExecutionInfoNotFoundException;

}
