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

package com.alpha.pineapple.substitution;

import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Interface for runtime resolution of variables.
 */
public interface VariableResolver {

	/**
	 * Resolve variables in string.
	 * 
	 * @param variables
	 *            variables to for substitution.
	 * @param source
	 *            source string to resolve.
	 * 
	 * @return string with resolved variables.
	 */
	String resolve(Variables variables, String source);

}
