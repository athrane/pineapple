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

package com.alpha.pineapple.execution.report;

import static com.alpha.javautils.StringUtils.createReportFriendlyString;
import static com.alpha.javautils.reflection.MethodUtils.invokeMethodWithNoArgs;
import static com.alpha.javautils.reflection.MethodUtils.isMethodWithNoParameters;
import static com.alpha.javautils.reflection.MethodUtils.methodReturnsObject;
import static com.alpha.javautils.reflection.MethodUtils.methodReturnsObjectInPackage;
import static com.alpha.javautils.reflection.MethodUtils.methodReturnsPrimitive;
import static com.alpha.javautils.reflection.MethodUtils.methodReturnsPrimitiveAsObject;
import static com.alpha.javautils.reflection.MethodUtils.methodReturnsString;
import static com.alpha.javautils.reflection.MethodUtils.resolveGetterMethods;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alpha.javautils.reflection.GetterMethodMatcher;
import com.alpha.pineapple.execution.ExecutionResult;

/**
 * Helper class for generating a report from a JAXB generated object structure.
 */
public class JaxbReportUtils {

    /**
     * Logger object.
     */
    static Logger logger = Logger.getLogger(JaxbReportUtils.class.getName());

    /**
     * Create report from JAXB generated object structure by generating a
     * structure of {@linkplain ExecutionResult} for all properties and child
     * objects.
     * 
     * @param result
     *            root execution result from which the child structure as added.
     * @param jaxbObject
     *            JAXB generated object from which the report is generated.
     * @param methodMatcher
     *            method matcher which identifies getter methods to report on.
     * @param mapsPackageName
     *            Java package name where custom map implementations are located
     *            to support binding to maps instead of lists.
     * 
     * @throws Exception
     *             if reporting fails.
     */
    @SuppressWarnings("unchecked")
    public static void reportOnObject(ExecutionResult result, Object jaxbObject, GetterMethodMatcher methodMatcher,
	    String mapsPackageName) throws Exception {

	// get getter methods and iterate
	Method[] getterMethods = resolveGetterMethods(jaxbObject, methodMatcher);
	for (Method method : getterMethods) {
	    if (!isMethodWithNoParameters(method))
		continue;

	    // property name and value
	    String propertyName = methodMatcher.resolveAttributeFromGetterMethod(method);
	    Object value = invokeMethodWithNoArgs(jaxbObject, method);

	    // process string, primitive and primitive as object
	    if ((methodReturnsString(method)) || (methodReturnsPrimitive(method))
		    || (methodReturnsPrimitiveAsObject(method))) {
		reportPrimitiveAndString(result, propertyName, value);
		continue;
	    }

	    // handle list object
	    if (methodReturnsObject(method, List.class)) {
		ExecutionResult childResult = result.addChild(propertyName);
		List<String> valueList = List.class.cast(value);
		reportList(childResult, methodMatcher, mapsPackageName, propertyName, valueList);
		childResult.setState(ExecutionResult.ExecutionState.SUCCESS);
		continue;
	    }

	    // handle map object
	    if (methodReturnsObjectInPackage(method, mapsPackageName)) {
		ExecutionResult childResult = result.addChild(propertyName);
		Map<String, Object> valueMap = Map.class.cast(value);
		reportMap(childResult, methodMatcher, mapsPackageName, propertyName, valueMap);
		childResult.setState(ExecutionResult.ExecutionState.SUCCESS);
		continue;
	    }

	    // process object
	    if (value != null) {
		ExecutionResult childResult = result.addChild(propertyName);
		reportOnObject(childResult, value, methodMatcher, mapsPackageName);
		childResult.setState(ExecutionResult.ExecutionState.SUCCESS);
		continue;
	    }

	    reportPrimitiveAndString(result, propertyName, value);

	}
    }

    /**
     * Report on map.
     * 
     * @param result
     *            result to report to.
     * @param methodMatcher
     *            method matcher which identifies getter methods to report on.
     * @param mapsPackageName
     *            Java package name where custom map implementations are located
     *            to support binding to maps instead of lists.
     * @param propertyName
     *            property name to report.
     * @param value
     *            list to report.
     * @throws Exception
     *             if reporting fails
     */
    static void reportMap(ExecutionResult result, GetterMethodMatcher methodMatcher, String mapsPackageName,
	    String propertyName, Map<String, Object> value) throws Exception {

	// handle null case
	if (value == null) {
	    String valueAsString = createReportFriendlyString(value);
	    result.addMessage(propertyName, valueAsString);
	    return;
	}

	// handle defined map
	for (String key : value.keySet()) {
	    Object mapValue = value.get(key);
	    ExecutionResult childResult = result.addChild(key);
	    reportOnObject(childResult, mapValue, methodMatcher, mapsPackageName);
	    childResult.setState(ExecutionResult.ExecutionState.SUCCESS);
	}
    }

    /**
     * Report on list.
     * 
     * @param result
     *            result to report to.
     * @param methodMatcher
     *            method matcher which identifies getter methods to report on.
     * @param mapsPackageName
     *            Java package name where custom map implementations are located
     *            to support binding to maps instead of lists.
     * @param propertyName
     *            property name to report.
     * @param value
     *            list to report.
     * @throws Exception
     *             if reporting fails
     */
    static void reportList(ExecutionResult result, GetterMethodMatcher methodMatcher, String mapsPackageName,
	    String propertyName, List<String> value) throws Exception {

	// handle null case
	if (value == null) {
	    String valueAsString = createReportFriendlyString(value);
	    result.addMessage(propertyName, valueAsString);
	    return;
	}

	// handle defined list
	for (Object listValue : value) {
	    String valueAsString = createReportFriendlyString(listValue);
	    ExecutionResult childResult = result.addChild(valueAsString);
	    reportOnObject(childResult, listValue, methodMatcher, mapsPackageName);
	    childResult.setState(ExecutionResult.ExecutionState.SUCCESS);
	}
    }

    /**
     * Report on primitive and string.
     * 
     * @param result
     *            result to report to.
     * @param propertyName
     *            property name to report
     * @param value
     *            property value to report.
     */
    static void reportPrimitiveAndString(ExecutionResult result, String propertyName, Object value) {
	String valueAsString = createReportFriendlyString(value);
	result.addMessage(propertyName, valueAsString);
    }

    /**
     * Test JAXB generated object structure by generating a structure of
     * {@linkplain ExecutionResult} for all properties and child objects.
     * 
     * @param result
     *            root execution result from which the child structure as added.
     * @param jaxbObjExpected
     *            JAXB generated object from which contains the expected values.
     * @param jaxbObjActual
     *            JAXB generated object from which contains the actual values.
     * @param methodMatcher
     *            method matcher which identifies getter methods to report on.
     * @param mapsPackageName
     *            Java package name where custom map implementations are located
     *            to support binding to maps instead of lists.
     * 
     * @throws Exception
     *             if reporting fails.
     */
    @SuppressWarnings("unchecked")
    public static void testOnObjects(ExecutionResult result, Object jaxbObjExpected, Object jaxbObjActual,
	    GetterMethodMatcher methodMatcher, String mapsPackageName) throws Exception {

	// get getter methods from expected
	Method[] getterMethodsExpected = resolveGetterMethods(jaxbObjExpected, methodMatcher);

	// get getter methods from actual
	Method[] getterMethodsActual = resolveGetterMethods(jaxbObjActual, methodMatcher);
	Map<String, Method> getterMethodsActualMap = new HashMap<>();
	for (Method m : getterMethodsActual) {
	    getterMethodsActualMap.put(m.getName(), m);
	}

	for (Method method : getterMethodsExpected) {
	    if (!isMethodWithNoParameters(method))
		continue;

	    // resolved expected property name and value
	    String propertyName = methodMatcher.resolveAttributeFromGetterMethod(method);
	    Object expectedValue = invokeMethodWithNoArgs(jaxbObjExpected, method);

	    // skip if no actual value is defined.
	    if (!getterMethodsActualMap.containsKey(method.getName())) {
		continue;
	    }
	    // resolve actual value
	    Method actualMethod = getterMethodsActualMap.get(method.getName());
	    Object actualValue = invokeMethodWithNoArgs(jaxbObjActual, actualMethod);

	    // process string, primitive and primitive as object
	    if ((methodReturnsString(method)) || (methodReturnsPrimitive(method))
		    || (methodReturnsPrimitiveAsObject(method))) {

		if (expectedValue == null) {
		    reportPrimitiveAndString(result, propertyName, expectedValue);
		    continue;
		}

	    }

	    // handle list object
	    if (methodReturnsObject(method, List.class)) {
		/**
		 * List<String> valueAsList = (List<String>) expectedValue;
		 * String listAsString = Arrays.toString(valueAsList.toArray());
		 * String valueAsString =
		 * createReportFriendlyString(listAsString);
		 * result.addMessage(propertyName,valueAsString);
		 **/
		continue;
	    }

	    // handle map object
	    if (methodReturnsObjectInPackage(method, mapsPackageName)) {
		/**
		 * String valueAsString =
		 * createReportFriendlyString(expectedValue);
		 * result.addMessage(propertyName,valueAsString);
		 **/
		continue;
	    }

	    // process object
	    if (expectedValue != null) {
		ExecutionResult childResult = result.addChild(propertyName);
		testOnObjects(childResult, expectedValue, actualValue, methodMatcher, mapsPackageName);
		childResult.setState(ExecutionResult.ExecutionState.SUCCESS);
		continue;
	    }

	    reportPrimitiveAndString(result, propertyName, expectedValue);

	}
    }

}
