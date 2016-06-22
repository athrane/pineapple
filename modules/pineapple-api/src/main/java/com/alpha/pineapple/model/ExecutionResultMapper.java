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

package com.alpha.pineapple.model;

import java.util.Map;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResultNotification;
import com.alpha.pineapple.model.execution.Results;

/**
 * Maps object to from {@linkplain ExecutionResult} object graph to schema
 * generated objects in the Execution result schema.
 */
public interface ExecutionResultMapper {

    /**
     * Map sequence of execution result notifications to model objects.
     * 
     * @param notifications
     *            array of execution result notifications.
     * 
     * @return execution result model containing mapped execution result.
     */
    Results mapNotificationsToModel(ExecutionResultNotification[] notifications);

    /**
     * Map sequence of model result updates to execution results.
     * 
     * @param modelResults
     *            execution result model.
     * @param resultsMap
     *            execution result map where is used to keep a index of mapped
     *            model results into execution results.
     */
    void mapModelToResults(Results modelResults, Map<Integer, ExecutionResult> resultsMap);

    /**
     * Create execution result map and register the local root result.
     * 
     * The map is used during mapping of model results into execution results.
     * 
     * @param rootResult
     *            local root result.
     * @param resultsMap
     *            execution result map where is used to keep a index of mapped
     *            model results into execution results.
     */
    Map<Integer, ExecutionResult> createExecutionResultMap(ExecutionResult rootResult);
}
