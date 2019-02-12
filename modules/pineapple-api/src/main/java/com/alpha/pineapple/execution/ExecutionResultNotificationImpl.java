/*
 *    Pineapple - a tool to install, configure and test Java web applications 
 *    and infrastructure. 
 *
 *    Copyright (C) 2007-2013 Allan Thrane Andersen..
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
 *    along with Pineapple. If not, see <http://www.gnu.org/licenses/>.
 *   
 *    DefaultExecutionResultNotificationImpl.java created by AllanThrane on 31/05/2014 
 *    File revision  $Revision: 0.0 $
 */

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

import static com.alpha.javautils.ArgumentUtils.notNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;

/**
 * Default implementation of the {@linkplain ExecutionResultNotification}
 * interface.
 */
public class ExecutionResultNotificationImpl implements ExecutionResultNotification {

	/**
	 * Execution result.
	 */
	ExecutionResult result;

	/**
	 * Recorded state.
	 */
	ExecutionState state;

	/**
	 * ExecutionResultNotificationImpl constructor.
	 * 
	 * @param result
	 *            execution result.
	 * @param state
	 *            recorded state.
	 */
	ExecutionResultNotificationImpl(ExecutionResult result, ExecutionState state) {
		this.result = result;
		this.state = state;
	}

	@Override
	public ExecutionResult getResult() {
		return result;
	}

	@Override
	public ExecutionState getState() {
		return state;
	}

	/**
	 * Create execution result notification.
	 *
	 * @param result
	 *            execution result.
	 * @param state
	 *            recorded state.
	 * 
	 * @return execution result notification.
	 */
	public static ExecutionResultNotification getInstance(ExecutionResult result, ExecutionState state) {
		notNull(result, "result is undefined.");
		notNull(state, "state is undefined.");
		return new ExecutionResultNotificationImpl(result, state);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
