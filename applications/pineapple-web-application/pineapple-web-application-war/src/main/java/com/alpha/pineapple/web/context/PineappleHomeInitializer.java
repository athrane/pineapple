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

import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alpha.javautils.SystemUtils;

/**
 * Implementation of the {@link ServletContextListener} interface which will set
 * the Pineapple Home directory system property if it isn't already set.
 * 
 * The system property is set from the value of the context-param
 * "pineapple.home.dir".
 */
public class PineappleHomeInitializer implements ServletContextListener {

	/**
	 * Default name for log4j properties file.
	 */
	public static final String DEFAULT_CONFIG = "${user.home}/.pineapple";

	/**
	 * Name of context-param in the web.xml which contains the Pineapple Home
	 * directory.
	 */
	static final String PARAM_NAME = "pineapple.home.dir";

	/**
	 * Logger object
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Java System properties.
	 */
	Properties systemProperties;

	/**
	 * Java system utilities.
	 */
	SystemUtils systemUtils;

	/**
	 * PineappleHomeInitializer default constructor.
	 */
	public PineappleHomeInitializer() {
		this(new SystemUtils(), System.getProperties());
	}

	/**
	 * PineappleHomeInitializer constructor
	 * 
	 * @param systemUtils
	 *            System utilities.
	 * @param systemProperties
	 *            System properties.
	 */
	public PineappleHomeInitializer(SystemUtils systemUtils, Properties systemProperties) {
		this.systemUtils = systemUtils;
		this.systemProperties = systemProperties;
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {

		// exit if Pineapple home system property is defined.
		if (systemUtils.isPineappleHomeDefined(systemProperties))
			return;

		// get value from web.xml
		String descriptorValue = sce.getServletContext().getInitParameter(PARAM_NAME);

		// use default value if undefined
		if (null == descriptorValue) {
			descriptorValue = DEFAULT_CONFIG;
		}

		// do variable substitution of system properties
		String[] matches = StringUtils.substringsBetween(descriptorValue, "${", "}");

		// iterate over matches
		for (String match : matches) {

			// get system property
			String propertyValue = systemUtils.getSystemProperty(match, systemProperties);

			// replace value
			String searchString = "${" + match + "}";

			descriptorValue = StringUtils.replace(descriptorValue, searchString, propertyValue);
		}

		// set Pineapple home system property
		systemProperties.setProperty(PARAM_NAME, descriptorValue);
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
