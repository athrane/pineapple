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

/**
 * Implementation of the {@link Session} interface which provides access to the
 * resource and credential.
 */
public class DefaultSessionImpl implements Session {

	/**
	 * Resource.
	 */
	Resource resource;

	/**
	 * Credential.
	 */
	Credential credential;

	public void connect(Resource resource, Credential credential) throws SessionConnectException {
		// store parameters
		this.resource = resource;
		this.credential = credential;
	}

	public void disconnect() throws SessionDisconnectException {
	}

	public Resource getResource() {
		return this.resource;
	}

	public Credential getCredential() {
		return this.credential;
	}
}
