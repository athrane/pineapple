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


package com.alpha.pineapple.plugin.filesystem.session.uri;

/**
 * Abstract implementation of the {@link VfsUriGenerator} interface.
 */
public abstract class AbstractUriGeneratorImpl implements VfsUriGenerator {

	/**
	 * Host.
	 */
	String host;

	/**
	 * TCP port.
	 */
	String port;
	
	/**
	 * User.
	 */
	String user;
	
	/**
	 * Password
	 */
	String password;

	/**
	 * Set object fields
	 * 
	 * @param host Host to generate URI's for.
	 * @param port TCP port used to access the host. 
	 * @param user User name used to access the host.  
	 * @param password Password used to access the host. 
	 */
	void setFields(String host, String port, String user, String password) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;				
	}
	
	boolean isPortDefined() {
		return ((this.port != null) && (this.port.length() != 0 ));
	}
	
}
