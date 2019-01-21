/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen.
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

package com.alpha.pineapple.docker.utils;

import com.alpha.pineapple.docker.model.rest.JsonRawMessage;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Extension of the {@linkplain PropertyNamingStrategy} which implements a XX 
 * strategy for JSON marshalled object attributes.
 *
 */
public class MultipleRootElementsStyleStrategy extends PropertyNamingStrategy {
	
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 6837299198068815802L;

	@Override
	public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
		return defaultName;		
	}

	@Override
	public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		return defaultName;
	}

	@Override
	public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {

		// get class name
		String className = method.getDeclaringClass().getName();
		
		// handle case for JsonRawMessage.id
		// - map received JSON property "ID" to JsonRawMessage.id as defined in the
		// Docker schema
		if (className.equals(JsonRawMessage.class.getName())) {
			if (defaultName.equals("id"))
				return "ID";
		}
		
		return defaultName;
	}
		
}
