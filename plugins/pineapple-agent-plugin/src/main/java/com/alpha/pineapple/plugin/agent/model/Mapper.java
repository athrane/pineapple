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

package com.alpha.pineapple.plugin.agent.model;

import org.apache.commons.chain.Context;

import com.alpha.pineapple.model.execution.scheduled.ScheduledOperation;
import com.alpha.pineapple.plugin.agent.session.AgentSession;

/**
 * Maps values from the schema generated objects into the command context.
 */
public interface Mapper {

	/**
	 * Map command to command context.
	 * 
	 * @param command
	 *            model command.
	 * @param context
	 *            command context.
	 * @param session
	 *            operation session.
	 */
	public void mapModel(AgentCommand command, Context context, AgentSession session);

	/**
	 * Map command to command context.
	 * 
	 * @param command
	 *            model command.
	 * @param context
	 *            command context.
	 * @param session
	 *            operation session.
	 */
	void mapDistributeModule(DistributeModule command, Context context, AgentSession session);

	/**
	 * Map command to command context.
	 * 
	 * @param command
	 *            model command.
	 * @param context
	 *            command context.
	 * @param session
	 *            operation session.
	 */
	void mapDeleteModule(DeleteModule command, Context context, AgentSession session);

	/**
	 * Map command to command context.
	 * 
	 * @param context
	 *            command context.
	 * @param session
	 *            operation session.
	 */
	void mapRefreshEnvironmentConfiguration(Context context, AgentSession session);

	/**
	 * Map command to command context.
	 * 
	 * @param context
	 *            command context.
	 * @param session
	 *            operation session.
	 */
	public void mapScheduleOperation(ScheduleOperation command, Context context, AgentSession session);

	/**
	 * Map command to command context.
	 * 
	 * @param context
	 *            command context.
	 * @param session
	 *            operation session.
	 */
	public void mapDeleteScheduledOperation(DeleteScheduledOperation command, Context context, AgentSession session);

	/**
	 * Map command to command context.
	 * 
	 * @param context
	 *            command context.
	 * @param session
	 *            operation session.
	 */
	public void mapDeleteAllScheduledOperations(Context context, AgentSession session);

}
