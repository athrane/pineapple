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

import org.hamcrest.Matcher;

/**
 * Defines a collection of {@link ResponsePropertyInfo} which can tested
 * against the content of a {@link HttpInvocationsSet}.  
 */
public interface ResponsePropertyInfoSet {

	/**
	 * Get the registered property objects as an array.
	 * 
	 * @return the registered property objects as an array.
	 */
	ResponsePropertyInfo[] getProperties();

	/**
	 * Register a response property object.
	 * 
	 * @param name The name of the property.
	 * @param xpath XPath expression which defines where the value can be
	 * extracted from a HTTP response.   
	 * @param intra Intra sequence matcher. 
	 * @param inter Inter sequence matcher.
	 */
	void addProperty(String name, String xpath, Matcher<?> intra, Matcher<?> inter);
}
