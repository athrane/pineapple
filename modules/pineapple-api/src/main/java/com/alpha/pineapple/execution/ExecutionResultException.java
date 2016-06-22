/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Exception class for signaling an error during execution of a
 * {@linkplain ExecutionResult}.
 */
public class ExecutionResultException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4848432043385964993L;

    /**
     * Execution result which caused the exception.
     */
    ExecutionResult result;

    /**
     * ExecutionResultException constructor.
     * 
     * @param result
     *            execution result whose execution caused the error.
     */
    public ExecutionResultException(ExecutionResult result) {
	super(resolveErrorMessage(result));
	this.result = result;
    }

    /**
     * Return the execution result which caused the error.
     * 
     * @return the execution result which caused the error.
     */
    public ExecutionResult getResult() {
	return result;
    }

    /**
     * Result error message from error message in execution result.
     * 
     * @param result
     *            execution result from message is resolved.
     * 
     * @return
     */
    static String resolveErrorMessage(ExecutionResult result) {
	if (result == null)
	    return "N/A. No result defined.";
	Map<String, String> messages = result.getMessages();
	if (result.isError())
	    return messages.get(ExecutionResult.MSG_ERROR_MESSAGE);
	return messages.get(ExecutionResult.MSG_MESSAGE);

    }

}
