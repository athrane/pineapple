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

package com.alpha.pineapple.execution;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.alpha.pineapple.CoreConstants;
import com.alpha.pineapple.i18n.MessageProvider;

/**
 * Default implementation of the {@linkplain OperationResolver} interface.
 */
public class DefaultOperationResolverImpl implements OperationResolver {

    /**
     * Message provider for I18N support.
     */
    @Resource
    MessageProvider messageProvider;

    @Override
    public boolean restrictOperationFromExecution(String operation, String targetOperation,
	    ExecutionResult rootResult) {

	// handle undefined target operation
	if ((targetOperation == null) || (targetOperation.isEmpty())) {
	    String header = messageProvider.getMessage("dor.resolve_target_operation_info");
	    String message = messageProvider.getMessage("dor.target_operation_notdefined");
	    rootResult.addMessage(header, message);
	    return false;
	}

	// trim
	targetOperation = targetOperation.trim();

	// resolve as list
	if (isList(targetOperation)) {
	    return restrictAsList(operation, targetOperation, rootResult);
	}

	// resolve as string
	return restrictFromString(operation, targetOperation, rootResult);
    }

    /**
     * Resolve operation from string.
     * 
     * @param operation
     *            current operation.
     * @param targetOperation
     *            target operation defined in module model.
     * @param rootResult
     *            Root execution result for operation.
     * 
     * @return true if operation shouldn't be executed.
     */
    boolean restrictFromString(String operation, String targetOperation, ExecutionResult rootResult) {
	String header = messageProvider.getMessage("dor.resolve_target_operation_info");

	// trim
	targetOperation = targetOperation.trim();

	// handle wildcard target operation
	if (targetOperation.equals(CoreConstants.WILDCARD_OPERATION)) {
	    String message = messageProvider.getMessage("dor.target_wildcard_operation");
	    rootResult.addMessage(header, message);
	    return false;
	}

	// resolve restriction
	if (targetOperation.equals(operation)) {
	    Object[] args = { targetOperation, operation };
	    String message = messageProvider.getMessage("dor.matched_target_operation", args);
	    rootResult.addMessage(header, message);
	    return false;
	}

	// handle restricted operation
	Object[] args = { targetOperation, operation };
	String message = messageProvider.getMessage("dor.match_target_operation_failure", args);
	rootResult.addMessage(header, message);
	return true;
    }

    /**
     * Return true if target operation defines a list.
     * 
     * @param targetOperation
     *            target operation to parse.
     * 
     * @return true if target operation defines a list.
     */
    boolean isList(String targetOperation) {
	if (!targetOperation.startsWith("{"))
	    return false;
	if (!targetOperation.endsWith("}"))
	    return false;
	return true;
    }

    /**
     * Parse target operation as a list.
     * 
     * @param operation
     *            current operation as list.
     * @param targetOperation
     *            target operation defined in module model.
     * @param rootResult
     *            Root execution result for operation.
     * 
     * @return true if operation shouldn't be executed.
     */
    boolean restrictAsList(String operation, String targetOperation, ExecutionResult rootResult) {
	String nestedString = StringUtils.substringBetween(targetOperation, "{", "}");

	// handle undefined target operation
	if ((nestedString == null) || (nestedString.isEmpty())) {
	    // handle restricted operation
	    Object[] args = { targetOperation, operation };
	    String header = messageProvider.getMessage("dor.resolve_target_operation_info");
	    String message = messageProvider.getMessage("dor.match_target_operation_failure", args);
	    rootResult.addMessage(header, message);
	    return true;
	}

	// parse string
	String[] list = StringUtils.split(nestedString, ",");

	// resolve
	for (String item : list) {
	    if (!restrictFromString(operation, item, rootResult))
		return false;
	}

	return true;
    }

}
