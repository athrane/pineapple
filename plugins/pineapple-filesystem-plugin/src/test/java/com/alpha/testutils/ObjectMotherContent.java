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

import org.apache.log4j.Logger;

import com.alpha.pineapple.plugin.filesystem.model.Filesystem;
import com.alpha.pineapple.plugin.filesystem.model.ObjectFactory;

/**
 * Implementation of the ObjectMother pattern, provides helper functions for
 * unit testing by creating content for operations.
 */
public class ObjectMotherContent {
	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * File system object factory.
	 */
	ObjectFactory fileSystemFactory;

	/**
	 * ObjectMotherContent constructor.
	 */
	public ObjectMotherContent() {
		super();

		// create test case factory
		fileSystemFactory = new com.alpha.pineapple.plugin.filesystem.model.ObjectFactory();
	}

	/**
	 * Create empty file system document.
	 * 
	 * @return empty file system document.
	 */
	public Filesystem createEmptyFilesystem() {

		Filesystem fsDoc = fileSystemFactory.createFilesystem();
		return fsDoc;
	}

}
