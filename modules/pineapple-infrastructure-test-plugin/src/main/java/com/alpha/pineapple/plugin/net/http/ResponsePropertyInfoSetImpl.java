/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2013 Allan Thrane Andersen..
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

package com.alpha.pineapple.plugin.net.http;

import java.util.ArrayList;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hamcrest.Matcher;

public class ResponsePropertyInfoSetImpl implements ResponsePropertyInfoSet {

	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * List of response property info objects.
	 */
	ArrayList<ResponsePropertyInfo> properties;

	public ResponsePropertyInfoSetImpl() {
		this.properties = new ArrayList<ResponsePropertyInfo>();
	}

	public ResponsePropertyInfo[] getProperties() {
		return properties.toArray(new ResponsePropertyInfo[properties.size()]);
	}

	public void addProperty(String name, String xpath, Matcher<?> intra, Matcher<?> inter) {

		// validate parameters
		Validate.notNull(name, "name is undefined");
		Validate.notNull(xpath, "xpath is undefined");

		// create property info
		ResponsePropertyInfo property = new ResponsePropertyInfoImpl(name, xpath, intra, inter);

		// add property
		properties.add(property);

		// log debug message
		if (logger.isDebugEnabled()) {
			StringBuilder message = new StringBuilder();
			message.append("Successfully registered response property with name <");
			message.append(name);
			message.append(">, XPath expression <");
			message.append(xpath);
			message.append(">, intra matcher <");
			message.append(intra);
			message.append("> and inter matcher <");
			message.append(inter);
			message.append(">.");
			logger.debug(message.toString());
		}

	}

}
