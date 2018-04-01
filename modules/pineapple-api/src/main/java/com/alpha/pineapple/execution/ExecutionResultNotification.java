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

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;

/**
 * Interface for notification of state change in execution result.
 * 
 * A notification is used to capture the change of state since an execution
 * result isn't a immutable object.
 * 
 * Since an execution result is isn't immutable then the recorded state in the
 * state property and the state property in the execution result might not
 * match, because the execution result can have its state changed again AFTER
 * the creation of the notification object - which might result in the creation
 * of a new notification at that point in time.
 */
public interface ExecutionResultNotification {

	/**
	 * Return the execution result which changed state.
	 * 
	 * @return execution result which changed state.
	 */
	ExecutionResult getResult();

	/**
	 * Return new recorded state of the execution result.
	 * 
	 * The recorded state and the state of the executionresult might not match,
	 * because the execution result can have its state changed AFTER the creation of
	 * the notification object due to its mutable nature.
	 * 
	 * @return new recorded state of the execution result.
	 */
	ExecutionState getState();
}
