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


package com.alpha.pineapple.command.execution;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Command runner interface, which defines operations for execution of Chain commands. 
 */
public interface CommandRunner {
	
    /**
     * <p>Execute a Chain command with a Chain context object a input. Information about the
     * result of the execution is stored in the returned execution result object.</p>
     *
     * <p>If the runner has a defined root execution result object, then a new
     * execution result object is created and added as a child object to 
     * the root execution result object. If no root execution result object is defined 
     * then one is created and the child result object is added to new root.</p>
     *
     * <p>The created execution result object which is used to describe
     * the execution of the command, is also added to the command context under the
     * key <code>execution-result</code>. this enables command objects to
     * update the execution result object with information the outcome of 
     * the execution of the command.</p> 
     *   
     * @param command The command which should executed with the supplied context.
     *  
     * @param description Human readable description of the command which should be executed.  
     *  
     * @param context The context which should be used during command 
     * execution. 
     * 
     * @return Execution result which contains information describing the the result 
     * of the execution of the command, including the provided description.  
     */    
    public ExecutionResult run(Command command, String description, Context context);

    /**
     * <p>Execute a Chain command with a Chain context object a input. Information about the
     * result of the execution is stored in the supplied execution result object.</p>
     *
     * <p>If the runner has a defined root execution result object, then it is ignored and 
     * the supplied execution object is updated in isolation to contain the result of 
     *  execution of the command. The root execution result object isn't updated in any way.</p>
     *
     * <p>The created execution result object which is used to describe
     * the execution of the command, is also added to the command context under the
     * key <code>execution-result</code>. this enables command objects to
     * update the execution result object with information the outcome of 
     * the execution of the command.</p> 
     *   
     * @param command The command which should executed with the supplied context.
     *  
     * @param result Execution result which contains information describing the the result 
     * of the execution of the command, including the provided description.   
     *  
     * @param context The context which should be used during command 
     * execution. 
     * 
     * @return Execution result which contains information describing the the result 
     * of the execution of the command, including the provided description.  
     */    
    public ExecutionResult run(Command command, ExecutionResult result, Context context);
    
    
    /**
     * Set the root execution result object for the command runner.
     *  
     * @param result The root execution result object. 
     */
    public void setExecutionResult(ExecutionResult result);

    /**
     * Get the root execution result object for the command runner. If no result is
     * defined then one is created and returned.
     *  
     * @return The root execution result object for the command runner. 
     */
    public ExecutionResult getExecutionResult();

    /**
     * Create new Chain context.
     * 
     * @return new Chain context instance.
     */
    public Context createContext();

	/**
	 * Return true if the last execution succeeded.
	 * 
	 * @return true if the last execution succeeded.
	 */
	public boolean lastExecutionSucceeded();
    
}
