/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2015 Allan Thrane Andersen..
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

package com.alpha.javautils;

/**
 * Helper class for handling strings.
 */
public class StringUtils {

	/**
	 * Create string representation of an object on a single line.
	 * 
	 * If the string representation of the object contains newline then the string
	 * is abbreviated at the new line and "..." is appended. If the object is null
	 * then null is returned.
	 * 
	 * @param targetObject
	 *            the object to create string representation for.
	 * @return Create string representation of an object on a single line.
	 */
	public static String toStringOneLine(Object targetObject) {

		// handle null case
		if (targetObject == null)
			return null;

		// get value as string
		String valueAsString = targetObject.toString();

		// if string contains newline then abbreviate at new line
		if (valueAsString.contains("\n")) {
			int width = valueAsString.indexOf("\n");
			return org.apache.commons.lang.StringUtils.abbreviate(valueAsString, width);
		}

		return valueAsString;
	}

	/**
	 * Removes enclosing brackets [..source..] if string starts and ends with
	 * brackets.
	 * 
	 * If the string doesn't starts and ends with brackets then the original string
	 * is returned.
	 * 
	 * @param source
	 *            string containing brackets.
	 * 
	 * @return string with brackets remomved.
	 */
	public static String removeEnclosingBrackets(String source) {
		if (source == null)
			return source;
		if (source.isEmpty())
			return source;
		if (!source.startsWith("["))
			return source;
		if (!source.endsWith("]"))
			return source;
		return source.substring(1, source.length() - 1);
	}

	/**
	 * Create report friendly string from an object.
	 * 
	 * If the string representation of the object contains newline then the string
	 * is abbreviated at the new line and "..." is appended. If the object is null
	 * then the string "null" is returned.
	 * 
	 * @param value
	 *            value to create string from.
	 * 
	 * @return report friendly string from a value received from REST.
	 */
	public static String createReportFriendlyString(Object value) {
		if (value == null)
			return "null";
		return toStringOneLine(value);
	}

}
