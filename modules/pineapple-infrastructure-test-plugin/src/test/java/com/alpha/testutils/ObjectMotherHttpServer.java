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

package com.alpha.testutils;

import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.alpha.javautils.StackTraceHelper;
import com.alpha.testutils.jetty.ConfigurableRedirectServlet;
import com.alpha.testutils.jetty.JSessionIdSetterFilter;
import com.alpha.testutils.jetty.SnifferServlet;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * creating HTTP server instances.
 * 
 * The class create HTTP servers based in the Jetty server.
 */
public class ObjectMotherHttpServer {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Get host IP address.
	 * 
	 * @param isLocalHost
	 *            If true then 127.0.0.1 is return.
	 * 
	 * @return IP address of the current machine. If the parameter is true then
	 *         127.0.0.1 is returned.
	 */
	public String getHostIPAddress(boolean isLocalHost) {

		try {

			// return local IP if local host
			if (isLocalHost) {
				return "127.0.0.1";
			}

			// return FQ host name
			return InetAddress.getLocalHost().getHostAddress();

		} catch (UnknownHostException e) {
			fail("Failed to resolve host IP address due to exception:" + StackTraceHelper.getStrackTrace(e));
			return "";
		}
	}

	/**
	 * Create HTTP server.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServer(String host, int port) {

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Starting TO create HTTP Server at <");
			message.append(host);
			message.append(":");
			message.append(port);
			message.append(">.");
			logger.debug(message.toString());
		}

		try {
			// create server
			enableJettyDebugging();
			Server server = createServer(host, port);

			// create servlet handler
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");

			// add servlets
			context.addServlet(new ServletHolder(new SnifferServlet()), "/sniffer");
			EnumSet<DispatcherType> dispatches = EnumSet.of(DispatcherType.REQUEST);
			context.addFilter(new FilterHolder(new JSessionIdSetterFilter()), "/*", dispatches);

			// set handler
			server.setHandler(context);

			// start server
			server.start();

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Successfully created HTTP Server at <");
				message.append(host);
				message.append(":");
				message.append(port);
				message.append(">.");
				logger.debug(message.toString());
			}

			return server;

		} catch (Exception e) {
			fail("HTTP server creation failed with exception:" + StackTraceHelper.getStrackTrace(e));
			return null;

		}
	}

	/**
	 * Create HTTP server, which redirects from "/redirect" to "/sniffer" returning
	 * http status code 301.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServerWithRedirect301(String host, int port) {
		return createHttpServerWithRedirect(host, port, 301);
	}

	/**
	 * Create HTTP server, which redirects from "/redirect" to "/sniffer" returning
	 * http status code 302.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServerWithRedirect302(String host, int port) {
		return createHttpServerWithRedirect(host, port, 302);
	}

	/**
	 * Create HTTP server, which redirects from "/redirect" to "/sniffer" returning
	 * http status code 303.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServerWithRedirect303(String host, int port) {
		return createHttpServerWithRedirect(host, port, 303);
	}

	/**
	 * Create HTTP server, which redirects from "/redirect" to "/sniffer" returning
	 * http status code 304.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServerWithRedirect304(String host, int port) {
		return createHttpServerWithRedirect(host, port, 304);
	}

	/**
	 * Create HTTP server, which redirects from "/redirect" to "/sniffer" returning
	 * http status code 305.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServerWithRedirect305(String host, int port) {
		return createHttpServerWithRedirect(host, port, 305);
	}

	/**
	 * Create HTTP server, which redirects from "/redirect" to "/sniffer" returning
	 * http status code 306.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServerWithRedirect306(String host, int port) {
		return createHttpServerWithRedirect(host, port, 306);
	}

	/**
	 * Create HTTP server, which redirect from redirect to sniffer.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * @param httpStatusCode
	 *            HTTP status returned by servlet
	 * 
	 * @return Configured HTTP server.
	 */
	public Server createHttpServerWithRedirect(String host, int port, int httpStatusCode) {

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Starting TO create HTTP Server at <");
			message.append(host);
			message.append(":");
			message.append(port);
			message.append(">.");
			logger.debug(message.toString());
		}

		try {
			// create server
			enableJettyDebugging();
			Server server = createServer(host, port);

			// create servlet handler
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");

			// add servlets
			context.addServlet(new ServletHolder(new SnifferServlet()), "/sniffer");
			context.addServlet(
					new ServletHolder(new ConfigurableRedirectServlet("/redirect", "/sniffer", httpStatusCode)),
					"/redirect");
			EnumSet<DispatcherType> dispatches = EnumSet.of(DispatcherType.REQUEST);
			context.addFilter(new FilterHolder(new JSessionIdSetterFilter()), "/*", dispatches);

			// set handler
			server.setHandler(context);

			// start server
			server.start();

			// log debug message
			if (logger.isDebugEnabled()) {
				StringBuilder message = new StringBuilder();
				message.append("Successfully created HTTP Server at <");
				message.append(host);
				message.append(":");
				message.append(port);
				message.append(">.");
				logger.debug(message.toString());
			}

			return server;

		} catch (Exception e) {
			fail("HTTP server creation failed with exception:" + StackTraceHelper.getStrackTrace(e));
			return null;

		}
	}

	/**
	 * Set system property to enabled Jetty7 debugging.
	 */
	public void enableJettyDebugging() {
		System.setProperty("org.eclipse.jetty.util.log.stderr.DEBUG", Boolean.toString(true));
	}

	/**
	 * Create Jetty server object.
	 * 
	 * @param host
	 *            Host name.
	 * @param port
	 *            Port number.
	 * 
	 * @return Jetty server object.
	 */
	public Server createServer(String host, int port) {
		// create server
		Server server = new Server();

		// create connector
		ServerConnector httpConnector = new ServerConnector(server);
		httpConnector.setHost(host);
		httpConnector.setPort(port);
		server.addConnector(httpConnector);
		return server;
	}
}
