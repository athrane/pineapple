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

package com.alpha.pineapple.execution.trigger;

import java.util.stream.Stream;

import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.execution.ExecutionResult.ExecutionState;
import com.alpha.pineapple.model.module.model.Trigger;

/**
 * Interface for resolution of trigger(s) to execute for a model. The trigger
 * candidate list is resolved versus the expected execution state.
 */
public interface ResultTriggerResolver {

	/**
	 * Resolves stream of candidate list of trigger to execute.
	 * 
	 * @param triggers
	 *            list of candidate triggers to resolve..
	 * @param state
	 *            execution of model where triggers are defined.
	 * 
	 * @return stream of trigger to execute.
	 */
	Stream<Trigger> resolve(Stream<Trigger> triggers, ExecutionState state);
}
