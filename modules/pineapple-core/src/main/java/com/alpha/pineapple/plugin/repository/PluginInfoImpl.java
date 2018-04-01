/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
* Copyright (C) 2007-2015 Allan Thrane Andersen.
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

package com.alpha.pineapple.plugin.repository;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

class PluginInfoImpl implements PluginInfo {
	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	boolean inputMarshallingEnabled;

	boolean sessionHandlingEnabled;

	String unmarshallerId;

	String pluginId;

	String configFileName;

	ApplicationContext context;

	String sessionId;

	public boolean isInputMarshallingEnabled() {
		return inputMarshallingEnabled;
	}

	public void setInputMarshallingEnabled(boolean inputMarshallingEnabled) {
		this.inputMarshallingEnabled = inputMarshallingEnabled;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Input marshalling is <");
			message.append(this.inputMarshallingEnabled ? "enabled" : "disabled");
			message.append("> for plugin <");
			message.append(this.pluginId);
			message.append(">.");
			logger.debug(message.toString());
		}
	}

	public boolean isSessionHandlingEnabled() {
		return sessionHandlingEnabled;
	}

	public void setSessionHandlingEnabled(boolean sessionHandlingEnabled) {
		this.sessionHandlingEnabled = sessionHandlingEnabled;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Session handling is <");
			message.append(this.sessionHandlingEnabled ? "enabled" : "disabled");
			message.append("> for plugin <");
			message.append(this.pluginId);
			message.append(">.");
			logger.debug(message.toString());
		}
	}

	public String getUnmarshallerId() {
		return unmarshallerId;
	}

	public void setUnmarshallerId(String unmarshallerId) {
		this.unmarshallerId = unmarshallerId;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Registered unmarshaller id <");
			message.append(this.unmarshallerId);
			message.append("> for plugin <");
			message.append(this.pluginId);
			message.append(">.");
			logger.debug(message.toString());
		}
	}

	public String getPluginId() {
		return this.pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Registered plugin id <");
			message.append(this.pluginId);
			message.append(">.");
			logger.debug(message.toString());
		}
	}

	public String getConfigFileName() {
		return this.configFileName;
	}

	public void setConfigFileName(String configFileName) {
		// if file name is null, then generate default name:
		// ${pluginId}-config.xml
		if ((configFileName == null) || (configFileName.length() == 0)) {
			StringBuilder newFileName = new StringBuilder();
			newFileName.append(pluginId);
			newFileName.append("-config.xml");
			this.configFileName = newFileName.toString();

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Generated default plugin configuration file name <");
				message.append(this.configFileName);
				message.append(">.");
				logger.debug(message.toString());
			}
		} else {
			this.configFileName = configFileName;
		}

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Registered plugin configuration file name <");
			message.append(this.configFileName);
			message.append("> for plugin <");
			message.append(this.pluginId);
			message.append(">.");
			logger.debug(message.toString());
		}
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Registered application context <");
			message.append(this.context);
			message.append("> for plugin <");
			message.append(this.pluginId);
			message.append(">.");
			logger.debug(message.toString());
		}

	}

	/**
	 * @see com.alpha.pineapple.plugin.repository.PluginInfo#getSessionId()
	 */
	public String getSessionId() {
		return this.sessionId;
	}

	/**
	 * @see com.alpha.pineapple.plugin.repository.PluginInfo#setSessionId(java.lang.String)
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Registered session id <");
			message.append(this.sessionId);
			message.append("> for plugin <");
			message.append(this.pluginId);
			message.append(">.");
			logger.debug(message.toString());
		}
	}

}
