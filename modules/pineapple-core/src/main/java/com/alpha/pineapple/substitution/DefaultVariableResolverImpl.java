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

import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;

import com.alpha.pineapple.substitution.variables.Variables;

/**
 * Can resolve variables.
 */
/**
 * Implementation of {@linkplain VariableResolver} interface which support
 * substitution of variables.
 */

public class DefaultVariableResolverImpl implements VariableResolver {

	@Override
	public String resolve(Variables variables, String source) {
		Map<String, String> variablesMap = variables.getMap();
		StrSubstitutor substitutor = new StrSubstitutor(variablesMap);
		return substitutor.replace(source);
	}

}
