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

package com.alpha.pineapple.command.initialization;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Helper class for {@link CommandInitializerImpl} which do value validation
 * based on the of {@link ValidateValue} annotations on a field.
 *
 */
class ValueValidator {
	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Validate context value based on validation policies defined by
	 * {@link ValidateValue} annotation on field. If no validation annotation is
	 * found then validation is skipped.
	 * 
	 * @param value
	 *            Context value which is validated.
	 * @param field
	 *            The field who might define a validation annotation.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If value validation fails.
	 */
	public void validateValue(Object value, Field field) throws CommandInitializationFailedException {
		// validate parameters
		Validate.notNull(field, "field is undefined");

		// exit if no validation annotation is defined on field
		if (!field.isAnnotationPresent(ValidateValue.class)) {

			// exit
			return;
		}

		// get validation annotation
		ValidateValue annotation = field.getAnnotation(ValidateValue.class);

		// get validation policies
		ValidationPolicy[] policies = annotation.value();

		// iterate over defines policies on field.
		for (ValidationPolicy policy : policies) {

			switch (policy) {

			case NOT_NULL:
				validateNotNull(value, field);
				break;

			case NOT_EMPTY:
				validateNotEmpty(value, field);
				break;

			default:
			}

		}
	}

	/**
	 * Validate the {@link ValidationPolicy.NOT_NULL} policy.
	 *
	 * @param Value
	 *            The value which is validated.
	 * @param field
	 *            the field which defined the context key.
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	void validateNotNull(Object value, Field field) throws CommandInitializationFailedException {
		try {
			// validate
			Validate.notNull(value);
		} catch (IllegalArgumentException e) {

			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Validation policy <");
			message.append(ValidationPolicy.NOT_NULL);
			message.append("> failed on field <");
			message.append(field);
			message.append("> with context value <");
			message.append(value);
			message.append(">.");

			// throw exception
			throw new CommandInitializationFailedException(message.toString());

		}
	}

	/**
	 * Validate the {@link ValidationPolicy.NOT_EMPTY} policy.
	 *
	 * @param Value
	 *            The value which is validated.
	 * @param field
	 *            the field which defined the context key.
	 * @throws CommandInitializationFailedException
	 *             If validation fails.
	 */
	void validateNotEmpty(Object value, Field field) throws CommandInitializationFailedException {
		try {
			// do initial not-null validation
			Validate.notNull(value);

			// validate
			if (value instanceof String) {
				Validate.notEmpty((String) value);
			}
			if (value instanceof Map) {
				Validate.notEmpty((Map) value);
			}
			if (value instanceof Collection) {
				Validate.notEmpty((Collection) value);
			}
			if (value instanceof Object[]) {
				Validate.notEmpty((Object[]) value);
			}
			if (value instanceof File) {
				File file = (File) value;
				int length = file.getName().length();
				Validate.isTrue(length != 0);
				length = file.getPath().length();
				Validate.isTrue(length != 0);
			}
		} catch (IllegalArgumentException e) {

			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Validation policy <");
			message.append(ValidationPolicy.NOT_NULL);
			message.append("> failed on field <");
			message.append(field);
			message.append("> with context value <");
			message.append(value);
			message.append(">.");

			// throw exception
			throw new CommandInitializationFailedException(message.toString());

		}
	}

}
