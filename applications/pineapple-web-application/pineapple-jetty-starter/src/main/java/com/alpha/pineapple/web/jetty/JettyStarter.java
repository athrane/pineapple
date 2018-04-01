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

package com.alpha.pineapple.web.jetty;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;

import com.alpha.javautils.StackTraceHelper;

public class JettyStarter {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * JettyStarter no-arg constructor which start an embedded Jetty web server
	 * instance.
	 *
	 * <p>
	 * The class must be invoked with these system properties defined:
	 *
	 * <ul>
	 * <li><code>pineapple.jettystarter.home</code> defines home directory for
	 * Jetty. The type is <code>java.lang.String</code>.</li>
	 * <li><code>pineapple.jettystarter.host</code> defines host name which the
	 * Jetty server should listen on. The type is
	 * <code>java.lang.String</code>.</li>
	 * <li><code>pineapple.jettystarter.port</code> defines port number on which the
	 * Jetty server should listen on. The type is <code>integer</code>.</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * The class can be invoked with these system properties defined:
	 *
	 * <ul>
	 * <li><code>pineapple.jettystarter.stdoutlogging</code> defines whether the
	 * Jetty starter should log to standard out. if the property is defined and the
	 * value is "true" then the logging to standard out is enabled.
	 * <code>java.lang.String</code>.</li>
	 * </ul>
	 * </p>
	 * 
	 * @param args
	 *            command line arguments. Not used by the class.
	 * 
	 * @throws Exception
	 *             If starting the Jetty server fails.
	 */
	JettyStarter(String[] args) throws Exception {
		String stdLogging = null;

		try {
			// get home system properties
			String serverHome = System.getProperty("pineapple.jettystarter.home");
			String serverHost = System.getProperty("pineapple.jettystarter.host");
			String serverPort = System.getProperty("pineapple.jettystarter.port");
			stdLogging = System.getProperty("pineapple.jettystarter.stdoutlogging");

			// create server
			Server server = new Server();

			// create Jetty XML file
			File jettyConfDirectory = new File(serverHome, "conf");
			File jettyXmlFile = new File(jettyConfDirectory, "jetty.xml");

			// load configuration
			XmlConfiguration configuration = new XmlConfiguration(jettyXmlFile.toURL());
			configuration.configure(server);

			// create WAR path
			File serverHomeDir = new File(serverHome);
			File webAppsDir = new File(serverHomeDir, "webapps");
			File warFile = new File(webAppsDir, "pineapple-web-application-war.war");

			// add web application
			WebAppContext webapp = new WebAppContext();
			webapp.setContextPath("/");
			webapp.setWar(warFile.getAbsolutePath());

			// create connector
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(Integer.parseInt(serverPort));
			connector.setHost(serverHost);

			// log info message
			String message = "Starting Jetty.";
			logger.info(message);
			logToStdOut(message, stdLogging);

			// configure the server
			server.addConnector(connector);
			server.setHandler(webapp);
			server.setStopAtShutdown(true);
			server.start();

			// log info message
			message = "Successfully started Jetty. Will launch browser.";
			logger.info(message);
			logToStdOut(message.toString(), stdLogging);

			// launch browser
			launchBrowser(serverHost, serverPort, stdLogging);

			server.join();

		} catch (Exception e) {
			String message = StackTraceHelper.getStrackTrace(e);
			logger.error(message);
			logToStdOut(message, stdLogging);
			String message2 = "Exiting application due to unrecoverable error.";
			logger.error(message2);
			logToStdOut(message2, stdLogging);
			System.exit(1);
		}
	}

	/**
	 * Launch default web browser
	 * 
	 * @param serverHost
	 *            Server host name.
	 * @param serverPort
	 *            Server port.
	 * @param stdLogging
	 *            if string contains the string "true" then message is logged to
	 *            standard out.
	 * 
	 * @throws IOException
	 */
	void launchBrowser(String serverHost, String serverPort, String stdLogging) {
		// create page URL
		StringBuilder pageUrl = new StringBuilder();
		pageUrl.append("http://");
		pageUrl.append(serverHost);
		pageUrl.append(":");
		pageUrl.append(serverPort);

		// exit if desktop isn't supported
		if (!java.awt.Desktop.isDesktopSupported()) {
			String message = new StringBuilder().append("No desktop support - please open the application at: ")
					.append(pageUrl.toString()).toString();
			logger.info(message);
			logToStdOut(message, stdLogging);
			return;
		}

		// get desktop
		Desktop desktop = java.awt.Desktop.getDesktop();

		// exit if no browser support
		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			String message = new StringBuilder().append("No desktop support - please open the application at: ")
					.append(pageUrl.toString()).toString();
			logger.info(message);
			logToStdOut(message, stdLogging);
			return;
		}

		try {
			final URI url = java.net.URI.create(pageUrl.toString());

			// open page in browser
			java.awt.Desktop.getDesktop().browse(url);

		} catch (Exception e) {
			String message = new StringBuilder().append("Failed to open application url [").append(pageUrl.toString())
					.append("] due to exception:").append(StackTraceHelper.getStrackTrace(e)).toString();
			logger.info(message);
			logToStdOut(message, stdLogging);
		}
	}

	/**
	 * Log to standard out if enabled.
	 * 
	 * @param message
	 *            message to log.
	 * @param stdLogging
	 *            if string contains the string "true" then message is logged to
	 *            standard out.
	 */
	void logToStdOut(String message, String stdLogging) {
		if (stdLogging == null)
			return;
		if (!stdLogging.equalsIgnoreCase("true"))
			return;
		System.out.println(message);
	}

	/**
	 * Static main method to invoke Jetty starter class.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new JettyStarter(args);
	}
}
