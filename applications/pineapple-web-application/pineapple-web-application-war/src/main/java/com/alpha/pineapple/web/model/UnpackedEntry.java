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

package com.alpha.pineapple.web.model;

/**
 * Unpacked file entry in ZIP archive use for unpacked entry grid in upload
 * module module view.
 * 
 */
public class UnpackedEntry {

	/**
	 * Entry name.
	 */
	String name;

	/**
	 * Entry type.
	 */
	String type;

	/**
	 * Entry size in KB.
	 */
	String size;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            Entry name.
	 * @param type
	 *            Entry type.
	 * @param size
	 *            Entry size in KB.
	 */
	public UnpackedEntry(String name, String type, String size) {
		super();
		this.name = name;
		this.type = type;
		this.size = size;
	}

	/**
	 * Return name.
	 * 
	 * @return name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return type.
	 * 
	 * @return type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Return size.
	 * 
	 * @return size.
	 */
	public String getSize() {
		return size;
	}

}
