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

package com.alpha.pineapple.execution;

import static com.alpha.pineapple.CoreConstants.EXECUTION_HISTORY_CAPACITY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.module.ModuleInfo;

/**
 * Implementation of the {@link ResultRepository} interface.
 */
public class ResultRepositoryImpl implements ResultRepository {

	/**
	 * First list entry in result sequence.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * List of result listeners, Which support removal of listeners during
	 * iteration.
	 */
	List<ResultListener> listeners;

	/**
	 * Collection of current and past executions.
	 * 
	 * Entries are stored by the string representation if the root result.
	 */
	LinkedHashMap<String, List<ExecutionResultNotification>> executionMap;

	/**
	 * Execution result factory
	 */
	@Resource
	ExecutionResultFactory executionResultFactory;

	/**
	 * ResultRepositoryImpl constructor.
	 */
	public ResultRepositoryImpl() {
		super();
		this.listeners = new CopyOnWriteArrayList<ResultListener>();

		/**
		 * Linked hash map which only has limited capacity.
		 */
		this.executionMap = new LinkedHashMap<String, List<ExecutionResultNotification>>(EXECUTION_HISTORY_CAPACITY) {

			/**
			 * Serial Version UID.
			 */
			private static final long serialVersionUID = 386757772945555733L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<String, List<ExecutionResultNotification>> eldest) {
				// exit if capacity isn't exceeded
				if (size() < EXECUTION_HISTORY_CAPACITY)
					return false;

				// exit if sequence is empty - execution isn't started yet
				List<ExecutionResultNotification> resultSequence = eldest.getValue();
				if (resultSequence.isEmpty())
					return false;

				// only remove eldest if it completed execution
				ExecutionResultNotification firstNotification = resultSequence.get(FIRST_INDEX);
				ExecutionResult firstResult = firstNotification.getResult();
				ExecutionResult rootResult = firstResult.getRootResult();
				if (rootResult.isExecuting())
					return false;

				// remove if execution is completed and capacity is exceeded
				return size() > EXECUTION_HISTORY_CAPACITY;
			}
		};
	}

	public ExecutionInfo startExecution(ModuleInfo moduleInfo, String environment, String operation) {

		// create execution result description
		Object[] args = { operation, moduleInfo.getId(), environment };
		String description = messageProvider.getMessage("rri.start_operation_info", args);

		// create execution result object for operation
		ExecutionResult rootResult = startExecution(description);

		// create execution info
		ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, environment, operation, rootResult);

		return executionInfo;
	}

	@Override
	public ExecutionInfo startCompositeExecution(ModuleInfo moduleInfo, String environment, String operation,
			String description, ExecutionResult result) {
		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { description };
			logger.debug(messageProvider.getMessage("rri.start_info", args));
		}

		// create execution result object for composite operation
		ExecutionResult childResult = result.addChild(description);

		// create execution info
		ExecutionInfo executionInfo = new ExecutionInfoImpl(moduleInfo, environment, operation, childResult);

		return executionInfo;
	}

	public ExecutionResult startExecution(String description) {
		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { description };
			logger.debug(messageProvider.getMessage("rri.start_info", args));
		}

		// create result
		ExecutionResultImpl result = new ExecutionResultImpl(this, description);

		return result;
	}

	public void notifyOfResultStateChange(ExecutionResult result) {

		// validate parameter
		Validate.notNull(result, "result is undefined.");

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { result.getState(), result.getDescription() };
			logger.debug(messageProvider.getMessage("rri.notify_info", args));
		}

		// create notification for event capture
		ExecutionState state = result.getState();
		ExecutionResultNotification notification = ExecutionResultNotificationImpl.getInstance(result, state);

		// capture event for queries
		captureEvent(notification);

		// iterate over the listeners and notify them
		for (ResultListener listener : listeners) {
			listener.notify(notification);
		}
	}

	public void addListener(ResultListener listener) {

		// validate parameter
		Validate.notNull(listener, "listener is undefined.");

		// exit if repository contains listener
		if (this.listeners.contains(listener))
			return;

		// register listener
		this.listeners.add(listener);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { listener };
			logger.debug(messageProvider.getMessage("rri.add_listener_info", args));
		}
	}

	public void removeListener(ResultListener listener) {

		// validate parameter
		Validate.notNull(listener, "listener is undefined.");

		// exit if repository doesn't contain listener.
		if (!this.listeners.contains(listener))
			return;

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { listener };
			logger.debug(messageProvider.getMessage("rri.remove_listener_info", args));
		}

		// remove the listener
		this.listeners.remove(listener);
	}

	public ResultListener[] getListeners() {
		return listeners.toArray(new ResultListener[listeners.size()]);
	}

	public ExecutionResultNotification[] getResultSequence(ExecutionInfo executionInfo, int firstIndex, int lastIndex)
			throws ExecutionInfoNotFoundException {

		// validate parameter
		Validate.notNull(executionInfo, "executionInfo is undefined.");
		Validate.isTrue(firstIndex >= 0, "firstIndex is less then zero. Legal values are 0....max-number-of-results");
		Validate.isTrue(lastIndex >= 0, "lastIndex is less then zero. Legal values are 0....max-number-of-results");
		Validate.isTrue(lastIndex >= firstIndex,
				"lastIndex larger the firstIndex. The first index must be smaller of equal to the last index.");

		// resolve root result
		ExecutionResult rootResult = executionInfo.getResult();
		String rootResultId = getResultId(rootResult);

		// throw exception if if no execution exists
		if (!executionMap.containsKey(rootResultId)) {
			Object[] args = { rootResultId };
			String message = messageProvider.getMessage("rri.getresultsequence_notfound_failure", args);
			throw new ExecutionInfoNotFoundException(message);
		}

		// get sequence
		List<ExecutionResultNotification> resultSequence = executionMap.get(rootResultId);

		// throw exception if last index is larger than current index
		if (lastIndex > resultSequence.size()) {
			String message = messageProvider.getMessage("rri.illegalindex_failure");
			throw new IllegalArgumentException(message);
		}

		// create sub sequence

		synchronized (resultSequence) {
			List<ExecutionResultNotification> subSequence = resultSequence.subList(firstIndex, lastIndex);
			return subSequence.toArray(new ExecutionResultNotification[subSequence.size()]);
		}
	}

	@Override
	public int getCurrentResultIndex(ExecutionInfo executionInfo) throws ExecutionInfoNotFoundException {

		// validate parameter
		Validate.notNull(executionInfo, "executionInfo is undefined.");

		// resolve root result
		ExecutionResult rootResult = executionInfo.getResult();
		String rootResultId = getResultId(rootResult);

		// throw exception if if no execution exists
		if (!executionMap.containsKey(rootResultId)) {
			Object[] args = { rootResultId };
			String message = messageProvider.getMessage("rri.getresultsequenceindex_notfound_failure", args);
			throw new ExecutionInfoNotFoundException(message);
		}

		// return sequence size
		List<ExecutionResultNotification> resultSequence = executionMap.get(rootResultId);

		// returns last exclusive index
		return resultSequence.size();
	}

	/**
	 * Captures received execution result notification. The notification is appended
	 * to event sequence for the execution.
	 * 
	 * If result is a root result in a new execution then a new sequence is created
	 * (and an old one is discarded if the collection capacity is reached).
	 * 
	 * @param result
	 *            execution result notification which is captured and added to event
	 *            sequence for the execution.
	 */
	void captureEvent(ExecutionResultNotification notification) {
		ExecutionResult result = notification.getResult();
		ExecutionResult rootResult = result.getRootResult();
		String rootId = getResultId(rootResult);

		// add notification if sequence exists
		if (!executionMap.containsKey(rootId)) {
			List<ExecutionResultNotification> resultSequence = Collections
					.synchronizedList(new ArrayList<ExecutionResultNotification>());
			executionMap.put(getResultId(notification), resultSequence);
		}

		// add notification
		List<ExecutionResultNotification> resultSequence = executionMap.get(getResultId(rootResult));
		resultSequence.add(notification);
	}

	/**
	 * Calculate result ID.
	 * 
	 * The result is calculated as a string representation of the hash code.
	 * 
	 * @param result
	 *            result for which the Id is calculated.
	 * 
	 * @return result ID.
	 */
	String getResultId(ExecutionResult result) {
		return Integer.toString(result.hashCode());
	}

	/**
	 * Calculate result ID.
	 * 
	 * The result is calculated as a string representation of the hash code.
	 * 
	 * @param notification
	 *            notification which contains the result for which the ID is
	 *            calculated.
	 * 
	 * @return result ID.
	 */
	String getResultId(ExecutionResultNotification notification) {
		return getResultId(notification.getResult());
	}

	@Override
	public ExecutionResultNotification createNotification(ExecutionResult result, ExecutionState state) {
		return executionResultFactory.createNotification(result, state);
	}

}
