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

import java.util.Map;

import com.alpha.pineapple.execution.continuation.ContinuationPolicy;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Defines result of execution of a operation or any part of it.
 */
public interface ExecutionResult {

    /**
     * Defines the execution state of a execution result.
     */
    enum ExecutionState {
	COMPUTED, // completed execution state is computed from children
	SUCCESS, // completed execution, execution was successful
	FAILURE, // completed execution, execution resulted in failure
	ERROR, // completed execution, execution encountered an unexpected error
	EXECUTING, // execution is in progress
	INTERRUPTED // completed execution, execution was interrupted (either
		    // cancelled of aborted due to failure)
    }

    /**
     * Default message id, to post messages.
     */
    public final String MSG_MESSAGE = "Message";

    /**
     * Default message id, to post error messages.
     */
    public final String MSG_ERROR_MESSAGE = "Error Message";

    /**
     * Default message id, to post stack traces with errors.
     */
    public final String MSG_STACKTRACE = "Stack Trace";

    /**
     * Default message id, to post composite execution results.
     */
    public final String MSG_COMPOSITE = "Composite Execution Result";

    /**
     * Default message id, to post messages.
     */
    public final String MSG_DESCRIPTION = "Description";

    /**
     * Default message id, to post report id.
     */
    public final String MSG_REPORT = "Report";

    /**
     * Returns true if result is root object.
     * 
     * @return true if result is root object.
     */
    public boolean isRoot();

    /**
     * Returns parent result. If result is root object then null is returned.
     * 
     * @return parent result. If result is root object then null is returned.
     */
    public ExecutionResult getParent();

    /**
     * Adds child result.
     * 
     * @param description
     *            description of result which is added to result.
     * 
     * @return Added result object with supplied description and in running
     *         state.
     */
    public ExecutionResult addChild(String description);

    /**
     * Returns all child result objects. If object has no children then an empty
     * array is returned.
     * 
     * @return all child result objects. If object has no children then an empty
     *         array is returned.
     */
    public ExecutionResult[] getChildren();

    /**
     * Set description of what is executing.
     * 
     * @param description
     *            of what is executing.
     */
    public void setDescription(String description);

    /**
     * Get description of what is executing.
     * 
     * @return description of what is executing.
     */
    public String getDescription();

    /**
     * Get continuation policy.
     * 
     * @return continuation policy for execution result.
     */
    public ContinuationPolicy getContinuationPolicy();

    /**
     * Set cancellation directive.
     * 
     * Used to signal that the execution should be interrupted (through
     * cancellation). Will update the cancellation state on the continuation
     * policy.
     * 
     * The state of the result (and children) will not be updated untilexplict
     * set or a child is created.
     */
    public void setCancelled();

    /**
     * Set the state of the execution result.
     * 
     * @param the
     *            state of the execution result.
     */
    public void setState(ExecutionState state);

    /**
     * Get the state of the execution result.
     * 
     * @return the state of the execution result.
     */
    public ExecutionState getState();

    /**
     * Returns true if the state of the execution result is in executing state.
     * 
     * @return true if the state of the execution result is in executing state.
     */
    public boolean isExecuting();

    /**
     * Returns true if the state of the execution result is success.
     * 
     * @return true if the state of the execution result is success.
     */
    public boolean isSuccess();

    /**
     * Returns true if the state of the execution result is an error.
     * 
     * @return true if the state of the execution result is error.
     */
    public boolean isError();

    /**
     * Returns true if the state of the execution result is an failure.
     * 
     * @return true if the state of the execution result is failure.
     */
    public boolean isFailed();

    /**
     * Returns true if the state of the execution result is interrupted.
     * 
     * @return true if the state of the execution result is interrupted.
     */
    public boolean isInterrupted();

    /**
     * Get the time that the operation took to execute. If the execution isn't
     * completed yet, then the current running time is returned.
     * 
     * @return the time that the operation took to execute. If the execution
     *         isn't completed yet, then the current running time is returned.
     */
    public long getTime();

    /**
     * Get the start time when the operation started to execute.
     * 
     * @return the start time when the operation started to execute.
     */
    public long getStartTime();

    /**
     * Add detailed message about the outcome of the execution in a human
     * readable form.
     * 
     * If a message with the key already exists then the new message is appended
     * to the existing message separated by a line separator.
     * 
     * @param key
     *            Id of the detailed message.
     * @param message
     *            the content of the detailed message.
     */
    public void addMessage(String id, String message);

    /**
     * Replace message about the outcome of the execution in a human readable
     * form.
     * 
     * If a message with the key doesn't exists then the new message is added.
     * 
     * @param key
     *            Id of the detailed message.
     * @param message
     *            the content of the detailed message.
     */
    public void addOrReplaceMessage(String id, String message);

    /**
     * Get map of stored messages about the outcome of the execution.
     * 
     * @return map of stored messages about the outcome of the execution.
     */
    public Map<String, String> getMessages();

    /**
     * Returns all children which is registered with the requested execution
     * state. If the execution result has no children registered with the
     * requested state, then an empty array is returned.
     * 
     * @param state
     *            Execution state which is requested.
     * 
     * @return Returns all children which is registered with the requested
     *         execution state.
     */
    public ExecutionResult[] getChildrenWithState(ExecutionState state);

    /**
     * Return first child. If the execution result has no children registered
     * then null is returned.
     * 
     * @return first child.
     */
    public ExecutionResult getFirstChild();

    /**
     * Return the number of children.
     * 
     * @return the number of children.
     */
    public int getNumberOfChildren();
    
    /**
     * Returns root result of execution.
     * 
     * @param result
     *            result for which the root result is returned.
     * 
     * @return root result of execution.
     */
    public ExecutionResult getRootResult();

    /**
     * Set the state as successful and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     * 
     * @param args
     *            Array of arguments that will be filled in for params within
     *            the message (params look like "{0}", "{1,date}", "{2,time}"
     *            within a message), or null if none.
     */
    public void completeAsSuccessful(MessageProvider messageProvider, String key, Object[] args);

    /**
     * Set the state as successful and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     * 
     */
    public void completeAsSuccessful(MessageProvider messageProvider, String key);

    /**
     * Set the state as failure and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     * 
     * @param args
     *            Array of arguments that will be filled in for params within
     *            the message (params look like "{0}", "{1,date}", "{2,time}"
     *            within a message), or null if none.
     */
    public void completeAsFailure(MessageProvider messageProvider, String key, Object[] args);

    /**
     * Set the state as failure and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     * 
     */
    public void completeAsFailure(MessageProvider messageProvider, String key);

    /**
     * Set the state as error and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message' and 'Error Message'. A stack trace is extracted from the
     * exception and stored under the id 'StackTrace'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     * 
     * @param args
     *            Array of arguments that will be filled in for params within
     *            the message (params look like "{0}", "{1,date}", "{2,time}"
     *            within a message), or null if none.
     * 
     * @param exception
     *            Exception from which the stack trace is extracted.
     */
    public void completeAsError(MessageProvider messageProvider, String key, Object[] args, Exception e);

    /**
     * Set the state as error and add a message which is read from the exception
     * description The message is stored under the id 'Message' and 'Error
     * Message'. A stack trace is extracted from the exception and stored under
     * the id 'StackTrace'.
     * 
     * @param exception
     *            Exception from which the description and stack trace is
     *            extracted.
     */
    public void completeAsError(Exception e);

    /**
     * Set the state as error and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'. A stack trace is extracted from the exception and stored under
     * the id 'StackTrace'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     * 
     * @param exception
     *            Exception from which the stack trace is extracted.
     */
    public void completeAsError(MessageProvider messageProvider, String key, Exception e);

    /**
     * Set the state as computed and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'. A message which details the results of the children is stored
     * under the id 'Composite execution result'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     * 
     * @param args
     *            Array of arguments that will be filled in for params within
     *            the message (params look like "{0}", "{1,date}", "{2,time}"
     *            within a message), or null if none.
     */
    public void completeAsComputed(MessageProvider messageProvider, String key, Object[] args);

    /**
     * Set the state as computed and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'. A message which details the results of the children is stored
     * under the id 'Composite execution result'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     */
    public void completeAsComputed(MessageProvider messageProvider, String key);

    /**
     * Set the state as computed and add a message which is looked up from the
     * message provider object. The looked up message is stored under the id
     * 'Message'. A message which details the results of the children is stored
     * under the id 'Composite execution result'.
     * 
     * If the failed message is resolved, then two additional arguments will be
     * inserted at the start of the <code>failedArgs</code> array. The first
     * argument will contain the number of failed children and the second
     * argument will contain the number children with error. If the failed
     * message is defined as
     * <code>XYZ failed, because [{0}] child executions failed and 
     * [{1}] child executions terminated with an error.</code> then the values
     * in argument 0 and 1 will be resolved by the method.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param successfulKey
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. The key is only used to lookup a
     *            message if the state is computed to successful
     * 
     * @param failedKey
     *            The key to lookup up in the message source, such as
     *            'calculator.noRateSet'. The key is used to lookup a message if
     *            the state is computed to failure or error.
     * 
     * @param successfulArgs
     *            Array of arguments that will be filled in for params within
     *            the successful message (params look like "{0}", "{1,date}",
     *            "{2,time}" within a message), or null if none.
     * 
     * @param failedArgs
     *            Array of arguments that will be filled in for params within
     *            the failed message (params look like "{0}", "{1,date}",
     *            "{2,time}" within a message), or null if none.
     */
    public void completeAsComputed(MessageProvider messageProvider, String successfulKey, Object[] successfulArgs,
	    String failedKey, Object[] failedArgs);

    /**
     * Set the state as interrupted and add a message which is looked up from
     * the message provider object. The looked up message is stored under the id
     * 'Message'. A message which details the results of the children is stored
     * under the id 'Composite execution result'.
     * 
     * @param messageProvider
     *            The message provider used to look up the message.
     * 
     * @param key
     *            The key to lookup up in the message provider, such as
     *            'calculator.noRateSet'. Users of this class are encouraged to
     *            base message names on the relevant fully qualified class name,
     *            thus avoiding conflict and ensuring maximum clarity.
     */
    public void completeAsInterrupted(MessageProvider messageProvider, String key);

}
