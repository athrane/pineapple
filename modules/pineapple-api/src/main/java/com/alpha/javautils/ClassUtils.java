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

package com.alpha.javautils;

import org.apache.log4j.Logger;

/**
 * Helper class for getting reflection info about Java classes.
 */
public class ClassUtils {

    /**
     * Logger object.
     */
    static Logger logger = Logger.getLogger(ClassUtils.class.getName());

    /**
     * Validate that class with queried name exists in class loader.
     * 
     * @param className
     *            Fully qualified class name to verify existence of.
     * 
     * @return true if the class exists in the class loader. Returns false if
     *         the name doesn't exist, the name is empty or null.
     */
    public static boolean isValidClass(String className) {
	try {
	    // exit if name is null
	    if (className == null)
		return false;

	    // try to get class from class loader.
	    Class<?> classInQuestion = Class.forName(className);

	    if (classInQuestion != null) {

		// log debug message
		if (logger.isDebugEnabled()) {
		    StringBuilder message = new StringBuilder();
		    message.append("Successfully verified existence of class <");
		    message.append(className);
		    message.append(">. ");
		    logger.debug(message.toString());
		}
		return true;
	    }

	    // log debug message
	    if (logger.isDebugEnabled()) {
		StringBuilder message = new StringBuilder();
		message.append("Failed to verify existence of class <");
		message.append(className);
		message.append(">. ");
		logger.debug(message.toString());
	    }
	    return false;

	} catch (ClassNotFoundException e) {
	    // log debug message
	    if (logger.isDebugEnabled()) {
		StringBuilder message = new StringBuilder();
		message.append("Failed to verify existence of class <");
		message.append(className);
		message.append(">. ");
		logger.debug(message.toString());
	    }
	    return false;
	}

    }

}
