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

package com.alpha.pineapple.session;

import javax.annotation.Resource;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.pineapple.command.ProcessExecutionCommand;
import com.alpha.pineapple.command.execution.CommandRunner;
import com.alpha.pineapple.execution.ExecutionResult;
import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.resource.ResourceException;
import com.alpha.pineapple.resource.ResourcePropertyGetter;

/**
 * Implementation of the {@link ProcessExecutionSession} interface.
 */
public class ProcessExecutionSessionImpl implements ProcessExecutionSession {

	/**
	 * Null time out value, triggers the command to use is default value.
	 */
	static final int NULL_TIMEOUT = 0;

	/**
	 * First array index.
	 */
	final int FIRST_INDEX = 0;

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Resource.
	 */
	com.alpha.pineapple.model.configuration.Resource resource;

	/**
	 * Resource credential.
	 */
	Credential credential;

	/**
	 * Command runner
	 */
	@Resource
	CommandRunner commandRunner;

	/**
	 * Message provider for I18N support.
	 */
	@Resource(name = "processExecutionMessageProvider")
	MessageProvider messageProvider;

	/**
	 * Resource getter object.
	 */
	@Resource
	ResourcePropertyGetter propertyGetter;

	/**
	 * Process execution command.
	 */
	@Resource
	Command processExecutionCommand;

	public void connect(com.alpha.pineapple.model.configuration.Resource resource, Credential credential)
			throws SessionConnectException {
		// validate parameters
		Validate.notNull(resource, "resource is undefined.");
		Validate.notNull(credential, "credential is undefined.");

		// store in fields
		this.credential = credential;
		this.resource = resource;

		// initialize property getter
		this.propertyGetter.setResource(resource);

		// log debug message
		if (logger.isDebugEnabled()) {
			Object[] args = { resource, credential.getId() };
			String message = messageProvider.getMessage("pesi.connect", args);
			logger.debug(message);
		}
	}

	public void disconnect() throws SessionDisconnectException {
		// log debug message
		if (logger.isDebugEnabled()) {
			String message = messageProvider.getMessage("pesi.disconnect");
			logger.debug(message);
		}
	}

	public com.alpha.pineapple.model.configuration.Resource getResource() {
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}

	@SuppressWarnings("unchecked")
	public void execute(String executable, String[] arguments, long timeout, String description,
			ExecutionResult parent) {

		// create context
		Context context = commandRunner.createContext();

		// map command parameters
		context.put(ProcessExecutionCommand.EXECUTABLE_KEY, executable);
		context.put(ProcessExecutionCommand.ARGUMENTS_KEY, arguments);
		context.put(ProcessExecutionCommand.TIMEOUT_KEY, timeout);

		// configure command runner with execution result
		commandRunner.setExecutionResult(parent);

		// run test
		commandRunner.run(processExecutionCommand, description, context);
	}

	public void execute(String executable, String[] arguments, String description, ExecutionResult parent) {
		execute(executable, arguments, NULL_TIMEOUT, description, parent);
	}

	@Override
	public boolean isResourcePropertyDefined(String name) {
		return propertyGetter.containsProperty(name);
	}

	@Override
	public String getResourceProperty(String name) throws ResourceException {
		return propertyGetter.getProperty(name);
	}

}
