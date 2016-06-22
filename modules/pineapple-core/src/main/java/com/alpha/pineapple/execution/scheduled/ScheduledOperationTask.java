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

import org.apache.commons.lang.Validate;

import com.alpha.pineapple.execution.OperationTask;

/**
 * Implementation of the {@linkplain Runnable} interface for the scheduled
 * execution of an operation.
 */
public class ScheduledOperationTask implements Runnable {

    /**
     * Operation task.
     */
    OperationTask operationTask;

    /**
     * Operation.
     */
    String operation;

    /**
     * Environment.
     */
    String environment;

    /**
     * Module.
     */
    String module;

    /**
     * ScheduledTask constructor.
     * 
     * @param operation
     *            operation to execute.
     * @param environment
     *            target environment to execution operation in.
     * @param module
     *            target module.
     * @param operationTask
     *            synchronous operation task to delegate execution to.
     */
    public ScheduledOperationTask(String operation, String environment, String module, OperationTask operationTask) {
	Validate.notNull(operation, "operation is undefined");
	Validate.notEmpty(operation, "operation is empty");
	Validate.notNull(environment, "environment is undefined");
	Validate.notEmpty(environment, "environment is empty");
	Validate.notNull(module, "module is undefined");
	Validate.notEmpty(module, "module is empty");
	Validate.notNull(operationTask, "core is undefined");
	this.operation = operation;
	this.environment = environment;
	this.module = module;
	this.operationTask = operationTask;
    }

    @Override
    public void run() {
	operationTask.execute(operation, environment, module);
	// TODO: capture execution info.
    }

}
