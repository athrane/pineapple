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

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Default implementation of the {@linkplain ContinuationPolicy} interface.
 */
public class DefaultContinuationPolicyImpl implements ContinuationPolicy {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Continue-on-failure directive.
     */
    Boolean continueOnFailure = null;

    /**
     * Cancellation state.
     */
    boolean isCancelled = false;

    /**
     * Failure result.
     */
    ExecutionResult failedResult;

    /**
     * DefaultContinuationPolicyImpl constructor.
     */
    DefaultContinuationPolicyImpl() {
    }

    @Override
    public boolean continueExecution() {
	if (isCancelled)
	    return false;
	if (isContinueOnFailure())
	    return true;
	return (!isFailed());
    }

    @Override
    public boolean isContinueOnFailure() {
	if (continueOnFailure == null)
	    return true;
	return continueOnFailure.booleanValue();
    }

    @Override
    public boolean isCancelled() {
	return isCancelled;
    }

    /**
     * Returns true if execution is failed.
     * 
     * @return if execution is failed.
     */
    boolean isFailed() {
	return (failedResult != null);
    }

    @Override
    public void disableContinueOnFailure() {
	if (continueOnFailure != null)
	    return;
	continueOnFailure = new Boolean(false);
    }

    @Override
    public void enableContinueOnFailure() {
	if (continueOnFailure != null)
	    return;
	continueOnFailure = new Boolean(true);
    }

    @Override
    public void setCancelled() {
	isCancelled = true;
    }

    @Override
    public void setFailed(ExecutionResult result) {
	Validate.notNull(result, "result is undefined");
	if (failedResult != null)
	    return;
	failedResult = result;
    }

    @Override
    public ExecutionResult getFailedResult() {
	return failedResult;
    }

    /**
     * Factory method for creation of policy instances.
     * 
     * @return policy instances.
     */
    public static ContinuationPolicy getInstance() {
	return new DefaultContinuationPolicyImpl();
    }
}
