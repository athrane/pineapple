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

package com.alpha.pineapple.plugin.agent.command;

import static com.alpha.pineapple.plugin.agent.AgentConstants.DELETE_MODULE_URI;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.initialization.CommandInitializer;
import com.alpha.pineapple.command.initialization.CommandInitializerImpl;
import com.alpha.pineapple.command.initialization.Initialize;
import com.alpha.pineapple.command.initialization.ValidateValue;
import com.alpha.pineapple.command.initialization.ValidationPolicy;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.plugin.agent.session.AgentSession;

/**
 * <p>
 * Implementation of the <code>org.apache.commons.chain.Command</code> interface
 * which deletes a module at Pineapple.
 * 
 * <p>
 * Precondition for execution of the command is definition of these keys in the
 * context:
 * 
 * <ul>
 * <li><code>module</code> defines the name of the module. The type is
 * <code>java.lang.String</code>.</li>
 * 
 * <li><code>session</code> defines the agent session used communicate with an
 * agent. The type is
 * <code>com.alpha.pineapple.plugin.agent.session.AgentSession</code>.</li>
 * 
 * <li><code>execution-result</code> contains execution result object which
 * collects information about the execution of the test. The type is
 * <code>com.alpha.pineapple.plugin.execution.ExecutionResult</code>.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Postcondition after execution of the command is:
 * 
 * <ul>
 * <li>The the state of the supplied <code>ExecutionResult</code> is updated
 * with <code>ExecutionState.SUCCESS</code> if the test succeeded. If the test
 * failed then the <code>ExecutionState.FAILURE</code> is returned.</li>
 * <li>If the test fails due to an exception then the exception isn't caught,
 * but passed on the the invoker whose responsibility it is to catch it and
 * update the <code>ExecutionResult</code> with the state
 * <code>ExecutionState.ERROR</code>.</li>
 * </ul>
 * </p>
 */
public class DeleteModuleCommand implements Command {

	/**
	 * URL module variable.
	 */
	static final String URL_VAR_MODULE = "module";

	/**
	 * Key used to identify property in context: Name of the module.
	 */
	public static final String MODULE_KEY = URL_VAR_MODULE;

	/**
	 * Key used to identify property in context: plugin session object.
	 */
	public static final String SESSION_KEY = "session";

	/**
	 * Key used to identify property in context: Contains execution result object,.
	 */
	public static final String EXECUTIONRESULT_KEY = "execution-result";

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Module name.
	 */
	@Initialize(MODULE_KEY)
	@ValidateValue(ValidationPolicy.NOT_EMPTY)
	String module;

	/**
	 * Plugin session.
	 */
	@Initialize(SESSION_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	AgentSession session;

	/**
	 * Defines execution result object.
	 */
	@Initialize(EXECUTIONRESULT_KEY)
	@ValidateValue(ValidationPolicy.NOT_NULL)
	ExecutionResult executionResult;

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	public boolean execute(Context context) throws Exception {
		// initialize command
		CommandInitializer initializer = new CommandInitializerImpl();
		initializer.initialize(context, this);

		// delete module
		doDelete(context);

		return Command.CONTINUE_PROCESSING;
	}

	/**
	 * Delete module.
	 * 
	 * @param context
	 *            Command context.
	 * 
	 * @throws Exception
	 *             If deletion fails.
	 */
	void doDelete(Context context) {
		try {
			// add message
			addModuleNameMessage();

			// create URL variables
			Map<String, String> urlVariables = Collections.singletonMap(URL_VAR_MODULE, module);

			// post
			session.httpDelete(DELETE_MODULE_URI, urlVariables);

			// complete result
			executionResult.completeAsSuccessful(messageProvider, "demc.delete_module_completed");

		} catch (Exception e) {
			executionResult.completeAsError(messageProvider, "demc.error", e);
		}
	}

	/**
	 * Add module name message to execution result.
	 */
	void addModuleNameMessage() {
		String key = messageProvider.getMessage("demc.delete_module_info_key");
		executionResult.addMessage(key, module);
		return;
	}
}
