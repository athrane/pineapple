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
import static com.alpha.javautils.ArgumentUtils.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.pineapple.execution.continuation.ContinuationPolicy;
import com.alpha.pineapple.execution.continuation.DefaultContinuationPolicyImpl;
import com.alpha.pineapple.execution.continuation.InterruptedExecutionException;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Implementation of the <code>ExecutionResult</code> interface.
 */
public class ExecutionResultImpl implements ExecutionResult {

	/**
	 * Number of arguments which is added to message source argument list if the
	 * computed state is failed.
	 */
	static final int NUMBER_FAILED_ARGS = 2;

	/**
	 * First child result.
	 */
	static final int FIRST_INDEX = 0;

	/**
	 * Fields which are excluded from toString.
	 */
	static final String[] excludeToStrings = new String[] { "repository", "parent", "children" };

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Result repository
	 */
	ResultRepository repository;

	/**
	 * List of children
	 */
	ArrayList<ExecutionResult> children;

	/**
	 * Correlation ID.
	 */
	int correlationId;

	/**
	 * Description of what is executing.
	 */
	String description;

	/**
	 * Parent result.
	 */
	ExecutionResult parent;

	/**
	 * Continuation policy.
	 */
	ContinuationPolicy policy;

	/**
	 * Execution state.
	 */
	ExecutionState state = ExecutionState.EXECUTING;

	/**
	 * Stop watch for timing execution time.
	 */
	StopWatch watch;

	/**
	 * Flag to determine whether watch is running.
	 */
	boolean isWatchRunning;

	/**
	 * Map of detailed messages.
	 */
	HashMap<String, String> detailMessages;

	/**
	 * ExecutionResultImpl constructor. Execution state is initially running.
	 * 
	 * @param repository
	 *            result repository.
	 * @param parent
	 *            parent execution result.
	 * @param description
	 *            description of what is executing.
	 * @param policy
	 *            continuation policy.
	 */
	ExecutionResultImpl(ResultRepository repository, ExecutionResult parent, String description,
			ContinuationPolicy policy) {

		super();

		// validate parameters
		notNull(description, "description is undefined");
		notNull(policy, "policy is undefined");

		// set attributes
		this.correlationId = hashCode();
		this.children = new ArrayList<ExecutionResult>();
		this.repository = repository;
		this.parent = parent;
		this.description = description;
		this.policy = policy;
		this.watch = new StopWatch();
		this.detailMessages = new HashMap<String, String>();

		// start timing
		watch.start();
		isWatchRunning = true;

		// trigger event at repository
		notifyRepository();
	}

	/**
	 * ExecutionResultImpl constructor. Execution state is initially running.
	 * 
	 * @param repository
	 *            result repository.
	 * @param parent
	 *            parent execution result.
	 * @param description
	 *            description of what is executing.
	 */
	public ExecutionResultImpl(ResultRepository repository, ExecutionResult parent, String description) {
		this(repository, null, description, DefaultContinuationPolicyImpl.getInstance());
	}

	/**
	 * ExecutionResultImpl constructor. Execution state is initially running.
	 * 
	 * @param repository
	 *            result repository.
	 * @param description
	 *            description of what is executing.
	 */
	public ExecutionResultImpl(ResultRepository repository, String description) {
		this(repository, null, description);
	}

	/**
	 * ExecutionResultImpl constructor. Execution state is initially running.
	 * 
	 * @param description
	 *            description of what is executing.
	 */
	public ExecutionResultImpl(String description) {
		this(null, null, description);
	}

	/**
	 * Notify repository of result state change.
	 */
	void notifyRepository() {

		// exit if repository is undefined.
		if (repository == null)
			return;

		// notify the repository
		repository.notifyOfResultStateChange(this);
	}

	public ExecutionResult addChild(String description) {
		enforceContinuationPolicy();

		// create child
		ExecutionResultImpl child = new ExecutionResultImpl(repository, this, description, policy);

		// store child
		this.children.add(child);

		return child;
	}

	public ExecutionResult[] getChildren() {
		return children.toArray(new ExecutionResult[children.size()]);
	}

	@Override
	public int getNumberOfChildren() {
		return children.size();
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public ContinuationPolicy getContinuationPolicy() {
		return this.policy;
	}

	public ExecutionResult getParent() {
		return this.parent;
	}

	public ExecutionState getState() {
		return this.state;
	}

	public boolean isExecuting() {
		return (getState() == ExecutionState.EXECUTING);
	}

	public boolean isSuccess() {
		return (getState() == ExecutionState.SUCCESS);
	}

	@Override
	public boolean isFailed() {
		return (getState() == ExecutionState.FAILURE);
	}

	public boolean isError() {
		return (getState() == ExecutionState.ERROR);
	}

	public boolean isInterrupted() {
		return (getState() == ExecutionState.INTERRUPTED);
	}

	public boolean isRoot() {
		return (parent == null);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setCancelled() {
		policy.setCancelled();
	}

	public void setState(ExecutionState state) {

		// capture current state
		ExecutionState currentState = this.state;

		// set state
		this.state = state;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder debugMessage = new StringBuilder();
			debugMessage.append("State is set to [");
			debugMessage.append(this.state);
			debugMessage.append("] for [");
			debugMessage.append(description);
			debugMessage.append("].");
			logger.debug(debugMessage.toString());
		}

		// if not running then compute the final result
		if (state != ExecutionState.EXECUTING) {

			// stop watch if its is running
			if (isWatchRunning) {
				watch.stop();
				isWatchRunning = false;
			}
		}

		// compute state from child results
		if (state == ExecutionState.COMPUTED) {
			computeStatus(currentState);
		}

		// update continuation policy
		notifyPolicy();

		// notify the repository
		notifyRepository();

		// update parent
		notifyParent();
	}

	/**
	 * Update continuation policy with state change on this result.
	 */
	void notifyPolicy() {
		if (policy == null)
			return;

		// exit if execution was a success
		if (isSuccess())
			return;

		// set failed
		policy.setFailed(this);
	}

	/**
	 * Update parent result with state change on this result.
	 */
	void notifyParent() {
		if (parent == null)
			return;

		// exit if parent is still running
		if (parent.isExecuting())
			return;

		// update parent
		parent.setState(ExecutionState.COMPUTED);
	}

	/**
	 * Compute the final status of the result
	 * 
	 * @param currentState
	 *            Current state prior to update.
	 */
	void computeStatus(ExecutionState currentState) {

		// define counters
		int numberFailed = 0;
		int numberSuccessful = 0;
		int numberError = 0;
		int numberInterrupted = 0;

		// get number of results
		int numberResults = children.size();

		// iterate over the results
		for (ExecutionResult childResult : children) {

			switch (childResult.getState()) {
			case SUCCESS:
				numberSuccessful++;
				break;
			case FAILURE:
				numberFailed++;
				break;
			case ERROR:
				numberError++;
				break;
			case EXECUTING:
				forceChildError(childResult);
				numberError++;
				break;
			case INTERRUPTED:
				numberInterrupted++;
				break;
			default:
				numberError++;
				break;
			}
		}

		// create summary message
		StringBuilder message = new StringBuilder();
		message.append("Results: ");
		message.append(numberResults);
		message.append(", successful: ");
		message.append(numberSuccessful);
		message.append(", failures: ");
		message.append(numberFailed);
		message.append(", errors: ");
		message.append(numberError);
		message.append(", interrupted: ");
		message.append(numberInterrupted);
		message.append(".");

		// add or replace message
		addOrReplaceMessage(MSG_COMPOSITE, message.toString());

		// if current state was error then keep that
		if (currentState == ExecutionState.ERROR) {
			this.state = ExecutionState.ERROR;
			return;
		}

		// if any error occurred then propagate the composite state
		if (numberError > 0) {
			this.state = ExecutionState.ERROR;
			logDebugMessage();
			return;
		}

		// if current state was interrupted then keep that
		if (currentState == ExecutionState.INTERRUPTED) {
			this.state = ExecutionState.INTERRUPTED;
			return;
		}

		// if any error occurred then propagate the composite state
		if (numberInterrupted > 0) {
			this.state = ExecutionState.INTERRUPTED;
			logDebugMessage();
			return;
		}

		// if current state was failure then keep that
		if (currentState == ExecutionState.FAILURE) {
			this.state = ExecutionState.FAILURE;
			return;
		}

		// if any failure occurred then propagate the composite state
		if (numberFailed > 0) {
			this.state = ExecutionState.FAILURE;
			logDebugMessage();
			return;
		}

		// finally set state as successful
		this.state = ExecutionState.SUCCESS;
		logDebugMessage();
		return;
	}

	/**
	 * Force a child result with running state to terminate with an error.
	 * 
	 * @param childResult
	 */
	void forceChildError(ExecutionResult childResult) {

		// add messages
		childResult.addMessage(MSG_MESSAGE, "State is forced to error due to state not being set explicit.");
		childResult.addMessage(MSG_STACKTRACE, "n/a");

		// set as error
		childResult.setState(ExecutionState.ERROR);
	}

	public long getTime() {
		return watch.getTime();
	}

	public long getStartTime() {
		return watch.getStartTime();
	}

	public void addMessage(String id, String message) {

		// if message doesn't exist then create it
		if (!detailMessages.containsKey(id)) {

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder debugMessage = new StringBuilder();
				debugMessage.append("Message added with id [");
				debugMessage.append(id);
				debugMessage.append("] and content [");
				debugMessage.append(message);
				debugMessage.append("].");
				logger.debug(debugMessage.toString());
			}

			this.detailMessages.put(id, message);
			return;
		}

		// append to existing message

		// get message
		String existingMessage = detailMessages.get(id);

		// append
		StringBuilder newMessage = new StringBuilder();
		newMessage.append(existingMessage);
		newMessage.append(SystemUtils.LINE_SEPARATOR);
		newMessage.append(message);

		// store message
		this.detailMessages.put(id, newMessage.toString());

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder debugMessage = new StringBuilder();
			debugMessage.append("Updated Message with id [");
			debugMessage.append(id);
			debugMessage.append("] and new content [");
			debugMessage.append(message);
			debugMessage.append("].");
			logger.debug(debugMessage.toString());
		}

	}

	/**
	 * Replace existing message. If message doesn't exist then it is added.
	 * 
	 * @param id
	 *            message ID.
	 * @param message
	 *            message.
	 */
	public void addOrReplaceMessage(String id, String message) {

		// if message doesn't exist then create it
		if (!detailMessages.containsKey(id)) {
			addMessage(id, message);
			return;
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder debugMessage = new StringBuilder();
			debugMessage.append("Message replaced with id [");
			debugMessage.append(id);
			debugMessage.append("] and content [");
			debugMessage.append(message);
			debugMessage.append("].");
			logger.debug(debugMessage.toString());
		}

		this.detailMessages.put(id, message);
		return;
	}

	public Map<String, String> getMessages() {
		return Collections.unmodifiableMap(this.detailMessages);
	}

	public ExecutionResult[] getChildrenWithState(ExecutionState state) {

		// create container
		ArrayList<ExecutionResult> foundChildren = new ArrayList<ExecutionResult>();

		// iterate over the children
		for (ExecutionResult child : children) {

			// store found child
			if (child.getState() == state) {
				foundChildren.add(child);
			}
		}

		// return as array
		return foundChildren.toArray(new ExecutionResult[foundChildren.size()]);
	}

	@Override
	public ExecutionResult getFirstChild() {
		if (children.isEmpty())
			return null;
		return children.get(FIRST_INDEX);
	}

	@Override
	public ExecutionResult getRootResult() {
		return getRootResult(this);
	}

	/**
	 * Returns root result of execution.
	 * 
	 * @param result
	 *            result for which the root result is returned.
	 * 
	 * @return root result of execution.
	 */
	ExecutionResult getRootResult(ExecutionResult result) {
		if (result.isRoot())
			return result;
		return getRootResult(result.getParent());
	}

	public void completeAsComputed(MessageProvider messageProvider, String key, Object[] args) {
		String message = messageProvider.getMessage(key, args);

		// store the message and set as computed
		addMessage(MSG_MESSAGE, message);
		setState(ExecutionState.COMPUTED);
	}

	public void completeAsComputed(MessageProvider messageProvider, String key) {
		String message = messageProvider.getMessage(key);

		// store the message and set as computed
		addMessage(MSG_MESSAGE, message);
		setState(ExecutionState.COMPUTED);
	}

	public void completeAsComputed(MessageProvider messageProvider, String successfulKey, Object[] successfulArgs,
			String failedKey, Object[] failedArgs) {

		// set state as compute
		setState(ExecutionState.COMPUTED);

		// handle the successful case
		if (getState() == ExecutionState.SUCCESS) {

			// create message
			String message = messageProvider.getMessage(successfulKey, successfulArgs);

			// store the message and set as computed
			addMessage(MSG_MESSAGE, message);
			return;
		}

		// create extended arguments array for failed messages
		Object[] args;
		if (failedArgs == null) {
			args = new Object[NUMBER_FAILED_ARGS];
		} else {
			args = new Object[failedArgs.length + NUMBER_FAILED_ARGS];
		}

		// create message for failed case, insert values for failed and errors
		// test as first arguments
		ExecutionResult[] failedTests = getChildrenWithState(ExecutionState.FAILURE);
		ExecutionResult[] errorTests = getChildrenWithState(ExecutionState.ERROR);
		args[0] = failedTests.length;
		args[1] = errorTests.length;
		String message = messageProvider.getMessage(failedKey, args);

		// store the message and set as computed
		addMessage(MSG_MESSAGE, message);
	}

	public void completeAsError(MessageProvider messageProvider, String key, Object[] args, Exception e) {
		String message = messageProvider.getMessage(key, args);

		// store the messages and set state as error
		addMessage(MSG_ERROR_MESSAGE, message);
		addMessage(MSG_STACKTRACE, StackTraceHelper.getStrackTrace(e));
		setState(ExecutionState.ERROR);
	}

	public void completeAsError(MessageProvider messageProvider, String key, Exception e) {
		String message = messageProvider.getMessage(key);

		// store the messages and set state as error
		addMessage(MSG_ERROR_MESSAGE, message);
		addMessage(MSG_STACKTRACE, StackTraceHelper.getStrackTrace(e));
		setState(ExecutionState.ERROR);
	}

	@Override
	public void completeAsError(Exception e) {
		String message = e.getMessage();

		// store the messages and set state as error
		addMessage(MSG_ERROR_MESSAGE, message);
		addMessage(MSG_STACKTRACE, StackTraceHelper.getStrackTrace(e));
		setState(ExecutionState.ERROR);
	}

	public void completeAsFailure(MessageProvider messageProvider, String key, Object[] args) {
		String message = messageProvider.getMessage(key, args);

		// store the message and set as failed
		addMessage(MSG_ERROR_MESSAGE, message);
		setState(ExecutionState.FAILURE);
	}

	public void completeAsFailure(MessageProvider messageProvider, String key) {
		String message = messageProvider.getMessage(key);

		// store the message and set as failed
		addMessage(MSG_ERROR_MESSAGE, message);
		setState(ExecutionState.FAILURE);
	}

	public void completeAsSuccessful(MessageProvider messageProvider, String key, Object[] args) {
		String message = messageProvider.getMessage(key, args);

		// store the message and set as success
		addMessage(MSG_MESSAGE, message);
		setState(ExecutionState.SUCCESS);
	}

	public void completeAsSuccessful(MessageProvider messageProvider, String key) {
		String message = messageProvider.getMessage(key);

		// store the message and set as success
		addMessage(MSG_MESSAGE, message);
		setState(ExecutionState.SUCCESS);
	}

	@Override
	public void completeAsInterrupted(MessageProvider messageProvider, String key) {
		String message = messageProvider.getMessage(key);

		// store the message and set as interrupted
		addMessage(MSG_MESSAGE, message);
		setState(ExecutionState.INTERRUPTED);
	}

	/**
	 * Log state computation log message.
	 */
	void logDebugMessage() {
		if (logger.isDebugEnabled()) {
			String debugMessage = new StringBuilder().append("State is computed to [").append(this.state).append("].")
					.toString();
			logger.debug(debugMessage);
		}
	}

	/**
	 * Enforce the continuation policy.
	 * 
	 * @throws InterruptedExecutionException
	 *             if continuation policy signals that execution should be
	 *             interrupted.
	 */
	void enforceContinuationPolicy() {
		if (policy.continueExecution())
			return;

		// set interrupted state
		setState(ExecutionState.INTERRUPTED);

		String message = null;
		if (policy.isCancelled()) {
			message = new StringBuilder().append("Execution is interrupted due to cancellation.").toString();

		} else {
			message = new StringBuilder().append("Execution is interrupted due to failure in result [")
					.append(policy.getFailedResult().getDescription()).append("].").toString();
		}

		throw new InterruptedExecutionException(message);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toStringExclude(this, excludeToStrings);
	}

}
