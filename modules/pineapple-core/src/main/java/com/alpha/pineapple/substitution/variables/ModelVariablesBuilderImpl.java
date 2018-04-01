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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.module.model.Models;
import com.alpha.pineapple.model.module.model.Variable;
import com.alpha.pineapple.substitution.VariableSubstitutionException;

/**
 * Implementation of the {@linkplain ModelVariablesBuilder} interface.
 */
public class ModelVariablesBuilderImpl implements ModelVariablesBuilder {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Message provider for I18N support.
	 */
	@Resource
	MessageProvider messageProvider;

	/**
	 * Module model.
	 */
	Models model;

	@Override
	public Variables getVariables() throws VariableSubstitutionException {

		// if model is undefined throw exception
		if (model == null) {
			String message = messageProvider.getMessage("mvb.getvariables_model_notdefineded_error");
			throw new VariableSubstitutionException(message);
		}

		// create variables data object
		Map<String, String> variablesMap = new HashMap<String, String>();
		DefaultVariablesImpl variables = new DefaultVariablesImpl(variablesMap);

		// get variables container
		com.alpha.pineapple.model.module.model.Variables modelVariables = model.getVariables();
		if (modelVariables == null)
			return variables;

		// get variables list
		List<Variable> variableList = modelVariables.getVariable();
		if (variableList == null)
			return variables;

		// iterate over model vars
		for (Variable modelVariable : variableList) {

			// handle null key case
			if (modelVariable.getKey() == null) {
				String message = messageProvider.getMessage("mvb.initialize_null_key_warning");
				logger.info(message);
				continue;
			}

			// handle empty key case
			if (modelVariable.getKey().isEmpty()) {
				String message = messageProvider.getMessage("mvb.initialize_empty_key_warning");
				logger.info(message);
				continue;
			}

			// handle null value case
			if (modelVariable.getValue() == null) {
				Object[] args = { modelVariable.getKey() };
				String message = messageProvider.getMessage("mvb.initialize_null_value_warning", args);
				logger.info(message);
				continue;
			}

			// log debug message
			if (logger.isDebugEnabled()) {
				Object[] args = { modelVariable.getKey(), modelVariable.getValue() };
				String message = messageProvider.getMessage("mvb.initialize_variable_info", args);
				logger.debug(message);
			}

			// add variable
			variablesMap.put(modelVariable.getKey(), modelVariable.getValue());

		}

		return variables;
	}

	@Override
	public void setModel(Models model) {
		this.model = model;
	}

}
