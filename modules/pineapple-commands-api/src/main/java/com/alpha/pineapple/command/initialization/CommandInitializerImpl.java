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

import java.lang.reflect.Field;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.reflection.AnnotationFinder;
import com.alpha.javautils.reflection.ReflectionHelper;

/**
 * Implementation of the {@link CommandInitializer} interface.
 */
public class CommandInitializerImpl implements CommandInitializer {
	/**
	 * Logger object.
	 */
	Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * Reflection helper
	 */
	ReflectionHelper helper;

	/**
	 * Annotation finder.
	 */
	AnnotationFinder finder;

	/**
	 * Value validation object.
	 */
	ValueValidator validator;

	/**
	 * CommandInitializerImpl no-arg constructor.
	 */
	public CommandInitializerImpl() {
		this(new ReflectionHelper(), new AnnotationFinder(), new ValueValidator());
	}

	/**
	 * CommandInitializerImpl constructor.
	 * 
	 * @param helper
	 *            Reflection helper object.
	 * @param finder
	 *            Annotation finder object.
	 */
	public CommandInitializerImpl(ReflectionHelper helper, AnnotationFinder finder, ValueValidator validator) {
		this.helper = helper;
		this.finder = finder;
		this.validator = validator;
	}

	public void initialize(Context context, Command command) throws CommandInitializationFailedException {
		// validate arguments
		Validate.notNull(context, "context is undefined.");
		Validate.notNull(command, "command is undefined.");

		// variable used for exception handling
		Field exceptionHandlingField = null;

		// variable to store context value
		Object contextValue = null;

		try {
			// search for annotation
			Field[] fields = finder.findAnnotatedFields(command, Initialize.class);

			// iterate over the fields
			for (Field field : fields) {
				// store for exception handling purposes
				exceptionHandlingField = field;

				// get context key
				String contextKey = getContextKey(context, field);

				// lookup value from context
				contextValue = getContextValue(context, contextKey);

				// validate value
				validator.validateValue(contextValue, field);

				// initialize command field with value
				helper.setFieldValue(command, field, contextValue);
			}
		} catch (IllegalArgumentException e) {
			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Setter invocation failed for field <");
			message.append(exceptionHandlingField);
			message.append("> on command < ");
			message.append(command);
			message.append("> with value <");
			message.append(contextValue);
			message.append(">. Inspect embbeded exception for details.");

			// throw exception
			throw new CommandInitializationFailedException(message.toString(), e);
		} catch (SecurityException e) {
			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Setter invocation failed for field <");
			message.append(exceptionHandlingField);
			message.append("> on command < ");
			message.append(command);
			message.append("> with value <");
			message.append(contextValue);
			message.append(">. Inspect embbeded exception for details.");

			// throw exception
			throw new CommandInitializationFailedException(message.toString(), e);
		} catch (IllegalAccessException e) {
			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Setter invocation failed for field <");
			message.append(exceptionHandlingField);
			message.append("> on command < ");
			message.append(command);
			message.append("> with value <");
			message.append(contextValue);
			message.append(">. Inspect embbeded exception for details.");

			// throw exception
			throw new CommandInitializationFailedException(message.toString(), e);
		}
	}

	/**
	 * Look up context key from annotation.
	 * 
	 * @param field
	 *            The field containing the annotation whose value is read as key.
	 * 
	 * @return Context key from {@link Initialize} annotation.
	 * 
	 * @throws CommandInitializationFailedException
	 *             If key isn't defined in the context.
	 */
	String getContextKey(Context context, Field field) throws CommandInitializationFailedException {
		// get annotation
		Initialize annotation;
		annotation = field.getAnnotation(Initialize.class);

		// get annotation value
		String contextKey = annotation.value();

		// validate whether context is defined
		if (!context.containsKey(contextKey)) {
			// create error message
			StringBuilder message = new StringBuilder();
			message.append("Context key not found. ");
			message.append("The key <");
			message.append(contextKey);
			message.append("> defined by the < ");
			message.append(Initialize.class.getName());
			message.append("> annotation on the field <");
			message.append(field);
			message.append("> was not found in the context.");

			// throw exception
			throw new CommandInitializationFailedException(message.toString());
		}

		return contextKey;
	}

	/**
	 * Look up value from context object using key.
	 * 
	 * @param context
	 *            The context.
	 * @param context
	 *            key The key used to look up value in the context.
	 * 
	 * @return Value from context.
	 */
	Object getContextValue(Context context, String contextKey) {
		// lookup value from context
		Object contextValue = context.get(contextKey);

		return contextValue;
	}

}
