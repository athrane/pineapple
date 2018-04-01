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

import com.alpha.pineapple.model.configuration.Credential;
import com.alpha.pineapple.model.configuration.Resource;

/*
 * Defines interface for session which is used to represent a connection to an 
 * external resource with used by a plugin.
 */
public interface Session {

	/**
	 * Establish connection to external resource.
	 * 
	 * @param resource
	 *            The resource to which the session should establish a connection.
	 * @param credential
	 *            Credential used to authenticate session with resource.
	 * 
	 * @throws SessionConnectException
	 *             If connecting to resource fails.
	 */
	public void connect(Resource resource, Credential credential) throws SessionConnectException;

	/**
	 * Terminate connection to external resource.
	 * 
	 * @throws SessionDisconnectException
	 *             If termination of connection to external resource fails.
	 */
	public void disconnect() throws SessionDisconnectException;

	/**
	 * Get resource.
	 * 
	 * @return resource object.
	 */
	Resource getResource();

	/**
	 * Get credential.
	 * 
	 * @return credential object.
	 */
	Credential getCredential();
}
