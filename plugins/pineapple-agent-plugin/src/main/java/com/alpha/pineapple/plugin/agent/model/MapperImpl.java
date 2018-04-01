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

import com.alpha.pineapple.plugin.agent.command.CreateEnvironmentCommand;
import com.alpha.pineapple.plugin.agent.command.DeleteAllScheduledOperationsCommand;
import com.alpha.pineapple.plugin.agent.command.DeleteModuleCommand;
import com.alpha.pineapple.plugin.agent.command.DeleteScheduledOperationCommand;
import com.alpha.pineapple.plugin.agent.command.DistributeModuleCommand;
import com.alpha.pineapple.plugin.agent.command.ExecuteOperationCommand;
import com.alpha.pineapple.plugin.agent.command.ScheduleOperationCommand;
import com.alpha.pineapple.plugin.agent.session.AgentSession;

/**
 * Implementation of the <code>Mapper</code> interface which map model content
 * to command context.
 */
public class MapperImpl implements Mapper {

	public void mapModel(AgentCommand command, Context context, AgentSession session) {
		if (command instanceof DistributeModule)
			mapDistributeModule((DistributeModule) command, context, session);
		if (command instanceof ExecuteOperation)
			mapExecuteOperation((ExecuteOperation) command, context, session);
		if (command instanceof CreateEnvironment)
			mapCreateEnvironment((CreateEnvironment) command, context, session);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void mapDistributeModule(DistributeModule command, Context context, AgentSession session) {
		context.put(DistributeModuleCommand.MODULE_KEY, command.getModule());
		context.put(DistributeModuleCommand.SESSION_KEY, session);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void mapDeleteModule(DeleteModule command, Context context, AgentSession session) {
		context.put(DeleteModuleCommand.MODULE_KEY, command.getModule());
		context.put(DeleteModuleCommand.SESSION_KEY, session);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void mapRefreshEnvironmentConfiguration(Context context, AgentSession session) {
		context.put(DeleteModuleCommand.SESSION_KEY, session);
	}

	@SuppressWarnings("unchecked")
	void mapCreateEnvironment(CreateEnvironment command, Context context, AgentSession session) {
		context.put(CreateEnvironmentCommand.ENVIRONMENT_KEY, command.getEnvironment());
		context.put(CreateEnvironmentCommand.DESCRIPTION_KEY, command.getDescription());
		context.put(CreateEnvironmentCommand.SESSION_KEY, session);
	}

	@SuppressWarnings("unchecked")
	void mapExecuteOperation(ExecuteOperation command, Context context, AgentSession session) {
		context.put(ExecuteOperationCommand.MODULE_KEY, command.getModule());
		context.put(ExecuteOperationCommand.ENVIRONMENT_KEY, command.getEnvironment());
		context.put(ExecuteOperationCommand.OPERATION_KEY, command.getOperation());
		context.put(ExecuteOperationCommand.SESSION_KEY, session);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mapScheduleOperation(ScheduleOperation command, Context context, AgentSession session) {
		context.put(ScheduleOperationCommand.SESSION_KEY, session);
		context.put(ScheduleOperationCommand.NAME_KEY, command.getName());
		context.put(ScheduleOperationCommand.MODULE_KEY, command.getModule());
		context.put(ScheduleOperationCommand.ENVIRONMENT_KEY, command.getEnvironment());
		context.put(ScheduleOperationCommand.OPERATION_KEY, command.getOperation());
		context.put(ScheduleOperationCommand.SCHEDULING_EXPRESSION_KEY, command.getSchedulingExpression());
		context.put(ScheduleOperationCommand.DESCRIPTION_KEY, command.getDescription());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mapDeleteScheduledOperation(DeleteScheduledOperation command, Context context, AgentSession session) {
		context.put(DeleteScheduledOperationCommand.SESSION_KEY, session);
		context.put(DeleteScheduledOperationCommand.NAME_KEY, command.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mapDeleteAllScheduledOperations(Context context, AgentSession session) {
		context.put(DeleteAllScheduledOperationsCommand.SESSION_KEY, session);
	}

}
