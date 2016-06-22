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

package com.alpha.javautils.reflection;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.alpha.javautils.StackTraceHelper;

/**
 * Helper class which provides helper functions for reflection.
 */
public class ReflectionHelper {

    /**
     * Logger object.
     */
    Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * ReflectionHelper no-arg constructor.
     */
    public ReflectionHelper() {
	super();
    }

    /**
     * Create object instance for class which takes single String as constructor
     * argument.
     * 
     * @param classs
     *            Class to instantiate object from.
     * @param argument
     *            String argument which should be used as parameter when
     *            invoking the constructor.
     * 
     * @return object instance of supplied class.
     * 
     * @throws InstantiationError
     *             If instance creation fails.
     */
    public Object createObject(Class<?> classs, String argument) {
	// validate parameters
	Validate.notNull(classs, "classs is undefined.");
	Validate.notNull(argument, "argument is undefined.");

	try {
	    // create string constructor argument
	    Class[] classArgs = { String.class };

	    // get constructor
	    Constructor<?> constructor;
	    constructor = classs.getConstructor(classArgs);
	    Object[] constructorArgs = new Object[] { argument };
	    return constructor.newInstance(constructorArgs);
	} catch (Exception e) {
	    // create error message
	    StringBuilder message = new StringBuilder();
	    message.append("Object instantiation failed for class <");
	    message.append(classs);
	    message.append("> with exception <");
	    message.append(StackTraceHelper.getStrackTrace(e));
	    message.append(">.");

	    // throw
	    throw new InstantiationError(message.toString());
	}
    }

    /**
     * Create object instance with no-arg constructor.
     * 
     * @param className
     *            Class name to create instance of.
     * 
     * @return object instance of requested class.
     * 
     * @throws InstantiationError
     *             If instance creation fails.
     */
    public Object createObject(String className) {
	// validate parameters
	Validate.notNull(className, "className is undefined.");
	Validate.notEmpty(className, "className is empty string.");

	try {
	    Class<?> classs = Class.forName(className);
	    return classs.newInstance();
	} catch (Exception e) {
	    // create error message
	    StringBuilder message = new StringBuilder();
	    message.append("Object instantiation failed for class <");
	    message.append(className);
	    message.append("> with exception <");
	    message.append(StackTraceHelper.getStrackTrace(e));
	    message.append(">.");

	    // throw
	    throw new InstantiationError(message.toString());
	}
    }

    /**
     * Get setter method from object. The target objects is searched for a
     * setter method with the name set + &lt; field-name &gt;.
     * 
     * @param targetObject
     *            The object which is searched for a setter method.
     * @param field
     *            Field on target object whose name is used to search for a
     *            corresponding setter method.
     * 
     * @return Setter method from target object.
     * 
     * @throws SecurityException
     *             If setter method resolution fails.
     * @throws NoSuchMethodException
     *             If setter method resolution fails.
     */
    public Method getSetterMethod(Object targetObject, Field field) throws SecurityException, NoSuchMethodException {
	// get field name
	String fieldName = field.getName();

	// create setter method name
	String setterName = createSetterMethodName(fieldName);

	// log debug message
	if (logger.isDebugEnabled()) {
	    // create error message
	    StringBuilder message = new StringBuilder();
	    message.append("Created setter method name <");
	    message.append(setterName);
	    message.append(">.");

	    logger.debug(message.toString());
	}

	// get class for target object
	Class<? extends Object> targetClass = targetObject.getClass();

	// define setter parameter type
	Class[] setterParamsType = { field.getType() };

	// lookup setter method on target class
	Method setterMethod;
	setterMethod = targetClass.getMethod(setterName.toString(), setterParamsType);

	// log debug message
	if (logger.isDebugEnabled()) {
	    // create error message
	    StringBuilder message = new StringBuilder();
	    message.append("Looked up setter method <");
	    message.append(setterMethod);
	    message.append("> on target object <");
	    message.append(targetObject);
	    message.append("> from field <");
	    message.append(field);
	    message.append(">.");

	    logger.debug(message.toString());
	}

	return setterMethod;
    }

    /**
     * Create setter method name from the name of the field.
     * 
     * @param String
     *            fieldName The name of the field..
     * 
     * @return Setter method name .
     */
    String createSetterMethodName(String fieldName) {
	// validate parameters
	Validate.notNull(fieldName);

	// create setter method name
	StringBuilder setterName = new StringBuilder();
	setterName.append("set");
	setterName.append(StringUtils.capitalize(fieldName));
	return setterName.toString();
    }

    /**
     * Invoke setter method on target object.
     * 
     * @param targetObject
     *            The object on which the setter method is invoked.
     * @param field
     *            Field on target object whose name is used to search for a
     *            setter method.
     * @param setterParameter
     *            The value which used when the setter method is invoked.
     * 
     * @throws SecurityException
     *             If setter method invocation fails.
     * @throws NoSuchMethodException
     *             If setter method invocation fails.
     * @throws IllegalArgumentException
     *             If setter method invocation fails.
     * @throws IllegalAccessException
     *             If setter method invocation fails.
     * @throws InvocationTargetException
     *             If setter method invocation fails.
     */
    public void invokeSetterMethod(Object targetObject, Field field, Object setterParameter) throws SecurityException,
	    NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	// validate parameters
	Validate.notNull(targetObject);
	Validate.notNull(field);

	// lookup setter
	Method setterMethod = getSetterMethod(targetObject, field);

	// initialize parameter
	Object[] setterArgs = new Object[] { setterParameter };

	// invoke setter
	setterMethod.invoke(targetObject, setterArgs);

	// log debug message
	if (logger.isDebugEnabled()) {
	    // create error message
	    StringBuilder message = new StringBuilder();
	    message.append("Invoked setter method <");
	    message.append(setterMethod);
	    message.append("> on target object <");
	    message.append(targetObject);
	    message.append("> with parameter <");
	    message.append(setterParameter);
	    message.append(">.");

	    logger.debug(message.toString());
	}
    }

    /**
     * <p>
     * Set field value on on target object.
     * </p>
     * 
     * <p>
     * If the type of the field is primitive (i.e. int) and the value is
     * <code>null</code> then the assign is skipped. As null assignments to
     * primitives don't go well
     * </p>
     *
     * <p>
     * If the type of the value is String and the type of the field is
     * primitive, then then String value to converted to the primitive type
     * before the value is assigned.
     * </p>
     * 
     * <p>
     * If the type of the value is String and the type of the field is
     * <code>String[]</code>, then then String value to converted to the string
     * array entries before the value is assigned.
     * </p>
     * 
     * @param targetObject
     *            The object on which the field is defined.
     * @param field
     *            Field on target object whose value is set.
     * @param fieldValue
     *            The value which is assigned to the field.
     * 
     * @throws IllegalAccessException
     *             If field assignment fails.
     * @throws IllegalArgumentException
     *             If field assignment fails.
     */
    public void setFieldValue(Object targetObject, Field field, Object fieldValue)
	    throws IllegalArgumentException, IllegalAccessException {
	// validate parameters
	Validate.notNull(targetObject);
	Validate.notNull(field);

	// make field accessible
	if (!field.isAccessible()) {
	    field.setAccessible(true);
	}

	// get field type
	Class<?> fieldType = field.getType();

	// skip assignment if value is null and type is primitive
	if (fieldValue == null && fieldType.isPrimitive()) {

	    // log debug message
	    if (logger.isDebugEnabled()) {
		// create error message
		StringBuilder message = new StringBuilder();
		message.append("Skipping null value assignment to primitive field <");
		message.append(field);
		message.append("> on target object <");
		message.append(targetObject);
		message.append(">.");

		logger.debug(message.toString());
	    }

	    // exit
	    return;
	}

	// convert value if type is string, and string isn't compatible with
	// target type
	if (fieldValue instanceof String) {

	    // assign value to string variable
	    String valueAsString = (String) fieldValue;

	    // target type is primitive then convert
	    if (fieldType.isPrimitive()) {

		// log debug message
		if (logger.isDebugEnabled()) {
		    // create error message
		    StringBuilder message = new StringBuilder();
		    message.append("Converting field value of type String <");
		    message.append(fieldValue);
		    message.append("> to primitive type  <");
		    message.append(fieldType);
		    message.append(">.");

		    logger.debug(message.toString());
		}

		// convert from string to primitive type
		PropertyEditor editor = PropertyEditorManager.findEditor(fieldType);
		editor.setAsText(valueAsString);
		fieldValue = editor.getValue();
	    }

	    // if target type is string[] then convert string to string[]
	    if (isStringArray(fieldType)) {

		// log debug message
		if (logger.isDebugEnabled()) {
		    // create error message
		    StringBuilder message = new StringBuilder();
		    message.append("Converting field value of type String <");
		    message.append(fieldValue);
		    message.append("> to String[] <");
		    message.append(fieldType);
		    message.append(">.");

		    logger.debug(message.toString());
		}

		// convert from string to primitive type
		PropertyEditor editor = PropertyEditorManager.findEditor(fieldType);
		editor.setAsText(valueAsString);
		fieldValue = editor.getValue();
	    }

	}

	// do assignment
	field.set(targetObject, fieldValue);

    }

    /**
     * Determine whether field type is <code>String[]</code>.
     * 
     * @param fieldType
     *            The field type to test.
     * @return True if field type is <code>String[]</code>.
     */
    boolean isStringArray(Class<?> fieldType) {

	// exit if type isn't array
	if (!fieldType.isArray())
	    return false;

	// get component type
	Class<?> componentType = fieldType.getComponentType();

	// return true component type is string
	return componentType.equals(java.lang.String.class);
    }
}
