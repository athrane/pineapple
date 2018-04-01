/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen.
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
 * Property for rendering execution result properties.
 */
public class ExecutionResultProperty {

	/**
	 * Key.
	 */
	String key;

	/**
	 * Value.
	 */
	String value;

	/**
	 * ExecutionResultProperty constructor.
	 * 
	 * @param key
	 *            property key.
	 * @param value
	 *            property value.
	 */
	public ExecutionResultProperty(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Return key.
	 * 
	 * @return key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Return value.
	 * 
	 * @return value.
	 */
	public String getValue() {
		return value;
	}

}
