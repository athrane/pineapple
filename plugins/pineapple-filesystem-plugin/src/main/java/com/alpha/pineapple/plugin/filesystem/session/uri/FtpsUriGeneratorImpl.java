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
 * Implementation of the {@link VfsUriGenerator} interface for the FTPS protocol.
 */
public class FtpsUriGeneratorImpl extends AbstractUriGeneratorImpl {

	/**
	 * FtpsUriGeneratorImpl constructor.
	 * 
	 * @param host Host to generate URI's for.
	 * @param port TCP port used to access the host. 
	 * @param user User name used to access the host.  
	 * @param password Password used to access the host. 
	 */
	public FtpsUriGeneratorImpl(String host, String port, String user, String password) {
		setFields(host, port, user, password);
	}

	public String createUri(String path) {
		StringBuilder uri = new StringBuilder();
		uri.append("ftps://");
		uri.append(user);
		uri.append(":");
		uri.append(password);
		uri.append("@");
		uri.append(host);
		
		// add port if defined
		if (isPortDefined()) {
			uri.append(":");
			uri.append(port);
		}
		
		uri.append(path);		
		return uri.toString();
	}
		
}
