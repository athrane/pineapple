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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Helper class which provides helper functions for exception handling.
 */
public class StackTraceHelper {

    /**
     * Converts stack trace to string.
     * 
     * @param t
     *            Throwable which contains the stack trace.
     * 
     * @return Strings containing a stack trace.
     */
    public static final String getStrackTrace(Throwable t) {
	StringWriter stringWriter = new StringWriter();
	PrintWriter printWriter = new PrintWriter(stringWriter, true);
	t.printStackTrace(printWriter);
	printWriter.flush();
	stringWriter.flush();
	return stringWriter.toString();
    }

    /**
     * Get the current method name. The name is extracted from the stack trace
     * of the current thread.
     * 
     * @return the name of the current method.
     */
    public static final String getMethodName() {

	// this method isn't matched yet
	boolean isFound = false;

	// get stack trace for current thread
	StackTraceElement[] ste = Thread.currentThread().getStackTrace();

	// iterate over the stack trace elements
	for (StackTraceElement s : ste) {

	    // return method if previous method was matched
	    if (isFound) {
		return s.getMethodName();
	    }

	    // flag this method as found, to return next method in stack
	    isFound = s.getMethodName().equals("getMethodName");
	}

	// return null method name of no method as matched.
	return "";
    }
}
