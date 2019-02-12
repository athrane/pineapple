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

import static com.alpha.javautils.ArgumentUtils.notNull;

import java.util.ArrayList;

import org.hamcrest.Matcher;

public class ResponsePropertyInfoSetImpl implements ResponsePropertyInfoSet {

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
		notNull(name, "name is undefined");
		notNull(xpath, "xpath is undefined");

		// create property info
		ResponsePropertyInfo property = new ResponsePropertyInfoImpl(name, xpath, intra, inter);

		// add property
		properties.add(property);
	}

}
