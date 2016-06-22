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

import org.apache.log4j.Logger;

import com.alpha.pineapple.i18n.MessageProvider;
import com.alpha.pineapple.model.configuration.Property;
import com.alpha.pineapple.model.configuration.Resource;
import com.alpha.pineapple.session.Session;
import com.alpha.pineapple.substitution.VariableSubstitutionException;

/**
 * Implementation of the {@linkplain ResourceVariablesBuilder} interface.
 */
public class ResourceVariablesBuilderImpl implements ResourceVariablesBuilder {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Message provider for I18N support.
     */
    @javax.annotation.Resource
    MessageProvider messageProvider;

    /**
     * Resource.
     */
    Resource resource;

    /**
     * Resource ID.
     */
    String resourceId;

    @Override
    public void setResource(Resource resource) {
	this.resource = resource;
    }

    @Override
    public Variables getVariables() throws VariableSubstitutionException {

	// if resource is undefined throw exception
	if (resource == null) {
	    String message = messageProvider.getMessage("rvb.getvariables_resouce_notdefineded_error");
	    throw new VariableSubstitutionException(message);
	}

	// create variables data object
	Map<String, String> variablesMap = new HashMap<String, String>();
	DefaultVariablesImpl variables = new DefaultVariablesImpl(variablesMap);

	// get property list
	List<Property> propertyList = resource.getProperty();
	if (propertyList == null)
	    return variables;

	// iterate over properties
	for (Property property : propertyList) {

	    // handle null key case
	    if (property.getKey() == null) {
		Object[] args = { resource.getId() };
		String message = messageProvider.getMessage("rvb.initialize_null_key_warning", args);
		logger.info(message);
		continue;
	    }

	    // handle empty key case
	    if (property.getKey().isEmpty()) {
		Object[] args = { resource.getId() };
		String message = messageProvider.getMessage("rvb.initialize_empty_key_warning", args);
		logger.info(message);
		continue;
	    }

	    // handle null value case
	    if (property.getValue() == null) {
		Object[] args = { resource.getId(), property.getKey() };
		String message = messageProvider.getMessage("rvb.initialize_null_value_warning", args);
		logger.info(message);
		continue;
	    }

	    // log debug message
	    if (logger.isDebugEnabled()) {
		Object[] args = { property.getKey(), property.getValue(), resource.getId() };
		String message = messageProvider.getMessage("rvb.initialize_variable_info", args);
		logger.debug(message);
	    }

	    // add property
	    variablesMap.put(property.getKey(), property.getValue());
	}

	return variables;
    }

}
