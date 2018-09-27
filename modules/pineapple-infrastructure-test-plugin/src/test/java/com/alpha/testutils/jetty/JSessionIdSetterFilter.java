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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Servlet filter which sets a JSESSIONID cookie if it isn't set in request.
 */
public class JSessionIdSetterFilter implements Filter {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Starting to filter request <");
			message.append(request);
			message.append(">.");
			logger.debug(message.toString());
		}

		// exit if request isn't HTTP request
		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Successfully completed filtering of non HTTP request <");
				message.append(request);
				message.append(">.");
				logger.debug(message.toString());
			}

			return;
		}

		// type cast request and response
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (!isSessionCookieDefined(httpRequest)) {

			// create session
			HttpSession session = httpRequest.getSession(true);

			// create cookie
			Cookie cookie = new Cookie("JSESSIONID", session.getId());

			// add cookie
			httpResponse.addCookie(cookie);

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Added session cookie <");
				message.append(ReflectionToStringBuilder.toString(cookie));
				message.append(">.");
				logger.debug(message.toString());
			}

		}

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Successfully completed filtering of HTTP request <");
			message.append(request);
			message.append(">.");
			logger.debug(message.toString());
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/**
	 * Returns true if JSESSIONID cookie is defined in HTTP request.
	 * 
	 * @param request
	 *            HTTP request.
	 * 
	 * @return true if JSESSIONID cookie is defined in HTTP request.
	 */
	boolean isSessionCookieDefined(HttpServletRequest request) {

		// get cookies
		Cookie[] cookies = request.getCookies();

		// if no cookies is set then return false
		if (cookies == null) {
			return false;
		}

		// if no cookies is set then return false
		if (cookies.length == 0) {
			return false;
		}

		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("JSESSIONID")) {
				return true;
			}
		}

		return true;
	}
}
