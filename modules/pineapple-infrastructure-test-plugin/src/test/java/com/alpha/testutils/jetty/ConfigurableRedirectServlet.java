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


package com.alpha.testutils.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple redirect servlet for testing. 
 */
public class ConfigurableRedirectServlet extends HttpServlet {

	/**
	 * Serial Version UID. 
	 */
	static final long serialVersionUID = 1L;
	
	/**
	 * Location Header.
	 */
	public static final String LOCATION_HEADER = "Location"; 
	
	/**
	 * Location where request should be redirected to.
	 */
	String location;

	/**
	 * URI of the redirect servlet.
	 */
	String myUri;

	/**
	 * Redirect status code.
	 */
	int redirectStatusCode;

	public ConfigurableRedirectServlet(String uri, String location, int redirectStatusCode ) {
		this.myUri = uri;
		this.location = location;
		this.redirectStatusCode = redirectStatusCode;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {	
		StringBuffer rawUrl = request.getRequestURL();
		int myUriIndex = rawUrl.lastIndexOf(myUri);
		
		StringBuilder newURL = new StringBuilder();
		newURL.append(rawUrl.substring(0, myUriIndex));
		newURL.append(location);						       
        response.setHeader(LOCATION_HEADER,newURL.toString());
        response.setStatus(redirectStatusCode);
    }
	
}
