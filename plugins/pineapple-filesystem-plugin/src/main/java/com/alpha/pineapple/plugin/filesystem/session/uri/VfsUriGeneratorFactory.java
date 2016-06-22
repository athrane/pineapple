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
 * Factory which creates VFS URI generator instances. 
 */
public class VfsUriGeneratorFactory {

	/**
	 * Returns a VFS URI generator for supported protocols.
	 * 
	 * @param protocol Protocol to return a URI generator for.
	 * @param host Host to generate URI's for.
	 * @param port TCP used to access the host. 
	 * @param user User name used to access the host.  
	 * @param password Password used to access the host. 
	 * @param port  
	 * 
	 * @return VFS URI generator for requested protocol.
	 */	
	public VfsUriGenerator createGenerator(String protocol, String host, String port, String user, String password) {
		if (protocol.equalsIgnoreCase("file") ) return new FileUriGeneratorImpl();		
		if (protocol.equalsIgnoreCase("ftp") ) return new FtpUriGeneratorImpl(host, port, user, password);
		if (protocol.equalsIgnoreCase("ftps") ) return new FtpsUriGeneratorImpl(host, port, user, password);		
		if (protocol.equalsIgnoreCase("http") ) return new HttpUriGeneratorImpl(host, port, user, password);
		if (protocol.equalsIgnoreCase("https") ) return new HttpsUriGeneratorImpl(host, port, user, password);		
		if (protocol.equalsIgnoreCase("smb") ) return new SmbUriGeneratorImpl(host, port, user, password);
		if (protocol.equalsIgnoreCase("zip") ) return new ZipUriGeneratorImpl();
		return null;
	}
}
