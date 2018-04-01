/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2012 Allan Thrane Andersen.
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

package com.alpha.pineapple.substitution.proxy;

/**
 * Test class to test proxy.
 */
public class ProxiedTestClass {

	public ProxiedTestClass() {
	};

	/**
	 * Return value.
	 * 
	 * @param value
	 * 
	 * @return value.
	 */
	public <T> T getObject(T value) {
		return value;
	}

	/**
	 * Return string value.
	 * 
	 * @param value
	 * 
	 * @return value.
	 */
	public String getString(String value) {
		return value;
	}

	/**
	 * Return child object.
	 * 
	 * @return child object.
	 */
	public ProxiedTestClass getChildObject() {
		return new ProxiedTestClass();
	}

}
