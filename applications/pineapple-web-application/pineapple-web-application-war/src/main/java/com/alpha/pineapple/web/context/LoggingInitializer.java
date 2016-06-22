/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2016 Allan Thrane Andersen..
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

package com.alpha.pineapple.web.context;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * Implementation of the {@link ServletContextListener} interface which will
 * listen for contextInitialized and initialize Log4J with the properties file
 * pointed to bye the context-param "log4jConfig".
 * 
 * The files will be searched for first in the class path, and then in the
 * physical path. (unless a full URI is provided).
 *
 */
public class LoggingInitializer implements ServletContextListener {

    /**
     * Default name for log4j properties file.
     */
    public static final String DEFAULT_CONFIG = "log4j.properties";

    /**
     * Name of context-param in the web.xml which contains the path/file name of
     * the log4j configuration file. Example value:
     * /WEB-INF/config/log4j.properties
     */
    private static final String PARAM_NAME = "log4jConfig";

    /**
     * Default constructor.
     */
    public LoggingInitializer() {
    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
	String config = sce.getServletContext().getInitParameter(PARAM_NAME);
	if (null == config) {
	    config = DEFAULT_CONFIG;
	}
	URL url = determineUrl(sce.getServletContext(), config);
	initializeLog4j(url);
    }

    private void initializeLog4j(URL url) {
	if (null != url) {
	    PropertyConfigurator.configure(url);
	}
    }

    private URL determineUrl(ServletContext ctx, String config) {
	URL ret = null;
	try {
	    ret = new URL(config);
	} catch (MalformedURLException e) {
	    try {
		ret = ctx.getResource(config);
	    } catch (MalformedURLException e2) {
		File f = new File(config);
		try {
		    ret = f.toURL();
		} catch (MalformedURLException e1) {
		    System.err.println("Could not read log4j config: " + config + "\nWill default: " + DEFAULT_CONFIG);
		    e1.printStackTrace();
		    ret = Thread.currentThread().getContextClassLoader().getResource(DEFAULT_CONFIG);
		}
	    }
	}
	return ret;
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
