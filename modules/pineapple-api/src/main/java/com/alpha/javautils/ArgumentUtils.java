/*******************************************************************************
 * Pineapple - a tool to install, configure and test Java web applications 
 * and infrastructure. 
 * 
 * Copyright (C) 2007-2019 Allan Thrane Andersen..
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
 * Helper class for validating method arguments.
 */
public class ArgumentUtils {
	
	/**
	 * Not null exception message.
	 */
	static final String VALIDATED_OBJECT_IS_NULL = "Validated object is null";

	/**
	 * Validate that the specified argument is not null.
	 *
	 * @param object  the object to validate
	 * @param message the exception message if the object is null.
	 * 
	 * @return the validated object (never null for method chaining)
	 * 
	 * @throws IllegalArgumentException if the object is null.
	 */
	public static <T> T notNull(final T object, final String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
		return object;
	}

	/**
	 * Validate that the specified argument is not null.
	 *
	 * @param object  the object to validate
	 * 
	 * @return the validated object (never null for method chaining)
	 * 
	 * @throws IllegalArgumentException if the object is null.
	 */
	public static <T> T notNull(final T object) {
		if (object == null) {
			throw new IllegalArgumentException(VALIDATED_OBJECT_IS_NULL);
		}
		return object;
	}
	
}
