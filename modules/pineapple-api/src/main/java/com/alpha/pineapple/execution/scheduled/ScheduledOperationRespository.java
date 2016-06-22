/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2015 Allan Thrane Andersen..
 *
 *    This file is part of Pineapple.
 *
 *    Pineapple is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    Pineapple is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with Pineapple. If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */
package com.alpha.pineapple.execution.scheduled;

import java.util.stream.Stream;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.module.ModuleNotFoundException;

/**
 * Interface for management of scheduled operations.
 */
public interface ScheduledOperationRespository {

    /**
     * Initialize repository.
     * 
     * @param result
     *            A child execution result will be added to this result. The
     *            child result will reflects the out come of the initialization.
     *            If the initialization fails the state of the result object
     *            will be failure or error and will contain a stack trace
     *            message.
     */
    void initialize(ExecutionResult result);

    /**
     * Get all scheduled operations.
     * 
     * @return stream of scheduled operation info's.
     */
    Stream<ScheduledOperationInfo> getOperations();

    /**
     * Add scheduled operation.
     *
     * @param name
     *            name of the scheduled operation. Must be unique.
     * @param module
     *            name of the module to be scheduled for execution.
     * @param operation
     *            operation which should be executed.
     * @param environment
     *            environment for which a model within the module should be
     *            executed.
     * @param description
     *            description of the scheduled operation.
     * @param cron
     *            CRON scheduling expression.
     *
     * @return scheduled operation info.
     * 
     * @throws ModuleNotFoundException
     *             if module isn't found.
     * @throws EnvironmentNotFoundException
     *             if environment isn't found with module.
     * @throws IllegalJobNameException
     *             if job name isn't unique.
     */
    ScheduledOperationInfo create(String name, String module, String operation, String environment, String description,
	    String cron);

    /**
     * Add scheduled operation.
     *
     * @param operation
     *            scheduled operation which should be executed.
     *
     * @return scheduled operation info.
     * 
     * @throws ModuleNotFoundException
     *             if module isn't found.
     * @throws EnvironmentNotFoundException
     *             if environment isn't found with module.
     * @throws IllegalJobNameException
     *             if job name isn't unique.
     */
    ScheduledOperationInfo create(ScheduledOperation operation);

    /**
     * Remove scheduled job.
     * 
     * @param name
     *            name of job to remove.
     * 
     * @throws ScheduledOperationNotFoundException
     *             if job doesn't exist.
     */
    public void delete(String name);

    /**
     * Remove all scheduled jobs.
     */
    public void deleteAll();

}
