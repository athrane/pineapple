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

import java.util.HashMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.Header;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.w3c.dom.Document;

/**
 * Implementation of the <code>HttpInvocationResult</code> interface.
 */
public class HttpInvocationResultImpl implements HttpInvocationResult {

	/**
	 * HTTP status code.
	 */
	public int statusCode;

	/**
	 * Registered host name
	 */
	public String host;

	/**
	 * Registered environment.
	 */
	public String environment;

	/**
	 * Registered location.
	 */
	public String location;

	/**
	 * Registered server.
	 */
	public String server;

	/**
	 * Registered cookies.
	 */
	public HashMap<String, String> cookies;

	/**
	 * HTTP response headers.
	 */
	public Header[] responseHeaders;

	/**
	 * Contains the response as XML.
	 */
	public Document responseAsXml;

	/**
	 * Registered properties.
	 */
	public HashMap<String, Object> properties;

	/**
	 * XPath object.
	 */
	static XPath xpath = XPathFactory.newInstance().newXPath();

	/**
	 * HttpInvocationResult constructor.
	 */
	public HttpInvocationResultImpl() {
		cookies = new HashMap<String, String>();
		properties = new HashMap<String, Object>();
	}

	public HashMap<String, String> getCookies() {
		return cookies;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getHost() {
		return host;
	}

	public String getLocation() {
		return location;
	}

	public Header[] getResponseHeaders() {
		return responseHeaders;
	}

	public String getServer() {
		return server;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setCookies(HashMap<String, String> cookies) {
		this.cookies = cookies;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setResponseHeaders(Header[] headers) {
		this.responseHeaders = headers;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setStatusCode(int code) {
		this.statusCode = code;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	public boolean containsProperty(String name) {

		// return answer
		return properties.containsKey(name);
	}

	public Object getProperty(String name) {

		// return answer
		return properties.get(name);
	}

	public void setPropertyValue(String name, Object value) {
		this.properties.put(name, value);
	}

	@Override
	public String toString() {
		String excludeFieldNames = "responseHeaders";
		return ReflectionToStringBuilder.toStringExclude(this, excludeFieldNames);
	}

}
