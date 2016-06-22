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

/**
 * Interface for execution of operations.
 */
public interface OperationTask {

    /**
     * Execute task.
     * 
     * @param info
     *            Execution info.
     */
    public void execute(ExecutionInfo info);

    /**
     * Execute task.
     * 
     * @param operation
     *            operation to execute.
     * @param environment
     *            target environment to execute operation in.
     * @param module
     *            target module to execute operation in.
     * @return execution info.
     */
    public ExecutionInfo execute(String operation, String environment, String module);

    /**
     * Attach execution of composite task to existing execution.
     * 
     * @param operation
     *            operation to execute.
     * @param environment
     *            target environment to execute operation in.
     * @param module
     *            target module to execute operation in.
     * @param description
     *            description of the composite execution.
     * @param result
     *            root result of existing execution that the composite execution
     *            is attached to.
     *            
     * @return execution info.
     */
    public ExecutionInfo executeComposite(String operation, String environment, String module, String description, ExecutionResult result);
    
}
