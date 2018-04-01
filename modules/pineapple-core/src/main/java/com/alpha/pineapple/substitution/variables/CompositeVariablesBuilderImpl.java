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

package com.alpha.pineapple.substitution.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.substitution.VariableSubstitutionException;

/**
 * Implementation of the {@linkplain CompositeVariablesBuilder} interface.
 */
public class CompositeVariablesBuilderImpl implements CompositeVariablesBuilder {

	/**
	 * Logger object
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Builder names.
	 */
	ArrayList<String> builderNames = new ArrayList<String>();

	/**
	 * Builders.
	 */
	ArrayList<VariablesBuilder> builders = new ArrayList<VariablesBuilder>();

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	@Override
	public Variables getVariables() throws VariableSubstitutionException {

		// create variables data object
		Map<String, String> variablesMap = new HashMap<String, String>();
		DefaultVariablesImpl variables = new DefaultVariablesImpl(variablesMap);

		// add prefixed variables
		int index = 0;
		for (VariablesBuilder builder : builders) {
			String name = builderNames.get(index);
			Map<String, String> map = builder.getVariables().getMap();
			addPrefixedVariables(variablesMap, name, map);
			index++;
		}

		// add variables - considering precedence
		for (VariablesBuilder builder : builders) {
			Map<String, String> map = builder.getVariables().getMap();
			addVariables(variablesMap, map);
		}

		return variables;
	}

	@Override
	public void addBuilder(String name, VariablesBuilder builder) {
		builderNames.add(name);
		builders.add(builder);
	}

	/**
	 * Add prefixed variables to variables map. A variable isn't if is already
	 * defined.
	 * 
	 * @param variablesMap
	 *            target map.
	 * @param prefix
	 *            key prefix.
	 * @param map
	 *            source map.
	 */
	void addPrefixedVariables(Map<String, String> variablesMap, String prefix, Map<String, String> map) {
		for (String key : map.keySet()) {
			String prefixedKey = new StringBuilder().append(prefix).append(".").append(key).toString();

			if (!variablesMap.containsKey(prefixedKey)) {

				// log debug message
				if (logger.isDebugEnabled()) {
					Object[] args = { prefixedKey, map.get(key) };
					String message = messageProvider.getMessage("cvb.initialize_prefixed_variable_info", args);
					logger.debug(message);
				}

				variablesMap.put(prefixedKey, map.get(key));
			}
		}
	}

	/**
	 * Add variables to variables map.
	 * 
	 * @param variablesMap
	 *            target map.
	 * @param map
	 *            source map.
	 */
	void addVariables(Map<String, String> variablesMap, Map<String, String> map) {
		for (String key : map.keySet()) {
			if (!variablesMap.containsKey(key)) {

				// log debug message
				if (logger.isDebugEnabled()) {
					Object[] args = { key, map.get(key) };
					String message = messageProvider.getMessage("cvb.initialize_variable_info", args);
					logger.debug(message);
				}

				variablesMap.put(key, map.get(key));
			}
		}
	}

}
