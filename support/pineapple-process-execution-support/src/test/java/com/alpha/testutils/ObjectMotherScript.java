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

import java.io.File;

import junit.framework.AssertionFailedError;

import org.apache.commons.exec.OS;

/**
 * An implementation of the ObjectMother pattern. The class provides helper 
 * functions for creating and resolving test scripts which can be used in unit tests.  
 */
public class ObjectMotherScript {

	/**
	 * Resolve scripts based on OS.
	 * 
	 * @param script script name.
	 * 
	 * @return OS specific script name.
	 */
	public File resolveOsSpecificScriptName(String script) {

		if (OS.isFamilyWindows()) {
			return new File(script + ".cmd");
		} else if (OS.isFamilyUnix()) {
			return new File(script + ".sh");
		} else if (OS.isFamilyOpenVms()) {
			return new File(script + ".dcl");
		} else {
			throw new AssertionFailedError("Script not supported for this OS");
		}
	}
	
}
