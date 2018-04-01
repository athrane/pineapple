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

import org.apache.log4j.Logger;

import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;

/**
 * Null implementation of the {@link Session} interface.
 */
public class NullSessionImpl implements Session {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Resource.
	 */
	Resource resource;

	/**
	 * Credential.
	 */
	Credential credential;

	public void connect(Resource resource, Credential credential) throws SessionConnectException {
		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Successfully connected to resource <.");
			message.append(resource);
			message.append("> using credential with id <");
			message.append(credential.getId());
			message.append(">.");
			logger.debug(message.toString());
		}

		this.resource = resource;
		this.credential = credential;
	}

	public void disconnect() throws SessionDisconnectException {
		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Successfully disconnected..");
			logger.debug(message.toString());
		}
	}

	public Resource getResource() {
		return resource;
	}

	public Credential getCredential() {
		return credential;
	}

}
