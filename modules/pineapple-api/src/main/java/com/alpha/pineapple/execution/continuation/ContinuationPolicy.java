/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.execution.continuation;

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Interface for continuation policy for executions.
 * 
 * A policy directs whether an execution defined by a tree of execution results
 * should continue or stop due a directives.
 * 
 * A policy can be controlled by a user initiated cancellation and the
 * directive: Continue-on-failure directive defined in a model.
 * 
 * The initial state for a policy instance is: 1) Cancelled = false 2)
 * Continue-on-failure = true
 * 
 * The interface defines methods for modifying the directives. The
 * Continue-on-failure directive will be set based on the value of the used
 * model.
 */
public interface ContinuationPolicy {

	/**
	 * Returns true if execution should continue.
	 * 
	 * @return true if execution should continue.
	 */
	boolean continueExecution();

	/**
	 * Returns the current state of the Continue-on-failure directive.
	 * 
	 * @return the current state of the Continue-on-failure directive.
	 */
	boolean isContinueOnFailure();

	/**
	 * Returns the current state of the cancellation state.
	 * 
	 * @return the current state of the cancellation state.
	 */
	boolean isCancelled();

	/**
	 * Disable the Continue-on-failure directive.
	 * 
	 * Used to signal that execution should stop if a failure or error is
	 * encountered. Once set the directive can't be changed. A failure is registered
	 * using the setFailed() method.
	 */
	void disableContinueOnFailure();

	/**
	 * Enable the Continue-on-failure directive.
	 * 
	 * Used to signal that execution should continue if a failure or error is
	 * encountered. Once set the directive can't be changed.
	 */
	void enableContinueOnFailure();

	/**
	 * Set failure.
	 * 
	 * Used to signal that failure or error has occurred in execution result tree.
	 * 
	 * @param result
	 *            result that contains failure or error.
	 */
	void setFailed(ExecutionResult result);

	/**
	 * Returns the failed result if registered. Otherwise null is returned.
	 * 
	 * @return failed result if registered. Otherwise null is returned.
	 */
	ExecutionResult getFailedResult();

	/**
	 * Set cancellation directive.
	 * 
	 * Used to signal that execution should be stopped due to a user initiated
	 * cancellation.
	 */
	void setCancelled();
}
