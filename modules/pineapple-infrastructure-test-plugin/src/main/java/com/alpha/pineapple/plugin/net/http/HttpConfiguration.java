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


package com.alpha.pineapple.plugin.net.http;

/**
 * HTTP configuration object which is used to configure 
 * the <code>InvokeHttpGetMethodCommand</code>.  
 */
public interface HttpConfiguration {
	
	/**
	 * Set the proxy host.
	 * 
	 * @param host Proxy host name.
	 */
	void setProxyHost(String host);

	/**
	 * Get the proxy host.
	 * 
	 * @return the proxy host.
	 */
	String getProxyHost();
	
	/**
	 * Set the proxy port.
	 * 
	 * @param host Proxy port.
	 */
	void setProxyPort(int port);

	/**
	 * Get the proxy port.
	 * 
	 * @return the proxy port.
	 */
	int getProxyPort();

	/**
	 * Set the TCP socket time out.
	 * 
	 * @param timeout TCP socket time out in milliseconds.
	 */
	void setTcpSocketTimeout(int timeout);

	/**
	 * Get the TCP socket time out.
	 * 
	 * @return TCP socket time out in milliseconds.
	 */
	int getTcpSocketTimeout();
		
	/**
	 * Set the HTTP follow redirects behavior.
	 * 
	 * @param followRedirects If true then the HTTP client should follow redirects. 
	 */
	void setHttpFollowRedirects(boolean followRedirects);

	/**
	 * Get the HTTP follow redirects behavior.
	 * 
	 * @return true if HTTP client should follow redirects.
	 */
	boolean getHttpFollowRedirects();
	
}
