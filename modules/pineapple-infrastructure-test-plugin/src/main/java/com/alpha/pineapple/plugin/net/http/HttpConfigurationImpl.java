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
 * Implements <code>HttpConfiguration>/code> interface.
 * 
 *  If the no value is set of a property then the object defines 
 *  default values.
 */
public class HttpConfigurationImpl implements HttpConfiguration {

	boolean httpFollowRedirects = true;;
	String proxyHost = "";
	int proxyPort = 0;
	int tcpSocketTimeout = 3000;

	public boolean getHttpFollowRedirects() {		
		return this.httpFollowRedirects;
	}

	public String getProxyHost() {
		return this.proxyHost;
	}

	public int getProxyPort() {
		return this.proxyPort;
	}

	public int getTcpSocketTimeout() {
		return this.tcpSocketTimeout;
	}

	public void setHttpFollowRedirects(boolean followRedirects) {
		this.httpFollowRedirects = followRedirects;
	}

	public void setProxyHost(String host) {
		this.proxyHost = host;
	}

	public void setProxyPort(int port) {
		this.proxyPort = port;
	}

	public void setTcpSocketTimeout(int timeout) {
		this.tcpSocketTimeout = timeout;
	}

}
