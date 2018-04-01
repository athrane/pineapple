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

import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Factory for creation of object proxies which supports variable substitution.
 */
public interface VariableSubstitutedProxyFactory {

	/**
	 * Initialize factory with variable set used for variable substitution.
	 * 
	 * @param variables
	 *            variable set used for variable substitution.
	 */
	void initialize(Variables variables);

	/**
	 * Decorate target object with proxy to support variable substitution. All
	 * attributes on the target object (e..g child objects) will also be proxied and
	 * thus have variable substitution enabled.
	 * 
	 * @param targetObject
	 *            target object
	 * 
	 * @return proxy decorating the target object.
	 * 
	 * @throws IllegalStateException
	 *             if factory isn't initialized prior to usage.
	 */
	public <T> T decorateWithProxy(T targetObject);

}
